<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.mycompany.isp490_gr3.model.Doctor" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>


<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Homepage - Ánh Dương Clinic</title>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <%-- CHẮC CHẮN ĐƯỜNG DẪN NÀY ĐÚNG --%>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
    </head>
    <body>
        <%
            // Lấy dữ liệu từ REQUEST (chỉ tồn tại trong request hiện tại)
            List<Doctor> doctorsWithoutSchedule = (List<Doctor>) request.getAttribute("doctorsWithoutSchedule");
            String scheduleToastMessage = (String) request.getAttribute("scheduleToastMessage");
            
            // Cờ boolean để kiểm soát việc hiển thị HTML của toast
            // Toast sẽ hiển thị nếu scheduleToastMessage không null và không rỗng
            boolean shouldRenderToastHtml = (scheduleToastMessage != null && !scheduleToastMessage.isEmpty());
            
            // Lưu ý: Không cần xóa session attributes ở đây nữa vì chúng ta dùng request attributes
        %>
        
        <% if (shouldRenderToastHtml) { %>
        <div class="position-fixed bottom-0 end-0 p-3" style="z-index: 1055;">
            <div id="toastDoctor" class="toast align-items-center text-dark border-warning" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="toast-header bg-warning text-dark">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    <strong class="me-auto">Thông báo</strong>
                    <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
                <div class="toast-body">
                    <p><%= scheduleToastMessage %></p>
                    <% if (doctorsWithoutSchedule != null && !doctorsWithoutSchedule.isEmpty()) { %>
                        <p class="mb-1">Chi tiết các bác sĩ:</p>
                        <ul class="mb-1">
                            <% for (Doctor d : doctorsWithoutSchedule) { %>
                            <li><%= d.getFullName() %></li>
                            <% } %>
                        </ul>
                    <% } %>
                    <a href="<%= request.getContextPath() %>/receptionist/manage-doctor-schedule" class="btn btn-sm btn-primary">Tạo lịch ngay</a>
                </div>
            </div>
        </div>
        <% } %>
        
        <%
        // Lấy vai trò người dùng để điều khiển menu (Code này không đổi)
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
        
        // Lấy thông tin người dùng (Code này không đổi)
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
                <li><a href="${pageContext.request.contextPath}/admin/medicines"><i class="bi bi-hospital"></i> Quản lý kho thuốc</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/prescriptions"><i class="bi bi-capsule"></i> Quản lý thuốc</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/medical-supplies"><i class="bi bi-gear-fill"></i> Quản lý vật tư</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/report"><i class="bi bi-bar-chart-fill"></i> Báo cáo thống kê</a></li>
                <% } else if (currentRole == User.Role.DOCTOR) { %>
                <li><a href="${pageContext.request.contextPath}/homepage"><i class="bi bi-speedometer2"></i> Trang chủ</a></li>
                <li><a href="${pageContext.request.contextPath}/queue"><i class="bi bi-calendar-check"></i> Lịch khám bệnh</a></li>
                <li><a href="${pageContext.request.contextPath}/doctor/patients"><i class="bi bi-people"></i> Hồ sơ bệnh nhân</a></li>
                <li><a href="${pageContext.request.contextPath}/doctor/report"><i class="bi bi-bar-chart-fill"></i> Báo cáo thống kê</a></li>
                <% } else if (currentRole == User.Role.RECEPTIONIST) { %>
                <li class="active"> <a href="${pageContext.request.contextPath}/homepage"><i class="bi bi-house-door-fill"></i> Trang chủ</a></li>
                <li><a href="${pageContext.request.contextPath}/appointments"><i class="bi bi-calendar-check-fill"></i> Quản lý đặt lịch</a></li>
                <li><a href="${pageContext.request.contextPath}/checkin"><i class="bi bi-person-check-fill"></i> Quản lý check-in</a></li>
                <li><a href="${pageContext.request.contextPath}/queue"><i class="bi bi-people-fill"></i> Quản lý hàng đợi</a></li>
                <li><a href="${pageContext.request.contextPath}/doctor/patients"><i class="bi bi-people"></i> Hồ sơ bệnh nhân</a></li>
                <li><a href="${pageContext.request.contextPath}/receptionist/manage-doctor-schedule"><i class="bi bi-calendar-event-fill"></i> Quản lý lịch bác sĩ</a></li>
                <li><a href="${pageContext.request.contextPath}/receptionist/report"><i class="bi bi-speedometer2"></i> Báo cáo thống kê</a></li>

                <% } else { %>
                <li class="active"><a href="${pageContext.request.contextPath}/homepage"><i class="bi bi-speedometer2"></i> Trang chủ</a></li>
                <li><a href="${pageContext.request.contextPath}/patient/my-appointments"><i class="bi bi-calendar-check"></i> Lịch hẹn của tôi</a></li>               
                <% } %>
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
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile"><i class="bi bi-person-fill"></i><span>Thông tin cá nhân</span></a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile"><i class="bi bi-key-fill"></i><span>Đổi mật khẩu</span></a></li>
                            <li><a class="dropdown-item" href="#"><i class="bi bi-gear-fill"></i><span>Cài đặt</span></a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth/logout"><i class="bi bi-box-arrow-right"></i><span>Đăng xuất</span></a></li>
                        </ul>
                    </div>
                </div>
            </nav>

            <div class="container-fluid mt-4">
                <div class="row mb-5">
                    <div class="col-12">
                        <div class="welcome-banner">
                            <div class="card-body text-center py-5 px-4">
                                <h1 class="display-3 fw-bold mb-4 welcome-title">
                                    Chào mừng đến với <span style="color: white; text-shadow: 2px 2px 4px rgba(0,0,0,0.4);">Phòng khám Ánh Dương</span>
                                </h1>
                                <p class="lead mb-4 welcome-subtitle" style="font-size: 1.3rem;">
                                    Sức khỏe của bạn là ưu tiên hàng đầu của chúng tôi
                                </p>
                                <div class="mt-4">
                                    <span class="badge bg-light text-dark px-4 py-2 me-3" style="font-size: 1rem;"><i class="bi bi-shield-check me-2"></i>An toàn</span>
                                    <span class="badge bg-light text-dark px-4 py-2 me-3" style="font-size: 1rem;"><i class="bi bi-heart me-2"></i>Chất lượng</span>
                                    <span class="badge bg-light text-dark px-4 py-2" style="font-size: 1rem;"><i class="bi bi-people me-2"></i>Tận tâm</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row mb-5">
                    <div class="col-lg-6 mb-4">
                        <div class="enhanced-card h-100 animate-fadeInUp">
                            <div class="card-body p-5">
                                <h3 class="card-title text-primary-custom mb-4"><i class="bi bi-hospital me-2"></i>Về Phòng khám Ánh Dương</h3>
                                <p class="card-text text-secondary-custom mb-4" style="line-height: 1.7;">
                                    Phòng khám Ánh Dương được thành lập với sứ mệnh mang đến dịch vụ chăm sóc sức khỏe chất lượng cao, 
                                    tận tâm và chuyên nghiệp cho cộng đồng.
                                    Với đội ngũ bác sĩ giàu kinh nghiệm và trang thiết bị hiện đại, 
                                    chúng tôi cam kết cung cấp dịch vụ y tế tốt nhất.
                                </p>
                                <div class="row mt-4">
                                    <div class="col-6">
                                        <div class="text-center p-3 rounded" style="background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);"><h3 class="stat-number mb-2">10+</h3><small class="text-secondary-custom fw-semibold">Năm kinh nghiệm</small></div>
                                    </div>
                                    <div class="col-6">
                                        <div class="text-center p-3 rounded" style="background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);"><h3 class="stat-number mb-2">5000+</h3><small class="text-secondary-custom fw-semibold">Bệnh nhân tin tưởng</small></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-6 mb-4">
                        <div class="enhanced-card h-100 animate-fadeInUp">
                            <div class="card-body p-5">
                                <h3 class="card-title text-primary-custom mb-4"><i class="bi bi-geo-alt me-2"></i>Thông tin liên hệ</h3>
                                <div class="contact-info">
                                    <div class="d-flex align-items-center mb-4 contact-item p-3">
                                        <i class="bi bi-building text-primary-custom me-3" style="font-size: 1.4rem;"></i>
                                        <div><strong class="d-block text-dark mb-1">Địa chỉ:</strong><span class="text-secondary-custom">123 Đường ABC, Phường XYZ, Quận 1, TP.HCM</span></div>
                                    </div>
                                    <div class="d-flex align-items-center mb-4 contact-item p-3">
                                        <i class="bi bi-telephone text-primary-custom me-3" style="font-size: 1.4rem;"></i>
                                        <div><strong class="d-block text-dark mb-1">Điện thoại:</strong><span class="text-secondary-custom">(028) 1234 5678</span></div>
                                    </div>
                                    <div class="d-flex align-items-center mb-4 contact-item p-3">
                                        <i class="bi bi-envelope text-primary-custom me-3" style="font-size: 1.4rem;"></i>
                                        <div><strong class="d-block text-dark mb-1">Email:</strong><span class="text-secondary-custom">info@anhduongclinic.com</span></div>
                                    </div>
                                    <div class="d-flex align-items-center contact-item p-3">
                                        <i class="bi bi-clock text-primary-custom me-3" style="font-size: 1.4rem;"></i>
                                        <div><strong class="d-block text-dark mb-1">Giờ làm việc:</strong><span class="text-secondary-custom">Thứ 2 - Thứ 7: 8:00 - 20:00</span><br><span class="text-secondary-custom">Chủ nhật: 8:00 - 17:00</span></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row mb-5 pattern-bg py-5">
                    <div class="col-12"><h3 class="text-center section-header text-primary-custom"><i class="bi bi-heart-pulse me-2"></i>Dịch vụ nổi bật</h3></div>
                    <div class="col-lg-3 col-md-6 mb-4">
                        <div class="service-card text-center h-100">
                            <div class="card-body p-4">
                                <div class="service-icon mb-4"><i class="bi bi-heart-pulse text-primary-custom" style="font-size: 3.5rem;"></i></div>
                                <h5 class="card-title text-dark fw-bold mb-3">Khám tổng quát</h5>
                                <p class="card-text text-secondary-custom">Khám sức khỏe định kỳ và tư vấn y tế chuyên nghiệp với đội ngũ bác sĩ giàu kinh nghiệm</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-3 col-md-6 mb-4">
                        <div class="service-card text-center h-100">
                            <div class="card-body p-4">
                                <div class="service-icon mb-4"><i class="bi bi-prescription2 text-primary-custom" style="font-size: 3.5rem;"></i></div>
                                <h5 class="card-title text-dark fw-bold mb-3">Tư vấn thuốc</h5>
                                <p class="card-text text-secondary-custom">Hướng dẫn sử dụng thuốc an toàn và hiệu quả cho mọi lứa tuổi</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-3 col-md-6 mb-4">
                        <div class="service-card text-center h-100">
                            <div class="card-body p-4">
                                <div class="service-icon mb-4"><i class="bi bi-calendar-check text-primary-custom" style="font-size: 3.5rem;"></i></div>
                                <h5 class="card-title text-dark fw-bold mb-3">Đặt lịch online</h5>
                                <p class="card-text text-secondary-custom">Đặt lịch khám dễ dàng, tiện lợi 24/7 qua hệ thống trực tuyến</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-3 col-md-6 mb-4">
                        <div class="service-card text-center h-100">
                            <div class="card-body p-4">
                                <div class="service-icon mb-4"><i class="bi bi-shield-check text-primary-custom" style="font-size: 3.5rem;"></i></div>
                                <h5 class="card-title text-dark fw-bold mb-3">Bảo mật thông tin</h5>
                                <p class="card-text text-secondary-custom" style="line-height: 1.6;">Cam kết bảo mật tuyệt đối thông tin cá nhân và y tế của bệnh nhân</p>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row mb-5">
                    <div class="col-12 mb-5"><h3 class="text-center section-header text-primary-custom"><i class="bi bi-star me-2"></i>Tại sao chọn Phòng khám Ánh Dương?</h3></div>
                    <div class="col-lg-4 mb-4">
                        <div class="text-center p-4">
                            <div class="feature-icon mb-4"><i class="bi bi-people-fill text-primary-custom" style="font-size: 4.5rem;"></i></div>
                            <h5 class="fw-bold text-dark mb-3">Đội ngũ chuyên nghiệp</h5>
                            <p class="card-text text-secondary-custom" style="line-height: 1.6;">Bác sĩ có trình độ cao, nhiều năm kinh nghiệm trong lĩnh vực y tế, được đào tạo bài bản và cập nhật kiến thức thường xuyên</p>
                        </div>
                    </div>
                    <div class="col-lg-4 mb-4">
                        <div class="text-center p-4">
                            <div class="feature-icon mb-4"><i class="bi bi-gear-fill text-primary-custom" style="font-size: 4.5rem;"></i></div>
                            <h5 class="fw-bold text-dark mb-3">Trang thiết bị hiện đại</h5>
                            <p class="card-text text-secondary-custom" style="line-height: 1.6;">Đầu tư trang thiết bị y tế tiên tiến nhập khẩu từ các nước phát triển, đảm bảo chính xác trong chẩn đoán và điều trị</p>
                        </div>
                    </div>
                    <div class="col-lg-4 mb-4">
                        <div class="text-center p-4">
                            <div class="feature-icon mb-4"><i class="bi bi-heart-fill text-primary-custom" style="font-size: 4.5rem;"></i></div>
                            <h5 class="fw-bold text-dark mb-3">Dịch vụ tận tâm</h5>
                            <p class="card-text text-secondary-custom" style="line-height: 1.6;">Luôn đặt sức khỏe và sự hài lòng của bệnh nhân lên hàng đầu, phục vụ với tinh thần trách nhiệm cao</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const toastEl = document.getElementById("toastDoctor");
                // Lấy cờ từ request attribute
                // SỬA ĐỔI DÒNG NÀY ĐỂ ĐẢM BẢO CHÍNH XÁC TRUE/FALSE
                const shouldShowToastNow = <%= request.getAttribute("scheduleToastMessage") != null && !((String)request.getAttribute("scheduleToastMessage")).isEmpty() %>; 
                console.log("homepage.jsp: shouldShowToastNow =", shouldShowToastNow);

                if (toastEl && shouldShowToastNow) {
                    const toast = new bootstrap.Toast(toastEl, {delay: 10000});
                    toast.show();
                    toastEl.addEventListener('hidden.bs.toast', function () {
                        toastEl.remove();
                    });
                }
            });
        </script>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const sidebarCollapse = document.getElementById('sidebarCollapse');
                const sidebar = document.getElementById('sidebar');
                const content = document.getElementById('content');

                sidebarCollapse.addEventListener('click', function () {
                    sidebar.classList.toggle('active');
                    content.classList.toggle('active');
                });

                function checkWidth() {
                    if (window.innerWidth <= 768) {
                        sidebar.classList.add('active'); 
                        content.classList.add('active'); 
                    } else {
                        sidebar.classList.remove('active'); 
                        content.classList.remove('active'); 
                    }
                }

                checkWidth();
                window.addEventListener('resize', checkWidth);

                setTimeout(() => {
                    const allIcons = document.querySelectorAll('#sidebar i, .top-navbar i');
                    allIcons.forEach(icon => {
                        void icon.offsetWidth; 
                    });
                }, 500);
            });
        </script>

    </body>
</html>