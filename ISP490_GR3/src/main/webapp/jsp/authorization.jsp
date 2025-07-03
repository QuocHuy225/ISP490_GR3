<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Phân quyền - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Homepage specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <!-- Authorization specific styles -->
        <style>
            /* Authorization specific styles only */
            .auth-container {
                background: white;
                border-radius: 15px;
                box-shadow: 0 5px 25px rgba(3, 96, 217, 0.1);
                overflow: hidden;
                margin-bottom: 30px;
            }

            .auth-header {
                background: linear-gradient(135deg, #0360D9 0%, #00d4ff 100%);
                color: white;
                padding: 25px 30px;
                font-size: 1.3rem;
                font-weight: 600;
                display: flex;
                align-items: center;
                justify-content: space-between;
                gap: 10px;
            }

            .users-table {
                margin: 0;
                border: none;
            }

            .users-table th {
                background: #f8f9fa;
                border: none;
                padding: 20px 15px;
                font-weight: 600;
                color: #2B3674;
                text-transform: uppercase;
                font-size: 0.85rem;
                letter-spacing: 0.5px;
                position: sticky;
                top: 0;
                z-index: 10;
            }

            .users-table td {
                padding: 20px 15px;
                border-bottom: 1px solid #eee;
                vertical-align: middle;
            }

            .users-table tr:hover {
                background-color: #f8f9fa;
            }

            .role-badge {
                padding: 8px 16px;
                border-radius: 20px;
                font-size: 0.85rem;
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .role-badge.admin {
                background: #dc354520;
                color: #dc3545;
                border: 1px solid #dc354540;
            }

            .role-badge.doctor {
                background: #28a74520;
                color: #28a745;
                border: 1px solid #28a74540;
            }

            .role-badge.receptionist {
                background: #ffc10720;
                color: #856404;
                border: 1px solid #ffc10740;
            }

            .role-badge.patient {
                background: #17a2b820;
                color: #17a2b8;
                border: 1px solid #17a2b840;
            }

            .user-avatar {
                width: 50px;
                height: 50px;
                border-radius: 50%;
                background: linear-gradient(135deg, #0360D9 0%, #00d4ff 100%);
                display: flex;
                align-items: center;
                justify-content: center;
                color: white;
                font-weight: 700;
                font-size: 1.2rem;
                margin-right: 15px;
            }

            .user-info {
                display: flex;
                align-items: center;
            }

            .user-details {
                flex: 1;
            }

            .user-email {
                color: #A3AED0;
                font-size: 0.9rem;
            }

            .update-btn {
                background: #0360D9;
                border: none;
                color: white;
                padding: 8px 16px;
                border-radius: 8px;
                font-size: 0.85rem;
                font-weight: 600;
                transition: all 0.3s ease;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .update-btn:hover {
                background: #0246a3;
                transform: translateY(-2px);
                box-shadow: 0 5px 25px rgba(3, 96, 217, 0.1);
                color: white;
            }

            .update-btn:disabled {
                background: #ccc;
                cursor: not-allowed;
                transform: none;
                box-shadow: none;
            }

            .table-responsive {
                max-height: 600px;
                overflow-y: auto;
            }

            /* Filter section styles */
            .filter-badge {
                transition: all 0.3s ease;
            }

            .filter-badge:hover {
                transform: scale(1.05);
            }

            .btn-close-white {
                filter: brightness(0) invert(1);
                font-size: 0.7em !important;
            }

            /* Loading state */
            .loading {
                opacity: 0.6;
                pointer-events: none;
            }

            /* Statistics Cards Styles */
            .stats-card {
                background: white;
                border-radius: 15px;
                padding: 20px;
                display: flex;
                align-items: center;
                gap: 15px;
                box-shadow: 0 3px 15px rgba(0, 0, 0, 0.08);
                transition: all 0.3s ease;
                border-left: 4px solid transparent;
                height: 100px;
            }

            .stats-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
            }

            .stats-card.total {
                border-left-color: #6c757d;
                background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            }

            .stats-card.admin {
                border-left-color: #dc3545;
                background: linear-gradient(135deg, #fff5f5 0%, #fed7d7 100%);
            }

            .stats-card.doctor {
                border-left-color: #28a745;
                background: linear-gradient(135deg, #f0fff4 0%, #c6f6d5 100%);
            }

            .stats-card.receptionist {
                border-left-color: #ffc107;
                background: linear-gradient(135deg, #fffbf0 0%, #fef5e7 100%);
            }

            .stats-card.patient {
                border-left-color: #17a2b8;
                background: linear-gradient(135deg, #f0fdff 0%, #c4f1f9 100%);
            }

            .stats-icon {
                width: 60px;
                height: 60px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 1.8rem;
                flex-shrink: 0;
            }

            .stats-card.total .stats-icon {
                background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
                color: white;
            }

            .stats-card.admin .stats-icon {
                background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
                color: white;
            }

            .stats-card.doctor .stats-icon {
                background: linear-gradient(135deg, #28a745 0%, #218838 100%);
                color: white;
            }

            .stats-card.receptionist .stats-icon {
                background: linear-gradient(135deg, #ffc107 0%, #e0a800 100%);
                color: white;
            }

            .stats-card.patient .stats-icon {
                background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
                color: white;
            }

            .stats-content {
                flex: 1;
            }

            .stats-number {
                font-size: 2rem;
                font-weight: 700;
                color: #2B3674;
                line-height: 1;
                margin-bottom: 5px;
            }

            .stats-label {
                font-size: 0.9rem;
                color: #A3AED0;
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            /* Responsive adjustments */
            @media (max-width: 768px) {
                .users-table th,
                .users-table td {
                    padding: 15px 10px;
                    font-size: 0.9rem;
                }

                .user-avatar {
                    width: 40px;
                    height: 40px;
                    font-size: 1rem;
                }

                .stats-card {
                    padding: 15px;
                    height: 80px;
                    gap: 10px;
                }

                .stats-icon {
                    width: 45px;
                    height: 45px;
                    font-size: 1.4rem;
                }

                .stats-number {
                    font-size: 1.5rem;
                }

                .stats-label {
                    font-size: 0.8rem;
                }

                .col-md-3, .col-md-4, .col-md-2 {
                    margin-bottom: 1rem;
                }
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
        User currentUser = null;
        if (userObj instanceof User) {
            currentUser = (User) userObj;
            userName = currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getEmail();
            userRoleDisplay = currentUser.getRole() != null ? currentUser.getRole().getValue() : "Patient";
        }
        
        // Check if user is admin
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp");
            return;
        }
        
        // Get attributes from controller
        List<User> allUsers = (List<User>) request.getAttribute("allUsers");
        List<User> deletedUsers = (List<User>) request.getAttribute("deletedUsers");
        Boolean showDeleted = (Boolean) request.getAttribute("showDeleted");
        
        String successMessage = (String) request.getAttribute("successMessage");
        String errorMessage = (String) request.getAttribute("errorMessage");
        
        // Get filter parameters
        String roleFilter = (String) request.getAttribute("roleFilter");
        String sortOrder = (String) request.getAttribute("sortOrder");
        String emailSearch = (String) request.getAttribute("emailSearch");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        boolean isShowingDeleted = showDeleted != null && showDeleted;
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
                <li class="active">
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
                <li>
                    <a href="${pageContext.request.contextPath}/admin/prescriptions">
                        <i class="bi bi-capsule"></i> Quản lý thuốc
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/medical-exam-templates">
                        <i class="bi bi-file-text"></i> Mẫu khám bệnh
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

            <!-- Main Content Area - Authorization Management -->
            <div class="container-fluid mt-4">
                <!-- Success/Error Messages -->
                <% if (successMessage != null) { %>
                <div class="alert alert-success">
                    <i class="bi bi-check-circle me-2"></i>
                    <%= successMessage %>
                </div>
                <% } %>

                <% if (errorMessage != null) { %>
                <div class="alert alert-danger">
                    <i class="bi bi-exclamation-circle me-2"></i>
                    <%= errorMessage %>
                </div>
                <% } %>

                <!-- Quản lý Phân quyền và Bộ lọc -->
                <div class="auth-container mb-4">
                    <div class="auth-header">
                        <div>
                            <i class="bi bi-shield-check me-2"></i>
                            <% if (isShowingDeleted) { %>
                            Tài khoản đã xóa
                            <% } else { %>
                            Quản lý Phân quyền
                            <% } %>
                        </div>
                        <div class="d-flex gap-2 flex-wrap">
                            <button type="button" class="btn btn-light btn-sm" onclick="clearFilters()">
                                <i class="bi bi-x-circle"></i>
                                Xóa bộ lọc
                            </button>
                            <% if (isShowingDeleted) { %>
                            <a href="${pageContext.request.contextPath}/admin/authorization" class="btn btn-light btn-sm">
                                <i class="bi bi-people"></i>
                                Xem tài khoản hoạt động
                            </a>
                            <% } else { %>
                            <a href="${pageContext.request.contextPath}/admin/authorization?showDeleted=true" class="btn btn-warning btn-sm">
                                <i class="bi bi-trash"></i>
                                Xem tài khoản đã xóa
                            </a>
                            <% } %>
                        </div>
                    </div>

                    <div class="p-4">
                        <p class="mb-4 text-muted">
                            <% if (isShowingDeleted) { %>
                            Danh sách các tài khoản đã bị xóa
                            <% } else { %>
                            Quản lý và phân quyền cho tất cả người dùng trong hệ thống
                            <% } %>
                        </p>

                        <form method="get" action="${pageContext.request.contextPath}/admin/authorization" id="filterForm">
                            <% if (isShowingDeleted) { %>
                            <input type="hidden" name="showDeleted" value="true">
                            <% } %>

                            <div class="row g-3 align-items-end">
                                <!-- Role Filter -->
                                <div class="col-md">
                                    <label for="roleFilter" class="form-label fw-semibold">
                                        <i class="bi bi-person-badge me-1"></i>Lọc theo quyền
                                    </label>
                                    <select name="roleFilter" id="roleFilter" class="form-select">
                                        <option value="all" <%= "all".equals(roleFilter) || roleFilter == null ? "selected" : "" %>>
                                            Tất cả quyền
                                        </option>
                                        <option value="Admin" <%= "Admin".equals(roleFilter) ? "selected" : "" %>>
                                            Quản trị viên
                                        </option>
                                        <option value="Doctor" <%= "Doctor".equals(roleFilter) ? "selected" : "" %>>
                                            Bác sĩ
                                        </option>
                                        <option value="Receptionist" <%= "Receptionist".equals(roleFilter) ? "selected" : "" %>>
                                            Lễ tân
                                        </option>
                                        <option value="Patient" <%= "Patient".equals(roleFilter) ? "selected" : "" %>>
                                            Bệnh nhân
                                        </option>
                                    </select>
                                </div>

                                <!-- Sort Order -->
                                <div class="col-md">
                                    <label for="sortOrder" class="form-label fw-semibold">
                                        <i class="bi bi-sort-down me-1"></i>Sắp xếp theo thời gian
                                    </label>
                                    <select name="sortOrder" id="sortOrder" class="form-select">
                                        <option value="newest" <%= "newest".equals(sortOrder) || sortOrder == null ? "selected" : "" %>>
                                            Mới nhất trước
                                        </option>
                                        <option value="oldest" <%= "oldest".equals(sortOrder) ? "selected" : "" %>>
                                            Cũ nhất trước
                                        </option>
                                    </select>
                                </div>

                                <!-- Email Search -->
                                <div class="col-md">
                                    <label for="emailSearch" class="form-label fw-semibold">
                                        <i class="bi bi-search me-1"></i>Tìm kiếm
                                    </label>
                                    <input type="text" name="emailSearch" id="emailSearch" class="form-control" 
                                           placeholder="Nhập email hoặc tên người dùng..." 
                                           value="<%= emailSearch != null ? emailSearch : "" %>">
                                </div>

                                <!-- Filter Button - Ẩn vì đã có auto-submit -->
                                <div class="col-md-2" style="display: none;">
                                    <button type="submit" class="btn btn-primary w-100">
                                        <i class="bi bi-search me-1"></i>
                                        Lọc
                                    </button>
                                </div>
                            </div>

                            <!-- Active Filters Display -->
                            <% if (roleFilter != null || sortOrder != null || (emailSearch != null && !emailSearch.trim().isEmpty())) { %>
                            <div class="mt-3">
                                <small class="text-muted">Bộ lọc đang áp dụng:</small>
                                <div class="d-flex flex-wrap gap-2 mt-2">
                                    <% if (roleFilter != null && !"all".equals(roleFilter)) { %>
                                    <span class="badge bg-primary">
                                        Quyền: <%= roleFilter %>
                                        <button type="button" class="btn-close btn-close-white ms-1" style="font-size: 0.7em;" 
                                                onclick="removeFilter('roleFilter')"></button>
                                    </span>
                                    <% } %>
                                    <% if ("oldest".equals(sortOrder)) { %>
                                    <span class="badge bg-info">
                                        Sắp xếp: Cũ nhất trước
                                        <button type="button" class="btn-close btn-close-white ms-1" style="font-size: 0.7em;" 
                                                onclick="removeFilter('sortOrder')"></button>
                                    </span>
                                    <% } %>
                                    <% if (emailSearch != null && !emailSearch.trim().isEmpty()) { %>
                                    <span class="badge bg-success">
                                        Tìm kiếm: "<%= emailSearch %>"
                                        <button type="button" class="btn-close btn-close-white ms-1" style="font-size: 0.7em;" 
                                                onclick="removeFilter('emailSearch')"></button>
                                    </span>
                                    <% } %>
                                </div>
                            </div>
                            <% } %>
                        </form>
                    </div>
                </div>

                <!-- Statistics Container -->
                <% 
                // Tính toán thống kê số lượng theo role (chỉ cho tài khoản hoạt động)
                if (!isShowingDeleted && allUsers != null) {
                    int totalUsers = allUsers.size();
                    int adminCount = 0, doctorCount = 0, receptionistCount = 0, patientCount = 0;
                    
                    for (User user : allUsers) {
                        switch (user.getRole()) {
                            case ADMIN:
                                adminCount++;
                                break;
                            case DOCTOR:
                                doctorCount++;
                                break;
                            case RECEPTIONIST:
                                receptionistCount++;
                                break;
                            case PATIENT:
                                patientCount++;
                                break;
                        }
                    }
                %>
                <div class="auth-container mb-4">
                    <div class="auth-header">
                        <div>
                            <i class="bi bi-bar-chart me-2"></i>
                            Thống kê tài khoản theo quyền
                        </div>
                    </div>

                    <div class="p-4">
                        <div class="row g-3">
                            <!-- Tổng số tài khoản -->
                            <div class="col-md">
                                <div class="stats-card total">
                                    <div class="stats-icon">
                                        <i class="bi bi-people-fill"></i>
                                    </div>
                                    <div class="stats-content">
                                        <div class="stats-number"><%= totalUsers %></div>
                                        <div class="stats-label">Tổng số</div>
                                    </div>
                                </div>
                            </div>

                            <!-- Admin -->
                            <div class="col-md">
                                <div class="stats-card admin">
                                    <div class="stats-icon">
                                        <i class="bi bi-shield-fill-check"></i>
                                    </div>
                                    <div class="stats-content">
                                        <div class="stats-number"><%= adminCount %></div>
                                        <div class="stats-label">Quản trị viên</div>
                                    </div>
                                </div>
                            </div>

                            <!-- Doctor -->
                            <div class="col-md">
                                <div class="stats-card doctor">
                                    <div class="stats-icon">
                                        <i class="bi bi-heart-pulse-fill"></i>
                                    </div>
                                    <div class="stats-content">
                                        <div class="stats-number"><%= doctorCount %></div>
                                        <div class="stats-label">Bác sĩ</div>
                                    </div>
                                </div>
                            </div>

                            <!-- Receptionist -->
                            <div class="col-md">
                                <div class="stats-card receptionist">
                                    <div class="stats-icon">
                                        <i class="bi bi-person-workspace"></i>
                                    </div>
                                    <div class="stats-content">
                                        <div class="stats-number"><%= receptionistCount %></div>
                                        <div class="stats-label">Lễ tân</div>
                                    </div>
                                </div>
                            </div>

                            <!-- Patient -->
                            <div class="col-md">
                                <div class="stats-card patient">
                                    <div class="stats-icon">
                                        <i class="bi bi-person-fill"></i>
                                    </div>
                                    <div class="stats-content">
                                        <div class="stats-number"><%= patientCount %></div>
                                        <div class="stats-label">Bệnh nhân</div>
                                    </div>
                                </div>
                            </div>


                        </div>
                    </div>
                </div>
                <% } %>

                <!-- Users Table -->
                <div class="auth-container">
                    <div class="auth-header">
                        <div>
                            <i class="bi bi-table me-2"></i>
                            <% if (isShowingDeleted) { %>
                            Danh sách tài khoản đã xóa
                            <% } else { %>
                            Danh sách người dùng và phân quyền
                            <% } %>
                        </div>
                    </div>

                    <% 
                    List<User> usersToShow = isShowingDeleted ? deletedUsers : allUsers;
                    if (usersToShow != null && !usersToShow.isEmpty()) { 
                    %>
                    <div class="table-responsive">
                        <table class="table users-table">
                            <thead class="table-primary">
                                <tr>
                                    <th>Người dùng</th>
                                    <th>Quyền hiện tại</th>
                                    <th><%= isShowingDeleted ? "Ngày xóa" : "Ngày tạo" %></th>
                                        <% if (!isShowingDeleted) { %>
                                    <th>Thay đổi quyền</th>
                                        <% } %>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (User user : usersToShow) { 
                                    String avatarLetter = user.getFullName() != null && !user.getFullName().isEmpty() 
                                        ? user.getFullName().substring(0, 1).toUpperCase() 
                                        : "U";
                                    String roleCssClass = user.getRole().getValue().toLowerCase();
                                    boolean isAdmin = user.getRole() == User.Role.ADMIN;
                                    boolean isSelf = user.getId().equals(currentUser.getId());
                                %>
                                <tr>
                                    <td>
                                        <div class="user-info">
                                            <div class="user-avatar">
                                                <%= avatarLetter %>
                                            </div>
                                            <div class="user-details">
                                                <div class="user-name">
                                                    <%= user.getFullName() != null ? user.getFullName() : "N/A" %>
                                                    <% if (isSelf) { %>
                                                    <span class="badge bg-secondary ms-2">Bạn</span>
                                                    <% } %>
                                                </div>
                                                <div class="user-email"><%= user.getEmail() %></div>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="role-badge <%= roleCssClass %>">
                                            <%= user.getRole().getValue() %>
                                        </span>
                                    </td>
                                    <td>
                                        <% if (isShowingDeleted) { %>
                                        <%= user.getUpdatedAt() != null ? dateFormat.format(user.getUpdatedAt()) : "N/A" %>
                                        <% } else { %>
                                        <%= user.getCreatedAt() != null ? dateFormat.format(user.getCreatedAt()) : "N/A" %>
                                        <% } %>
                                    </td>
                                    <% if (!isShowingDeleted) { %>
                                    <td>
                                        <% if (isAdmin || isSelf) { %>
                                        <select class="form-select role-select" disabled>
                                            <option>Không thể thay đổi</option>
                                        </select>
                                        <% } else { %>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/authorization/update" style="display: inline;">
                                            <input type="hidden" name="userId" value="<%= user.getId() %>">
                                            <select name="newRole" class="form-select role-select" required>
                                                <option value="">-- Chọn quyền --</option>
                                                <option value="doctor" <%= user.getRole() == User.Role.DOCTOR ? "selected" : "" %>>
                                                    Bác sĩ
                                                </option>
                                                <option value="receptionist" <%= user.getRole() == User.Role.RECEPTIONIST ? "selected" : "" %>>
                                                    Lễ tân
                                                </option>
                                                <option value="patient" <%= user.getRole() == User.Role.PATIENT ? "selected" : "" %>>
                                                    Bệnh nhân
                                                </option>
                                            </select>
                                            <% } %>
                                    </td>
                                    <% } %>
                                    <td>
                                        <% if (isShowingDeleted) { %>
                                        <!-- Restore button for deleted users -->
                                        <form method="post" action="${pageContext.request.contextPath}/admin/authorization/restore" style="display: inline;">
                                            <input type="hidden" name="userId" value="<%= user.getId() %>">
                                            <button type="button" class="btn btn-success btn-sm" onclick="showRestoreConfirmation('<%= user.getFullName() != null ? user.getFullName() : user.getEmail() %>', '<%= user.getId() %>')">
                                                <i class="bi bi-arrow-clockwise"></i>
                                                Khôi phục
                                            </button>
                                        </form>
                                        <% } else { %>
                                        <!-- Update and Delete buttons for active users -->
                                        <% if (isAdmin || isSelf) { %>
                                        <button class="btn update-btn" disabled>
                                            <i class="bi bi-lock"></i>
                                            Không thể cập nhật
                                        </button>
                                        <% } else { %>
                                        <button type="submit" class="btn update-btn">
                                            <i class="bi bi-arrow-repeat"></i>
                                            Cập nhật
                                        </button>
                                        </form>
                                        <!-- Delete button -->
                                        <form method="post" action="${pageContext.request.contextPath}/admin/authorization/delete" style="display: inline; margin-left: 5px;">
                                            <input type="hidden" name="userId" value="<%= user.getId() %>">
                                            <button type="button" class="btn btn-sm btn-outline-danger" onclick="showDeleteConfirmation('<%= user.getFullName() != null ? user.getFullName() : user.getEmail() %>', '<%= user.getId() %>')" title="Xóa người dùng">
                                                <i class="bi bi-trash3"></i>
                                            </button>
                                        </form>
                                        <% } %>
                                        <% } %>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    <% } else { %>
                    <div class="p-5 text-center">
                        <i class="bi bi-people" style="font-size: 4rem; color: #A3AED0;"></i>
                        <h4 class="mt-3">Không có người dùng nào</h4>
                        <p class="text-muted">Danh sách người dùng trống hoặc có lỗi khi tải dữ liệu.</p>
                    </div>
                    <% } %>
                </div>
            </div>
        </div>

        <!-- Role Change Confirmation Modal -->
        <div class="modal fade" id="roleChangeModal" tabindex="-1" aria-labelledby="roleChangeModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="roleChangeModalLabel">
                            <i class="bi bi-shield-exclamation me-2 text-warning"></i>
                            Xác nhận thay đổi quyền
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="d-flex align-items-start">
                            <div class="flex-shrink-0">
                                <div class="bg-warning bg-opacity-10 rounded-circle p-3">
                                    <i class="bi bi-question-circle-fill text-warning" style="font-size: 2rem;"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-2">Bạn có chắc chắn muốn thực hiện thay đổi này?</h6>
                                <p class="mb-3" id="roleChangeMessage">
                                    <!-- Message will be populated by JavaScript -->
                                </p>
                                <div class="alert alert-info d-flex align-items-center" role="alert">
                                    <i class="bi bi-info-circle me-2"></i>
                                    <div>
                                        <strong>Lưu ý:</strong> Thay đổi quyền sẽ có hiệu lực ngay lập tức và có thể ảnh hưởng đến quyền truy cập của người dùng.
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle me-1"></i>
                            Hủy bỏ
                        </button>
                        <button type="button" class="btn btn-warning" id="confirmRoleChange">
                            <i class="bi bi-check-circle me-1"></i>
                            Xác nhận thay đổi
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Delete Account Confirmation Modal -->
        <div class="modal fade" id="deleteAccountModal" tabindex="-1" aria-labelledby="deleteAccountModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="deleteAccountModalLabel">
                            <i class="bi bi-exclamation-triangle me-2 text-danger"></i>
                            Xác nhận xóa tài khoản
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="d-flex align-items-start">
                            <div class="flex-shrink-0">
                                <div class="bg-danger bg-opacity-10 rounded-circle p-3">
                                    <i class="bi bi-trash3-fill text-danger" style="font-size: 2rem;"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-2">Bạn có chắc chắn muốn xóa tài khoản này?</h6>
                                <p class="mb-3" id="deleteAccountMessage">
                                    <!-- Message will be populated by JavaScript -->
                                </p>
                                <div class="alert alert-danger d-flex align-items-center" role="alert">
                                    <i class="bi bi-exclamation-triangle me-2"></i>
                                    <div>
                                        <strong>Cảnh báo:</strong> Tài khoản sẽ bị vô hiệu hóa và không thể đăng nhập. Tài khoản có thể được khôi phục lại sau này.
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle me-1"></i>
                            Hủy bỏ
                        </button>
                        <button type="button" class="btn btn-danger" id="confirmDeleteAccount">
                            <i class="bi bi-trash me-1"></i>
                            Xóa tài khoản
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Restore Account Confirmation Modal -->
        <div class="modal fade" id="restoreAccountModal" tabindex="-1" aria-labelledby="restoreAccountModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="restoreAccountModalLabel">
                            <i class="bi bi-arrow-clockwise me-2 text-success"></i>
                            Xác nhận khôi phục tài khoản
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="d-flex align-items-start">
                            <div class="flex-shrink-0">
                                <div class="bg-success bg-opacity-10 rounded-circle p-3">
                                    <i class="bi bi-arrow-clockwise text-success" style="font-size: 2rem;"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-2">Bạn có chắc chắn muốn khôi phục tài khoản này?</h6>
                                <p class="mb-3" id="restoreAccountMessage">
                                    <!-- Message will be populated by JavaScript -->
                                </p>
                                <div class="alert alert-success d-flex align-items-center" role="alert">
                                    <i class="bi bi-check-circle me-2"></i>
                                    <div>
                                        <strong>Thông tin:</strong> Tài khoản sẽ được kích hoạt lại và có thể đăng nhập bình thường.
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle me-1"></i>
                            Hủy bỏ
                        </button>
                        <button type="button" class="btn btn-success" id="confirmRestoreAccount">
                            <i class="bi bi-arrow-clockwise me-1"></i>
                            Khôi phục tài khoản
                        </button>
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

                                                    // Enhanced dropdown animations
                                                    const dropdownToggle = document.getElementById('userDropdown');
                                                    const dropdownMenu = dropdownToggle.nextElementSibling;

                                                    dropdownToggle.addEventListener('show.bs.dropdown', function () {
                                                        dropdownMenu.style.opacity = '0';
                                                        dropdownMenu.style.transform = 'translateY(-10px)';
                                                        setTimeout(() => {
                                                            dropdownMenu.style.transition = 'all 0.3s ease';
                                                            dropdownMenu.style.opacity = '1';
                                                            dropdownMenu.style.transform = 'translateY(0)';
                                                        }, 10);
                                                    });

                                                    // Add confirmation dialog for role updates
                                                    const updateForms = document.querySelectorAll('form[action*="authorization/update"]');
                                                    updateForms.forEach(form => {
                                                        form.addEventListener('submit', function (e) {
                                                            const userId = form.querySelector('input[name="userId"]').value;
                                                            const newRole = form.querySelector('select[name="newRole"]').value;
                                                            const userName = form.closest('tr').querySelector('.user-name').textContent.trim();

                                                            if (!newRole) {
                                                                e.preventDefault();
                                                                alert('Vui lòng chọn quyền hạn mới.');
                                                                return;
                                                            }

                                                            const roleNames = {
                                                                'doctor': 'Bác sĩ',
                                                                'receptionist': 'Lễ tân',
                                                                'patient': 'Bệnh nhân'
                                                            };

                                                            const roleName = roleNames[newRole] || 'quyền mới';

                                                            // Prevent default submission
                                                            e.preventDefault();
                                                            
                                                            // Show custom confirmation modal
                                                            showRoleChangeConfirmation(userName, roleName, form);
                                                        });
                                                    });

                                                    // Role change confirmation modal function
                                                    window.showRoleChangeConfirmation = function(userName, roleName, form) {
                                                        const modalMessage = document.getElementById('roleChangeMessage');
                                                        modalMessage.innerHTML = `
                                                            Quyền của <strong class="text-primary">${userName}</strong> sẽ được thay đổi thành <strong class="text-warning">${roleName}</strong>.
                                                        `;
                                                        
                                                        const modal = new bootstrap.Modal(document.getElementById('roleChangeModal'));
                                                        modal.show();
                                                        
                                                        // Handle confirm button click
                                                        const confirmBtn = document.getElementById('confirmRoleChange');
                                                        confirmBtn.onclick = function() {
                                                            modal.hide();
                                                            form.submit();
                                                        };
                                                    };

                                                    // Delete account confirmation modal function
                                                    window.showDeleteConfirmation = function(userName, userId) {
                                                        const modalMessage = document.getElementById('deleteAccountMessage');
                                                        modalMessage.innerHTML = `
                                                            Tài khoản của <strong class="text-danger">${userName}</strong> sẽ bị xóa khỏi hệ thống.
                                                        `;
                                                        
                                                        const modal = new bootstrap.Modal(document.getElementById('deleteAccountModal'));
                                                        modal.show();
                                                        
                                                        // Handle confirm button click
                                                        const confirmBtn = document.getElementById('confirmDeleteAccount');
                                                        confirmBtn.onclick = function() {
                                                            modal.hide();
                                                            // Create and submit form
                                                            const form = document.createElement('form');
                                                            form.method = 'POST';
                                                            form.action = '${pageContext.request.contextPath}/admin/authorization/delete';
                                                            form.style.display = 'none';
                                                            
                                                            const userIdInput = document.createElement('input');
                                                            userIdInput.type = 'hidden';
                                                            userIdInput.name = 'userId';
                                                            userIdInput.value = userId;
                                                            
                                                            form.appendChild(userIdInput);
                                                            document.body.appendChild(form);
                                                            form.submit();
                                                        };
                                                    };

                                                    // Restore account confirmation modal function
                                                    window.showRestoreConfirmation = function(userName, userId) {
                                                        const modalMessage = document.getElementById('restoreAccountMessage');
                                                        modalMessage.innerHTML = `
                                                            Tài khoản của <strong class="text-success">${userName}</strong> sẽ được khôi phục và kích hoạt lại.
                                                        `;
                                                        
                                                        const modal = new bootstrap.Modal(document.getElementById('restoreAccountModal'));
                                                        modal.show();
                                                        
                                                        // Handle confirm button click
                                                        const confirmBtn = document.getElementById('confirmRestoreAccount');
                                                        confirmBtn.onclick = function() {
                                                            modal.hide();
                                                            // Create and submit form
                                                            const form = document.createElement('form');
                                                            form.method = 'POST';
                                                            form.action = '${pageContext.request.contextPath}/admin/authorization/restore';
                                                            form.style.display = 'none';
                                                            
                                                            const userIdInput = document.createElement('input');
                                                            userIdInput.type = 'hidden';
                                                            userIdInput.name = 'userId';
                                                            userIdInput.value = userId;
                                                            
                                                            form.appendChild(userIdInput);
                                                            document.body.appendChild(form);
                                                            form.submit();
                                                        };
                                                    };

                                                    // Filter and search functionality - Expose to global scope
                                                    window.clearFilters = function () {
                                                        const form = document.getElementById('filterForm');

                                                        // Reset all form fields
                                                        document.getElementById('roleFilter').value = 'all';
                                                        document.getElementById('sortOrder').value = 'newest';
                                                        document.getElementById('emailSearch').value = '';

                                                        // Submit form to clear filters
                                                        form.submit();
                                                    }

                                                    window.removeFilter = function (filterName) {
                                                        const form = document.getElementById('filterForm');

                                                        if (filterName === 'roleFilter') {
                                                            document.getElementById('roleFilter').value = 'all';
                                                        } else if (filterName === 'sortOrder') {
                                                            document.getElementById('sortOrder').value = 'newest';
                                                        } else if (filterName === 'emailSearch') {
                                                            document.getElementById('emailSearch').value = '';
                                                        }

                                                        form.submit();
                                                    }

                                                    // Auto-submit form when filter dropdowns change
                                                    document.getElementById('roleFilter').addEventListener('change', function () {
                                                        document.getElementById('filterForm').submit();
                                                    });

                                                    document.getElementById('sortOrder').addEventListener('change', function () {
                                                        document.getElementById('filterForm').submit();
                                                    });

                                                    // Submit search when Enter is pressed in search box
                                                    document.getElementById('emailSearch').addEventListener('keypress', function (e) {
                                                        if (e.key === 'Enter') {
                                                            e.preventDefault();
                                                            document.getElementById('filterForm').submit();
                                                        }
                                                    });

                                                    // Real-time search with debounce
                                                    let searchTimeout;
                                                    document.getElementById('emailSearch').addEventListener('input', function () {
                                                        clearTimeout(searchTimeout);
                                                        searchTimeout = setTimeout(() => {
                                                            // Auto-submit after 1 second of no typing
                                                            document.getElementById('filterForm').submit();
                                                        }, 1000);
                                                    });
                                                });
        </script>
    </body>
</html> 