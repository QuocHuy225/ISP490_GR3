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
                                    <a href="${pageContext.request.contextPath}/patients">
                                        <i class="bi bi-people me-1"></i>Quản lý bệnh nhân
                                    </a>
                                </li>
                                <% if (patient != null) { %>
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/medical-records?action=list&patientId=<%= patient.getId() %>">
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
                <div class="row mb-4 no-print">
                    <div class="col-12">
                        <% if (medicalRecord != null && patient != null) { %>
                        <div class="d-flex justify-content-start align-items-center mb-3">
                            <a href="${pageContext.request.contextPath}/medical-records?action=list&patientId=<%= patient.getId() %>" 
                               class="btn btn-outline-secondary me-3">
                                <i class="bi bi-arrow-left me-2"></i>Quay lại Hồ sơ bệnh án
                            </a>
                            <h4 class="mb-0 text-primary">Chi tiết hồ sơ bệnh án</h4>
                        </div>
                        <% } %>
                        
                        <div class="d-flex justify-content-end align-items-center">
                            <div class="btn-group">
                                <button type="button" class="btn btn-info" onclick="window.print()">
                                    <i class="bi bi-printer me-2"></i>In hồ sơ
                                </button>
                                <% if (medicalRecord != null) { %>
                                <a href="${pageContext.request.contextPath}/medical-records?action=edit&recordId=<%= medicalRecord.getId() %>" 
                                   class="btn btn-primary">
                                    <i class="bi bi-pencil-square me-2"></i>Chỉnh sửa
                                </a>
                                <% } %>
                            </div>
                        </div>
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

                <!-- Patient & Record Info -->
                <div class="medical-section medical-details">
                    <div class="row">
                        <div class="col-md-6">
                            <h6 class="text-primary mb-3"><i class="bi bi-person-fill me-2"></i>Thông tin bệnh nhân</h6>
                            <p class="mb-2"><strong>Mã bệnh nhân:</strong> <%= patient.getPatientCode() %></p>
                            <p class="mb-2"><strong>Họ tên:</strong> <%= patient.getFullName() %></p>
                            <p class="mb-2"><strong>Ngày sinh:</strong> <%= patient.getDob() != null ? shortSdf.format(patient.getDob()) : "" %></p>
                            <p class="mb-2"><strong>Giới tính:</strong> <%= patient.getGender() == 1 ? "Nam" : "Nữ" %></p>
                            <p class="mb-2"><strong>Điện thoại:</strong> <%= patient.getPhone() %></p>
                            <p class="mb-0"><strong>Địa chỉ:</strong> <%= patient.getAddress() %></p>
                        </div>
                        <div class="col-md-6">
                            <h6 class="text-primary mb-3"><i class="bi bi-file-medical me-2"></i>Thông tin hồ sơ</h6>
                            <p class="mb-2"><strong>Mã hồ sơ:</strong> <%= medicalRecord.getId() %></p>
                            <p class="mb-2"><strong>Ngày tạo:</strong> <%= medicalRecord.getCreatedAt() != null ? sdf.format(medicalRecord.getCreatedAt()) : "" %></p>
                            <p class="mb-2"><strong>Trạng thái:</strong>
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
                            <p class="mb-0"><strong>Cập nhật cuối:</strong> <%= sdf.format(medicalRecord.getUpdatedAt()) %></p>
                            <% } %>
                        </div>
                    </div>
                </div>

                <!-- Vital Signs -->
                <div class="medical-section vital-signs">
                    <h6 class="text-success mb-3"><i class="bi bi-heart-pulse me-2"></i>Dấu hiệu sinh tồn</h6>
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
                    <h6 class="text-primary mb-3"><i class="bi bi-clock-history me-2"></i>Tiền sử bệnh</h6>
                    <p class="mb-0"><%= medicalRecord.getMedicalHistory() %></p>
                </div>
                <% } %>

                <!-- Current Disease -->
                <% if (medicalRecord.getCurrentDisease() != null && !medicalRecord.getCurrentDisease().trim().isEmpty()) { %>
                <div class="medical-section medical-details">
                    <h6 class="text-primary mb-3"><i class="bi bi-bandaid me-2"></i>Bệnh hiện tại</h6>
                    <p class="mb-0"><%= medicalRecord.getCurrentDisease() %></p>
                </div>
                <% } %>

                <!-- Physical Examination -->
                <% if (medicalRecord.getPhysicalExam() != null && !medicalRecord.getPhysicalExam().trim().isEmpty()) { %>
                <div class="medical-section medical-details">
                    <h6 class="text-primary mb-3"><i class="bi bi-search me-2"></i>Khám thể lực</h6>
                    <p class="mb-0"><%= medicalRecord.getPhysicalExam() %></p>
                </div>
                <% } %>

                <!-- Clinical Information -->
                <% if (medicalRecord.getClinicalInfo() != null && !medicalRecord.getClinicalInfo().trim().isEmpty()) { %>
                <div class="medical-section medical-details">
                    <h6 class="text-primary mb-3"><i class="bi bi-clipboard-data me-2"></i>Thông tin lâm sàng</h6>
                    <p class="mb-0"><%= medicalRecord.getClinicalInfo() %></p>
                </div>
                <% } %>

                <!-- Final Diagnosis -->
                <% if (medicalRecord.getFinalDiagnosis() != null && !medicalRecord.getFinalDiagnosis().trim().isEmpty()) { %>
                <div class="medical-section medical-details">
                    <h6 class="text-primary mb-3"><i class="bi bi-clipboard-check me-2"></i>Chẩn đoán cuối cùng</h6>
                    <p class="mb-0"><%= medicalRecord.getFinalDiagnosis() %></p>
                </div>
                <% } %>

                <!-- Treatment Plan -->
                <% if (medicalRecord.getTreatmentPlan() != null && !medicalRecord.getTreatmentPlan().trim().isEmpty()) { %>
                <div class="medical-section medical-details">
                    <h6 class="text-primary mb-3"><i class="bi bi-prescription2 me-2"></i>Kế hoạch điều trị</h6>
                    <p class="mb-0"><%= medicalRecord.getTreatmentPlan() %></p>
                </div>
                <% } %>

                <!-- Note -->
                <% if (medicalRecord.getNote() != null && !medicalRecord.getNote().trim().isEmpty()) { %>
                <div class="medical-section medical-details">
                    <h6 class="text-primary mb-3"><i class="bi bi-sticky me-2"></i>Ghi chú</h6>
                    <p class="mb-0"><%= medicalRecord.getNote() %></p>
                </div>
                <% } %>

                <!-- Signature Section -->
                <div class="medical-section print-section">
                    <div class="row text-center">
                        <div class="col-md-4">
                            <p class="mb-5"><strong>Bệnh nhân</strong></p>
                            <p>________________________</p>
                            <p class="mb-0"><em>(Ký và ghi rõ họ tên)</em></p>
                        </div>
                        <div class="col-md-4">
                            <p class="mb-5"><strong>Người nhà bệnh nhân</strong></p>
                            <p>________________________</p>
                            <p class="mb-0"><em>(Ký và ghi rõ họ tên)</em></p>
                        </div>
                        <div class="col-md-4">
                            <p class="mb-5"><strong>Bác sĩ điều trị</strong></p>
                            <p>________________________</p>
                            <p class="mb-0"><em>(Ký và ghi rõ họ tên)</em></p>
                        </div>
                    </div>
                    <div class="text-center mt-4">
                        <p class="mb-1"><em>Ngày <%= new java.text.SimpleDateFormat("dd").format(new java.util.Date()) %> tháng <%= new java.text.SimpleDateFormat("MM").format(new java.util.Date()) %> năm <%= new java.text.SimpleDateFormat("yyyy").format(new java.util.Date()) %></em></p>
                        <p class="mb-0"><strong>Hồ sơ này được lập theo quy định của Bộ Y tế</strong></p>
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