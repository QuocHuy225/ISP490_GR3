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
                        <input type="text" class="form-control" id="appointmentCode" name="appointmentCode" placeholder="Nhập" value="${param.appointmentCode}">
                    </div>
                    <div class="col-md-4">
                        <label for="slotDate" class="form-label">Ngày hẹn</label>
                        <input type="date" class="form-control" id="slotDate" name="slotDate" value="${empty slotDate ? LocalDate.now().toString() : slotDate}"/>
                    </div>
                    <div class="col-md-4">
                        <label for="patientCode" class="form-label">Mã bệnh nhân</label>
                        <input type="text" class="form-control" id="patientCode" name="patientCode" placeholder="Nhập" value="${param.patientCode}">
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


                </div>
            </div>

            <div class="table-responsive">
                <table class="table table-hover table-bordered table-appointments">
                    <thead class="table-primary">
                        <tr>
                            <!--                            <th><input type="checkbox" id="checkAll" /></th>-->
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
                            <!--                            <th>Thanh toán</th>-->
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="appointment" items="${currentPageAppointments}" varStatus="status">
                            <tr>
<!--                                <td><input type="checkbox" name="selectedAppointments" value="${appointment.id}" class="appointment-checkbox" /></td>-->
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
                                <!--
                                                                <td>${appointment.paymentStatus}</td>-->
                                <td>
                                    <c:choose>
                                        <c:when test="${empty appointment.patientCode}">
                                            <!-- No patient assigned: Show Assign -->
                                            <a href="#" class="btn btn-sm btn-outline-success btn-assign" title="Gán bệnh nhân vào lịch hẹn" data-bs-toggle="modal" data-bs-target="#addAppointmentModal" data-id="${appointment.id}">
                                                <i class="bi bi-person-plus"> Gán </i>
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <!-- Patient assigned: Show Edit and Delete buttons -->
<!--                                            <a href="#" class="btn btn-sm btn-outline-primary btn-edit" title="Đổi/Sửa lịch hẹn " data-bs-toggle="modal" data-bs-target="#updateAppointmentModal" data-id="${appointment.id}">
                                                <i class="bi bi-pencil-square"></i>
                                            </a>-->
                                            <a href="#" class="btn btn-sm btn-outline-warning btn-remove-patient" title="Bỏ gán bệnh nhân vào lịch hẹn" data-bs-toggle="modal" data-bs-target="#confirmRemovePatientModal" data-id="${appointment.id}" data-code="${appointment.appointmentCode}">
                                                <i class="bi bi-person-x"> Bỏ gán </i>
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
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

    <%-- Modal gán bệnh nhân --%>
    <div class="modal fade" id="addAppointmentModal" tabindex="-1" aria-labelledby="addAppointmentModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content shadow-lg rounded-4">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold" id="addAppointmentModalLabel">Gán bệnh nhân vào lịch hẹn</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form id="addAppointmentForm" action="${pageContext.request.contextPath}/appointments/add" method="post">
                    <input type="hidden" name="action" value="assignPatient">
                    <input type="hidden" id="appointmentId" name="appointmentId" />
                    <input type="hidden" id="patientId" name="patientId" />

                    <div class="modal-body px-4 py-3">
                        <!-- Phần tìm kiếm bệnh nhân -->
                        <div class="mb-4 border-bottom pb-3">
                            <h6 class="fw-bold mb-3">Tìm kiếm bệnh nhân</h6>
                            <div class="row g-2">
                                <!-- 2 cột trên -->
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="filterPatientCode" class="form-label">Mã bệnh nhân</label>
                                        <input type="text" class="form-control" id="filterPatientCode" placeholder="Nhập mã">
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="filterPatientCCCD" class="form-label">CCCD</label>
                                        <input type="text" class="form-control" id="filterPatientCCCD" placeholder="Nhập CCCD">
                                    </div>
                                </div>
                                <!-- 2 cột dưới -->
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="filterPatientName" class="form-label">Họ tên</label>
                                        <input type="text" class="form-control" id="filterPatientName" placeholder="Nhập tên">
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="filterPatientPhone" class="form-label">Số điện thoại</label>
                                        <input type="text" class="form-control" id="filterPatientPhone" placeholder="Nhập số">
                                    </div>
                                </div>
                            </div>

                        </div>

                        <!-- Phần danh sách bệnh nhân -->
                        <div class="mt-4">
                            <h6 class="fw-bold mb-3">Danh sách bệnh nhân</h6>
                            <div class="table-responsive">
                                <table class="table table-hover table-bordered" id="patientListTable">
                                    <thead>
                                        <tr>
                                            <th>Mã bệnh nhân</th>
                                            <th>CCCD</th>
                                            <th>Họ tên</th>
                                            <th>Số điện thoại</th>
                                            <th>Chọn</th>
                                        </tr>
                                    </thead>
                                    <tbody id="patientListBody">
                                        <!-- Dữ liệu sẽ được tải từ API -->
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                        </button>
                        <button type="submit" class="btn btn-primary" id="saveAssignmentBtn" disabled>
                            <i class="bi bi-check-circle me-2"></i>Lưu
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        <%
         String scheme = request.getScheme();             // http or https
         String serverName = request.getServerName();     // localhost or domain
         int serverPort = request.getServerPort();        // 8080, 443, etc.
         String contextPath = request.getContextPath();   // /ISP490_GR3
         String baseURL = scheme + "://" + serverName + ":" + serverPort + contextPath;
        %>
        var BASE_URL = "<%= baseURL %>";
        console.log("BASE_URL:", BASE_URL);

        document.addEventListener('DOMContentLoaded', function () {


            const assignButtons = document.querySelectorAll('.btn-assign');
            console.log('Số lượng nút .btn-assign:', assignButtons.length);
            if (assignButtons.length === 0) {
               
                return;
            }

            document.querySelectorAll('.btn-assign').forEach(button => {
                button.addEventListener('click', function (event) {
                    event.preventDefault();
                    const appointmentId = this.dataset.id;
                    console.log('Nhấp vào nút Gán, appointmentId:', appointmentId);

                    if (!appointmentId) {
                        console.error('appointmentId không tồn tại trong dataset!');
                        return;
                    }

                    const modalEl = document.getElementById('addAppointmentModal');
                    if (!modalEl) {
                        console.error('Không tìm thấy modal #addAppointmentModal!');
                        return;
                    }

                    document.getElementById('appointmentId').value = appointmentId;
                    console.log('Đã đặt appointmentId vào form:', appointmentId);

                    const modal = new bootstrap.Modal(modalEl);
                    modal.show();
                    console.log('Modal đã được hiển thị');




                    // Reset filter trước khi load
                    const filterInputs = ['filterPatientCode', 'filterPatientCCCD', 'filterPatientName', 'filterPatientPhone'];
                    filterInputs.forEach(inputId => {
                        const input = document.getElementById(inputId);
                        if (input) {
                            input.value = '';
                            console.log(`Đã reset trường ${inputId}`);
                        } else {
                            console.error(`Không tìm thấy input ${inputId}!`);
                        }
                    });

                    // Delay 300ms để đảm bảo modal đã render DOM đầy đủ
                    setTimeout(() => {
                        loadPatientList();
                    }, 300);
                });
            });

            // Load danh sách bệnh nhân 

            function loadPatientList() {

                console.log('Bắt đầu loadPatientList...');

                const code = document.getElementById('filterPatientCode').value.trim();
                const cccd = document.getElementById('filterPatientCCCD').value.trim();
                const name = document.getElementById('filterPatientName').value.trim();
                const phone = document.getElementById('filterPatientPhone').value.trim();
                console.log('Tham số lọc:', {code, cccd, name, phone});

                const query = new URLSearchParams({
                    code: code,
                    cccd: cccd,
                    name: name,
                    phone: phone
                }).toString();
                console.log('URL query:', query);

                const url = BASE_URL + "/patient/search?" + query;
                console.log('URL đầy đủ:', url);

                fetch(url, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                        .then(res => {
                            console.log('Response status:', res.status);
                            if (!res.ok) {
                                throw new Error(`HTTP error! status: ${res.status}`);
                            }
                            return res.json();
                        })

                        .then(data => {
                            console.log("DATA NHẬN VỀ:", data);
                            console.log("Array.isArray(data):", Array.isArray(data));
                            console.log("data.length:", data.length);

                            console.log('Dữ liệu nhận được từ server:', data);
                            const tbody = document.getElementById('patientListBody');
                            if (!tbody) {
                                console.error('Không tìm thấy tbody #patientListBody!');
                                return;
                            }
                            tbody.innerHTML = '';
                            if (!Array.isArray(data) || data.length === 0) {
                                tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Không tìm thấy bệnh nhân.</td></tr>';
                            } else {
                                data.forEach(patient => {

                                    const row = document.createElement('tr');
                                    //Hiển thị patient list
                                    row.innerHTML = '<td>' + (patient.patientCode || '-') + '</td>' +
                                            '<td>' + (patient.cccd || '-') + '</td>' +
                                            '<td>' + (patient.fullName || '-') + '</td>' +
                                            '<td>' + (patient.phone || '-') + '</td>' +
                                            '<td>' +
                                            '<div class="form-check">' +
                                            '<input type="radio" class="form-check-input select-patient" name="patientSelection" value="' + (patient.id || '') + '" data-id="' + (patient.id || '') + '">' +
                                            '</div>' +
                                            '</td>';
                                    ;
                                    tbody.appendChild(row);
                                });
                            }

                            // Reattach event listeners to new buttons
                            document.querySelectorAll('.select-patient').forEach(button => {
                                button.addEventListener('click', function () {
                                    const patientId = this.dataset.id;
                                    console.log('Chọn bệnh nhân, patientId:', patientId);
                                    if (!patientId) {
                                        console.error('patientId không hợp lệ!');
                                        return;
                                    }
                                    document.getElementById('patientId').value = patientId;
                                    document.getElementById('saveAssignmentBtn').disabled = false;
                                    console.log('Đã đặt patientId và kích hoạt nút Lưu');
                                });
                            });
                        })
                        .catch(err => {
                            console.error('Lỗi khi tải danh sách bệnh nhân:', err);
                            const tbody = document.getElementById('patientListBody');
                            if (tbody) {
                                tbody.innerHTML = '<tr><td colspan="5" class="text-center text-danger">Lỗi: Không thể tải dữ liệu.</td></tr>';
                            }
                            alert("Lỗi khi tải danh sách bệnh nhân!");
                        });
            }
            
            
                   

            // Event listeners for filter inputs and buttons
            const filterInputs = ['filterPatientCode', 'filterPatientCCCD', 'filterPatientName', 'filterPatientPhone'];
            filterInputs.forEach(inputId => {
                const input = document.getElementById(inputId);
                if (input) {
                    input.addEventListener('input', loadPatientList);
                    console.log(`Đã gắn sự kiện input cho ${inputId}`);
                } else {
                    console.error(`Không tìm thấy input ${inputId} trong DOM!`);
                }
            });


            const saveButton = document.getElementById('saveAssignmentBtn');
            if (saveButton) {
                saveButton.disabled = true;
                console.log('Nút Lưu đã được vô hiệu hóa ban đầu');
            } else {
                console.error('Không tìm thấy nút #saveAssignmentBtn!');
            }

            document.querySelectorAll('.modal').forEach(modal => {
                modal.addEventListener('hidden.bs.modal', function () {
                    console.log(`Modal ${modal.id} đang đóng`);
                    const backdrops = document.querySelectorAll('.modal-backdrop');
                    backdrops.forEach(backdrop => backdrop.remove());
                    document.body.classList.remove('modal-open');
                    document.body.style.overflow = '';
                    document.body.style.paddingRight = '';
                    console.log(`Modal ${modal.id} đã đóng, xóa backdrop`);
                    const patientIdInput = document.getElementById('patientId');
                    if (patientIdInput)
                        patientIdInput.value = '';
                    if (saveButton)
                        saveButton.disabled = true;
                    console.log('Đã reset form và vô hiệu hóa nút Lưu');
                });
            });
        });
    </script>



    <script>
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
