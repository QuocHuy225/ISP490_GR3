<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="com.mycompany.isp490_gr3.model.Appointment" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Lịch hẹn</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/appointment.css">
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
                        <i class="bi bi-speedometer2"></i> Trang chủ
                    </a>
                </li>
                <li class="active">
                    <a href="${pageContext.request.contextPath}/appointments">
                        <i class="bi bi-calendar-check"></i> Quản lý lịch hẹn
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

            <ul class="nav nav-tabs justify-content-center">
                <li class="nav-item">
                    <a class="nav-link active" href="#">Quản lý Lịch hẹn</a>
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

            <form id="searchForm" action="appointments" method="get" class="mb-4 mt-3 p-3 border rounded shadow-sm bg-light">
                <div class="row mb-3">
                    <div class="col-md-4">
                        <label for="appointmentCode" class="form-label">Mã lịch hẹn</label>
                        <input type="text" class="form-control" id="appointmentCode" name="appointmentCode" placeholder="Nhập mã lịch hẹn"
                               value="${appointmentCode}">
                    </div>
                    <div class="col-md-4">
                        <label for="patientId" class="form-label">Mã bệnh nhân</label>
                        <input type="text" class="form-control" id="patientId" name="patientId" placeholder="Nhập mã bệnh nhân"
                               value="${patientId}">
                    </div>
                    <div class="col-md-4">
                        <label for="doctorId" class="form-label">Mã bác sĩ</label>
                        <input type="text" class="form-control" id="doctorId" name="doctorId" placeholder="Nhập mã bác sĩ"
                               value="${doctorId}">
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="col-md-4">
                        <label for="status" class="form-label">Trạng thái</label>
                        <select class="form-select" id="status" name="status">
                            <option value="all" ${empty status || status eq 'all' ? 'selected' : ''}>--Tất cả--</option>
                            <option value="pending" ${'pending' eq status ? 'selected' : ''}>Đang chờ</option>
                            <option value="confirmed" ${'confirmed' eq status ? 'selected' : ''}>Đã xác nhận</option>
                            <option value="done" ${'done' eq status ? 'selected' : ''}>Hoàn thành</option>
                            <option value="cancelled" ${'cancelled' eq status ? 'selected' : ''}>Đã hủy</option>
                        </select>
                    </div>
                    <div class="col-md-4 d-flex align-items-end">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="showDeleted" name="showDeleted" value="true"
                                   ${showDeleted ? 'checked' : ''}>
                            <label class="form-check-label" for="showDeleted">
                                Hiển thị lịch hẹn đã xóa
                            </label>
                        </div>
                    </div>
                    <div class="col-md-4"></div>
                </div>

                <div class="d-flex justify-content-end gap-2">
                    <button type="reset" class="btn btn-light border" id="resetFilterButton">
                        <i class="bi bi-arrow-clockwise"></i> Đặt lại bộ lọc
                    </button>
                    <button type="submit" name="submitSearch" class="btn btn-primary">
                        <i class="bi bi-search"></i> Tìm kiếm
                    </button>
                </div>
            </form>
            <div class="appointment-list-section animate-fade-in">  
                <div class="appointment-list-header">
                    <h5>Danh sách Lịch hẹn (${totalRecords} kết quả)</h5>
                    <div class="d-flex gap-2">
                        <form id="deleteMultipleForm" action="${pageContext.request.contextPath}/appointments" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="deleteMultiple">
                            <button type="submit" class="btn btn-danger btn-delete-selected">
                                <i class="bi bi-trash"></i> Xóa đã chọn
                            </button>
                        </form>
                        <button type="button" class="btn btn-success btn-add-appointment" data-bs-toggle="modal" data-bs-target="#addAppointmentModal">
                            <i class="bi bi-plus-circle"></i> Thêm Lịch hẹn
                        </button>
                    </div>
                </div>

                <div class="table-responsive">
                    <table class="table table-hover table-bordered table-appointments">
                        <thead class="table-primary">
                            <tr>
                                <th><input type="checkbox" id="checkAll"></th>
                                <th>Mã lịch hẹn</th>
                                <th>Ngày khám</th>
                                <th>Slot khám</th>
                                <th>Mã bệnh nhân</th>
                                <th>Họ và tên</th>
                                <th>Số điện thoại</th>
                                <th>Địa chỉ</th>
                                <th>Bác sĩ phụ trách</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="appointment" items="${currentPageAppointments}">
                                <tr>
                                    <td><input type="checkbox" name="selectedAppointments" value="${appointment.id}" form="deleteMultipleForm"></td>
                                    <td>${appointment.appointmentCode}</td>
                                    <td>${appointment.appointmentDate}</td>
                                    <td>${appointment.appointmentTime}</td>
                                    <td>${appointment.patientId}</td>
                                    <td>${appointment.patientName}</td>
                                    <td>${appointment.patientPhoneNumber}</td>
                                    <td>${appointment.patientAddress}</td>
                                    <td>${appointment.doctorName}</td>
                                    <td>${appointment.status}</td>
                                    <td>
                                        <button type="button" class="btn btn-action btn-edit" 
                                                data-bs-toggle="modal" 
                                                data-bs-target="#updateAppointmentModal"
                                                data-id="${appointment.id}"
                                                data-code="${appointment.appointmentCode}"
                                                data-patient-id="${appointment.patientId}"
                                                data-doctor-id="${appointment.doctorId}"
                                                data-slot-id="${appointment.slotId}"
                                                data-status="${appointment.status}"
                                                title="Chỉnh sửa">
                                            <i class="bi bi-pencil-square"></i>
                                        </button>
                                        <button type="button" class="btn btn-action btn-danger btn-delete-single" 
                                                data-id="${appointment.id}"
                                                data-bs-toggle="modal" 
                                                data-bs-target="#confirmSingleDeleteModal"
                                                title="Xóa">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty currentPageAppointments && requestScope.searchPerformed}">
                                <tr>
                                    <td colspan="11" class="text-center">Không tìm thấy lịch hẹn nào.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>

                <div class="pagination-container">
                    <div class="pagination-info">
                        Hiển thị ${totalRecords > 0 ? startIndex + 1 : 0} - ${endIndex} trong ${totalRecords} kết quả
                    </div>
                    <nav aria-label="Page navigation">
                        <ul class="pagination justify-content-end">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="appointments?page=${currentPage - 1}<%= generateFilterParams(request) %>" aria-label="Previous">
                                    <span aria-hidden="true">&laquo;</span>
                                </a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="appointments?page=${i}<%= generateFilterParams(request) %>">${i}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage == totalPages || totalPages == 0 ? 'disabled' : ''}">
                                <a class="page-link" href="appointments?page=${currentPage + 1}<%= generateFilterParams(request) %>" aria-label="Next">
                                    <span aria-hidden="true">&raquo;</span>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>

            <div id="noResultsMessage" class="alert alert-info text-center mt-3 animate-fade-in" role="alert" style="display: none;">
            </div>

        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Các biến này được đặt từ Controller của bạn
            window.GLOBAL_IS_SEARCH_PERFORMED = ${requestScope.searchPerformed != null ? requestScope.searchPerformed : false};
            window.GLOBAL_HAS_RESULTS = ${requestScope.hasResults != null ? requestScope.hasResults : false};
        </script>
        <script src="${pageContext.request.contextPath}/js/appointment.js"></script>

        <%-- Modal Thêm lịch hẹn mới (Giữ nguyên) --%>
        <div class="modal fade" id="addAppointmentModal" tabindex="-1" aria-labelledby="addAppointmentModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addAppointmentModalLabel">Thêm lịch hẹn mới</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="addAppointmentForm" action="${pageContext.request.contextPath}/appointments" method="post">
                            <input type="hidden" name="action" value="addAppointment">

                            <div class="mb-3">
                                <label for="addAppointmentCode" class="form-label">Mã lịch hẹn</label>
                                <input type="text" class="form-control" id="addAppointmentCode" name="appointmentCode" placeholder="Mã lịch hẹn">
                            </div>

                            <div class="mb-3">
                                <label for="addPatientId" class="form-label">Mã bệnh nhân <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="addPatientId" name="patientId" required placeholder="Nhập mã bệnh nhân">
                            </div>

                            <div class="mb-3">
                                <label for="addDoctorId" class="form-label">Bác sĩ phụ trách <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="addDoctorId" name="doctorId" required placeholder="Nhập mã bác sĩ">
                            </div>

                            <div class="mb-3">
                                <label for="addSlotId" class="form-label">Slot <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="addSlotId" name="slotId" required placeholder="Nhập mã slot">
                            </div>

                            <div class="mb-3">
                                <label for="addStatus" class="form-label">Trạng thái</label>
                                <select class="form-select" id="addStatus" name="status">
                                    <option value="pending">Đang chờ</option>
                                    <option value="confirmed">Đã xác nhận</option>
                                    <option value="done">Hoàn thành</option>
                                    <option value="cancelled">Đã hủy</option>
                                </select>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" form="addAppointmentForm" class="btn btn-primary">Thêm</button>
                    </div>
                </div>
            </div>
        </div>

        <%-- NEW Modal Cập nhật lịch hẹn --%>
        <div class="modal fade" id="updateAppointmentModal" tabindex="-1" aria-labelledby="updateAppointmentModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="updateAppointmentModalLabel">Cập nhật lịch hẹn</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="updateAppointmentForm" action="${pageContext.request.contextPath}/appointments" method="post">
                            <input type="hidden" name="action" value="updateAppointment">
                            <input type="hidden" name="id" id="updateAppointmentId">

                            <div class="mb-3">
                                <label for="updateAppointmentCode" class="form-label">Mã lịch hẹn</label>
                                <input type="text" class="form-control" id="updateAppointmentCode" name="appointmentCode" placeholder="Mã lịch hẹn" readonly disable>
                            </div>

                            <div class="mb-3">
                                <label for="updatePatientId" class="form-label">Mã bệnh nhân <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="updatePatientId" name="patientId" required placeholder="Nhập mã bệnh nhân">
                            </div>

                            <div class="mb-3">
                                <label for="updateDoctorId" class="form-label">Bác sĩ phụ trách <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="updateDoctorId" name="doctorId" required placeholder="Nhập mã bác sĩ">
                            </div>

                            <div class="mb-3">
                                <label for="updateSlotId" class="form-label">Slot <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="updateSlotId" name="slotId" required placeholder="Nhập mã slot">
                            </div>

                            <div class="mb-3">
                                <label for="updateStatus" class="form-label">Trạng thái</label>
                                <select class="form-select" id="updateStatus" name="status">
                                    <option value="pending">Đang chờ</option>
                                    <option value="confirmed">Đã xác nhận</option>
                                    <option value="done">Hoàn thành</option>
                                    <option value="cancelled">Đã hủy</option>
                                </select>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" form="updateAppointmentForm" class="btn btn-primary">Cập nhật</button>
                    </div>
                </div>
            </div>
        </div>

        <%-- Modal xác nhận xóa nhiều (Giữ nguyên) --%>
        <div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-labelledby="confirmDeleteModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="confirmDeleteModalLabel">Xác nhận xóa</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        Bạn có chắc chắn muốn xóa <span id="deleteCount"></span> lịch hẹn đã chọn không?
                        Hành động này sẽ xóa mềm các bản ghi.
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-danger" id="confirmDeleteButton">Xóa</button>
                    </div>
                </div>
            </div>
        </div>

        <%-- NEW Modal xác nhận xóa TỪNG LỊCH HẸN --%>
        <div class="modal fade" id="confirmSingleDeleteModal" tabindex="-1" aria-labelledby="confirmSingleDeleteModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="confirmSingleDeleteModalLabel">Xác nhận xóa lịch hẹn</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        Bạn có chắc chắn muốn xóa lịch hẹn có ID: <span id="singleDeleteAppointmentIdSpan"></span> không?
                        Hành động này sẽ xóa mềm bản ghi.
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-danger" id="confirmSingleDeleteButton">Xóa</button>
                    </div>
                </div>
            </div>
        </div>


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
