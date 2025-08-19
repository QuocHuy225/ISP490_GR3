<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.Patient" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalRecord" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalExamTemplate" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hồ sơ bệnh án - Ánh Dương Clinic</title>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <style>
            body {
                font-family: 'Roboto', sans-serif;
                background-color: #f4f7fa;
            }
            .medical-form-container {
                border: 1px solid #dee2e6;
                border-radius: 8px;
                padding: 15px;
                background-color: #fff;
                margin-bottom: 20px;
            }
            .vital-signs-grid {
                display: flex;
                flex-wrap: wrap;
                gap: 1.5rem;
                width: 100%;
            }
            .vital-sign-item {
                flex: 1 1 calc(25% - 1.5rem);
                min-width: 200px;
                display: flex;
                flex-direction: column;
            }
            .vital-sign-item .form-label {
                font-weight: 500;
                color: #333;
                margin-bottom: 0.5rem;
            }
            .vital-sign-item .form-text {
                font-size: 0.85rem;
                color: #6c757d;
                margin-top: 0.25rem;
            }
            .action-btn {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 0.5rem 1.5rem;
                border: none;
                border-radius: 8px;
                text-decoration: none;
                font-size: 14px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.3s ease;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                min-width: 120px;
                justify-content: center;
            }
            .action-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0,0,0,0.15);
                text-decoration: none;
            }
            .action-btn i {
                font-size: 16px;
            }
            .btn-text {
                font-size: 15px;
                white-space: nowrap;
            }
            .action-btn-save, .action-btn-update {
                background: linear-gradient(135deg, #007bff, #0056b3);
                color: white;
            }
            .action-btn-save:hover, .action-btn-update:hover {
                background: linear-gradient(135deg, #0056b3, #004085);
                color: white;
            }
            .action-btn-complete {
                background: linear-gradient(135deg, #28a745, #218838);
                color: white;
            }
            .action-btn-complete:hover {
                background: linear-gradient(135deg, #218838, #1e7e34);
                color: white;
            }
            .action-btn-apply {
                background: linear-gradient(135deg, #28a745, #218838);
                color: white;
            }
            .action-btn-apply:hover {
                background: linear-gradient(135deg, #218838, #1e7e34);
                color: white;
            }
            .back-btn, .action-btn-clear {
                background: linear-gradient(135deg, #adb5bd, #868e96);
                color: white;
            }
            .back-btn:hover, .action-btn-clear:hover {
                background: linear-gradient(135deg, #868e96, #6c757d);
                color: white;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0,0,0,0.15);
            }
            .action-btn-view {
                background: linear-gradient(135deg, #17a2b8, #138496);
                color: white;
            }
            .action-btn-view:hover {
                background: linear-gradient(135deg, #138496, #117a8b);
                color: white;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0,0,0,0.15);
            }
            .action-btn-medical-request {
                background: linear-gradient(135deg, #17a2b8, #138496);
                color: white;
            }
            .action-btn-medical-request:hover {
                background: linear-gradient(135deg, #138496, #117a8b);
                color: white;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0,0,0,0.15);
            }
            @media (max-width: 768px) {
                .vital-sign-item {
                    flex: 1 1 calc(50% - 1.5rem);
                }
                .action-btn {
                    padding: 0.6rem 1.2rem;
                    min-width: 100px;
                }
                .btn-text {
                    display: none;
                }
                .action-btn i {
                    font-size: 18px;
                }
                .back-btn {
                    padding: 0.6rem 1.2rem;
                    min-width: 100px;
                    font-size: 13px;
                }
            }
            @media (max-width: 576px) {
                .vital-sign-item {
                    flex: 1 1 100%;
                }
            }
            .medical-card {
                border: none;
                border-radius: 8px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.08);
                margin-bottom: 1.5rem;
            }
            .medical-card .card-header {
                background: none;
                border-bottom: none;
                padding: 15px;
            }
            .medical-card .card-header h5 {
                font-weight: 600;
                font-size: 1.2rem;
                margin: 0;
                color: #007bff;
            }
            .patient-info-card {
                background: #fff;
            }
            .patient-info-card .card-body {
                padding: 15px;
            }
            .patient-info-card p {
                margin-bottom: 12px;
                padding: 6px 0;
                border-bottom: 1px solid #e9ecef;
                font-size: 1rem;
                color: #444;
            }
            .patient-info-card p:last-child {
                margin-bottom: 0;
                border-bottom: none;
            }
            .patient-info-card strong {
                min-width: 150px;
                display: inline-block;
                color: #333;
            }
            .form-control, .form-select, textarea {
                border-radius: 0.5rem;
                transition: border-color 0.3s ease, box-shadow 0.3s ease;
                width: 100%;
            }
            .form-control:focus, .form-select:focus, textarea:focus {
                border-color: #007bff;
                box-shadow: 0 0 8px rgba(0,123,255,0.2);
            }
            .form-control.is-invalid {
                border-color: #dc3545;
                background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 12 12' width='12' height='12' fill='none' stroke='%23dc3545'%3e%3ccircle cx='6' cy='6' r='4.5'/%3e%3cpath stroke-linejoin='round' d='M5.8 3.6h.4L6 6.5z'/%3e%3ccircle cx='6' cy='8.2' r='.6' fill='%23dc3545' stroke='none'/%3e%3c/svg%3e");
                background-repeat: no-repeat;
                background-position: right calc(0.375em + 0.1875rem) center;
                background-size: calc(0.75em + 0.375rem) calc(0.75em + 0.375rem);
            }
            .form-control.is-valid {
                border-color: #28a745;
                background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 8 8'%3e%3cpath fill='%2328a745' d='M2.3 6.73L.6 4.53c-.4-1.04.56-1.87 1.47-1.47L4 5.27 6.73 2.53c-.4-.4.92-.12 1.47.45.56.57.38 1.4-.28 1.74L4 6.73c-.2.2-.47.2-.67 0z'/%3e%3c/svg%3e");
                background-repeat: no-repeat;
                background-position: right calc(0.375em + 0.1875rem) center;
                background-size: calc(0.75em + 0.375rem) calc(0.75em + 0.375rem);
            }
            .form-control:disabled, textarea:disabled {
                background-color: #e9ecef;
                cursor: not-allowed;
            }
            .action-btn[title]:hover::after {
                content: attr(title);
                position: absolute;
                background: rgba(0,0,0,0.8);
                color: white;
                padding: 4px 8px;
                border-radius: 4px;
                font-size: 11px;
                z-index: 1000;
                margin-top: -35px;
                margin-left: -20px;
                white-space: nowrap;
            }
            @media (min-width: 768px) {
                .patient-info-card {
                    flex: 0 0 25%;
                    max-width: 25%;
                }
                .medical-content-area {
                    flex: 0 0 75%;
                    max-width: 75%;
                }
            }
        </style>
    </head>
    <body>
        <%
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
            Object userObj = session.getAttribute("user");
            String userName = "User";
            String userRoleDisplay = "Patient";
            if (userObj instanceof User) {
                User user = (User) userObj;
                userName = user.getFullName() != null ? user.getFullName() : user.getEmail();
                userRoleDisplay = user.getRole() != null ? user.getRole().getValue() : "Patient";
            }
            Patient patient = (Patient) request.getAttribute("patient");
            MedicalRecord medicalRecord = (MedicalRecord) request.getAttribute("medicalRecord");
            List<MedicalExamTemplate> templates = (List<MedicalExamTemplate>) request.getAttribute("templates");
            Boolean isEdit = (Boolean) request.getAttribute("isEdit");
            if (isEdit == null) {
                isEdit = false;
            }
            boolean isCompleted = medicalRecord != null && "completed".equals(medicalRecord.getStatus());
            SimpleDateFormat shortSdf = new SimpleDateFormat("dd/MM/yyyy");
        %>
        <nav id="sidebar">
            <div class="sidebar-header">
                <h3>MENU</h3>
            </div>
            <ul class="list-unstyled components">
                <% if (currentRole == User.Role.DOCTOR) { %>
                <li><a href="${pageContext.request.contextPath}/homepage"><i class="bi bi-speedometer2"></i> Trang chủ</a></li>
                <li><a href="#"><i class="bi bi-calendar-check"></i> Lịch khám bệnh</a></li>
                <li class="active"><a href="${pageContext.request.contextPath}/doctor/patients"><i class="bi bi-people"></i> Hồ sơ bệnh nhân</a></li>
                <li><a href="${pageContext.request.contextPath}/doctor/report"><i class="bi bi-bar-chart-fill"></i> Báo cáo thống kê</a></li>
                    <% } %>
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
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile"><i class="bi bi-person-fill"></i> Thông tin cá nhân</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile"><i class="bi bi-key-fill"></i> Đổi mật khẩu</a></li>
                            <li><a class="dropdown-item" href="#"><i class="bi bi-gear-fill"></i> Cài đặt</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth/logout"><i class="bi bi-box-arrow-right"></i> Đăng xuất</a></li>
                        </ul>
                    </div>
                </div>
            </nav>
            <div class="container-fluid mt-4">
                <nav aria-label="breadcrumb" class="mb-4">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/doctor/patients">
                                <i class="bi bi-people me-1"></i>Quản lý bệnh nhân
                            </a>
                        </li>
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%= patient != null ? patient.getId() : "" %>">
                                Hồ sơ bệnh án
                            </a>
                        </li>
                        <li class="breadcrumb-item active" aria-current="page">
                            <%= isEdit ? "Chỉnh sửa" : "Tạo mới" %>
                        </li>
                    </ol>
                </nav>
                <% if (patient == null) { %>
                <div class="alert alert-danger">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    Không tìm thấy thông tin bệnh nhân! Vui lòng quay lại danh sách bệnh nhân.
                </div>
                <% } else { %>
                
                <!-- Alert Messages -->
                <%
                String success = request.getParameter("success");
                String error = request.getParameter("error");
                String message = request.getParameter("message");
                %>

                <% if (success != null) { %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle me-2"></i>
                    <% if ("added".equals(success)) { %>
                    Hồ sơ bệnh án đã được tạo thành công!
                    <% } else if ("updated".equals(success)) { %>
                    Hồ sơ bệnh án đã được cập nhật thành công!
                    <% } else if ("status_completed".equals(success)) { %>
                    Hồ sơ bệnh án đã được hoàn thành!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <% if ("final_diagnosis_required".equals(error)) { %>
                    <strong>Thiếu chẩn đoán cuối cùng!</strong> Vui lòng nhập chẩn đoán cuối cùng trước khi hoàn thành hồ sơ bệnh án.
                    <% } else if ("vital_signs_respiration_invalid".equals(error)) { %>
                    <strong>Chỉ số sinh tồn không hợp lệ!</strong> Nhịp thở phải từ 8 đến 40 lần/phút.
                    <% } else if ("vital_signs_temperature_invalid".equals(error)) { %>
                    <strong>Chỉ số sinh tồn không hợp lệ!</strong> Nhiệt độ phải từ 32 đến 45°C.
                    <% } else if ("vital_signs_pulse_invalid".equals(error)) { %>
                    <strong>Chỉ số sinh tồn không hợp lệ!</strong> Mạch phải từ 30 đến 200 lần/phút.
                    <% } else if ("vital_signs_blood_pressure_invalid".equals(error)) { %>
                    <strong>Chỉ số sinh tồn không hợp lệ!</strong> Huyết áp phải có định dạng số/số (ví dụ: 120/80).
                    <% } else if ("vital_signs_blood_pressure_range_invalid".equals(error)) { %>
                    <strong>Chỉ số sinh tồn không hợp lệ!</strong> Huyết áp tâm thu phải từ 70-300 mmHg, tâm trương từ 40-200 mmHg.
                    <% } else if ("vital_signs_blood_pressure_logic_invalid".equals(error)) { %>
                    <strong>Chỉ số sinh tồn không hợp lệ!</strong> Huyết áp tâm thu phải cao hơn tâm trương.
                    <% } else if ("vital_signs_blood_pressure_format_invalid".equals(error)) { %>
                    <strong>Chỉ số sinh tồn không hợp lệ!</strong> Huyết áp không đúng định dạng số.
                    <% } else if ("vital_signs_height_invalid".equals(error)) { %>
                    <strong>Chỉ số sinh tồn không hợp lệ!</strong> Chiều cao phải từ 50 đến 250 cm.
                    <% } else if ("vital_signs_weight_invalid".equals(error)) { %>
                    <strong>Chỉ số sinh tồn không hợp lệ!</strong> Cân nặng phải từ 5 đến 300 kg.
                    <% } else if ("vital_signs_spo2_invalid".equals(error)) { %>
                    <strong>Chỉ số sinh tồn không hợp lệ!</strong> SpO2 phải từ 70 đến 100%.
                    <% } else if ("medical_record_completed".equals(error)) { %>
                    Không thể tạo hoặc chỉnh sửa phiếu chỉ định vì hồ sơ bệnh án đã được hoàn thành!
                    <% } else if ("add_failed".equals(error)) { %>
                    Tạo hồ sơ bệnh án thất bại!
                    <% } else if ("update_failed".equals(error)) { %>
                    Cập nhật hồ sơ bệnh án thất bại!
                    <% } else if ("system_error".equals(error)) { %>
                    Có lỗi hệ thống xảy ra!
                    <% } else { %>
                    Có lỗi xảy ra. Vui lòng thử lại!
                    <% } %>
                    <% if (message != null) { %>
                    <br><%= message %>
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>
                <div class="medical-form-container">
                    <form id="medicalRecordForm" method="POST" action="${pageContext.request.contextPath}/doctor/medical-records">
                        <input type="hidden" name="action" value="<%= isEdit ? "update" : "add" %>">
                        <input type="hidden" name="patientId" value="<%= patient.getId() %>">
                        <% if (isEdit && medicalRecord != null) { %>
                        <input type="hidden" name="recordId" value="<%= medicalRecord.getId() %>">
                        <% } %>
                        <input type="hidden" id="status" name="status" value="<%= medicalRecord != null ? medicalRecord.getStatus() : "ongoing" %>">
                        <div class="row mb-4">
                            <div class="col-md-3 patient-info-card">
                                <div class="card medical-card">
                                    <div class="card-header">
                                        <h4 class="text-primary mb-3">
                                            <i class="bi bi-file-medical me-2"></i>Thông tin bệnh nhân
                                        </h4>
                                    </div>
                                    <div class="card-body">
                                        <p><strong>Mã bệnh nhân:</strong> <%= patient.getPatientCode() %></p>
                                        <p><strong>Họ tên:</strong> <%= patient.getFullName() %></p>
                                        <p><strong>Ngày sinh:</strong> <%= patient.getDob() != null ? shortSdf.format(patient.getDob()) : "" %></p>
                                        <p><strong>Điện thoại:</strong> <%= patient.getPhone() != null ? patient.getPhone() : "" %></p>
                                        <p><strong>CCCD:</strong> <%= patient.getCccd() != null ? patient.getCccd() : "" %></p>
                                        <p><strong>Địa chỉ:</strong> <%= patient.getAddress() != null ? patient.getAddress() : "" %></p>
                                        <p><strong>Bác sĩ phụ trách:</strong> 
                                        <%
                                            Object doctorObj = request.getAttribute("doctor");
                                            System.out.println("DEBUG JSP: doctor attribute = " + doctorObj);
                                            if (doctorObj != null) {
                                                com.mycompany.isp490_gr3.model.Doctor doctor = (com.mycompany.isp490_gr3.model.Doctor) doctorObj;
                                                System.out.println("DEBUG JSP: doctor name = " + doctor.getFullName());
                                                out.print(doctor.getFullName());
                                            } else {
                                                out.print("Chưa xác định");
                                            }
                                        %>
                                        </p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-9 medical-content-area">
                                <div class="card medical-card vital-signs-card mb-4">
                                    <div class="card-header">
                                        <h4 class="text-primary mb-3">
                                            <i class="bi bi-file-medical me-2"></i>Chỉ số sinh tồn
                                        </h4>
                                    </div>
                                    <div class="card-body">
                                        <div class="vital-signs-grid">
                                            <div class="vital-sign-item">
                                                <label for="respirationRate" class="form-label">Nhịp thở (lần/phút) <span class="text-muted">(8-40)</span></label>
                                                <input type="number" class="form-control" id="respirationRate" name="respirationRate" min="8" max="40"
                                                       value="<%= medicalRecord != null && medicalRecord.getRespirationRate() != null ? medicalRecord.getRespirationRate() : "" %>"
                                                       <%= isCompleted ? "disabled" : "" %>>
                                                <div class="invalid-feedback" id="respirationRate-error"></div>
                                                <small class="form-text text-muted">Bình thường: 12-20 lần/phút</small>
                                            </div>
                                            <div class="vital-sign-item">
                                                <label for="temperature" class="form-label">Nhiệt độ (°C) <span class="text-muted">(32-45)</span></label>
                                                <input type="number" step="0.1" class="form-control" id="temperature" name="temperature" min="32" max="45"
                                                       value="<%= medicalRecord != null && medicalRecord.getTemperature() != null ? medicalRecord.getTemperature() : "" %>"
                                                       <%= isCompleted ? "disabled" : "" %>>
                                                <div class="invalid-feedback" id="temperature-error"></div>
                                                <small class="form-text text-muted">Bình thường: 36.0-37.5°C</small>
                                            </div>
                                            <div class="vital-sign-item">
                                                <label for="pulse" class="form-label">Mạch (lần/phút) <span class="text-muted">(30-200)</span></label>
                                                <input type="number" class="form-control" id="pulse" name="pulse" min="30" max="200"
                                                       value="<%= medicalRecord != null && medicalRecord.getPulse() != null ? medicalRecord.getPulse() : "" %>"
                                                       <%= isCompleted ? "disabled" : "" %>>
                                                <div class="invalid-feedback" id="pulse-error"></div>
                                                <small class="form-text text-muted">Bình thường: 60-100 lần/phút</small>
                                            </div>
                                            <div class="vital-sign-item">
                                                <label for="bloodPressure" class="form-label">Huyết áp (mmHg)</label>
                                                <input type="text" class="form-control" id="bloodPressure" name="bloodPressure" placeholder="120/80" pattern="^\d{2,3}\/\d{2,3}$"
                                                       value="<%= medicalRecord != null && medicalRecord.getBloodPressure() != null ? medicalRecord.getBloodPressure() : "" %>"
                                                       <%= isCompleted ? "disabled" : "" %>>
                                                <div class="invalid-feedback" id="bloodPressure-error"></div>
                                                <small class="form-text text-muted">Định dạng: Tâm thu/Tâm trương (VD: 120/80)</small>
                                            </div>
                                            <div class="vital-sign-item">
                                                <label for="height" class="form-label">Chiều cao (cm) <span class="text-muted">(50-250)</span></label>
                                                <input type="number" step="0.1" class="form-control" id="height" name="height" min="50" max="250"
                                                       value="<%= medicalRecord != null && medicalRecord.getHeight() != null ? medicalRecord.getHeight() : "" %>"
                                                       <%= isCompleted ? "disabled" : "" %>>
                                                <div class="invalid-feedback" id="height-error"></div>
                                                <small class="form-text text-muted">Người lớn: 140-220cm</small>
                                            </div>
                                            <div class="vital-sign-item">
                                                <label for="weight" class="form-label">Cân nặng (kg) <span class="text-muted">(5-300)</span></label>
                                                <input type="number" step="0.1" class="form-control" id="weight" name="weight" min="5" max="300"
                                                       value="<%= medicalRecord != null && medicalRecord.getWeight() != null ? medicalRecord.getWeight() : "" %>"
                                                       <%= isCompleted ? "disabled" : "" %>>
                                                <div class="invalid-feedback" id="weight-error"></div>
                                                <small class="form-text text-muted">Người lớn: 40-150kg</small>
                                            </div>
                                            <div class="vital-sign-item">
                                                <label for="bmi" class="form-label">BMI <span class="text-muted">(tự động tính)</span></label>
                                                <input type="number" step="0.1" class="form-control" id="bmi" name="bmi" readonly
                                                       value="<%= medicalRecord != null && medicalRecord.getBmi() != null ? medicalRecord.getBmi() : "" %>">
                                                <small class="form-text" id="bmi-status"></small>
                                            </div>
                                            <div class="vital-sign-item">
                                                <label for="spo2" class="form-label">SpO2 (%) <span class="text-muted">(70-100)</span></label>
                                                <input type="number" step="0.1" class="form-control" id="spo2" name="spo2" min="70" max="100"
                                                       value="<%= medicalRecord != null && medicalRecord.getSpo2() != null ? medicalRecord.getSpo2() : "" %>"
                                                       <%= isCompleted ? "disabled" : "" %>>
                                                <div class="invalid-feedback" id="spo2-error"></div>
                                                <small class="form-text text-muted">Bình thường: 95-100%</small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="card medical-card">
                                    <div class="card-header">
                                        <h4 class="text-primary mb-3">
                                            <i class="bi bi-file-medical me-2"></i>Thông tin y tế
                                        </h4>
                                    </div>
                                    <div class="card-body">
                                        <div class="row">
                                            <div class="col-md-12">
                                                <% if (!isEdit) { %>
                                                <div class="d-flex align-items-center gap-2 mb-3">
                                                    <select class="form-select flex-grow-1" id="templateSelect" <%= isCompleted ? "disabled" : "" %>>
                                                        <option value="">Chọn mẫu đơn...</option>
                                                        <% if (templates != null) { %>
                                                        <% for (MedicalExamTemplate template : templates) { %>
                                                        <option value="<%= template.getId() %>"
                                                                data-physical="<%= template.getPhysicalExam() != null ? template.getPhysicalExam().replace("\"", "'") : "" %>"
                                                                data-clinical="<%= template.getClinicalInfo() != null ? template.getClinicalInfo().replace("\"", "'") : "" %>"
                                                                data-diagnosis="<%= template.getFinalDiagnosis() != null ? template.getFinalDiagnosis().replace("\"", "'") : "" %>">
                                                            <%= template.getName() != null ? template.getName() : "N/A" %>
                                                        </option>
                                                        <% } %>
                                                        <% } %>
                                                    </select>
                                                    <button type="button" class="action-btn action-btn-apply" onclick="applyTemplate()" <%= isCompleted ? "disabled" : "" %> title="Áp dụng mẫu">
                                                        <i class="bi bi-clipboard-plus"></i>
                                                        <span class="btn-text">Áp dụng mẫu</span>
                                                    </button>
                                                    <button type="button" class="action-btn action-btn-clear" onclick="clearForm()" <%= isCompleted ? "disabled" : "" %> title="Xóa form">
                                                        <i class="bi bi-eraser"></i>
                                                        <span class="btn-text">Xóa form</span>
                                                    </button>
                                                </div>
                                                <% } %>
                                                <div class="row mb-3">
                                                    <div class="col-md-6">
                                                        <label for="medicalHistory" class="form-label">Tiền sử bệnh án</label>
                                                        <textarea class="form-control" id="medicalHistory" name="medicalHistory" rows="3" <%= isCompleted ? "disabled" : "" %>><%= medicalRecord != null && medicalRecord.getMedicalHistory() != null ? medicalRecord.getMedicalHistory() : "" %></textarea>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label for="currentDisease" class="form-label">Bệnh hiện tại</label>
                                                        <textarea class="form-control" id="currentDisease" name="currentDisease" rows="3" <%= isCompleted ? "disabled" : "" %>><%= medicalRecord != null && medicalRecord.getCurrentDisease() != null ? medicalRecord.getCurrentDisease() : "" %></textarea>
                                                    </div>
                                                </div>
                                                <div class="row mb-3">
                                                    <div class="col-md-6">
                                                        <label for="physicalExam" class="form-label">Khám thể lý</label>
                                                        <textarea class="form-control" id="physicalExam" name="physicalExam" rows="3" <%= isCompleted ? "disabled" : "" %>><%= medicalRecord != null && medicalRecord.getPhysicalExam() != null ? medicalRecord.getPhysicalExam() : "" %></textarea>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label for="clinicalInfo" class="form-label">Thông tin lâm sàng</label>
                                                        <textarea class="form-control" id="clinicalInfo" name="clinicalInfo" rows="3" <%= isCompleted ? "disabled" : "" %>><%= medicalRecord != null && medicalRecord.getClinicalInfo() != null ? medicalRecord.getClinicalInfo() : "" %></textarea>
                                                    </div>
                                                </div>
                                                <div class="row mb-3">
                                                    <div class="col-md-6">
                                                        <label for="finalDiagnosis" class="form-label">Chẩn đoán cuối cùng</label>
                                                        <textarea class="form-control" id="finalDiagnosis" name="finalDiagnosis" rows="3" <%= isCompleted ? "disabled" : "" %>><%= medicalRecord != null && medicalRecord.getFinalDiagnosis() != null ? medicalRecord.getFinalDiagnosis() : "" %></textarea>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label for="treatmentPlan" class="form-label">Kế hoạch điều trị</label>
                                                        <textarea class="form-control" id="treatmentPlan" name="treatmentPlan" rows="3" <%= isCompleted ? "disabled" : "" %>><%= medicalRecord != null && medicalRecord.getTreatmentPlan() != null ? medicalRecord.getTreatmentPlan() : "" %></textarea>
                                                    </div>
                                                </div>
                                                <div class="mb-3">
                                                    <label for="note" class="form-label">Ghi chú</label>
                                                    <textarea class="form-control" id="note" name="note" rows="3" placeholder="Ghi chú bổ sung..."><%= medicalRecord != null && medicalRecord.getNote() != null ? medicalRecord.getNote() : "" %></textarea>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%= patient.getId() %>" class="action-btn back-btn" title="Quay lại">
                                    <i class="bi bi-arrow-left"></i>
                                    <span class="btn-text">Quay lại</span>
                                </a>
                                <% if (isEdit && medicalRecord != null) { %>
                                <a href="${pageContext.request.contextPath}/doctor/medical-records?action=view&recordId=<%= medicalRecord.getId() %>" class="action-btn action-btn-view ms-2" title="Xem chi tiết">
                                    <i class="bi bi-eye"></i>
                                    <span class="btn-text">Xem chi tiết</span>
                                </a>
                                <% } %>
                            </div>
                            <div>
                                <% if (isCompleted) { %>
                                <button type="button" class="action-btn action-btn-update" onclick="saveRecord('completed')" title="Cập nhật">
                                    <i class="bi bi-check-circle"></i>
                                    <span class="btn-text">Cập nhật</span>
                                </button>
                                <% } else { %>
                                <button type="button" class="action-btn action-btn-save me-2" onclick="saveRecord('ongoing')" title="Lưu nháp">
                                    <i class="bi bi-save"></i>
                                    <span class="btn-text">Lưu nháp</span>
                                </button>
                                <button type="button" class="action-btn action-btn-complete" onclick="saveRecord('completed')" title="Hoàn thành">
                                    <i class="bi bi-check-all"></i>
                                    <span class="btn-text">Hoàn thành</span>
                                </button>
                                <% } %>
                                <% if (isEdit && medicalRecord != null) { %>
                                <a href="${pageContext.request.contextPath}/doctor/medical-requests?medicalRecordId=<%= medicalRecord.getId() %>" 
                                   class="action-btn action-btn-medical-request ms-2" title="Quản lý phiếu chỉ định">
                                    <i class="bi bi-clipboard-data"></i>
                                    <span class="btn-text">Phiếu chỉ định</span>
                                </a>
                                <% } %>
                            </div>
                        </div>
                    </form>
                </div>
                <% } %>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
        <script>
                                    $(document).ready(function () {
                                        // Sidebar toggle
                                        $('#sidebarCollapse').on('click', function () {
                                            $('#sidebar').toggleClass('active');
                                            $('#content').toggleClass('active');
                                        });

                                        // Calculate BMI on input change
                                        function calculateBMI() {
                                            let height = parseFloat($('#height').val());
                                            let weight = parseFloat($('#weight').val());
                                            let bmiField = $('#bmi');
                                            let bmiStatus = $('#bmi-status');

                                            if (height > 0 && weight > 0) {
                                                let bmi = weight / ((height / 100) * (height / 100));
                                                bmiField.val(bmi.toFixed(2));
                                                validateBMI(); // Add validation for BMI
                                            } else {
                                                bmiField.val('');
                                                bmiField.removeClass('is-invalid is-valid');
                                                bmiStatus.text('');
                                            }
                                        }

                                        // Enhanced BMI validation
                                        function validateBMI() {
                                            const bmiField = $('#bmi');
                                            const bmiStatus = $('#bmi-status');
                                            const bmiValue = parseFloat(bmiField.val());

                                            if (bmiValue > 0) {
                                                bmiField.removeClass('is-invalid').addClass('is-valid');
                                                if (bmiValue < 18.5) {
                                                    bmiStatus.text('Gầy').css('color', '#ffc107');
                                                } else if (bmiValue >= 18.5 && bmiValue <= 24.9) {
                                                    bmiStatus.text('Bình thường').css('color', '#28a745');
                                                } else if (bmiValue >= 25 && bmiValue <= 29.9) {
                                                    bmiStatus.text('Thừa cân').css('color', '#fd7e14');
                                                } else {
                                                    bmiStatus.text('Béo phì').css('color', '#dc3545');
                                                }
                                                return true;
                                            } else {
                                                bmiField.removeClass('is-invalid is-valid');
                                                bmiStatus.text('');
                                                return true;
                                            }
                                        }

                                        // Form validation for vital signs
                                        function validateField(fieldId, min, max, errorMessage) {
                                            const inputField = $('#' + fieldId);
                                            const errorDiv = $('#' + fieldId + '-error');
                                            const value = parseFloat(inputField.val());

                                            if (inputField.is(':disabled') || !inputField.val()) {
                                                inputField.removeClass('is-invalid is-valid');
                                                errorDiv.text('');
                                                return true; // Allow empty or disabled fields
                                            }

                                            if (isNaN(value) || value < min || value > max) {
                                                inputField.addClass('is-invalid').removeClass('is-valid');
                                                errorDiv.text(errorMessage);
                                                return false;
                                            } else {
                                                inputField.removeClass('is-invalid').addClass('is-valid');
                                                errorDiv.text('');
                                                return true;
                                            }
                                        }

                                        function validateBloodPressure() {
                                            const inputField = $('#bloodPressure');
                                            const errorDiv = $('#bloodPressure-error');
                                            const value = inputField.val();
                                            const pattern = /^\d{2,3}\/\d{2,3}$/;

                                            if (inputField.is(':disabled') || !value) {
                                                inputField.removeClass('is-invalid is-valid');
                                                errorDiv.text('');
                                                return true; // Allow empty or disabled field
                                            }

                                            if (!pattern.test(value)) {
                                                inputField.addClass('is-invalid').removeClass('is-valid');
                                                errorDiv.text('Định dạng huyết áp không đúng (VD: 120/80)');
                                                return false;
                                            } else {
                                                const parts = value.split('/');
                                                const systolic = parseInt(parts[0]);
                                                const diastolic = parseInt(parts[1]);
                                                const minSystolic = 70;
                                                const maxSystolic = 190;
                                                const minDiastolic = 40;
                                                const maxDiastolic = 120;
                                                if (systolic < minSystolic || systolic > maxSystolic || diastolic < minDiastolic || diastolic > maxDiastolic) {
                                                    inputField.addClass('is-invalid').removeClass('is-valid');
                                                    errorDiv.text(`Huyết áp không nằm trong khoảng hợp lý (${minSystolic}-${maxSystolic}/${minDiastolic}-${maxDiastolic}).`);
                                                    return false;
                                                } else {
                                                    inputField.removeClass('is-invalid').addClass('is-valid');
                                                    errorDiv.text('');
                                                    return true;
                                                }
                                            }
                                        }

                                        // Add input event listeners for all vital signs fields
                                        $('#respirationRate').on('input', function () {
                                            validateField('respirationRate', 8, 40, 'Nhịp thở phải từ 8 đến 40 lần/phút.');
                                        });
                                        $('#temperature').on('input', function () {
                                            validateField('temperature', 32, 45, 'Nhiệt độ phải từ 32 đến 45°C.');
                                        });
                                        $('#pulse').on('input', function () {
                                            validateField('pulse', 30, 200, 'Mạch phải từ 30 đến 200 lần/phút.');
                                        });
                                        $('#bloodPressure').on('input', validateBloodPressure);
                                        $('#height').on('input', function () {
                                            validateField('height', 50, 250, 'Chiều cao phải từ 50 đến 250 cm.');
                                        });
                                        $('#weight').on('input', function () {
                                            validateField('weight', 5, 300, 'Cân nặng phải từ 5 đến 300 kg.');
                                        });
                                        $('#spo2').on('input', function () {
                                            validateField('spo2', 70, 100, 'SpO2 phải từ 70 đến 100%.');
                                        });

                                        // Setup BMI calculation
                                        $('#height, #weight').on('input', calculateBMI);
                                        calculateBMI(); // Initial calculation

                                        // Validate existing data on page load
                                        // Validate all fields that have values on page load
                                        if ($('#respirationRate').val()) {
                                            validateField('respirationRate', 8, 40, 'Nhịp thở phải từ 8 đến 40 lần/phút.');
                                        }
                                        if ($('#temperature').val()) {
                                            validateField('temperature', 32, 45, 'Nhiệt độ phải từ 32 đến 45°C.');
                                        }
                                        if ($('#pulse').val()) {
                                            validateField('pulse', 30, 200, 'Mạch phải từ 30 đến 200 lần/phút.');
                                        }
                                        if ($('#bloodPressure').val()) {
                                            validateBloodPressure();
                                        }
                                        if ($('#height').val()) {
                                            validateField('height', 50, 250, 'Chiều cao phải từ 50 đến 250 cm.');
                                        }
                                        if ($('#weight').val()) {
                                            validateField('weight', 5, 300, 'Cân nặng phải từ 5 đến 300 kg.');
                                        }
                                        if ($('#spo2').val()) {
                                            validateField('spo2', 70, 100, 'SpO2 phải từ 70 đến 100%.');
                                        }
                                        if ($('#bmi').val()) {
                                            validateBMI();
                                        }

                                        window.applyTemplate = function () {
                                            const templateSelect = document.getElementById('templateSelect');
                                            const selectedOption = templateSelect.options[templateSelect.selectedIndex];

                                            if (selectedOption.value) {
                                                document.getElementById('physicalExam').value = selectedOption.dataset.physical || '';
                                                document.getElementById('clinicalInfo').value = selectedOption.dataset.clinical || '';
                                                document.getElementById('finalDiagnosis').value = selectedOption.dataset.diagnosis || '';
                                            }
                                        };

                                        window.clearForm = function () {
                                            const fields = ['medicalHistory', 'currentDisease', 'physicalExam', 'clinicalInfo', 'finalDiagnosis', 'treatmentPlan', 'note'];
                                            fields.forEach(id => {
                                                const input = document.getElementById(id);
                                                if (input && !input.hasAttribute('disabled')) {
                                                    input.value = '';
                                                    input.classList.remove('is-invalid', 'is-valid');
                                                }
                                            });
                                            document.getElementById('templateSelect').value = '';
                                        };

                                        // Validation function removed - all validation now handled by backend

                                        window.saveRecord = function (status) {
                                            const form = document.getElementById('medicalRecordForm');
                                            document.getElementById('status').value = status;
                                            
                                            // Tất cả validation sẽ được xử lý ở backend
                                            // Submit form trực tiếp cho cả lưu nháp và hoàn thành
                                            form.submit();
                                        };
                                    });
        </script>
    </body>
</html>