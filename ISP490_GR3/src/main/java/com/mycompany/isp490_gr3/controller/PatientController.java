package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.PatientDAO;
import com.mycompany.isp490_gr3.model.Patient;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet xử lý các yêu cầu liên quan đến bệnh nhân.
 */
@WebServlet(name = "PatientController", urlPatterns = {"/patients", "/patients/add", "/patients/update"})
public class PatientController extends HttpServlet {

    private void processPatientList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String code = request.getParameter("code") != null ? request.getParameter("code").trim() : "";
        String name = request.getParameter("name") != null ? request.getParameter("name").trim() : "";
        String phone = request.getParameter("phone") != null ? request.getParameter("phone").trim() : "";

        PatientDAO dao = new PatientDAO();
        List<Patient> patients;
        boolean isSearch = !code.isEmpty() || !name.isEmpty() || !phone.isEmpty();

        if (isSearch) {
            patients = dao.searchPatients(code, name, phone);
        } else {
            patients = dao.getAllPatients();
        }

        request.setAttribute("patients", patients);
        request.setAttribute("searchCode", code);
        request.setAttribute("searchName", name);
        request.setAttribute("searchPhone", phone);
        request.getRequestDispatcher("/jsp/patient-list.jsp").forward(request, response);
    }

    private void addPatient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            // Lấy tham số từ form
            String fullName = request.getParameter("fullName");
            String genderStr = request.getParameter("gender");
            String dobStr = request.getParameter("dob");
            String phone = request.getParameter("phone");
            String cccd = request.getParameter("cccd");
            String address = request.getParameter("address");

            // Kiểm tra các trường bắt buộc
            if (fullName == null || fullName.trim().isEmpty()
                    || genderStr == null || genderStr.trim().isEmpty()
                    || dobStr == null || dobStr.trim().isEmpty()
                    || phone == null || phone.trim().isEmpty()
                    || cccd == null || cccd.trim().isEmpty()
                    || address == null || address.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng điền đầy đủ tất cả các trường.");
            }

            // Parse gender và dob
            int gender;
            try {
                gender = Integer.parseInt(genderStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Giới tính không hợp lệ.");
            }

            Date dob;
            try {
                dob = Date.valueOf(dobStr);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Ngày sinh không hợp lệ (định dạng: yyyy-MM-dd).");
            }

            // Kiểm tra ngày sinh không phải tương lai
            if (dob.after(new java.util.Date())) {
                throw new IllegalArgumentException("Ngày sinh không được là tương lai.");
            }

            // Kiểm tra định dạng phone và cccd
            if (!phone.matches("\\d{10,11}")) {
                throw new IllegalArgumentException("Số điện thoại phải là 10-11 chữ số.");
            }
            if (!cccd.matches("\\d{12}")) {
                throw new IllegalArgumentException("CCCD phải là 12 chữ số.");
            }

            // Tạo đối tượng Patient
            Patient patient = new Patient();
            patient.setFullName(fullName);
            patient.setGender(gender);
            patient.setDob(dob);
            patient.setPhone(phone);
            patient.setCccd(cccd);
            patient.setAddress(address);
            patient.setCreatedBy(null); // Chưa phân quyền
            patient.setUpdatedBy(null); // Chưa phân quyền
            patient.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            patient.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            patient.setDeleted(false);

            // Lưu vào DB
            PatientDAO dao = new PatientDAO();
            dao.addPatient(patient);

            // Thông báo thành công
            request.getSession().setAttribute("success", "Thêm bệnh nhân thành công");
            response.sendRedirect(request.getContextPath() + "/patients");

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Dữ liệu không hợp lệ: " + e.getMessage());
            processPatientList(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi khi thêm bệnh nhân: " + e.getMessage());
            processPatientList(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processPatientList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.equals("/patients/add")) {
            addPatient(request, response);
        } else {
            processPatientList(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Patient Controller";
    }
}
