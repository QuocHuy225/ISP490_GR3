<%-- 
    Document   : checkin
    Created on : Jun 25, 2025, 12:17:06 AM
    Author     : FPT SHOP
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="com.mycompany.isp490_gr3.model.Appointment" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý check-in</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/checkin.css">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

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
                     // Fallback for String role if User.Role enum is not used directly
                     isAdmin = "ADMIN".equalsIgnoreCase(userRole.toString());
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
                <li class="active">
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
                <li>
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

            <!-- Breadcrumb được căn giữa theo chiều dọc -->
            <div class="content-wrapper">
                <div class="breadcrumb-container">
                    <nav aria-label="breadcrumb" style="margin-top:17px;">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/homepage">Trang chủ</a></li>
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/appointments">Quản lý đặt lịch</a></li>
                            <li class="breadcrumb-item active" aria-current="page">Check-in</li>
                        </ol>
                    </nav>
                </div>
            </div>                          
            <ul class="nav nav-tabs w-100">
                <li class="nav-item flex-fill text-center">
                    <a class="nav-link" href="${pageContext.request.contextPath}/appointments">Lịch hẹn</a>
                </li>
                <li class="nav-item flex-fill text-center">
                    <a class="nav-link active" href="${pageContext.request.contextPath}/checkin">Check-in</a>
                </li>
                <li class="nav-item flex-fill text-center">
                    <a class="nav-link" href="${pageContext.request.contextPath}/slot">Slot</a>
                </li>
            </ul>

            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show mt-3" role="alert">
                    ${sessionScope.message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <% 
                    // Xóa thông báo khỏi session sau khi hiển thị để tránh hiển thị lại
                    session.removeAttribute("message");
                    session.removeAttribute("messageType");
                %>
            </c:if>



            <form id="searchForm" action="${pageContext.request.contextPath}/checkin" method="post" class="mb-4 mt-3 p-3 border rounded shadow-sm bg-light">
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label for="appointmentCode" class="form-label">Mã lịch hẹn</label>
                        <input type="text" class="form-control" id="appointmentCode" name="appointmentCode" placeholder="Nhập" value="${param.appointmentCode}">
                    </div>
                    <!--                    <div class="col-md-4">
                                            <label for="slotDate" class="form-label">Ngày hẹn</label>
                                            <input type="date" class="form-control" id="slotDate" name="slotDate" value="${empty slotDate ? LocalDate.now().toString() : slotDate}"/>
                                        </div>-->
                    <div class="col-md-6">
                        <label for="patientCode" class="form-label">Mã bệnh nhân</label>
                        <input type="text" class="form-control" id="patientCode" name="patientCode" placeholder="Nhập" value="${param.patientCode}">
                    </div>
                    <div class="col-md-6">
                        <label for="doctorId" class="form-label">Bác sĩ</label>
                        <select id="doctorId" name="doctorId" class="form-select">
                            <option value="">--Chọn--</option>

                            <c:forEach var="d" items="${doctorList}">
                                <option value="${d.id}" <c:if test="${param.doctorId == d.id}">selected</c:if>>${d.fullName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label for="servicesId" class="form-label">Dịch vụ</label>
                        <select id="servicesId" name="servicesId" class="form-select">
                            <option value="">--Chọn--</option>
                            <c:forEach var="s" items="${serviceList}">
                                <option value="${s.servicesId}">${s.serviceName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <!--                    <div class="col-md-4">
                                            <label for="status" class="form-label">Trạng thái</label>
                                            <select id="status" name="status" class="form-select">
                                                <option value="">--Chọn--</option>
                    <c:forEach var="status" items="${statusList}">
                        <option value="${status}" <c:if test="${param.status == status}">selected</c:if>>
                        <c:choose>
                            <c:when test="${status == 'pending'}">Chờ xác nhận</c:when>
                            <c:when test="${status == 'confirmed'}">Đã xác nhận</c:when>
                            <c:when test="${status == 'done'}">Đã khám</c:when>
                            <c:when test="${status == 'cancelled'}">Đã hủy</c:when>
                            <c:otherwise>${status}</c:otherwise>
                        </c:choose>
                    </option>
                    </c:forEach>
                </select>
            </div>-->
                </div>
                <div class="d-flex justify-content-end gap-2">
                    <button type="button" class="btn btn-light border" id="resetFilterButton">
                        <i class="bi bi-arrow-clockwise"></i> Đặt lại bộ lọc
                    </button>
                    <button type="submit" name="submitSearch" class="btn btn-primary">
                        <i class="bi bi-search"></i> Tìm kiếm
                    </button>
                </div>
            </form>

            <div class="row">
                <div id="appointment-list-section" class="${not empty selectedAppointment ? 'col-md-8' : 'col-md-12'}">
                    <div class="appointment-list-section animate-fade-in">  
                        <div class="appointment-list-header">
                            <h5>Danh sách lịch check-in ${empty slotDate ? LocalDate.now().toString() : slotDate}  (${totalRecords} kết quả)</h5>
                        </div>

                        <div class="table-responsive">
                            <table class="table table-hover table-bordered table-appointments">
                                <thead class="table-primary">
                                    <tr>
                                        <th>STT</th>
                                        <th>Mã lịch hẹn</th>
                                        <th>Ngày khám</th>
                                        <th>Slot khám</th>
                                        <th>Bác sĩ</th>
                                        <th>Mã bệnh nhân</th>
                                        <th>Bệnh nhân</th>
                                        <th>Số điện thoại</th>
                                        <th>Dịch vụ</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="appointment" items="${currentPageAppointments}" varStatus="status">
                                        <tr>
                                            <td>${startIndex + status.index + 1}</td>
                                            <td>${appointment.appointmentCode}</td>
                                            <td>${appointment.slotDate}</td>
                                            <td>${appointment.slotTimeRange}</td>
                                            <td>${appointment.doctorName}</td>
                                            <td>${appointment.patientCode}</td>
                                            <td>${appointment.patientName}</td>
                                            <td>${appointment.patientPhone}</td>
                                            <td>${appointment.serviceName}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${appointment.status == 'pending'}">Chờ xác nhận</c:when>
                                                    <c:when test="${appointment.status == 'confirmed'}">Đã xác nhận</c:when>
                                                    <c:when test="${appointment.status == 'done'}">Đã khám</c:when>
                                                    <c:when test="${appointment.status == 'cancelled'}">Đã hủy</c:when>
                                                    <c:otherwise>${appointment.status}</c:otherwise>
                                                </c:choose>
                                            </td>

                                            <td>

                                                <button type="button" class="btn btn-sm btn-outline-primary select-btn"
                                                        data-id="${appointment.id}"
                                                        data-code="${appointment.appointmentCode}"
                                                        data-name="${appointment.patientName}"
                                                        data-date="${appointment.slotDate}"
                                                        data-time="${appointment.slotTimeRange}">
                                                    <i class="bi bi-check-circle"></i> Chọn
                                                </button>

                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <c:if test="${empty currentPageAppointments}">
                                        <tr>
                                            <td colspan="13" class="text-center text-muted">Không có lịch hẹn nào được tìm thấy.</td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>





                        <!-- Phân trang -->
                        <div class="pagination-container">
                            <div class="pagination-info">
                                Hiển thị ${totalRecords > 0 ? startIndex + 1 : 0} - ${endIndex} trong ${totalRecords} kết quả
                            </div>
                            <nav aria-label="Page navigation">
                                <ul class="pagination justify-content-end">
                                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/checkin?page=${currentPage - 1}${filterParams}" aria-label="Previous">
                                            <span aria-hidden="true">«</span>
                                        </a>
                                    </li>
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/checkin?page=${i}${filterParams}">${i}</a>
                                        </li>
                                    </c:forEach>
                                    <li class="page-item ${currentPage == totalPages || totalPages == 0 ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/checkin?page=${currentPage + 1}${filterParams}" aria-label="Next">
                                            <span aria-hidden="true">»</span>
                                        </a>
                                    </li>
                                </ul>
                            </nav>
                        </div>
                    </div>
                </div>



                <div id="checkin-form-section" class="col-md-4" style="display: none;">
                    <h5>Check-in lịch hẹn</h5>
                    <form id="checkinForm" action="${pageContext.request.contextPath}/checkin/confirm" method="post" class="p-3 border rounded bg-light">
                        <input type="hidden" name="action" value="doCheckin"/>
                        <input type="hidden" name="appointmentId" id="appointmentId"/>

                        <div class="mb-3">
                            <label class="form-label">
                                Độ ưu tiên
                                <i class="bi bi-info-circle text-primary" data-bs-toggle="tooltip" title="Chỉ chọn 'Cao' nếu là trường hợp khẩn cấp. Mặc định là Trung bình."></i>
                            </label>
                            <select class="form-select" id="priority" name="priority">
                                <option value="" selected>Trung bình (mặc định)</option>
                                <option value="High">Cao</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">
                                Ghi chú 
                                <i class="bi bi-info-circle text-primary" data-bs-toggle="tooltip" title="Ghi chú thêm nếu có yêu cầu đặc biệt hoặc đã chọn ưu tiên Cao."></i>
                            </label>
                            <textarea name="note" id="note" class="form-control" rows="3" placeholder="Ví dụ: ưu tiên khám nhanh"></textarea>
                        </div>

                        <div class="d-flex justify-content-end">
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check2-circle"></i> Xác nhận Check-in
                            </button>
                        </div>
                    </form>
                </div>

            </div>



            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    const selectButtons = document.querySelectorAll(".select-btn");
                    const formSection = document.getElementById("checkin-form-section");
                    const appointmentList = document.getElementById("appointment-list-section");
                    const appointmentIdInput = document.getElementById("appointmentId");
                    const prioritySelect = document.getElementById("priority");
                    const noteTextarea = document.getElementById("note");
                    const form = document.getElementById("checkinForm");

                    let lastSelectedCode = null;

                    // Xử lý khi click nút Chọn
                    selectButtons.forEach(btn => {
                        btn.addEventListener("click", () => {
                            const id = btn.dataset.id;
                            const code = btn.dataset.code;

                            // Toggle form hiển thị/ẩn nếu bấm lại cùng dòng
                            if (lastSelectedCode === code && formSection.style.display === "block") {
                                formSection.style.display = "none";
                                appointmentList.classList.remove("col-md-8");
                                appointmentList.classList.add("col-md-12");
                                lastSelectedCode = null;
                                return;
                            }

                            // Gán dữ liệu cần thiết vào form
                            appointmentIdInput.value = id;
                            prioritySelect.value = ""; // Reset về mặc định
                            noteTextarea.value = "";   // Xóa ghi chú cũ nếu có

                            // Hiện form và co danh sách
                            formSection.style.display = "block";
                            appointmentList.classList.remove("col-md-12");
                            appointmentList.classList.add("col-md-8");

                            lastSelectedCode = code;
                            formSection.scrollIntoView({behavior: "smooth"});
                        });
                    });

                    // Hiển thị tooltip Bootstrap
                    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
                    tooltipTriggerList.forEach(function (tooltipTriggerEl) {
                        new bootstrap.Tooltip(tooltipTriggerEl);
                    });

                    // Validate form trước khi submit
                    form.addEventListener("submit", function (e) {
                        const priority = prioritySelect.value;
                        const note = noteTextarea.value.trim();

                        if (priority === "High" && note === "") {
                            e.preventDefault();
                            alert("Vui lòng nhập ghi chú nếu chọn độ ưu tiên Cao.");
                            noteTextarea.focus();
                        }
                    });
                });
            </script>


            <script>
                document.getElementById("resetFilterButton").addEventListener("click", function () {
                    const form = document.getElementById("searchForm");
                    form.reset();
                    form.action = "${pageContext.request.contextPath}/checkin"; // Reset lại action nếu bị override
                });
                // Các biến này được đặt từ Controller của bạn
                window.GLOBAL_IS_SEARCH_PERFORMED = ${requestScope.searchPerformed != null ? requestScope.searchPerformed : false};
                window.GLOBAL_HAS_RESULTS = ${requestScope.hasResults != null ? requestScope.hasResults : false};
            </script>

            <script src="${pageContext.request.contextPath}/js/checkin.js"></script>

            <%!
                   // Hàm trợ giúp để tạo lại các tham số lọc cho phân trang
                   // Lấy các giá trị đã được Controller setAttribute (là các param gốc từ người dùng)
                   private String generateFilterParams(HttpServletRequest request) {
                       StringBuilder params = new StringBuilder();

                       // Lấy các giá trị đã được Controller setAttribute
                       String appointmentCode = (String) request.getAttribute("appointmentCode");
                       if (appointmentCode != null && !appointmentCode.isEmpty()) {
                           params.append("&appointmentCode=").append(appointmentCode);
                       }
                       String patientId = (String) request.getAttribute("patientId"); // Đây là patient ID String
                       if (patientId != null && !patientId.isEmpty()) {
                           params.append("&patientId=").append(patientId);
                       }
                       String doctorId = (String) request.getAttribute("doctorId"); // Đây là doctor ID String
                       if (doctorId != null && !doctorId.isEmpty()) {
                           params.append("&doctorId=").append(doctorId);
                       }
                       String status = (String) request.getAttribute("status");
                       if (status != null && !status.isEmpty()) {
                           params.append("&status=").append(status);
                       }
                       Boolean showDeleted = (Boolean) request.getAttribute("showDeleted");
                       if (showDeleted != null && showDeleted) { // Chỉ thêm nếu là true
                           params.append("&showDeleted=").append(showDeleted);
                       }
            
                       // recordsPerPage có thể lấy từ request.getAttribute vì Controller cũng set nó
                       Integer rp = (Integer) request.getAttribute("recordsPerPage");
                       if (rp != null) {
                           params.append("&recordsPerPage=").append(rp);
                       }

                       // IMPORTANT: Always append submitSearch=true for pagination links
                       // This ensures the Controller always considers it a "search" request
                       // when navigating between pages, even if no explicit filter changed.
                       params.append("&submitSearch=true");

                       return params.toString();
                   }
            %>
    </body>
</html>