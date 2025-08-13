<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.Patient" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalRecord" %>
<%@ page import="com.mycompany.isp490_gr3.model.Invoice" %>
<%@ page import="com.mycompany.isp490_gr3.model.InvoiceItem" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalService" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalSupply" %>
<%@ page import="com.mycompany.isp490_gr3.model.Medicine" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.math.BigDecimal" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><%= "update".equals(request.getAttribute("action")) ? "Chỉnh sửa" : "Tạo" %> hóa đơn - Ánh Dương Clinic</title>
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
                position: relative;
            }
            .vital-signs-grid {
                display: flex;
                flex-wrap: wrap;
                gap: 1rem;
                width: 100%;
            }
            .vital-sign-item {
                flex: 1 1 calc(25% - 1rem);
                min-width: 150px;
                display: flex;
                flex-direction: column;
            }
            .vital-sign-item .form-label {
                font-weight: 500;
                color: #333;
                margin-bottom: 0.25rem;
                font-size: 1rem;
            }
            .vital-sign-item .form-text {
                font-size: 0.75rem;
                color: #6c757d;
                margin-top: 0.25rem;
            }
            .action-btn {
                display: inline-flex;
                align-items: center;
                gap: 4px;
                padding: 0.4rem 1rem;
                border: none;
                border-radius: 6px;
                text-decoration: none;
                font-size: 14px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.3s ease;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                min-width: 100px;
                justify-content: center;
            }
            .action-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0,0,0,0.15);
                text-decoration: none;
            }
            .action-btn:disabled {
                background: #cccccc;
                cursor: not-allowed;
                transform: none;
                box-shadow: none;
            }
            .action-btn i {
                font-size: 16px;
            }
            .btn-text {
                font-size: 15px;
                white-space: nowrap;
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
            .action-btn-create {
                background: linear-gradient(135deg, #28a745, #218838);
                color: white;
            }
            .action-btn-create:hover {
                background: linear-gradient(135deg, #218838, #1e7e34);
                color: white;
            }
            @media (max-width: 768px) {
                .vital-sign-item {
                    flex: 1 1 calc(50% - 1rem);
                }
                .action-btn {
                    padding: 0.5rem 1rem;
                    min-width: 90px;
                }
                .btn-text {
                    display: none;
                }
                .action-btn i {
                    font-size: 16px;
                }
                .back-btn {
                    padding: 0.5rem 1rem;
                    min-width: 90px;
                    font-size: 11px;
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
                box-shadow: 0 4px 12px rgba(0,0,0,0.06);
                margin-bottom: 1rem;
            }
            .medical-card .card-header {
                background: none;
                border-bottom: none;
                padding: 12px;
            }
            .medical-card .card-header h5 {
                font-weight: 600;
                font-size: 1rem;
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
                margin-bottom: 10px;
                padding: 5px 0;
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
                border-radius: 0.4rem;
                transition: border-color 0.3s ease, box-shadow 0.3s ease;
                width: 100%;
                font-size: 1rem;
            }
            .form-control:focus, .form-select:focus, textarea:focus {
                border-color: #007bff;
                box-shadow: 0 0 6px rgba(0,123,255,0.2);
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
            .form-control:disabled, .form-select:disabled, textarea:disabled {
                background-color: #e9ecef;
                cursor: not-allowed;
            }
            .form-control[readonly] {
                background-color: #f8f9fa;
                border-color: #dee2e6;
                cursor: not-allowed;
            }
            .action-btn[title]:hover::after {
                content: attr(title);
                position: absolute;
                background: rgba(0,0,0,0.8);
                color: white;
                padding: 3px 6px;
                border-radius: 3px;
                font-size: 15px;
                z-index: 1000;
                margin-top: -30px;
                margin-left: -15px;
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
            .receipt-section {
                background: #f8f9fa;
                border: 1px solid #e9ecef;
                border-radius: 0.5rem;
                padding: 1rem;
                transition: all 0.3s ease;
            }
            .receipt-section.disabled {
                background-color: #e9ecef;
                cursor: not-allowed;
                opacity: 0.6;
            }
            .item-headers {
                background: #ffffff;
                border: 1px solid #dee2e6;
                border-radius: 0.4rem;
                padding: 0.75rem;
                margin-bottom: 0.75rem;
                font-weight: 600;
                color: #495057;
                font-size: 1rem;
            }
            .item-row {
                background: white;
                border: 1px solid #e9ecef;
                border-radius: 0.4rem;
                padding: 0.75rem;
                margin-bottom: 0.5rem;
                transition: all 0.3s ease;
            }
            .btn-add-item {
                background: linear-gradient(135deg, #007bff, #0056b3);
                border: none;
                color: white;
                padding: 0.5rem 1rem;
                border-radius: 0.4rem;
                font-weight: 500;
                transition: all 0.3s ease;
                display: inline-flex;
                align-items: center;
                gap: 0.4rem;
                font-size: 1rem;
            }
            .btn-delete-item {
                background: #dc3545;
                border: none;
                color: white;
                padding: 0.4rem;
                border-radius: 0.2rem;
                width: 30px;
                height: 30px;
                display: flex;
                align-items: center;
                justify-content: center;
                transition: all 0.3s ease;
            }
            .total-section {
                background: linear-gradient(135deg, #e8f5e8, #c8e6c9);
                border: 1px solid #4caf50;
                border-radius: 0.5rem;
                padding: 1rem;
                position: sticky;
                bottom: 20px;
                z-index: 1000;
                width: 100%;
                margin-left: auto;
                margin-right: auto;
            }
            .receipt-totals {
                background: linear-gradient(135deg, #d6eaff, #bfdeff);
                border: 1px solid #e6f1ff;
                border-radius: 0.75rem;
                padding: 1.5rem;
                margin-top: 1.5rem;
            }
            .receipt-totals h6 {
                margin-bottom: 1rem;
                font-weight: 600;
                color: #495057;
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
                background: #0d6efd;
                display: inline-flex;
                justify-content: center;
                align-items: center;
                line-height: 1;
            }
            .receipt-toggle {
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }
            .receipt-toggle input[type="checkbox"] {
                width: 1.2rem;
                height: 1.2rem;
                cursor: pointer;
            }
            .receipt-toggle label {
                font-weight: 500;
                color: #6c757d;
                cursor: pointer;
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
            String action = (String) request.getAttribute("action");
            boolean isEdit = "update".equals(action);
            Patient patient = (Patient) request.getAttribute("patient");
            MedicalRecord medicalRecord = (MedicalRecord) request.getAttribute("medicalRecord");
            Invoice invoice = (Invoice) request.getAttribute("invoice");
            List<MedicalService> services = (List<MedicalService>) request.getAttribute("services");
            List<MedicalSupply> supplies = (List<MedicalSupply>) request.getAttribute("supplies");
            List<Medicine> medicines = (List<Medicine>) request.getAttribute("medicines");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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
                            <%= isEdit ? "Chỉnh sửa" : "Tạo mới" %> hóa đơn
                        </li>
                    </ol>
                </nav>
                <% if (patient == null) { %>
                <div class="alert alert-danger">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    Không tìm thấy thông tin bệnh nhân! Vui lòng quay lại danh sách bệnh nhân.
                </div>
                <% } else { %>
                <%
                    String success = request.getParameter("success");
                    String error = request.getParameter("error");
                    String message = request.getParameter("message");
                %>
                <% if (success != null) { %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle me-2"></i>
                    <% if ("added".equals(success)) { %>Tạo hóa đơn thành công!<% } else if ("updated".equals(success)) { %>Cập nhật hóa đơn thành công!<% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>
                <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <% if ("add_failed".equals(error)) { %>Tạo hóa đơn thất bại! Vui lòng thử lại.<% }
                else if ("update_failed".equals(error)) { %>Cập nhật hóa đơn thất bại! Vui lòng thử lại.<% }
                else if ("insufficient_stock".equals(error)) { %>Không đủ tồn kho!<br><%= message != null ? message : "Vui lòng kiểm tra lại số lượng vật tư/thuốc." %><% }
                else if ("database_update_needed".equals(error)) { %>Cần cập nhật hệ thống!<br><%= message != null ? message : "Hệ thống cần được cập nhật. Vui lòng liên hệ quản trị viên." %><% }
                else if ("missing_data".equals(error)) { %>Thiếu thông tin! Vui lòng điền đầy đủ thông tin bắt buộc.<% }
                else if ("invalid_data".equals(error)) { %>Dữ liệu không hợp lệ! Vui lòng kiểm tra lại thông tin nhập.<% }
                else if ("system_error".equals(error)) { %>Lỗi hệ thống! Vui lòng thử lại sau hoặc liên hệ quản trị viên.<% }
                else { %>Có lỗi xảy ra. Vui lòng thử lại!<% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>
                <div class="medical-form-container">
                    <form id="invoiceForm" method="POST" action="${pageContext.request.contextPath}/doctor/invoices">
                        <input type="hidden" name="action" value="<%= isEdit ? "update" : "add" %>">
                        <input type="hidden" name="medicalRecordId" value="<%= medicalRecord != null ? medicalRecord.getId() : "" %>">
                        <input type="hidden" name="patientId" value="<%= patient != null ? patient.getId() : "" %>">
                        <% if (isEdit && invoice != null) { %>
                        <input type="hidden" name="invoiceId" value="<%= invoice.getInvoiceId() %>">
                        <% } %>
                        <input type="hidden" name="enableSecondReceipt" id="enableSecondReceipt" value="false">
                        <div class="row mb-4">
                            <div class="col-md-3 patient-info-card">
                                <div class="card medical-card">
                                    <div class="card-header">
                                        <h4 class="text-primary mb-3">
                                            <i class="bi bi-file-medical me-2"></i>Tạo hóa đơn
                                        </h4>
                                    </div>
                                    <div class="card-body">
                                        <p><strong>Mã bệnh nhân:</strong> <%= patient.getPatientCode() %></p>
                                        <p><strong>Họ tên:</strong> <%= patient.getFullName() %></p>
                                        <p><strong>Ngày sinh:</strong> <%= patient.getDob() != null ? shortSdf.format(patient.getDob()) : "" %></p>
                                        <p><strong>Mã hồ sơ:</strong> <%= medicalRecord.getId() %></p>
                                        <p><strong>Ngày tạo hồ sơ:</strong> <%= medicalRecord.getCreatedAt() != null ? shortSdf.format(medicalRecord.getCreatedAt()) : "" %></p>
                                        <% if (isEdit && invoice != null) { %>
                                        <p><strong>Mã hóa đơn:</strong> <%= invoice.getInvoiceId() %></p>
                                        <% } %>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-9 medical-content-area">
                                <div class="card medical-card mb-4">
                                    <div class="card-header d-flex justify-content-between align-items-center">
                                        <h4 class="text-primary mb-0">
                                            <i class="bi bi-receipt me-2"></i>Phiếu thu
                                        </h4>
                                    </div>
                                    <div class="card-body">
                                        <div id="receiptsContainer">
                                            <div class="receipt-section" id="receipt1">
                                                <div class="receipt-header">
                                                    <div class="receipt-title">
                                                        <i class="bi bi-receipt"></i>Phiếu thu 1
                                                    </div>
                                                </div>
                                                <div class="item-headers">
                                                    <div class="row">
                                                        <div class="col-md-3">Tên</div>
                                                        <div class="col-md-2">Loại</div>
                                                        <div class="col-md-2">Số lượng</div>
                                                        <div class="col-md-2">Đơn giá</div>
                                                        <div class="col-md-2">Thành tiền</div>
                                                        <div class="col-md-1">Thao tác</div>
                                                    </div>
                                                </div>
                                                <div id="receipt1_servicesContainer"></div>
                                                <div id="receipt1_suppliesContainer"></div>
                                                <div id="receipt1_medicinesContainer"></div>
                                                <div class="mt-2" id="receipt1_buttons">
                                                                                                                        <button type="button" class="action-btn action-btn-create" onclick="addNewItemRow('receipt1', 'service')" title="Thêm dịch vụ">
                                                                        <i class="bi bi-plus-circle"></i><span class="btn-text">Dịch vụ</span>
                                                                    </button>
                                                                    <button type="button" class="action-btn action-btn-create" onclick="addNewItemRow('receipt1', 'supply')" title="Thêm vật tư">
                                                                        <i class="bi bi-plus-circle"></i><span class="btn-text">Vật tư</span>
                                                                    </button>
                                                                    <button type="button" class="action-btn action-btn-create" onclick="addNewItemRow('receipt1', 'medicine')" title="Thêm thuốc">
                                                                        <i class="bi bi-plus-circle"></i><span class="btn-text">Thuốc</span>
                                                                    </button>
                                                </div>
                                                <div class="receipt-totals" fw-bold">
                                                    <h6>Tổng phiếu thu</h6>
                                                    <div class="row">
                                                        <div class="col-md-4">
                                                            <label class="form-label fw-bold">Dịch vụ</label>
                                                            <input type="text" class="form-control" id="receipt1_totalServices" readonly>
                                                        </div>
                                                        <div class="col-md-4">
                                                            <label class="form-label fw-bold">Vật tư & Thuốc</label>
                                                            <input type="text" class="form-control" id="receipt1_totalSupplies" readonly>
                                                        </div>
                                                        <div class="col-md-4">
                                                            <label class="form-label fw-bold">Tổng phiếu thu</label>
                                                            <input type="text" class="form-control fw-bold" id="receipt1_total" readonly>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="receipt-section mt-3" id="receipt2">
                                                <div class="receipt-header">
                                                    <div class="receipt-title">
                                                        <i class="bi bi-receipt"></i>Phiếu thu 2
                                                    </div>
                                                    <div class="receipt-toggle">
                                                        <input type="checkbox" id="enableSecondReceiptCheckbox" name="enableSecondReceiptCheckbox" value="true" onchange="toggleSecondReceipt()">
                                                        <label for="enableSecondReceiptCheckbox">Sử dụng phiếu thu 2</label>
                                                    </div>
                                                </div>
                                                <div id="receipt2_sections">
                                                    <div class="item-headers">
                                                        <div class="row">
                                                            <div class="col-md-3">Tên</div>
                                                            <div class="col-md-2">Loại</div>
                                                            <div class="col-md-2">Số lượng</div>
                                                            <div class="col-md-2">Đơn giá</div>
                                                            <div class="col-md-2">Thành tiền</div>
                                                            <div class="col-md-1">Thao tác</div>
                                                        </div>
                                                    </div>
                                                    <div id="receipt2_servicesContainer"></div>
                                                    <div id="receipt2_suppliesContainer"></div>
                                                    <div id="receipt2_medicinesContainer"></div>
                                                    <div class="mt-2" id="receipt2_buttons">
                                                                                                                                <button type="button" class="action-btn action-btn-create" onclick="addNewItemRow('receipt2', 'service')" title="Thêm dịch vụ">
                                                                            <i class="bi bi-plus-circle"></i><span class="btn-text">Dịch vụ</span>
                                                                        </button>
                                                                        <button type="button" class="action-btn action-btn-create" onclick="addNewItemRow('receipt2', 'supply')" title="Thêm vật tư">
                                                                            <i class="bi bi-plus-circle"></i><span class="btn-text">Vật tư</span>
                                                                        </button>
                                                                        <button type="button" class="action-btn action-btn-create" onclick="addNewItemRow('receipt2', 'medicine')" title="Thêm thuốc">
                                                                            <i class="bi bi-plus-circle"></i><span class="btn-text">Thuốc</span>
                                                                        </button>
                                                    </div>
                                                    <div class="receipt-totals" fw-bold">
                                                        <h6>Tổng phiếu thu</h6>
                                                        <div class="row">
                                                            <div class="col-md-4">
                                                                <label class="form-label fw-bold">Dịch vụ</label>
                                                                <input type="text" class="form-control" id="receipt2_totalServices" readonly>
                                                            </div>
                                                            <div class="col-md-4">
                                                                <label class="form-label fw-bold">Vật tư & Thuốc</label>
                                                                <input type="text" class="form-control" id="receipt2_totalSupplies" readonly>
                                                            </div>
                                                            <div class="col-md-4">
                                                                <label class="form-label fw-bold">Tổng phiếu thu</label>
                                                                <input type="text" class="form-control fw-bold" id="receipt2_total" readonly>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="card-header">
                                        <h4 class="text-primary mb-3">
                                            <i class="bi bi-calculator me-2"></i>Tổng cộng hóa đơn
                                        </h4>
                                    </div>
                                    <div class="card-body">
                                        <div class="total-section">
                                            <div class="row">
                                                <div class="col-md-3">
                                                    <label for="totalServices" class="form-label fw-bold">Tổng dịch vụ</label>
                                                    <input type="text" class="form-control" id="totalServices" readonly>
                                                </div>
                                                <div class="col-md-3">
                                                    <label for="totalSupplies" class="form-label fw-bold">Tổng vật tư & thuốc</label>
                                                    <input type="text" class="form-control" id="totalSupplies" readonly>
                                                </div>
                                                <div class="col-md-3">
                                                    <label for="discountAmount" class="form-label fw-bold">Giảm giá</label>
                                                    <input type="number" class="form-control" id="discountAmount" name="discountAmount"
                                                           value="<%= isEdit && invoice != null && invoice.getDiscountAmount() != null ? invoice.getDiscountAmount().toString() : "0" %>"
                                                           min="0" onchange="calculateTotal()">
                                                </div>
                                                <div class="col-md-3">
                                                    <label for="finalAmount" class="form-label fw-bold">Thành tiền</label>
                                                    <input type="text" class="form-control fw-bold text-primary" id="finalAmount" readonly>
                                                </div>
                                            </div>
                                            <div class="row mt-2">
                                                <div class="col-md-12">
                                                    <label for="notes" class="form-label fw-bold">Ghi chú</label>
                                                    <textarea class="form-control" id="notes" name="notes" rows="2" placeholder="Ghi chú thêm về hóa đơn..."><%= isEdit && invoice != null && invoice.getNotes() != null ? invoice.getNotes() : "" %></textarea>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="d-flex justify-content-between align-items-center mt-3">
                            <div>
                                <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%= patient.getId() %>" class="action-btn back-btn" title="Hủy">
                                    <i class="bi bi-arrow-left"></i>
                                    <span class="btn-text">Quay lại</span>
                                </a>
                            </div>
                            <div>
                                <button type="submit" class="action-btn action-btn-create" title="Tạo hóa đơn">
                                    <i class="bi bi-check-circle"></i>
                                    <span class="btn-text">Tạo hóa đơn</span>
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
                <% } %>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
        <!-- Select2 CSS & JS -->
        <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
        <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
        <script>
            const servicesData = [
            <% if (services != null) {
                    for (int i = 0; i < services.size(); i++) {
                        MedicalService service = services.get(i); %>
                {
                    id: <%= service.getServicesId() %>,
                    name: '<%= service.getServiceName().replace("'", "\\'") %>',
                    price: <%= service.getPrice() %>
                }<%= i < services.size() - 1 ? "," : "" %>
            <% }
                } %>
            ];
            const suppliesData = [
            <% if (supplies != null) {
                    for (int i = 0; i < supplies.size(); i++) {
                        MedicalSupply supply = supplies.get(i); %>
                {
                    id: <%= supply.getSupplyId() %>,
                    name: '<%= supply.getSupplyName().replace("'", "\\'") %>',
                    price: <%= supply.getUnitPrice() %>,
                    stock: <%= supply.getStockQuantity() %>
                }<%= i < supplies.size() - 1 ? "," : "" %>
            <% }
                } %>
            ];
            const medicinesData = [
            <% if (medicines != null) {
                    for (int i = 0; i < medicines.size(); i++) {
                        Medicine medicine = medicines.get(i); %>
                {
                    id: <%= medicine.getExamMedicineId() %>,
                    name: '<%= medicine.getMedicineName().replace("'", "\\'") %>',
                    price: <%= medicine.getUnitPrice() %>,
                    stock: <%= medicine.getStockQuantity() %>
                }<%= i < medicines.size() - 1 ? "," : "" %>
            <% }
                } %>
            ];
            const existingFirstReceiptItems = [];
            const existingSecondReceiptItems = [];
            <% if (isEdit && invoice != null && invoice.getInvoiceItems() != null) {
                for (InvoiceItem item : invoice.getInvoiceItems()) {
                    if (item.getReceiptNumber() == 1) { %>
                existingFirstReceiptItems.push({
                    type: '<%= item.getItemType() %>',
                    id: <%= item.getItemId() %>,
                    name: '<%= item.getItemName().replace("'", "\\'") %>',
                    quantity: <%= item.getQuantity() %>,
                    price: <%= item.getUnitPrice() %>
                });
            <% } else if (item.getReceiptNumber() == 2) { %>
                existingSecondReceiptItems.push({
                    type: '<%= item.getItemType() %>',
                    id: <%= item.getItemId() %>,
                    name: '<%= item.getItemName().replace("'", "\\'") %>',
                    quantity: <%= item.getQuantity() %>,
                    price: <%= item.getUnitPrice() %>
                });
            <% }
                }
            } %>
            const hasSecondReceipt = existingSecondReceiptItems.length > 0;
            let itemCounters = {
                receipt1: { service: 0, supply: 0, medicine: 0 },
                receipt2: { service: 0, supply: 0, medicine: 0 }
            };
            document.addEventListener('DOMContentLoaded', function() {
                initializeSidebar();
                if (hasSecondReceipt) {
                    document.getElementById('enableSecondReceiptCheckbox').checked = true;
                    toggleSecondReceipt();
                } else {
                    toggleSecondReceipt();
                }
                loadExistingItems();
                calculateTotal();
            });
            function initializeSidebar() {
                const sidebarCollapse = document.getElementById('sidebarCollapse');
                const sidebar = document.getElementById('sidebar');
                const content = document.getElementById('content');
                sidebarCollapse.addEventListener('click', function() {
                    sidebar.classList.toggle('active');
                    content.classList.toggle('active');
                });
                function checkWidth() {
                    if (window.innerWidth <= 768) {
                        sidebar.classList.add('active');
                        content.classList.add('active');
                    } else {
                        sidebar.classList.remove('active');
                        content.classList.remove('active');
                    }
                }
                checkWidth();
                window.addEventListener('resize', checkWidth);
            }

            function toggleSecondReceipt() {
                const checkbox = document.getElementById('enableSecondReceiptCheckbox');
                const receipt2 = document.getElementById('receipt2');
                const receipt2Sections = document.getElementById('receipt2_sections');
                const receipt1 = document.getElementById('receipt1');
                const receipt1Buttons = document.getElementById('receipt1_buttons');
                if (checkbox.checked) {
                    document.getElementById('enableSecondReceipt').value = 'true';
                    receipt2.classList.remove('disabled');
                    receipt2Sections.style.display = 'block';
                    receipt1.classList.add('disabled');
                    receipt1Buttons.style.display = 'none';
                } else {
                    document.getElementById('enableSecondReceipt').value = 'false';
                    receipt2.classList.add('disabled');
                    receipt2Sections.style.display = 'none';
                    receipt1.classList.remove('disabled');
                    receipt1Buttons.style.display = 'block';
                    clearReceiptItems('receipt2');
                }
                calculateTotal();
            }

            function clearReceiptItems(receiptId) {
                ['service', 'supply', 'medicine'].forEach(itemType => {
                    const container = document.getElementById(receiptId + '_' + getContainerSuffix(itemType) + 'Container');
                    if (container) {
                        container.innerHTML = '';
                        itemCounters[receiptId][itemType] = 0;
                    }
                });
                calculateTotal();
            }

            function addItemRow(receiptId, itemType, itemId = '', quantity = 1) {
                const container = document.getElementById(receiptId + '_' + getContainerSuffix(itemType) + 'Container');
                const counter = itemCounters[receiptId][itemType]++;
                const rowId = receiptId + '_' + itemType + '_' + counter;
                const row = document.createElement('div');
                row.className = 'item-row';
                row.id = rowId;
                let itemOptions = '';
                let itemsData = [];
                if (itemType === 'service') itemsData = servicesData;
                else if (itemType === 'supply') itemsData = suppliesData;
                else if (itemType === 'medicine') itemsData = medicinesData;
                
                // Filter out already selected items to prevent duplicates for all item types
                const selectedItems = getSelectedItems(receiptId, itemType);
                itemsData = itemsData.filter(item => !selectedItems.includes(item.id.toString()) || item.id.toString() === itemId.toString());
                                                               
                                                               for (let i = 0; i < itemsData.length; i++) {
                                                               const item = itemsData[i];
                                                               const selected = (itemId == item.id) ? 'selected' : '';
                                                               const stockInfo = item.stock ? (item.stock > 0 ? ' (Còn: ' + item.stock + ')' : ' (Hết hàng)') : '';
                                                               const stockClass = item.stock > 0 ? '' : ' class="text-danger"';
                                                               itemOptions += '<option value="' + item.id + '" ' + selected +
                                                                       ' data-price="' + item.price + '"' +
                                                                       (item.stock !== undefined ? ' data-stock="' + item.stock + '"' : '') +
                                                                       stockClass + '>' + item.name + stockInfo + '</option>';
                                                               }

                                                               row.innerHTML =
                                                                       '<div class="row align-items-center">' +
                                                                       '<div class="col-md-3">' +
                                                                       '<select class="form-select item-select" id="' + rowId + '_select" name="' + receiptId + '_' + itemType + 'Id" ' +
                                                                       'onchange="updateItemPrice(\'' + rowId + '\', \'' + itemType + '\', this)" required>' +
                                                                       '<option value="">-- Chọn --</option>' +
                                                                       itemOptions +
                                                                       '</select>' +
                                                                       '<input type="hidden" name="' + receiptId + '_' + itemType + 'Name_' + itemId + '" id="' + rowId + '_name">' +
                                                                       '<input type="hidden" name="' + receiptId + '_' + itemType + 'Price_' + itemId + '" id="' + rowId + '_price">' +
                                                                       '</div>' +
                                                                       '<div class="col-md-2 text-center">' +
                                                                       '<input type="text" class="form-control" value="' + getItemTypeLabel(itemType) + '" readonly>' +
                                                                       '</div>' +
                                                                       '<div class="col-md-2">' +
                                                                       '<input type="number" class="form-control" name="' + receiptId + '_' + itemType + 'Quantity" ' +
                                                                       'value="' + (itemType === 'service' ? '1' : quantity) + '" min="1" ' +
                                                                       (itemType === 'service' ? 'readonly ' : '') +
                                                                       'onchange="calculateRowTotal(\'' + rowId + '\', \'' + itemType + '\')" required>' +
                                                                       '<small class="text-info" id="' + rowId + '_stock"></small>' +
                                                                       '</div>' +
                                                                       '<div class="col-md-2">' +
                                                                       '<input type="text" class="form-control" id="' + rowId + '_unitPrice" readonly>' +
                                                                       '</div>' +
                                                                       '<div class="col-md-2">' +
                                                                       '<input type="text" class="form-control" id="' + rowId + '_total" readonly>' +
                                                                       '</div>' +
                                                                       '<div class="col-md-1 text-center">' +
                                                                       '<button type="button" class="btn btn-danger btn-delete-item" onclick="removeRow(\'' + rowId + '\')"><i class="bi bi-trash"></i></button>' +
                                                                       '</div>' +
                                                                       '</div>';
                                                               container.appendChild(row);
                                                               updateItemPrice(rowId, itemType);
                                                               $(row).find('.item-select').select2({
                                                                       width: '100%',
                                                                       dropdownParent: $(row).find('.col-md-3')
                                                               });
                                                               }

                                                               function updateItemPrice(rowId, itemType, selectElement = null) {
                                                               const select = selectElement || document.querySelector('#' + rowId + ' select');
                                                               const selectedOption = select.options[select.selectedIndex];
                                                               if (selectedOption.value) {
                                                               // Check if this item is already selected in another row
                                                               const receiptId = rowId.split('_')[0];
                                                               const existingRow = findExistingItemRow(receiptId, itemType, selectedOption.value);
                                                               if (existingRow && existingRow.id !== rowId) {
                                                               // Reset the select to empty
                                                               select.value = '';
                                                               // Focus on the existing row
                                                               const quantityInput = existingRow.querySelector('input[name$="Quantity"]');
                                                               if (quantityInput) {
                                                               quantityInput.focus();
                                                               quantityInput.select();
                                                               existingRow.style.backgroundColor = '#fff3cd';
                                                               setTimeout(() => {
                                                               existingRow.style.backgroundColor = '';
                                                               }, 2000);
                                                               }
                                                               alert('Mục này đã được thêm vào phiếu thu. Vui lòng thay đổi số lượng ở dòng hiện có thay vì chọn lại.');
                                                               return;
                                                               }
                                                               const price = parseFloat(selectedOption.dataset.price);
                                                               const name = selectedOption.text.split(' (')[0];
                                                               const itemId = selectedOption.value;
                                                               document.getElementById(rowId + '_unitPrice').value = formatCurrency(price);
                                                               document.getElementById(rowId + '_name').value = name;
                                                               document.getElementById(rowId + '_name').name = rowId.split('_')[0] + '_' + itemType + 'Name_' + itemId;
                                                               document.getElementById(rowId + '_price').value = price;
                                                               document.getElementById(rowId + '_price').name = rowId.split('_')[0] + '_' + itemType + 'Price_' + itemId;
                                                               if (itemType === 'supply' || itemType === 'medicine') {
                                                               const stock = parseInt(selectedOption.dataset.stock);
                                                               const stockDisplay = document.getElementById(rowId + '_stock');
                                                               if (stockDisplay) {
                                                               if (stock > 0) {
                                                               stockDisplay.textContent = 'Tồn kho: ' + stock;
                                                               stockDisplay.className = 'text-info';
                                                               } else {
                                                               stockDisplay.textContent = 'Hết hàng';
                                                               stockDisplay.className = 'text-danger';
                                                               }
                                                               }
                                                               }
                                                               calculateRowTotal(rowId, itemType);
                                                               
                                                               // Refresh dropdowns to prevent duplicates for all item types
                                                               refreshItemDropdowns(rowId, itemType);
                                                               } else {
                                                               document.getElementById(rowId + '_unitPrice').value = '';
                                                               document.getElementById(rowId + '_total').value = '';
                                                               const stockDisplay = document.getElementById(rowId + '_stock');
                                                               if (stockDisplay) stockDisplay.textContent = '';
                                                               calculateTotal();
                                                               
                                                               // Refresh dropdowns for all item types
                                                               refreshItemDropdowns(rowId, itemType);
                                                               }
                                                               }

                                                               function calculateRowTotal(rowId, itemType) {
                                                               const row = document.getElementById(rowId);
                                                               const quantityInput = row.querySelector('input[name$="Quantity"]');
                                                               const unitPriceInput = document.getElementById(rowId + '_unitPrice');
                                                               const totalInput = document.getElementById(rowId + '_total');
                                                               if (quantityInput && unitPriceInput && totalInput) {
                                                               const quantity = parseInt(quantityInput.value) || 0;
                                                               const unitPrice = parseFloat(unitPriceInput.value.replace(/[,.đ\s]/g, '')) || 0;
                                                               const total = quantity * unitPrice;
                                                               totalInput.value = formatCurrency(total);
                                                               if (itemType === 'supply' || itemType === 'medicine') validateStock(rowId, itemType);
                                                               }
                                                               calculateTotal();
                                                               }

                                                               function validateStock(rowId, itemType) {
                                                               const row = document.getElementById(rowId);
                                                               const select = row.querySelector('select');
                                                               const quantityInput = row.querySelector('input[name$="Quantity"]');
                                                               const stockDisplay = document.getElementById(rowId + '_stock');
                                                               if (select && quantityInput && stockDisplay) {
                                                               const selectedOption = select.options[select.selectedIndex];
                                                               if (selectedOption.value) {
                                                               const stock = parseInt(selectedOption.dataset.stock);
                                                               const requestedQuantity = parseInt(quantityInput.value) || 0;
                                                               if (requestedQuantity > stock) {
                                                               stockDisplay.textContent = 'Tồn kho: ' + stock + ', Yêu cầu: ' + requestedQuantity;
                                                               stockDisplay.className = 'text-danger fw-bold';
                                                               quantityInput.classList.add('is-invalid');
                                                               } else if (requestedQuantity > 0) {
                                                               stockDisplay.textContent = 'Tồn kho: ' + stock + ', Dùng: ' + requestedQuantity;
                                                               stockDisplay.className = 'text-success';
                                                               quantityInput.classList.remove('is-invalid');
                                                               } else {
                                                               stockDisplay.textContent = 'Tồn kho: ' + stock;
                                                               stockDisplay.className = 'text-info';
                                                               quantityInput.classList.remove('is-invalid');
                                                               }
                                                               }
                                                               }
                                                               }

                                                               function getSelectedItems(receiptId, itemType) {
                                                               const selectedItems = [];
                                                               const containerSuffix = getContainerSuffix(itemType);
                                                               const itemRows = document.querySelectorAll('#' + receiptId + '_' + containerSuffix + 'Container .item-row');
                                                               itemRows.forEach(row => {
                                                               const select = row.querySelector('select');
                                                               if (select && select.value) {
                                                               selectedItems.push(select.value);
                                                               }
                                                               });
                                                               return selectedItems;
                                                               }
                                                               
                                                               function findExistingItemRow(receiptId, itemType, itemId) {
                                                               const containerSuffix = getContainerSuffix(itemType);
                                                               const itemRows = document.querySelectorAll('#' + receiptId + '_' + containerSuffix + 'Container .item-row');
                                                               for (let row of itemRows) {
                                                               const select = row.querySelector('select');
                                                               if (select && select.value === itemId.toString()) {
                                                               return row;
                                                               }
                                                               }
                                                               return null;
                                                               }
                                                               
                                                               function refreshItemDropdowns(excludeRowId, itemType) {
                                                               // Refresh dropdowns for both receipts
                                                               ['receipt1', 'receipt2'].forEach(receiptId => {
                                                               const containerSuffix = getContainerSuffix(itemType);
                                                               const itemRows = document.querySelectorAll('#' + receiptId + '_' + containerSuffix + 'Container .item-row');
                                                               itemRows.forEach(row => {
                                                               if (row.id !== excludeRowId) {
                                                               const select = row.querySelector('select');
                                                               const currentValue = select.value;
                                                               const selectedItems = getSelectedItems(receiptId, itemType);
                                                               
                                                               // Get data source based on item type
                                                               let itemsData = [];
                                                               if (itemType === 'service') itemsData = servicesData;
                                                               else if (itemType === 'supply') itemsData = suppliesData;
                                                               else if (itemType === 'medicine') itemsData = medicinesData;
                                                               
                                                               // Clear and rebuild options
                                                               select.innerHTML = '<option value="">-- Chọn --</option>';
                                                               itemsData.forEach(item => {
                                                               if (!selectedItems.includes(item.id.toString()) || item.id.toString() === currentValue) {
                                                               const option = document.createElement('option');
                                                               option.value = item.id;
                                                               const stockInfo = item.stock ? (item.stock > 0 ? ' (Còn: ' + item.stock + ')' : ' (Hết hàng)') : '';
                                                               option.textContent = item.name + stockInfo;
                                                               option.setAttribute('data-price', item.price);
                                                               if (item.stock !== undefined) {
                                                               option.setAttribute('data-stock', item.stock);
                                                               }
                                                               if (item.id.toString() === currentValue) {
                                                               option.selected = true;
                                                               }
                                                               select.appendChild(option);
                                                               }
                                                               });
                                                               
                                                               // Reinitialize Select2
                                                               $(select).select2('destroy').select2({
                                                               width: '100%',
                                                               dropdownParent: $(row).find('.col-md-3')
                                                               });
                                                               }
                                                               });
                                                               });
                                                               }

                                                               function addNewItemRow(receiptId, itemType) {
                                                               // Simply call addItemRow without itemId to create a new empty row
                                                               addItemRow(receiptId, itemType, '', 1);
                                                               }

                                                               function removeRow(rowId) {
                                                               const row = document.getElementById(rowId);
                                                               if (row) {
                                                               const itemType = rowId.includes('_service_') ? 'service' : (rowId.includes('_supply_') ? 'supply' : 'medicine');
                                                               row.remove();
                                                               calculateTotal();
                                                               
                                                               // Refresh dropdowns for all item types
                                                               refreshItemDropdowns('', itemType);
                                                               }
                                                               }

                                                               function calculateTotal() {
                                                               const receipt1ServiceTotal = calculateReceiptTotal('receipt1', 'service');
                                                               const receipt1SupplyTotal = calculateReceiptTotal('receipt1', 'supply');
                                                               const receipt1MedicineTotal = calculateReceiptTotal('receipt1', 'medicine');
                                                               const receipt1Total = receipt1ServiceTotal + receipt1SupplyTotal + receipt1MedicineTotal;
                                                               document.getElementById('receipt1_totalServices').value = formatCurrency(receipt1ServiceTotal);
                                                               document.getElementById('receipt1_totalSupplies').value = formatCurrency(receipt1SupplyTotal + receipt1MedicineTotal);
                                                               document.getElementById('receipt1_total').value = formatCurrency(receipt1Total);
                                                               let receipt2ServiceTotal = 0;
                                                               let receipt2SupplyTotal = 0;
                                                               let receipt2MedicineTotal = 0;
                                                               let receipt2Total = 0;
                                                               if (document.getElementById('enableSecondReceiptCheckbox').checked) {
                                                               receipt2ServiceTotal = calculateReceiptTotal('receipt2', 'service');
                                                               receipt2SupplyTotal = calculateReceiptTotal('receipt2', 'supply');
                                                               receipt2MedicineTotal = calculateReceiptTotal('receipt2', 'medicine');
                                                               receipt2Total = receipt2ServiceTotal + receipt2SupplyTotal + receipt2MedicineTotal;
                                                               }

                                                               document.getElementById('receipt2_totalServices').value = formatCurrency(receipt2ServiceTotal);
                                                               document.getElementById('receipt2_totalSupplies').value = formatCurrency(receipt2SupplyTotal + receipt2MedicineTotal);
                                                               document.getElementById('receipt2_total').value = formatCurrency(receipt2Total);
                                                               const totalServices = receipt1ServiceTotal + receipt2ServiceTotal;
                                                               const totalSupplies = receipt1SupplyTotal + receipt1MedicineTotal + receipt2SupplyTotal + receipt2MedicineTotal;
                                                               const discount = parseFloat(document.getElementById('discountAmount').value) || 0;
                                                               const finalAmount = totalServices + totalSupplies - discount;
                                                               document.getElementById('totalServices').value = formatCurrency(totalServices);
                                                               document.getElementById('totalSupplies').value = formatCurrency(totalSupplies);
                                                               document.getElementById('finalAmount').value = formatCurrency(finalAmount);
                                                               }

                                                               function calculateReceiptTotal(receiptId, itemType) {
                                                               let total = 0;
                                                               const containers = document.querySelectorAll('#' + receiptId + '_' + getContainerSuffix(itemType) + 'Container .item-row');
                                                               containers.forEach(row => {
                                                               const totalInput = row.querySelector('input[id$="_total"]');
                                                               if (totalInput) {
                                                               const value = parseFloat(totalInput.value.replace(/[,.đ\s]/g, '')) || 0;
                                                               total += value;
                                                               }
                                                               });
                                                               return total;
                                                               }

                                                               function formatCurrency(amount) {
                                                               return new Intl.NumberFormat('vi-VN').format(amount) + 'đ';
                                                               }

                                                               function getItemTypeLabel(itemType) {
                                                               return { 'service': 'Dịch vụ', 'supply': 'Vật tư', 'medicine': 'Thuốc' }[itemType] || '';
                                                               }

                                                               function getContainerSuffix(itemType) {
                                                               return itemType === 'supply' ? 'supplies' : itemType + 's';
                                                               }

                                                               function loadExistingItems() {
                                                               existingFirstReceiptItems.forEach(item => {
                                                               addItemRow('receipt1', item.type, item.id, item.quantity);
                                                               });
                                                               if (existingSecondReceiptItems.length > 0) {
                                                               existingSecondReceiptItems.forEach(item => {
                                                               addItemRow('receipt2', item.type, item.id, item.quantity);
                                                               });
                                                               }
                                                               }

                                                               // Hàm lọc option theo keyword
                                                               function filterDropdownOptions(input, selectId) {
                                                               const filter = input.value.toLowerCase();
                                                               const select = document.getElementById(selectId);
                                                               for (let i = 0; i < select.options.length; i++) {
                                                               const option = select.options[i];
                                                               const text = option.text.toLowerCase();
                                                               // Không ẩn option đầu tiên ("-- Chọn --")
                                                               if (i === 0) {
                                                               option.style.display = '';
                                                               continue;
                                                               }
                                                               option.style.display = text.includes(filter) ? '' : 'none';
                                                               }
                                                               }
        </script>
    </body>
</html>