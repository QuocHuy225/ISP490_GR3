<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Homepage - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Custom CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <style>
            :root {
                --primary-color: #0360D9;
                --primary-dark: #0246a3;
                --primary-light: #e3f2fd;
                --white: #ffffff;
                --text-dark: #2B3674;
                --text-light: #A3AED0;
                --body-bg: #f8f9fa;
                --gradient-primary: linear-gradient(135deg, var(--primary-color) 0%, #00d4ff 100%);
                --shadow-light: 0 5px 25px rgba(3, 96, 217, 0.1);
                --shadow-medium: 0 10px 40px rgba(3, 96, 217, 0.15);
            }

            * {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
                -webkit-font-smoothing: antialiased;
                -moz-osx-font-smoothing: grayscale;
            }

            body {
                background-color: var(--body-bg);
                color: var(--text-dark);
            }

            /* Sidebar Styles */
            #sidebar {
                min-width: 250px;
                max-width: 250px;
                min-height: 100vh;
                background: var(--primary-color);
                color: var(--white);
                transition: all 0.3s;
                position: fixed;
                left: 0;
                top: 0;
                z-index: 1000;
                box-shadow: 4px 0 10px rgba(0,0,0,0.1);
            }

            #sidebar.collapsed {
                margin-left: -250px;
            }

            #sidebar .sidebar-header {
                padding: 20px;
                background: var(--primary-dark);
                text-align: center;
            }

            #sidebar .sidebar-header h3 {
                color: var(--white);
                margin: 0;
                font-weight: 700;
                font-size: 1.5rem;
            }

            #sidebar ul.components {
                padding: 20px 0;
            }

            #sidebar ul li a {
                padding: 15px 20px;
                font-size: 1.1em;
                display: flex;
                align-items: center;
                color: var(--white);
                text-decoration: none;
                transition: all 0.3s;
                border-radius: 8px;
                margin: 4px 8px;
            }

            #sidebar ul li a:hover {
                background: var(--primary-dark);
                transform: translateX(5px);
            }

            #sidebar ul li a i {
                margin-right: 10px;
                font-size: 1.2em;
            }

            #sidebar ul li.active > a {
                background: var(--primary-dark);
                border-left: 4px solid var(--white);
            }

            /* Admin only menu items */
            .admin-only {
                display: none;
            }

            /* Main Content Styles */
            #content {
                width: calc(100% - 250px);
                min-height: 100vh;
                transition: all 0.3s;
                position: absolute;
                top: 0;
                right: 0;
            }

            #content.expanded {
                width: 100%;
            }

            /* Navbar Styles */
            .top-navbar {
                background: var(--white);
                padding: 1rem;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                margin-bottom: 2rem;
                position: sticky;
                top: 0;
                z-index: 1020;
                width: 100%;
            }

            .navbar-search {
                max-width: 600px;
                width: 100%;
                position: relative;
            }

            .navbar-search input {
                padding: 0.8rem 1rem 0.8rem 3rem;
                border-radius: 30px;
                border: 2px solid #e0e0e0;
                background: #f8f9fa;
                font-size: 1.1rem;
                transition: all 0.3s ease;
            }

            .navbar-search input:focus {
                border-color: var(--primary-color);
                box-shadow: 0 0 0 0.2rem rgba(3, 96, 217, 0.25);
            }

            .navbar-search i {
                position: absolute;
                left: 1.2rem;
                top: 50%;
                transform: translateY(-50%);
                color: var(--text-light);
                font-size: 1.2rem;
            }

            /* Enhanced Dropdown Styling */
            .user-dropdown {
                position: relative;
            }

            .dropdown-toggle {
                background: linear-gradient(135deg, var(--primary-color), #00d4ff) !important;
                border: none !important;
                border-radius: 50px !important;
                padding: 0.75rem 1.5rem !important;
                color: white !important;
                font-weight: 600 !important;
                box-shadow: var(--shadow-light) !important;
                transition: all 0.3s ease !important;
                text-decoration: none !important;
                display: flex !important;
                align-items: center !important;
                gap: 0.75rem !important;
            }

            .dropdown-toggle:hover {
                transform: translateY(-2px) !important;
                box-shadow: var(--shadow-medium) !important;
                color: white !important;
            }

            .dropdown-toggle:focus {
                box-shadow: var(--shadow-medium) !important;
                color: white !important;
            }

            .dropdown-toggle::after {
                border: none !important;
                content: '\f282' !important;
                font-family: 'Bootstrap Icons' !important;
                margin-left: 0.5rem !important;
                transition: transform 0.3s ease !important;
            }

            .dropdown-toggle[aria-expanded="true"]::after {
                transform: rotate(180deg) !important;
            }

            .dropdown-menu {
                border: none !important;
                border-radius: 20px !important;
                box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15) !important;
                padding: 1rem 0 !important;
                margin-top: 0.5rem !important;
                backdrop-filter: blur(20px) !important;
                background: rgba(255, 255, 255, 0.95) !important;
                min-width: 280px !important;
            }

            .dropdown-item {
                padding: 0.75rem 1.5rem !important;
                font-weight: 500 !important;
                transition: all 0.3s ease !important;
                border: none !important;
                background: none !important;
                display: flex !important;
                align-items: center !important;
                gap: 0.75rem !important;
                color: var(--text-dark) !important;
                text-decoration: none !important;
                margin: 0.25rem 0 !important;
            }

            .dropdown-item:hover {
                background: linear-gradient(135deg, var(--primary-light), rgba(0, 212, 255, 0.1)) !important;
                color: var(--primary-dark) !important;
                transform: translateX(5px) !important;
                padding-left: 2rem !important;
            }

            .dropdown-item i {
                font-size: 1.1rem !important;
                width: 20px !important;
                text-align: center !important;
            }

            .dropdown-item.text-danger {
                color: #dc3545 !important;
            }

            .dropdown-item.text-danger:hover {
                background: linear-gradient(135deg, #fee, #fdd) !important;
                color: #c82333 !important;
            }

            .dropdown-divider {
                margin: 0.5rem 1rem !important;
                border-top: 1px solid rgba(0, 0, 0, 0.1) !important;
                background: none !important;
            }

            /* User profile image styling */
            .user-profile-icon {
                width: 45px !important;
                height: 45px !important;
                border-radius: 50% !important;
                background: rgba(255, 255, 255, 0.2) !important;
                display: flex !important;
                align-items: center !important;
                justify-content: center !important;
                border: 2px solid rgba(255, 255, 255, 0.3) !important;
                backdrop-filter: blur(10px) !important;
            }

            .user-info {
                display: flex !important;
                flex-direction: column !important;
                align-items: flex-start !important;
            }

            .user-name {
                font-size: 1rem !important;
                font-weight: 600 !important;
                margin: 0 !important;
                line-height: 1.2 !important;
            }

            .user-role {
                font-size: 0.8rem !important;
                opacity: 0.8 !important;
                margin: 0 !important;
                line-height: 1.2 !important;
                text-transform: uppercase !important;
                letter-spacing: 0.5px !important;
            }

            /* Dashboard Cards */
            .stat-card {
                background: var(--white);
                border-radius: 15px;
                padding: 1.5rem;
                box-shadow: 0 4px 20px rgba(0,0,0,0.05);
                transition: all 0.3s ease;
            }

            .stat-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 8px 25px rgba(0,0,0,0.1);
            }

            .stat-icon {
                width: 60px;
                height: 60px;
                border-radius: 12px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 1.8rem;
                margin-bottom: 1rem;
            }

            .stat-icon.primary {
                background: var(--primary-light);
                color: var(--primary-color);
            }

            .stat-icon.success {
                background: #e6f4ea;
                color: #34a853;
            }

            .stat-icon.info {
                background: #e8f0fe;
                color: #4285f4;
            }

            .stat-icon.warning {
                background: #fef7e6;
                color: #fbbc04;
            }

            .stat-value {
                font-size: 2rem;
                font-weight: 700;
                margin-bottom: 0.5rem;
                color: var(--text-dark);
            }

            .stat-label {
                color: var(--text-light);
                font-size: 0.9rem;
            }

            /* Animations */
            @keyframes fadeIn {
                from {
                    opacity: 0;
                    transform: translateY(20px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            .animate-fade-in {
                animation: fadeIn 0.5s ease forwards;
            }

            /* Responsive */
            @media (max-width: 768px) {
                #sidebar {
                    margin-left: -250px;
                }
                #sidebar.active {
                    margin-left: 0;
                }
                #content {
                    width: 100%;
                }
                .top-navbar {
                    margin-left: 0;
                }
                
                .dropdown-toggle {
                    padding: 0.5rem 1rem !important;
                }
                
                .user-info {
                    display: none !important;
                }
                
                .dropdown-menu {
                    min-width: 250px !important;
                }
            }

            /* Debug info */
            .debug-info {
                background: #f8f9fa;
                border: 1px solid #dee2e6;
                border-radius: 5px;
                padding: 10px;
                margin: 10px;
                font-family: monospace;
                font-size: 12px;
            }
        </style>
    </head>
    <body>
        <%
        // Get user role for access control
        Object userRole = session.getAttribute("userRole");
        boolean isAdmin = false;
        if (userRole != null) {
            if (userRole instanceof User.Role) {
                isAdmin = ((User.Role) userRole) == User.Role.ADMIN;
            } else {
                isAdmin = "Admin".equals(userRole.toString());
            }
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
                <li class="active">
                    <a href="#">
                        <i class="bi bi-speedometer2"></i> Trang chủ
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-calendar-check"></i> Quản lý lịch hẹn
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-file-medical"></i> Hồ sơ bệnh án
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-hospital"></i> Dịch vụ
                    </a>
                </li>
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
                        <input type="text" class="form-control" placeholder="Tìm kiếm bệnh nhân, lịch hẹn, hồ sơ...">
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
                            <% if (isAdmin) { %>
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/authorization">
                                    <i class="bi bi-people-fill"></i>
                                    <span>Quản lý người dùng</span>
                                </a>
                            </li>
                            <li><hr class="dropdown-divider"></li>
                            <% } %>
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
            });
        </script>
    </body>
</html>