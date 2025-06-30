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
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Homepage specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <style>
            /* Enhanced Medical Record Form Styling */
            .medical-form-container {
                background: linear-gradient(135deg, #f0f8ff 0%, #ffffff 100%);
                border-radius: 1rem;
                box-shadow: 0 8px 32px rgba(0,0,0,0.1);
                padding: 2rem;
                margin-bottom: 2rem;
            }

            .medical-card {
                background: white;
                border: none;
                border-radius: 1rem;
                box-shadow: 0 4px 20px rgba(0,0,0,0.08);
                transition: all 0.3s ease;
                position: relative;
                overflow: hidden;
                margin-bottom: 2rem;
            }

            .medical-card::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                height: 4px;
                background: linear-gradient(90deg, #007bff, #0056b3);
            }

            .medical-card:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 30px rgba(0,0,0,0.12);
            }

            /* Special card themes */
            .vital-signs-card::before {
                background: linear-gradient(90deg, #28a745, #20c997);
            }

            .medical-info-card::before {
                background: linear-gradient(90deg, #6f42c1, #e83e8c);
            }

            .template-card::before {
                background: linear-gradient(90deg, #17a2b8, #138496);
            }

            .medical-card .card-header {
                background: linear-gradient(135deg, #f8f9fa, #e9ecef);
                border: none;
                border-radius: 1rem 1rem 0 0;
                padding: 1.5rem;
                border-bottom: 1px solid #dee2e6;
            }

            .medical-card .card-title {
                color: #2c3e50;
                font-weight: 700;
                font-size: 1.1rem;
                margin: 0;
                display: flex;
                align-items: center;
            }

            .medical-card .card-title i {
                background: linear-gradient(135deg, #007bff, #0056b3);
                color: white;
                padding: 0.5rem;
                border-radius: 0.5rem;
                margin-right: 0.75rem;
                font-size: 1rem;
            }

            /* Vital signs specific styling */
            .vital-signs-card .card-title i {
                background: linear-gradient(135deg, #28a745, #20c997);
            }

            .medical-info-card .card-title i {
                background: linear-gradient(135deg, #6f42c1, #e83e8c);
            }

            .template-card .card-title i {
                background: linear-gradient(135deg, #17a2b8, #138496);
            }

            .medical-card .card-body {
                padding: 2rem;
            }

            /* Enhanced Form Controls */
            .form-control, .form-select {
                border: 2px solid #e9ecef;
                border-radius: 0.75rem;
                padding: 0.75rem 1rem;
                font-weight: 500;
                transition: all 0.3s ease;
                background-color: #fafbfc;
            }

            .form-control:focus, .form-select:focus {
                border-color: #007bff;
                box-shadow: 0 0 0 0.2rem rgba(0,123,255,0.25);
                background-color: #fff;
                transform: translateY(-1px);
            }

            /* Vital Signs Grid */
            .vital-signs-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 1.5rem;
            }

            .vital-sign-item {
                background: linear-gradient(135deg, #f8f9fa, #ffffff);
                border: 2px solid #e9ecef;
                border-radius: 0.75rem;
                padding: 1.5rem;
                transition: all 0.3s ease;
                position: relative;
            }

            .vital-sign-item:hover {
                border-color: #28a745;
                box-shadow: 0 4px 15px rgba(40,167,69,0.15);
                transform: translateY(-2px);
            }

            .vital-sign-item::before {
                content: '';
                position: absolute;
                left: 0;
                top: 0;
                bottom: 0;
                width: 4px;
                background: linear-gradient(180deg, #28a745, #20c997);
                border-radius: 0.75rem 0 0 0.75rem;
                opacity: 0;
                transition: opacity 0.3s ease;
            }

            .vital-sign-item:hover::before {
                opacity: 1;
            }

            /* Form Labels */
            .form-label {
                font-weight: 600;
                color: #495057;
                margin-bottom: 0.5rem;
                display: flex;
                align-items: center;
                justify-content: space-between;
            }

            .form-label .text-muted {
                font-weight: 400;
                font-size: 0.875rem;
            }

            /* Small text helpers */
            .form-text {
                font-size: 0.75rem;
                margin-top: 0.25rem;
                color: #6c757d;
            }

            /* Enhanced buttons */
            .btn-enhanced {
                border-radius: 0.75rem;
                padding: 0.75rem 1.5rem;
                font-weight: 600;
                transition: all 0.3s ease;
                border: none;
                position: relative;
                overflow: hidden;
            }

            .btn-enhanced::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
                transition: left 0.5s;
            }

            .btn-enhanced:hover::before {
                left: 100%;
            }

            .btn-apply {
                background: linear-gradient(135deg, #17a2b8, #138496);
                color: white;
                box-shadow: 0 4px 15px rgba(23,162,184,0.3);
            }

            .btn-apply:hover {
                background: linear-gradient(135deg, #138496, #117a8b);
                transform: translateY(-2px);
                box-shadow: 0 6px 20px rgba(23,162,184,0.4);
                color: white;
            }

            .btn-clear {
                background: linear-gradient(135deg, #6c757d, #5a6268);
                color: white;
                box-shadow: 0 4px 15px rgba(108,117,125,0.3);
            }

            .btn-clear:hover {
                background: linear-gradient(135deg, #5a6268, #495057);
                transform: translateY(-2px);
                box-shadow: 0 6px 20px rgba(108,117,125,0.4);
                color: white;
            }

            .btn-save {
                background: linear-gradient(135deg, #28a745, #20c997);
                color: white;
                box-shadow: 0 4px 15px rgba(40,167,69,0.3);
            }

            .btn-save:hover {
                background: linear-gradient(135deg, #20c997, #1cc88a);
                transform: translateY(-2px);
                box-shadow: 0 6px 20px rgba(40,167,69,0.4);
                color: white;
            }

            /* Textarea enhancements */
            textarea.form-control {
                resize: vertical;
                min-height: 100px;
            }

            textarea.form-control:focus {
                min-height: 120px;
            }

            /* BMI Status Colors */
            .bmi-underweight { color: #17a2b8; font-weight: 600; }
            .bmi-normal { color: #28a745; font-weight: 600; }
            .bmi-overweight { color: #ffc107; font-weight: 600; }
            .bmi-obese { color: #dc3545; font-weight: 600; }

            /* Validation states */
            .form-control.is-valid, .form-select.is-valid {
                border-color: #28a745;
                background-color: #f8fff9;
            }

            .form-control.is-invalid, .form-select.is-invalid {
                border-color: #dc3545;
                background-color: #fff8f8;
            }

            /* Responsive adjustments */
            @media (max-width: 768px) {
                .medical-form-container {
                    padding: 1rem;
                }
                
                .medical-card .card-body {
                    padding: 1.5rem;
                }
                
                .vital-signs-grid {
                    grid-template-columns: 1fr;
                    gap: 1rem;
                }
                
                .vital-sign-item {
                    padding: 1rem;
                }
            }

            /* Enhanced breadcrumb */
            .breadcrumb {
                background: linear-gradient(135deg, #f8f9fa, #e9ecef);
                border-radius: 0.75rem;
                padding: 1rem 1.5rem;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                border: none;
            }

            .breadcrumb-item + .breadcrumb-item::before {
                color: #007bff;
                font-weight: bold;
            }

            /* Patient info card enhancement */
            .patient-info-card {
                background: linear-gradient(135deg, #e3f2fd, #f8f9fa);
                border: 2px solid #2196f3;
                border-radius: 1rem;
            }

            .patient-info-card .card-body {
                padding: 1.5rem;
            }

            .patient-info-card h4 {
                color: #1976d2;
            }

            /* Animation for form sections */
            .medical-card {
                animation: slideInUp 0.5s ease-out;
            }

            @keyframes slideInUp {
                from {
                    opacity: 0;
                    transform: translateY(30px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            /* Stagger animation */
            .medical-card:nth-child(1) { animation-delay: 0.1s; }
            .medical-card:nth-child(2) { animation-delay: 0.2s; }
            .medical-card:nth-child(3) { animation-delay: 0.3s; }
            .medical-card:nth-child(4) { animation-delay: 0.4s; }
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
        
        // Get form data
        Patient patient = (Patient) request.getAttribute("patient");
        MedicalRecord medicalRecord = (MedicalRecord) request.getAttribute("medicalRecord");
        List<MedicalExamTemplate> templates = (List<MedicalExamTemplate>) request.getAttribute("templates");
        Boolean isEdit = (Boolean) request.getAttribute("isEdit");
        
        SimpleDateFormat shortSdf = new SimpleDateFormat("dd/MM/yyyy");
        %>

        <!-- Sidebar -->
        <nav id="sidebar">
            <div class="sidebar-header">
                <h3>MENU</h3>
            </div>
            <ul class="list-unstyled components">
                <% if (currentRole == User.Role.ADMIN) { %>
                <!-- Menu cho Admin -->
                <li>
                    <a href="${pageContext.request.contextPath}/homepage">
                        <i class="bi bi-speedometer2"></i> Trang chủ
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/authorization">
                        <i class="bi bi-people-fill"></i> Quản lý người dùng
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/services">
                        <i class="bi bi-file-medical"></i> Quản lý dịch vụ
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/patients">
                        <i class="bi bi-people"></i> Hồ sơ bệnh nhân
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/medicines">
                        <i class="bi bi-hospital"></i> Quản lý kho thuốc
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/prescriptions">
                        <i class="bi bi-capsule"></i> Quản lý đơn thuốc
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/medical-exam-templates">
                        <i class="bi bi-file-text"></i> Mẫu đơn khám bệnh
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/medical-supplies">
                        <i class="bi bi-gear-fill"></i> Quản lý vật tư
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-bar-chart-fill"></i> Báo cáo thống kê
                    </a>
                </li>
                <% } else if (currentRole == User.Role.DOCTOR) { %>
                <!-- Menu cho Bác sĩ -->
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
                <li>
                    <a href="${pageContext.request.contextPath}/patients">
                        <i class="bi bi-people"></i> Hồ sơ bệnh nhân
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-clipboard-pulse"></i> Toa thuốc
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-journal-medical"></i> Chỉ định dịch vụ
                    </a>
                </li>
                <% } else if (currentRole == User.Role.RECEPTIONIST) { %>
                <!-- Menu cho Lễ tân -->
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
                    <a href="${pageContext.request.contextPath}/patients">
                        <i class="bi bi-people"></i> Hồ sơ bệnh nhân
                    </a>
                </li>
                <% } else { %>
                <!-- Menu cho Bệnh nhân (PATIENT) -->
                <li>
                    <a href="${pageContext.request.contextPath}/homepage">
                        <i class="bi bi-speedometer2"></i> Trang chủ
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/makeappointments">
                        <i class="bi bi-calendar-plus"></i> Đặt lịch hẹn
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-calendar-check"></i> Lịch hẹn của tôi
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-file-medical"></i> Hồ sơ sức khỏe
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-hospital"></i> Dịch vụ
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-chat-dots"></i> Liên hệ bác sĩ
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

                    <div class="navbar-search mx-auto">
                        <i class="bi bi-search"></i>
                        <input type="text" class="form-control" placeholder="Tìm kiếm">
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

            <!-- Main Content Area -->
            <div class="container-fluid mt-4">
                <!-- Breadcrumb & Patient Info -->
                <div class="row mb-4">
                    <div class="col-12">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/patients">
                                        <i class="bi bi-people me-1"></i>Quản lý bệnh nhân
                                    </a>
                                </li>
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/medical-records?action=list&patientId=<%= patient.getId() %>">
                                        Hồ sơ bệnh án
                                    </a>
                                </li>
                                <li class="breadcrumb-item active" aria-current="page">
                                    <%= isEdit ? "Chỉnh sửa" : "Tạo mới" %>
                                </li>
                            </ol>
                        </nav>
                        
                        <div class="card patient-info-card">
                            <div class="card-body">
                                <h4 class="text-primary mb-3">
                                    <i class="bi bi-file-medical me-2"></i><%= isEdit ? "Chỉnh sửa hồ sơ bệnh án" : "Tạo hồ sơ bệnh án mới" %>
                                </h4>
                                <div class="row">
                                    <div class="col-md-6">
                                        <p class="mb-2"><strong>Mã bệnh nhân:</strong> <%= patient.getPatientCode() %></p>
                                        <p class="mb-0"><strong>Họ tên:</strong> <%= patient.getFullName() %></p>
                                    </div>
                                    <div class="col-md-6">
                                        <p class="mb-2"><strong>Ngày sinh:</strong> <%= patient.getDob() != null ? shortSdf.format(patient.getDob()) : "" %></p>
                                        <p class="mb-0"><strong>Điện thoại:</strong> <%= patient.getPhone() %></p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Medical Record Form -->
                <div class="medical-form-container">
                <form method="POST" action="${pageContext.request.contextPath}/medical-records">
                    <input type="hidden" name="action" value="<%= isEdit ? "update" : "add" %>">
                    <input type="hidden" name="patientId" value="<%= patient.getId() %>">
                    <% if (isEdit && medicalRecord != null) { %>
                        <input type="hidden" name="recordId" value="<%= medicalRecord.getId() %>">
                    <% } %>

                    <!-- Template Selection -->
                    <% if (!isEdit) { %>
                    <div class="row mb-4">
                        <div class="col-12">
                            <div class="card medical-card template-card">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-file-text me-2"></i>Chọn mẫu đơn khám bệnh (tùy chọn)
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <select class="form-select" id="templateSelect">
                                                <option value="">Chọn mẫu đơn...</option>
                                                <% if (templates != null) {
                                                    for (MedicalExamTemplate template : templates) { %>
                                                        <option value="<%= template.getId() %>" 
                                                                data-physical="<%= template.getPhysicalExam() != null ? template.getPhysicalExam().replace("\"", "&quot;") : "" %>"
                                                                data-clinical="<%= template.getClinicalInfo() != null ? template.getClinicalInfo().replace("\"", "&quot;") : "" %>"
                                                                data-diagnosis="<%= template.getFinalDiagnosis() != null ? template.getFinalDiagnosis().replace("\"", "&quot;") : "" %>">
                                                            <%= template.getName() %>
                                                        </option>
                                                    <% } %>
                                                <% } %>
                                            </select>
                                        </div>
                                        <div class="col-md-6">
                                            <button type="button" class="btn btn-enhanced btn-apply" onclick="applyTemplate()">
                                                <i class="bi bi-clipboard-plus me-2"></i>Áp dụng mẫu
                                            </button>
                                            <button type="button" class="btn btn-enhanced btn-clear ms-2" onclick="clearForm()">
                                                <i class="bi bi-eraser me-2"></i>Xóa form
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <% } %>

                    <!-- Vital Signs -->
                    <div class="row mb-4">
                        <div class="col-12">
                            <div class="card medical-card vital-signs-card">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-heart-pulse me-2"></i>Chỉ số sinh tồn
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <div class="vital-signs-grid">
                                        <div class="vital-sign-item">
                                            <label for="respirationRate" class="form-label">
                                                Nhịp thở (lần/phút) <span class="text-muted">(8-40)</span>
                                            </label>
                                            <input type="number" class="form-control" id="respirationRate" name="respirationRate" 
                                                   min="8" max="40"
                                                   value="<%= medicalRecord != null && medicalRecord.getRespirationRate() != null ? medicalRecord.getRespirationRate() : "" %>">
                                            <div class="invalid-feedback" id="respirationRate-error"></div>
                                            <small class="form-text text-muted">Bình thường: 12-20 lần/phút</small>
                                        </div>
                                        
                                        <div class="vital-sign-item">
                                            <label for="temperature" class="form-label">
                                                Nhiệt độ (°C) <span class="text-muted">(32-45)</span>
                                            </label>
                                            <input type="number" step="0.1" class="form-control" id="temperature" name="temperature" 
                                                   min="32" max="45"
                                                   value="<%= medicalRecord != null && medicalRecord.getTemperature() != null ? medicalRecord.getTemperature() : "" %>">
                                            <div class="invalid-feedback" id="temperature-error"></div>
                                            <small class="form-text text-muted">Bình thường: 36.0-37.5°C</small>
                                        </div>
                                        
                                        <div class="vital-sign-item">
                                            <label for="pulse" class="form-label">
                                                Mạch (lần/phút) <span class="text-muted">(30-200)</span>
                                            </label>
                                            <input type="number" class="form-control" id="pulse" name="pulse" 
                                                   min="30" max="200"
                                                   value="<%= medicalRecord != null && medicalRecord.getPulse() != null ? medicalRecord.getPulse() : "" %>">
                                            <div class="invalid-feedback" id="pulse-error"></div>
                                            <small class="form-text text-muted">Bình thường: 60-100 lần/phút</small>
                                        </div>
                                        
                                        <div class="vital-sign-item">
                                            <label for="bloodPressure" class="form-label">
                                                Huyết áp (mmHg)
                                            </label>
                                            <input type="text" class="form-control" id="bloodPressure" name="bloodPressure" 
                                                   placeholder="120/80" pattern="^\d{2,3}\/\d{2,3}$"
                                                   value="<%= medicalRecord != null && medicalRecord.getBloodPressure() != null ? medicalRecord.getBloodPressure() : "" %>">
                                            <div class="invalid-feedback" id="bloodPressure-error"></div>
                                            <small class="form-text text-muted">Định dạng: Tâm thu/Tâm trương (VD: 120/80)</small>
                                        </div>
                                        
                                        <div class="vital-sign-item">
                                            <label for="height" class="form-label">
                                                Chiều cao (cm) <span class="text-muted">(50-250)</span>
                                            </label>
                                            <input type="number" step="0.1" class="form-control" id="height" name="height" 
                                                   min="50" max="250"
                                                   value="<%= medicalRecord != null && medicalRecord.getHeight() != null ? medicalRecord.getHeight() : "" %>">
                                            <div class="invalid-feedback" id="height-error"></div>
                                            <small class="form-text text-muted">Người lớn: 140-220cm</small>
                                        </div>
                                        
                                        <div class="vital-sign-item">
                                            <label for="weight" class="form-label">
                                                Cân nặng (kg) <span class="text-muted">(5-300)</span>
                                            </label>
                                            <input type="number" step="0.1" class="form-control" id="weight" name="weight" 
                                                   min="5" max="300"
                                                   value="<%= medicalRecord != null && medicalRecord.getWeight() != null ? medicalRecord.getWeight() : "" %>">
                                            <div class="invalid-feedback" id="weight-error"></div>
                                            <small class="form-text text-muted">Người lớn: 40-150kg</small>
                                        </div>
                                        
                                        <div class="vital-sign-item">
                                            <label for="bmi" class="form-label">
                                                BMI <span class="text-muted">(tự động tính)</span>
                                            </label>
                                            <input type="number" step="0.1" class="form-control" id="bmi" name="bmi" readonly 
                                                   value="<%= medicalRecord != null && medicalRecord.getBmi() != null ? medicalRecord.getBmi() : "" %>">
                                            <small class="form-text" id="bmi-status"></small>
                                        </div>
                                        
                                        <div class="vital-sign-item">
                                            <label for="spo2" class="form-label">
                                                SpO2 (%) <span class="text-muted">(70-100)</span>
                                            </label>
                                            <input type="number" step="0.1" class="form-control" id="spo2" name="spo2" 
                                                   min="70" max="100"
                                                   value="<%= medicalRecord != null && medicalRecord.getSpo2() != null ? medicalRecord.getSpo2() : "" %>">
                                            <div class="invalid-feedback" id="spo2-error"></div>
                                            <small class="form-text text-muted">Bình thường: 95-100%</small>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Medical Information -->
                    <div class="row mb-4">
                        <div class="col-12">
                            <div class="card medical-card medical-info-card">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-clipboard-heart me-2"></i>Thông tin y tế
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="mb-3">
                                                <label for="medicalHistory" class="form-label">Tiền sử bệnh án</label>
                                                <textarea class="form-control" id="medicalHistory" name="medicalHistory" rows="3"><%= medicalRecord != null && medicalRecord.getMedicalHistory() != null ? medicalRecord.getMedicalHistory() : "" %></textarea>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="mb-3">
                                                <label for="currentDisease" class="form-label">Bệnh hiện tại</label>
                                                <textarea class="form-control" id="currentDisease" name="currentDisease" rows="3"><%= medicalRecord != null && medicalRecord.getCurrentDisease() != null ? medicalRecord.getCurrentDisease() : "" %></textarea>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="physicalExam" class="form-label">Khám thể lý</label>
                                        <textarea class="form-control" id="physicalExam" name="physicalExam" rows="4"><%= medicalRecord != null && medicalRecord.getPhysicalExam() != null ? medicalRecord.getPhysicalExam() : "" %></textarea>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="clinicalInfo" class="form-label">Thông tin lâm sàng</label>
                                        <textarea class="form-control" id="clinicalInfo" name="clinicalInfo" rows="4"><%= medicalRecord != null && medicalRecord.getClinicalInfo() != null ? medicalRecord.getClinicalInfo() : "" %></textarea>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="finalDiagnosis" class="form-label">Chẩn đoán cuối cùng</label>
                                        <textarea class="form-control" id="finalDiagnosis" name="finalDiagnosis" rows="3"><%= medicalRecord != null && medicalRecord.getFinalDiagnosis() != null ? medicalRecord.getFinalDiagnosis() : "" %></textarea>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="treatmentPlan" class="form-label">Kế hoạch điều trị</label>
                                        <textarea class="form-control" id="treatmentPlan" name="treatmentPlan" rows="4"><%= medicalRecord != null && medicalRecord.getTreatmentPlan() != null ? medicalRecord.getTreatmentPlan() : "" %></textarea>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label for="note" class="form-label">Ghi chú</label>
                                        <textarea class="form-control" id="note" name="note" rows="3" placeholder="Ghi chú bổ sung..."><%= medicalRecord != null && medicalRecord.getNote() != null ? medicalRecord.getNote() : "" %></textarea>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Status and Actions -->
                    <div class="row mb-4">
                        <div class="col-12">
                            <div class="card medical-card">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-gear me-2"></i>Trạng thái và thao tác
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="mb-3">
                                                <label for="status" class="form-label">Trạng thái hồ sơ</label>
                                                <select class="form-select" id="status" name="status">
                                                    <option value="ongoing" 
                                                            <%= medicalRecord != null && "ongoing".equals(medicalRecord.getStatus()) ? "selected" : "" %>
                                                            <%= medicalRecord != null && "completed".equals(medicalRecord.getStatus()) ? "disabled" : "" %>>
                                                        Đang điều trị
                                                    </option>
                                                    <option value="completed" 
                                                            <%= medicalRecord != null && "completed".equals(medicalRecord.getStatus()) ? "selected" : "" %>>
                                                        Hoàn thành
                                                    </option>
                                                </select>
                                                <% if (medicalRecord != null && "completed".equals(medicalRecord.getStatus())) { %>
                                                    <div class="form-text text-warning">
                                                        <i class="bi bi-exclamation-triangle me-1"></i>
                                                        Hồ sơ đã hoàn thành. Không thể chuyển về trạng thái "Đang điều trị".
                                                    </div>
                                                <% } %>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="d-flex justify-content-between align-items-center">
                                        <a href="${pageContext.request.contextPath}/medical-records?action=list&patientId=<%= patient.getId() %>" 
                                           class="btn btn-enhanced btn-clear">
                                            <i class="bi bi-arrow-left me-2"></i>Quay lại
                                        </a>
                                        
                                        <div>
                                            <button type="button" class="btn btn-enhanced btn-clear me-3" onclick="resetForm()">
                                                <i class="bi bi-arrow-clockwise me-2"></i>Đặt lại
                                            </button>
                                            <button type="submit" class="btn btn-enhanced btn-save">
                                                <i class="bi bi-check-circle me-2"></i><%= isEdit ? "Cập nhật hồ sơ" : "Tạo hồ sơ mới" %>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
                </div>
            </div>
        </div>

        <!-- Bootstrap Bundle with Popper -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <!-- jQuery -->
        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
        
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Sidebar toggle
                const sidebarCollapse = document.getElementById('sidebarCollapse');
                const sidebar = document.getElementById('sidebar');
                const content = document.getElementById('content');

                sidebarCollapse.addEventListener('click', function () {
                    sidebar.classList.toggle('collapsed');
                    content.classList.toggle('expanded');
                });

                // Responsive sidebar
                function checkWidth() {
                    if (window.innerWidth <= 768) {
                        sidebar.classList.add('collapsed');
                        content.classList.add('expanded');
                    } else {
                        sidebar.classList.remove('collapsed');
                        content.classList.remove('expanded');
                    }
                }

                // Initial check
                checkWidth();

                // Listen for window resize
                window.addEventListener('resize', checkWidth);

                // Auto calculate BMI and add validation
                document.getElementById('height').addEventListener('input', function() {
                    calculateBMI();
                    validateVitalSign(this);
                });
                document.getElementById('weight').addEventListener('input', function() {
                    calculateBMI();
                    validateVitalSign(this);
                });
                
                // Add validation to other vital signs
                document.getElementById('respirationRate').addEventListener('input', function() {
                    validateVitalSign(this);
                });
                document.getElementById('temperature').addEventListener('input', function() {
                    validateVitalSign(this);
                });
                document.getElementById('pulse').addEventListener('input', function() {
                    validateVitalSign(this);
                });
                document.getElementById('bloodPressure').addEventListener('input', function() {
                    validateBloodPressure(this);
                });
                document.getElementById('spo2').addEventListener('input', function() {
                    validateVitalSign(this);
                });
                
                // Form submission validation
                document.querySelector('form').addEventListener('submit', function(e) {
                    if (!validateAllVitalSigns()) {
                        e.preventDefault();
                        alert('Vui lòng kiểm tra lại các chỉ số sinh tồn đã nhập!');
                    }
                });
                
                // Check if record is completed and disable fields accordingly
                const statusSelect = document.getElementById('status');
                const isCompleted = statusSelect.value === 'completed';
                
                if (isCompleted) {
                    disableFieldsForCompletedRecord();
                }
                
                // Add event listener for status change
                statusSelect.addEventListener('change', function() {
                    const currentValue = this.value;
                    const originalValue = '<%= medicalRecord != null ? medicalRecord.getStatus() : "ongoing" %>';
                    
                    // Prevent changing from completed back to ongoing
                    if (originalValue === 'completed' && currentValue === 'ongoing') {
                        alert('Không thể chuyển hồ sơ từ trạng thái "Hoàn thành" về "Đang điều trị". Hồ sơ đã hoàn thành không thể chỉnh sửa lại.');
                        this.value = 'completed'; // Reset to completed
                        return;
                    }
                    
                    if (currentValue === 'completed') {
                        if (confirm('Bạn có chắc chắn muốn chuyển hồ sơ sang trạng thái "Hoàn thành"? Sau khi hoàn thành, chỉ có thể chỉnh sửa trường "Ghi chú".')) {
                            disableFieldsForCompletedRecord();
                        } else {
                            this.value = originalValue; // Reset to original value
                        }
                    } else {
                        enableAllFields();
                    }
                });
            });

            function calculateBMI() {
                const height = parseFloat(document.getElementById('height').value);
                const weight = parseFloat(document.getElementById('weight').value);
                
                if (height && weight && height > 0) {
                    const heightInMeters = height / 100;
                    const bmi = weight / (heightInMeters * heightInMeters);
                    const bmiValue = bmi.toFixed(1);
                    document.getElementById('bmi').value = bmiValue;
                    
                    // Update BMI status
                    const bmiStatus = document.getElementById('bmi-status');
                    if (bmi < 18.5) {
                        bmiStatus.textContent = 'Thiếu cân';
                        bmiStatus.className = 'form-text text-info';
                    } else if (bmi < 25) {
                        bmiStatus.textContent = 'Bình thường';
                        bmiStatus.className = 'form-text text-success';
                    } else if (bmi < 30) {
                        bmiStatus.textContent = 'Thừa cân';
                        bmiStatus.className = 'form-text text-warning';
                    } else {
                        bmiStatus.textContent = 'Béo phì';
                        bmiStatus.className = 'form-text text-danger';
                    }
                } else {
                    document.getElementById('bmi').value = '';
                    document.getElementById('bmi-status').textContent = '';
                }
            }
            
            function validateVitalSign(input) {
                const value = parseFloat(input.value);
                const min = parseFloat(input.min);
                const max = parseFloat(input.max);
                const errorDiv = document.getElementById(input.id + '-error');
                
                let isValid = true;
                let errorMessage = '';
                
                if (input.value && (isNaN(value) || value < min || value > max)) {
                    isValid = false;
                    errorMessage = `Giá trị phải nằm trong khoảng ${min} - ${max}`;
                }
                
                // Special validation for specific fields
                if (input.id === 'respirationRate' && value) {
                    if (value < 12 || value > 20) {
                        input.style.borderColor = '#ffc107'; // Warning color
                    } else {
                        input.style.borderColor = '#28a745'; // Success color
                    }
                } else if (input.id === 'temperature' && value) {
                    if (value < 36.0 || value > 37.5) {
                        input.style.borderColor = '#ffc107';
                    } else {
                        input.style.borderColor = '#28a745';
                    }
                } else if (input.id === 'pulse' && value) {
                    if (value < 60 || value > 100) {
                        input.style.borderColor = '#ffc107';
                    } else {
                        input.style.borderColor = '#28a745';
                    }
                } else if (input.id === 'spo2' && value) {
                    if (value < 95) {
                        input.style.borderColor = '#ffc107';
                    } else {
                        input.style.borderColor = '#28a745';
                    }
                }
                
                if (isValid) {
                    input.classList.remove('is-invalid');
                    errorDiv.textContent = '';
                } else {
                    input.classList.add('is-invalid');
                    errorDiv.textContent = errorMessage;
                    input.style.borderColor = '#dc3545'; // Error color
                }
                
                return isValid;
            }
            
            function validateBloodPressure(input) {
                const value = input.value.trim();
                const errorDiv = document.getElementById('bloodPressure-error');
                const pattern = /^\d{2,3}\/\d{2,3}$/;
                
                let isValid = true;
                let errorMessage = '';
                
                if (value && !pattern.test(value)) {
                    isValid = false;
                    errorMessage = 'Định dạng không đúng. Vui lòng nhập theo mẫu: 120/80';
                } else if (value) {
                    const parts = value.split('/');
                    const systolic = parseInt(parts[0]);
                    const diastolic = parseInt(parts[1]);
                    
                    if (systolic < 70 || systolic > 250 || diastolic < 40 || diastolic > 150) {
                        isValid = false;
                        errorMessage = 'Giá trị huyết áp không hợp lý';
                    } else if (systolic <= diastolic) {
                        isValid = false;
                        errorMessage = 'Huyết áp tâm thu phải lớn hơn tâm trương';
                    } else {
                        // Color coding for blood pressure
                        if (systolic < 120 && diastolic < 80) {
                            input.style.borderColor = '#28a745'; // Normal
                        } else if (systolic < 140 && diastolic < 90) {
                            input.style.borderColor = '#ffc107'; // Pre-hypertension
                        } else {
                            input.style.borderColor = '#fd7e14'; // Hypertension
                        }
                    }
                }
                
                if (isValid) {
                    input.classList.remove('is-invalid');
                    errorDiv.textContent = '';
                } else {
                    input.classList.add('is-invalid');
                    errorDiv.textContent = errorMessage;
                    input.style.borderColor = '#dc3545';
                }
                
                return isValid;
            }
            
            function validateAllVitalSigns() {
                const fields = ['respirationRate', 'temperature', 'pulse', 'height', 'weight', 'spo2'];
                let allValid = true;
                
                fields.forEach(fieldId => {
                    const field = document.getElementById(fieldId);
                    if (field.value && !validateVitalSign(field)) {
                        allValid = false;
                    }
                });
                
                const bloodPressure = document.getElementById('bloodPressure');
                if (bloodPressure.value && !validateBloodPressure(bloodPressure)) {
                    allValid = false;
                }
                
                return allValid;
            }

            function applyTemplate() {
                const select = document.getElementById('templateSelect');
                const selectedOption = select.options[select.selectedIndex];
                
                if (selectedOption.value) {
                    if (confirm('Áp dụng mẫu sẽ ghi đè nội dung hiện tại. Bạn có chắc chắn?')) {
                        document.getElementById('physicalExam').value = selectedOption.getAttribute('data-physical') || '';
                        document.getElementById('clinicalInfo').value = selectedOption.getAttribute('data-clinical') || '';
                        document.getElementById('finalDiagnosis').value = selectedOption.getAttribute('data-diagnosis') || '';
                    }
                } else {
                    alert('Vui lòng chọn một mẫu đơn');
                }
            }

            function clearForm() {
                if (confirm('Xóa tất cả nội dung trong form?')) {
                    // Clear vital signs
                    document.getElementById('respirationRate').value = '';
                    document.getElementById('temperature').value = '';
                    document.getElementById('pulse').value = '';
                    document.getElementById('bloodPressure').value = '';
                    document.getElementById('height').value = '';
                    document.getElementById('weight').value = '';
                    document.getElementById('bmi').value = '';
                    document.getElementById('spo2').value = '';
                    
                    // Clear medical information
                    document.getElementById('medicalHistory').value = '';
                    document.getElementById('currentDisease').value = '';
                    document.getElementById('physicalExam').value = '';
                    document.getElementById('clinicalInfo').value = '';
                    document.getElementById('finalDiagnosis').value = '';
                    document.getElementById('treatmentPlan').value = '';
                    document.getElementById('note').value = '';
                    
                    // Reset template selection
                    const templateSelect = document.getElementById('templateSelect');
                    if (templateSelect) {
                        templateSelect.selectedIndex = 0;
                    }
                    
                    // Reset status
                    document.getElementById('status').value = 'ongoing';
                }
            }

            function resetForm() {
                if (confirm('Đặt lại form về trạng thái ban đầu?')) {
                    location.reload();
                }
            }

            function disableFieldsForCompletedRecord() {
                // Disable all fields except note and status
                const fieldsToDisable = [
                    'doctorId', 'respirationRate', 'temperature', 'height', 'pulse', 
                    'bmi', 'weight', 'bloodPressure', 'spo2', 'medicalHistory', 
                    'currentDisease', 'physicalExam', 'clinicalInfo', 'finalDiagnosis', 'treatmentPlan'
                ];
                
                fieldsToDisable.forEach(fieldId => {
                    const field = document.getElementById(fieldId);
                    if (field) {
                        field.disabled = true;
                        field.classList.add('form-control-disabled');
                    }
                });
                
                // Show warning message
                showCompletedRecordWarning();
            }
            
            function enableAllFields() {
                // Enable all fields
                const fieldsToEnable = [
                    'doctorId', 'respirationRate', 'temperature', 'height', 'pulse', 
                    'bmi', 'weight', 'bloodPressure', 'spo2', 'medicalHistory', 
                    'currentDisease', 'physicalExam', 'clinicalInfo', 'finalDiagnosis', 'treatmentPlan'
                ];
                
                fieldsToEnable.forEach(fieldId => {
                    const field = document.getElementById(fieldId);
                    if (field) {
                        field.disabled = false;
                        field.classList.remove('form-control-disabled');
                    }
                });
                
                // Hide warning message
                hideCompletedRecordWarning();
            }
            
            function showCompletedRecordWarning() {
                // Remove existing warning if any
                hideCompletedRecordWarning();
                
                const warningDiv = document.createElement('div');
                warningDiv.id = 'completed-warning';
                warningDiv.className = 'alert alert-warning alert-dismissible fade show mt-3 completed-warning';
                warningDiv.innerHTML = `
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <strong>Lưu ý:</strong> Hồ sơ đã hoàn thành. Chỉ có thể chỉnh sửa trường "Ghi chú". 
                    Không thể chuyển về trạng thái "Đang điều trị" hoặc chỉnh sửa các thông tin khác.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                `;
                
                const statusCard = document.querySelector('.card-body');
                statusCard.insertBefore(warningDiv, statusCard.firstChild);
            }
            
            function hideCompletedRecordWarning() {
                const warning = document.getElementById('completed-warning');
                if (warning) {
                    warning.remove();
                }
            }
        </script>
    </body>
</html> 