<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lịch Làm Việc Bác Sĩ</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manage-doctor-schedule.css">
        <script> const contextPath = "<%= request.getContextPath() 
%>";</script>
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
        %>
        <nav id="sidebar">
            <div class="sidebar-header">
                <h3>MENU</h3>
            </div>
            <ul class="list-unstyled components">
                <li>
                    <a href="${pageContext.request.contextPath}/homepage">
                        <i class="bi bi-house-door-fill"></i> Trang chủ
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/appointments">
                        <i class="bi bi-calendar-check-fill"></i> Quản lý đặt lịch
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/checkin">
                        <i class="bi bi-person-check-fill"></i> Quản lý check-in
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/queue">
                        <i class="bi bi-people-fill"></i> Quản lý hàng đợi
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/doctor/patients">
                        <i class="bi bi-people"></i> Hồ sơ bệnh nhân
                    </a>
                </li>
                <li class="active">
                    <a href="${pageContext.request.contextPath}/receptionist/manage-doctor-schedule">
                        <i class="bi bi-calendar-event-fill"></i> Quản lý lịch bác sĩ
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/receptionist/report">
                        <i class="bi bi-speedometer2"></i> Báo cáo thống kê
                    </a>
                </li>
            </ul>
        </nav>
        <div id="content">
            <nav class="navbar navbar-expand-lg top-navbar">
                <div class="container-fluid">
                    <button type="button" id="sidebarCollapse" class="btn btn-primary">
                        <i class="bi bi-list"></i>
                    </button>

                    <div style="margin-left: 60px;">
                        <h3>
                            <span style="color: var(--primary);">Ánh Dương</span>
                            <span style="color: var(--foreground);">Clinic</span>
                        </h3>
                    </div>

                    <div class="dropdown user-dropdown">
                        <button class="btn dropdown-toggle" type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                            <div class="user-profile-icon">
                                <i class="bi bi-person-fill"></i>
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
            <div class="calendar-container">
                <div class="calendar-header">
                    <div class="d-flex align-items-center">
                        <button id="todayButton" class="btn btn-sm me-3">TODAY</button>
                        <div class="nav-buttons">
                            <button id="prevMonth"><i class="fas fa-chevron-left"></i></button>
                            <button id="nextMonth" class="me-3"><i class="fas fa-chevron-right"></i></button>
                        </div>
                        <h2 id="currentMonthYear">Tháng 6 2025</h2>
                    </div>
                    <div class="d-flex align-items-center">
                        <select class="form-select form-select-sm me-2 rounded-md" id="viewMode" style="width: auto;">
                            <option value="month">Xem theo Tháng</option>
                            <option value="week">Xem theo Tuần</option>
                            <option value="day">Xem theo Ngày</option>
                        </select>
                        <button class="btn btn-primary rounded-md" id="addEventButton">THÊM LỊCH LÀM VIỆC</button>
                    </div>
                </div>

                <div class="calendar-weekdays">
                    <span>CN</span>
                    <span>T2</span>
                    <span>T3</span>
                    <span>T4</span>
                    <span>T5</span>
                    <span>T6</span>
                    <span>T7</span>
                </div>

                <div class="calendar-day-grid" id="calendarGrid">
                </div>
            </div>

            <div class="modal fade" id="scheduleModal" tabindex="-1" aria-labelledby="scheduleModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="scheduleModalLabel">Thêm Lịch Làm Việc</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="scheduleForm">
                                <input type="hidden" id="scheduleId" name="schedule_id">
                                <div class="mb-3">
                                    <label for="modalDoctorId" class="form-label">Chọn Bác Sĩ:</label>
                                    <select class="form-select rounded-md" id="modalDoctorId" name="doctor_id" required>
                                        <option value="">-- Chọn bác sĩ --</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label for="modalWorkDate" class="form-label">Ngày Làm Việc:</label>
                                    <input type="date" class="form-control rounded-md" id="modalWorkDate" name="work_date" required>
                                </div>
                                <div class="mb-3 form-check">
                                    <input type="checkbox" class="form-check-input rounded-md" id="modalIsActive" name="is_active" value="1" checked>
                                    <label class="form-check-label" for="modalIsActive">Hoạt động</label>
                                </div>
                                <div class="mb-3">
                                    <label for="modalEventName" class="form-label">Tên Lịch:</label>
                                    <input type="text" class="form-control rounded-md" id="modalEventName" name="event_name">
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-danger rounded-md me-auto" id="deleteScheduleBtn" style="display:none;">
                                <i class="bi bi-trash me-2"></i>Xóa Lịch
                            </button>
                            <button type="button" class="btn btn-secondary rounded-md" data-bs-dismiss="modal">
                                <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                            </button>
                            <button type="button" class="btn btn-primary rounded-md" id="saveScheduleBtn">
                                <i class="bi bi-check-circle me-2"></i>Lưu Lịch
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal fade" id="confirmationModal" tabindex="-1" aria-labelledby="confirmationModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="confirmationModalLabel">Xác Nhận</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <p id="confirmationMessage"></p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary rounded-md" data-bs-dismiss="modal">Hủy</button>
                            <button type="button" class="btn btn-danger rounded-md" id="confirmDeleteBtn">Xóa</button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal fade" id="notificationModal" tabindex="-1" aria-labelledby="notificationModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="notificationModalLabel">Thông Báo</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <p id="notificationMessage"></p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary rounded-md" data-bs-dismiss="modal">Đóng</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/manage-doctor-schedule.js"></script>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const sidebar = document.getElementById('sidebar');
                const content = document.getElementById('content');
                const sidebarCollapse = document.getElementById('sidebarCollapse');

                if (sidebarCollapse && sidebar && content) {
                    sidebarCollapse.addEventListener('click', function () {
                        sidebar.classList.toggle('active');
                        content.classList.toggle('active');
                    });
                }
            });
        </script>
    </body>
</html>