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

    <body >
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
                            <li class="breadcrumb-item active" aria-current="page">Slot</li>
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
                    <a class="nav-link active" href="${pageContext.request.contextPath}/slot">Slot</a>
                </li>
            </ul>

            <!-- Thông báo -->
            <%
      String message = (String) session.getAttribute("message");
      String messageType = (String) session.getAttribute("messageType");
      if (message != null && messageType != null) {
            %>
            <div class="alert alert-<%= messageType %> alert-dismissible fade show" role="alert">
                <%= message %>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <%
                    session.removeAttribute("message");
                    session.removeAttribute("messageType");
                }
            %>

            <!-- Form tìm kiếm -->
            <form id="searchForm" action="${pageContext.request.contextPath}/slot" method="post"
                  class="mb-4 mt-3 p-3 border rounded shadow-sm bg-light">
                <input type="hidden" name="action" value="search" />
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label for="doctorId" class="form-label">Bác sĩ</label>
                        <select class="form-select" id="doctorId" name="doctorId">
                            <option value="">--Chọn--</option>
                            <c:forEach var="doctor" items="${doctors}">
                                <option value="${doctor.id}" ${doctor.id == param.doctorId ? 'selected' : ''}>
                                    ${doctor.fullName}
                                </option>
                            </c:forEach>
                            <c:if test="${empty doctors}">
                                <option value="">Không có bác sĩ nào</option>
                            </c:if>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label for="slotDate" class="form-label">Ngày hẹn</label>
                        <input type="date" class="form-control" id="slotDate" name="slotDate" value="${slotDate}" />
                    </div>
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


            <!-- Danh sách slot -->
            <div class="appointment-list-section animate-fade-in">  
                <div class="appointment-list-header">
                    <h5>Danh sách Slot (${totalRecords} kết quả)</h5>
                    <div class="d-flex gap-2">
                        <form id="deleteMultipleForm" action="${pageContext.request.contextPath}/slot/delete" method="post" style="display: none;">
                            <input type="hidden" name="action" value="deleteMultiple"/>
                            <input type="hidden" name="doctorId" value="${doctorId}"/>
                            <input type="hidden" name="slotDate" value="${slotDate}"/>
                            <input type="hidden" name="page" value="${currentPage}"/>
                            <div id="selectedSlotIds"></div>
                            <button type="button" class="btn btn-danger btn-delete-selected" id="btnDeleteSelected" data-bs-toggle="modal" data-bs-target="#confirmDeleteModal">
                                <i class="bi bi-trash"></i> Xóa (<span id="selectedCount">0</span> bản ghi)
                            </button>
                        </form>
                        <button type="button" class="btn btn-success btn-add-appointment" data-bs-toggle="modal" data-bs-target="#addSlotModal">
                            <i class="bi bi-plus-circle"></i> Thêm Slot mới
                        </button>
                    </div>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover table-bordered table-appointments">
                        <thead class="table-primary">
                            <tr>
                                <th><input type="checkbox" id="checkAll"></th>
                                <th>STT</th>
                                <th>Ngày hẹn</th>
                                <th>Slot</th>
                                <th>Bác sĩ</th>
                                <th>Số bệnh nhân tối đa</th>
                                <th>Đã đặt</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="slot" items="${currentPageSlots}" varStatus="loop">
                                <tr>
                                    <td><input type="checkbox" name="selectedSlots" value="${slot.id}" class="slot-checkbox" /></td>
                                    <td>${startIndex + loop.index + 1}</td>
                                    <td><c:out value="${slot.slotDate}"/></td>
                                    <td><c:out value="${slot.checkinRange}"/></td>
                                    <td><c:out value="${slot.doctorName}"/></td>
                                    <td><c:out value="${slot.maxPatients}"/></td>
                                    <td><c:out value="${slot.bookingStatus}"/></td>
                                    <td>
                                        <a href="#" class="btn btn-sm btn-outline-primary" title="Sửa">
                                            <i class="bi bi-eye"></i>
                                        </a>
                                        <a href="#" class="btn btn-sm btn-outline-danger btn-delete-single" title="Xóa" data-bs-toggle="modal" data-bs-target="#confirmSingleDeleteModal" data-id="${slot.id}">
                                            <i class="bi bi-trash"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty currentPageSlots}">
                                <tr>
                                    <td colspan="8" class="text-center text-muted">Không có slot nào được tìm thấy.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
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
                        <h5 class="modal-title" id="addSlotModalLabel">Thêm Slot mới</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="addSlotForm" action="${pageContext.request.contextPath}/slot/add" method="post">
                            <div class="mb-3">
                                <label for="modalDoctorId" class="form-label">Bác sĩ <span class="text-danger">*</span></label>
                                <select class="form-select" id="modalDoctorId" name="doctorId" required>
                                    <option value="">--Chọn--</option>
                                    <c:forEach var="doctor" items="${doctors}">
                                        <option value="${doctor.id}" ${doctor.id == param.doctorId ? 'selected' : ''}>
                                            ${doctor.fullName}
                                        </option>
                                    </c:forEach>
                                    <c:if test="${empty doctors}">
                                        <option value="">Không có bác sĩ nào</option>
                                    </c:if>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label for="slotDateDropdown" class="form-label">Ngày slot<span class="text-danger">*</span></label>
                                <select class="form-select" id="slotDateDropdown" name="slotDate" required>
                                    <option value="">--Chọn--</option>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label for="addType" class="form-label">Loại thêm <span class="text-danger">*</span></label>
                                <select class="form-select" id="addType" name="addType" required>
                                    <option value="single">Thêm slot đơn</option>
                                    <option value="range">Thêm theo dải</option>
                                </select>
                            </div>

                            <div id="singleSlotFields">
                                <div class="mb-3">
                                    <label for="addStartTime" class="form-label">Giờ bắt đầu <span class="text-danger">*</span></label>
                                    <input type="time" class="form-control" id="addStartTime" name="startTime" required>
                                </div>
                                <div class="mb-3">
                                    <label for="addSlotDuration" class="form-label">Khoảng thời gian (phút) <span class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="addSlotDuration" name="slotDuration" required placeholder="Nhập số phút">
                                </div>
                            </div>

                            <div id="rangeSlotFields" style="display: none;">
                                <div class="mb-3">
                                    <label for="addStartRangeTime" class="form-label">Giờ bắt đầu dải <span class="text-danger">*</span></label>
                                    <input type="time" class="form-control" id="addStartRangeTime" name="startRangeTime">
                                </div>
                                <div class="mb-3">
                                    <label for="addEndRangeTime" class="form-label">Giờ kết thúc dải <span class="text-danger">*</span></label>
                                    <input type="time" class="form-control" id="addEndRangeTime" name="endRangeTime">
                                </div>
                                <div class="mb-3">
                                    <label for="addRangeSlotDuration" class="form-label">Khoảng thời gian mỗi slot (phút) <span class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="addRangeSlotDuration" name="rangeSlotDuration" placeholder="Nhập số phút">
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="addMaxPatients" class="form-label">Số bệnh nhân tối đa <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="addMaxPatients" name="maxPatients" required placeholder="Nhập số bệnh nhân tối đa">
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                        </button>
                        <button type="submit" form="addSlotForm" class="btn btn-primary">
                            <i class="bi bi-check-circle me-2"></i>Thêm
                        </button>
                    </div>
                </div>
            </div>
        </div>


        <script>
            <%
                String scheme = request.getScheme();             // http or https
                String serverName = request.getServerName();     // localhost or domain
                int serverPort = request.getServerPort();        // 8080, 443, etc.
                String contextPath = request.getContextPath();   // /your-app

                String baseURL = scheme + "://" + serverName + ":" + serverPort + contextPath;
            %>

            document.addEventListener("DOMContentLoaded", function () {
                const BASE_URL = '<%= request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() %>';

                console.log("BASE_URL:", BASE_URL);
                console.log("Protocol:", window.location.protocol);
                console.log("Host:", window.location.host);
                console.log("Context Path:", BASE_URL);
                console.log("Full URL:", window.location.href);

                const doctorSelect = document.getElementById("modalDoctorId");
                const slotDateDropdown = document.getElementById("slotDateDropdown");
                const addTypeSelect = document.getElementById("addType");
                const singleFields = document.getElementById("singleSlotFields");
                const rangeFields = document.getElementById("rangeSlotFields");
                const addSlotModal = document.getElementById("addSlotModal");

                // Toggle giữa slot đơn và slot theo dải
                function toggleSlotFields() {
                    const isSingle = addTypeSelect.value === "single";
                    singleFields.style.display = isSingle ? "block" : "none";
                    rangeFields.style.display = isSingle ? "none" : "block";
                    document.getElementById("addStartTime").required = isSingle;
                    document.getElementById("addSlotDuration").required = isSingle;
                    document.getElementById("addStartRangeTime").required = !isSingle;
                    document.getElementById("addEndRangeTime").required = !isSingle;
                    document.getElementById("addRangeSlotDuration").required = !isSingle;
                }
                addTypeSelect.addEventListener("change", toggleSlotFields);
                toggleSlotFields();

                // Load ngày slot
                function loadSlotDates(doctorId) {
                    console.log("doctorId:", doctorId);
                    slotDateDropdown.innerHTML = '<option value="">Đang tải...</option>';

                    if (!doctorId) {
                        slotDateDropdown.innerHTML = '<option value="">--Chọn--</option>';
                        console.log("Không có doctorId, bỏ qua fetch");
                        return;
                    }

                    const url = BASE_URL + "/slot/filterSlotDate?doctorId=" + doctorId;

                    console.log("Fetch URL:", url);

                    fetch(url)
                            .then(res => {
                                console.log("res", res);
                                if (!res.ok) {
                                    if (res.status === 404) {
                                        throw new Error("404: Endpoint không tồn tại hoặc doctorId không hợp lệ");
                                    }
                                    throw new Error(`Lỗi ${res.status}: Không thể tải dữ liệu`);
                                }
                                return res.json();
                            })
                            .then(data => {
                                console.log("Dữ liệu nhận được:", data);
                                slotDateDropdown.innerHTML = '<option value="">--Chọn--</option>';
                                if (data.error) {
                                    slotDateDropdown.innerHTML = `<option value="">${data.error}</option>`;
                                } else if (!Array.isArray(data) || data.length === 0) {
                                    slotDateDropdown.innerHTML = '<option value="">Không có ngày làm việc</option>';
                                } else {
                                    data.forEach((item, index) => {
                                        console.log(`Item ${index}:`, item, typeof item);
                                        const date = item?.trim();
                                        if (date) {
                                            const option = document.createElement("option");
                                            option.value = date;
                                            option.textContent = date;
                                            slotDateDropdown.appendChild(option);
                                        } else {
                                            console.warn("Bỏ qua item rỗng:", item);
                                        }
                                    });
                                }
                            })
                            .catch(error => {
                                console.error("Lỗi khi tải ngày làm việc:", error);
                                slotDateDropdown.innerHTML = `<option value="">Lỗi: ${error.message}</option>`;
                                alert("Không thể tải ngày làm việc. Vui lòng thử lại sau.");
                            });
                }

                // Khi chọn bác sĩ
                doctorSelect.addEventListener("change", function () {
                    console.log("Doctor ID từ dropdown:", this.value);
                    loadSlotDates(this.value);
                });

                // Khi mở modal
                addSlotModal.addEventListener("shown.bs.modal", function () {
                    addTypeSelect.value = "single";
                    toggleSlotFields();
                    slotDateDropdown.innerHTML = '<option value="">--Chọn--</option>';
                    const selectedDoctorId = doctorSelect.value;
                    console.log("Doctor ID khi mở modal:", selectedDoctorId);
                    if (selectedDoctorId) {
                        loadSlotDates(selectedDoctorId);
                    }
                });
            });

            document.getElementById("resetFilterButton").addEventListener("click", function () {
                const form = document.getElementById("searchForm");
                form.reset(); // reset giá trị form về mặc định

                // Xóa các giá trị nếu chúng được giữ qua attribute `value`
                document.getElementById("doctorId").value = "";
                document.getElementById("slotDate").value = "";

                // Gửi form mà không có tham số lọc
                const actionURL = form.getAttribute("action");
                const formData = new FormData();
                formData.append("action", "search");

                fetch(actionURL, {
                    method: "POST",
                    body: formData
                }).then(() => {
                    // Sau khi submit xong, reload lại trang
                    window.location.href = actionURL;
                });
            });


            function updateDeleteMultipleVisibility() {
                const checkboxes = document.querySelectorAll('.slot-checkbox');
                const selected = Array.from(checkboxes).filter(cb => cb.checked);
                const count = selected.length;

                const deleteForm = document.getElementById("deleteMultipleForm");
                const countSpan = document.getElementById("selectedCount");

                if (count > 0) {
                    deleteForm.style.display = "inline-block"; // Hiện nút
                    countSpan.textContent = count;
                } else {
                    deleteForm.style.display = "none"; // Ẩn nút
                    countSpan.textContent = 0;
                }
            }

