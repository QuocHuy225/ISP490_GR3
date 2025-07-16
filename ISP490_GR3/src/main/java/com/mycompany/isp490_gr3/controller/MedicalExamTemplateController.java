package com.mycompany.isp490_gr3.controller;

import com.mycompany.isp490_gr3.dao.DAOMedicalExamTemplate;
import com.mycompany.isp490_gr3.model.MedicalExamTemplate;
import com.mycompany.isp490_gr3.model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * =====================================================
 * MedicalExamTemplateController - CONTROLLER QUẢN LÝ MẪU ĐƠN KHÁM BỆNH
 * 
 * Chức năng: Xử lý các request liên quan đến quản lý mẫu đơn khám bệnh
 * URL patterns: /admin/medical-exam-templates/*
 * DAO sử dụng: DAOMedicalExamTemplate
 * JSP tương ứng: medical-exam-templates.jsp
 * 
 * Các chức năng chính:
 * - Hiển thị danh sách mẫu đơn khám bệnh
 * - Tìm kiếm mẫu đơn theo tên
 * - Thêm mẫu đơn mới
 * - Chỉnh sửa mẫu đơn
 * - Xóa mẫu đơn (soft delete)
 * - Chỉ Admin có quyền truy cập
 * =====================================================
 */
@WebServlet(name = "MedicalExamTemplateController", urlPatterns = {"/admin/medical-exam-templates/*"})
public class MedicalExamTemplateController extends HttpServlet {
    
    private DAOMedicalExamTemplate daoTemplate;
    
    @Override
    public void init() throws ServletException {
        super.init();
        daoTemplate = new DAOMedicalExamTemplate();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check admin access
        if (!checkAdminAccess(request, response)) {
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            showTemplateList(request, response);
        } else {
            switch (pathInfo) {
                case "/list":
                    showTemplateList(request, response);
                    break;
                case "/add":
                    showAddForm(request, response);
                    break;
                case "/edit":
                    showEditForm(request, response);
                    break;
                case "/search":
                    searchTemplates(request, response);
                    break;
                case "/delete":
                    deleteTemplate(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/admin/medical-exam-templates/list");
                    break;
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check admin access
        if (!checkAdminAccess(request, response)) {
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null) {
            switch (pathInfo) {
                case "/add":
                    addTemplate(request, response);
                    break;
                case "/update":
                    updateTemplate(request, response);
                    break;
                case "/delete":
                    deleteTemplate(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/admin/medical-exam-templates/list");
                    break;
            }
        }
    }
    
    /**
     * Check admin access - standardized across all controllers
     */
    private boolean checkAdminAccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return false;
        }
        
        // Get current user
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/landing.jsp");
            return false;
        }
        
        // Check if user is admin
        if (currentUser.getRole() != User.Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return false;
        }
        
        return true;
    }
    
    /**
     * Hiển thị danh sách mẫu đơn khám bệnh
     */
    private void showTemplateList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if edit parameter is present
        String editParam = request.getParameter("edit");
        if (editParam != null && !editParam.trim().isEmpty()) {
            try {
                int editId = Integer.parseInt(editParam);
                MedicalExamTemplate editTemplate = daoTemplate.getTemplateById(editId);
                if (editTemplate != null) {
                    request.setAttribute("editTemplate", editTemplate);
                }
            } catch (NumberFormatException e) {
                // Invalid edit ID, ignore
            }
        }
        
        List<MedicalExamTemplate> templates = daoTemplate.getAllTemplates();
        request.setAttribute("templates", templates);
        request.setAttribute("totalTemplates", templates.size());
        
        request.getRequestDispatcher("/jsp/medical-exam-templates.jsp").forward(request, response);
    }
    
