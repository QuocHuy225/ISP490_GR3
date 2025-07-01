<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.Doctor" %>
<%@ page import="com.mycompany.isp490_gr3.model.Schedule" %>
<%@ page import="com.mycompany.isp490_gr3.dto.SlotDetailDTO" %> <%-- Import DTO mới --%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>


<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết Bác sĩ: ${doctor.fullName}</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/make-appointment.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/doctor-detail.css"> <%-- Đảm bảo đường dẫn này trỏ đúng đến file CSS --%>

    </head>
    <body>
        <%
        String contextPath = request.getContextPath();
        Object userRole = session.getAttribute("userRole");
        User.Role currentRole = null;
        if (userRole != null && userRole instanceof User.Role) {
            currentRole = (User.Role) userRole;
        }
        boolean isAdmin = (currentRole == User.Role.ADMIN);
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
            <div class="sidebar-header"><h3>MENU</h3></div>
            <ul class="list-unstyled components">
                <li><a href="<%= contextPath %>/homepage"><i class="bi bi-speedometer2"></i> Trang chủ</a></li>
                <li><a href="<%= contextPath %>/makeappointments"><i class="bi bi-calendar-check"></i> Đặt lịch hẹn</a></li>
                <li><a href="<%= contextPath %>/api/patient/*"><i class="bi bi-calendar-check"></i> Lịch hẹn của tôi</a></li>
                <li><a href="#"><i class="bi bi-file-medical"></i> Hồ sơ sức khỏe</a></li>
                <li><a href="#"><i class="bi bi-hospital"></i> Dịch vụ</a></li>
                <li><a href="#"><i class="bi bi-chat-dots"></i> Liên hệ bác sĩ</a></li>
                <li><a href="<%= contextPath %>/appointments"><i class="bi bi-calendar-check"></i> Thống kê lịch hẹn</a></li>
            </ul>
        </nav>
        <div id="sidebarOverlay"></div>

        <div id="main-wrapper">
            <nav class="navbar navbar-expand-lg top-navbar">
                <div class="container-fluid">
                    <button type="button" id="sidebarCollapse" class="btn btn-primary"><i class="bi bi-list"></i></button>
                    <a class="navbar-brand-custom" href="#"><span class="text-primary">Ánh Dương</span> <span class="text-dark">Clinic</span></a>
                    <div class="navbar-search mx-auto">
                        <i class="bi bi-search"></i>
                        <input type="text" class="form-control" placeholder="Tìm kiếm bệnh nhân, lịch hẹn, hồ sơ...">
                    </div>
                    <div class="dropdown user-dropdown">
                        <button class="btn dropdown-toggle" type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                            <div class="user-profile-icon"><i class="bi bi-person-fill" style="font-size: 1.5rem;"></i></div>
                            <div class="user-info d-none d-md-block"><div class="user-name"><%= userName %></div><div class="user-role"><%= userRoleDisplay %></div></div>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                            <% if (isAdmin) { %>
                            <li><a class="dropdown-item" href="<%= contextPath %>/admin/authorization"><i class="bi bi-people-fill"></i><span>Quản lý người dùng</span></a></li>
                            <li><hr class="dropdown-divider"></li>
                                <% } %>
                            <li><a class="dropdown-item" href="<%= contextPath %>/user/profile"><i class="bi bi-person-fill"></i><span>Thông tin cá nhân</span></a></li>
                            <li><a class="dropdown-item" href="<%= contextPath %>/user/profile"><i class="bi bi-key-fill"></i><span>Đổi mật khẩu</span></a></li>
                            <li><a class="dropdown-item" href="#"><i class="bi bi-gear-fill"></i><span>Cài đặt</span></a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="<%= contextPath %>/auth/logout"><i class="bi bi-box-arrow-right"></i><span>Đăng xuất</span></a></li>
                        </ul>
                    </div>
                </div>
            </nav>

            <main class="container-fluid py-4">
                <div class="container custom-container bg-white rounded-3 shadow-sm p-4">
                    <div class="doctor-detail-header">
                        <%-- Hình ảnh bác sĩ: Hardcode hoặc dùng placeholder. --%>
                        <img src="${pageContext.request.contextPath}/images/doctor_profile_placeholder.jpg" alt="Ảnh bác sĩ" style="width: 120px; height: 120px; border-radius: 50%; object-fit: cover;">
                        <div class="doctor-info">
                            <h1>${doctor.fullName}</h1>
                            <%-- Thông tin bác sĩ cơ bản --%>
                            <p><span class="badge bg-secondary">${doctor.specializationName}</span></p>

                        </div>
                    </div>

                    <div class="card detail-card mb-4">
                        <h4 class="section-header">Đặt khám nhanh</h4>
                        <div class="schedule-nav-wrapper position-relative">
                            <button class="doctor-scroll-btn doctor-scroll-left" id="scheduleScrollLeft" style="left: 0; display: none;"><i class="bi bi-chevron-left"></i></button>
                            <div class="schedule-nav" id="scheduleNav">
                                <%
                                LocalDate today = LocalDate.now();
                                DateTimeFormatter dayOfWeekFullFormatter = DateTimeFormatter.ofPattern("EEEE", new java.util.Locale("vi"));
                                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM");

                                List<Schedule> schedules = (List<Schedule>) request.getAttribute("schedules");

                                java.util.Map<LocalDate, Schedule> schedulesMap = new java.util.HashMap<>();
                                if (schedules != null) {
                                    for (Schedule s : schedules) {
                                        schedulesMap.put(s.getWorkingDate(), s);
                                    }
                                }

                                for (int i = 0; i < 60; i++) {
                                    LocalDate date = today.plusDays(i);
                                    String dayOfWeekName = dayOfWeekFullFormatter.format(date);
                                    String formattedDate = date.format(dateFormatter);

                                    int slotCount = 0;
                                    if (schedulesMap.containsKey(date)) {
                                        // Sử dụng availableSlotDetails để đếm số lượng slot
                                        slotCount = schedulesMap.get(date).getAvailableSlotDetails().size(); 
                                    }
                                %>
                                <div class="schedule-item <%= i == 0 ? "selected" : "" %>" data-date="<%= date.toString() %>">
                                    <div class="day-of-week"><%= dayOfWeekName %></div>
                                    <div class="date"><%= formattedDate %></div>
                                    <div class="slot-count"><%= slotCount %> khung giờ</div>
                                </div>
                                <% } %>
                            </div>
                            <button class="doctor-scroll-btn doctor-scroll-right" id="scheduleScrollRight" style="right: 0;"><i class="bi bi-chevron-right"></i></button>
                        </div>

                        <div class="time-slots mt-3" id="timeSlotsContainer">
                            <%-- Nội dung này sẽ được render bởi JavaScript --%>
                        </div>
                        <button class="btn btn-primary mt-4 w-100" id="bookFinalBtn" disabled>Chọn ngày và giờ để đặt lịch</button>
                    </div>
                </div>
            </main>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

        <script>
        window.APP_CONTEXT_PATH = '<%= request.getContextPath() %>';
        const doctorData = {
            id: ${doctor.id},
            schedules: []
        };
        // Populate doctorData.schedules from server-side schedules list
        <c:forEach var="schedule" items="${schedules}">
        doctorData.schedules.push({
            workingDate: "${schedule.workingDate.toString()}",
            // Đã thay đổi cách truyền timeSlots để gửi các đối tượng SlotDetailDTO
            timeSlots: [
                <c:forEach var="slotDetail" items="${schedule.availableSlotDetails}" varStatus="loop">
                {
                    slotId: ${slotDetail.slotId}, // Thêm slotId nếu bạn cần
                    startTime: "${slotDetail.startTime.toString()}",
                    endTime: "${slotDetail.endTime.toString()}",
                    maxPatients: ${slotDetail.maxPatients},
                    bookedPatients: ${slotDetail.bookedPatients}
                }<c:if test="${!loop.last}">,</c:if>
                </c:forEach>
            ]
        });
        </c:forEach>

        // DEBUGGING: In ra dữ liệu schedules mà JS nhận được
        console.log("Dữ liệu lịch trình bác sĩ (JS):", doctorData.schedules);
        </script>

        <script src="${pageContext.request.contextPath}/js/doctor-detail.js"></script>

    </body>
</html>