// Gán sự kiện thay đổi cho từng checkbox
            document.querySelectorAll('.slot-checkbox').forEach(cb => {
                cb.addEventListener("change", updateDeleteMultipleVisibility);
            });

// Gán sự kiện cho checkbox "Chọn tất cả"
            document.getElementById("checkAll").addEventListener("change", function () {
                const checked = this.checked;
                document.querySelectorAll('.slot-checkbox').forEach(cb => {
                    cb.checked = checked;
                });
                updateDeleteMultipleVisibility();
            });

        </script>



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
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                        </button>
                        <button type="submit" form="updateSlotForm" class="btn btn-primary">
                            <i class="bi bi-check-circle me-2"></i>Cập nhật
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Xác nhận Xóa Từng Slot -->
        <div class="modal fade" id="confirmSingleDeleteModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Xóa một slot</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        Bạn muốn xóa slot <span class="text-primary fw-bold" id="slotTimeRange"></span> 
                        ngày <span class="text-primary fw-bold" id="modalSlotDate"></span> ra khỏi hệ thống?
                    </div>
                    <div class="modal-footer">
                        <form id="singleDeleteForm" action="${pageContext.request.contextPath}/slot/delete" method="post">
                            <input type="hidden" name="action" value="deleteSingle">
                            <input type="hidden" id="singleDeleteSlotId" name="slotId" />
                            <input type="hidden" name="doctorId" value="${doctorId}"/>
                            <input type="hidden" name="slotDate" value="${slotDate}"/>
                            <input type="hidden" name="page" value="${currentPage}"/>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-danger">Xóa</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>


        <!-- Modal Xác nhận Xóa Nhiều Slot -->
        <div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Xóa nhiều slot</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        Bạn muốn xóa <span id="deleteCount" class="text-primary fw-bold"></span> slot ra khỏi hệ thống?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                        </button>
                        <button type="button" class="btn btn-danger" id="confirmDeleteButton">
                            <i class="bi bi-trash me-2"></i>Xóa
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Cập nhật hiển thị nút xóa nhiều
                function updateDeleteMultipleVisibility() {
                    const checkboxes = document.querySelectorAll('.slot-checkbox');
                    const selected = Array.from(checkboxes).filter(cb => cb.checked);
                    const count = selected.length;

                    const deleteForm = document.getElementById("deleteMultipleForm");
                    const countSpan = document.getElementById("selectedCount");

                    if (count > 0) {
                        deleteForm.style.display = "inline-block";
                        countSpan.textContent = count;
                    } else {
                        deleteForm.style.display = "none";
                        countSpan.textContent = 0;
                    }
                }

                // Gắn sự kiện cho checkbox
                document.querySelectorAll('.slot-checkbox').forEach(cb => {
                    cb.addEventListener("change", updateDeleteMultipleVisibility);
                });

                // Gắn sự kiện cho checkbox "Chọn tất cả"
                document.getElementById("checkAll").addEventListener("change", function () {
                    const checked = this.checked;
                    document.querySelectorAll('.slot-checkbox').forEach(cb => {
                        cb.checked = checked;
                    });
                    updateDeleteMultipleVisibility();
                });

                // Xử lý xóa một slot
                document.querySelectorAll('.btn-delete-single').forEach(button => {
                    button.addEventListener('click', function (event) {
                        event.preventDefault();
                        const slotId = this.dataset.id;
                        document.getElementById('singleDeleteSlotId').value = slotId;

                        const row = this.closest('tr');
                        const slotDateCell = row.querySelector('td:nth-child(3)');
                        const slotTimeRangeCell = row.querySelector('td:nth-child(4)');

                        const slotDate = slotDateCell ? slotDateCell.textContent.trim() : 'N/A';
                        const slotTimeRange = slotTimeRangeCell ? slotTimeRangeCell.textContent.trim() : 'N/A';

                        const slotDateElement = document.getElementById('modalSlotDate');
                        const slotTimeRangeElement = document.getElementById('slotTimeRange');

                        if (slotDateElement) {
                            slotDateElement.textContent = slotDate;
                        } else {
                            console.error('Phần tử #modalSlotDate không tồn tại');
                        }
                        if (slotTimeRangeElement) {
                            slotTimeRangeElement.textContent = slotTimeRange;
                        } else {
                            console.error('Phần tử #slotTimeRange không tồn tại');
                        }

                        console.log('Slot ID:', slotId);
                        console.log('Slot Date:', slotDate);
                        console.log('Slot Time Range:', slotTimeRange);

                        const modal = new bootstrap.Modal(document.getElementById('confirmSingleDeleteModal'));
                        modal.show();
                    });
                });

                // Xử lý xóa nhiều slot
                document.getElementById('btnDeleteSelected').addEventListener('click', function (event) {
                    event.preventDefault();
                    const checkedCount = document.querySelectorAll('input[name="selectedSlots"]:checked').length;
                    document.getElementById('deleteCount').textContent = checkedCount;
                    console.log('Số lượng slot được chọn:', checkedCount);
                    const modal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
                    modal.show();
                });

                document.getElementById('confirmDeleteButton').addEventListener('click', function () {
                    const selected = document.querySelectorAll('input[name="selectedSlots"]:checked');
                    const container = document.getElementById('selectedSlotIds');
                    container.innerHTML = "";
                    selected.forEach(checkbox => {
                        const hiddenInput = document.createElement("input");
                        hiddenInput.type = "hidden";
                        hiddenInput.name = "slotIds";
                        hiddenInput.value = checkbox.value;
                        container.appendChild(hiddenInput);
                    });
                    console.log('Slot IDs được chọn:', Array.from(selected).map(cb => cb.value));
                    document.getElementById('deleteMultipleForm').submit();
                });

                // Xử lý khi bất kỳ modal nào đóng
                document.querySelectorAll('.modal').forEach(modal => {
                    modal.addEventListener('hidden.bs.modal', function () {
                        const backdrops = document.querySelectorAll('.modal-backdrop');
                        backdrops.forEach(backdrop => backdrop.remove());
                        document.body.classList.remove('modal-open');
                        document.body.style.overflow = '';
                        document.body.style.paddingRight = '';
                        console.log(`Modal ${modal.id} đã đóng, xóa backdrop`);
                    });
                });
            });
            // Các biến từ Controller
            window.GLOBAL_IS_SEARCH_PERFORMED = ${requestScope.searchPerformed != null ? requestScope.searchPerformed : false};
            window.GLOBAL_HAS_RESULTS = ${requestScope.hasResults != null ? requestScope.hasResults : false};
        </script>
        <script src="${pageContext.request.contextPath}/js/slot.js"></script>

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