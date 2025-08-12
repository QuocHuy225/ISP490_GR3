<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Báo Cáo Hồ Sơ Bệnh Án - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Homepage specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <style>
            .stat-card {
                border-radius: 10px;
                padding: 20px;
                margin-bottom: 20px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            }
            .chart-container {
                position: relative;
                margin: auto;
                height: 300px;
                width: 100%;
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
                <li class="active">
                    <a href="${pageContext.request.contextPath}/report">
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
                        <i class="bi bi-calendar-check"></i> Quản lý hàng đợi
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/patients">
                        <i class="bi bi-people"></i> Quản lý bệnh nhân
                    </a>
                </li> 
               <li>
                    <a href="${pageContext.request.contextPath}/#">
                        <i class="bi bi-speedometer2"></i> Quản lý lịch bác sĩ
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
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2>Báo Cáo Thống Kê Hồ Sơ Bệnh Án</h2>
                    <a href="${pageContext.request.contextPath}/admin/report" class="btn btn-outline-primary">
                        <i class="bi bi-arrow-left me-2"></i>Quay lại Dashboard
                    </a>
                </div>

                <div class="row">
                    <!-- Tổng số hồ sơ -->
                    <div class="col-md-4">
                        <div class="stat-card bg-primary bg-opacity-10">
                            <h5 class="text-primary">Tổng số hồ sơ</h5>
                            <h2 class="mb-0">${medicalRecordStats.totalRecords}</h2>
                        </div>
                    </div>

                    <!-- Số bệnh nhân duy nhất -->
                    <div class="col-md-4">
                        <div class="stat-card bg-success bg-opacity-10">
                            <h5 class="text-success">Số bệnh nhân duy nhất</h5>
                            <h2 class="mb-0">${medicalRecordStats.uniquePatients}</h2>
                        </div>
                    </div>

                    <!-- Số bác sĩ tham gia -->
                    <div class="col-md-4">
                        <div class="stat-card bg-info bg-opacity-10">
                            <h5 class="text-info">Số bác sĩ tham gia</h5>
                            <h2 class="mb-0">${medicalRecordStats.uniqueDoctors}</h2>
                        </div>
                    </div>
                </div>

                <div class="row mt-4">
                    <!-- Biểu đồ trạng thái hồ sơ -->
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">Trạng thái hồ sơ bệnh án</h5>
                                <div class="chart-container">
                                    <canvas id="statusChart"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Biểu đồ phân bố hồ sơ theo bác sĩ -->
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">Tỷ lệ hồ sơ hoàn thành</h5>
                                <div class="chart-container">
                                    <canvas id="completionChart"></canvas>
                                </div>
                            </div>
                        </div>
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

            // Biểu đồ trạng thái hồ sơ
            const statusCtx = document.getElementById('statusChart').getContext('2d');
            new Chart(statusCtx, {
                type: 'pie',
                data: {
                    labels: ['Hoàn thành', 'Đang xử lý'],
                    datasets: [{
                        data: [${medicalRecordStats.completedRecords}, ${medicalRecordStats.ongoingRecords}],
                        backgroundColor: ['#36A2EB', '#FF6384']
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false
                }
            });

            // Biểu đồ tỷ lệ hoàn thành
            const completionCtx = document.getElementById('completionChart').getContext('2d');
            const completionRate = (${medicalRecordStats.completedRecords} / ${medicalRecordStats.totalRecords} * 100).toFixed(1);
            new Chart(completionCtx, {
                type: 'doughnut',
                data: {
                    labels: ['Hoàn thành', 'Chưa hoàn thành'],
                    datasets: [{
                        data: [completionRate, (100 - completionRate)],
                        backgroundColor: ['#4BC0C0', '#FFCE56']
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    tooltips: {
                        callbacks: {
                            label: function(tooltipItem, data) {
                                return data.labels[tooltipItem.index] + ': ' + data.datasets[0].data[tooltipItem.index] + '%';
                            }
                        }
                    }
                }
            });
        </script>
    </body>
</html> 