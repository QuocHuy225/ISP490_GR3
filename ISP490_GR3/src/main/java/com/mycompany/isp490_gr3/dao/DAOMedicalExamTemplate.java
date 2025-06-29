package com.mycompany.isp490_gr3.dao;

import com.mycompany.isp490_gr3.model.MedicalExamTemplate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================
 * DAOMedicalExamTemplate - QUẢN LÝ MẪU ĐƠN KHÁM BỆNH
 * 
 * Chức năng: Xử lý tất cả các thao tác liên quan đến mẫu đơn khám bệnh
 * Bảng database: medical_exam_templates
 * Model tương ứng: MedicalExamTemplate
 * URL liên quan: /admin/medical-exam-templates
 * 
 * Các chức năng chính:
 * - Lấy danh sách mẫu đơn (có phân trang)
 * - Tìm kiếm mẫu đơn theo tên
 * - Thêm mẫu đơn mới
 * - Cập nhật mẫu đơn
 * - Xóa mẫu đơn (soft delete)
 * - Khôi phục mẫu đơn đã xóa
 * - Lấy chi tiết mẫu đơn theo ID
 * =====================================================
 */
public class DAOMedicalExamTemplate {
    
    /**
     * Lấy tất cả mẫu đơn khám bệnh chưa bị xóa
     * @return List<MedicalExamTemplate>
     */
    public List<MedicalExamTemplate> getAllTemplates() {
        List<MedicalExamTemplate> templates = new ArrayList<>();
        String sql = "SELECT * FROM medical_exam_templates WHERE isdeleted = FALSE ORDER BY created_at DESC";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                templates.add(mapResultSetToTemplate(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all templates: " + e.getMessage());
            e.printStackTrace();
        }
        
        return templates;
    }
    
    /**
     * Lấy danh sách mẫu đơn có phân trang
     * @param page Trang hiện tại (bắt đầu từ 1)
     * @param pageSize Số lượng bản ghi trên một trang
     * @return List<MedicalExamTemplate>
     */
    public List<MedicalExamTemplate> getTemplatesWithPaging(int page, int pageSize) {
        List<MedicalExamTemplate> templates = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM medical_exam_templates WHERE isdeleted = FALSE ORDER BY created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                templates.add(mapResultSetToTemplate(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting templates with paging: " + e.getMessage());
            e.printStackTrace();
        }
        
        return templates;
    }
    
    /**
     * Tìm kiếm mẫu đơn theo tên
     * @param searchKeyword Từ khóa tìm kiếm
     * @return List<MedicalExamTemplate>
     */
    public List<MedicalExamTemplate> searchTemplatesByName(String searchKeyword) {
        List<MedicalExamTemplate> templates = new ArrayList<>();
        String sql = "SELECT * FROM medical_exam_templates WHERE isdeleted = FALSE AND name LIKE ? ORDER BY created_at DESC";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + searchKeyword + "%");
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                templates.add(mapResultSetToTemplate(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching templates: " + e.getMessage());
            e.printStackTrace();
        }
        
        return templates;
    }
    
    /**
     * Lấy mẫu đơn theo ID
     * @param id ID của mẫu đơn
     * @return MedicalExamTemplate hoặc null nếu không tìm thấy
     */
    public MedicalExamTemplate getTemplateById(int id) {
        String sql = "SELECT * FROM medical_exam_templates WHERE id = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToTemplate(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting template by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Thêm mẫu đơn mới
     * @param template Đối tượng MedicalExamTemplate cần thêm
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean addTemplate(MedicalExamTemplate template) {
        String sql = "INSERT INTO medical_exam_templates (name, physical_exam, clinical_info, final_diagnosis, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            ps.setString(1, template.getName());
            ps.setString(2, template.getPhysicalExam());
            ps.setString(3, template.getClinicalInfo());
            ps.setString(4, template.getFinalDiagnosis());
            ps.setTimestamp(5, now);
            ps.setTimestamp(6, now);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding template: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Cập nhật mẫu đơn
     * @param template Đối tượng MedicalExamTemplate cần cập nhật
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateTemplate(MedicalExamTemplate template) {
        String sql = "UPDATE medical_exam_templates SET name = ?, physical_exam = ?, clinical_info = ?, final_diagnosis = ?, updated_at = ? WHERE id = ? AND isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, template.getName());
            ps.setString(2, template.getPhysicalExam());
            ps.setString(3, template.getClinicalInfo());
            ps.setString(4, template.getFinalDiagnosis());
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            ps.setInt(6, template.getId());
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating template: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Xóa mẫu đơn (soft delete)
     * @param id ID của mẫu đơn cần xóa
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean deleteTemplate(int id) {
        String sql = "UPDATE medical_exam_templates SET isdeleted = TRUE, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, id);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting template: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Khôi phục mẫu đơn đã xóa
     * @param id ID của mẫu đơn cần khôi phục
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean restoreTemplate(int id) {
        String sql = "UPDATE medical_exam_templates SET isdeleted = FALSE, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, id);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error restoring template: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Đếm tổng số mẫu đơn
     * @return Số lượng mẫu đơn
     */
    public int getTotalTemplatesCount() {
        String sql = "SELECT COUNT(*) FROM medical_exam_templates WHERE isdeleted = FALSE";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting templates: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Kiểm tra tên mẫu đơn đã tồn tại chưa
     * @param name Tên mẫu đơn
     * @param excludeId ID cần loại trừ (dùng khi update)
     * @return true nếu tên đã tồn tại, false nếu chưa
     */
    public boolean isTemplateNameExists(String name, int excludeId) {
        String sql = "SELECT COUNT(*) FROM medical_exam_templates WHERE name = ? AND isdeleted = FALSE AND id != ?";
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, name);
            ps.setInt(2, excludeId);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking template name existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Kiểm tra tên mẫu đơn đã tồn tại chưa (dùng khi thêm mới)
     * @param name Tên mẫu đơn
     * @return true nếu tên đã tồn tại, false nếu chưa
     */
    public boolean isTemplateNameExists(String name) {
        return isTemplateNameExists(name, 0);
    }
    
    /**
     * Map ResultSet to MedicalExamTemplate object
     * @param rs ResultSet
     * @return MedicalExamTemplate object
     * @throws SQLException
     */
    private MedicalExamTemplate mapResultSetToTemplate(ResultSet rs) throws SQLException {
        MedicalExamTemplate template = new MedicalExamTemplate();
        
        template.setId(rs.getInt("id"));
        template.setName(rs.getString("name"));
        template.setPhysicalExam(rs.getString("physical_exam"));
        template.setClinicalInfo(rs.getString("clinical_info"));
        template.setFinalDiagnosis(rs.getString("final_diagnosis"));
        template.setCreatedAt(rs.getTimestamp("created_at"));
        template.setUpdatedAt(rs.getTimestamp("updated_at"));
        template.setDeleted(rs.getBoolean("isdeleted"));
        
        return template;
    }
} 