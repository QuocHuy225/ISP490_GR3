<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="com.mycompany.isp490_gr3.model.Appointment" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Lịch hẹn</title>
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
                <li>
                    <a href="${pageContext.request.contextPath}/appointments">
                        <i class="bi bi-calendar-check"></i> Quản lý đặt lịch
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/checkin">
                        <i class="bi bi-calendar-check"></i> Quản lý check-in
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/#">
                        <i class="bi bi-calendar-check"></i> Quản lý hàng đợi
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/#">
                        <i class="bi bi-speedometer2"></i> Quản lý bệnh nhân
                    </a>
                </li> 
                <li>
                    <a href="${pageContext.request.contextPath}/#">
                        <i class="bi bi-speedometer2"></i> Quản lý lịch bác sĩ
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
                            <li class="breadcrumb-item active" aria-current="page">Lịch hẹn</li>
                        </ol>
                    </nav>
                </div>
            </div>                          
            <ul class="nav nav-tabs w-100">
                <li class="nav-item flex-fill text-center">
                    <a class="nav-link active" href="${pageContext.request.contextPath}/appointments">Lịch hẹn</a>
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

            <form id="searchForm" action="${pageContext.request.contextPath}/appointments" method="post" class="mb-4 mt-3 p-3 border rounded shadow-sm bg-light">
                <div class="row mb-3">
                    <div class="col-md-4">
                        <label for="appointmentCode" class="form-label">Mã lịch hẹn</label>
                        <input type="text" class="form-control" id="appointmentCode" name="appointmentCode"
                               placeholder="Nhập" value="${param.appointmentCode}">
                    </div>

                    <div class="col-md-4">
                        <label for="slotDate" class="form-label">Ngày hẹn</label>
                        <input type="date" class="form-control" id="slotDate" name="slotDate"
                               value="${param.slotDate}" />
                    </div>

                    <div class="col-md-4">
                        <label for="patientCode" class="form-label">Mã bệnh nhân</label>
                        <input type="text" class="form-control" id="patientCode" name="patientCode"
                               placeholder="Nhập" value="${param.patientCode}">
                    </div>

                    <div class="col-md-4">
                        <label for="doctorId" class="form-label">Bác sĩ</label>
                        <select id="doctorId" name="doctorId" class="form-select">
                            <option value="">--Chọn--</option>
                            <c:forEach var="d" items="${doctorList}">
                                <option value="${d.id}" <c:if test="${param.doctorId == d.id}">selected</c:if>>${d.fullName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-4">
                        <label for="servicesId" class="form-label">Dịch vụ</label>
                        <select id="servicesId" name="servicesId" class="form-select">
                            <option value="">--Chọn--</option>
                            <c:forEach var="s" items="${serviceList}">
                                <option value="${s.servicesId}" <c:if test="${param.servicesId == s.servicesId}">selected</c:if>>${s.serviceName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-4">
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


        </form>
        <div class="appointment-list-section animate-fade-in">  
            <div class="appointment-list-header">
                <h5>Danh sách Lịch hẹn (${totalRecords} kết quả)</h5>
                <div class="d-flex gap-2">
                    <form id="deleteMultipleForm" action="${pageContext.request.contextPath}/appointments/delete" method="post" style="display: none;">
                        <input type="hidden" name="action" value="deleteMultiple"/>
                        <input type="hidden" name="page" value="${currentPage}"/>
                        <div id="selectedAppointmentIds"></div>

                        <button type="button" class="btn btn-danger btn-delete-selected" id="btnDeleteSelected" data-bs-toggle="modal" data-bs-target="#confirmDeleteModal">
                            <i class="bi bi-trash"></i> Xóa (<span id="selectedCount">0</span> bản ghi)
                        </button>
                    </form>


                    <button type="button" class="btn btn-success btn-add-appointment" data-bs-toggle="modal" data-bs-target="#addAppointmentModal">
                        <i class="bi bi-plus-circle"></i> Thêm Lịch hẹn mới
                    </button>
                </div>
            </div>

            <div class="table-responsive">
                <table class="table table-hover table-bordered table-appointments">
                    <thead class="table-primary">
                        <tr>
                            <th><input type="checkbox" id="checkAll" /></th>
                            <th>STT</th>
                            <th>Mã lịch hẹn</th>
                            <th>Ngày khám</th>
                            <th>Slot khám</th>
                            <th>Mã bệnh nhân</th>
                            <th>Bệnh nhân</th>
                            <th>Số điện thoại</th>
                            <th>Bác sĩ</th>
                            <th>Dịch vụ</th>
                            <th>Trạng thái</th>
                            <!--                            <th>Thanh toán</th>-->
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="appointment" items="${currentPageAppointments}" varStatus="status">
                            <tr>
                                <td><input type="checkbox" name="selectedAppointments" value="${appointment.id}" class="appointment-checkbox" /></td>
                                <td>${startIndex + status.index + 1}</td>
                                <td>${appointment.appointmentCode}</td>
                                <td>${appointment.slotDate}</td>
                                <td>${appointment.slotTimeRange}</td>
                                <td>${appointment.patientCode}</td>
                                <td>${appointment.patientName}</td>
                                <td>${appointment.patientPhone}</td>
                                <td>${appointment.doctorName}</td>
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
                                <!--
                                                                <td>${appointment.paymentStatus}</td>-->
                                <td>
                                    <a href="#" class="btn btn-sm btn-outline-primary" title="Xem / Sửa">
                                        <i class="bi bi-pencil-square"></i>
                                    </a>
                                    <a href="#" class="btn btn-sm btn-outline-danger btn-delete-single" title="Xóa" data-bs-toggle="modal" data-bs-target="#confirmSingleDeleteModal" data-id="${appointment.id}">
                                        <i class="bi bi-trash"></i>
                                    </a>
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
                            <a class="page-link" href="${pageContext.request.contextPath}/appointments?page=${currentPage - 1}${filterParams}" aria-label="Previous">
                                <span aria-hidden="true">«</span>
                            </a>
                        </li>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/appointments?page=${i}${filterParams}">${i}</a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages || totalPages == 0 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/appointments?page=${currentPage + 1}${filterParams}" aria-label="Next">
                                <span aria-hidden="true">»</span>
                            </a>
                        </li>
                    </ul>
                </nav>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Các biến này được đặt từ Controller của bạn
        window.GLOBAL_IS_SEARCH_PERFORMED = ${requestScope.searchPerformed != null ? requestScope.searchPerformed : false};
        window.GLOBAL_HAS_RESULTS = ${requestScope.hasResults != null ? requestScope.hasResults : false};
    </script>

    <script src="${pageContext.request.contextPath}/js/appointment.js"></script>


    <!-- Modal Thêm lịch hẹn -->
    <div class="modal fade" id="addAppointmentModal" tabindex="-1" aria-labelledby="addAppointmentLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content shadow-lg rounded-4">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold" id="addAppointmentLabel">Thêm lịch hẹn mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                </div>

                <form action="${pageContext.request.contextPath}/appointments/add" method="post">
                    <div class="modal-body px-4 py-3">
                        <!-- Dịch vụ -->
                        <div class="mb-3">
                            <label for="servicesId" class="form-label fw-semibold">Dịch vụ <span class="text-danger">*</span></label>
                            <select class="form-select" id="servicesId" name="servicesId" required>
                                <option value="">--Chọn--</option>
                                <c:forEach var="service" items="${serviceList}">
                                    <option value="${service.servicesId}">${service.serviceName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Slot check-in: dropdown + nút lọc -->
                        <div class="mb-3">
                            <label for="slotId" class="form-label fw-semibold">Slot <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <select class="form-select" id="slotId" name="slotId" required>
                                    <option value="">--Chọn--</option>
                                    <!-- Render bằng JS sau khi lọc -->
                                </select>
                                <button type="button" class="btn btn-outline-secondary" id="filterSlotBtn" title="Lọc slot">
                                    <i class="bi bi-funnel-fill"></i>
                                </button>
                            </div>
                        </div>

                        <!-- Bệnh nhân: dropdown + nút lọc -->
                        <div class="mb-3">
                            <label for="patientId" class="form-label fw-semibold">Bệnh nhân <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <select class="form-select" id="patientId" name="patientId" required>
                                    <option value="">--Chọn--</option>
                                    <c:forEach var="p" items="${patientList}">
                                        <option value="${p.id}">${p.patientCode} - ${p.fullName}</option>
                                    </c:forEach>
                                </select>
                                <button type="button" class="btn btn-outline-secondary" id="filterPatientBtn" title="Lọc bệnh nhân">
                                    <i class="bi bi-funnel-fill"></i>
                                </button>
                            </div>
                        </div>

                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary">Lưu</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Modal Lọc Slot -->
    <div class="modal fade" id="filterSlotModal" tabindex="-1" aria-labelledby="filterSlotModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content rounded-4 shadow">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold" id="filterSlotModalLabel">Lọc Slot</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                </div>
                <div class="modal-body">
                    <!-- Chọn bác sĩ -->
                    <div class="mb-3">
                        <label for="filterDoctorId" class="form-label">Bác sĩ</label>
                        <select class="form-select" id="filterDoctorId">
                            <option value="">--Tất cả bác sĩ--</option>
                            <c:forEach var="doc" items="${doctorList}">
                                <option value="${doc.id}">${doc.fullName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- Ngày -->
                    <div class="mb-3">
                        <label for="filterSlotDate" class="form-label">Ngày</label>
                        <input type="date" class="form-control" id="filterSlotDate" />
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="button" class="btn btn-primary" id="applySlotFilter">Áp dụng</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal Lọc Bệnh Nhân -->
    <div class="modal fade" id="filterPatientModal" tabindex="-1" aria-labelledby="filterPatientModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content rounded-4 shadow">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold" id="filterPatientModalLabel">Lọc bệnh nhân</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                </div>
                <div class="modal-body">
                    <!-- Mã bệnh nhân -->
                    <div class="mb-3">
                        <label for="filterPatientCode" class="form-label">Mã bệnh nhân</label>
                        <input type="text" class="form-control" id="filterPatientCode" placeholder="Nhập mã bệnh nhân">
                    </div>

                    <!-- Họ tên bệnh nhân -->
                    <div class="mb-3">
                        <label for="filterPatientName" class="form-label">Họ tên</label>
                        <input type="text" class="form-control" id="filterPatientName" placeholder="Nhập tên bệnh nhân">
                    </div>

                    <!-- Số điện thoại -->
                    <div class="mb-3">
                        <label for="filterPatientPhone" class="form-label">Số điện thoại</label>
                        <input type="text" class="form-control" id="filterPatientPhone" placeholder="Nhập số điện thoại">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="button" class="btn btn-primary" id="applyPatientFilter">Áp dụng</button>
                </div>
            </div>
        </div>
    </div>



    <!-- JS lọc slot, bệnh nhân -->
    <script>
      
        document.addEventListener("DOMContentLoaded", function () {
            // Modal lọc slot
            const filterSlotModal = new bootstrap.Modal(document.getElementById('filterSlotModal'));
            document.getElementById("filterSlotBtn").addEventListener("click", function () {
                filterSlotModal.show();
            });

            // Áp dụng lọc slot
            document.getElementById("applySlotFilter").addEventListener("click", function () {
                const doctorId = document.getElementById("filterDoctorId").value;
                const slotDate = document.getElementById("filterSlotDate").value;

                // Gọi API lọc slot theo doctorId + slotDate, rồi render lại dropdown slot
                const slotSelect = document.getElementById("slotId");
                slotSelect.innerHTML = `<option value="">Đang tải...</option>`;

                fetch(`/slot/filter?doctorId=${doctorId}&slotDate=${slotDate}`)
                        .then(res => res.json())
                        .then(data => {
                            slotSelect.innerHTML = '<option value="">--Chọn slot check-in--</option>';
                            data.forEach(slot => {
                                const option = document.createElement("option");
                                option.value = slot.id;
                                option.textContent = `${slot.slotDate} ${slot.startTime} - ${slot.endTime} (${slot.doctorName})`;
                                slotSelect.appendChild(option);
                            });
                            filterSlotModal.hide();
                        })
                        .catch(err => {
                            alert("Lỗi khi lọc slot!");
                            console.error(err);
                        });
            });

            // Modal lọc bệnh nhân
            const filterPatientModal = new bootstrap.Modal(document.getElementById('filterPatientModal'));
            document.getElementById("filterPatientBtn").addEventListener("click", function () {
                filterPatientModal.show();
            });

            // Áp dụng lọc bệnh nhân
            document.getElementById("applyPatientFilter").addEventListener("click", function () {
                const code = document.getElementById("filterPatientCode").value.trim();
                const name = document.getElementById("filterPatientName").value.trim();
                const phone = document.getElementById("filterPatientPhone").value.trim();

                const query = new URLSearchParams({
                    patientCode: code,
                    fullName: name,
                    phone: phone
                });

                const patientSelect = document.getElementById("patientId");
                patientSelect.innerHTML = `<option value="">Đang tải...</option>`;

                fetch(`/patient/filter?${query.toString()}`)
                        .then(res => res.json())
                        .then(data => {
                            patientSelect.innerHTML = `<option value="">--Chọn bệnh nhân--</option>`;
                            data.forEach(p => {
                                const option = document.createElement("option");
                                option.value = p.id;
                                option.textContent = `${p.patientCode} - ${p.fullName}`;
                                                            patientSelect.appendChild(option);
                                                        });
                                                        filterPatientModal.hide();
                                                    })
                                                    .catch(err => {
                                                        alert("Lỗi khi lọc bệnh nhân!");
                                                        console.error(err);
                                                    });
                                        });
                                    });

    </script>


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


    <!-- Modal Xác nhận Xóa Từng Lịch hẹn -->
    <div class="modal fade" id="confirmSingleDeleteModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form id="singleDeleteForm" action="${pageContext.request.contextPath}/appointments/delete" method="post">
                    <input type="hidden" name="action" value="deleteSingle">
                    <input type="hidden" id="singleDeleteAppointmentId" name="appointmentId" />
                    <input type="hidden" name="page" value="${currentPage}"/>

                    <div class="modal-header">
                        <h5 class="modal-title">Xóa lịch hẹn</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        Bạn muốn xóa lịch hẹn <span class="text-primary fw-bold" id="modalAppointmentCode"></span> ngày <span class="text-primary fw-bold" id="modalSlotDate"></span>?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-danger">Xóa</button>
                    </div>
                </form>
            </div>
        </div>
    </div>




    <!-- Modal Xác nhận Xóa Nhiều Slot -->
    <div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Xóa nhiều lịch hẹn</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    Bạn muốn xóa <span id="deleteCount" class="text-primary fw-bold"></span> lịch hẹn khỏi hệ thống?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="button" class="btn btn-danger" id="confirmDeleteButton">Xóa</button>
                </div>
            </div>
        </div>
    </div>


    <script>
        document.addEventListener('DOMContentLoaded', function () {
            // Hiển thị/xóa nút "Xóa nhiều"
            function updateDeleteMultipleVisibility() {
                const checkboxes = document.querySelectorAll('.appointment-checkbox');
                const selected = Array.from(checkboxes).filter(cb => cb.checked);
                const count = selected.length;

                const deleteForm = document.getElementById("deleteMultipleForm");
                const countSpan = document.getElementById("selectedCount");

                if (deleteForm && countSpan) {
                    if (count > 0) {
                        deleteForm.style.display = "inline-block";
                        countSpan.textContent = count;
                    } else {
                        deleteForm.style.display = "none";
                        countSpan.textContent = 0;
                    }
                }
            }

            // Gắn sự kiện checkbox từng dòng
            document.querySelectorAll('.appointment-checkbox').forEach(cb => {
                cb.addEventListener("change", updateDeleteMultipleVisibility);
            });

            // Sự kiện "Chọn tất cả"
            const checkAll = document.getElementById("checkAll");
            if (checkAll) {
                checkAll.addEventListener("change", function () {
                    const checked = this.checked;
                    document.querySelectorAll('.appointment-checkbox').forEach(cb => {
                        cb.checked = checked;
                    });
                    updateDeleteMultipleVisibility();
                });
            }

            // Sự kiện XÓA 1
            document.querySelectorAll('.btn-delete-single').forEach(button => {
                button.addEventListener('click', function (event) {
                    event.preventDefault();
                    const appointmentId = this.dataset.id;
                    document.getElementById('singleDeleteAppointmentId').value = appointmentId;

                    const row = this.closest('tr');
                    const appointmentCode = row.querySelector('td:nth-child(3)').textContent.trim();
                    const slotDate = row.querySelector('td:nth-child(4)').textContent.trim();

                    document.getElementById('modalAppointmentCode').textContent = appointmentCode;
                    document.getElementById('modalSlotDate').textContent = slotDate;

                    const modalEl = document.getElementById('confirmSingleDeleteModal');
                    if (modalEl) {
                        const modal = new bootstrap.Modal(modalEl);
                        modal.show();
                    }
                });
            });

            // Sự kiện bấm nút "Xóa đã chọn"
            const btnDeleteSelected = document.getElementById('btnDeleteSelected');
            if (btnDeleteSelected) {
                btnDeleteSelected.addEventListener('click', function (event) {
                    event.preventDefault();
                    const checkedCount = document.querySelectorAll('input[name="selectedAppointments"]:checked').length;
                    const deleteCountSpan = document.getElementById('deleteCount');
                    if (deleteCountSpan) {
                        deleteCountSpan.textContent = checkedCount;
                    }
                });
            }

            // Xác nhận XÓA NHIỀU
            const confirmDeleteButton = document.getElementById('confirmDeleteButton');
            if (confirmDeleteButton) {
                confirmDeleteButton.addEventListener('click', function () {
                    const selected = document.querySelectorAll('input[name="selectedAppointments"]:checked');
                    const container = document.getElementById('selectedAppointmentIds');
                    if (container) {
                        container.innerHTML = "";
                        selected.forEach(checkbox => {
                            const hiddenInput = document.createElement("input");
                            hiddenInput.type = "hidden";
                            hiddenInput.name = "appointmentIds";
                            hiddenInput.value = checkbox.value;
                            container.appendChild(hiddenInput);
                        });
                        document.getElementById('deleteMultipleForm').submit();
                    }
                });
            }

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
