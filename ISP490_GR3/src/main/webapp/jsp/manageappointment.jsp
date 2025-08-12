<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Lịch hẹn</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/appointment.css">
        <style>
            .patient-list-wrapper {
                max-height: 300px;
                overflow-y: auto;
                border: 1px solid #dee2e6;
                border-radius: 4px;
            }
            .patient-list-wrapper table {
                margin-bottom: 0;
            }
        </style>
    </head>
    <body>
        <%
            Object userRole = session.getAttribute("userRole");
            boolean isAdmin = false;
            if (userRole != null) {
                if (userRole instanceof User.Role) {
                    isAdmin = ((User.Role) userRole) == User.Role.ADMIN;
                } else {
                    isAdmin = "ADMIN".equalsIgnoreCase(userRole.toString());
                }
            }
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
            <div class="sidebar-header"><h3>MENU</h3></div>
            <ul class="list-unstyled components">
                <li><a href="${pageContext.request.contextPath}/homepage"><i class="bi bi-house-door-fill"></i> Trang chủ</a></li>
                <li class="active"><a href="${pageContext.request.contextPath}/appointments"><i class="bi bi-calendar-check-fill"></i> Quản lý đặt lịch</a></li>
                <li><a href="${pageContext.request.contextPath}/checkin"><i class="bi bi-person-check-fill"></i> Quản lý check-in</a></li>
                <li><a href="${pageContext.request.contextPath}/queue"><i class="bi bi-people-fill"></i> Quản lý hàng đợi</a></li>
                <li><a href="${pageContext.request.contextPath}/doctor/patients"><i class="bi bi-people"></i> Hồ sơ bệnh nhân</a></li>
                <li><a href="${pageContext.request.contextPath}/receptionist/manage-doctor-schedule"><i class="bi bi-calendar-event-fill"></i> Quản lý lịch bác sĩ</a></li>
                <li><a href="${pageContext.request.contextPath}/receptionist/report"><i class="bi bi-speedometer2"></i> Báo cáo thống kê</a></li>
            </ul>
        </nav>

        <div id="content">
            <nav class="navbar navbar-expand-lg top-navbar">
                <div class="container-fluid">
                    <button type="button" id="sidebarCollapse" class="btn btn-primary"><i class="bi bi-list"></i></button>
                    <div style="margin-left: 60px;"><h3><span style="color: #007bff;">Ánh Dương</span> <span style="color: #333;">Clinic</span></h3></div>

                    <div class="dropdown user-dropdown">
                        <button class="btn dropdown-toggle" type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                            <div class="user-profile-icon"><i class="bi bi-person-fill" style="font-size: 1.5rem;"></i></div>
                            <div class="user-info d-none d-md-block"><div class="user-name"><%= userName %></div><div class="user-role"><%= userRoleDisplay %></div></div>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                            <% if (isAdmin) { %>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/authorization"><i class="bi bi-people-fill"></i> Quản lý người dùng</a></li>
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

            <div class="content-wrapper">
                <div class="breadcrumb-container">
                    <nav aria-label="breadcrumb" style="margin-top:17px;">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/homepage"> Trang chủ</a></li>
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/appointments">Quản lý đặt lịch</a></li>
                            <li class="breadcrumb-item active" aria-current="page">Lịch hẹn</li>
                        </ol>
                    </nav>
                </div>
            </div>
            <ul class="nav nav-tabs w-100">
                <li class="nav-item flex-fill text-center"><a class="nav-link active" href="${pageContext.request.contextPath}/appointments">Lịch hẹn</a></li>
                <li class="nav-item flex-fill text-center"><a class="nav-link" href="${pageContext.request.contextPath}/checkin">Check-in</a></li>
                <li class="nav-item flex-fill text-center"><a class="nav-link" href="${pageContext.request.contextPath}/slot">Slot</a></li>
            </ul>

            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show mt-3" role="alert">
                    ${sessionScope.message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <% session.removeAttribute("message"); session.removeAttribute("messageType"); %>
            </c:if>

            <!-- Hiển thị tiêu chí tìm kiếm -->
            <div class="alert alert-info mt-3">
                Kết quả tìm kiếm cho:
                Mã lịch hẹn: ${appointmentCodeDisplay},
                Mã bệnh nhân: ${patientCodeDisplay},
                Bác sĩ: ${doctorDisplay},
                Dịch vụ: ${servicesDisplay},
                Trạng thái: ${statusDisplay},
                Ngày: ${slotDateDisplay}
            </div>

            <!-- Form tìm kiếm sử dụng GET -->
            <form id="searchForm" action="${pageContext.request.contextPath}/appointments" method="get" class="mb-4 mt-3 p-3 border rounded shadow-sm bg-light">
                <input type="hidden" name="action" value="search"/>
                <div class="row mb-3">
                    <div class="col-md-4">
                        <label for="appointmentCode" class="form-label">Mã lịch hẹn</label>
                        <input type="text" class="form-control" id="appointmentCode" name="appointmentCode" placeholder="Nhập" value="${param.appointmentCode}">
                    </div>
                    <div class="col-md-4">
                        <label for="slotDate" class="form-label">Ngày hẹn</label>
                        <div class="input-group">
                            <input type="date" class="form-control" id="slotDate" name="slotDate" value="${param.slotDate}">
                            <!--                            <button type="button" class="btn btn-outline-secondary" onclick="document.getElementById('slotDate').value = ''">Tất cả ngày</button>-->
                        </div>
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
                                <option value="${d.id}" ${param.doctorId == d.id ? 'selected' : ''}>${d.fullName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label for="servicesId" class="form-label">Dịch vụ</label>
                        <select id="servicesId" name="servicesId" class="form-select">
                            <option value="">--Chọn--</option>
                            <c:forEach var="s" items="${serviceList}">
                                <option value="${s.servicesId}" ${param.servicesId == s.servicesId ? 'selected' : ''}>${s.serviceName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label for="status" class="form-label">Trạng thái</label>
                        <select id="status" name="status" class="form-select">
                            <option value="">--Chọn--</option>
                            <c:forEach var="status" items="${statusList}">
                                <option value="${status}" ${param.status == status ? 'selected' : ''}>
                                    <c:choose>
                                        <c:when test="${status == 'pending'}">Chờ xác nhận</c:when>
                                        <c:when test="${status == 'confirmed'}">Đã xác nhận</c:when>
                                        <c:when test="${status == 'done'}">Đã khám</c:when>
                                        <c:when test="${status == 'cancelled'}">Đã hủy</c:when>
                                        <c:when test="${status == 'no_show'}">Quá giờ</c:when>
                                        <c:when test="${status == 'expired'}">Hết hạn</c:when>
                                        <c:otherwise>${status}</c:otherwise>
                                    </c:choose>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <input type="hidden" name="recordsPerPage" value="${recordsPerPage != null ? recordsPerPage : 10}">
                </div>
                <div class="d-flex justify-content-end gap-2">
                    <a href="${pageContext.request.contextPath}/appointments" class="btn btn-light border"><i class="bi bi-arrow-clockwise"></i> Đặt lại bộ lọc</a>
                    <button type="submit" name="submitSearch" value="true" class="btn btn-primary"><i class="bi bi-search"></i> Tìm kiếm</button>
                </div>
            </form>

            <div class="appointment-list-section animate-fade-in">
                <div class="appointment-list-header">
                    <h5>Danh sách Lịch hẹn (${totalRecords} kết quả)</h5>

                </div>

                <div class="table-responsive">
                    <table class="table table-hover table-bordered table-appointments">
                        <thead class="table-primary">
                            <tr>
                                <!--                                <th><input type="checkbox" id="checkAll"></th>-->
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
<!--                                    <td><input type="checkbox" name="selectedAppointments" value="${appointment.id}" class="appointment-checkbox"/></td>-->
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
                                            <c:when test="${appointment.status == 'no_show'}">Quá giờ</c:when>
                                            <c:when test="${appointment.status == 'expired'}">Hết hạn</c:when>
                                            <c:otherwise>${appointment.status}</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${empty appointment.patientCode}">
                                                <a href="#" class="btn btn-sm btn-outline-success btn-assign" title="Gán bệnh nhân vào lịch hẹn" data-bs-toggle="modal" data-bs-target="#addAppointmentModal" data-id="${appointment.id}">
                                                    <i class="bi bi-person-plus"> Gán </i>
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="#" class="btn btn-sm btn-outline-warning btn-remove-patient" title="Bỏ gán bệnh nhân khỏi lịch hẹn" data-bs-toggle="modal" data-bs-target="#confirmRemovePatientModal" data-id="${appointment.id}" data-code="${appointment.appointmentCode}">
                                                    <i class="bi bi-person-x"> Bỏ gán </i>
                                                </a>
                                                <a href="#" class="btn btn-sm btn-outline-primary btn-change-slot" title="Đổi slot cho lịch hẹn" data-bs-toggle="modal" data-bs-target="#changeSlotModal" data-id="${appointment.id}" data-code="${appointment.appointmentCode}">
                                                    <i class="bi bi-arrow-repeat"> Đổi slot </i>
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty currentPageAppointments}">
                                <tr><td colspan="12" class="text-center text-muted">Không có lịch hẹn nào được tìm thấy.</td></tr>
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
                                <a class="page-link" href="${pageContext.request.contextPath}/appointments?page=${currentPage - 1}&appointmentCode=${param.appointmentCode}&patientCode=${param.patientCode}&doctorId=${param.doctorId}&servicesId=${param.servicesId}&status=${param.status}&slotDate=${param.slotDate}&submitSearch=true" aria-label="Previous">
                                    <span aria-hidden="true">«</span>
                                </a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/appointments?page=${i}&appointmentCode=${param.appointmentCode}&patientCode=${param.patientCode}&doctorId=${param.doctorId}&servicesId=${param.servicesId}&status=${param.status}&slotDate=${param.slotDate}&submitSearch=true">${i}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage == totalPages || totalPages == 0 ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/appointments?page=${currentPage + 1}&appointmentCode=${param.appointmentCode}&patientCode=${param.patientCode}&doctorId=${param.doctorId}&servicesId=${param.servicesId}&status=${param.status}&slotDate=${param.slotDate}&submitSearch=true" aria-label="Next">
                                    <span aria-hidden="true">»</span>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>

            <!-- Modal xác nhận bỏ gán bệnh nhân -->
            <div class="modal fade" id="confirmRemovePatientModal" tabindex="-1" aria-labelledby="confirmRemovePatientModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content shadow-lg rounded-4">
                        <div class="modal-header">
                            <h5 class="modal-title fw-bold" id="confirmRemovePatientModalLabel">Xác nhận bỏ gán bệnh nhân</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body px-4 py-3">
                            <p>Bạn có chắc chắn muốn bỏ gán bệnh nhân khỏi lịch hẹn <strong id="appointmentCodeDisplay"></strong> không?</p>
                            <div id="removeErrorMsg" class="alert alert-danger d-none" role="alert"></div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="button" class="btn btn-danger" id="confirmRemoveBtn">Xác nhận</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Modal gán bệnh nhân -->
            <div class="modal fade" id="addAppointmentModal" tabindex="-1" aria-labelledby="addAppointmentModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered modal-xl">
                    <div class="modal-content shadow-lg rounded-4">
                        <div class="modal-header">
                            <h5 class="modal-title fw-bold" id="addAppointmentModalLabel">Gán bệnh nhân vào lịch hẹn</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <form id="addAppointmentForm" action="${pageContext.request.contextPath}/appointments/add" method="post">
                            <input type="hidden" name="action" value="assignPatient"/>
                            <input type="hidden" id="appointmentId" name="appointmentId"/>
                            <input type="hidden" id="patientId" name="patientId"/>
                            <div class="modal-body px-4 py-3">
                                <div class="mb-4 border-bottom pb-3">
                                    <div class="row g-2">
                                        <div class="col-md-12">
                                            <label for="serviceSelect" class="form-label">Dịch vụ <span class="text-danger">*</span></label>
                                            <select class="form-select" id="serviceSelect" name="servicesId">
                                                <option value="">--Chọn--</option>
                                                <c:forEach var="s" items="${serviceList}">
                                                    <option value="${s.servicesId}" ${param.servicesId == s.servicesId ? 'selected' : ''}>${s.serviceName}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-body px-4 pt-0">
                                <div class="mb-4 border-bottom pb-3">
                                    <h6 class="fw-bold mb-3">Tìm kiếm bệnh nhân</h6>
                                    <div class="row g-2">
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
                                <div class="mt-4">
                                    <h6 class="fw-bold mb-3">Danh sách bệnh nhân <span class="text-danger">*</span></h6>
                                    <div class="patient-list-wrapper">
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
                                                <tbody id="patientListBody"></tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-body px-4 pt-0">
                                <div id="assignErrorMsg" class="alert alert-danger d-none" role="alert"></div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                <button type="submit" class="btn btn-primary" id="saveAssignmentBtn" disabled>Gán</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Modal đổi slot -->
            <div class="modal fade" id="changeSlotModal" tabindex="-1" aria-labelledby="changeSlotModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered modal-lg">
                    <div class="modal-content shadow-lg rounded-4">
                        <div class="modal-header">
                            <h5 class="modal-title fw-bold" id="changeSlotModalLabel">Đổi slot cho lịch hẹn</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <form id="changeSlotForm">
                            <input type="hidden" id="changeAppointmentId" />
                            <div class="modal-body px-4 py-3">
                                <div class="mb-4 border-bottom pb-3">
                                    <h6 class="fw-bold mb-3">Chọn ngày và slot trống</h6>
                                    <div class="row g-2">
                                        <div class="col-md-12">
                                            <label for="filterSlotDate" class="form-label">Ngày slot</label>
                                            <input type="date" class="form-control" id="filterSlotDate">
                                        </div>
                                    </div>
                                </div>
                                <div class="mt-4">
                                    <h6 class="fw-bold mb-3">Danh sách slot trống</h6>
                                    <div class="patient-list-wrapper"> <!-- Reuse style for scrollable list -->
                                        <div class="table-responsive">
                                            <table class="table table-hover table-bordered" id="availableSlotsTable">
                                                <thead>
                                                    <tr>
                                                        <th>Ngày</th>
                                                        <th>Thời gian</th>
                                                        <th>Bác sĩ</th>
                                                        <th>Số bệnh nhân tối đa</th>
                                                        <th>Đã đặt</th>
                                                        <th>Chọn</th>
                                                    </tr>
                                                </thead>
                                                <tbody id="availableSlotsBody"></tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-body px-4 pt-0">
                                <div id="changeSlotErrorMsg" class="alert alert-danger d-none" role="alert"></div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                <button type="submit" class="btn btn-primary" id="saveChangeSlotBtn" disabled>Đổi slot</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <style>
                .patient-list-wrapper {
                    max-height: 300px;
                    overflow-y: auto;
                    border: 1px solid #dee2e6;
                    border-radius: 4px;
                }
                .patient-list-wrapper table {
                    margin-bottom: 0;
                }
            </style>

            <script>
                <%
                    String scheme = request.getScheme();
                    String serverName = request.getServerName();
                    int serverPort = request.getServerPort();
                    String contextPath = request.getContextPath();
                    String baseURL = scheme + "://" + serverName + ":" + serverPort + contextPath;
                %>
                const BASE_URL = '<%= baseURL %>';
                console.log("BASE_URL:", BASE_URL);

                document.addEventListener("DOMContentLoaded", function () {



                    // Xử lý nút bỏ gán
                    document.querySelectorAll('.btn-remove-patient').forEach(button => {
                        button.addEventListener('click', function (event) {
                            event.preventDefault();
                            const appointmentId = this.dataset.id;
                            const appointmentCode = this.dataset.code;
                            console.log('Nhấp vào nút Bỏ gán, appointmentId:', appointmentId, 'appointmentCode:', appointmentCode);

                            if (!appointmentId || !appointmentCode) {
                                console.error('appointmentId hoặc appointmentCode không tồn tại trong dataset!');
                                return;
                            }

                            const appointmentCodeDisplay = document.getElementById('appointmentCodeDisplay');
                            if (appointmentCodeDisplay) {
                                appointmentCodeDisplay.textContent = appointmentCode;
                            } else {
                                console.error('Không tìm thấy #appointmentCodeDisplay!');
                                return;
                            }

                            const errorBox = document.getElementById('removeErrorMsg');
                            if (errorBox) {
                                errorBox.classList.add('d-none');
                                errorBox.textContent = '';
                                console.log('Reset lỗi bỏ gán bệnh nhân');
                            }

                            const modalEl = document.getElementById('confirmRemovePatientModal');
                            if (!modalEl) {
                                console.error('Không tìm thấy modal #confirmRemovePatientModal!');
                                return;
                            }

                            const modal = new bootstrap.Modal(modalEl);
                            modal.show();
                            console.log('Modal xác nhận bỏ gán đã được hiển thị');

                            const confirmRemoveBtn = document.getElementById('confirmRemoveBtn');
                            if (confirmRemoveBtn) {
                                const newConfirmBtn = confirmRemoveBtn.cloneNode(true);
                                confirmRemoveBtn.parentNode.replaceChild(newConfirmBtn, confirmRemoveBtn);

                                newConfirmBtn.addEventListener('click', function () {
                                    console.log('Xác nhận bỏ gán, appointmentId:', appointmentId);
                                    const url = BASE_URL + "/appointments/remove-patient";
                                    const params = new URLSearchParams({
                                        action: 'removePatient',
                                        appointmentId: appointmentId
                                    });

                                    fetch(url, {
                                        method: 'POST',
                                        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                        body: params
                                    })
                                            .then(res => {
                                                console.log('Response status:', res.status);
                                                return res.json();
                                            })
                                            .then(data => {
                                                console.log('Phản hồi từ server:', data);
                                                if (data.success) {
                                                    if (errorBox) {
                                                        errorBox.classList.add('d-none');
                                                        errorBox.textContent = '';
                                                    }
                                                    alert('Bỏ gán bệnh nhân thành công!');
                                                    modal.hide();
                                                    window.location.reload();
                                                } else {
                                                    if (errorBox) {
                                                        errorBox.classList.remove('d-none');
                                                        errorBox.textContent = data.message || 'Không thể bỏ gán bệnh nhân';
                                                    } else {
                                                        alert('Lỗi: ' + (data.message || 'Không thể bỏ gán bệnh nhân'));
                                                    }
                                                }
                                            })
                                            .catch(err => {
                                                console.error('Lỗi khi bỏ gán bệnh nhân:', err);
                                                if (errorBox) {
                                                    errorBox.classList.remove('d-none');
                                                    errorBox.textContent = 'Lỗi khi bỏ gán bệnh nhân: ' + err.message;
                                                } else {
                                                    alert('Lỗi khi bỏ gán bệnh nhân: ' + err.message);
                                                }
                                            });
                                });
                            } else {
                                console.error('Không tìm thấy nút #confirmRemoveBtn!');
                            }
                        });
                    });

                    // Xử lý nút gán bệnh nhân
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

                            const errorBox = document.getElementById('assignErrorMsg');
                            if (errorBox) {
                                errorBox.classList.add('d-none');
                                errorBox.textContent = '';
                                console.log('Reset lỗi gán bệnh nhân');
                            }

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

                            setTimeout(() => {
                                loadPatientList();
                            }, 300);
                        });
                    });

                    // Xử lý nút đổi slot
                    document.querySelectorAll('.btn-change-slot').forEach(button => {
                        button.addEventListener('click', function (event) {
                            event.preventDefault();
                            const appointmentId = this.dataset.id;
                            const appointmentCode = this.dataset.code;
                            console.log('Nhấp vào nút Đổi slot, appointmentId:', appointmentId, 'appointmentCode:', appointmentCode);

                            if (!appointmentId) {
                                console.error('appointmentId không tồn tại trong dataset!');
                                return;
                            }

                            const modalEl = document.getElementById('changeSlotModal');
                            if (!modalEl) {
                                console.error('Không tìm thấy modal #changeSlotModal!');
                                return;
                            }

                            document.getElementById('changeAppointmentId').value = appointmentId;
                            console.log('Đã đặt appointmentId vào form đổi slot:', appointmentId);

                            const errorBox = document.getElementById('changeSlotErrorMsg');
                            if (errorBox) {
                                errorBox.classList.add('d-none');
                                errorBox.textContent = '';
                                console.log('Reset lỗi đổi slot');
                            }

                            // Reset filter date
                            const filterSlotDate = document.getElementById('filterSlotDate');
                            if (filterSlotDate) {
                                filterSlotDate.value = '';
                            }

                            const modal = new bootstrap.Modal(modalEl);
                            modal.show();
                            console.log('Modal đổi slot đã được hiển thị');

                            // Load available slots initially (without date filter)
                            setTimeout(() => {
                                loadAvailableSlots();
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
                            headers: {'Content-Type': 'application/json'}
                        })
                                .then(res => {
                                    console.log('Response status:', res.status);
                                    if (!res.ok)
                                        throw new Error(`HTTP error! status: ${res.status}`);
                                    return res.json();
                                })
                                .then(data => {
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
                                            console.log('Đã đặt patientId và kích hoạt nút Gán');
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

                    // Load danh sách slot trống
                    function loadAvailableSlots() {
                        console.log('Bắt đầu loadAvailableSlots...');
                        const slotDate = document.getElementById('filterSlotDate').value.trim();
                        const appointmentId = document.getElementById('changeAppointmentId').value;
                        console.log('Tham số lọc:', {slotDate, appointmentId});


                        const query = new URLSearchParams({
                            date: slotDate, // Nếu không có ngày, backend có thể trả tất cả slot trống
                            appointmentId: appointmentId
                        }).toString();
                        const url = BASE_URL + "/slots/available?" + query; // Giả sử endpoint /slots/available để lấy slot trống
                        console.log('URL đầy đủ:', url);
                        fetch(url, {
                            method: 'GET',
                            headers: {'Content-Type': 'application/json'}
                        })
                                .then(res => {
                                    console.log('Response status:', res.status);
                                    if (!res.ok)
                                        throw new Error(`HTTP error! status: ${res.status}`);
                                    return res.json();
                                })
                                .then(data => {
                                    console.log('Dữ liệu slot nhận được từ server:', data);
                                    const tbody = document.getElementById('availableSlotsBody');
                                    if (!tbody) {
                                        console.error('Không tìm thấy tbody #availableSlotsBody!');
                                        return;
                                    }
                                    tbody.innerHTML = '';
                                    if (!Array.isArray(data) || data.length === 0) {
                                        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Không tìm thấy slot trống.</td></tr>';
                                    } else {
                                        data.forEach(slot => {
                                            const row = document.createElement('tr');
                                            row.innerHTML = '<td>' + (slot.slotDate || '-') + '</td>' +
                                                    '<td>' + (slot.checkinRange || '-') + '</td>' +
                                                    '<td>' + (slot.doctorName || '-') + '</td>' +
                                                    '<td>' + (slot.maxPatients || '-') + '</td>' + // Thêm cột maxPatients
                                                    '<td>' + (slot.bookedPatients || '0') + '/' + (slot.maxPatients || '-') + '</td>' + // Thêm cột bookedPatients
                                                    '<td>' +
                                                    '<div class="form-check">' +
                                                    '<input type="radio" class="form-check-input select-slot" name="slotSelection" value="' + (slot.id || '') + '" data-id="' + (slot.id || '') + '">' +
                                                    '</div>' +
                                                    '</td>';

                                            tbody.appendChild(row);
                                        });
                                    }

                                    // Xử lý chọn slot
                                    document.querySelectorAll('.select-slot').forEach(radio => {
                                        radio.addEventListener('click', function () {
                                            const slotId = this.dataset.id;
                                            console.log('Chọn slot, slotId:', slotId);
                                            if (!slotId) {
                                                console.error('slotId không hợp lệ!');
                                                return;
                                            }
                                            document.getElementById('saveChangeSlotBtn').disabled = false;
                                            console.log('Kích hoạt nút Đổi slot');
                                        });
                                    });
                                })
                                .catch(err => {
                                    console.error('Lỗi khi tải danh sách slot trống:', err);
                                    const tbody = document.getElementById('availableSlotsBody');
                                    if (tbody) {
                                        tbody.innerHTML = '<tr><td colspan="4" class="text-center text-danger">Lỗi: Không thể tải dữ liệu.</td></tr>';
                                    }
                                    alert("Lỗi khi tải danh sách slot trống!");
                                });
                    }




                    // Xử lý submit form gán bệnh nhân
                    const form = document.getElementById('addAppointmentForm');
                    if (form) {
                        form.addEventListener('submit', function (event) {
                            event.preventDefault();
                            console.log('Submit form gán bệnh nhân');
                            const serviceId = document.getElementById('serviceSelect').value;
                            const appointmentId = document.getElementById('appointmentId').value;
                            const patientId = document.getElementById('patientId').value;
                            const errorBox = document.getElementById('assignErrorMsg');

                            if (errorBox) {
                                errorBox.classList.add('d-none');
                                errorBox.textContent = '';
                            }

                            if (!appointmentId || !patientId || !serviceId) {
                                if (errorBox) {
                                    errorBox.classList.remove('d-none');
                                    errorBox.textContent = 'Vui lòng chọn đầy đủ bệnh nhân và dịch vụ!';
                                } else {
                                    alert('Vui lòng chọn đầy đủ bệnh nhân và dịch vụ!');
                                }
                                return;
                            }

                            const url = BASE_URL + "/appointments/add";
                            const params = new URLSearchParams({
                                action: 'assignPatient',
                                appointmentId: appointmentId,
                                patientId: patientId,
                                servicesId: serviceId
                            });
                            fetch(url, {
                                method: 'POST',
                                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                body: params
                            })
                                    .then(res => {
                                        console.log('Response status:', res.status);
                                        return res.json();
                                    })
                                    .then(data => {
                                        console.log('Phản hồi từ server:', data);
                                        if (data.type === "warning") {
                                            if (confirm(data.message)) {
                                                params.append("ignoreWarning", "true");
                                                fetch(url, {
                                                    method: 'POST',
                                                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                                    body: params
                                                })
                                                        .then(res2 => res2.json())
                                                        .then(data2 => {
                                                            if (data2.success) {
                                                                alert('Gán bệnh nhân thành công!');
                                                                const modal = bootstrap.Modal.getInstance(document.getElementById('addAppointmentModal'));
                                                                modal.hide();
                                                                window.location.reload();
                                                            } else {
                                                                if (errorBox) {
                                                                    errorBox.classList.remove('d-none');
                                                                    errorBox.textContent = data2.message || 'Không thể gán bệnh nhân';
                                                                } else {
                                                                    alert('Lỗi: ' + (data2.message || 'Không thể gán bệnh nhân'));
                                                                }
                                                            }
                                                        })
                                                        .catch(err2 => {
                                                            console.error('Lỗi khi gán bệnh nhân (ignore):', err2);
                                                            if (errorBox) {
                                                                errorBox.classList.remove('d-none');
                                                                errorBox.textContent = 'Lỗi khi gán bệnh nhân: ' + err2.message;
                                                            } else {
                                                                alert('Lỗi khi gán bệnh nhân: ' + err2.message);
                                                            }
                                                        });
                                            }
                                        } else if (data.success) {
                                            if (errorBox) {
                                                errorBox.classList.add('d-none');
                                                errorBox.textContent = '';
                                            }
                                            alert('Gán bệnh nhân thành công!');
                                            const modal = bootstrap.Modal.getInstance(document.getElementById('addAppointmentModal'));
                                            modal.hide();
                                            window.location.reload();
                                        } else {
                                            if (errorBox) {
                                                errorBox.classList.remove('d-none');
                                                errorBox.textContent = data.message || 'Không thể gán bệnh nhân';
                                            } else {
                                                alert('Lỗi: ' + (data.message || 'Không thể gán bệnh nhân'));
                                            }
                                        }
                                    })
                                    .catch(err => {
                                        console.error('Lỗi khi gán bệnh nhân:', err);
                                        if (errorBox) {
                                            errorBox.classList.remove('d-none');
                                            errorBox.textContent = 'Lỗi khi gán bệnh nhân: ' + err.message;
                                        } else {
                                            alert('Lỗi khi gán bệnh nhân: ' + err.message);
                                        }
                                    });
                        });
                    }

                    const changeSlotForm = document.getElementById('changeSlotForm');
                    if (changeSlotForm) {
                        changeSlotForm.addEventListener('submit', function (event) {
                            event.preventDefault();
                            console.log('Submit form đổi slot');
                            const appointmentId = document.getElementById('changeAppointmentId').value;
                            const selectedSlot = document.querySelector('input[name="slotSelection"]:checked');
                            const slotId = selectedSlot ? selectedSlot.value : null;
                            const errorBox = document.getElementById('changeSlotErrorMsg');

                            console.log('Params trước khi gửi:', {
                                appointmentId: appointmentId,
                                newSlotId: slotId
                            });

                            if (errorBox) {
                                errorBox.classList.add('d-none');
                                errorBox.textContent = '';
                            }

                            if (!appointmentId || !slotId) {
                                if (errorBox) {
                                    errorBox.classList.remove('d-none');
                                    errorBox.textContent = 'Vui lòng chọn slot mới!';
                                } else {
                                    alert('Vui lòng chọn slot mới!');
                                }
                                return;
                            }

                            const url = BASE_URL + "/appointments/change-slot";
                            const params = new URLSearchParams({
                                appointmentId: appointmentId,
                                newSlotId: slotId
                            });

                            console.log('URL và params đầy đủ:', url, params.toString());

                            function sendChangeSlotRequest(params) {
                                const url = '/appointments/change-slot'; // Xác minh endpoint chính xác
                                const errorBox = document.getElementById('errorBox'); // Giả định ID của errorBox

                                fetch(url, {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                    body: params
                                })
                                        .then(res => {
                                            console.log('Trạng thái phản hồi từ server:', res.status, 'Content-Type:', res.headers.get('Content-Type'));
                                            // Kiểm tra content type trước khi parse JSON
                                            if (!res.headers.get('Content-Type')?.includes('application/json')) {
                                                return res.text().then(text => {
                                                    throw new Error(`Phản hồi không phải JSON: ${text.substring(0, 50)}...`);
                                                });
                                            }
                                            return res.json().then(data => ({status: res.status, data}));
                                        })
                                        .then(({status, data}) => {
                                            console.log('Phản hồi JSON từ server:', data);
                                            if (!data || typeof data !== 'object') {
                                                throw new Error('Phản hồi JSON không hợp lệ');
                                            }

                                            if (data.type === "warning") {
                                                if (confirm(data.message)) {
                                                    params.append("ignoreWarning", "true");
                                                    console.log('Gửi lại với ignoreWarning=true:', params.toString());
                                                    sendChangeSlotRequest(params); // Gửi lại
                                                }
                                            } else if (data.success) {
                                                if (errorBox) {
                                                    errorBox.classList.add('d-none');
                                                    errorBox.textContent = '';
                                                }
                                                alert(`Đổi slot thành công! (Mã: ${data.messageCode || 'MSG155'})`);
                                                const modal = bootstrap.Modal.getInstance(document.getElementById('changeSlotModal'));
                                                modal.hide();
                                                window.location.reload();
                                            } else {
                                                const errorMessage = data.message || `Không thể đổi slot (Mã: ${data.messageCode || 'Không xác định'})`;
                                                if (errorBox) {
                                                    errorBox.classList.remove('d-none');
                                                    errorBox.textContent = errorMessage;
                                                } else {
                                                    alert(`Lỗi: ${errorMessage}`);
                                                }
                                        }
                                        })
                                        .catch(err => {
                                            console.error('Không thể đổi slot', err);
                                            let errorMessage = err.message;
                                            // Xử lý trường hợp chuyển hướng đến trang đăng nhập
                                            if (err.message.includes('<!doctype')) {
                                                errorMessage = 'Phiên hết hạn hoặc không có quyền truy cập. Vui lòng đăng nhập lại.';
                                            }
                                            if (errorBox) {
                                                errorBox.classList.remove('d-none');
                                                errorBox.textContent = `Lỗi khi đổi slot: ${errorMessage}`;
                                            } else {
                                                alert(`Lỗi khi đổi slot: ${errorMessage}`);
                                            }
                                            // Chuyển hướng đến trang đăng nhập nếu cần
                                            if (errorMessage.includes('Vui lòng đăng nhập lại')) {
                                                window.location.href = '/jsp/landing.jsp';
                                            }
                                        });
                            }

                            sendChangeSlotRequest(params);
                        });
                    }

                    // Gắn sự kiện input cho tìm kiếm bệnh nhân
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

                    // Gắn sự kiện change cho filter ngày slot
                    const filterSlotDate = document.getElementById('filterSlotDate');
                    if (filterSlotDate) {
                        filterSlotDate.addEventListener('change', loadAvailableSlots);
                        console.log('Đã gắn sự kiện change cho filterSlotDate');
                    } else {
                        console.error('Không tìm thấy input #filterSlotDate!');
                    }

                    // Xử lý sidebar
                    const sidebarCollapse = document.getElementById('sidebarCollapse');
                    const sidebar = document.getElementById('sidebar');
                    const content = document.getElementById('content');
                    let userToggled = false;

                    sidebarCollapse.addEventListener('click', function () {
                        sidebar.classList.toggle('collapsed');
                        content.classList.toggle('expanded');
                        userToggled = true;
                    });

                    function checkWidth() {
                        if (!userToggled) {
                            if (window.innerWidth <= 768) {
                                sidebar.classList.add('collapsed');
                                content.classList.add('expanded');
                            } else {
                                sidebar.classList.remove('collapsed');
                                content.classList.remove('expanded');
                            }
                        }
                    }

                    checkWidth();
                    window.addEventListener('resize', checkWidth);

                    document.querySelectorAll('.modal').forEach(modal => {
                        modal.addEventListener('hidden.bs.modal', function () {
                            const backdrops = document.querySelectorAll('.modal-backdrop');
                            backdrops.forEach(backdrop => backdrop.remove());
                            document.body.classList.remove('modal-open');
                            document.body.style.overflow = '';
                            document.body.style.paddingRight = '';
                            console.log(`Modal ${modal.id} đã đóng, xóa backdrop`);
                            const patientIdInput = document.getElementById('patientId');
                            if (patientIdInput)
                                patientIdInput.value = '';
                            const saveButton = document.getElementById('saveAssignmentBtn');
                            if (saveButton)
                                saveButton.disabled = true;
                            // Reset cho modal đổi slot
                            if (modal.id === 'changeSlotModal') {
                                const saveChangeBtn = document.getElementById('saveChangeSlotBtn');
                                if (saveChangeBtn)
                                    saveChangeBtn.disabled = true;
                            }
                        });
                    });

                    window.GLOBAL_IS_SEARCH_PERFORMED = ${requestScope.searchPerformed != null ? requestScope.searchPerformed : false};
                    window.GLOBAL_HAS_RESULTS = ${requestScope.hasResults != null ? requestScope.hasResults : false};
                });
            </script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>