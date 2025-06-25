<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý Slot Check-in</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/appointment.css">
</head>
<body>
    <%
        // Kiểm tra phân quyền (hiển thị menu cho Admin)
        Object userRole = session.getAttribute("userRole");
        boolean isAdmin = false;
        if (userRole != null) {
            if (userRole instanceof User.Role) {
                isAdmin = ((User.Role) userRole) == User.Role.ADMIN;
            } else {
                isAdmin = "ADMIN".equalsIgnoreCase(userRole.toString());
            }
        }
        
        // Lấy thông tin người dùng
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
            <li><a href="${pageContext.request.contextPath}/homepage"><i class="bi bi-speedometer2"></i> Trang chủ</a></li>
            <li><a href="${pageContext.request.contextPath}/appointments"><i class="bi bi-calendar-check"></i> Quản lý đặt lịch</a></li>
            <li><a href="${pageContext.request.contextPath}/checkin"><i class="bi bi-calendar-check"></i> Quản lý check-in</a></li>
            <li><a href="${pageContext.request.contextPath}/#"><i class="bi bi-calendar-check"></i> Quản lý hàng đợi</a></li>
            <li><a href="${pageContext.request.contextPath}/#"><i class="bi bi-speedometer2"></i> Quản lý bệnh nhân</a></li>
            <li><a href="${pageContext.request.contextPath}/#"><i class="bi bi-speedometer2"></i> Quản lý lịch bác sĩ</a></li>
        </ul>
    </nav>
    
    <div id="content">
        <!-- Navbar -->
        <nav class="navbar navbar-expand-lg top-navbar">
            <div class="container-fluid">
                <button type="button" id="sidebarCollapse" class="btn btn-primary"><i class="bi bi-list"></i></button>
                <div style="margin-left: 60px;">
                    <h3><span style="color: #007bff;">Ánh Dương</span> <span style="color: #333;">Clinic</span></h3>
                </div>
                <div class="navbar-search mx-auto">
                    <i class="bi bi-search"></i>
                    <input type="text" class="form-control" placeholder="Tìm kiếm slot, bác sĩ...">
                </div>
                <div class="dropdown user-dropdown">
                    <button class="btn dropdown-toggle" type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                        <div class="user-profile-icon"><i class="bi bi-person-fill" style="font-size: 1.5rem;"></i></div>
                        <div class="user-info d-none d-md-block">
                            <div class="user-name"><%= userName %></div>
                            <div class="user-role"><%= userRoleDisplay %></div>
                        </div>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                        <% if (isAdmin) { %>
                        <li>
                            <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/authorization">
                                <i class="bi bi-people-fill"></i> Quản lý người dùng
                            </a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <% } %>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile"><i class="bi bi-person-fill"></i> Thông tin cá nhân</a></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile"><i class="bi bi-key-fill"></i> Đổi mật khẩu</a></li>
                        <li><a class="dropdown-item" href="#"><i class="bi bi-gear-fill"></i> Cài đặt</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth/logout"><i class="bi bi-box-arrow-right"></i> Đăng xuất</a></li>
                    </ul>
                </div>
            </div>
        </nav>

        <!-- Breadcrumb -->
        <div class="content-wrapper">
            <div class="breadcrumb-container">
                <nav aria-label="breadcrumb" style="margin-top:17px;">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/homepage">Trang chủ</a></li>
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/appointments">Quản lý đặt lịch</a></li>
                        <li class="breadcrumb-item active" aria-current="page">Slot check-in</li>
                    </ol>
                </nav>
            </div>
        </div>

        <!-- Tabs -->
        <ul class="nav nav-tabs w-100">
            <li class="nav-item flex-fill text-center">
                <a class="nav-link" href="${pageContext.request.contextPath}/appointments">Lịch hẹn</a>
            </li>
            <li class="nav-item flex-fill text-center">
                <a class="nav-link active" href="${pageContext.request.contextPath}/slot">Slot check-in</a>
            </li>
        </ul>

        <!-- Thông báo -->
        <c:if test="${not empty sessionScope.message}">
            <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show mt-3" role="alert">
                ${sessionScope.message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <% session.removeAttribute("message"); session.removeAttribute("messageType"); %>
        </c:if>

        <!-- Form tìm kiếm -->
        <form id="searchForm" action="${pageContext.request.contextPath}/slot" method="get" class="mb-4 mt-3 p-3 border rounded shadow-sm bg-light">
            <div class="row mb-3">
                <div class="col-md-4">
                    <label for="slotCode" class="form-label">Mã slot</label>
                    <input type="text" class="form-control" id="slotCode" name="slotCode" placeholder="Nhập mã slot" value="${slotCode}">
                </div>
                <div class="col-md-4">
                    <label for="doctorId" class="form-label">Bác sĩ</label>
                    <input type="text" class="form-control" id="doctorId" name="doctorId" placeholder="Nhập mã bác sĩ" value="${doctorId}">
                </div>
                <div class="col-md-4">
                    <label for="slotDate" class="form-label">Ngày slot</label>
                    <input type="date" class="form-control" id="slotDate" name="slotDate" value="${slotDate}">
                </div>
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

        <!-- Danh sách slot -->
        <div class="slot-list-section animate-fade-in">
            <div class="slot-list-header">
                <h5>Danh sách Slot Check-in (${totalRecords} kết quả)</h5>
                <div class="d-flex gap-2">
                    <form id="deleteMultipleForm" action="${pageContext.request.contextPath}/slot" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="deleteMultiple">
                        <button type="submit" class="btn btn-danger btn-delete-selected">
                            <i class="bi bi-trash"></i> Xóa đã chọn
                        </button>
                    </form>
                    <button type="button" class="btn btn-success btn-add-slot" data-bs-toggle="modal" data-bs-target="#addSlotModal">
                        <i class="bi bi-plus-circle"></i> Thêm Slot
                    </button>
                </div>
            </div>

            <div class="table-responsive">
                <table class="table table-hover table-bordered table-slots">
                    <thead>
                        <tr>
                            <th><input type="checkbox" id="checkAll"></th>
                            <th>Mã slot</th>
                            <th>Ngày</th>
                            <th>Thời gian</th>
                            <th>Bác sĩ</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="slot" items="${currentPageSlots}">
                            <tr>
                                <td><input type="checkbox" name="selectedSlots" value="${slot.id}" form="deleteMultipleForm"></td>
                                <td>${slot.slotCode}</td>
                                <td>${slot.slotDate}</td>
                                <td>${slot.slotTime}</td>
                                <td>${slot.doctorName}</td>
                                <td>${slot.status}</td>
                                <td>
                                    <button type="button" class="btn btn-action btn-edit" 
                                            data-bs-toggle="modal" 
                                            data-bs-target="#updateSlotModal"
                                            data-id="${slot.id}"
                                            data-code="${slot.slotCode}"
                                            data-date="${slot.slotDate}"
                                            data-time="${slot.slotTime}"
                                            data-doctor-id="${slot.doctorId}"
                                            data-status="${slot.status}"
                                            title="Chỉnh sửa">
                                        <i class="bi bi-pencil-square"></i>
                                    </button>
                                    <button type="button" class="btn btn-action btn-danger btn-delete-single" 
                                            data-id="${slot.id}"
                                            data-bs-toggle="modal" 
                                            data-bs-target="#confirmSingleDeleteModal"
                                            title="Xóa">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty currentPageSlots && requestScope.searchPerformed}">
                            <tr>
                                <td colspan="7" class="text-center">Không tìm thấy slot nào.</td>
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
                            <a class="page-link" href="slot?page=${currentPage - 1}<%= generateFilterParams(request) %>" aria-label="Previous">
                                <span aria-hidden="true">«</span>
                            </a>
                        </li>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link" href="slot?page=${i}<%= generateFilterParams(request) %>">${i}</a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages || totalPages == 0 ? 'disabled' : ''}">
                            <a class="page-link" href="slot?page=${currentPage + 1}<%= generateFilterParams(request) %>" aria-label="Next">
                                <span aria-hidden="true">»</span>
                            </a>
                        </li>
                    </ul>
                </nav>
            </div>
        </div>

        <!-- Modal Thêm Slot -->
        <div class="modal fade" id="addSlotModal" tabindex="-1" aria-labelledby="addSlotModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addSlotModalLabel">Thêm Slot Check-in</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="addSlotForm" action="${pageContext.request.contextPath}/slot" method="post">
                            <input type="hidden" name="action" value="addSlot">
                            <div class="mb-3">
                                <label for="addSlotCode" class="form-label">Mã slot</label>
                                <input type="text" class="form-control" id="addSlotCode" name="slotCode" placeholder="Nhập mã slot">
                            </div>
                            <div class="mb-3">
                                <label for="addSlotDate" class="form-label">Ngày <span class="text-danger">*</span></label>
                                <input type="date" class="form-control" id="addSlotDate" name="slotDate" required>
                            </div>
                            <div class="mb-3">
                                <label for="addSlotTime" class="form-label">Thời gian <span class="text-danger">*</span></label>
                                <input type="time" class="form-control" id="addSlotTime" name="slotTime" required>
                            </div>
                            <div class="mb-3">
                                <label for="addDoctorId" class="form-label">Bác sĩ <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="addDoctorId" name="doctorId" required placeholder="Nhập mã bác sĩ">
                            </div>
                            <div class="mb-3">
                                <label for="addStatus" class="form-label">Trạng thái</label>
                                <select class="form-select" id="addStatus" name="status">
                                    <option value="available">Có sẵn</option>
                                    <option value="booked">Đã đặt</option>
                                    <option value="cancelled">Đã hủy</option>
                                </select>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" form="addSlotForm" class="btn btn-primary">Thêm</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Cập nhật Slot -->
        <div class="modal fade" id="updateSlotModal" tabindex="-1" aria-labelledby="updateSlotModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="updateSlotModalLabel">Cập nhật Slot Check-in</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="updateSlotForm" action="${pageContext.request.contextPath}/slot" method="post">
                            <input type="hidden" name="action" value="updateSlot">
                            <input type="hidden" name="id" id="updateSlotId">
                            <div class="mb-3">
                                <label for="updateSlotCode" class="form-label">Mã slot</label>
                                <input type="text" class="form-control" id="updateSlotCode" name="slotCode" readonly>
                            </div>
                            <div class="mb-3">
                                <label for="updateSlotDate" class="form-label">Ngày <span class="text-danger">*</span></label>
                                <input type="date" class="form-control" id="updateSlotDate" name="slotDate" required>
                            </div>
                            <div class="mb-3">
                                <label for="updateSlotTime" class="form-label">Thời gian <span class="text-danger">*</span></label>
                                <input type="time" class="form-control" id="updateSlotTime" name="slotTime" required>
                            </div>
                            <div class="mb-3">
                                <label for="updateDoctorId" class="form-label">Bác sĩ <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="updateDoctorId" name="doctorId" required placeholder="Nhập mã bác sĩ">
                            </div>
                            <div class="mb-3">
                                <label for="updateStatus" class="form-label">Trạng thái</label>
                                <select class="form-select" id="updateStatus" name="status">
                                    <option value="available">Có sẵn</option>
                                    <option value="booked">Đã đặt</option>
                                    <option value="cancelled">Đã hủy</option>
                                </select>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" form="updateSlotForm" class="btn btn-primary">Cập nhật</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Xác nhận Xóa Từng Slot -->
        <div class="modal fade" id="confirmSingleDeleteModal" tabindex="-1" aria-labelledby="confirmSingleDeleteModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="confirmSingleDeleteModalLabel">Xác nhận xóa slot</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        Bạn có chắc chắn muốn xóa slot có ID: <span id="singleDeleteSlotIdSpan"></span> không?
                        Hành động này sẽ xóa mềm bản ghi.
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-danger" id="confirmSingleDeleteButton">Xóa</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Xác nhận Xóa Nhiều Slot -->
        <div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-labelledby="confirmDeleteModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="confirmDeleteModalLabel">Xác nhận xóa</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        Bạn có chắc chắn muốn xóa <span id="deleteCount"></span> slot đã chọn không?
                        Hành động này sẽ xóa mềm các bản ghi.
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-danger" id="confirmDeleteButton">Xóa</button>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // JavaScript để điền dữ liệu vào modal cập nhật
            document.querySelectorAll('.btn-edit').forEach(button => {
                button.addEventListener('click', function() {
                    document.getElementById('updateSlotId').value = this.dataset.id;
                    document.getElementById('updateSlotCode').value = this.dataset.code;
                    document.getElementById('updateSlotDate').value = this.dataset.date;
                    document.getElementById('updateSlotTime').value = this.dataset.time;
                    document.getElementById('updateDoctorId').value = this.dataset.doctorId;
                    document.getElementById('updateStatus').value = this.dataset.status;
                });
            });

            // JavaScript để điền ID vào modal xóa từng slot
            document.querySelectorAll('.btn-delete-single').forEach(button => {
                button.addEventListener('click', function() {
                    document.getElementById('singleDeleteSlotIdSpan').textContent = this.dataset.id;
                    // Có thể thêm logic để gửi request xóa
                    document.getElementById('confirmSingleDeleteButton').onclick = function() {
                        // Tạm thời log ID, cần tích hợp với POST request
                        console.log('Xóa slot ID: ' + this.dataset.id);
                    };
                });
            });

            // JavaScript cho checkbox "Chọn tất cả"
            document.getElementById('checkAll').addEventListener('change', function() {
                document.querySelectorAll('input[name="selectedSlots"]').forEach(checkbox => {
                    checkbox.checked = this.checked;
                });
                updateDeleteCount();
            });

            // Cập nhật số lượng slot được chọn
            function updateDeleteCount() {
                const checkedCount = document.querySelectorAll('input[name="selectedSlots"]:checked').length;
                document.getElementById('deleteCount').textContent = checkedCount;
            }

            // Gắn sự kiện cho các checkbox
            document.querySelectorAll('input[name="selectedSlots"]').forEach(checkbox => {
                checkbox.addEventListener('change', updateDeleteCount);
            });

            // JavaScript cho modal xóa nhiều
            document.getElementById('confirmDeleteButton').addEventListener('click', function() {
                document.getElementById('deleteMultipleForm').submit();
            });

            // Các biến từ Controller
            window.GLOBAL_IS_SEARCH_PERFORMED = ${requestScope.searchPerformed != null ? requestScope.searchPerformed : false};
            window.GLOBAL_HAS_RESULTS = ${requestScope.hasResults != null ? requestScope.hasResults : false};
        </script>
        <script src="${pageContext.request.contextPath}/js/appointment.js"></script>

        <%!
            // Hàm trợ giúp để tạo lại các tham số lọc cho phân trang
            private String generateFilterParams(HttpServletRequest request) {
                StringBuilder params = new StringBuilder();

                String slotCode = (String) request.getAttribute("slotCode");
                if (slotCode != null && !slotCode.isEmpty()) {
                    params.append("&slotCode=").append(slotCode);
                }
                String doctorId = (String) request.getAttribute("doctorId");
                if (doctorId != null && !doctorId.isEmpty()) {
                    params.append("&doctorId=").append(doctorId);
                }
                String slotDate = (String) request.getAttribute("slotDate");
                if (slotDate != null && !slotDate.isEmpty()) {
                    params.append("&slotDate=").append(slotDate);
                }
                String status = (String) request.getAttribute("status");
                if (status != null && !status.isEmpty()) {
                    params.append("&status=").append(status);
                }

                Integer rp = (Integer) request.getAttribute("recordsPerPage");
                if (rp != null) {
                    params.append("&recordsPerPage=").append(rp);
                }

                params.append("&submitSearch=true");

                return params.toString();
            }
        %>
    </body>
</html>