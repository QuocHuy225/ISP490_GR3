<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.Patient" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalRecord" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalRequest" %>
<%@ page import="com.mycompany.isp490_gr3.model.Partner" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><%= "update".equals(request.getAttribute("action")) ? "Chỉnh sửa" : "Tạo" %> phiếu chỉ định - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Homepage specific CSS -->
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
            .medical-card .card-header h4 {
                font-weight: 600;
                font-size: 1.5rem;
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
            .back-btn {
                background: linear-gradient(135deg, #adb5bd, #868e96);
                color: white;
            }
            .back-btn:hover {
                background: linear-gradient(135deg, #868e96, #6c757d);
                color: white;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0,0,0,0.15);
            }
            .form-label {
                font-weight: 500;
                color: #333;
                margin-bottom: 0.5rem;
            }
            .form-text {
                font-size: 0.85rem;
                color: #6c757d;
                margin-top: 0.25rem;
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
            @media (max-width: 768px) {
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
            }
            .alert {
                border-radius: 0.75rem;
                border: none;
                padding: 1rem 1.5rem;
                box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            }
        </style>
    </head>
    <body>
        <%
        // Get user role for access control
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
    
        // Get user information
        Object userObj = session.getAttribute("user");
        String userName = "User";
        String userRoleDisplay = "Patient";
        if (userObj instanceof User) {
            User user = (User) userObj;
            userName = user.getFullName() != null ? user.getFullName() : user.getEmail();
            userRoleDisplay = user.getRole() != null ? user.getRole().getValue() : "Patient";
        }
    
        // Get data from request
        String action = (String) request.getAttribute("action");
        boolean isEdit = "update".equals(action);
    
        Patient patient = (Patient) request.getAttribute("patient");
        MedicalRecord medicalRecord = (MedicalRecord) request.getAttribute("medicalRecord");
        MedicalRequest medicalRequest = (MedicalRequest) request.getAttribute("medicalRequest");
        List<Partner> partners = (List<Partner>) request.getAttribute("partners");
    
        // Check if medical record is completed
        boolean isCompleted = medicalRecord != null && "completed".equals(medicalRecord.getStatus());
    
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat shortSdf = new SimpleDateFormat("dd/MM/yyyy");
        %>

        <!-- Sidebar -->
        <nav id="sidebar">
            <div class="sidebar-header">
                <h3>MENU</h3>
            </div>
            <ul class="list-unstyled components">
                <% if (currentRole == User.Role.DOCTOR) { %>
                <li>
                    <a href="${pageContext.request.contextPath}/homepage">
                        <i class="bi bi-speedometer2"></i> Trang chủ
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-calendar-check"></i> Lịch khám bệnh
                    </a>
                </li>
                <li class="active">
                    <a href="${pageContext.request.contextPath}/doctor/patients">
                        <i class="bi bi-people"></i> Hồ sơ bệnh nhân
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/doctor/report">
                        <i class="bi bi-bar-chart-fill"></i> Báo cáo thống kê
                    </a>
                </li>
                <% } %>
            </ul>
        </nav>

        <!-- Main Content -->
        <div id="content">
            <!-- Top Navbar -->
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
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">
                                    <i class="bi bi-person-fill"></i> Thông tin cá nhân
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">
                                    <i class="bi bi-key-fill"></i> Đổi mật khẩu
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="#">
                                    <i class="bi bi-gear-fill"></i> Cài đặt
                                </a>
                            </li>
                            <li><hr class="dropdown-divider"></li>
                            <li>
                                <a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth/logout">
                                    <i class="bi bi-box-arrow-right"></i> Đăng xuất
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>

            <!-- Main Content Area -->
            <div class="container-fluid mt-4">
                <!-- Breadcrumb -->
                <nav aria-label="breadcrumb" class="mb-4">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/doctor/patients">
                                <i class="bi bi-people me-1"></i>Quản lý bệnh nhân
                            </a>
                        </li>
                        <% if (patient != null) { %>
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%= patient.getId() %>">
                                Hồ sơ bệnh án
                            </a>
                        </li>
                        <% } %>
                        <li class="breadcrumb-item active" aria-current="page">
                            <%= isEdit ? "Chỉnh sửa phiếu chỉ định" : "Tạo phiếu chỉ định mới" %>
                        </li>
                    </ol>
                </nav>

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
                    Tạo phiếu chỉ định thành công!
                    <% } else if ("updated".equals(success)) { %>
                    Cập nhật phiếu chỉ định thành công!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>
                <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <% if ("missing_data".equals(error)) { %>
                    Dữ liệu không đầy đủ. Vui lòng kiểm tra lại!
                    <% } else if ("add_failed".equals(error)) { %>
                    Tạo phiếu chỉ định thất bại!
                    <% } else if ("update_failed".equals(error)) { %>
                    Cập nhật phiếu chỉ định thất bại!
                    <% } else if ("request_exists".equals(error)) { %>
                    Phiếu chỉ định đã tồn tại cho hồ sơ này!
                    <% } else if ("medical_record_completed".equals(error)) { %>
                    Không thể chỉnh sửa phiếu chỉ định vì hồ sơ bệnh án đã được hoàn thành!
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
                <% if (isCompleted) { %>
                <div class="alert alert-warning alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <strong>Hồ sơ bệnh án đã hoàn thành!</strong> Không thể chỉnh sửa phiếu chỉ định khi hồ sơ bệnh án đã được hoàn thành.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <!-- Main Form Area -->
                <div class="medical-form-container">
                    <form id="medicalRequestForm" method="POST" action="${pageContext.request.contextPath}/doctor/medical-requests">
                        <input type="hidden" name="action" value="<%= isEdit ? "update" : "add" %>">
                        <input type="hidden" name="medicalRecordId" value="<%= medicalRecord != null ? medicalRecord.getId() : "" %>">
                        <input type="hidden" name="patientId" value="<%= patient != null ? patient.getId() : "" %>">
                        <% if (isEdit && medicalRequest != null) { %>
                        <input type="hidden" name="requestId" value="<%= medicalRequest.getId() %>">
                        <% } %>
                        <div class="row mb-4">
                            <!-- Patient Info (1/4 screen) -->
                            <% if (patient != null && medicalRecord != null) { %>
                            <div class="col-md-3 patient-info-card">
                                <div class="card medical-card">
                                    <div class="card-header">
                                        <h4 class="text-primary mb-3">
                                            <i class="bi bi-file-medical me-2"></i>
                                            Thông tin bệnh nhân
                                        </h4>
                                    </div>
                                    <div class="card-body">
                                        <p><strong>Mã bệnh nhân:</strong> <%= patient.getPatientCode() %></p>
                                        <p><strong>Họ tên:</strong> <%= patient.getFullName() %></p>
                                        <p><strong>Ngày sinh:</strong> <%= patient.getDob() != null ? shortSdf.format(patient.getDob()) : "" %></p>
                                        <p><strong>Mã hồ sơ:</strong> <%= medicalRecord.getId() %></p>
                                        <p><strong>Ngày tạo hồ sơ:</strong> <%= medicalRecord.getCreatedAt() != null ? shortSdf.format(medicalRecord.getCreatedAt()) : "" %></p>
                                        <% if (isEdit && medicalRequest != null) { %>
                                        <p><strong>Mã phiếu chỉ định:</strong> <%= medicalRequest.getId() %></p>
                                        <% } %>
                                    </div>
                                </div>
                            </div>
                            <% } %>

                            <!-- Medical Request Form (3/4 screen) -->
                            <div class="col-md-9 medical-content-area">
                                <div class="card medical-card">
                                    <div class="card-header">
                                        <h4 class="text-primary mb-3">
                                            <i class="bi bi-clipboard-data me-2"></i>
                                            <%= isEdit ? "Chỉnh sửa phiếu chỉ định" : "Tạo phiếu chỉ định mới" %>
                                            <% if (isCompleted) { %>
                                            <span class="badge bg-warning text-dark ms-2">Hồ sơ đã hoàn thành</span>
                                            <% } %>
                                        </h4>
                                    </div>
                                    <div class="card-body">
                                        <div class="row mb-3">
                                            <div class="col-md-12">
                                                <label for="clinicSampleSelect" class="form-label">Chọn mẫu cơ sở chỉ định</label>
                                                <select class="form-select" id="clinicSampleSelect" <%= isCompleted ? "disabled" : "" %>>
                                                    <option value="">-- Chọn mẫu --</option>
                                                    <% if (partners != null) { %>
                                                        <% for (Partner partner : partners) { %>
                                                            <option value='{"name":"<%= partner.getName() != null ? partner.getName().replace("\"", "\\\"") : "" %>","phone":"<%= partner.getPhone() != null ? partner.getPhone().replace("\"", "\\\"") : "" %>","address":"<%= partner.getAddress() != null ? partner.getAddress().replace("\"", "\\\"") : "" %>"}'>
                                                                <%= partner.getName() != null ? partner.getName() : "N/A" %> - <%= partner.getPhone() != null ? partner.getPhone() : "N/A" %> - <%= partner.getAddress() != null ? partner.getAddress() : "N/A" %>
                                                            </option>
                                                        <% } %>
                                                    <% } %>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="row mb-3">
                                            <div class="col-md-6">
                                                <label for="clinicName" class="form-label">Tên cơ sở chỉ định <span class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="clinicName" name="clinicName"
                                                       value="<%= isEdit && medicalRequest != null ? (medicalRequest.getClinicName() != null ? medicalRequest.getClinicName() : "") : "" %>"
                                                       <%= isCompleted ? "disabled" : "required" %>>
                                                <div class="invalid-feedback" id="clinicName-error"></div>
                                            </div>
                                            <div class="col-md-6">
                                                <label for="clinicPhone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                                                <input type="tel" class="form-control" id="clinicPhone" name="clinicPhone"
                                                       value="<%= isEdit && medicalRequest != null ? (medicalRequest.getClinicPhone() != null ? medicalRequest.getClinicPhone() : "") : "" %>"
                                                       <%= isCompleted ? "disabled" : "required" %>>
                                                <div class="invalid-feedback" id="clinicPhone-error"></div>
                                            </div>
                                        </div>
                                        <div class="row mb-3">
                                            <div class="col-md-12">
                                                <label for="clinicAddress" class="form-label">Địa chỉ cơ sở <span class="text-danger">*</span></label>
                                                <textarea class="form-control" id="clinicAddress" name="clinicAddress" rows="3"
                                                          <%= isCompleted ? "disabled" : "required" %>><%= isEdit && medicalRequest != null ? (medicalRequest.getClinicAddress() != null ? medicalRequest.getClinicAddress() : "") : "" %></textarea>
                                                <div class="invalid-feedback" id="clinicAddress-error"></div>
                                            </div>
                                        </div>
                                        <div class="row mb-3">
                                            <div class="col-md-12">
                                                <label for="instructionContent" class="form-label">Nội dung chỉ định <span class="text-danger">*</span></label>
                                                <textarea class="form-control" id="instructionContent" name="instructionContent" rows="3"
                                                          <%= isCompleted ? "disabled" : "required" %>><%= isEdit && medicalRequest != null ? (medicalRequest.getInstructionContent() != null ? medicalRequest.getInstructionContent() : "") : "" %></textarea>
                                                <div class="invalid-feedback" id="instructionContent-error"></div>
                                            </div>
                                        </div>
                                        <div class="row mb-3">
                                            <div class="col-md-12">
                                                <label for="instructionRequirements" class="form-label">Yêu cầu chỉ định</label>
                                                <textarea class="form-control" id="instructionRequirements" name="instructionRequirements" rows="3"
                                                          <%= isCompleted ? "disabled" : "" %>><%= isEdit && medicalRequest != null ? (medicalRequest.getInstructionRequirements() != null ? medicalRequest.getInstructionRequirements() : "") : "" %></textarea>
                                            </div>
                                        </div>
                                        <div class="row mb-3">
                                            <div class="col-md-12">
                                                <label for="notes" class="form-label">Ghi chú</label>
                                                <textarea class="form-control" id="notes" name="notes" rows="3"
                                                          placeholder="Ghi chú bổ sung..." <%= isCompleted ? "disabled" : "" %>><%= isEdit && medicalRequest != null ? (medicalRequest.getNotes() != null ? medicalRequest.getNotes() : "") : "" %></textarea>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <% if (isEdit && medicalRequest != null) { %>
                                <a href="${pageContext.request.contextPath}/doctor/medical-requests?action=view&requestId=<%= medicalRequest.getId() %>"
                                   class="action-btn back-btn" title="Quay lại">
                                    <i class="bi bi-arrow-left"></i>
                                    <span class="btn-text">Quay lại</span>
                                </a>
                                <% } else { %>
                                <a href="${pageContext.request.contextPath}/doctor/medical-records?action=edit&recordId=<%= medicalRecord != null ? medicalRecord.getId() : "" %>"
                                   class="action-btn back-btn" title="Quay lại">
                                    <i class="bi bi-arrow-left"></i>
                                    <span class="btn-text">Quay lại</span>
                                </a>
                                <% } %>
                            </div>
                            <div>
                                <% if (!isCompleted) { %>
                                <button type="submit" class="action-btn action-btn-save" title="<%= isEdit ? "Cập nhật" : "Lưu" %>">
                                    <i class="bi bi-check-circle"></i>
                                    <span class="btn-text"><%= isEdit ? "Cập nhật" : "Lưu" %></span>
                                </button>
                                <% } %>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Bootstrap Bundle with Popper -->
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
            <!-- jQuery -->
            <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
            <script>
                $(document).ready(function () {
                    // Sidebar toggle
                    $('#sidebarCollapse').on('click', function () {
                        $('#sidebar').toggleClass('active');
                        $('#content').toggleClass('active');
                    });

                    // Medical record completion status
                    const medicalRecordCompleted = '<%= isCompleted %>' === 'true';

                    // Form validation
                    document.getElementById('medicalRequestForm').addEventListener('submit', function (e) {
                        if (medicalRecordCompleted) {
                            e.preventDefault();
                            alert('Không thể lưu thay đổi vì hồ sơ bệnh án đã được hoàn thành!');
                            return false;
                        }

                        let isValid = true;

                        // Validate Clinic Name
                        const clinicName = $('#clinicName');
                        const clinicNameError = $('#clinicName-error');
                        if (!clinicName.val().trim()) {
                            clinicName.addClass('is-invalid').removeClass('is-valid');
                            clinicNameError.text('Vui lòng nhập tên cơ sở chỉ định!');
                            isValid = false;
                        } else {
                            clinicName.removeClass('is-invalid').addClass('is-valid');
                            clinicNameError.text('');
                        }

                        // Validate Clinic Phone
                        const clinicPhone = $('#clinicPhone');
                        const clinicPhoneError = $('#clinicPhone-error');
                        const phonePattern = /^\d{10,11}$/;

                        if (!clinicPhone.val().trim()) {
                            clinicPhone.addClass('is-invalid').removeClass('is-valid');
                            clinicPhoneError.text('Vui lòng nhập số điện thoại cơ sở chỉ định!');
                            isValid = false;
                        } else if (!phonePattern.test(clinicPhone.val().trim())) {
                            clinicPhone.addClass('is-invalid').removeClass('is-valid');
                            clinicPhoneError.text('Số điện thoại phải gồm 10 đến 11 chữ số!');
                            isValid = false;
                        } else {
                            clinicPhone.removeClass('is-invalid').addClass('is-valid');
                            clinicPhoneError.text('');
                        }


                        // Validate Clinic Address
                        const clinicAddress = $('#clinicAddress');
                        const clinicAddressError = $('#clinicAddress-error');
                        if (!clinicAddress.val().trim()) {
                            clinicAddress.addClass('is-invalid').removeClass('is-valid');
                            clinicAddressError.text('Vui lòng nhập địa chỉ cơ sở chỉ định!');
                            isValid = false;
                        } else {
                            clinicAddress.removeClass('is-invalid').addClass('is-valid');
                            clinicAddressError.text('');
                        }

                        // Validate Instruction Content
                        const instructionContent = $('#instructionContent');
                        const instructionContentError = $('#instructionContent-error');
                        if (!instructionContent.val().trim()) {
                            instructionContent.addClass('is-invalid').removeClass('is-valid');
                            instructionContentError.text('Vui lòng nhập nội dung chỉ định!');
                            isValid = false;
                        } else {
                            instructionContent.removeClass('is-invalid').addClass('is-valid');
                            instructionContentError.text('');
                        }

                        if (!isValid) {
                            e.preventDefault();
                        }
                    });

                    // Real-time validation
                    $('#clinicName').on('input', function () {
                        if ($(this).val().trim()) {
                            $(this).removeClass('is-invalid').addClass('is-valid');
                            $('#clinicName-error').text('');
                        } else {
                            $(this).addClass('is-invalid').removeClass('is-valid');
                            $('#clinicName-error').text('Vui lòng nhập tên cơ sở chỉ định!');
                        }
                    });

                    $('#clinicPhone').on('input', function () {
                        const value = $(this).val().trim();
                        const phonePattern = /^\d{10,11}$/;

                        if (!value) {
                            $(this).addClass('is-invalid').removeClass('is-valid');
                            $('#clinicPhone-error').text('Vui lòng nhập số điện thoại cơ sở chỉ định!');
                        } else if (!phonePattern.test(value)) {
                            $(this).addClass('is-invalid').removeClass('is-valid');
                            $('#clinicPhone-error').text('Số điện thoại phải gồm 10 đến 11 chữ số!');
                        } else {
                            $(this).removeClass('is-invalid').addClass('is-valid');
                            $('#clinicPhone-error').text('');
                        }
                    });


                    $('#clinicAddress').on('input', function () {
                        if ($(this).val().trim()) {
                            $(this).removeClass('is-invalid').addClass('is-valid');
                            $('#clinicAddress-error').text('');
                        } else {
                            $(this).addClass('is-invalid').removeClass('is-valid');
                            $('#clinicAddress-error').text('Vui lòng nhập địa chỉ cơ sở chỉ định!');
                        }
                    });

                    $('#instructionContent').on('input', function () {
                        if ($(this).val().trim()) {
                            $(this).removeClass('is-invalid').addClass('is-valid');
                            $('#instructionContent-error').text('');
                        } else {
                            $(this).addClass('is-invalid').removeClass('is-valid');
                            $('#instructionContent-error').text('Vui lòng nhập nội dung chỉ định!');
                        }
                    });

                    $('#clinicSampleSelect').on('change', function () {
                        var val = $(this).val();
                        if (val) {
                            try {
                                var data = JSON.parse(val);
                                $('#clinicName').val(data.name).trigger('input');
                                $('#clinicPhone').val(data.phone).trigger('input');
                                $('#clinicAddress').val(data.address).trigger('input');
                            } catch (e) {}
                        }
                    });
                });
            </script>
    </body>
</html>