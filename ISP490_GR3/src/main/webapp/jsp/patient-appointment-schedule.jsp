<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lịch Hẹn Của Bạn - Phòng Khám</title>
        <%-- Bootstrap CSS --%>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <%-- Bootstrap Icons --%>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <%-- Font Awesome (nếu vẫn cần các icon FA cụ thể) --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

        <%-- CSS chung cho homepage/layout (TẢI TRƯỚC để thiết lập base styles) --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <%-- CSS cụ thể cho trang lịch hẹn (TẢI SAU để tinh chỉnh/bổ sung mà không xung đột base) --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/patient-appointment-schedule.css">

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
                <li>
                    <a href="${pageContext.request.contextPath}/homepage">
                        <i class="bi bi-speedometer2"></i> Trang chủ
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/patient/my-appointments">
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
            <div class="container">
                <header>
                    <h1>Lịch Hẹn Của Bạn</h1>
                    <p>Chào mừng, <span id="patientName"><%= userName %></span>!</p>
                    <button id="btnNewAppointment" class="btn btn-primary"><i class="fas fa-plus-circle"></i> Đặt Hẹn Mới</button>
                </header>

                <%-- Bộ điều khiển Tab --%>
                <div class="tab-controls">
                    <button class="tab-button active" data-tab-id="upcomingAppointmentsContent">
                        <i class="fas fa-calendar-alt"></i> Lịch Hẹn Sắp Tới
                    </button>
                    <button class="tab-button" data-tab-id="appointmentHistoryContent">
                        <i class="fas fa-history"></i> Lịch Sử Các Cuộc Hẹn
                    </button>
                </div>

                <%-- Nội dung của Tab "Lịch hẹn sắp tới" --%>
                <section id="upcomingAppointmentsContent" class="tab-content active-tab-content">
                    <h2><i class="fas fa-calendar-alt"></i> Lịch Hẹn Sắp Tới</h2>
                    <div id="upcomingAppointmentsList" class="appointment-list">
                        <p class="no-appointments" id="noUpcomingAppointments">Bạn không có lịch hẹn sắp tới nào.</p>
                    </div>
                    <%-- Thêm container cho phân trang sắp tới --%>
                    <div id="upcomingPagination" class="pagination-controls"></div>
                </section>

                <%-- Nội dung của Tab "Lịch sử các cuộc hẹn" --%>
                <section id="appointmentHistoryContent" class="tab-content">
                    <h2><i class="fas fa-history"></i> Lịch Sử Các Cuộc Hẹn</h2>
                    <div id="appointmentHistoryList" class="appointment-list">
                        <p class="no-appointments" id="noHistoryAppointments">Bạn chưa có lịch sử cuộc hẹn nào.</p>
                    </div>
                    <%-- Thêm container cho phân trang lịch sử --%>
                    <div id="historyPagination" class="pagination-controls"></div>
                </section>

                <%-- Modal chi tiết lịch hẹn --%>
                <div id="appointmentDetailModal" class="modal">
                    <div class="modal-content">
                        <span class="close-button">&times;</span>
                        <h3>Chi Tiết Lịch Hẹn</h3>
                        <div id="detailContent">
                        </div>
                        <div class="modal-actions">
                            <button class="btn btn-secondary" id="btnCancelAppointment" style="display: none;"><i class="bi bi-x-circle"></i> Hủy Hẹn</button>
                            <button class="btn btn-primary" id="btnCloseDetail"><i class="bi bi-check-circle"></i> Đóng</button>
                        </div>
                    </div>
                </div>

                <%-- MODAL XÁC NHẬN TÙY CHỈNH --%>
                <div id="cancelConfirmModal" class="modal">
                    <div class="modal-content small-modal-content">
                        <span class="close-button" id="closeConfirmModalBtn">&times;</span>
                        <h3>Xác nhận Hủy Lịch Hẹn</h3>
                        <p>Bạn có chắc chắn muốn hủy lịch hẹn này không?</p>
                        <p>ID Lịch Hẹn: <span id="confirmAppointmentIdDisplay"></span></p>
                        <div class="modal-actions justify-content-center">
                            <button class="btn btn-danger" id="confirmCancelBtn"><i class="bi bi-check-circle"></i> Xác nhận Hủy</button>
                            <button class="btn btn-secondary" id="cancelConfirmBtn"><i class="bi bi-x-circle"></i> Hủy bỏ</button>
                        </div>
                    </div>
                </div>

                <%-- MODAL THÔNG BÁO THÀNH CÔNG --%>
                <div id="successMessageModal" class="modal">
                    <div class="modal-content small-modal-content">
                        <span class="close-button" id="closeSuccessModalBtn">&times;</span>
                        <h3><i class="fas fa-check-circle text-success"></i> Thành công!</h3>
                        <p id="successMessageText" style="font-size: 1.1em; text-align: center;"></p>
                        <div class="modal-actions justify-content-center">
                            <button class="btn btn-primary" id="okSuccessModalBtn">OK</button>
                        </div>
                    </div>
                </div>               
            </div>
        </div>
        <div id="newAppointmentModal" class="modal">
            <div class="modal-content large-modal-content">
                <span class="close-button" id="closeNewAppointmentModalBtn">&times;</span>
                <h3><i class="fas fa-plus-circle"></i> Đặt Lịch Hẹn Mới</h3>
                <form id="newAppointmentForm">
                    <div class="form-group">
                        <label for="newDoctorName">Chọn Bác sĩ:</label>
                        <select id="newDoctorName" name="doctorName" required>
                            <option value="">-- Chọn bác sĩ --</option>
                            <option value="Dr. Nguyễn Văn A">Dr. Nguyễn Văn A</option>
                            <option value="Dr. Trần Thị B">Dr. Trần Thị B</option>
                            <option value="Dr. Lê Văn C">Dr. Lê Văn C</option>
                            <option value="Dr. Phạm Thị D">Dr. Phạm Thị D</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="newService">Dịch vụ:</label>
                        <select id="newService" name="service" required>
                            <option value="">-- Chọn dịch vụ --</option>
                            <option value="Khám tổng quát">Khám tổng quát</option>
                            <option value="Tư vấn dinh dưỡng">Tư vấn dinh dưỡng</option>
                            <option value="Siêu âm">Siêu âm</option>
                            <option value="Khám răng">Khám răng</option>
                            <option value="Tái khám">Tái khám</option>
                            <option value="Xét nghiệm máu">Xét nghiệm máu</option>
                            <option value="Kiểm tra huyết áp">Kiểm tra huyết áp</option>
                            <option value="Tiêm phòng">Tiêm phòng</option>
                            <option value="Tư vấn tâm lý">Tư vấn tâm lý</option>
                            <option value="Kiểm tra sức khỏe định kỳ">Kiểm tra sức khỏe định kỳ</option>
                            <option value="Khám chuyên khoa">Khám chuyên khoa</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="newAppointmentDate">Ngày hẹn:</label>
                        <input type="date" id="newAppointmentDate" name="date" required>
                    </div>
                    <div class="form-group">
                        <label for="newAppointmentTime">Giờ hẹn:</label>
                        <select id="newAppointmentTime" name="time" required>
                            <option value="">-- Chọn giờ hẹn --</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="newNotes">Ghi chú (tùy chọn):</label>
                        <textarea id="newNotes" name="notes" rows="3"></textarea>
                    </div>
                    <div class="modal-actions">
                        <button type="submit" class="btn btn-primary" id="submitNewAppointmentBtn"><i class="bi bi-check-circle"></i> Đặt Lịch</button>
                        <button type="button" class="btn btn-secondary" id="cancelNewAppointmentBtn"><i class="bi bi-x-circle"></i> Hủy</button>
                    </div>
                </form>
            </div>
        </div>
        <%-- Bootstrap Bundle with Popper --%>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
        <%-- Đường dẫn JS của bạn --%>
        <script src="${pageContext.request.contextPath}/js/patient-appointment-schedule.js"></script>
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