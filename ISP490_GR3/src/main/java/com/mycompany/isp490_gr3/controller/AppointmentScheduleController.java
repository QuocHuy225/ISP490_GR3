package com.mycompany.isp490_gr3.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.isp490_gr3.dao.DAOAppointment;
import com.mycompany.isp490_gr3.dao.DAODoctor;
import com.mycompany.isp490_gr3.dao.DAOService;
import com.mycompany.isp490_gr3.model.Appointment;
import com.mycompany.isp490_gr3.model.Doctor;
import com.mycompany.isp490_gr3.model.MedicalService;
import com.mycompany.isp490_gr3.model.User; // Assuming User model exists

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

// Map this servlet to handle API requests specifically for patient-related appointments, doctors, and services
//@WebServlet("/api/patient/*") // Changed URL pattern to be more specific
public class AppointmentScheduleController extends HttpServlet {

    private Gson gson;
    private DAOAppointment appointmentDAO;
    private DAODoctor doctorDAO;
    private DAOService medicalServiceDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        gson = new GsonBuilder().setPrettyPrinting().create();
        appointmentDAO = new DAOAppointment();
        doctorDAO = new DAODoctor();
        medicalServiceDAO = new DAOService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // pathInfo will now be relative to /api/patient/ (e.g., "/appointments/patient/PAT001", "/doctors", "/services")
        String pathInfo = request.getPathInfo(); 
        System.out.println("AppointmentScheduleController - doGet - Request URI: " + request.getRequestURI());
        System.out.println("AppointmentScheduleController - doGet - PathInfo: " + pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                System.out.println("AppointmentScheduleController - doGet - PathInfo is null or root.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(Map.of("message", "Đường dẫn API không hợp lệ.")));
                return;
            }

            // --- Handle /api/patient/appointments/patient/{userAccountId} ---
            if (pathInfo.startsWith("/appointments/patient/")) {
                System.out.println("AppointmentScheduleController - doGet - Handling /appointments/patient/ request.");
                String[] pathParts = pathInfo.split("/");
                // Expected format: /appointments/patient/{userAccountId} -> pathParts[3]
                if (pathParts.length == 4) { 
                    String userAccountId = pathParts[3]; // This is the user.id from session
                    System.out.println("AppointmentScheduleController - doGet - userAccountId: " + userAccountId);

                    Integer patientId = appointmentDAO.getPatientIdByAccountId(userAccountId);

                    if (patientId == null) {
                        System.out.println("AppointmentScheduleController - doGet - Patient ID not found for account ID: " + userAccountId);
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gson.toJson(Map.of("message", "Không tìm thấy bệnh nhân liên kết với tài khoản đã cung cấp.")));
                        return;
                    }
                    System.out.println("AppointmentScheduleController - doGet - Found patientId: " + patientId);

                    List<Appointment> appointments = appointmentDAO.getAppointmentsByPatientId(patientId);
                    out.print(gson.toJson(appointments));
                    response.setStatus(HttpServletResponse.SC_OK);
                    System.out.println("AppointmentScheduleController - doGet - Sent appointments for patient: " + patientId);
                } else {
                    System.out.println("AppointmentScheduleController - doGet - Bad request format for /appointments/patient/: " + pathInfo);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.toJson(Map.of("message", "Định dạng đường dẫn không đúng cho lịch hẹn của bệnh nhân.")));
                }
            }
            // --- Handle /api/patient/doctors ---
            else if (pathInfo.equals("/doctors")) {
                System.out.println("AppointmentScheduleController - doGet - Handling /doctors request.");
                List<Doctor> doctors = doctorDAO.findAllDoctors();
                out.print(gson.toJson(doctors));
                response.setStatus(HttpServletResponse.SC_OK);
                System.out.println("AppointmentScheduleController - doGet - Sent list of doctors.");
            }
            // --- Handle /api/patient/services ---
            else if (pathInfo.equals("/services")) {
                System.out.println("AppointmentScheduleController - doGet - Handling /services request.");
                List<MedicalService> services = medicalServiceDAO.getAllServices();
                out.print(gson.toJson(services));
                response.setStatus(HttpServletResponse.SC_OK);
                System.out.println("AppointmentScheduleController - doGet - Sent list of services.");
            }
            else {
                System.out.println("AppointmentScheduleController - doGet - No API resource found for pathInfo: " + pathInfo);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(Map.of("message", "Không tìm thấy tài nguyên API.")));
            }
        } catch (Exception e) {
            System.err.println("AppointmentScheduleController - doGet - Internal Server Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("message", "Lỗi máy chủ nội bộ: " + e.getMessage())));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        System.out.println("AppointmentScheduleController - doPost - Request URI: " + request.getRequestURI());
        System.out.println("AppointmentScheduleController - doPost - PathInfo: " + pathInfo);

        try {
            // --- Handle /api/patient/appointments (Create new appointment) ---
            if (pathInfo != null && pathInfo.equals("/appointments")) {
                System.out.println("AppointmentScheduleController - doPost - Handling /appointments (create) request.");
                StringBuilder sb = new StringBuilder();
                String line;
                try (BufferedReader reader = request.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }
                String jsonInput = sb.toString();
                System.out.println("AppointmentScheduleController - doPost - Received JSON: " + jsonInput);

                Map<String, Object> inputMap = gson.fromJson(jsonInput, Map.class);

                String userAccountId = (String) inputMap.get("patient_id");
                String appointmentDateStr = (String) inputMap.get("appointment_date");
                String appointmentTimeStr = (String) inputMap.get("appointment_time");
                Double doctorIdDouble = (Double) inputMap.get("doctor_id");
                Double servicesIdDouble = (Double) inputMap.get("services_id");
                String notes = (String) inputMap.get("notes");

                if (userAccountId == null || appointmentDateStr == null || appointmentTimeStr == null ||
                    doctorIdDouble == null || servicesIdDouble == null) {
                    System.out.println("AppointmentScheduleController - doPost - Missing required fields.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.toJson(Map.of("message", "Thiếu thông tin bắt buộc để đặt lịch hẹn.")));
                    return;
                }

                int doctorId = doctorIdDouble.intValue();
                int servicesId = servicesIdDouble.intValue();

                Integer patientId = appointmentDAO.getPatientIdByAccountId(userAccountId);
                if (patientId == null) {
                    System.out.println("AppointmentScheduleController - doPost - Patient ID not found for account ID: " + userAccountId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(Map.of("message", "Không tìm thấy bệnh nhân liên kết với tài khoản này.")));
                    return;
                }
                System.out.println("AppointmentScheduleController - doPost - Found patientId: " + patientId);


                Integer slotId = appointmentDAO.findAvailableSlot(doctorId, appointmentDateStr, appointmentTimeStr);

                if (slotId == null) {
                    System.out.println("AppointmentScheduleController - doPost - No available slot found for doctor " + doctorId + " on " + appointmentDateStr + " at " + appointmentTimeStr);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.toJson(Map.of("message", "Không tìm thấy khung giờ khám phù hợp hoặc đã đầy.")));
                    return;
                }
                System.out.println("AppointmentScheduleController - doPost - Found slotId: " + slotId);

                Appointment newAppointment = new Appointment(patientId, slotId, servicesId, notes);

                Appointment createdAppointment = appointmentDAO.createAppointment(newAppointment);

                if (createdAppointment != null) {
                    createdAppointment.setPatientFullName(loggedInPatientFullNameFromSession(request.getSession()));
                    createdAppointment.setAppointmentDate(appointmentDateStr);
                    createdAppointment.setAppointmentTime(appointmentTimeStr);
                    Doctor doctor = doctorDAO.findDoctorById(doctorId);
                    if (doctor != null) createdAppointment.setDoctorFullName(doctor.getFullName());
                    MedicalService service = medicalServiceDAO.getServiceById(servicesId);
                    if (service != null) createdAppointment.setServiceName(service.getServiceName());

                    response.setStatus(HttpServletResponse.SC_CREATED);
                    out.print(gson.toJson(createdAppointment));
                    System.out.println("AppointmentScheduleController - doPost - Appointment created successfully with ID: " + createdAppointment.getId());
                } else {
                    System.out.println("AppointmentScheduleController - doPost - Failed to create appointment in DAO.");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print(gson.toJson(Map.of("message", "Không thể tạo lịch hẹn.")));
                }
            } else {
                System.out.println("AppointmentScheduleController - doPost - No API resource found for POST pathInfo: " + pathInfo);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(Map.of("message", "Đường dẫn API không hợp lệ cho phương thức POST.")));
            }
        } catch (DateTimeParseException e) {
            System.err.println("AppointmentScheduleController - doPost - Date/Time parsing error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("message", "Định dạng ngày hoặc giờ không hợp lệ.")));
        } catch (Exception e) {
            System.err.println("AppointmentScheduleController - doPost - Internal Server Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("message", "Lỗi máy chủ nội bộ: " + e.getMessage())));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo(); // e.g., "/appointments/{appointmentId}/cancel"
        System.out.println("AppointmentScheduleController - doPut - Request URI: " + request.getRequestURI());
        System.out.println("AppointmentScheduleController - doPut - PathInfo: " + pathInfo);

        try {
            // --- Handle /api/patient/appointments/{appointmentId}/cancel ---
            if (pathInfo != null && pathInfo.matches("/appointments/\\d+/cancel")) {
                System.out.println("AppointmentScheduleController - doPut - Handling /appointments/{id}/cancel request.");
                String[] pathParts = pathInfo.split("/");
                int appointmentId = Integer.parseInt(pathParts[2]); // Get ID from path
                System.out.println("AppointmentScheduleController - doPut - Appointment ID for cancellation: " + appointmentId);

                boolean success = appointmentDAO.cancelAppointment(appointmentId);

                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(gson.toJson(Map.of("message", "Lịch hẹn đã được hủy thành công.")));
                    System.out.println("AppointmentScheduleController - doPut - Appointment " + appointmentId + " cancelled successfully.");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(Map.of("message", "Không tìm thấy lịch hẹn hoặc không thể hủy.")));
                    System.out.println("AppointmentScheduleController - doPut - Failed to cancel appointment " + appointmentId + " or not found.");
                }
            } else {
                System.out.println("AppointmentScheduleController - doPut - No API resource found for PUT pathInfo: " + pathInfo);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(Map.of("message", "Đường dẫn API không hợp lệ cho phương thức PUT.")));
            }
        } catch (NumberFormatException e) {
            System.err.println("AppointmentScheduleController - doPut - Invalid appointment ID format: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("message", "ID lịch hẹn không hợp lệ.")));
        } catch (Exception e) {
            System.err.println("AppointmentScheduleController - doPut - Internal Server Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("message", "Lỗi máy chủ nội bộ: " + e.getMessage())));
        } finally {
            out.flush();
        }
    }

    /**
     * Helper method to get logged-in patient's full name from session.
     * This is used to populate the response for new appointment creation.
     * In a more robust system, you might have a dedicated service for user context.
     */
    private String loggedInPatientFullNameFromSession(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return user.getFullName() != null ? user.getFullName() : user.getEmail();
        }
        return "Khách";
    }
}
