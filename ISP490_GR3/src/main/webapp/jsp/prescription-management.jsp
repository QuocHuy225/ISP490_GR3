<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.PrescriptionMedicine" %>
<%@ page import="com.mycompany.isp490_gr3.model.PrescriptionForm" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý đơn thuốc - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Homepage specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <!-- DataTables CSS -->
        <link rel="stylesheet" href="https://cdn.datatables.net/1.13.7/css/dataTables.bootstrap5.min.css">
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
        
        // Get data from request
        List<PrescriptionMedicine> medicines = (List<PrescriptionMedicine>) request.getAttribute("medicines");
        List<PrescriptionForm> prescriptionForms = (List<PrescriptionForm>) request.getAttribute("prescriptionForms");
        String medicineSearchKeyword = (String) request.getAttribute("medicineSearchKeyword");
        String formSearchKeyword = (String) request.getAttribute("formSearchKeyword");
        String activeTab = (String) request.getAttribute("activeTab"); // "medicines" or "forms"
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
                <li class="active">
                    <a href="${pageContext.request.contextPath}/admin/prescriptions">
                        <i class="bi bi-capsule"></i> Quản lý đơn thuốc
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/medical-exam-templates">
                        <i class="bi bi-file-text"></i> Mẫu đơn khám bệnh
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/medical-supplies">
                        <i class="bi bi-gear-fill"></i> Quản lý vật tư
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-bar-chart-fill"></i> Báo cáo thống kê
                    </a>
                </li>
                <% } else if (currentRole == User.Role.DOCTOR) { %>
                <!-- Menu cho Bác sĩ -->
                <li>
                    <a href="${pageContext.request.contextPath}/homepage">
                        <i class="bi bi-speedometer2"></i> Trang chủ
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-calendar-check"></i> Lịch khám bệnh
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-file-medical"></i> Hồ sơ bệnh án
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-people"></i> Danh sách bệnh nhân
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-clipboard-pulse"></i> Toa thuốc
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-journal-medical"></i> Chỉ định dịch vụ
                    </a>
                </li>
                <% } else if (currentRole == User.Role.RECEPTIONIST) { %>
                <!-- Menu cho Lễ tân -->
                <li>
                    <a href="${pageContext.request.contextPath}/homepage">
                        <i class="bi bi-speedometer2"></i> Trang chủ
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-calendar-plus"></i> Đặt lịch hẹn
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/appointments">
                        <i class="bi bi-calendar-check"></i> Quản lý lịch hẹn
                    </a>
                </li>
               
                <% } else { %>
                <!-- Menu cho Bệnh nhân (PATIENT) -->
                <li>
                    <a href="${pageContext.request.contextPath}/homepage">
                        <i class="bi bi-speedometer2"></i> Trang chủ
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-calendar-plus"></i> Đặt lịch hẹn
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-calendar-check"></i> Lịch hẹn của tôi
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-file-medical"></i> Hồ sơ sức khỏe
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-hospital"></i> Dịch vụ
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-chat-dots"></i> Liên hệ bác sĩ
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
                        <h2 class="text-primary">
                            <i class="bi bi-capsule me-2"></i>Quản lý đơn thuốc
                        </h2>
                    </div>
                </div>

                <!-- Alert Messages -->
                <%
                String success = request.getParameter("success");
                String error = request.getParameter("error");
                %>
                <% if (success != null) { %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle me-2"></i>
                    <% if ("medicine_added".equals(success)) { %>
                        Thêm thuốc thành công!
                    <% } else if ("medicine_updated".equals(success)) { %>
                        Cập nhật thông tin thuốc thành công!
                    <% } else if ("medicine_deleted".equals(success)) { %>
                        Xóa thuốc thành công!
                    <% } else if ("form_added".equals(success)) { %>
                        Thêm đơn thuốc mẫu thành công!
                    <% } else if ("form_updated".equals(success)) { %>
                        Cập nhật đơn thuốc mẫu thành công!
                    <% } else if ("form_deleted".equals(success)) { %>
                        Xóa đơn thuốc mẫu thành công!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <% if ("missing_fields".equals(error)) { %>
                        Vui lòng điền đầy đủ thông tin!
                    <% } else if ("medicine_exists".equals(error)) { %>
                        Tên thuốc này đã tồn tại trong hệ thống!
                    <% } else if ("form_exists".equals(error)) { %>
                        Tên đơn thuốc mẫu này đã tồn tại trong hệ thống!
                    <% } else if ("add_failed".equals(error)) { %>
                        Thêm thất bại!
                    <% } else if ("update_failed".equals(error)) { %>
                        Cập nhật thất bại!
                    <% } else if ("delete_failed".equals(error)) { %>
                        Xóa thất bại!
                    <% } else if ("invalid_format".equals(error)) { %>
                        Định dạng dữ liệu không hợp lệ!
                    <% } else { %>
                        Có lỗi xảy ra. Vui lòng thử lại!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <!-- Tab Navigation -->
                <ul class="nav nav-tabs" id="prescriptionTabs" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link <%= (activeTab == null || "medicines".equals(activeTab)) ? "active" : "" %>" 
                                id="medicines-tab" data-bs-toggle="tab" data-bs-target="#medicines" type="button" role="tab">
                            <i class="bi bi-pill me-2"></i>Quản lý thuốc
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link <%= "forms".equals(activeTab) ? "active" : "" %>" 
                                id="prescription-forms-tab" data-bs-toggle="tab" data-bs-target="#prescription-forms" type="button" role="tab">
                            <i class="bi bi-clipboard-pulse me-2"></i>Đơn thuốc mẫu
                        </button>
                    </li>
                </ul>

                <!-- Tab Content -->
                <div class="tab-content" id="prescriptionTabsContent">
                    <!-- Medicines Tab -->
                    <div class="tab-pane fade <%= (activeTab == null || "medicines".equals(activeTab)) ? "show active" : "" %>" id="medicines" role="tabpanel">
                        <div class="card">
                            <div class="card-body">
                                <!-- Search and Add Medicine Section -->
                                <div class="row mb-4">
                                    <div class="col-12">
                                        <div class="card">
                                            <div class="card-body">
                                                <div class="row align-items-center">
                                                    <div class="col-md-8">
                                                        <form method="GET" action="${pageContext.request.contextPath}/admin/prescriptions" class="d-flex">
                                                            <input type="hidden" name="action" value="searchMedicine">
                                                            <div class="input-group">
                                                                <span class="input-group-text">
                                                                    <i class="bi bi-search"></i>
                                                                </span>
                                                                <input type="text" class="form-control" name="keyword" 
                                                                       placeholder="Tìm kiếm thuốc theo tên..." 
                                                                       value="<%= medicineSearchKeyword != null ? medicineSearchKeyword : "" %>">
                                                                <button class="btn btn-primary" type="submit">
                                                                    Tìm kiếm
                                                                </button>
                                                                <% if (medicineSearchKeyword != null && !medicineSearchKeyword.trim().isEmpty()) { %>
                                                                <a href="${pageContext.request.contextPath}/admin/prescriptions" class="btn btn-outline-secondary">
                                                                    <i class="bi bi-x-circle"></i>
                                                                </a>
                                                                <% } %>
                                                            </div>
                                                        </form>
                                                    </div>
                                                    <div class="col-md-4">
                                                        <button type="button" class="btn btn-success w-100" data-bs-toggle="modal" data-bs-target="#addMedicineModal">
                                                            <i class="bi bi-plus-circle me-2"></i>Thêm thuốc
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>



                                <!-- Medicines Table -->
                                <div class="row">
                                    <div class="col-12">
                                        <div class="card">
                                            <div class="card-header">
                                                <h5 class="card-title mb-0">
                                                    <i class="bi bi-table me-2"></i>Danh sách thuốc
                                                    <% if (medicineSearchKeyword != null && !medicineSearchKeyword.trim().isEmpty()) { %>
                                                    <span class="badge bg-primary ms-2">Kết quả tìm kiếm: "<%= medicineSearchKeyword %>"</span>
                                                    <% } %>
                                                </h5>
                                            </div>
                                            <div class="card-body">
                                                <div class="table-responsive">
                                                    <table id="medicinesTable" class="table table-striped table-hover">
                                                        <thead class="table-dark">
                                                            <tr>
                                                                <th>ID</th>
                                                                <th>Tên thuốc</th>
                                                                <th>Đơn vị tính</th>
                                                                <th>Đường dùng</th>
                                                                <th>Thao tác</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <% if (medicines != null && !medicines.isEmpty()) {
                                                                for (PrescriptionMedicine medicine : medicines) { %>
                                                                <tr>
                                                                    <td><%= medicine.getPreMedicineId() %></td>
                                                                    <td><%= medicine.getMedicineName() %></td>
                                                                    <td><%= medicine.getUnitOfMeasure() %></td>
                                                                    <td><%= medicine.getAdministrationRoute() %></td>
                                                                    <td>
                                                                        <a href="${pageContext.request.contextPath}/admin/prescriptions?editMedicine=<%= medicine.getPreMedicineId() %>" 
                                                                           class="btn btn-sm btn-primary me-1" title="Chỉnh sửa">
                                                                            <i class="bi bi-pencil"></i>
                                                                        </a>
                                                                        <form method="POST" action="${pageContext.request.contextPath}/admin/prescriptions/medicines" 
                                                                              style="display: inline-block;" 
                                                                              onsubmit="return confirm('Bạn có chắc chắn muốn xóa thuốc?')">
                                                                            <input type="hidden" name="action" value="delete">
                                                                            <input type="hidden" name="medicineId" value="<%= medicine.getPreMedicineId() %>">
                                                                            <button type="submit" class="btn btn-sm btn-danger" title="Xóa">
                                                                                <i class="bi bi-trash"></i>
                                                                            </button>
                                                                        </form>
                                                                    </td>
                                                                </tr>
                                                                <% } %>
                                                            <% } else { %>
                                                                <tr>
                                                                    <td colspan="5" class="text-center">
                                                                        <i class="bi bi-inbox me-2"></i>
                                                                        Chưa có thuốc nào trong hệ thống
                                                                    </td>
                                                                </tr>
                                                            <% } %>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Prescription Forms Tab -->
                    <div class="tab-pane fade <%= "forms".equals(activeTab) ? "show active" : "" %>" id="prescription-forms" role="tabpanel">
                        <div class="card">
                            <div class="card-body">
                                <!-- Search and Add Form Section -->
                                <div class="row mb-4">
                                    <div class="col-12">
                                        <div class="card">
                                            <div class="card-body">
                                                <div class="row align-items-center">
                                                    <div class="col-md-8">
                                                        <form method="GET" action="${pageContext.request.contextPath}/admin/prescriptions" class="d-flex">
                                                            <input type="hidden" name="action" value="searchForm">
                                                            <div class="input-group">
                                                                <span class="input-group-text">
                                                                    <i class="bi bi-search"></i>
                                                                </span>
                                                                <input type="text" class="form-control" name="keyword" 
                                                                       placeholder="Tìm kiếm đơn thuốc mẫu theo tên..." 
                                                                       value="<%= formSearchKeyword != null ? formSearchKeyword : "" %>">
                                                                <button class="btn btn-primary" type="submit">
                                                                    Tìm kiếm
                                                                </button>
                                                                <% if (formSearchKeyword != null && !formSearchKeyword.trim().isEmpty()) { %>
                                                                <a href="${pageContext.request.contextPath}/admin/prescriptions" class="btn btn-outline-secondary">
                                                                    <i class="bi bi-x-circle"></i>
                                                                </a>
                                                                <% } %>
                                                            </div>
                                                        </form>
                                                    </div>
                                                    <div class="col-md-4">
                                                        <button type="button" class="btn btn-success w-100" data-bs-toggle="modal" data-bs-target="#addFormModal">
                                                            <i class="bi bi-plus-circle me-2"></i>Thêm đơn thuốc mẫu
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <% if (formSearchKeyword != null && !formSearchKeyword.trim().isEmpty()) { %>
                                    <div class="alert alert-info">
                                        <i class="bi bi-info-circle me-2"></i>
                                        Kết quả tìm kiếm cho: "<strong><%= formSearchKeyword %></strong>"
                                    </div>
                                    <% } %>
                                </div>



                                <!-- Prescription Forms Grid -->
                                <div class="row" id="prescriptionFormsList">
                                    <% if (prescriptionForms != null && !prescriptionForms.isEmpty()) {
                                        for (PrescriptionForm form : prescriptionForms) { %>
                                        <div class="col-lg-4 col-md-6 mb-4">
                                            <div class="card h-100 shadow-sm">
                                                <div class="card-header bg-primary text-white">
                                                    <h6 class="card-title mb-0">
                                                        <i class="bi bi-clipboard-pulse me-2"></i>
                                                        <%= form.getFormName() %>
                                                    </h6>
                                                </div>
                                                <div class="card-body">
                                                    <% if (form.getNotes() != null && !form.getNotes().trim().isEmpty()) { %>
                                                    <p class="card-text text-muted mb-3">
                                                        <small><%= form.getNotes() %></small>
                                                    </p>
                                                    <% } %>
                                                    
                                                    <h6 class="text-secondary mb-2">Thuốc trong đơn:</h6>
                                                    <div class="medicine-list" style="max-height: 200px; overflow-y: auto;">
                                                        <% if (form.getMedicines() != null && !form.getMedicines().isEmpty()) { %>
                                                            <% for (PrescriptionMedicine medicine : form.getMedicines()) { %>
                                                            <div class="d-flex align-items-center mb-2">
                                                                <i class="bi bi-pill text-success me-2"></i>
                                                                <span class="small"><%= medicine.getMedicineName() %></span>
                                                            </div>
                                                            <% } %>
                                                        <% } else { %>
                                                            <p class="text-muted small">Chưa có thuốc nào</p>
                                                        <% } %>
                                                    </div>
                                                </div>
                                                <div class="card-footer bg-light">
                                                    <div class="d-flex justify-content-between">
                                                        <a href="${pageContext.request.contextPath}/admin/prescriptions?editForm=<%= form.getPrescriptionFormId() %>&tab=forms" 
                                                           class="btn btn-sm btn-primary" title="Chỉnh sửa">
                                                            <i class="bi bi-pencil"></i> Chỉnh sửa
                                                        </a>
                                                        <form method="POST" action="${pageContext.request.contextPath}/admin/prescriptions/forms" 
                                                              style="display: inline-block;" 
                                                              onsubmit="return confirm('Bạn có chắc chắn muốn xóa đơn thuốc mẫu?')">
                                                            <input type="hidden" name="action" value="delete">
                                                            <input type="hidden" name="formId" value="<%= form.getPrescriptionFormId() %>">
                                                            <button type="submit" class="btn btn-sm btn-danger" title="Xóa">
                                                                <i class="bi bi-trash"></i> Xóa
                                                            </button>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <% } %>
                                    <% } else { %>
                                        <div class="col-12">
                                            <div class="text-center py-5">
                                                <i class="bi bi-inbox display-1 text-muted"></i>
                                                <h5 class="text-muted mt-3">Chưa có đơn thuốc mẫu nào</h5>
                                                <p class="text-muted">Thêm đơn thuốc mẫu đầu tiên của bạn</p>
                                            </div>
                                        </div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Add Medicine Modal -->
        <div class="modal fade" id="addMedicineModal" tabindex="-1" aria-labelledby="addMedicineModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addMedicineModalLabel">
                            <i class="bi bi-plus-circle me-2"></i>Thêm thuốc mới
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/prescriptions/medicines">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="add">
                            
                            <div class="mb-3">
                                <label for="addMedicineName" class="form-label">Tên thuốc <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addMedicineName" name="medicineName" placeholder="Nhập tên thuốc" required>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="addUnitOfMeasure" class="form-label">Đơn vị tính <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="addUnitOfMeasure" name="unitOfMeasure" placeholder="viên, ml, gói..." required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="addAdministrationRoute" class="form-label">Đường dùng <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="addAdministrationRoute" name="administrationRoute" placeholder="Uống, tiêm, bôi..." required>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-circle me-2"></i>Thêm thuốc
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>



        <!-- Add Prescription Form Modal -->
        <div class="modal fade" id="addFormModal" tabindex="-1" aria-labelledby="addFormModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addFormModalLabel">
                            <i class="bi bi-plus-circle me-2"></i>Thêm đơn thuốc mẫu
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/prescriptions/forms">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="add">
                            
                            <div class="mb-3">
                                <label for="addFormName" class="form-label">Tên đơn thuốc mẫu <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addFormName" name="formName" placeholder="Nhập tên bệnh hoặc triệu chứng" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="addFormNotes" class="form-label">Ghi chú</label>
                                <textarea class="form-control" id="addFormNotes" name="notes" rows="3" placeholder="Nhập ghi chú về đơn thuốc mẫu"></textarea>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Chọn thuốc cho đơn mẫu</label>
                                <div class="border rounded p-3" style="max-height: 300px; overflow-y: auto;">
                                    <% if (medicines != null && !medicines.isEmpty()) { %>
                                        <% for (PrescriptionMedicine medicine : medicines) { %>
                                        <div class="form-check">
                                            <input class="form-check-input" type="checkbox" name="medicineIds" value="<%= medicine.getPreMedicineId() %>" id="addMedicine<%= medicine.getPreMedicineId() %>">
                                            <label class="form-check-label" for="addMedicine<%= medicine.getPreMedicineId() %>">
                                                <strong><%= medicine.getMedicineName() %></strong>
                                                <small class="text-muted">(<%= medicine.getUnitOfMeasure() %> - <%= medicine.getAdministrationRoute() %>)</small>
                                            </label>
                                        </div>
                                        <% } %>
                                    <% } else { %>
                                        <p class="text-muted">Không có thuốc nào trong hệ thống. Vui lòng thêm thuốc trước.</p>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-circle me-2"></i>Thêm đơn mẫu
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <%
        // Get edit data for modals
        PrescriptionMedicine editMedicine = (PrescriptionMedicine) request.getAttribute("editMedicine");
        PrescriptionForm editForm = (PrescriptionForm) request.getAttribute("editForm");
        List<PrescriptionMedicine> allMedicines = (List<PrescriptionMedicine>) request.getAttribute("allMedicines");
        %>

        <!-- Edit Medicine Modal -->
        <div class="modal fade" id="editMedicineModal" tabindex="-1" aria-labelledby="editMedicineModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editMedicineModalLabel">
                            <i class="bi bi-pencil me-2"></i>Chỉnh sửa thuốc
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <% if (editMedicine != null) { %>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/prescriptions/medicines">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="medicineId" value="<%= editMedicine.getPreMedicineId() %>">
                            
                            <div class="mb-3">
                                <label for="editMedicineName" class="form-label">Tên thuốc <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="editMedicineName" name="medicineName" 
                                       value="<%= editMedicine.getMedicineName() %>" required>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editUnitOfMeasure" class="form-label">Đơn vị tính <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="editUnitOfMeasure" name="unitOfMeasure" 
                                               value="<%= editMedicine.getUnitOfMeasure() %>" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editAdministrationRoute" class="form-label">Đường dùng <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="editAdministrationRoute" name="administrationRoute" 
                                               value="<%= editMedicine.getAdministrationRoute() %>" required>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <a href="${pageContext.request.contextPath}/admin/prescriptions" class="btn btn-secondary">Hủy</a>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check-circle me-2"></i>Cập nhật
                            </button>
                        </div>
                    </form>
                    <% } %>
                </div>
            </div>
        </div>

        <!-- Edit Prescription Form Modal -->
        <div class="modal fade" id="editFormModal" tabindex="-1" aria-labelledby="editFormModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editFormModalLabel">
                            <i class="bi bi-pencil me-2"></i>Chỉnh sửa đơn thuốc mẫu
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <% if (editForm != null) { %>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/prescriptions/forms">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="formId" value="<%= editForm.getPrescriptionFormId() %>">
                            
                            <div class="mb-3">
                                <label for="editFormName" class="form-label">Tên đơn thuốc mẫu <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="editFormName" name="formName" 
                                       value="<%= editForm.getFormName() %>" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editFormNotes" class="form-label">Ghi chú</label>
                                <textarea class="form-control" id="editFormNotes" name="notes" rows="3"><%= editForm.getNotes() != null ? editForm.getNotes() : "" %></textarea>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Chọn thuốc cho đơn mẫu</label>
                                <div class="border rounded p-3" style="max-height: 300px; overflow-y: auto;">
                                    <% if (allMedicines != null && !allMedicines.isEmpty()) { 
                                        // Create set of selected medicine IDs for easier checking
                                        java.util.Set<Integer> selectedMedicineIds = new java.util.HashSet<>();
                                        if (editForm.getMedicines() != null) {
                                            for (PrescriptionMedicine medicine : editForm.getMedicines()) {
                                                selectedMedicineIds.add(medicine.getPreMedicineId());
                                            }
                                        }
                                        
                                        for (PrescriptionMedicine medicine : allMedicines) { %>
                                        <div class="form-check">
                                            <input class="form-check-input" type="checkbox" name="medicineIds" 
                                                   value="<%= medicine.getPreMedicineId() %>" 
                                                   id="editMedicine<%= medicine.getPreMedicineId() %>"
                                                   <%= selectedMedicineIds.contains(medicine.getPreMedicineId()) ? "checked" : "" %>>
                                            <label class="form-check-label" for="editMedicine<%= medicine.getPreMedicineId() %>">
                                                <strong><%= medicine.getMedicineName() %></strong>
                                                <small class="text-muted">(<%= medicine.getUnitOfMeasure() %> - <%= medicine.getAdministrationRoute() %>)</small>
                                            </label>
                                        </div>
                                        <% } %>
                                    <% } else { %>
                                        <p class="text-muted">Không có thuốc nào trong hệ thống.</p>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <a href="${pageContext.request.contextPath}/admin/prescriptions?tab=forms" class="btn btn-secondary">Hủy</a>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check-circle me-2"></i>Cập nhật
                            </button>
                        </div>
                    </form>
                    <% } %>
                </div>
            </div>
        </div>

        <!-- JavaScript Libraries -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
        <script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
        <script src="https://cdn.datatables.net/1.13.7/js/dataTables.bootstrap5.min.js"></script>

        <script>
        $(document).ready(function() {
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

            // Initialize DataTables
            $('#medicinesTable').DataTable({
                pageLength: 10,
                responsive: true,
                searching: false, // Disable DataTables search
                language: {
                    url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/vi.json'
                }
            });
        });
        </script>

        <% if (editMedicine != null) { %>
        <script>
        $(document).ready(function() {
            $('#editMedicineModal').modal('show');
        });
        </script>
        <% } %>

        <% if (editForm != null) { %>
        <script>
        $(document).ready(function() {
            $('#editFormModal').modal('show');
        });
        </script>
        <% } %>
    </body>
</html> 