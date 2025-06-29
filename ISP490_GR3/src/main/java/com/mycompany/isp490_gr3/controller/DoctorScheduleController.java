package com.mycompany.isp490_gr3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.dao.DAODoctorSchedule;
import com.mycompany.isp490_gr3.model.DoctorSchedule; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet này hoạt động như một API endpoint để quản lý lịch làm việc chi tiết của bác sĩ.
 * Nó phục vụ:
 * 1. GET request để lấy các ngày làm việc đã được ghi nhận (/doctor/schedule).
 * 2. GET request để lấy cấu hình lịch chi tiết (appointment_duration, weekly_schedule, v.v.) (/doctor/schedule/detailed).
 * Dữ liệu này được lấy từ cột JSON trong database.
 * 3. POST request để nhận toàn bộ cấu hình lịch từ modal và lưu vào database.
 * Yêu cầu quyền "ADMIN" hoặc "Lễ tân" cho cả ba hoạt động.
 */
@WebServlet(name = "DoctorScheduleController", urlPatterns = {"/doctor/schedule", "/doctor/schedule/detailed"})
public class DoctorScheduleController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DoctorScheduleController.class.getName());

    private DAODoctorSchedule daoDoctorSchedule;
    private DAODoctor daoDoctor; // Sử dụng DAODoctor để quản lý cấu hình chi tiết
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        daoDoctorSchedule = new DAODoctorSchedule();
        daoDoctor = new DAODoctor(); // Khởi tạo DAODoctor
        objectMapper = new ObjectMapper();
        LOGGER.info("DoctorScheduleController initialized.");
    }

    private boolean checkAdminOrReceptionistRole(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException {
        HttpSession session = request.getSession();
        Object userRole = session.getAttribute("userRole");

        if (userRole == null || (!"ADMIN".equalsIgnoreCase(userRole.toString()) && !"RECEPTIONIST".equalsIgnoreCase(userRole.toString()))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.write("{\"error\": \"Truy cập bị từ chối. Cần quyền Admin hoặc Lễ tân.\"}");
            LOGGER.log(Level.WARNING, "Truy cập bị từ chối cho người dùng. Vai trò người dùng: {0}. Cần quyền Admin hoặc Lễ tân.", userRole);
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();


        if (!checkAdminOrReceptionistRole(request, response, out)) {
            return;
        }

        String servletPath = request.getServletPath(); // Sẽ là /doctor/schedule hoặc /doctor/schedule/detailed

        String doctorIdRaw = request.getParameter("doctorId");

        if (doctorIdRaw == null || doctorIdRaw.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Tham số doctorId bị thiếu.\"}");
            LOGGER.warning("Missing doctorId parameter in GET request for " + servletPath);
            return;
        }
        try {
            int doctorId = Integer.parseInt(doctorIdRaw);


            if (servletPath.endsWith("/detailed")) {
                LOGGER.log(Level.INFO, "Fetching detailed schedule for doctorId: {0} from DB.", doctorId);
                String detailedConfigJson = daoDoctor.getDoctorDetailedScheduleConfigJson(doctorId);
                Map<String, Object> detailedScheduleConfig;

                if (detailedConfigJson != null && !detailedConfigJson.trim().isEmpty()) {
                    // Nếu có dữ liệu trong DB, parse nó
                    detailedScheduleConfig = objectMapper.readValue(detailedConfigJson, Map.class);
                    LOGGER.log(Level.INFO, "Đã lấy cấu hình chi tiết từ DB cho doctorId: {0}", doctorId);
                } else {
                    // Nếu không có dữ liệu trong DB, trả về cấu hình mặc định
                    LOGGER.log(Level.INFO, "Không tìm thấy cấu hình chi tiết trong DB cho doctorId: {0}. Trả về cấu hình mặc định.", doctorId);
                    detailedScheduleConfig = new HashMap<>();
                    detailedScheduleConfig.put("appointment_duration", "30");
                    detailedScheduleConfig.put("schedule_period", "future"); 
                    // Nếu bạn muốn mặc định có các slot mẫu khi chưa có trong DB, bạn có thể thêm vào đây
                    Map<String, List<Map<String, String>>> defaultWeeklySchedule = new HashMap<>();
                    defaultWeeklySchedule.put("monday", new ArrayList<>());
                    defaultWeeklySchedule.put("tuesday", new ArrayList<>());
                    defaultWeeklySchedule.put("wednesday", new ArrayList<>());
                    defaultWeeklySchedule.put("thursday", new ArrayList<>());
                    defaultWeeklySchedule.put("friday", new ArrayList<>());
                    defaultWeeklySchedule.put("saturday", new ArrayList<>());
                    defaultWeeklySchedule.put("sunday", new ArrayList<>());
                    detailedScheduleConfig.put("weekly_schedule", defaultWeeklySchedule);
                }

                String json = objectMapper.writeValueAsString(detailedScheduleConfig);
                response.setStatus(HttpServletResponse.SC_OK);
                out.write(json);

            } else if (servletPath.endsWith("/schedule")) {
                // Xử lý yêu cầu GET cho /doctor/schedule (lấy danh sách ngày làm việc)
                LOGGER.log(Level.INFO, "Fetching work dates for doctorId: {0}", doctorId);
                List<DoctorSchedule> doctorScheduleEntries = daoDoctorSchedule.getDoctorScheduleEntries(doctorId);
                
                // Trả về danh sách các chuỗi ngày làm việc active
                List<String> workDatesStrings = new ArrayList<>();
                for (DoctorSchedule schedule : doctorScheduleEntries) {
                    if (schedule.isActive()) {
                        workDatesStrings.add(schedule.getWorkDate().toLocalDate().toString());
                    }
                }

                String json = objectMapper.writeValueAsString(workDatesStrings);
                response.setStatus(HttpServletResponse.SC_OK);
                out.write(json);
                LOGGER.log(Level.INFO, "Đã lấy thành công {0} ngày làm việc cho doctorId: {1}", new Object[]{workDatesStrings.size(), doctorId});
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\": \"Endpoint không tìm thấy.\"}");
                LOGGER.warning("Endpoint không tìm thấy cho yêu cầu GET: " + request.getRequestURI());
            }


            List<String> dates = daoDoctorSchedule.getWorkingDatesByDoctorId(doctorId);
            String json = new com.google.gson.Gson().toJson(dates != null ? dates : new ArrayList<>());
            System.out.println(json);
            response.getWriter().write(json);

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Định dạng doctorId không hợp lệ.\"}");
            LOGGER.log(Level.WARNING, "Định dạng doctorId không hợp lệ trong yêu cầu GET: {0}", doctorIdRaw);
        } catch (SQLException e) { 
            LOGGER.log(Level.SEVERE, "Lỗi database trong doGet: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"Lỗi database.\"}}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi không mong muốn trong doGet: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"Lỗi máy chủ nội bộ.\"}}");
        }
    }

    /**
     * Xử lý các yêu cầu POST để thêm hoặc cập nhật lịch làm việc của bác sĩ.
     * Dữ liệu nhận được dưới dạng JSON từ modal.
     * Yêu cầu quyền ADMIN hoặc RECEPTIONIST.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        LOGGER.info("Received POST request for /doctor/schedule (from modal).");

        if (!checkAdminOrReceptionistRole(request, response, out)) {
            return;
        }

        try {
            // Đọc toàn bộ request body dưới dạng JSON
            Map<String, Object> requestBody = objectMapper.readValue(request.getReader(), Map.class);
            
            String doctorAccountId = (String) requestBody.get("doctor_account_id");

            if (doctorAccountId == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"Thiếu ID tài khoản bác sĩ.\"}");
                LOGGER.warning("Missing doctor_account_id in POST request body.");
                return;
            }
            LOGGER.log(Level.INFO, "Processing schedule update for doctor_account_id: {0}", doctorAccountId);

            Integer doctorId = daoDoctor.getDoctorIdByAccountId(doctorAccountId);
            if (doctorId == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"message\": \"Không tìm thấy bác sĩ với ID tài khoản đã chọn.\"}");
                LOGGER.warning("Doctor not found for account ID: " + doctorAccountId + " during POST schedule update.");
                return;
            }

            // Chuyển đổi toàn bộ requestBody thành JSON String để lưu vào DB
            String jsonConfigToSave = objectMapper.writeValueAsString(requestBody);
            LOGGER.log(Level.INFO, "Saving detailed schedule config JSON to DB: {0}", jsonConfigToSave);

            boolean updated = daoDoctor.updateDoctorDetailedScheduleConfig(doctorId, jsonConfigToSave);

            response.setStatus(HttpServletResponse.SC_OK);
            ObjectNode responseNode = objectMapper.createObjectNode();
            if (updated) {
                responseNode.put("message", "Cấu hình lịch đã lưu thành công vào database.");
            } else {
                responseNode.put("message", "Không thể lưu cấu hình lịch vào database.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Hoặc một lỗi phù hợp hơn
            }
            responseNode.put("doctor_id", doctorId);
            out.write(objectMapper.writeValueAsString(responseNode));
            LOGGER.info("Schedule configuration saved/updated for doctorId: " + doctorId + ". Status: " + updated);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Lỗi đọc hoặc phân tích cú pháp JSON trong doPost: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"message\": \"Dữ liệu yêu cầu không hợp lệ.\", \"error\": \"" + e.getMessage() + "\"}");
        } catch (SQLException e) { 
            LOGGER.log(Level.SEVERE, "Lỗi database khi lấy doctorId hoặc cập nhật cấu hình: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"message\": \"Lỗi database.\"}}");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi không mong muốn trong doPost: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"message\": \"Lỗi máy chủ nội bộ không mong muốn.\", \"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Max-Age", "86400");
        resp.setStatus(HttpServletResponse.SC_OK);
        LOGGER.info("Handled OPTIONS pre-flight request for /doctor/schedule.");
    }

    @Override
    public void destroy() {
        if (daoDoctor != null) {
            daoDoctor.closeConnection();
        }
        if (daoDoctorSchedule != null) {
            daoDoctorSchedule.closeConnection();
        }
        super.destroy();
        LOGGER.info("DoctorScheduleController destroyed. DAO connections closed.");
    }
}
