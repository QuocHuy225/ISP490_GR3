<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.Patient" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý hồ sơ bệnh nhân - Ánh Dương Clinic</title>
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
        <style>
            /* Soft button styles */
            .btn-soft-primary {
                color: #0d6efd;
                background-color: rgba(13, 110, 253, 0.1);
                border: none;
            }
            .btn-soft-primary:hover {
                color: #fff;
                background-color: #0d6efd;
            }
            
            .btn-soft-info {
                color: #0dcaf0;
                background-color: rgba(13, 202, 240, 0.1);
                border: none;
            }
            .btn-soft-info:hover {
                color: #fff;
                background-color: #0dcaf0;
            }
            
            .btn-soft-danger {
                color: #dc3545;
                background-color: rgba(220, 53, 69, 0.1);
                border: none;
            }
            .btn-soft-danger:hover {
                color: #fff;
                background-color: #dc3545;
            }
            
            /* Button transition */
            .btn-sm {
                transition: all 0.2s ease-in-out;
            }
            .btn-sm:hover {
                transform: translateY(-1px);
            }
            
            /* Tooltip customization */
            .tooltip {
                font-size: 12px;
            }
            
            /* Custom Action Buttons Styling */
            .action-buttons-group {
                display: flex;
                gap: 8px;
                justify-content: center;
                align-items: center;
                flex-wrap: wrap;
            }
            
            .action-btn {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 8px 12px;
                border: none;
                border-radius: 8px;
                text-decoration: none;
                font-size: 13px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.3s ease;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                min-width: 70px;
                justify-content: center;
            }
            
            .action-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0,0,0,0.15);
                text-decoration: none;
            }
            
            .action-btn i {
                font-size: 14px;
            }
            
            .btn-text {
                font-size: 12px;
                white-space: nowrap;
            }
            
            /* Specific button colors */
            .action-btn-view {
                background: linear-gradient(135deg, #17a2b8, #138496);
                color: white;
            }
            .action-btn-view:hover {
                background: linear-gradient(135deg, #138496, #117a8b);
                color: white;
            }
            
            .action-btn-edit {
                background: linear-gradient(135deg, #007bff, #0056b3);
                color: white;
            }
            .action-btn-edit:hover {
                background: linear-gradient(135deg, #0056b3, #004085);
                color: white;
            }
            
            .action-btn-delete {
                background: linear-gradient(135deg, #dc3545, #bd2130);
                color: white;
                border: 2px solid transparent;
            }
            .action-btn-delete:hover {
                background: linear-gradient(135deg, #bd2130, #a71e2a);
                color: white;
                border-color: #dc3545;
            }
            
            /* Responsive design */
            @media (max-width: 768px) {
                .action-buttons-group {
                    gap: 4px;
                }
                
                .action-btn {
                    padding: 6px 8px;
                    min-width: 50px;
                }
                
                .btn-text {
                    display: none;
                }
                
                .action-btn i {
                    font-size: 16px;
                }
            }
            
            /* Enhanced table styling */
            .table tbody tr:hover .action-btn {
                transform: scale(1.05);
            }
            
            /* Loading effect */
            .action-btn:active {
                transform: scale(0.95);
                transition: transform 0.1s;
            }
            
            /* Custom tooltips enhancement */
            .action-btn[title]:hover::after {
                content: attr(title);
                position: absolute;
                background: rgba(0,0,0,0.8);
                color: white;
                padding: 4px 8px;
                border-radius: 4px;
                font-size: 11px;
                z-index: 1000;
                margin-top: -35px;
                margin-left: -20px;
                white-space: nowrap;
            }
            
            /* Custom search form styling */
            .search-form .form-control {
                height: 48px;
                padding: 0.5rem 1rem;
                font-size: 1rem;
            }
            
            .search-form .btn {
                height: 48px;
                padding: 0.5rem 1.5rem;
                font-size: 1rem;
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
                try {
                    currentRole = User.Role.valueOf(userRole.toString().toUpperCase());
                } catch (Exception e) {
                    currentRole = User.Role.fromString(userRole.toString());
                }
            }
        }
        
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
        
        // Get patients data
        List<Patient> patients = (List<Patient>) request.getAttribute("patients");
        String searchCode = (String) request.getAttribute("searchCode");
        String searchName = (String) request.getAttribute("searchName");
        String searchPhone = (String) request.getAttribute("searchPhone");
        String searchCccd = (String) request.getAttribute("searchCccd");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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
                <li class="active">
                    <a href="${pageContext.request.contextPath}/patients">
                        <i class="bi bi-people"></i> Hồ sơ bệnh nhân
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/medicines">
                        <i class="bi bi-hospital"></i> Quản lý kho thuốc
                    </a>
                </li>
                <li>
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
                <li class="active">
                    <a href="${pageContext.request.contextPath}/patients">
                        <i class="bi bi-people"></i> Hồ sơ bệnh nhân
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
                    <a href="${pageContext.request.contextPath}/appointments">
                        <i class="bi bi-calendar-check"></i> Quản lý đặt lịch
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/checkin">
                        <i class="bi bi-calendar-check"></i> Quản lý check-in
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/#">
                        <i class="bi bi-people-fill"></i> Quản lý hàng đợi
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/patients">
                        <i class="bi bi-people"></i> Hồ sơ bệnh nhân
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/receptionist/manage-doctor-schedule">
                        <i class="bi bi-calendar-event-fill"></i> Quản lý lịch bác sĩ
                    </a>
                </li> 
                 <li>
                    <a href="${pageContext.request.contextPath}/receptionist/report">
                        <i class="bi bi-speedometer2"></i> Báo cáo thống kê
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
                    <a href="${pageContext.request.contextPath}/makeappointments">
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
                            <i class="bi bi-people me-2"></i>Quản lý hồ sơ bệnh nhân
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
                    <% if ("added".equals(success)) { %>
                        Thêm bệnh nhân thành công!
                    <% } else if ("updated".equals(success)) { %>
                        Cập nhật thông tin bệnh nhân thành công!
                    <% } else if ("deleted".equals(success)) { %>
                        Xóa bệnh nhân thành công!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <% if ("missing_fields".equals(error)) { %>
                        Vui lòng điền đầy đủ thông tin!
                    <% } else if ("invalid_phone".equals(error)) { %>
                        Số điện thoại phải là 10-11 chữ số!
                    <% } else if ("invalid_cccd".equals(error)) { %>
                        CCCD phải là 12 chữ số!
                    <% } else if ("invalid_gender".equals(error)) { %>
                        Giới tính không hợp lệ!
                    <% } else if ("invalid_dob".equals(error)) { %>
                        Ngày sinh không được là tương lai!
                    <% } else if ("invalid_date_format".equals(error)) { %>
                        Định dạng ngày sinh không hợp lệ!
                    <% } else if ("add_failed".equals(error)) { %>
                        Thêm bệnh nhân thất bại!
                    <% } else if ("update_failed".equals(error)) { %>
                        Cập nhật thông tin bệnh nhân thất bại!
                    <% } else if ("delete_failed".equals(error)) { %>
                        Xóa bệnh nhân thất bại!
                    <% } else if ("system_error".equals(error)) { %>
                        Có lỗi hệ thống xảy ra!
                    <% } else { %>
                        Có lỗi xảy ra. Vui lòng thử lại!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <!-- Search and Add Patient Section -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-body">
                                <div class="row g-3 mb-3">
                                    <div class="col-md-9">
                                        <form method="GET" action="${pageContext.request.contextPath}/patients" class="search-form">
                                            <div class="row g-2">
                                                <div class="col">
                                                    <input type="text" class="form-control" name="code" 
                                                           placeholder="Mã bệnh nhân" 
                                                           value="<%= searchCode != null ? searchCode : "" %>">
                                                </div>
                                                <div class="col">
                                                    <input type="text" class="form-control" name="name" 
                                                           placeholder="Họ tên" 
                                                           value="<%= searchName != null ? searchName : "" %>">
                                                </div>
                                                <div class="col">
                                                    <input type="text" class="form-control" name="phone" 
                                                           placeholder="Số điện thoại" 
                                                           value="<%= searchPhone != null ? searchPhone : "" %>">
                                                </div>
                                                <div class="col">
                                                    <input type="text" class="form-control" name="cccd" 
                                                           placeholder="CCCD" 
                                                           value="<%= searchCccd != null ? searchCccd : "" %>">
                                                </div>
                                                <div class="col-auto d-flex gap-2">
                                                    <button class="btn btn-primary h-100" type="submit">
                                                        <i class="bi bi-search"></i>
                                                    </button>
                                                    <% if (!searchCode.isEmpty() || !searchName.isEmpty() || !searchPhone.isEmpty() || !searchCccd.isEmpty()) { %>
                                                    <a href="${pageContext.request.contextPath}/patients" class="btn btn-outline-secondary h-100">
                                                        <i class="bi bi-x-circle me-2"></i>Xóa bộ lọc
                                                    </a>
                                                    <% } %>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                    <div class="col-md-3">
                                        <button type="button" class="btn btn-success w-100" data-bs-toggle="modal" data-bs-target="#addPatientModal">
                                            <i class="bi bi-plus-circle me-2"></i>Thêm bệnh nhân
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Patients Table -->
                <div class="row">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="card-title mb-0">
                                    <i class="bi bi-table me-2"></i>Danh sách bệnh nhân
                                    <% if (!searchCode.isEmpty() || !searchName.isEmpty() || !searchPhone.isEmpty() || !searchCccd.isEmpty()) { %>
                                    <span class="badge bg-primary ms-2">Kết quả tìm kiếm</span>
                                    <% } %>
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table id="patientsTable" class="table table-striped table-hover">
                                        <thead class="table-primary">
                                            <tr>
                                                <th>Mã BN</th>
                                                <th>Họ tên</th>
                                                <th>Giới tính</th>
                                                <th>Ngày sinh</th>
                                                <th>Điện thoại</th>
                                                <th>CCCD</th>
                                                <th>Địa chỉ</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <% if (patients != null && !patients.isEmpty()) {
                                                for (Patient patient : patients) { %>
                                                <tr>
                                                    <td><%= patient.getPatientCode() != null ? patient.getPatientCode() : "" %></td>
                                                    <td><%= patient.getFullName() %></td>
                                                    <td>
                                                        <% if (patient.getGender() == 0) { %>
                                                            <span class="badge bg-info">Nam</span>
                                                        <% } else if (patient.getGender() == 1) { %>
                                                            <span class="badge bg-warning">Nữ</span>
                                                        <% } else { %>
                                                            <span class="badge bg-secondary">Khác</span>
                                                        <% } %>
                                                    </td>
                                                    <td><%= patient.getDob() != null ? sdf.format(patient.getDob()) : "" %></td>
                                                    <td><%= patient.getPhone() %></td>
                                                    <td><%= patient.getCccd() %></td>
                                                    <td><%= patient.getAddress() %></td>
                                                    <td>
                                                        <div class="action-buttons-group">
                                                            <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%=patient.getId()%>" 
                                                               class="action-btn action-btn-view" 
                                                               title="Xem hồ sơ bệnh án">
                                                                <i class="bi bi-file-medical"></i>
                                                                <span class="btn-text">Hồ sơ</span>
                                                            </a>
                                                            
                                                            <button type="button" 
                                                                    class="action-btn action-btn-edit" 
                                                                    onclick="editPatient(<%=patient.getId()%>)" 
                                                                    title="Chỉnh sửa thông tin">
                                                                <i class="bi bi-pencil-square"></i>
                                                                <span class="btn-text">Sửa</span>
                                                            </button>
                                                            
                                                            <button type="button" 
                                                                    class="action-btn action-btn-delete" 
                                                                    onclick="deletePatient(<%=patient.getId()%>, '<%=patient.getFullName()%>')" 
                                                                    title="Xóa bệnh nhân">
                                                                <i class="bi bi-trash3"></i>
                                                                <span class="btn-text">Xóa</span>
                                                            </button>
                                                        </div>
                                                    </td>
                                                </tr>
                                                <% } %>
                                            <% } else { %>
                                                <tr>
                                                    <td colspan="8" class="text-center">
                                                        <i class="bi bi-inbox me-2"></i>
                                                        <% if (!searchCode.isEmpty() || !searchName.isEmpty() || !searchPhone.isEmpty() || !searchCccd.isEmpty()) { %>
                                                            Không tìm thấy bệnh nhân nào
                                                        <% } else { %>
                                                            Chưa có bệnh nhân nào trong hệ thống
                                                        <% } %>
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

        <!-- Add Patient Modal -->
        <div class="modal fade" id="addPatientModal" tabindex="-1" aria-labelledby="addPatientModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addPatientModalLabel">
                            <i class="bi bi-plus-circle me-2"></i>Thêm bệnh nhân mới
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="POST" action="${pageContext.request.contextPath}/patients">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="add">
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="addFullName" class="form-label">Họ tên <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="addFullName" name="fullName" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="addGender" class="form-label">Giới tính <span class="text-danger">*</span></label>
                                        <select class="form-select" id="addGender" name="gender" required>
                                            <option value="">Chọn giới tính</option>
                                            <option value="0">Nam</option>
                                            <option value="1">Nữ</option>
                                            <option value="2">Khác</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="addDob" class="form-label">Ngày sinh <span class="text-danger">*</span></label>
                                        <input type="date" class="form-control" id="addDob" name="dob" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="addPhone" class="form-label">Điện thoại <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="addPhone" name="phone" pattern="[0-9]{10,11}" required>
                                        <div class="form-text">10-11 chữ số</div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="addCccd" class="form-label">CCCD <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addCccd" name="cccd" pattern="[0-9]{12}" required>
                                <div class="form-text">12 chữ số</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="addAddress" class="form-label">Địa chỉ <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="addAddress" name="address" rows="3" required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-circle me-2"></i>Thêm bệnh nhân
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Edit Patient Modal -->
        <div class="modal fade" id="editPatientModal" tabindex="-1" aria-labelledby="editPatientModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editPatientModalLabel">
                            <i class="bi bi-pencil me-2"></i>Chỉnh sửa thông tin bệnh nhân
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="POST" action="${pageContext.request.contextPath}/patients">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" id="editPatientId" name="patientId">
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editFullName" class="form-label">Họ tên <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="editFullName" name="fullName" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editGender" class="form-label">Giới tính <span class="text-danger">*</span></label>
                                        <select class="form-select" id="editGender" name="gender" required>
                                            <option value="">Chọn giới tính</option>
                                            <option value="0">Nam</option>
                                            <option value="1">Nữ</option>
                                            <option value="2">Khác</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editDob" class="form-label">Ngày sinh <span class="text-danger">*</span></label>
                                        <input type="date" class="form-control" id="editDob" name="dob" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="editPhone" class="form-label">Điện thoại <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="editPhone" name="phone" pattern="[0-9]{10,11}" required>
                                        <div class="form-text">10-11 chữ số</div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editCccd" class="form-label">CCCD <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="editCccd" name="cccd" pattern="[0-9]{12}" required>
                                <div class="form-text">12 chữ số</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editAddress" class="form-label">Địa chỉ <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="editAddress" name="address" rows="3" required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check-circle me-2"></i>Cập nhật
                            </button>
                        </div>
                    </form>
                </div>
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
                        <p>Bạn có chắc chắn muốn xóa bệnh nhân <strong id="deletePatientName"></strong> không?</p>
                        <p class="text-danger">
                            <i class="bi bi-exclamation-triangle me-2"></i>
                            Hành động này không thể hoàn tác!
                        </p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <form method="POST" action="${pageContext.request.contextPath}/patients" style="display: inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" id="deletePatientId" name="patientId">
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
        <!-- jQuery -->
        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
        <!-- DataTables JS -->
        <script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
        <script src="https://cdn.datatables.net/1.13.7/js/dataTables.bootstrap5.min.js"></script>
        
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

                // Initialize DataTable
                $('#patientsTable').DataTable({
                    "searching": false,
                    "language": {
                        "lengthMenu": "Hiển thị _MENU_ mục",
                        "zeroRecords": "",
                        "info": "Hiển thị _START_ đến _END_ của _TOTAL_ mục",
                        "infoEmpty": "Hiển thị 0 đến 0 của 0 mục",
                        "infoFiltered": "(lọc từ _MAX_ tổng số mục)",
                        "paginate": {
                            "first": "Đầu",
                            "last": "Cuối",
                            "next": "Tiếp",
                            "previous": "Trước"
                        }
                    }
                });

                // Initialize tooltips
                var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
                var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
                    return new bootstrap.Tooltip(tooltipTriggerEl);
                });
            });

            // Edit patient function
            function editPatient(patientId) {
                var contextPath = '<%= request.getContextPath() %>';
                fetch(contextPath + '/patients?action=get&id=' + patientId)
                    .then(response => response.json())
                    .then(data => {
                        document.getElementById('editPatientId').value = data.id;
                        document.getElementById('editFullName').value = data.fullName;
                        document.getElementById('editGender').value = data.gender;
                        document.getElementById('editDob').value = data.dob;
                        document.getElementById('editPhone').value = data.phone;
                        document.getElementById('editCccd').value = data.cccd;
                        document.getElementById('editAddress').value = data.address;
                        
                        new bootstrap.Modal(document.getElementById('editPatientModal')).show();
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('Không thể tải thông tin bệnh nhân!');
                    });
            }

            // Delete patient function
            function deletePatient(patientId, patientName) {
                document.getElementById('deletePatientId').value = patientId;
                document.getElementById('deletePatientName').textContent = patientName;
                new bootstrap.Modal(document.getElementById('deleteConfirmModal')).show();
            }
        </script>
    </body>
</html> 