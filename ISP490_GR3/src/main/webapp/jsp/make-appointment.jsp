<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đặt Lịch Hẹn Bác Sĩ</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" integrity="sha512-SnH5WK+bZxgPHs44uWIX+LLJAJ9/2PkPKZ5QiAj6Ta86w+fsb2TkcmfRyVX3pBnMFcV7oQPJkl9QevSCWr3W6A==" crossorigin="anonymous" referrerpolicy="no-referrer" />
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/make-appointment.css">
        <style>
            body.no-scroll {
                overflow: hidden;
            }
        </style>
    </head>
    <body>
        <%
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
        Object userObj = session.getAttribute("user");
        String userName = "User";
        String userRoleDisplay = "Patient";
        if (userObj instanceof User) {
            User user = (User) userObj;
            userName = user.getFullName() != null ? user.getFullName() : user.getEmail();
            userRoleDisplay = user.getRole() != null ? user.getRole().getValue() : "Patient";
        }
        boolean isAdmin = (currentRole == User.Role.ADMIN);
        String contextPath = request.getContextPath();
        %>

        <nav id="sidebar">
            <div class="sidebar-header">
                <h3>MENU</h3>
            </div>
            <ul class="list-unstyled components">
                <li>
                    <a href="${pageContext.request.contextPath}/homepage">
                        <i class="bi bi-speedometer2"></i> Trang chủ
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/makeappointments">
                        <i class="bi bi-calendar-check"></i> Đặt lịch hẹn
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/api/*">
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
            </ul>
        </nav>

        <div id="sidebarOverlay"></div>

        <div id="main-wrapper">
            <nav class="navbar navbar-expand-lg top-navbar">
                <div class="container-fluid">
                    <button type="button" id="sidebarCollapse" class="btn btn-primary">
                        <i class="bi bi-list"></i>
                    </button>
                    <a class="navbar-brand-custom" href="#">
                        <span class="text-primary">Ánh Dương</span> <span class="text-dark">Clinic</span>
                    </a>
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

            <header class="header-navigation">
                <div class="container-fluid d-flex justify-content-center">
                    <ul class="nav nav-pills custom-nav-pills">
                        <li class="nav-item">
                            <a class="nav-link active" aria-current="page" href="#">Đặt khám Bác sĩ</a>
                        </li>
                    </ul>
                </div>
            </header>

            <main class="container-fluid py-4">
                <div class="container custom-container bg-white rounded-3 shadow-sm p-4">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h2 class="h4 mb-0">Bác sĩ nổi bật</h2>
                        <button class="btn btn-primary btn-sm view-all-btn">
                            Xem tất cả <i class="fas fa-chevron-right ms-1"></i>
                        </button>
                    </div>

                    <div class="position-relative">
                        <div class="doctor-list-wrapper">
                            <div class="d-flex doctor-list-scroll">
                                <c:forEach var="doctor" items="${doctors}">
                                    <div class="card doctor-card me-4 flex-shrink-0">
                                        <div class="card-body d-flex flex-column align-items-center text-center">
                                            <img src="https://via.placeholder.com/100/ADD8E6/000000?text=Doc" 
                                                 class="rounded-circle mb-3 border border-2" alt="${doctor.fullName}">
                                            <h5 class="card-title mb-1 fs-6">${doctor.fullName}</h5>
                                            <p class="card-text text-muted mb-3 fs-6">${doctor.specializationName}</p>
                                            <button class="btn btn-outline-primary mt-auto book-now-btn" data-doctor-id="${doctor.id}">
                                                Đặt lịch ngay <i class="fas fa-chevron-right ms-1"></i>
                                            </button>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                        <button class="btn btn-light rounded-circle shadow-sm doctor-scroll-btn doctor-scroll-right">
                            <i class="fas fa-chevron-right"></i>
                        </button>
                    </div>
                </div>
            </main>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
        <script>
            window.APP_CONTEXT_PATH = '<%= request.getContextPath() %>';
        </script>
        <script src="${pageContext.request.contextPath}/js/make-appointment.js"></script>
    </body>
</html>