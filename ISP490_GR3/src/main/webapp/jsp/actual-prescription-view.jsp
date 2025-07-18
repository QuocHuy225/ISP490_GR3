<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.Patient" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalRecord" %>
<%@ page import="com.mycompany.isp490_gr3.model.ActualPrescriptionForm" %>
<%@ page import="com.mycompany.isp490_gr3.model.ActualPrescriptionMedicine" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Xem đơn thuốc - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Homepage specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <style>
            .prescription-header {
                text-align: center;
                border-bottom: 2px solid #007bff;
                padding-bottom: 20px;
                margin-bottom: 30px;
            }

            .prescription-section {
                margin-bottom: 25px;
                padding: 20px;
                border: 1px solid #e0e0e0;
                border-radius: 8px;
                background-color: #fff;
                box-shadow: 0 4px 12px rgba(0,0,0,0.06);
            }

            .prescription-details {
                background: linear-gradient(135deg, #e3f2fd, #bbdefb);
                border: 2px solid #007bff;
                position: relative;
                overflow: hidden;
            }

            .prescription-details::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                height: 4px;
                background: linear-gradient(90deg, #007bff, #0056b3);
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

            .medicine-table {
                border: 2px solid #007bff;
                border-radius: 8px;
                overflow: hidden;
            }

            .medicine-table th {
                background: #cfe2ff;
                color: black;
                font-weight: 600;
                text-align: center;
                border: 0.5px solid #fff;
                padding: 12px;
            }

            .medicine-table td {
                border: 1px solid #dee2e6;
                padding: 12px 8px;
                vertical-align: middle;
                font-size: 1rem;
            }

            .medicine-table .medicine-name {
                font-weight: 600;
                color: #007bff;
            }

            .medicine-table .usage-instructions {
                font-style: italic;
                color: #6c757d;
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

                .medicine-table th,
                .medicine-table td {
                    font-size: 0.85rem;
                    padding: 8px 6px;
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

                .prescription-section {
                    break-inside: avoid;
                    margin-bottom: 15px;
                    border: 1px solid #000;
                    padding: 10px;
                }

                .prescription-header {
                    border-bottom: 2px solid #000;
                    margin-bottom: 20px;
                }

                .prescription-details {
                    background: #fff !important;
                    border: 1px solid #000;
                }

                .prescription-details::before {
                    display: none;
                }

                .patient-info-card {
                    border: 1px solid #000;
                }

                .medicine-table {
                    font-size: 11pt;
                    border: 2px solid #000;
                }

                .medicine-table th,
                .medicine-table td {
                    padding: 8px 6px;
                    border: 1px solid #000;
                }

                .medicine-table th {
                    background: #000 !important;
                    color: #fff !important;
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
        ActualPrescriptionForm form = (ActualPrescriptionForm) request.getAttribute("form");
        Patient patient = (Patient) request.getAttribute("patient");
        MedicalRecord record = (MedicalRecord) request.getAttribute("medicalRecord");
        
        // Check if medical record is completed
        boolean isCompleted = record != null && "completed".equals(record.getStatus());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fullSdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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
                                    Chi tiết đơn thuốc
                                </li>
                            </ol>
                        </nav>
                    </div>
                </div>

                <!-- Action Buttons -->
                <div class="d-flex justify-content-between align-items-center mb-3 no-print">
                    <div>
                        <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%= patient.getId() %>" 
                           class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left me-2"></i>Quay lại
                        </a>
                    </div>
                    <div class="action-buttons">
                        <a href="${pageContext.request.contextPath}/doctor/actual-prescriptions?action=edit&formId=<%= form.getActualPrescriptionFormId() %>" 
                           class="btn btn-primary me-2">
                            <i class="bi bi-pencil-square me-2"></i>Chỉnh sửa
                        </a>
                        <button type="button" class="action-btn action-btn-success" onclick="window.print()">
                            <i class="bi bi-printer"></i>
                            <span class="btn-text">In đơn thuốc</span>
                        </button>
                    </div>
                </div>

                <% if (form != null && patient != null && record != null) { %>
                <!-- Prescription Header -->
                <div class="prescription-header">
                    <h2 class="text-primary mb-3">
                        <span style="color: #007bff;">Ánh Dương</span>
                        <span style="color: #333;">Clinic</span>
                    </h2>
                    <h4>ĐƠN THUỐC</h4>
                    <p class="mb-1">Địa chỉ: 123 Đường ABC, Quận XYZ, TP.HCM</p>
                    <p class="mb-0">Điện thoại: (028) 1234-5678 | Email: info@anhduongclinic.com</p>
                </div>

                <!-- Two-Column Layout -->
                <div class="row two-column-container">
                    <!-- Left Column: Patient & Prescription Info -->
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
                                <p><strong>Ngày sinh:</strong> <%= patient.getDob() != null ? sdf.format(patient.getDob()) : "" %></p>
                                <p><strong>Giới tính:</strong> <%= patient.getGender() == 1 ? "Nam" : "Nữ" %></p>
                                <p><strong>Điện thoại:</strong> <%= patient.getPhone() %></p>
                            </div>
                            <div class="card-header">
                                <h4 class="text-primary mb-3">
                                    <i class="bi bi-capsule me-2"></i>Thông tin đơn thuốc
                                </h4>
                            </div>
                            <div class="card-body">
                                <p><strong>Mã đơn thuốc:</strong> <%= form.getActualPrescriptionFormId() %></p>
                                <p><strong>Tên đơn thuốc:</strong> <%= form.getFormName() %></p>
                                <p><strong>Ngày kê đơn:</strong> <%= form.getPrescriptionDate() != null ? fullSdf.format(form.getPrescriptionDate()) : "" %></p>
                                <p><strong>Mã hồ sơ:</strong> <%= record.getId() %></p>
                            </div>
                        </div>
                    </div>

                    <!-- Right Column: Prescription Details -->
                    <div class="col-md-9 right-column">
                        <!-- Medicines Table -->
                        <div class="prescription-section">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-capsule-pill"></i>
                                    Danh sách thuốc
                                </div>
                            </div>
                            <% if (form.getMedicines() != null && !form.getMedicines().isEmpty()) { %>
                            <div class="table-responsive">
                                <table class="table medicine-table">
                                    <thead>
                                        <tr>
                                            <th style="width: 5%;">STT</th>
                                            <th style="width: 25%;">Tên thuốc</th>
                                            <th style="width: 10%;">ĐVT</th>
                                            <th style="width: 10%;">Đường dùng</th>
                                            <th style="width: 8%;">Số ngày</th>
                                            <th style="width: 8%;">Lần/ngày</th>
                                            <th style="width: 8%;">Tổng SL</th>
                                            <th style="width: 26%;">Hướng dẫn sử dụng</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% int index = 1; for (ActualPrescriptionMedicine medicine : form.getMedicines()) { %>
                                        <tr>
                                            <td class="text-center"><%= index++ %></td>
                                            <td class="medicine-name"><%= medicine.getMedicineName() %></td>
                                            <td class="text-center"><%= medicine.getUnitOfMeasure() != null ? medicine.getUnitOfMeasure() : "" %></td>
                                            <td class="text-center"><%= medicine.getAdministrationRoute() != null ? medicine.getAdministrationRoute() : "" %></td>
                                            <td class="text-center"><%= medicine.getDaysOfTreatment() != null ? medicine.getDaysOfTreatment() : "" %></td>
                                            <td class="text-center"><%= medicine.getUnitsPerDay() != null ? medicine.getUnitsPerDay() : "" %></td>
                                            <td class="text-center"><%= medicine.getTotalQuantity() != null ? medicine.getTotalQuantity() : "" %></td>
                                            <td class="usage-instructions">
                                                <%= medicine.getUsageInstructions() != null ? medicine.getUsageInstructions() : "" %>
                                            </td>
                                        </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                            <% } else { %>
                            <div class="text-center py-4">
                                <i class="bi bi-capsule display-4 text-muted"></i>
                                <p class="text-muted mt-2">Không có thuốc nào trong đơn này.</p>
                            </div>
                            <% } %>
                        </div>

                        <!-- Additional Notes -->
                        <% if (form.getNotes() != null && !form.getNotes().trim().isEmpty()) { %>
                        <div class="prescription-section">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-chat-text"></i>
                                    Lưu ý đặc biệt
                                </div>
                            </div>
                            <div class="alert alert-info">
                                <i class="bi bi-info-circle me-2"></i>
                                <%= form.getNotes() %>
                            </div>
                        </div>
                        <% } %>

                        <!-- Signature Section -->
                        <div class="prescription-section print-section">
                            <div class="signature-section">
                                <div class="row text-center g-4">
                                    <div class="col-md-4">
                                        <p class="mb-4 fw-bold">Bệnh nhân</p>
                                        <p class="signature-line mx-auto"></p>
                                        <p class="mb-0 text-muted"><em>(Ký và ghi rõ họ tên)</em></p>
                                    </div>
                                    <div class="col-md-4">
                                        <p class="mb-4 fw-bold">Người nhà bệnh nhân</p>
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
                                    <p class="mb-0 fw-bold text-primary">Đơn thuốc này được kê theo quy định của Bộ Y tế</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <% } else { %>
                <div class="alert alert-danger">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    Không tìm thấy thông tin đơn thuốc!
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