<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.List"%>
<%@page import="com.mycompany.isp490_gr3.model.Doctor"%>
<%@page import="java.time.LocalDate"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Lịch Làm Việc Bác Sĩ (Lễ tân)</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" xintegrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    
    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/css/manage-doctor-schedule.css" rel="stylesheet" type="text/css"/>
    
    <!-- Flatpickr CSS for Date Range Picker -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
</head>
<body>
    <div class="container">
        <div class="card">
            <div class="card-header">
                🗓️ Quản lý Lịch Làm Việc Bác Sĩ (Dành cho Lễ tân)
            </div>
            <div class="card-body p-4">
                <p class="text-center text-muted mb-4">
                    Chào mừng lễ tân! Tại đây bạn có thể chọn bác sĩ và chỉnh sửa lịch làm việc của họ.
                </p>

                <div id="messageContainer">
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show rounded-md" role="alert">
                            ${errorMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success alert-dismissible fade show rounded-md" role="alert">
                            ${successMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                </div>

                <!-- Doctor List -->
                <div class="mb-4 p-4 bg-light rounded-lg shadow-sm">
                    <h2 class="text-primary mb-3">
                        🧑‍⚕️ Danh sách Bác sĩ
                    </h2>
                    <div class="table-responsive">
                        <table class="table table-hover table-striped">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Tên đầy đủ</th>
                                    <th>Điện thoại</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty doctors}">
                                        <c:forEach var="doctor" items="${doctors}">
                                            <tr>
                                                <td>${doctor.id}</td>
                                                <td>${doctor.fullName}</td>
                                                <td>${doctor.phone}</td>
                                                <td>
                                                    <button type="button" class="btn btn-info btn-sm edit-schedule-btn" data-doctor-id="${doctor.id}" data-doctor-account-id="${doctor.accountId}" data-doctor-name="${doctor.fullName}">
                                                        Chỉnh sửa lịch
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="4" class="text-center text-muted">
                                                Không tìm thấy bác sĩ nào.
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Edit Schedule Modal -->
    <div class="modal fade" id="editScheduleModal" tabindex="-1" aria-labelledby="editScheduleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-xl modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header bg-primary text-white rounded-top-lg">
                    <h5 class="modal-title" id="editScheduleModalLabel">
                        Chỉnh sửa lịch làm việc cho <span id="modalDoctorName" class="fw-bold"></span>
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <form id="modalScheduleForm" action="${pageContext.request.contextPath}/doctor/schedule" method="POST">
                        <input type="hidden" id="modalDoctorId" name="doctor_id">
                        <input type="hidden" id="modalDoctorAccountId" name="doctor_account_id">

                        <!-- Schedule Type Selection -->
                        <div class="mb-4 p-3 border rounded bg-light">
                            <h6 class="mb-3">
                                Chọn khoảng thời gian mà bệnh nhân có thể đặt lịch khám:
                            </h6>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="schedulePeriod" id="periodFuture" value="future" checked>
                                <label class="form-check-label" for="periodFuture">
                                    Tiếp tục trong tương lai
                                </label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="schedulePeriod" id="periodRange" value="range">
                                <label class="form-check-label" for="periodRange">
                                    Trong một khoảng thời gian
                                </label>
                            </div>
                            <div class="mt-3" id="dateRangePickerContainer" style="display: none;">
                                <label for="dateRangePicker" class="form-label">Khoảng ngày:</label>
                                <input type="text" id="dateRangePicker" class="form-control" placeholder="Chọn ngày bắt đầu và kết thúc">
                            </div>
                        </div>

                        <!-- Appointment Duration -->
                        <div class="mb-4 p-3 border rounded bg-light">
                            <label for="appointmentDuration" class="form-label fw-bold">
                                Thời gian cuộc hẹn mặc định (phút):
                            </label>
                            <select class="form-select" id="appointmentDuration" name="appointment_duration">
                                <option value="15">15 phút</option>
                                <option value="30" selected>30 phút</option>
                                <option value="45">45 phút</option>
                                <option value="60">60 phút</option>
                            </select>
                        </div>
                        
                        <!-- Consolidated General Consultation Settings (replaces tabs) -->
                        <div class="mb-4 p-3 border rounded bg-light">
                            <h6 class="mb-3">
                                Cài đặt lịch làm việc chung:
                            </h6>
                            <%-- Đã bỏ phần "Thời gian chuẩn bị/chờ (trước mỗi cuộc hẹn)" --%>
                            <!-- Weekly Schedule -->
                            <h6 class="mt-4 mb-3">Chỉnh sửa lịch làm việc hàng tuần:</h6>
                            <div id="weeklySchedule">
                                <!-- Days of the week will be dynamically added here by JS -->
                            </div>
                        </div>

                        <div class="modal-footer d-flex justify-content-between align-items-center mt-4">
                            <div id="modalMessageContainer"></div>
                            <button type="submit" class="btn btn-primary" id="saveScheduleBtn">
                                Lưu lịch
                                <span id="modalLoadingSpinner" class="spinner-border spinner-border-sm ms-2 d-none" role="status" aria-hidden="true"></span>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Apply Similar Overlay (Hidden by default) -->
    <div id="applySimilarOverlay" class="apply-similar-overlay card p-3 position-absolute d-none">
        <h6 class="mb-3">Áp dụng tương tự cho:</h6>
        <div id="applySimilarCheckboxes">
            <!-- Checkboxes for days will be dynamically added here -->
        </div>
        <div class="d-flex justify-content-end mt-3">
            <button type="button" class="btn btn-secondary btn-sm me-2" id="cancelApplySimilarBtn">Hủy</button>
            <button type="button" class="btn btn-primary btn-sm" id="confirmApplySimilarBtn">Áp dụng</button>
        </div>
    </div>

    <!-- Bootstrap JS (bundle includes Popper) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" xintegrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eJqAQ1uGknoFuWdY" crossorigin="anonymous"></script>
    <!-- Flatpickr JS for Date Range Picker -->
    <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
    <!-- Define contextPath for external JS -->
    <script>const contextPath = "${pageContext.request.contextPath}";</script>
    <!-- Custom JavaScript -->
    <script src="${pageContext.request.contextPath}/js/manage-doctor-schedule.js"></script>
</body>
</html>
