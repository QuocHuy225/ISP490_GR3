<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.Patient" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalRecord" %>
<%@ page import="com.mycompany.isp490_gr3.model.Invoice" %>
<%@ page import="com.mycompany.isp490_gr3.model.InvoiceItem" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.math.BigDecimal" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết hóa đơn - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Homepage specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <!-- Invoice specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/invoice.css">
        <style>
            .invoice-header {
                text-align: center;
                border-bottom: 2px solid #007bff;
                padding-bottom: 20px;
                margin-bottom: 30px;
            }

            .invoice-section {
                margin-bottom: 25px;
                padding: 20px;
                border: 1px solid #e0e0e0;
                border-radius: 8px;
                background-color: #fff;
            }

            .invoice-details {
                background-color: #f8f9fa;
            }

            .print-section {
                page-break-inside: avoid;
            }

            .total-section {
                background-color: #f8f9fa;
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

            .payment-receipt {
                background: white;
                border: 2px solid #e9ecef;
                border-radius: 1rem;
                padding: 1.5rem;
                margin-bottom: 2rem;
                box-shadow: 0 4px 20px rgba(0,0,0,0.08);
                position: relative;
                overflow: hidden;
            }

            .payment-receipt.receipt-1 {
                border-color: #007bff;
            }

            .payment-receipt.receipt-1::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                height: 4px;
                background: linear-gradient(90deg, #007bff, #0056b3);
            }

            .payment-receipt.receipt-2 {
                border-color: #28a745;
            }

            .payment-receipt.receipt-2::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                height: 4px;
                background: linear-gradient(90deg, #28a745, #20c997);
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

            .receipt-1 .receipt-title i {
                background: linear-gradient(135deg, #007bff, #0056b3);
            }

            .receipt-2 .receipt-title i {
                background: linear-gradient(135deg, #28a745, #20c997);
            }

            .receipt-time {
                font-size: 0.9rem;
                color: #6c757d;
                font-weight: 500;
            }

            .receipt-totals {
                background: linear-gradient(135deg, #f8f9fa, #e9ecef);
                border: 1px solid #dee2e6;
                border-radius: 0.75rem;
                padding: 1rem;
                margin-top: 1rem;
            }

            .receipt-totals h6 {
                margin-bottom: 0.75rem;
                font-weight: 600;
                color: #495057;
            }

            .receipt-totals .row {
                align-items: center;
            }

            .receipt-totals .fw-bold {
                font-size: 1.1rem;
            }

            .receipt-1 .receipt-totals {
                background: linear-gradient(135deg, #e3f2fd, #bbdefb);
                border-color: #2196f3;
            }

            .receipt-2 .receipt-totals {
                background: linear-gradient(135deg, #e8f5e8, #c8e6c9);
                border-color: #4caf50;
            }

            /* Two-column layout adjustments */
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

                .invoice-section, .payment-receipt {
                    break-inside: avoid;
                    margin-bottom: 15px;
                    border: 1px solid #000;
                    padding: 10px;
                }

                .invoice-header {
                    border-bottom: 2px solid #000;
                    margin-bottom: 20px;
                }

                .table {
                    font-size: 11pt;
                }

                .table th,
                .table td {
                    padding: 8px 6px;
                    border: 1px solid #000;
                }

                .badge {
                    background-color: #000 !important;
                    color: #fff !important;
                }

                .payment-receipt::before {
                    display: none;
                }

                /* Ensure print layout remains full-width */
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
        Invoice invoice = (Invoice) request.getAttribute("invoice");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat shortSdf = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat currencyFormat = new DecimalFormat("#,###");
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



                    <div class="dropdown user-dropdown">
                        <button class="btn dropdown-toggle" type="button" id="userDropdown" data-bs-toggle="dropdown">
                            <div class="user-profile-icon">
                                <i class="bi bi-person-fill" style="font-size: 1.5rem;"></i>
                            </div>
                            <div class="user-info d-none d-md-block">
                                <div class="user-name"><%= userName %></div>
                                <div class="user-role"><%= userRoleDisplay %></div>
                            </div>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">
                                    <i class="bi bi-person-fill"></i> Thông tin cá nhân</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">
                                    <i class="bi bi-key-fill"></i> Đổi mật khẩu</a></li>
                            <li><a class="dropdown-item" href="#">
                                    <i class="bi bi-gear-fill"></i> Cài đặt</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth/logout">
                                    <i class="bi bi-box-arrow-right"></i> Đăng xuất</a></li>
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
                                <li class="breadcrumb-item active">Chi tiết hóa đơn</li>
                            </ol>
                        </nav>
                    </div>
                </div>

                <!-- Error/Success Messages -->
                <% 
                String errorParam = request.getParameter("error");
                String successParam = request.getParameter("success");
                %>

                <% if ("medical_record_completed".equals(errorParam)) { %>
                <div class="alert alert-warning alert-dismissible fade show no-print" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <strong>Không thể chỉnh sửa!</strong> Hóa đơn không thể chỉnh sửa vì hồ sơ bệnh án đã hoàn thành.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } else if ("updated".equals(successParam)) { %>
                <div class="alert alert-success alert-dismissible fade show no-print" role="alert">
                    <i class="bi bi-check-circle me-2"></i>
                    <strong>Thành công!</strong> Hóa đơn đã được cập nhật thành công.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } else if ("added".equals(successParam)) { %>
                <div class="alert alert-success alert-dismissible fade show no-print" role="alert">
                    <i class="bi bi-check-circle me-2"></i>
                    <strong>Thành công!</strong> Hóa đơn đã được tạo thành công.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <!-- Back Button -->
                <div class="d-flex justify-content-between align-items-center mb-3 no-print">
                    <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%= patient.getId() %>" 
                       class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left me-2"></i>Quay lại
                    </a>

                    <div class="action-buttons">
                        <% if (medicalRecord != null && !medicalRecord.isCompleted()) { %>
                        <a href="${pageContext.request.contextPath}/doctor/invoices?action=edit&invoiceId=<%= invoice.getInvoiceId() %>" 
                           class="btn btn-primary me-2">
                            <i class="bi bi-pencil-square me-2"></i>Chỉnh sửa
                        </a>
                        <% } else if (medicalRecord != null && medicalRecord.isCompleted()) { %>
                        <span class="btn btn-secondary me-2 disabled" title="Không thể chỉnh sửa hóa đơn khi hồ sơ bệnh án đã hoàn thành">
                            <i class="bi bi-pencil-square me-2"></i>Chỉnh sửa
                        </span>
                        <% } %>
                        <button type="button" class="btn btn-success" onclick="window.print()">
                            <i class="bi bi-printer me-2"></i>In hóa đơn
                        </button>
                    </div>
                </div>

                <% if (invoice != null && patient != null && medicalRecord != null) { %>
                <!-- Invoice Header -->
                <div class="invoice-header">
                    <h2 class="text-primary mb-3">
                        <span style="color: #007bff;">Ánh Dương</span>
                        <span style="color: #333;">Clinic</span>
                    </h2>
                    <h4>HÓA ĐƠN THANH TOÁN</h4>
                    <p class="mb-1">Địa chỉ: 123 Đường ABC, Quận XYZ, TP.HCM</p>
                    <p class="mb-0">Điện thoại: (028) 1234-5678 | Email: info@anhduongclinic.com</p>
                </div>

                <!-- Two-Column Layout -->
                <div class="row two-column-container">
                    <div class="col-md-3 patient-info-card">
                        <div class="card medical-card">
                            <div class="card-header">
                                <h4 class="text-primary mb-3">
                                    <i class="bi bi-file-medical me-2"></i>Thông tin bệnh nhân
                                </h4>
                            </div>
                                                            <p><strong>Mã bệnh nhân:</strong> <%= patient.getPatientCode() %></p>
                                <p><strong>Họ tên:</strong> <%= patient.getFullName() %></p>
                                <p><strong>Ngày sinh:</strong> <%= patient.getDob() != null ? shortSdf.format(patient.getDob()) : "" %></p>
                                <p><strong>Điện thoại:</strong> <%= patient.getPhone() %></p>
                                <p><strong>Địa chỉ:</strong> <%= patient.getAddress() %></p>

                            <div class="card-header">
                                <h4 class="text-primary mb-3">
                                    <i class="bi bi-file-medical me-2"></i>Thông tin hóa đơn
                                </h4>
                            </div>
                            <p><strong>Mã hóa đơn:</strong> <%= invoice.getInvoiceId() %></p>
                            <p><strong>Mã hồ sơ:</strong> <%= invoice.getMedicalRecordId() %></p>
                            <p><strong>Bác sĩ:</strong> <%= request.getAttribute("doctor") != null ? ((com.mycompany.isp490_gr3.model.Doctor) request.getAttribute("doctor")).getFullName() : "Chưa xác định" %></p>
                            <p><strong>Ngày tạo:</strong> <%= invoice.getCreatedAt() != null ? sdf.format(invoice.getCreatedAt()) : "" %></p>
                        </div>
                    </div>

                    <!-- Right Column: Payment Receipts & Signature (3/4 screen) -->
                    <div class="col-md-9 right-column">
                        <!-- Payment Receipt 1 -->
                        <div class="payment-receipt receipt-1">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-receipt"></i>
                                    Phiếu thu 1
                                </div>
                                <div class="receipt-time">
                                    <%= invoice.getCreatedAt() != null ? sdf.format(invoice.getCreatedAt()) : "" %>
                                </div>
                            </div>

                            <% 
                            // Filter items for receipt 1
                            java.util.List<InvoiceItem> receipt1Items = new java.util.ArrayList<>();
                            if (invoice.getInvoiceItems() != null) {
                                for (InvoiceItem item : invoice.getInvoiceItems()) {
                                    if (item.getReceiptNumber() == 1) {
                                        receipt1Items.add(item);
                                    }
                                }
                            }
                            %>

                            <% 
                            // Calculate totals for receipt 1
                            BigDecimal receipt1ServiceTotal = BigDecimal.ZERO;
                            BigDecimal receipt1SupplyTotal = BigDecimal.ZERO;
                            BigDecimal receipt1Total = BigDecimal.ZERO;
                            
                            if (!receipt1Items.isEmpty()) {
                                for (InvoiceItem item : receipt1Items) { 
                                    if ("service".equals(item.getItemType())) {
                                        receipt1ServiceTotal = receipt1ServiceTotal.add(item.getTotalAmount());
                                    } else {
                                        receipt1SupplyTotal = receipt1SupplyTotal.add(item.getTotalAmount());
                                    }
                                    receipt1Total = receipt1Total.add(item.getTotalAmount());
                                }
                            }
                            %>

                            <% if (!receipt1Items.isEmpty()) { %>
                            <div class="table-responsive">
                                <table class="table table-bordered">
                                    <thead class="table-light">
                                        <tr>
                                            <th>STT</th>
                                            <th>Tên dịch vụ</th>
                                            <th>Loại</th>
                                            <th class="text-center">Số lượng</th>
                                            <th class="text-end">Đơn giá</th>
                                            <th class="text-end">Thành tiền</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% 
                                        int itemIndex1 = 1;
                                        for (InvoiceItem item : receipt1Items) { 
                                        %>
                                        <tr>
                                            <td><%= itemIndex1++ %></td>
                                            <td><%= item.getItemName() %></td>
                                            <td>
                                                <% if ("service".equals(item.getItemType())) { %>
                                                <span class="badge bg-primary">Dịch vụ</span>
                                                <% } else if ("supply".equals(item.getItemType())) { %>
                                                <span class="badge bg-info">Vật tư</span>
                                                <% } else { %>
                                                <span class="badge bg-success">Thuốc</span>
                                                <% } %>
                                            </td>
                                            <td class="text-center"><%= item.getQuantity() %></td>
                                            <td class="text-end"><%= currencyFormat.format(item.getUnitPrice()) %>đ</td>
                                            <td class="text-end"><%= currencyFormat.format(item.getTotalAmount()) %>đ</td>
                                        </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                            <% } else { %>
                            <div class="text-center py-4 text-muted">
                                <i class="bi bi-inbox display-4"></i>
                                <p class="mt-2">Phiếu thu chưa có dịch vụ nào</p>
                            </div>
                            <% } %>

                            <div class="receipt-totals">
                                <div class="row">
                                    <div class="col-md-4">
                                        <div class="d-flex justify-content-between">
                                            <span>Dịch vụ: <%= currencyFormat.format(receipt1ServiceTotal) %>đ</span>
                                        </div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="d-flex justify-content-between">
                                            <span>Vật tư & Thuốc: <%= currencyFormat.format(receipt1SupplyTotal) %>đ</span>
                                        </div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="d-flex justify-content-between fw-bold">
                                            <span>Tổng phiếu thu: <%= currencyFormat.format(receipt1Total) %>đ</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Payment Receipt 2 -->
                        <% 
                        // Filter items for receipt 2
                        java.util.List<InvoiceItem> receipt2Items = new java.util.ArrayList<>();
                        if (invoice.getInvoiceItems() != null) {
                            for (InvoiceItem item : invoice.getInvoiceItems()) {
                                if (item.getReceiptNumber() == 2) {
                                    receipt2Items.add(item);
                                }
                            }
                        }
                        %>

                        <% 
                        // Calculate totals for receipt 2
                        BigDecimal receipt2ServiceTotal = BigDecimal.ZERO;
                        BigDecimal receipt2SupplyTotal = BigDecimal.ZERO;
                        BigDecimal receipt2Total = BigDecimal.ZERO;
                        
                        if (!receipt2Items.isEmpty()) {
                            for (InvoiceItem item : receipt2Items) { 
                                if ("service".equals(item.getItemType())) {
                                    receipt2ServiceTotal = receipt2ServiceTotal.add(item.getTotalAmount());
                                } else {
                                    receipt2SupplyTotal = receipt2SupplyTotal.add(item.getTotalAmount());
                                }
                                receipt2Total = receipt2Total.add(item.getTotalAmount());
                            }
                        }
                        %>

                        <% if (!receipt2Items.isEmpty()) { %>
                        <div class="payment-receipt receipt-2">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-receipt"></i>
                                    Phiếu thu 2
                                </div>
                                <div class="receipt-time">
                                    <%= sdf.format(invoice.getCreatedAt()) %>
                                </div>
                            </div>

                            <div class="table-responsive">
                                <table class="table table-bordered">
                                    <thead class="table-light">
                                        <tr>
                                            <th>STT</th>
                                            <th>Tên dịch vụ</th>
                                            <th>Loại</th>
                                            <th class="text-center">Số lượng</th>
                                            <th class="text-end">Đơn giá</th>
                                            <th class="text-end">Thành tiền</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% 
                                        int itemIndex2 = 1;
                                        for (InvoiceItem item : receipt2Items) { 
                                        %>
                                        <tr>
                                            <td><%= itemIndex2++ %></td>
                                            <td><%= item.getItemName() %></td>
                                            <td>
                                                <% if ("service".equals(item.getItemType())) { %>
                                                <span class="badge bg-primary">Dịch vụ</span>
                                                <% } else if ("supply".equals(item.getItemType())) { %>
                                                <span class="badge bg-info">Vật tư</span>
                                                <% } else { %>
                                                <span class="badge bg-success">Thuốc</span>
                                                <% } %>
                                            </td>
                                            <td class="text-center"><%= item.getQuantity() %></td>
                                            <td class="text-end"><%= currencyFormat.format(item.getUnitPrice()) %>đ</td>
                                            <td class="text-end"><%= currencyFormat.format(item.getTotalAmount()) %>đ</td>
                                        </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>

                            <div class="receipt-totals">
                                <div class="row">
                                    <div class="col-md-4">
                                        <div class="d-flex justify-content-between">
                                            <span>Dịch vụ: <%= currencyFormat.format(receipt2ServiceTotal) %>đ</span>
                                        </div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="d-flex justify-content-between">
                                            <span>Vật tư & Thuốc: <%= currencyFormat.format(receipt2SupplyTotal) %>đ</span>
                                        </div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="d-flex justify-content-between fw-bold">
                                            <span>Tổng phiếu thu: <%= currencyFormat.format(receipt2Total) %>đ</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <% } %>

                        <!-- Total Section -->
                        <div class="invoice-section total-signature-section card shadow-sm p-4 mb-4">

                            <div class="card-header bg-transparent border-0">
                                <h4 class="text-primary mb-4">
                                    <i class="bi bi-file-medical me-2"></i>Tổng hóa đơn
                                </h4>
                            </div>

                            <div class="row g-4">
                                <!-- Totals Table -->
                                <div class="col-md-6 offset-md-6">
                                    <table class="table table-sm table-borderless">
                                        <tr>
                                            <td><strong>Tổng dịch vụ:</strong></td>
                                            <td class="text-end"><%= currencyFormat.format(invoice.getTotalServiceAmount()) %>đ</td>
                                        </tr>
                                        <tr>
                                            <td><strong>Tổng vật tư & thuốc:</strong></td>
                                            <td class="text-end"><%= currencyFormat.format(invoice.getTotalSupplyAmount()) %>đ</td>
                                        </tr>
                                        <tr>
                                            <td><strong>Tổng tiền:</strong></td>
                                            <td class="text-end"><%= currencyFormat.format(invoice.getTotalAmount()) %>đ</td>
                                        </tr>
                                        <% if (invoice.getDiscountAmount() != null && invoice.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) { %>
                                        <tr>
                                            <td><strong>Giảm giá:</strong></td>
                                            <td class="text-end text-danger">-<%= currencyFormat.format(invoice.getDiscountAmount()) %>đ</td>
                                        </tr>
                                        <% } %>
                                        <tr class="table-primary">
                                            <td><strong>THÀNH TIỀN:</strong></td>
                                            <td class="text-end fw-bold fs-5"><%= currencyFormat.format(invoice.getFinalAmount()) %>đ</td>
                                        </tr>
                                    </table>
                                </div>
                                <!-- Notes Section -->
                                <% if (invoice.getNotes() != null && !invoice.getNotes().isEmpty()) { %>
                                <div class="col-12">
                                    <div class="notes-section p-3 bg-light rounded">
                                        <strong class="d-block mb-2" style="font-size: 1.2em;">Ghi chú:</strong>
                                        <span style="font-size: 1.1em;"><%= invoice.getNotes() %></span>
                                    </div>
                                </div>
                                <% } %>
                            </div>

                            <!-- Signature Section -->
                            <hr class="my-4">
                            <div class="signature-section mt-4">
                                <div class="row text-center g-4">
                                    <div class="col-md-4">
                                        <p class="mb-4 fw-bold">Người thanh toán</p>
                                        <p class="signature-line mx-auto"></p>
                                        <p class="mb-0 text-muted"><em>(Ký và ghi rõ họ tên)</em></p>
                                    </div>
                                    <div class="col-md-4">
                                        <p class="mb-4 fw-bold">Thu ngân</p>
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
                                    <p class="mb-0 fw-bold text-primary">Cảm ơn quý khách đã sử dụng dịch vụ!</p>
                                </div>
                            </div>

                            <style>
                                .total-signature-section {
                                    background: #ffffff;
                                    border-radius: 12px;
                                    transition: all 0.3s ease;
                                    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
                                }
                                .total-signature-section .table {
                                    margin-bottom: 0;
                                    font-size: 0.95rem;
                                }
                                .total-signature-section .table tr {
                                    border-bottom: 1px solid #e9ecef;
                                    transition: background-color 0.2s ease;
                                }
                                .total-signature-section .table tr:hover {
                                    background-color: #f8f9fa;
                                }
                                .total-signature-section .table tr:last-child {
                                    border-bottom: none;
                                }
                                .notes-section {
                                    background-color: #e3f2fd;
                                    border-left: 4px solid #007bff;
                                    border-radius: 8px;
                                    font-size: 0.9rem;
                                    line-height: 1.6;
                                }
                                .signature-line {
                                    border-bottom: 2px solid #343a40;
                                    width: 180px;
                                    height: 2px;
                                    margin: 0 auto;
                                }
                                .text-primary {
                                    color: #0d6efd !important;
                                }
                                .bg-light {
                                    background-color: #bfdeff !important;
                                }
                                .table-primary {
                                    background-color: #e7f1ff !important;
                                }
                                .signature-section .fw-bold {
                                    font-size: 1rem;
                                    color: #1a252f;
                                }
                                .text-muted {
                                    font-size: 0.85rem;
                                }
                                @media (max-width: 767.98px) {
                                    .total-signature-section .table {
                                        font-size: 0.85rem;
                                    }
                                    .signature-line {
                                        width: 140px;
                                    }
                                }
                            </style>
                        </div>
                    </div>
                </div>

                <% } else { %>
                <div class="alert alert-danger">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    Không tìm thấy thông tin hóa đơn!
                </div>
                <% } %>
            </div>
        </div>

        <!-- Scripts -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
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

                                checkWidth();
                                window.addEventListener('resize', checkWidth);
                            });
        </script>
    </body>
</html>