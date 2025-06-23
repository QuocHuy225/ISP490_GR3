<%-- src/main/webapp/WEB-INF/views/doctor-list.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %> <%-- Import User model for sidebar/navbar --%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách Bác sĩ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" xintegrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" xintegrity="sha512-SnH5WK+bZxgPHs44uWIX+LLJAJ9/2PkPKZ5QiAj6Ta86w+fsb2TkcmfRyVX3pBnMFcV7oQPJkl9QevSCWr3W6A==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/make-appointment.css"> <%-- Reuse existing CSS for sidebar/navbar --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/doctor-list.css"> <%-- New CSS for doctor list specific styles --%>
    <style>
        /* CSS to prevent body scrolling when mobile sidebar is open */
        body.no-scroll {
            overflow: hidden;
        }
    </style>
</head>
<body>
    <%
    // Re-include user role and info logic for sidebar/navbar
    Object userRole = session.getAttribute("userRole");
    User.Role currentRole = null;

    if (userRole != null) {
        if (userRole instanceof User.Role) {
            currentRole = (User.Role) userRole;
        } else {
            try {
                currentRole = User.Role.valueOf(userRole.toString().toUpperCase());
            } catch (Exception e) {
                System.err.println("Could not parse user role: " + userRole.toString());
                currentRole = User.Role.PATIENT; // Default if parsing fails
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
    String contextPath = request.getContextPath(); // Lấy contextPath ở đây
    %>

    <nav id="sidebar">
        <div class="sidebar-header">
            <h3>MENU</h3>
        </div>
        <ul class="list-unstyled components">
            <li><a href="<%= contextPath %>/homepage"><i class="bi bi-speedometer2"></i> Trang chủ</a></li>
            <li><a href="<%= contextPath %>/makeappointments"><i class="bi bi-calendar-check"></i> Đặt lịch hẹn</a></li>
            <li><a href="#"><i class="bi bi-calendar-check"></i> Lịch hẹn của tôi</a></li>
            <li><a href="#"><i class="bi bi-file-medical"></i> Hồ sơ sức khỏe</a></li>
            <li><a href="#"><i class="bi bi-hospital"></i> Dịch vụ</a></li>
            <li><a href="#"><i class="bi bi-chat-dots"></i> Liên hệ bác sĩ</a></li>
             <li>
                <a href="<%= contextPath %>/appointments">
                    <i class="bi bi-calendar-check"></i> Thống kê lịch hẹn
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
                            <a class="dropdown-item" href="<%= contextPath %>/admin/authorization">
                                <i class="bi bi-people-fill"></i>
                                <span>Quản lý người dùng</span>
                            </a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                            <% } %>
                        <li>
                            <a class="dropdown-item" href="<%= contextPath %>/user/profile">
                                <i class="bi bi-person-fill"></i>
                                <span>Thông tin cá nhân</span>
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="<%= contextPath %>/user/profile">
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
                            <a class="dropdown-item text-danger" href="<%= contextPath %>/auth/logout">
                                <i class="bi bi-box-arrow-right"></i>
                                <span>Đăng xuất</span>
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>

        <main class="container-fluid py-4">
            <div class="container custom-container bg-white rounded-3 shadow-sm p-4">
                <div class="mb-4">
                    <h2 class="h4 mb-0">Danh sách bác sĩ</h2>
                </div>


                <div class="mb-3 text-muted">Tìm thấy ${totalDoctors} kết quả.</div>

                <div class="doctor-list-container">
                    <c:choose>
                        <c:when test="${empty doctors}">
                            <div class="alert alert-info text-center" role="alert">
                                Không tìm thấy bác sĩ nào phù hợp.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="doctor" items="${doctors}">
                                <div class="doctor-item d-flex align-items-center p-3 mb-3 border rounded">
                                    <%-- Image URL is now hardcoded as requested --%>
                                    <img src="https://via.placeholder.com/100/ADD8E6/000000?text=Doctor"
                                         class="rounded-circle me-3 doctor-avatar" alt="${doctor.fullName}">
                                    <div class="doctor-details flex-grow-1">
                                        <h5 class="doctor-name mb-1">
                                            <%-- Removed doctor.degree as requested --%>
                                            ${doctor.fullName}
                                        </h5>
                                        <div class="doctor-specializations mb-2">
                                            <span class="badge bg-light text-secondary">${doctor.specializationName}</span>
                                        </div>
                                        
                                    </div>
                                    <button class="btn btn-primary book-doctor-btn" data-doctor-id="${doctor.id}">
                                        Đặt khám
                                    </button>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="d-flex justify-content-between align-items-center mt-4">
                    <div class="pagination-info d-flex align-items-center">
                        <span class="me-2">Số kết quả mỗi trang</span>
                        <select class="form-select form-select-sm w-auto" id="itemsPerPageSelectDoctor">
                            <option value="10" ${itemsPerPage == 10 ? 'selected' : ''}>10</option>
                            <option value="25" ${itemsPerPage == 25 ? 'selected' : ''}>25</option>
                            <option value="50" ${itemsPerPage == 50 ? 'selected' : ''}>50</option>
                        </select>
                    </div>
                    <nav aria-label="Page navigation for doctors">
                        <ul class="pagination pagination-sm mb-0" id="doctorPaginationNav">
                            <%-- Pagination links will be generated by JavaScript --%>
                        </ul>
                    </nav>
                    <div class="pagination-summary" id="doctorPaginationSummary">
                        <%-- Pagination summary will be updated by JavaScript --%>
                        Hiển thị kết quả 1 - 10 trên tổng ${totalDoctors} kết quả
                    </div>
                </div>
            </div>
        </main>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" xintegrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

    <script>
        // Định nghĩa contextPath toàn cục cho JavaScript
        // Điều này giúp tất cả các script sau có thể truy cập contextPath một cách dễ dàng
        window.APP_CONTEXT_PATH = '<%= request.getContextPath() %>';
    </script>
    
    <script src="${pageContext.request.contextPath}/js/make-appointment.js"></script> <%-- Keep for sidebar logic --%>
    <script src="${pageContext.request.contextPath}/js/doctor-list.js"></script> <%-- New JS for this page --%>

    <script>
        // Pass data from server-side (JSP) to client-side (JavaScript) for pagination
        // Sử dụng window.APP_CONTEXT_PATH thay vì gán lại contextPath ở đây nếu đã định nghĩa ở trên
        const doctorServerData = {
            totalDoctors: ${totalDoctors},
            currentPage: ${currentPage},
            itemsPerPage: ${itemsPerPage},
            searchQuery: '${searchQuery}'
        };
    </script>
</body>
</html>