<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.Patient" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalRecord" %>
<%@ page import="com.mycompany.isp490_gr3.model.ActualPrescriptionForm" %>
<%@ page import="com.mycompany.isp490_gr3.model.ActualPrescriptionMedicine" %>
<%@ page import="com.mycompany.isp490_gr3.model.PrescriptionMedicine" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đơn thuốc - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Homepage specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <style>
            .medicine-row {
                border: 1px solid #e0e0e0;
                border-radius: 8px;
                padding: 15px;
                margin-bottom: 15px;
                background-color: #f8f9fa;
                position: relative;
            }
            
            .medicine-row:hover {
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            }
            
            .remove-medicine-btn {
                position: absolute;
                top: 10px;
                right: 10px;
                background: #dc3545;
                color: white;
                border: none;
                border-radius: 50%;
                width: 30px;
                height: 30px;
                display: flex;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                font-size: 14px;
            }
            
            .template-medicine-card {
                border: 1px solid #dee2e6;
                border-radius: 8px;
                padding: 10px;
                margin-bottom: 10px;
                cursor: pointer;
                transition: all 0.3s ease;
            }
            
            .template-medicine-card:hover {
                border-color: #007bff;
                background-color: #f8f9fa;
            }
            
            .template-medicine-card.selected {
                border-color: #007bff;
                background-color: #e7f3ff;
            }
            
            .medicine-template-section {
                max-height: 400px;
                overflow-y: auto;
            }
            /* Đảm bảo thông báo lỗi không làm nhảy row */
            .medicine-row .invalid-feedback {
                position: absolute;
                left: 0;
                top: 100%;
                font-size: 0.85em;
                color: #dc3545;
                z-index: 2;
                white-space: nowrap;
                padding-left: 2px;
                background: transparent;
                margin-top: 2px;
            }
            .medicine-row .form-control.is-invalid {
                border-color: #dc3545;
                box-shadow: 0 0 0 0.1rem rgba(220,53,69,.25);
            }
            .medicine-row .col-md-2, .medicine-row .col-md-1, .medicine-row .col-md-3 {
                position: relative;
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
        if (action == null) action = "add";
        Patient patient = (Patient) request.getAttribute("patient");
        MedicalRecord medicalRecord = (MedicalRecord) request.getAttribute("medicalRecord");
        ActualPrescriptionForm form = (ActualPrescriptionForm) request.getAttribute("form");
        List<PrescriptionMedicine> templateMeds = (List<PrescriptionMedicine>) request.getAttribute("templateMeds");
        
        SimpleDateFormat shortSdf = new SimpleDateFormat("dd/MM/yyyy");
        %>

        <!-- Sidebar -->
        <nav id="sidebar">
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
                <!-- Breadcrumb -->
                <div class="row mb-4">
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
                                    <%= "add".equals(action) ? "Tạo đơn thuốc" : "Cập nhật đơn thuốc" %>
                                </li>
                            </ol>
                        </nav>
                    </div>
                </div>

                <!-- Page Header -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="d-flex justify-content-between align-items-center">
                            <h4 class="text-primary mb-0">
                                <i class="bi bi-capsule me-2"></i><%= "add".equals(action) ? "Tạo đơn thuốc mới" : "Cập nhật đơn thuốc" %>
                            </h4>
                        </div>
                    </div>
                </div>

                <!-- Patient Info Card -->
                <% if (patient != null && medicalRecord != null) { %>
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card bg-light">
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <p class="mb-2"><strong>Mã bệnh nhân:</strong> <%= patient.getPatientCode() %></p>
                                        <p class="mb-2"><strong>Họ tên:</strong> <%= patient.getFullName() %></p>
                                        <p class="mb-0"><strong>Ngày sinh:</strong> <%= patient.getDob() != null ? shortSdf.format(patient.getDob()) : "" %></p>
                                    </div>
                                    <div class="col-md-6">
                                        <p class="mb-2"><strong>Mã hồ sơ:</strong> <%= medicalRecord.getId() %></p>
                                        <p class="mb-2"><strong>Ngày tạo hồ sơ:</strong> <%= medicalRecord.getCreatedAt() != null ? shortSdf.format(medicalRecord.getCreatedAt()) : "" %></p>
                                        <p class="mb-0"><strong>Điện thoại:</strong> <%= patient.getPhone() %></p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>

                <!-- Form -->
                <div class="row">
                    <div class="col-lg-8">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="card-title mb-0">
                                    <i class="bi bi-file-earmark-medical me-2"></i>Thông tin đơn thuốc
                                </h5>
                            </div>
                            <div class="card-body">

                                <!-- Form Content -->
                                <form id="prescriptionForm" method="POST" action="${pageContext.request.contextPath}/doctor/actual-prescriptions" 
                                      class="needs-validation" novalidate>
                                    <input type="hidden" name="action" value="<%= "add".equals(action) ? "add" : "update" %>">
                                    <% if (form != null) { %>
                                        <input type="hidden" name="formId" value="<%= form.getActualPrescriptionFormId() %>">
                                    <% } %>
                                    <input type="hidden" name="medicalRecordId" value="<%= medicalRecord != null ? medicalRecord.getId() : "" %>">
                                    <input type="hidden" name="patientId" value="<%= patient != null ? patient.getId() : 0 %>">

                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="mb-3">
                                                <label class="form-label"><i class="bi bi-tag me-2"></i>Tên đơn thuốc <span class="text-danger" >*</span></label>
                                                <input type="text" class="form-control" name="formName" 
                                                       value="<%= form != null ? form.getFormName() : "" %>" 
                                                       placeholder="Nhập tên đơn thuốc" required>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="mb-3">
                                                <label class="form-label"><i class="bi bi-calendar3 me-2"></i>Ngày kê đơn</label>
                                                <input type="text" class="form-control" value="<%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()) %>" readonly>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label"><i class="bi bi-chat-text me-2"></i>Ghi chú</label>
                                        <textarea class="form-control" name="notes" rows="3" 
                                                  placeholder="Nhập ghi chú cho đơn thuốc (tùy chọn)"><%= form != null && form.getNotes() != null ? form.getNotes() : "" %></textarea>
                                    </div>

                                    <!-- Medicines Section -->
                                    <div class="mb-4">
                                        <div class="d-flex justify-content-between align-items-center mb-3">
                                            <h6 class="mb-0"><i class="bi bi-capsule-pill me-2"></i>Danh sách thuốc</h6>
                                            <button type="button" class="btn btn-outline-primary btn-sm" onclick="addMedicineRow()">
                                                <i class="bi bi-plus-lg me-2"></i>Thêm thuốc mới
                                            </button>
                                        </div>
                                        
                                        <div id="medicineContainer">
                                            <% if (form != null && form.getMedicines() != null && !form.getMedicines().isEmpty()) {
                                                int index = 0;
                                                for (ActualPrescriptionMedicine m : form.getMedicines()) { %>
                                                <div class="medicine-row" data-index="<%= index %>">
                                                    <button type="button" class="remove-medicine-btn" onclick="removeMedicineRow(this)">
                                                        <i class="bi bi-x"></i>
                                                    </button>
                                                    <div class="row g-2 align-items-end">
                                                        <div class="col-md-2">
                                                            <label class="form-label">Tên thuốc <span class="text-danger">*</span></label>
                                                            <input type="text" class="form-control" name="medicineName" value="<%= m.getMedicineName() %>" placeholder="Nhập tên thuốc" required>
                                                        </div>
                                                        <div class="col-md-1">
                                                            <label class="form-label">ĐVT</label>
                                                            <input type="text" class="form-control" name="unitOfMeasure" value="<%= m.getUnitOfMeasure() != null ? m.getUnitOfMeasure() : "" %>" placeholder="Viên, ml...">
                                                        </div>
                                                        <div class="col-md-2">
                                                            <label class="form-label">Đường dùng</label>
                                                            <input type="text" class="form-control" name="administrationRoute" value="<%= m.getAdministrationRoute() != null ? m.getAdministrationRoute() : "" %>" placeholder="Uống, tiêm...">
                                                        </div>
                                                        <div class="col-md-1">
                                                            <label class="form-label">Số ngày</label>
                                                            <input type="number" class="form-control" name="daysOfTreatment" value="<%= m.getDaysOfTreatment() != null ? m.getDaysOfTreatment() : "" %>" placeholder="0" min="0">
                                                        </div>
                                                        <div class="col-md-1">
                                                            <label class="form-label">Lần/ngày</label>
                                                            <input type="number" class="form-control" name="unitsPerDay" value="<%= m.getUnitsPerDay() != null ? m.getUnitsPerDay() : "" %>" placeholder="0" min="0">
                                                        </div>
                                                        <div class="col-md-1">
                                                            <label class="form-label">Tổng SL</label>
                                                            <input type="number" class="form-control" name="totalQuantity" value="<%= m.getTotalQuantity() != null ? m.getTotalQuantity() : "" %>" placeholder="0" min="0">
                                                        </div>
                                                        <div class="col-md-3">
                                                            <label class="form-label">HDSD</label>
                                                            <input type="text" class="form-control" name="usageInstructions" value="<%= m.getUsageInstructions() != null ? m.getUsageInstructions() : "" %>" placeholder="Hướng dẫn sử dụng">
                                                        </div>
                                                    </div>
                                                </div>
                                            <% index++; } } else { %>
                                                <!-- Empty row for new prescription -->
                                                <div class="medicine-row" data-index="0">
                                                    <button type="button" class="remove-medicine-btn" onclick="removeMedicineRow(this)">
                                                        <i class="bi bi-x"></i>
                                                    </button>
                                                    <div class="row g-2 align-items-end">
                                                        <div class="col-md-2">
                                                            <label class="form-label">Tên thuốc <span class="text-danger">*</span></label>
                                                            <input type="text" class="form-control" name="medicineName" placeholder="Nhập tên thuốc" required>
                                                        </div>
                                                        <div class="col-md-1">
                                                            <label class="form-label">ĐVT</label>
                                                            <input type="text" class="form-control" name="unitOfMeasure" placeholder="Viên, ml...">
                                                        </div>
                                                        <div class="col-md-2">
                                                            <label class="form-label">Đường dùng</label>
                                                            <input type="text" class="form-control" name="administrationRoute" placeholder="Uống, tiêm...">
                                                        </div>
                                                        <div class="col-md-1">
                                                            <label class="form-label">Số ngày</label>
                                                            <input type="number" class="form-control" name="daysOfTreatment" placeholder="0" min="0">
                                                        </div>
                                                        <div class="col-md-1">
                                                            <label class="form-label">Lần/ngày</label>
                                                            <input type="number" class="form-control" name="unitsPerDay" placeholder="0" min="0">
                                                        </div>
                                                        <div class="col-md-1">
                                                            <label class="form-label">Tổng SL</label>
                                                            <input type="number" class="form-control" name="totalQuantity" placeholder="0" min="0">
                                                        </div>
                                                        <div class="col-md-3">
                                                            <label class="form-label">HDSD</label>
                                                            <input type="text" class="form-control" name="usageInstructions" placeholder="Hướng dẫn sử dụng">
                                                        </div>
                                                    </div>
                                                </div>
                                            <% } %>
                                        </div>
                                    </div>

                                    <!-- Form Buttons -->
                                    <div class="form-buttons mt-4 d-flex justify-content-end">
                                        <% if ("add".equals(action)) { %>
                                            <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%= medicalRecord.getPatientId() %>" 
                                               class="btn btn-secondary me-2">
                                                <i class="bi bi-x-circle me-2"></i>Hủy
                                            </a>
                                        <% } else { %>
                                            <a href="${pageContext.request.contextPath}/doctor/actual-prescriptions?action=view&formId=<%= form.getActualPrescriptionFormId() %>" 
                                               class="btn btn-secondary me-2">
                                                <i class="bi bi-x-circle me-2"></i>Hủy
                                            </a>
                                        <% } %>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="bi bi-check-circle me-2"></i>
                                            <%= "add".equals(action) ? "Tạo đơn thuốc" : "Cập nhật" %>
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <!-- Medicine Templates Sidebar -->
                    <div class="col-lg-4">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="card-title mb-0">
                                    <i class="bi bi-collection me-2"></i>Thuốc mẫu
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="mb-3">
                                    <input type="text" class="form-control" id="templateSearch" 
                                           placeholder="Tìm kiếm thuốc mẫu...">
                                </div>
                                <div class="medicine-template-section" id="templateContainer">
                                    <% if (templateMeds != null) { for (PrescriptionMedicine template : templateMeds) { %>
                                        <div class="template-medicine-card" onclick="addTemplateToForm(this)" 
                                             data-name="<%= template.getMedicineName() %>"
                                             data-unit="<%= template.getUnitOfMeasure() != null ? template.getUnitOfMeasure() : "" %>"
                                             data-route="<%= template.getAdministrationRoute() != null ? template.getAdministrationRoute() : "" %>"
                                             data-days="<%= template.getDaysOfTreatment() != null ? template.getDaysOfTreatment() : "" %>"
                                             data-units-per-day="<%= template.getUnitsPerDay() != null ? template.getUnitsPerDay() : "" %>"
                                             data-total="<%= template.getTotalQuantity() != null ? template.getTotalQuantity() : "" %>"
                                             data-instructions="<%= template.getUsageInstructions() != null ? template.getUsageInstructions() : "" %>">
                                            <div class="d-flex justify-content-between align-items-start">
                                                <div>
                                                    <strong><%= template.getMedicineName() %></strong>
                                                    <% if (template.getUnitOfMeasure() != null) { %>
                                                        <small class="text-muted d-block">ĐVT: <%= template.getUnitOfMeasure() %></small>
                                                    <% } %>
                                                    <% if (template.getAdministrationRoute() != null) { %>
                                                        <small class="text-muted d-block">Đường dùng: <%= template.getAdministrationRoute() %></small>
                                                    <% } %>
                                                </div>
                                                <button type="button" class="btn btn-outline-primary btn-sm">
                                                    <i class="bi bi-plus"></i>
                                                </button>
                                            </div>
                                        </div>
                                    <% } } %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Bootstrap Bundle with Popper -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <!-- jQuery -->
        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
        
        <script>
            let medicineIndex = <%= form != null && form.getMedicines() != null ? form.getMedicines().size() : 1 %>;

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

                checkWidth();
                window.addEventListener('resize', checkWidth);

                // Template search
                $('#templateSearch').on('input', function() {
                    const searchTerm = $(this).val().toLowerCase();
                    $('.template-medicine-card').each(function() {
                        const medicineName = $(this).data('name').toLowerCase();
                        if (medicineName.includes(searchTerm)) {
                            $(this).show();
                        } else {
                            $(this).hide();
                        }
                    });
                });
            });

            function addMedicineRow() {
                const container = document.getElementById('medicineContainer');
                const row = document.createElement('div');
                row.className = 'medicine-row';
                row.setAttribute('data-index', medicineIndex);
                row.innerHTML = `
                    <button type="button" class="remove-medicine-btn" onclick="removeMedicineRow(this)">
                        <i class="bi bi-x"></i>
                    </button>
                    <div class="row g-2 align-items-end">
                        <div class="col-md-2">
                            <label class="form-label">Tên thuốc <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="medicineName" placeholder="Nhập tên thuốc" required>
                        </div>
                        <div class="col-md-1">
                            <label class="form-label">ĐVT</label>
                            <input type="text" class="form-control" name="unitOfMeasure" placeholder="Viên, ml...">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">Đường dùng</label>
                            <input type="text" class="form-control" name="administrationRoute" placeholder="Uống, tiêm...">
                        </div>
                        <div class="col-md-1">
                            <label class="form-label">Số ngày</label>
                            <input type="number" class="form-control" name="daysOfTreatment" placeholder="7" min="0">
                        </div>
                        <div class="col-md-1">
                            <label class="form-label">Lần/ngày</label>
                            <input type="number" class="form-control" name="unitsPerDay" placeholder="3" min="0">
                        </div>
                        <div class="col-md-1">
                            <label class="form-label">Tổng SL</label>
                            <input type="number" class="form-control" name="totalQuantity" placeholder="21" min="0">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">HDSD</label>
                            <input type="text" class="form-control" name="usageInstructions" placeholder="Hướng dẫn...">
                        </div>
                    </div>`;
                container.appendChild(row);
                medicineIndex++;
            }

            function removeMedicineRow(button) {
                const container = document.getElementById('medicineContainer');
                if (container.children.length > 1) {
                    button.closest('.medicine-row').remove();
                } else {
                    alert('Phải có ít nhất một thuốc trong đơn!');
                }
            }

            function addTemplateToForm(card) {
                // Get all data attributes from the template card
                const data = {
                    name: card.getAttribute('data-name'),
                    unit: card.getAttribute('data-unit'),
                    route: card.getAttribute('data-route'),
                    days: card.getAttribute('data-days'),
                    unitsPerDay: card.getAttribute('data-units-per-day'),
                    total: card.getAttribute('data-total'),
                    instructions: card.getAttribute('data-instructions')
                };

                // Add empty medicine row first
                addMedicineRow();
                
                // Get the last added row
                const lastRow = document.querySelector('.medicine-row:last-child');
                
                // Set values using direct DOM manipulation
                lastRow.querySelector('input[name="medicineName"]').value = data.name || '';
                lastRow.querySelector('input[name="unitOfMeasure"]').value = data.unit || '';
                lastRow.querySelector('input[name="administrationRoute"]').value = data.route || '';
                lastRow.querySelector('input[name="daysOfTreatment"]').value = data.days || '';
                lastRow.querySelector('input[name="unitsPerDay"]').value = data.unitsPerDay || '';
                lastRow.querySelector('input[name="totalQuantity"]').value = data.total || '';
                lastRow.querySelector('textarea[name="usageInstructions"]').value = data.instructions || '';

                // Calculate total quantity if not provided
                if (!data.total && data.days && data.unitsPerDay) {
                    const total = parseInt(data.days) * parseInt(data.unitsPerDay);
                    lastRow.querySelector('input[name="totalQuantity"]').value = total;
                }
            }

            document.addEventListener('input', function(e) {
                if (e.target.matches('input[name="daysOfTreatment"], input[name="unitsPerDay"], input[name="totalQuantity"]')) {
                    if (e.target.value && parseInt(e.target.value) < 0) {
                        e.target.value = 0;
                    }
                }
            });

            // Thêm kiểm tra required phía client cho tên đơn thuốc và ít nhất một tên thuốc
            $(function() {
                $('#prescriptionForm').on('submit', function(e) {
                    let valid = true;
                    // Xóa thông báo lỗi cũ
                    $('.invalid-feedback').remove();
                    $('.is-invalid').removeClass('is-invalid');

                    // Kiểm tra tên đơn thuốc
                    const formNameInput = $(this).find('input[name="formName"]');
                    const formName = formNameInput.val().trim();
                    if (!formName) {
                        if (formNameInput.next('.invalid-feedback').length === 0) {
                            formNameInput.addClass('is-invalid').after('<div class="invalid-feedback">Vui lòng nhập tên đơn thuốc!</div>');
                        }
                        formNameInput.focus();
                        valid = false;
                    }

                    // Kiểm tra ít nhất một tên thuốc
                    let hasMedicine = false;
                    let firstEmptyMedicine = null;
                    let medicineNames = [];
                    let duplicateFound = false;
                    $(this).find('input[name="medicineName"]').each(function() {
                        const val = $(this).val().trim();
                        if (val) {
                            hasMedicine = true;
                            if (medicineNames.includes(val.toLowerCase())) {
                                // Trùng tên thuốc
                                if ($(this).next('.invalid-feedback').length === 0) {
                                    $(this).addClass('is-invalid').after('<div class="invalid-feedback">Tên thuốc không được trùng nhau!</div>');
                                }
                                $(this).focus();
                                valid = false;
                                duplicateFound = true;
                            } else {
                                medicineNames.push(val.toLowerCase());
                            }
                        } else if (!firstEmptyMedicine) {
                            firstEmptyMedicine = $(this);
                        }
                    });
                    if (!hasMedicine && firstEmptyMedicine) {
                        if (firstEmptyMedicine.next('.invalid-feedback').length === 0) {
                            firstEmptyMedicine.addClass('is-invalid').after('<div class="invalid-feedback">Vui lòng nhập tên thuốc!</div>');
                            firstEmptyMedicine.focus();
                        }
                        valid = false;
                    }
                    if (!valid) {
                        e.preventDefault();
                        return false;
                    }
                });

                // Xóa thông báo khi người dùng nhập lại
                $(document).on('input', 'input[name="formName"], input[name="medicineName"]', function() {
                    if ($(this).val().trim()) {
                        $(this).removeClass('is-invalid');
                        $(this).next('.invalid-feedback').remove();
                    }
                });
            });
        </script>
    </body>
</html> 