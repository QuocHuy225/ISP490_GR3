package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOPatient;
import com.mycompany.isp490_gr3.model.Patient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import com.google.gson.Gson;

/**
 * Servlet xử lý các yêu cầu liên quan đến bệnh nhân.
 */
@WebServlet(name = "PatientController", urlPatterns = {"/patients", "/patients/*"})
public class PatientController extends HttpServlet {

    private DAOPatient patientDAO = new DAOPatient();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        if ("get".equals(action)) {
            getPatientById(request, response);
        } else {
            listPatients(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        switch (action != null ? action : "") {
            case "add":
                addPatient(request, response);
                break;
            case "update":
                updatePatient(request, response);
                break;
            case "delete":
                deletePatient(request, response);
                break;
            default:
                listPatients(request, response);
                break;
        }
    }

    private void listPatients(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String cccd = request.getParameter("cccd");

        List<Patient> patients;
        boolean isSearch = (code != null && !code.trim().isEmpty()) ||
                          (name != null && !name.trim().isEmpty()) ||
                          (phone != null && !phone.trim().isEmpty()) ||
                          (cccd != null && !cccd.trim().isEmpty());

        if (isSearch) {
            patients = patientDAO.searchPatients(code, name, phone, cccd);
        } else {
            patients = patientDAO.getAllPatients();
        }

        request.setAttribute("patients", patients);
        request.setAttribute("searchCode", code != null ? code : "");
        request.setAttribute("searchName", name != null ? name : "");
        request.setAttribute("searchPhone", phone != null ? phone : "");
        request.setAttribute("searchCccd", cccd != null ? cccd : "");
        
        request.getRequestDispatcher("/jsp/patient-management.jsp").forward(request, response);
    }

    private void getPatientById(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Patient patient = patientDAO.getPatientById(id);
            
            if (patient != null) {
                // Format date for JSON response
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                patient.setDob(Date.valueOf(sdf.format(patient.getDob())));
                
                PrintWriter out = response.getWriter();
                out.print(gson.toJson(patient));
                out.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void addPatient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy tham số từ form
            String fullName = request.getParameter("fullName");
            String genderStr = request.getParameter("gender");
            String dobStr = request.getParameter("dob");
            String phone = request.getParameter("phone");
            String cccd = request.getParameter("cccd");
            String address = request.getParameter("address");

            // Kiểm tra các trường bắt buộc
            if (fullName == null || fullName.trim().isEmpty() ||
                genderStr == null || genderStr.trim().isEmpty() ||
                dobStr == null || dobStr.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty() ||
                cccd == null || cccd.trim().isEmpty() ||
                address == null || address.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/patients?error=missing_fields");
                return;
            }

            // Parse và validate dữ liệu
            int gender;
            try {
                gender = Integer.parseInt(genderStr);
                if (gender < 0 || gender > 2) {
                    throw new NumberFormatException("Invalid gender value");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/patients?error=invalid_gender");
                return;
            }

            Date dob;
            try {
                dob = Date.valueOf(dobStr);
                if (dob.after(new java.util.Date())) {
                    response.sendRedirect(request.getContextPath() + "/patients?error=invalid_dob");
                    return;
                }
            } catch (IllegalArgumentException e) {
                response.sendRedirect(request.getContextPath() + "/patients?error=invalid_date_format");
                return;
            }

            // Validate phone và cccd
            if (!phone.matches("\\d{10,11}")) {
                response.sendRedirect(request.getContextPath() + "/patients?error=invalid_phone");
                return;
            }
            if (!cccd.matches("\\d{12}")) {
                response.sendRedirect(request.getContextPath() + "/patients?error=invalid_cccd");
                return;
            }

            // Tạo đối tượng Patient
            Patient patient = new Patient();
            patient.setFullName(fullName.trim());
            patient.setGender(gender);
            patient.setDob(dob);
            patient.setPhone(phone.trim());
            patient.setCccd(cccd.trim());
            patient.setAddress(address.trim());

            // Lưu vào DB
            boolean success = patientDAO.addPatient(patient);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/patients?success=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/patients?error=add_failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/patients?error=system_error");
        }
    }

    private void updatePatient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy tham số từ form
            String idStr = request.getParameter("patientId");
            String fullName = request.getParameter("fullName");
            String genderStr = request.getParameter("gender");
            String dobStr = request.getParameter("dob");
            String phone = request.getParameter("phone");
            String cccd = request.getParameter("cccd");
            String address = request.getParameter("address");

            // Kiểm tra các trường bắt buộc
            if (idStr == null || idStr.trim().isEmpty() ||
                fullName == null || fullName.trim().isEmpty() ||
                genderStr == null || genderStr.trim().isEmpty() ||
                dobStr == null || dobStr.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty() ||
                cccd == null || cccd.trim().isEmpty() ||
                address == null || address.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/patients?error=missing_fields");
                return;
            }

            // Parse và validate dữ liệu
            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/patients?error=invalid_id");
                return;
            }

            int gender;
            try {
                gender = Integer.parseInt(genderStr);
                if (gender < 0 || gender > 2) {
                    throw new NumberFormatException("Invalid gender value");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/patients?error=invalid_gender");
                return;
            }

            Date dob;
            try {
                dob = Date.valueOf(dobStr);
                if (dob.after(new java.util.Date())) {
                    response.sendRedirect(request.getContextPath() + "/patients?error=invalid_dob");
                    return;
                }
            } catch (IllegalArgumentException e) {
                response.sendRedirect(request.getContextPath() + "/patients?error=invalid_date_format");
                return;
            }

            // Validate phone và cccd
            if (!phone.matches("\\d{10,11}")) {
                response.sendRedirect(request.getContextPath() + "/patients?error=invalid_phone");
                return;
            }
            if (!cccd.matches("\\d{12}")) {
                response.sendRedirect(request.getContextPath() + "/patients?error=invalid_cccd");
                return;
            }

            // Tạo đối tượng Patient
            Patient patient = new Patient();
            patient.setId(id);
            patient.setFullName(fullName.trim());
            patient.setGender(gender);
            patient.setDob(dob);
            patient.setPhone(phone.trim());
            patient.setCccd(cccd.trim());
            patient.setAddress(address.trim());

            // Cập nhật trong DB
            boolean success = patientDAO.updatePatient(patient);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/patients?success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/patients?error=update_failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/patients?error=system_error");
        }
    }

    private void deletePatient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idStr = request.getParameter("patientId");
            
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/patients?error=missing_id");
                return;
            }

            int id = Integer.parseInt(idStr);
            boolean success = patientDAO.deletePatient(id);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/patients?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/patients?error=delete_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/patients?error=invalid_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/patients?error=system_error");
        }
    }

    @Override
    public String getServletInfo() {
        return "Patient Management Controller";
    }
}
