<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.Patient" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalRecord" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.math.BigDecimal" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết hồ sơ bệnh án - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Homepage specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <!-- Medical Record specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/medical-record.css">
        <style>
            .medical-header {
                text-align: center;
                border-bottom: 2px solid #007bff;
                padding-bottom: 20px;
                margin-bottom: 30px;
            }

            .medical-section {
                margin-bottom: 25px;
                padding: 20px;
                border: 1px solid #007bff;
                border-radius: 1rem;
                background-color: #fff;
                box-shadow: 0 4px 12px rgba(0,0,0,0.06);
            }

            .medical-details {
                background-color: #f8f9fa;
            }

            .vital-signs {
                border: 1px solid #007bff;
                border-radius: 1rem;
                position: relative;
                overflow: hidden;
            }

            .vital-signs::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                height: 4px;
            }

            .patient-info-card {
                border: none;
                border-radius: 8px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.06);
                margin-bottom: 1rem;
            }

            .patient-info-card .card-header {
                background: none;
                border-bottom: none;
                padding: 12px;
            }

            .patient-info-card .card-header h5 {
                font-weight: 600;
                font-size: 1rem;
                margin: 0;
                color: #007bff;
            }

            .patient-info-card .card-body {
                padding: 15px;
            }

            .patient-info-card p {
                margin-bottom: 10px;
                padding: 5px 5px 5px 20px;
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

            .notes-section {
                border-radius: 8px;
                font-size: 1rem;
                line-height: 1.6;
                padding: 1rem;
            }

            .signature-section .signature-line {
                border-bottom: 2px solid #343a40;
                width: 180px;
                height: 2px;
                margin: 0 auto;
            }

            .two-column-container {
                display: flex;
                flex-wrap: wrap;
            }

            .left-column {
                flex: 0 0 25%;
                max-width: 25%;
                padding-right: 15px;
            }

            .receipt-header {
                color: #0d6efd; /* Màu xanh dương Bootstrap */
                font-size: 1.2rem; /* Tăng kích thước chữ */
                font-weight: 600; /* Đậm hơn một chút nếu muốn */
            }

            .receipt-header .bi {
                font-size: 1.4rem; /* Tăng kích thước icon */
                margin-right: 6px; /* Khoảng cách giữa icon và chữ */
                vertical-align: middle;
            }

            .right-column {
                flex: 0 0 75%;
                max-width: 75%;
                padding-left: 15px;
            }

            @media (max-width: 768px) {
                .left-column, .right-column {
                    flex: 0 0 100%;
                    max-width: 100%;
                    padding-right: 0;
                    padding-left: 0;
                }
            }

            @media print {
                .no-print {
                    display: none !important;
                }

                body {
                    font-size: 12pt;
                    line-height: 1.4;
                }

                .medical-section {
                    break-inside: avoid;
                    margin-bottom: 15px;
                    border: 1px solid #000;
                    padding: 10px;
                }

                .medical-header {
                    border-bottom: 2px solid #000;
                    margin-bottom: 20px;
                }

                .vital-signs {
                    background: #f5f5f5 !important;
                    border: 1px solid #000;
                }

                .medical-details {
                    background: #fff !important;
                }

                .patient-info-card {
                    border: 1px solid #000;
                }

                .badge {
                    background-color: #000 !important;
                    color: #fff !important;
                }

                .text-primary {
                    color: #000 !important;
                }

                .text-success {
                    color: #000 !important;
                }

                .vital-signs::before {
                    display: none;
                }

                .left-column, .right-column {
                    flex: 0 0 100%;
                    max-width: 100%;
                    padding-right: 0;
                    padding-left: 0;
                }
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
        Patient patient = (Patient) request.getAttribute("patient");
        MedicalRecord medicalRecord = (MedicalRecord) request.getAttribute("medicalRecord");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat shortSdf = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.0");
        %>

        <!-- Sidebar -->
        <nav id="sidebar" class="no-print">
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
            <nav class="navbar navbar-expand-lg top-navbar no-print">
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
                <!-- Breadcrumb -->
                <div class="row mb-4 no-print">
                    <div class="col-12">
                        <nav aria-label="breadcrumb">
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
                                    Chi tiết hồ sơ bệnh án
                                </li>
                            </ol>
                        </nav>
                    </div>
                </div>

                <!-- Back Button & Actions -->
                <div class="d-flex justify-content-between align-items-center mb-3 no-print">
                    <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%= patient.getId() %>" 
                       class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left me-2"></i>Quay lại
                    </a>
                    <div class="action-buttons">
                        <% if (medicalRecord != null) { %>
                        <a href="${pageContext.request.contextPath}/doctor/medical-records?action=edit&recordId=<%= medicalRecord.getId() %>" 
                           class="btn btn-primary me-2">
                            <i class="bi bi-pencil-square me-2"></i>Chỉnh sửa
                        </a>
                        <% } %>
                        <button type="button" class="btn btn-success" onclick="window.print()">
                            <i class="bi bi-printer me-2"></i>In hồ sơ
                        </button>
                    </div>
                </div>

                <% if (medicalRecord != null && patient != null) { %>
                <!-- Medical Record Header -->
                <div class="medical-header">
                    <h2 class="text-primary mb-3">
                        <span style="color: #007bff;">Ánh Dương</span>
                        <span style="color: #333;">Clinic</span>
                    </h2>
                    <h4>HỒ SƠ BỆNH ÁN</h4>
                    <p class="mb-1">Địa chỉ: 123 Đường ABC, Quận XYZ, TP.HCM</p>
                    <p class="mb-0">Điện thoại: (028) 1234-5678 | Email: info@anhduongclinic.com</p>
                </div>

                <!-- Two-Column Layout -->
                <div class="row two-column-container">
                    <!-- Left Column: Patient & Record Info -->
                    <div class="col-md-3 left-column">
                        <div class="card medical-card patient-info-card">
                            <div class="card-header">
                                <h4 class="text-primary mb-3">
                                    <i class="bi bi-person-fill me-2"></i>Thông tin bệnh nhân
                                </h4>
                            </div>
                            <div class="card-body">
                                <p><strong>Mã bệnh nhân:</strong> <%= patient.getPatientCode() %></p>
                                <p><strong>Họ tên:</strong> <%= patient.getFullName() %></p>
                                <p><strong>Ngày sinh:</strong> <%= patient.getDob() != null ? shortSdf.format(patient.getDob()) : "" %></p>
                                <p><strong>Giới tính:</strong> <%= patient.getGender() == 1 ? "Nam" : "Nữ" %></p>
                                <p><strong>Điện thoại:</strong> <%= patient.getPhone() %></p>
                                <p><strong>Địa chỉ:</strong> <%= patient.getAddress() %></p>
                            </div>
                            <div class="card-header">
                                <h4 class="text-primary mb-3">
                                    <i class="bi bi-file-medical me-2"></i>Thông tin hồ sơ
                                </h4>
                            </div>
                            <div class="card-body">
                                <p><strong>Mã hồ sơ:</strong> <%= medicalRecord.getId() %></p>
                                <p><strong>Ngày tạo:</strong> <%= medicalRecord.getCreatedAt() != null ? sdf.format(medicalRecord.getCreatedAt()) : "" %></p>
                                <p><strong>Trạng thái:</strong>
                                    <% if ("ongoing".equals(medicalRecord.getStatus())) { %>
                                    <span class="badge bg-warning text-dark">
                                        <i class="bi bi-clock me-1"></i>Đang điều trị
                                    </span>
                                    <% } else { %>
                                    <span class="badge bg-success">
                                        <i class="bi bi-check-circle me-1"></i>Hoàn thành
                                    </span>
                                    <% } %>
                                </p>
                                <% if (medicalRecord.getUpdatedAt() != null) { %>
                                <p><strong>Cập nhật cuối:</strong> <%= sdf.format(medicalRecord.getUpdatedAt()) %></p>
                                <% } %>
                            </div>
                        </div>
                    </div>

                    <!-- Right Column: Medical Details -->
                    <div class="col-md-9 right-column">
                        <!-- Vital Signs -->
                        <div class="medical-section vital-signs">
                            <div class="receipt-header">
                                <div class="receipt-title" title="Thông tin về chỉ số sinh tồn của bệnh nhân">
                                    <i class="bi bi-heart-pulse"></i>
                                    Chỉ số sinh tồn
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-3">
                                    <p class="mb-2"><strong>Nhiệt độ:</strong> 
                                        <%= medicalRecord.getTemperature() != null ? decimalFormat.format(medicalRecord.getTemperature()) + "°C" : "N/A" %>
                                    </p>
                                    <p class="mb-0"><strong>Mạch:</strong> 
                                        <%= medicalRecord.getPulse() != null ? medicalRecord.getPulse() + " lần/phút" : "N/A" %>
                                    </p>
                                </div>
                                <div class="col-md-3">
                                    <p class="mb-2"><strong>Huyết áp:</strong> 
                                        <%= medicalRecord.getBloodPressure() != null ? medicalRecord.getBloodPressure() + " mmHg" : "N/A" %>
                                    </p>
                                    <p class="mb-0"><strong>Nhịp thở:</strong> 
                                        <%= medicalRecord.getRespirationRate() != null ? medicalRecord.getRespirationRate() + " lần/phút" : "N/A" %>
                                    </p>
                                </div>
                                <div class="col-md-3">
                                    <p class="mb-2"><strong>Chiều cao:</strong> 
                                        <%= medicalRecord.getHeight() != null ? decimalFormat.format(medicalRecord.getHeight()) + " cm" : "N/A" %>
                                    </p>
                                    <p class="mb-0"><strong>Cân nặng:</strong> 
                                        <%= medicalRecord.getWeight() != null ? decimalFormat.format(medicalRecord.getWeight()) + " kg" : "N/A" %>
                                    </p>
                                </div>
                                <div class="col-md-3">
                                    <p class="mb-2"><strong>BMI:</strong> 
                                        <%= medicalRecord.getBmi() != null ? decimalFormat.format(medicalRecord.getBmi()) : "N/A" %>
                                    </p>
                                    <p class="mb-0"><strong>SpO2:</strong> 
                                        <%= medicalRecord.getSpo2() != null ? decimalFormat.format(medicalRecord.getSpo2()) + "%" : "N/A" %>
                                    </p>
                                </div>
                            </div>
                        </div>

                        <!-- Medical History -->
                        <% if (medicalRecord.getMedicalHistory() != null && !medicalRecord.getMedicalHistory().trim().isEmpty()) { %>
                        <div class="medical-section medical-details">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-clock-history"></i>
                                    Tiền sử bệnh
                                </div>
                            </div>
                            <div class="notes-section">
                                <p class="mb-0"><%= medicalRecord.getMedicalHistory() %></p>
                            </div>
                        </div>
                        <% } %>

                        <!-- Current Disease -->
                        <% if (medicalRecord.getCurrentDisease() != null && !medicalRecord.getCurrentDisease().trim().isEmpty()) { %>
                        <div class="medical-section medical-details">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-bandaid"></i>
                                    Bệnh hiện tại
                                </div>
                            </div>
                            <div class="notes-section">
                                <p class="mb-0"><%= medicalRecord.getCurrentDisease() %></p>
                            </div>
                        </div>
                        <% } %>

                        <!-- Physical Examination -->
                        <% if (medicalRecord.getPhysicalExam() != null && !medicalRecord.getPhysicalExam().trim().isEmpty()) { %>
                        <div class="medical-section medical-details">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-search"></i>
                                    Khám thể lực
                                </div>
                            </div>
                            <div class="notes-section">
                                <p class="mb-0"><%= medicalRecord.getPhysicalExam() %></p>
                            </div>
                        </div>
                        <% } %>

                        <!-- Clinical Information -->
                        <% if (medicalRecord.getClinicalInfo() != null && !medicalRecord.getClinicalInfo().trim().isEmpty()) { %>
                        <div class="medical-section medical-details">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-clipboard-data"></i>
                                    Thông tin lâm sàng
                                </div>
                            </div>
                            <div class="notes-section">
                                <p class="mb-0"><%= medicalRecord.getClinicalInfo() %></p>
                            </div>
                        </div>
                        <% } %>

                        <!-- Final Diagnosis -->
                        <% if (medicalRecord.getFinalDiagnosis() != null && !medicalRecord.getFinalDiagnosis().trim().isEmpty()) { %>
                        <div class="medical-section medical-details">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-clipboard-check"></i>
                                    Chẩn đoán cuối cùng
                                </div>
                            </div>
                            <div class="notes-section">
                                <p class="mb-0"><%= medicalRecord.getFinalDiagnosis() %></p>
                            </div>
                        </div>
                        <% } %>

                        <!-- Treatment Plan -->
                        <% if (medicalRecord.getTreatmentPlan() != null && !medicalRecord.getTreatmentPlan().trim().isEmpty()) { %>
                        <div class="medical-section medical-details">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-prescription2"></i>
                                    Kế hoạch điều trị
                                </div>
                            </div>
                            <div class="notes-section">
                                <p class="mb-0"><%= medicalRecord.getTreatmentPlan() %></p>
                            </div>
                        </div>
                        <% } %>

                        <!-- Note -->
                        <% if (medicalRecord.getNote() != null && !medicalRecord.getNote().trim().isEmpty()) { %>
                        <div class="medical-section medical-details">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-sticky"></i>
                                    Ghi chú
                                </div>
                            </div>
                            <div class="notes-section">
                                <p class="mb-0"><%= medicalRecord.getNote() %></p>
                            </div>
                        </div>
                        <% } %>

                        <!-- Signature Section -->
                        <div class="medical-section print-section">
                            <div class="signature-section">
                                <div class="row text-center g-4">
                                    <div class="col-md-4">
                                        <p class="mb-4 fw-bold">Bệnh nhân</p>
                                        <p class="signature-line mx-auto"></p>
                                        <p class="mb-0 text-muted"><em>(Ký và ghi rõ họ tên)</em></p>
                                    </div>
                                    <div class="col-md-4">
                                        <p class="mb-4 fw-bold">Lễ tân</p>
                                        <p class="signature-line mx-auto"></p>
                                        <p class="mb-0 text-muted"><em>(Ký và ghi rõ họ tên)</em></p>
                                    </div>
                                    <div class="col-md-4">
                                        <p class="mb-4 fw-bold">Bác sĩ điều trị</p>
                                        <p class="signature-line mx-auto"></p>
                                        <p class="mb-0 text-muted"><em>(Ký và ghi rõ họ tên)</em></p>
                                    </div>
                                </div>
                                <div class="text-center mt-5">
                                    <p class="mb-1 text-muted"><em>Ngày <%= new java.text.SimpleDateFormat("dd").format(new java.util.Date()) %> tháng <%= new java.text.SimpleDateFormat("MM").format(new java.util.Date()) %> năm <%= new java.text.SimpleDateFormat("yyyy").format(new java.util.Date()) %></em></p>
                                    <p class="mb-0 fw-bold text-primary">Hồ sơ này được lập theo quy định của Bộ Y tế</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <% } else { %>
                <div class="alert alert-danger">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    Không tìm thấy thông tin hồ sơ bệnh án!
                </div>
                <% } %>
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

                                if (sidebarCollapse) {
                                    sidebarCollapse.addEventListener('click', function () {
                                        sidebar.classList.toggle('collapsed');
                                        content.classList.toggle('expanded');
                                    });
                                }

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
                                window.addEventListener('resize', checkWidth);
                            });
        </script>
    </body>
</html>