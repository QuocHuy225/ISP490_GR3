<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Báo Cáo Thống Kê Lịch Hẹn</title>
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                margin: 0;
                padding: 0;
                background-color: #f0f2f5;
            }

            /* Main content area padding to avoid elements going under navbar */
            #content {
                padding-top: 10px; /* Reduced padding-top to bring content closer to navbar */
            }

            /* New header section for title and back button */
            .header-with-button {
                display: flex;
                justify-content: space-between; /* Space out title and button */
                align-items: center; /* Vertically center them */
                padding: 0 30px; /* Horizontal padding */
                margin-bottom: 20px; /* Space below header to stat cards */
            }

            .header-with-button h2 {
                margin: 0; /* Remove default h2 margin to keep it tight with flex container */
                font-size: 32px;
                color: #2c3e50;
                /* No padding here, let the container handle it */
            }

            .back-link {
                background-color: #e6f3fc; /* Light blue */
                color: #1976d2; /* Stronger blue */
                padding: 8px 15px;
                border-radius: 8px;
                text-decoration: none;
                font-weight: 500;
                border: 2px solid #1976d2;
                transition: all 0.2s ease;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                display: flex;
                align-items: center;
                gap: 8px;
                /* Removed position: absolute; for simpler flexbox layout */
            }
            .back-link:hover {
                background-color: #1976d2; /* Darker blue on hover */
                color: white;
            }
            .back-link:active {
                transform: scale(0.97);
            }

            .stat-cards {
                display: flex;
                flex-wrap: wrap; /* Allow cards to wrap on smaller screens */
                gap: 20px;
                padding: 0 30px;
                margin-bottom: 30px;
                justify-content: center; /* Center cards if they don't fill the row */
            }
            .card {
                flex: 1;
                min-width: 200px; /* Ensure cards don't get too small */
                max-width: calc(25% - 15px); /* Four cards per row */
                background-color: #e3f2fd; /* Default light blue */
                padding: 20px;
                border-radius: 12px;
                text-align: center;
                box-shadow: 0 3px 6px rgba(0, 0, 0, 0.1);
                transition: transform 0.2s ease, box-shadow 0.2s ease, background-color 0.3s;
                cursor: pointer;
                box-sizing: border-box; /* Include padding in width calculation */
            }
            .card.green {
                background-color: #c8e6c9; /* Light green */
            }
            .card.blue {
                background-color: #bbdefb; /* Light blue, slightly different from default for distinction */
            }
            .card.red { /* Added red for cancelled appointments */
                background-color: #ffcdd2; /* Light red */
            }
            .card h3 {
                margin: 0;
                font-size: 18px;
                color: #333;
            }
            .card p {
                font-size: 32px;
                font-weight: bold;
                margin-top: 10px;
                color: #1976d2;
            }
            .card:hover {
                transform: translateY(-4px);
                box-shadow: 0 6px 12px rgba(0,0,0,0.1);
                background-color: #90caf9 !important; /* Consistent hover color */
            }
            .charts {
                display: flex;
                flex-wrap: wrap;
                justify-content: center; /* Căn giữa các biểu đồ */
                gap: 30px; /* Khoảng cách giữa các biểu đồ */
                padding: 0 20px 30px 20px;
                max-width: 1200px; /* Tăng max-width cho không gian biểu đồ */
                margin: 0 auto; /* Căn giữa toàn bộ khối biểu đồ */
            }
            .chart-container {
                background: white;
                padding: 15px;
                border-radius: 12px;
                box-shadow: 0 1px 4px rgba(0,0,0,0.1);
                height: 350px; /* Tăng chiều cao container biểu đồ, giống ảnh mẫu */
                width: calc(50% - 15px); /* Hai biểu đồ trên một hàng */
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                box-sizing: border-box;
            }
            .chart-container h3 {
                font-size: 18px; /* Slightly larger heading for charts */
                margin-bottom: 15px;
                text-align: center;
                color: #333;
            }
            canvas {
                height: 280px !important; /* Tăng chiều cao canvas, phù hợp với ảnh mẫu */
                max-width: 100%; /* Đảm bảo canvas không tràn ra ngoài */
            }

            /* Responsive adjustments */
            @media (max-width: 1200px) {
                .card {
                    max-width: calc(50% - 10px); /* Two cards per row on wider tablets */
                }
            }

            @media (max-width: 992px) { /* Adjust for smaller desktops/large tablets */
                .chart-container {
                    width: calc(100% - 15px); /* One chart per row */
                }
            }

            @media (max-width: 768px) { /* Adjust for tablets and mobile */
                .header-with-button {
                    flex-direction: column; /* Stack title and button vertically */
                    align-items: flex-start; /* Align to start */
                    padding: 10px 20px;
                }
                .header-with-button h2 {
                    margin-bottom: 10px; /* Space between title and button */
                }
                .back-link {
                    margin-top: 10px; /* Space from title if stacked */
                }
                .stat-cards {
                    flex-direction: column; /* Stack cards vertically */
                    padding: 0 20px;
                }
                .card {
                    max-width: 100%; /* Full width for cards */
                }
                .charts {
                    flex-direction: column; /* Stack charts vertically */
                    padding: 0 20px 20px 20px;
                }
                .chart-container {
                    width: 100%; /* Full width for charts */
                    height: 300px; /* Slightly smaller height for mobile */
                }
                canvas {
                    height: 230px !important; /* Adjust canvas height for mobile */
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

            <div class="header-with-button">
                <h2>Báo Cáo Thống Kê Lịch Hẹn</h2>
                <a href="${pageContext.request.contextPath}/admin/report" class="back-link">← Quay lại Dashboard</a>
            </div>

            <div class="stat-cards">
                <div class="card blue">
                    <h3>Tổng số lịch hẹn</h3>
                    <p>${appointmentStats.totalAppointments}</p>
                </div>
                <div class="card green">
                    <h3>Lịch hẹn đã hoàn tất</h3>
                    <p>${appointmentStats.doneAppointments}</p>
                </div>
                <div class="card red">
                    <h3>Lịch hẹn đã hủy</h3>
                    <p>${appointmentStats.cancelledAppointments}</p>
                </div>
                <div class="card blue">
                    <h3>Lịch hẹn hôm nay</h3>
                    <p>${appointmentStats.todayAppointments}</p>
                </div>
            </div>

            <div class="charts">
                <div class="chart-container">
                    <h3>Biểu đồ lịch hẹn theo ngày</h3>
                    <canvas id="chartByDate"></canvas>
                </div>
                <div class="chart-container">
                    <h3>Biểu đồ lịch hẹn theo bác sĩ</h3>
                    <canvas id="chartByDoctor"></canvas>
                </div>
                <div class="chart-container">
                    <h3>Biểu đồ lịch hẹn theo loại dịch vụ</h3>
                    <canvas id="chartByService"></canvas>
                </div>
                <div class="chart-container">
                    <h3>Biểu đồ lịch hẹn theo trạng thái</h3>
                    <canvas id="chartByStatus"></canvas>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

        <script>
            const appointmentByDate = ${appointmentsByDateJson};
            const appointmentByDoctor = ${appointmentsByDoctorJson};
            const appointmentByService = ${appointmentsByServiceJson};
            const appointmentStatusData = ${appointmentsByStatusJson};

            new Chart(document.getElementById('chartByDate'), {
                type: 'bar',
                data: {
                    labels: appointmentByDate.map(item => item.date),
                    datasets: [{
                            label: 'Số lịch hẹn',
                            data: appointmentByDate.map(item => item.count),
                            backgroundColor: '#64b5f6' /* Darker blue */
                        }]
                },
                options: {responsive: true, scales: {y: {beginAtZero: true}}, maintainAspectRatio: false}
            });

            new Chart(document.getElementById('chartByDoctor'), {
                type: 'bar',
                data: {
                    labels: appointmentByDoctor.map(item => item.doctor),
                    datasets: [{
                            label: 'Số lịch hẹn',
                            data: appointmentByDoctor.map(item => item.count),
                            backgroundColor: '#81c784' /* Darker green */
                        }]
                },
                options: {responsive: true, scales: {y: {beginAtZero: true}}, maintainAspectRatio: false}
            });

            new Chart(document.getElementById('chartByService'), {
                type: 'bar',
                data: {
                    labels: appointmentByService.map(item => item.service),
                    datasets: [{
                            label: 'Số lịch hẹn',
                            data: appointmentByService.map(item => item.count),
                            backgroundColor: '#ffb74d' /* Darker orange */
                        }]
                },
                options: {responsive: true, scales: {y: {beginAtZero: true}}, maintainAspectRatio: false}
            });

            new Chart(document.getElementById('chartByStatus'), {
                type: 'doughnut',
                data: {
                    labels: appointmentStatusData.map(item => item.status),
                    datasets: [{
                            label: 'Tỷ lệ',
                            data: appointmentStatusData.map(item => item.count),
                            backgroundColor: ['#4fc3f7', '#ef5350', '#ffca28'] /* Kept these for status distinction */
                        }]
                },
                options: {responsive: true, maintainAspectRatio: false}
            });
        </script>
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