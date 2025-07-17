<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.Patient" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalRecord" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalRequest" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết phiếu chỉ định - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Homepage specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <style>
            .medical-request-header {
                text-align: center;
                padding-bottom: 20px;
                margin-bottom: 30px;
            }

            .medical-request-section {
                margin-bottom: 25px;
                padding: 20px;
                border-radius: 8px;
                background-color: #fff;
                box-shadow: 0 4px 12px rgba(0,0,0,0.06);
            }

            .medical-request-details {
                border: 1px solid #007bff;
                position: relative;
                overflow: hidden;
            }

            .medical-request-details::before {
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

            .instruction-section {
                background: white;
                border: 1px solid #007bff;
                border-radius: 1rem;
                padding: 1.5rem;
                margin-bottom: 2rem;
                box-shadow: 0 4px 20px rgba(0,0,0,0.08);
                position: relative;
                overflow: hidden;
            }

            .instruction-section::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                height: 4px;
            }

            .instruction-content {
                border-radius: 8px;
                font-size: 1rem;
                line-height: 1.6;
            }

            .receipt-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 1.5rem;
                padding-bottom: 1rem;
                border-bottom: 2px solid #f8f9fa;
            }

            .receipt-title {
                font-size: 1.25rem;
                font-weight: 700;
                color: #2c3e50;
                display: flex;
                align-items: center;
            }

            .receipt-title i {
                font-size: 1.25rem;
                margin-right: 0.5rem;
                padding: 0.3rem;
                border-radius: 0.4rem;
                color: white;
                background: linear-gradient(135deg, #007bff, #0056b3);
                display: inline-flex;
                justify-content: center;
                align-items: center;
                line-height: 1;
            }

            .receipt-time {
                font-size: 0.9rem;
                color: #6c757d;
                font-weight: 500;
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

            .right-column {
                flex: 0 0 75%;
                max-width: 75%;
                padding-left: 15px;
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

            .action-btn-update {
                background: linear-gradient(135deg, #007bff, #0056b3);
                color: white;
            }

            .action-btn-update:hover {
                background: linear-gradient(135deg, #0056b3, #004085);
                color: white;
            }

            .action-btn-success {
                background: linear-gradient(135deg, #28a745, #218838);
                color: white;
            }

            .action-btn-success:hover {
                background: linear-gradient(135deg, #218838, #1e7e34);
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

            @media (max-width: 768px) {
                .left-column, .right-column {
                    flex: 0 0 100%;
                    max-width: 100%;
                    padding-right: 0;
                    padding-left: 0;
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
            }

            @media print {
                .no-print {
                    display: none !important;
                }

                body {
                    font-size: 12pt;
                    line-height: 1.4;
                }

                .medical-request-section {
                    break-inside: avoid;
                    margin-bottom: 15px;
                    border: 1px solid #000;
                    padding: 10px;
                }

                .medical-request-header {
                    border-bottom: 2px solid #000;
                    margin-bottom: 20px;
                }

                .medical-request-details {
                    background: #fff !important;
                    border: 1px solid #000;
                }

                .medical-request-details::before {
                    display: none;
                }

                .instruction-section {
                    border: 1px solid #000;
                    background: #fff;
                }

                .instruction-section::before {
                    display: none;
                }

                .instruction-content {
                    border: 1px solid #000;
                    background: #fff;
                }

                .patient-info-card {
                    border: 1px solid #000;
                }

                .text-primary {
                    color: #000 !important;
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
        MedicalRequest medicalRequest = (MedicalRequest) request.getAttribute("medicalRequest");
        
        // Check if medical record is completed
        boolean isCompleted = medicalRecord != null && "completed".equals(medicalRecord.getStatus());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat shortSdf = new SimpleDateFormat("dd/MM/yyyy");
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
                                    Chi tiết phiếu chỉ định
                                </li>
                            </ol>
                        </nav>
                    </div>
                </div>

                <!-- Alert Messages -->
                <%
                String success = request.getParameter("success");
                String error = request.getParameter("error");
                String message = request.getParameter("message");
                %>

                <% if (success != null) { %>
                <div class="alert alert-success alert-dismissible fade show no-print" role="alert">
                    <i class="bi bi-check-circle me-2"></i>
                    <% if ("updated".equals(success)) { %>
                    Cập nhật phiếu chỉ định thành công!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show no-print" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <% if ("medical_record_completed".equals(error)) { %>
                    Không thể chỉnh sửa phiếu chỉ định vì hồ sơ bệnh án đã được hoàn thành!
                    <% } else { %>
                    Có lỗi xảy ra. Vui lòng thử lại!
                    <% } %>
                    <% if (message != null) { %>
                    <br><%= message %>
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <!-- Action Buttons -->
                <div class="d-flex justify-content-between align-items-center mb-3 no-print">
                    <div>
                        <a href="${pageContext.request.contextPath}/doctor/medical-records?action=edit&recordId=<%= medicalRecord != null ? medicalRecord.getId() : "" %>" 
                           class="action-btn back-btn">
                            <i class="bi bi-arrow-left"></i>
                            <span class="btn-text">Quay lại hồ sơ</span>
                        </a>
                    </div>
                    <div class="action-buttons">
                        <% if (!isCompleted) { %>
                        <a href="${pageContext.request.contextPath}/doctor/medical-requests?action=edit&requestId=<%= medicalRequest.getId() %>" 
                           class="action-btn action-btn-update me-2">
                            <i class="bi bi-pencil-square"></i>
                            <span class="btn-text">Chỉnh sửa</span>
                        </a>
                        <% } %>
                        <button type="button" class="action-btn action-btn-success" onclick="window.print()">
                            <i class="bi bi-printer"></i>
                            <span class="btn-text">In phiếu chỉ định</span>
                        </button>
                    </div>
                </div>

                <% if (medicalRequest != null && patient != null && medicalRecord != null) { %>
                <!-- Medical Request Header -->
                <div class="medical-request-header">
                    <h2 class="text-primary mb-3">
                        <span style="color: #007bff;">Ánh Dương</span>
                        <span style="color: #333;">Clinic</span>
                    </h2>
                    <h4>PHIẾU CHỈ ĐỊNH</h4>
                    <p class="mb-1">Địa chỉ: 123 Đường ABC, Quận XYZ, TP.HCM</p>
                    <p class="mb-0">Điện thoại: (028) 1234-5678 | Email: info@anhduongclinic.com</p>
                </div>

                <!-- Two-Column Layout -->
                <div class="row two-column-container">
                    <!-- Left Column: Patient & Request Info -->
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
                                <p><strong>Điện thoại:</strong> <%= patient.getPhone() %></p>
                                <p><strong>Địa chỉ:</strong> <%= patient.getAddress() %></p>
                            </div>
                            <div class="card-header">
                                <h4 class="text-primary mb-3">
                                    <i class="bi bi-clipboard-data me-2"></i>Thông tin phiếu chỉ định
                                </h4>
                            </div>
                            <div class="card-body">
                                <p><strong>Mã phiếu chỉ định:</strong> <%= medicalRequest.getId() %></p>
                                <p><strong>Mã hồ sơ:</strong> <%= medicalRequest.getMedicalRecordId() %></p>
                                <p><strong>Ngày tạo:</strong> <%= medicalRequest.getCreatedAt() != null ? sdf.format(medicalRequest.getCreatedAt()) : "" %></p>
                                <p><strong>Người tạo:</strong> <%= medicalRequest.getCreatedBy() != null ? medicalRequest.getCreatedBy() : "" %></p>
                            </div>
                        </div>
                    </div>

                    <!-- Right Column: Request Details -->
                    <div class="col-md-9 right-column">
                        <!-- Clinic Information -->
                        <% if (medicalRequest.getClinicName() != null || medicalRequest.getClinicPhone() != null || medicalRequest.getClinicAddress() != null) { %>
                        <div class="medical-request-section medical-request-details">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-building"></i>
                                    Thông tin cơ sở chỉ định
                                </div>
                            </div>
                            <div class="row">
                                    <% if (medicalRequest.getClinicName() != null && !medicalRequest.getClinicName().isEmpty()) { %>
                                    <p class="mb-2"><strong>Tên cơ sở:</strong> <%= medicalRequest.getClinicName() %></p>
                                    <% } %>
                                    <% if (medicalRequest.getClinicPhone() != null && !medicalRequest.getClinicPhone().isEmpty()) { %>
                                    <p class="mb-2"><strong>Điện thoại:</strong> <%= medicalRequest.getClinicPhone() %></p>
                                    <% } %>
                                    <% if (medicalRequest.getClinicAddress() != null && !medicalRequest.getClinicAddress().isEmpty()) { %>
                                    <p class="mb-2"><strong>Địa chỉ:</strong> <%= medicalRequest.getClinicAddress() %></p>
                                    <% } %>
                            </div>
                        </div>
                        <% } %>

                        <!-- Instruction Content -->
                        <div class="instruction-section">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-clipboard-data"></i>
                                    Nội dung chỉ định
                                </div>
                            </div>
                            <div class="instruction-content">
                                <% if (medicalRequest.getInstructionContent() != null && !medicalRequest.getInstructionContent().isEmpty()) { %>
                                <%= medicalRequest.getInstructionContent() %>
                                <% } else { %>
                                <em class="text-muted">Chưa có nội dung chỉ định</em>
                                <% } %>
                            </div>
                        </div>

                        <!-- Instruction Requirements -->
                        <% if (medicalRequest.getInstructionRequirements() != null && !medicalRequest.getInstructionRequirements().isEmpty()) { %>
                        <div class="instruction-section">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-list-check"></i>
                                    Yêu cầu chỉ định
                                </div>
                            </div>
                            <div class="instruction-content">
                                <%= medicalRequest.getInstructionRequirements() %>
                            </div>
                        </div>
                        <% } %>

                        <!-- Notes -->
                        <% if (medicalRequest.getNotes() != null && !medicalRequest.getNotes().isEmpty()) { %>
                        <div class="instruction-section">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-chat-dots"></i>
                                    Ghi chú
                                </div>
                            </div>
                            <div class="instruction-content">
                                <%= medicalRequest.getNotes() %>
                            </div>
                        </div>
                        <% } %>

                        <!-- Signature Section -->
                        <div class="medical-request-section print-section">
                            <div class="signature-section">
                                <div class="row text-center g-4">
                                    <div class="col-md-4">
                                        <p class="mb-4 fw-bold">Bệnh nhân</p>
                                        <p class="signature-line mx-auto"></p>
                                        <p class="mb-0 text-muted"><em>(Ký và ghi rõ họ tên)</em></p>
                                    </div>
                                    <div class="col-md-4">
                                        <p class="mb-4 fw-bold">Người nhận</p>
                                        <p class="signature-line mx-auto"></p>
                                        <p class="mb-0 text-muted"><em>(Ký và ghi rõ họ tên)</em></p>
                                    </div>
                                    <div class="col-md-4">
                                        <p class="mb-4 fw-bold">Bác sĩ chỉ định</p>
                                        <p class="signature-line mx-auto"></p>
                                        <p class="mb-0 text-muted"><em>(Ký và ghi rõ họ tên)</em></p>
                                    </div>
                                </div>
                                <div class="text-center mt-5">
                                    <p class="mb-1 text-muted"><em>Ngày <%= new java.text.SimpleDateFormat("dd").format(new java.util.Date()) %> tháng <%= new java.text.SimpleDateFormat("MM").format(new java.util.Date()) %> năm <%= new java.text.SimpleDateFormat("yyyy").format(new java.util.Date()) %></em></p>
                                    <p class="mb-0 fw-bold text-primary">Cảm ơn quý khách đã sử dụng dịch vụ!</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <% } else { %>
                <div class="alert alert-danger">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    Không tìm thấy thông tin phiếu chỉ định!
                </div>
                <% } %>
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
            });
        </script>
    </body>
</html>