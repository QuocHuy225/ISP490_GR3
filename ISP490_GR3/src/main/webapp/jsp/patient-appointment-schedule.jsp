<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lịch Hẹn Của Bạn - Phòng Khám</title>
        <%-- Bootstrap CSS --%>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" xintegrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
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
            // Get user information from session
            Object userObj = session.getAttribute("user");
            com.mycompany.isp490_gr3.model.User currentUser = null;

            // Check if user object exists in session
            if (userObj instanceof User) {
                currentUser = (com.mycompany.isp490_gr3.model.User) userObj;
            }

            // --- Bắt đầu xử lý đăng nhập mới ---
            // Nếu người dùng chưa đăng nhập (currentUser là null), chuyển hướng về trang đăng nhập
            if (currentUser == null) {
                String loginPage = request.getContextPath() + "/auth/login";
                response.sendRedirect(loginPage);
                return; 
            }
            // --- Kết thúc xử lý đăng nhập mới ---

            // Nếu đã đăng nhập, tiếp tục lấy thông tin người dùng
            String userName = currentUser.getFullName() != null ?
                currentUser.getFullName() : currentUser.getEmail();
            
            // Get user role for access control (chỉ cần lấy nếu currentUser đã xác định)
            User.Role currentRole = currentUser.getRole();
            String userRoleDisplay = currentRole != null ? currentRole.getValue() : "Patient";
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
               
                <li class="active">                  
                     <a href="${pageContext.request.contextPath}/patient/my-appointments">
                        <i class="bi bi-calendar-check"></i> Lịch hẹn của tôi
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
                            <%-- Nội dung chi tiết lịch hẹn sẽ được chèn bởi JavaScript --%>
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
                        <h3 id="successMessageTitle"><i class="fas fa-check-circle text-success"></i> <span>Thông báo</span></h3>
                        <p id="successMessageText" style="font-size: 1.1em; text-align: center;"></p>
                        <div class="modal-actions justify-content-center">
                            <button class="btn btn-primary" id="okSuccessModalBtn">OK</button>
                        </div>
                    </div>
                </div>

                <%-- MODAL THÔNG BÁO THẤT BẠI --%>
                <div id="errorMessageModal" class="modal">
                    <div class="modal-content small-modal-content">
                        <span class="close-button" id="closeErrorModalBtn">&times;</span>
                        <h3><i class="fas fa-times-circle text-danger"></i> Thất bại!</h3>
                        <p id="errorMessageText" style="font-size: 1.1em; text-align: center;"></p>
                        <div class="modal-actions justify-content-center">
                            <button class="btn btn-secondary" id="okErrorModalBtn">OK</button>
                        </div>
                    </div>
                </div>
            </div>

            <%-- MODAL ĐẶT LỊCH HẸN MỚI --%>
            <div id="newAppointmentModal" class="modal">
                <div class="modal-content large-modal-content">
                    <span class="close-button" id="closeNewAppointmentModalBtn">&times;</span>
                    <h3><i class="fas fa-plus-circle"></i> Đặt Lịch Hẹn Mới</h3>
                    <form id="newAppointmentForm">
                        <div class="form-group">
                            <label for="newDoctorName">Chọn Bác sĩ:</label>
                            <select class="form-control" id="newDoctorName" name="doctorId" required>
                                <option value="">-- Chọn bác sĩ --</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="newService">Dịch vụ:</label>
                            <select class="form-control" id="newService" name="serviceId" required>
                                <option value="">-- Chọn dịch vụ --</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="newAppointmentDate">Ngày hẹn:</label>
                            <select class="form-control" id="newAppointmentDate" name="appointmentDate" required disabled>
                                <option value="">-- Vui lòng chọn ngày --</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="newAppointmentTime">Giờ hẹn:</label>
                            <select class="form-control" id="newAppointmentTime" name="time" required disabled>
                                <option value="">-- Chọn giờ hẹn --</option>
                            </select>
                            <small id="timeSlotMessage" class="form-text text-muted" style="color: red;"></small>
                        </div>
                        <div class="form-group">
                            <label for="newNotes">Ghi chú (tùy chọn):</label>
                            <textarea class="form-control" id="newNotes" name="notes" rows="3"></textarea>
                        </div>
                        <div class="modal-actions">
                            <button type="submit" class="btn btn-primary" id="submitNewAppointmentBtn"><i class="bi bi-check-circle"></i> Đặt Lịch</button>
                            <button type="button" class="btn btn-secondary" id="cancelNewAppointmentBtn"><i class="bi bi-x-circle"></i> Hủy</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <%-- Bootstrap Bundle with Popper --%>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" xintegrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

        <%-- Truyền API_BASE_URL từ JSP vào JavaScript --%>
        <script>
            const API_BASE_URL_FROM_JSP = "<%= request.getContextPath() %>/api/patient";
            const USER_ACCOUNT_ID_FROM_JSP_VAR = "<%= currentUser != null ? currentUser.getId() : "" %>";
        </script>
        <%-- Đường dẫn JS của bạn --%>
        <script src="${pageContext.request.contextPath}/js/patient-appointment-schedule.js"></script>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Sidebar toggle
                const sidebarCollapse = document.getElementById('sidebarCollapse');
                const sidebar = document.getElementById('sidebar');
                const content = document.getElementById('content');

                if (sidebarCollapse && sidebar && content) { // Kiểm tra null trước khi thêm event listener
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
                } else {
                    console.error("Sidebar elements not found. Sidebar functionality might be impaired.");
                }
            });
        </script>
    </body>
</html>