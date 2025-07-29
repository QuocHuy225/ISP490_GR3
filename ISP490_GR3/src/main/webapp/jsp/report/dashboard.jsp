<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Dashboard - Ánh Dương Clinic</title>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <style>
            .stat-card {
                border-radius: 10px;
                padding: 20px;
                margin-bottom: 20px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                transition: transform 0.3s;
            }
            .stat-card:hover {
                transform: translateY(-5px);
            }
            .stat-icon {
                font-size: 2.5rem;
                margin-bottom: 15px;
            }
            .stat-title {
                font-size: 1.1rem;
                color: #6c757d;
                margin-bottom: 10px;
            }
            .stat-value {
                font-size: 1.8rem;
                font-weight: bold;
                margin-bottom: 0;
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
        %>

        <nav id="sidebar">
            <div class="sidebar-header">
                <h3>MENU</h3>
            </div>
            <ul class="list-unstyled components">
                <% if (currentRole == User.Role.ADMIN) { %>
                <li><a href="${pageContext.request.contextPath}/homepage"><i class="bi bi-speedometer2"></i> Trang chủ</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/authorization"><i class="bi bi-people-fill"></i> Quản lý người dùng</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/medical-exam-templates"><i class="bi bi-file-text"></i> Mẫu khám bệnh</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/services"><i class="bi bi-file-medical"></i> Quản lý dịch vụ</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/partners"><i class="bi bi-building"></i> Quản lý đối tác</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/medicines"><i class="bi bi-hospital"></i> Quản lý kho thuốc</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/prescriptions"><i class="bi bi-capsule"></i> Quản lý thuốc</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/medical-supplies"><i class="bi bi-gear-fill"></i> Quản lý vật tư</a></li>
                <li class="active"><a href="${pageContext.request.contextPath}/admin/report"><i class="bi bi-bar-chart-fill"></i> Báo cáo thống kê</a></li>
                <% } %>
            </ul>
        </nav>

        <div id="content">
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

            <div class="container-fluid mt-4">
                <h2 class="mb-4">Tổng Quan Thống Kê</h2>

                <div class="row">
                    <div class="col-md-4">
                        <div class="stat-card bg-primary bg-opacity-10">
                            <i class="bi bi-people stat-icon text-primary"></i>
                            <div class="stat-title">Tổng số bệnh nhân</div>
                            <div class="stat-value text-primary">${summaryData.totalPatients}</div>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="stat-card bg-success bg-opacity-10">
                            <i class="bi bi-person-workspace stat-icon text-success"></i>
                            <div class="stat-title">Tổng số bác sĩ</div>
                            <div class="stat-value text-success">${summaryData.totalDoctors}</div>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="stat-card bg-info bg-opacity-10">
                            <i class="bi bi-calendar-check stat-icon text-info"></i>
                            <div class="stat-title">Tổng số cuộc hẹn</div>
                            <div class="stat-value text-info">${summaryData.totalAppointments}</div>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="stat-card bg-warning bg-opacity-10">
                            <i class="bi bi-file-medical stat-icon text-warning"></i>
                            <div class="stat-title">Tổng số hồ sơ bệnh án</div>
                            <div class="stat-value text-warning">${summaryData.totalMedicalRecords}</div>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="stat-card bg-danger bg-opacity-10">
                            <i class="bi bi-receipt stat-icon text-danger"></i>
                            <div class="stat-title">Tổng số hóa đơn</div>
                            <div class="stat-value text-danger">${summaryData.totalInvoices}</div>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="stat-card bg-secondary bg-opacity-10">
                            <i class="bi bi-currency-dollar stat-icon text-secondary"></i>
                            <div class="stat-title">Tổng doanh thu</div>
                            <div class="stat-value text-secondary">${String.format("%,.0f", summaryData.totalRevenue)} VNĐ</div>
                        </div>
                    </div>
                </div>

                <div class="row mt-4">
                    <div class="col-md-4">
                        <a href="${pageContext.request.contextPath}/admin/report/patient" class="btn btn-outline-primary w-100 mb-3">
                            <i class="bi bi-people me-2"></i>Báo cáo chi tiết bệnh nhân
                        </a>
                    </div>
                    <div class="col-md-4">
                        <a href="${pageContext.request.contextPath}/admin/report/medical-record" class="btn btn-outline-warning w-100 mb-3">
                            <i class="bi bi-file-medical me-2"></i>Báo cáo hồ sơ bệnh án
                        </a>
                    </div>
                    <div class="col-md-4">
                        <a href="${pageContext.request.contextPath}/admin/report/invoice" class="btn btn-outline-danger w-100 mb-3">
                            <i class="bi bi-receipt me-2"></i>Báo cáo hóa đơn
                        </a>
                    </div>
                    <div class="col-md-4"> <%-- Đã thay đổi từ col-md-3 sang col-md-4 --%>
                        <a href="${pageContext.request.contextPath}/admin/report/appointment" class="btn btn-outline-info w-100 mb-3">
                            <i class="bi bi-calendar-check me-2"></i>Báo cáo lịch hẹn
                        </a>
                    </div>
                </div>
            </div>
        </div>

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
        </script>
    </body>
</html>