    /**
     * Hiển thị form thêm mẫu đơn mới
     */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("action", "add");
        request.setAttribute("pageTitle", "Thêm mẫu đơn khám bệnh mới");
        request.getRequestDispatcher("/jsp/medical-exam-templates.jsp").forward(request, response);
    }
    
    /**
     * Hiển thị form chỉnh sửa mẫu đơn
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/medical-exam-templates/list");
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            MedicalExamTemplate template = daoTemplate.getTemplateById(id);
            
            if (template != null) {
                request.setAttribute("template", template);
                request.setAttribute("action", "edit");
                request.setAttribute("pageTitle", "Chỉnh sửa mẫu đơn khám bệnh");
                request.getRequestDispatcher("/jsp/medical-exam-templates.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Không tìm thấy mẫu đơn khám bệnh");
                showTemplateList(request, response);
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/medical-exam-templates/list");
        }
    }
    
    /**
     * Tìm kiếm mẫu đơn theo tên
     */
    private void searchTemplates(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";
        
        // Check if edit parameter is present
        String editParam = request.getParameter("edit");
        if (editParam != null && !editParam.trim().isEmpty()) {
            try {
                int editId = Integer.parseInt(editParam);
                MedicalExamTemplate editTemplate = daoTemplate.getTemplateById(editId);
                if (editTemplate != null) {
                    request.setAttribute("editTemplate", editTemplate);
                }
            } catch (NumberFormatException e) {
                // Invalid edit ID, ignore
            }
        }
        
        List<MedicalExamTemplate> templates = daoTemplate.searchTemplatesByName(keyword);
        request.setAttribute("templates", templates);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("totalTemplates", templates.size());
        
        request.getRequestDispatcher("/jsp/medical-exam-templates.jsp").forward(request, response);
    }
    
    /**
     * Thêm mẫu đơn mới
     */
    private void addTemplate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Lấy dữ liệu từ form
        String name = request.getParameter("name");
        String physicalExam = request.getParameter("physicalExam");
        String clinicalInfo = request.getParameter("clinicalInfo");
        String finalDiagnosis = request.getParameter("finalDiagnosis");
        
        // Validate dữ liệu
        if (name == null || name.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Tên mẫu đơn không được để trống");
            request.setAttribute("action", "add");
            request.getRequestDispatcher("/jsp/medical-exam-templates.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra tên đã tồn tại chưa
        if (daoTemplate.isTemplateNameExists(name.trim())) {
            request.setAttribute("errorMessage", "Tên mẫu đơn đã tồn tại");
            request.setAttribute("action", "add");
            request.setAttribute("name", name);
            request.setAttribute("physicalExam", physicalExam);
            request.setAttribute("clinicalInfo", clinicalInfo);
            request.setAttribute("finalDiagnosis", finalDiagnosis);
            request.getRequestDispatcher("/jsp/medical-exam-templates.jsp").forward(request, response);
            return;
        }
        
        // Tạo đối tượng mẫu đơn mới
        MedicalExamTemplate template = new MedicalExamTemplate();
        template.setName(name.trim());
        template.setPhysicalExam(physicalExam != null ? physicalExam.trim() : "");
        template.setClinicalInfo(clinicalInfo != null ? clinicalInfo.trim() : "");
        template.setFinalDiagnosis(finalDiagnosis != null ? finalDiagnosis.trim() : "");
        
        // Thêm vào database
        if (daoTemplate.addTemplate(template)) {
            request.setAttribute("successMessage", "Thêm mẫu đơn khám bệnh thành công");
        } else {
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi thêm mẫu đơn khám bệnh");
        }
        
        showTemplateList(request, response);
    }
    
    /**
     * Cập nhật mẫu đơn
     */
    private void updateTemplate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/medical-exam-templates/list");
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            
            // Lấy dữ liệu từ form
            String name = request.getParameter("name");
            String physicalExam = request.getParameter("physicalExam");
            String clinicalInfo = request.getParameter("clinicalInfo");
            String finalDiagnosis = request.getParameter("finalDiagnosis");
            
            // Validate dữ liệu
            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Tên mẫu đơn không được để trống");
                request.setAttribute("action", "edit");
                showEditForm(request, response);
                return;
            }
            
            // Kiểm tra tên đã tồn tại chưa (trừ bản ghi hiện tại)
            if (daoTemplate.isTemplateNameExists(name.trim(), id)) {
                request.setAttribute("errorMessage", "Tên mẫu đơn đã tồn tại");
                request.setAttribute("action", "edit");
                showEditForm(request, response);
                return;
            }
            
            // Tạo đối tượng mẫu đơn để cập nhật
            MedicalExamTemplate template = new MedicalExamTemplate();
            template.setId(id);
            template.setName(name.trim());
            template.setPhysicalExam(physicalExam != null ? physicalExam.trim() : "");
            template.setClinicalInfo(clinicalInfo != null ? clinicalInfo.trim() : "");
            template.setFinalDiagnosis(finalDiagnosis != null ? finalDiagnosis.trim() : "");
            
            // Cập nhật trong database
            if (daoTemplate.updateTemplate(template)) {
                request.setAttribute("successMessage", "Cập nhật mẫu đơn khám bệnh thành công");
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật mẫu đơn khám bệnh");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID không hợp lệ");
        }
        
        showTemplateList(request, response);
    }
    
    /**
     * Xóa mẫu đơn (soft delete)
     */
    private void deleteTemplate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/medical-exam-templates/list");
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            
            if (daoTemplate.deleteTemplate(id)) {
                request.setAttribute("successMessage", "Xóa mẫu đơn khám bệnh thành công");
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi xóa mẫu đơn khám bệnh");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID không hợp lệ");
        }
        
        showTemplateList(request, response);
    }
} 