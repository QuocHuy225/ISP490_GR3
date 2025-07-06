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
            }
            
            .prescription-details {
                background-color: #f8f9fa;
            }
            
            .print-section {
                page-break-inside: avoid;
            }
            
            .medicine-table {
                border: 2px solid #007bff;
            }
            
            .medicine-table th {
                background-color: #007bff;
                color: white;
                font-weight: 600;
                text-align: center;
                border: 1px solid #0056b3;
            }
            
            .medicine-table td {
                border: 1px solid #dee2e6;
                padding: 12px 8px;
                vertical-align: top;
            }
            
            .medicine-table .medicine-name {
                font-weight: 600;
                color: #007bff;
            }
            
            .medicine-table .usage-instructions {
                font-style: italic;
                color: #6c757d;
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
                }
                
                .medicine-table {
                    font-size: 11pt;
                }
                
                .medicine-table th,
                .medicine-table td {
                    padding: 8px 6px;
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

                <!-- Back Button -->
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%= patient.getId() %>" 
                       class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left me-2"></i>Quay lại
                    </a>
                    
                    <div class="action-buttons">
                        <a href="${pageContext.request.contextPath}/doctor/actual-prescriptions?action=edit&formId=<%= form.getActualPrescriptionFormId() %>" 
                           class="btn btn-primary me-2">
                            <i class="bi bi-pencil-square me-2"></i>Chỉnh sửa
                        </a>
                        <button type="button" class="btn btn-success" onclick="printPrescription()">
                            <i class="bi bi-printer me-2"></i>In đơn thuốc
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

                <!-- Patient & Prescription Info -->
                <div class="prescription-section prescription-details">
                    <div class="row">
                        <div class="col-md-6">
                            <h6 class="text-primary mb-3"><i class="bi bi-person-fill me-2"></i>Thông tin bệnh nhân</h6>
                            <p class="mb-2"><strong>Mã bệnh nhân:</strong> <%= patient.getPatientCode() %></p>
                            <p class="mb-2"><strong>Họ tên:</strong> <%= patient.getFullName() %></p>
                            <p class="mb-2"><strong>Ngày sinh:</strong> <%= patient.getDob() != null ? sdf.format(patient.getDob()) : "" %></p>
                            <p class="mb-2"><strong>Giới tính:</strong> <%= patient.getGender() == 1 ? "Nam" : "Nữ" %></p>
                            <p class="mb-0"><strong>Điện thoại:</strong> <%= patient.getPhone() %></p>
                        </div>
                        <div class="col-md-6">
                            <h6 class="text-primary mb-3"><i class="bi bi-capsule me-2"></i>Thông tin đơn thuốc</h6>
                            <p class="mb-2"><strong>Mã đơn thuốc:</strong> <%= form.getActualPrescriptionFormId() %></p>
                            <p class="mb-2"><strong>Tên đơn thuốc:</strong> <%= form.getFormName() %></p>
                            <p class="mb-2"><strong>Ngày kê đơn:</strong> <%= form.getPrescriptionDate() != null ? fullSdf.format(form.getPrescriptionDate()) : "" %></p>
                            <p class="mb-2"><strong>Mã hồ sơ:</strong> <%= record.getId() %></p>
                            <% if (form.getNotes() != null && !form.getNotes().trim().isEmpty()) { %>
                            <p class="mb-0"><strong>Ghi chú:</strong> <%= form.getNotes() %></p>
                            <% } %>
                        </div>
                    </div>
                </div>

                <!-- Medicines Table -->
                <div class="prescription-section">
                    <h6 class="text-primary mb-3"><i class="bi bi-capsule-pill me-2"></i>Danh sách thuốc</h6>
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
                    <h6 class="text-primary mb-3"><i class="bi bi-chat-text me-2"></i>Lưu ý đặc biệt</h6>
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle me-2"></i>
                        <%= form.getNotes() %>
                    </div>
                </div>
                <% } %>

                <!-- Signature Section -->
                <div class="prescription-section print-section">
                    <div class="row text-center">
                        <div class="col-md-4">
                            <p class="mb-5"><strong>Bệnh nhân</strong></p>
                            <p class="mb-1">________________________</p>
                            <p class="mb-0"><em>(Ký và ghi rõ họ tên)</em></p>
                        </div>
                        <div class="col-md-4">
                            <p class="mb-5"><strong>Người nhà bệnh nhân</strong></p>
                            <p class="mb-1">________________________</p>
                            <p class="mb-0"><em>(Ký và ghi rõ họ tên)</em></p>
                        </div>
                        <div class="col-md-4">
                            <p class="mb-5"><strong>Bác sĩ điều trị</strong></p>
                            <p class="mb-1">________________________</p>
                            <p class="mb-0"><em>(Ký và ghi rõ họ tên)</em></p>
                        </div>
                    </div>
                    <div class="text-center mt-4">
                        <p class="mb-1"><em>Ngày <%= new java.text.SimpleDateFormat("dd").format(new java.util.Date()) %> tháng <%= new java.text.SimpleDateFormat("MM").format(new java.util.Date()) %> năm <%= new java.text.SimpleDateFormat("yyyy").format(new java.util.Date()) %></em></p>
                        <p class="mb-0"><strong>Đơn thuốc này được kê theo quy định của Bộ Y tế</strong></p>
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