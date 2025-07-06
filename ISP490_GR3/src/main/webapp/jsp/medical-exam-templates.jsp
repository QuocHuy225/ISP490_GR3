<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalExamTemplate" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý mẫu đơn khám bệnh - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Homepage specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <style>
            .template-card {
                transition: transform 0.2s, box-shadow 0.2s;
                border: 1px solid #e9ecef;
                border-radius: 10px;
            }
            .template-card:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(0,123,255,0.15);
            }
            .template-content {
                max-height: 100px;
                overflow: hidden;
                text-overflow: ellipsis;
            }
            .btn-action {
                border-radius: 20px;
                padding: 0.375rem 1rem;
                font-size: 0.875rem;
                margin: 0 2px;
            }
            .search-box {
                border-radius: 25px;
                border: 2px solid #e9ecef;
                padding: 0.75rem 1.5rem;
                transition: border-color 0.3s;
            }
            .search-box:focus {
                border-color: #007bff;
                box-shadow: 0 0 0 0.2rem rgba(0,123,255,0.25);
            }
            .form-modal .modal-header {
                background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
                color: white;
                border-radius: 10px 10px 0 0;
            }
            /* Ensure delete modal has white header */
            #deleteConfirmModal .modal-header {
                background: white !important;
                color: #dc3545 !important;
                border-bottom: 1px solid #dee2e6;
            }
            .form-label {
                font-weight: 500;
                color: #495057;
            }
            .form-control {
                border-radius: 8px;
                border: 1px solid #ced4da;
                transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
            }
            .form-control:focus {
                border-color: #007bff;
                box-shadow: 0 0 0 0.2rem rgba(0,123,255,0.25);
            }
            .btn-custom {
                border-radius: 25px;
                padding: 0.75rem 2rem;
                font-weight: 500;
                transition: all 0.3s;
            }
            .btn-custom:hover {
                transform: translateY(-1px);
            }
            .content-display {
                background-color: #f8f9fa;
                border-radius: 8px;
                padding: 1rem;
                margin-bottom: 1rem;
                border-left: 4px solid #007bff;
            }
        </style>
    </head>
    <body>
        <%
        // Get user role for access control
        Object userRole = session.getAttribute("userRole");
        User.Role currentRole = null;
        
        if (userRole != null) {
            if (userRole instanceof User.Role) {
                currentRole = (User.Role) userRole;
            } else {
                // Try to parse from string
                try {
                    currentRole = User.Role.valueOf(userRole.toString().toUpperCase());
                } catch (Exception e) {
                    // Fallback to parsing from display value
                    currentRole = User.Role.fromString(userRole.toString());
                }
            }
        }
        
        // Default to PATIENT if no role found
        if (currentRole == null) {
            currentRole = User.Role.PATIENT;
        }
        
        // Get user information
        Object userObj = session.getAttribute("user");
        String userName = "User";
        String userRoleDisplay = "Patient";
        if (userObj instanceof User) {
            User user = (User) userObj;
            userName = user.getFullName() != null ? user.getFullName() : user.getEmail();
            userRoleDisplay = user.getRole() != null ? user.getRole().getValue() : "Patient";
        }
        
        // Get page action
        String action = (String) request.getAttribute("action");
        if (action == null) action = "list";
        
        // Get template data
        MedicalExamTemplate template = (MedicalExamTemplate) request.getAttribute("template");
        List<MedicalExamTemplate> templates = (List<MedicalExamTemplate>) request.getAttribute("templates");
        String searchKeyword = (String) request.getAttribute("searchKeyword");
        Integer totalTemplates = (Integer) request.getAttribute("totalTemplates");
        if (totalTemplates == null) totalTemplates = 0;
        
        // Get messages
        String successMessage = (String) request.getAttribute("successMessage");
        String errorMessage = (String) request.getAttribute("errorMessage");
        %>

        <!-- Sidebar -->
        <nav id="sidebar">
            <div class="sidebar-header">
                <h3>MENU</h3>
            </div>
            <ul class="list-unstyled components">
                <% if (currentRole == User.Role.ADMIN) { %>
                <!-- Menu cho Admin -->
                <li>
                    <a href="${pageContext.request.contextPath}/homepage">
                        <i class="bi bi-speedometer2"></i> Trang chủ
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/authorization">
                        <i class="bi bi-people-fill"></i> Quản lý người dùng
                    </a>
                </li>
                <li class="active">
                    <a href="${pageContext.request.contextPath}/admin/medical-exam-templates">
                        <i class="bi bi-file-text"></i> Mẫu khám bệnh
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/services">
                        <i class="bi bi-file-medical"></i> Quản lý dịch vụ
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/medicines">
                        <i class="bi bi-hospital"></i> Quản lý kho thuốc
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/prescriptions">
                        <i class="bi bi-capsule"></i> Quản lý thuốc
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/medical-supplies">
                        <i class="bi bi-gear-fill"></i> Quản lý vật tư
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/report">
                        <i class="bi bi-bar-chart-fill"></i> Báo cáo thống kê
                    </a>
                </li>
                <% } %>
            </ul>
        </nav>

        <!-- Main Content -->
        <div id="content">
            <!-- Top Navbar -->
            <nav class="navbar navbar-expand-lg top-navbar">
                <div class="container-fluid">
                    <button type="button" id="sidebarCollapse" class="btn btn-primary">
                        <i class="bi bi-list"></i>
                    </button>
                    
                    <div style="margin-left: 60px; margin-top: 10px">
                        <h3>
                            <span style="color: #007bff;">Ánh Dương</span>
                            <span style="color: #333;">Clinic</span>
                        </h3>
                    </div>

                    <div class="navbar-search mx-auto">
                        <i class="bi bi-search"></i>
                        <input type="text" class="form-control" placeholder="Tìm kiếm">
                    </div>

                    <div class="dropdown user-dropdown">
                        <button class="btn dropdown-toggle" type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                            <div class="user-profile-icon">
                                <i class="bi bi-person-fill" style="font-size: 1.5rem;"></i>
                            </div>
                            <div class="user-info d-none d-md-block">
                                <div class="user-name"><%= userName %></div>
                                <div class="user-role"><%= userRoleDisplay %></div>
                            </div>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">
                                    <i class="bi bi-person-fill"></i>
                                    <span>Thông tin cá nhân</span>
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">
                                    <i class="bi bi-key-fill"></i>
                                    <span>Đổi mật khẩu</span>
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="#">
                                    <i class="bi bi-gear-fill"></i>
                                    <span>Cài đặt</span>
                                </a>
                            </li>
                            <li><hr class="dropdown-divider"></li>
                            <li>
                                <a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth/logout">
                                    <i class="bi bi-box-arrow-right"></i>
                                    <span>Đăng xuất</span>
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>

            <!-- Main Content Area -->
            <div class="container-fluid mt-4">
                <!-- Page Header -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div>
                            <h2 class="text-primary fw-bold mb-1">
                                <i class="bi bi-file-text me-2"></i>Quản lý mẫu đơn khám bệnh
                            </h2>
                            <p class="text-muted mb-0">Quản lý các mẫu đơn khám bệnh trong hệ thống</p>
                        </div>
                    </div>
                </div>

                <!-- Messages -->
                <% if (successMessage != null && !successMessage.isEmpty()) { %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle me-2"></i><%= successMessage %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>
                
                <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i><%= errorMessage %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <% if ("list".equals(action)) { %>
                <!-- Template List View -->
                <!-- Search and Add Section -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col-md-8">
                                        <form method="GET" action="${pageContext.request.contextPath}/admin/medical-exam-templates/search" class="d-flex">
                                            <div class="input-group">
                                                <span class="input-group-text">
                                                    <i class="bi bi-search"></i>
                                                </span>
                                                <input type="text" class="form-control" name="keyword" 
                                                       placeholder="Tìm kiếm theo tên mẫu đơn khám bệnh..." 
                                                       value="<%= searchKeyword != null ? searchKeyword : "" %>">
                                                <button class="btn btn-primary" type="submit">
                                                    Tìm kiếm
                                                </button>
                                                <% if (searchKeyword != null && !searchKeyword.trim().isEmpty()) { %>
                                                <a href="${pageContext.request.contextPath}/admin/medical-exam-templates/list" class="btn btn-outline-secondary">
                                                    <i class="bi bi-x-circle"></i>
                                                </a>
                                                <% } %>
                                            </div>
                                        </form>
                                    </div>
                                    <div class="col-md-4">
                                        <a href="${pageContext.request.contextPath}/admin/medical-exam-templates/add" class="btn btn-success w-100">
                                            <i class="bi bi-plus-circle me-2"></i>Thêm mẫu đơn khám bệnh
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Templates List -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card shadow-sm border-0">
                            <div class="card-header bg-light">
                                <h5 class="mb-0 text-primary">
                                    <i class="bi bi-list-ul me-2"></i>Danh sách mẫu đơn
                                    <span class="badge bg-primary ms-2"><%= totalTemplates %></span>
                                    <% if (searchKeyword != null && !searchKeyword.trim().isEmpty()) { %>
                                    <span class="badge bg-info ms-2">Kết quả tìm kiếm: "<%= searchKeyword %>"</span>
                                    <% } %>
                                </h5>
                            </div>
                            <div class="card-body">
                                <% if (templates != null && !templates.isEmpty()) { %>
                                <div class="row">
                                    <% for (MedicalExamTemplate tmpl : templates) { %>
                                    <div class="col-lg-6 col-xl-4 mb-4">
                                        <div class="card template-card h-100">
                                            <div class="card-body">
                                                <h6 class="card-title text-primary fw-bold mb-3">
                                                    <i class="bi bi-file-text me-2"></i><%= tmpl.getName() %>
                                                </h6>
                                                <div class="template-content mb-3">
                                                    <small class="text-muted d-block mb-1">Khám lâm sàng:</small>
                                                    <p class="text-truncate mb-2"><%= tmpl.getPhysicalExam() != null ? tmpl.getPhysicalExam() : "" %></p>
                                                    
                                                    <small class="text-muted d-block mb-1">Thông tin lâm sàng:</small>
                                                    <p class="text-truncate mb-2"><%= tmpl.getClinicalInfo() != null ? tmpl.getClinicalInfo() : "" %></p>
                                                </div>
                                                <div class="d-flex justify-content-between align-items-center mt-3">
                                                    <small class="text-muted">
                                                        <i class="bi bi-calendar me-1"></i>
                                                        <%= tmpl.getCreatedAt() != null ? new SimpleDateFormat("dd/MM/yyyy").format(tmpl.getCreatedAt()) : "" %>
                                                    </small>
                                                    <div class="d-flex gap-2">
                                                        <a href="${pageContext.request.contextPath}/admin/medical-exam-templates/edit?id=<%= tmpl.getId() %>" 
                                                           class="btn btn-sm btn-primary me-2" title="Chỉnh sửa mẫu đơn">
                                                            <i class="bi bi-pencil-square"></i>
                                                        </a>
                                                                                                <button type="button" 
                                                class="btn btn-sm btn-outline-danger" 
                                                data-template-id="<%= tmpl.getId() %>"
                                                data-template-name="<%= tmpl.getName() %>"
                                                onclick="deleteTemplate(this.getAttribute('data-template-id'), this.getAttribute('data-template-name'))" 
                                                title="Xóa mẫu đơn">
                                            <i class="bi bi-trash3"></i>
                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <% } %>
                                </div>
                                <% } else { %>
                                <div class="text-center py-5">
                                    <i class="bi bi-inbox text-muted" style="font-size: 4rem;"></i>
                                    <h5 class="text-muted mt-3">Không có mẫu đơn nào</h5>
                                    <p class="text-muted">Hãy thêm mẫu đơn đầu tiên của bạn</p>
                                    <a href="${pageContext.request.contextPath}/admin/medical-exam-templates/add" class="btn btn-primary btn-custom">
                                        <i class="bi bi-plus-circle me-2"></i>Thêm mẫu đơn khám bệnh
                                    </a>
                                </div>
                                <% } %>
                            </div>
                        </div>
                    </div>
                </div>

                <% } else if (("add".equals(action)) || ("edit".equals(action) && template != null)) { %>
                <!-- Add/Edit Form -->
                <div class="row">
                    <div class="col-12">
                        <div class="card shadow-sm border-0">
                            <div class="card-header bg-primary text-white">
                                <h5 class="mb-0">
                                    <i class="bi bi-<%= "add".equals(action) ? "plus-circle" : "pencil" %> me-2"></i>
                                    <%= "add".equals(action) ? "Thêm mẫu đơn mới" : "Chỉnh sửa mẫu đơn" %>
                                </h5>
                            </div>
                            <div class="card-body p-4">
                                <form method="POST" action="${pageContext.request.contextPath}/admin/medical-exam-templates/<%= "add".equals(action) ? "add" : "update" %>">
                                    <% if ("edit".equals(action) && template != null) { %>
                                    <input type="hidden" name="id" value="<%= template.getId() %>">
                                    <% } %>
                                    
                                    <div class="row">
                                        <div class="col-12 mb-4">
                                            <label for="name" class="form-label">Tên mẫu đơn <span class="text-danger">*</span></label>
                                            <input type="text" class="form-control" id="name" name="name" required
                                                   value="<%= template != null ? template.getName() : (request.getAttribute("name") != null ? request.getAttribute("name") : "") %>">
                                        </div>
                                        
                                        <div class="col-12 mb-4">
                                            <label for="physicalExam" class="form-label">Khám lâm sàng</label>
                                            <textarea class="form-control" id="physicalExam" name="physicalExam" rows="4"
                                                      placeholder="Nhập thông tin khám lâm sàng..."><%= template != null ? (template.getPhysicalExam() != null ? template.getPhysicalExam() : "") : (request.getAttribute("physicalExam") != null ? request.getAttribute("physicalExam") : "") %></textarea>
                                        </div>
                                        
                                        <div class="col-12 mb-4">
                                            <label for="clinicalInfo" class="form-label">Thông tin lâm sàng</label>
                                            <textarea class="form-control" id="clinicalInfo" name="clinicalInfo" rows="4"
                                                      placeholder="Nhập thông tin lâm sàng..."><%= template != null ? (template.getClinicalInfo() != null ? template.getClinicalInfo() : "") : (request.getAttribute("clinicalInfo") != null ? request.getAttribute("clinicalInfo") : "") %></textarea>
                                        </div>
                                        
                                        <div class="col-12 mb-4">
                                            <label for="finalDiagnosis" class="form-label">Chẩn đoán cuối cùng</label>
                                            <textarea class="form-control" id="finalDiagnosis" name="finalDiagnosis" rows="4"
                                                      placeholder="Nhập chẩn đoán cuối cùng..."><%= template != null ? (template.getFinalDiagnosis() != null ? template.getFinalDiagnosis() : "") : (request.getAttribute("finalDiagnosis") != null ? request.getAttribute("finalDiagnosis") : "") %></textarea>
                                        </div>
                                    </div>
                                    
                                    <div class="d-flex justify-content-end gap-2">
                                        <a href="${pageContext.request.contextPath}/admin/medical-exam-templates/list" 
                                           class="btn btn-secondary btn-custom">
                                            <i class="bi bi-x-circle me-2"></i>Hủy
                                        </a>
                                        <button type="submit" class="btn btn-primary btn-custom">
                                            <i class="bi bi-<%= "add".equals(action) ? "plus" : "check" %>-circle me-2"></i>
                                            <%= "add".equals(action) ? "Thêm mẫu đơn" : "Cập nhật" %>
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
        </div>

        <!-- Delete Confirmation Modal -->
        <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title text-danger" id="deleteConfirmModalLabel">
                            <i class="bi bi-exclamation-triangle me-2"></i>Xác nhận xóa
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>Bạn có chắc chắn muốn xóa mẫu đơn <strong id="deleteTemplateName"></strong> không?</p>
                        <p class="text-danger">
                            <i class="bi bi-exclamation-triangle me-2"></i>
                            Hành động này không thể hoàn tác!
                        </p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                        </button>
                        <form method="GET" action="${pageContext.request.contextPath}/admin/medical-exam-templates/delete" style="display: inline;">
                            <input type="hidden" id="deleteTemplateId" name="id">
                            <button type="submit" class="btn btn-danger">
                                <i class="bi bi-trash me-2"></i>Xóa
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- Bootstrap Bundle with Popper -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Sidebar toggle
                const sidebarCollapse = document.getElementById('sidebarCollapse');
                const sidebar = document.getElementById('sidebar');
                const content = document.getElementById('content');

                sidebarCollapse.addEventListener('click', function () {
                    sidebar.classList.toggle('collapsed');
                    content.classList.toggle('expanded');
                });

                // Responsive sidebar
                function checkWidth() {
                    if (window.innerWidth <= 768) {
                        sidebar.classList.add('collapsed');
                        content.classList.add('expanded');
                    } else {
                        sidebar.classList.remove('collapsed');
                        content.classList.remove('expanded');
                    }
                }

                // Initial check
                checkWidth();

                // Listen for window resize
                window.addEventListener('resize', checkWidth);
            });

            // Delete template function
            function deleteTemplate(templateId, templateName) {
                document.getElementById('deleteTemplateId').value = templateId;
                document.getElementById('deleteTemplateName').textContent = templateName;
                new bootstrap.Modal(document.getElementById('deleteConfirmModal')).show();
            }
        </script>
    </body>
</html> 