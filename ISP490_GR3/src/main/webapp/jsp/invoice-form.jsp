<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.Patient" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalRecord" %>
<%@ page import="com.mycompany.isp490_gr3.model.Invoice" %>
<%@ page import="com.mycompany.isp490_gr3.model.PaymentReceipt" %>
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
            /* Payment Receipt Styling */
            .payment-receipt {
                background: white;
                border: 2px solid #e9ecef;
                border-radius: 1rem;
                padding: 2rem;
                margin-bottom: 2rem;
                box-shadow: 0 4px 20px rgba(0,0,0,0.08);
                transition: all 0.3s ease;
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
            
            .payment-receipt.disabled {
                opacity: 0.6;

                /* Tạm khóa thao tác bên trong, ngoại trừ phần toggle */
            }

            /* Ngăn mọi thao tác bên trong khi disabled */
            .payment-receipt.disabled * {
                pointer-events: none;
            }

            /* Nhưng vẫn cho phép click vào checkbox bật/tắt */
            .payment-receipt.disabled .receipt-toggle *,
            .payment-receipt.disabled .receipt-header {
                pointer-events: auto;
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
                margin-right: 0.5rem;
                padding: 0.5rem;
                border-radius: 0.5rem;
                color: white;
            }
            
            .receipt-1 .receipt-title i {
                background: linear-gradient(135deg, #007bff, #0056b3);
            }

            .receipt-2 .receipt-title i {
                background: linear-gradient(135deg, #28a745, #20c997);
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
            
            .receipt-sections {
                display: grid;
                gap: 1.5rem;
            }
            
            .receipt-section {
                background: #f8f9fa;
                border: 1px solid #e9ecef;
                border-radius: 0.75rem;
                padding: 1.5rem;
                transition: all 0.3s ease;
            }
            
            .receipt-section:hover {
                background: white;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }
            
            .receipt-section h6 {
                margin-bottom: 1rem;
                font-weight: 600;
                color: #495057;
                display: flex;
                align-items: center;
            }
            
            .receipt-section h6 i {
                margin-right: 0.5rem;
                color: #007bff;
            }
            
            .receipt-2 .receipt-section h6 i {
                color: #28a745;
            }
            
            .item-headers {
                background: #ffffff;
                border: 1px solid #dee2e6;
                border-radius: 0.5rem;
                padding: 1rem;
                margin-bottom: 1rem;
                font-weight: 600;
                color: #495057;
            }
            
            .item-row {
                background: white;
                border: 1px solid #e9ecef;
                border-radius: 0.5rem;
                padding: 1rem;
                margin-bottom: 0.75rem;
                transition: all 0.3s ease;
            }
            
            .item-row:hover {
                border-color: #007bff;
                box-shadow: 0 2px 8px rgba(0,123,255,0.15);
            }
            
            .receipt-2 .item-row:hover {
                border-color: #28a745;
                box-shadow: 0 2px 8px rgba(40,167,69,0.15);
            }
            
            .btn-add-item {
                background: linear-gradient(135deg, #007bff, #0056b3);
                border: none;
                color: white;
                padding: 0.75rem 1.5rem;
                border-radius: 0.5rem;
                font-weight: 500;
                transition: all 0.3s ease;
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
            }

            .btn-add-item:hover {
                background: linear-gradient(135deg, #0056b3, #004085);
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(0,123,255,0.3);
                color: white;
            }

            .receipt-2 .btn-add-item {
                background: linear-gradient(135deg, #28a745, #20c997);
            }
            
            .receipt-2 .btn-add-item:hover {
                background: linear-gradient(135deg, #20c997, #1e7e34);
                box-shadow: 0 4px 12px rgba(40,167,69,0.3);
                color: white;
            }
            
            .btn-delete-item {
                background: #dc3545;
                border: none;
                color: white;
                padding: 0.5rem;
                border-radius: 0.25rem;
                width: 35px;
                height: 35px;
                display: flex;
                align-items: center;
                justify-content: center;
                transition: all 0.3s ease;
            }

            .btn-delete-item:hover {
                background: #c82333;
                transform: scale(1.05);
            }
            
            .receipt-totals {
                background: linear-gradient(135deg, #f8f9fa, #e9ecef);
                border: 1px solid #dee2e6;
                border-radius: 0.75rem;
                padding: 1.5rem;
                margin-top: 1.5rem;
            }
            
            .receipt-totals h6 {
                margin-bottom: 1rem;
                font-weight: 600;
                color: #495057;
            }

            .total-section {
                background: linear-gradient(135deg, #e8f5e8, #c8e6c9);
                border: 2px solid #4caf50;
                border-radius: 1rem;
                padding: 2rem;
                margin-top: 2rem;
            }
            
            .total-section h6 {
                color: #2e7d32;
                font-weight: 700;
                margin-bottom: 1.5rem;
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
        Invoice invoice = (Invoice) request.getAttribute("invoice");
        
        List<MedicalService> services = (List<MedicalService>) request.getAttribute("services");
        List<MedicalSupply> supplies = (List<MedicalSupply>) request.getAttribute("supplies");
        List<Medicine> medicines = (List<Medicine>) request.getAttribute("medicines");
        
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
                    <a href="${pageContext.request.contextPath}/patients">
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
                                <% if (patient != null) { %>
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%= patient.getId() %>">
                                        Hồ sơ bệnh án
                                    </a>
                                </li>
                                <% } %>
                                <li class="breadcrumb-item active">
                                    <%= isEdit ? "Chỉnh sửa hóa đơn" : "Tạo hóa đơn mới" %>
                                </li>
                            </ol>
                        </nav>
                        
                        <!-- Alert Messages -->
                        <%
                        String success = request.getParameter("success");
                        String error = request.getParameter("error");
                        String errorMessage = request.getParameter("message");
                        %>
                        <% if (success != null) { %>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="bi bi-check-circle me-2"></i>
                            <% if ("added".equals(success)) { %>
                            Tạo hóa đơn thành công!
                            <% } else if ("updated".equals(success)) { %>
                            Cập nhật hóa đơn thành công!
                            <% } %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <% } %>

                        <% if (error != null) { %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-triangle me-2"></i>
                            <% if ("add_failed".equals(error)) { %>
                            <strong>Tạo hóa đơn thất bại!</strong> Vui lòng thử lại.
                            <% } else if ("update_failed".equals(error)) { %>
                            <strong>Cập nhật hóa đơn thất bại!</strong> Vui lòng thử lại.
                            <% } else if ("insufficient_stock".equals(error)) { %>
                            <strong>Không đủ tồn kho!</strong><br>
                            <% if (errorMessage != null) { %>
                            <%= errorMessage %>
                            <% } else { %>
                            Vui lòng kiểm tra lại số lượng vật tư/thuốc.
                            <% } %>
                            <% } else if ("database_update_needed".equals(error)) { %>
                            <strong>Cần cập nhật hệ thống!</strong><br>
                            <% if (errorMessage != null) { %>
                            <%= errorMessage %>
                            <% } else { %>
                            Hệ thống cần được cập nhật. Vui lòng liên hệ quản trị viên.
                            <% } %>
                            <% } else if ("missing_data".equals(error)) { %>
                            <strong>Thiếu thông tin!</strong> Vui lòng điền đầy đủ thông tin bắt buộc.
                            <% } else if ("invalid_data".equals(error)) { %>
                            <strong>Dữ liệu không hợp lệ!</strong> Vui lòng kiểm tra lại thông tin nhập.
                            <% } else if ("system_error".equals(error)) { %>
                            <strong>Lỗi hệ thống!</strong> Vui lòng thử lại sau hoặc liên hệ quản trị viên.
                            <% } else { %>
                            Có lỗi xảy ra. Vui lòng thử lại!
                            <% } %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <% } %>
                        
                        <% if (patient != null && medicalRecord != null) { %>
                        <div class="card bg-light">
                            <div class="card-body">
                                <h4 class="text-primary mb-3">
                                    <i class="bi bi-<%= isEdit ? "pencil-square" : "plus-circle" %> me-2"></i>
                                    <%= isEdit ? "Chỉnh sửa hóa đơn" : "Tạo hóa đơn mới" %>
                                </h4>
                                <div class="row">
                                    <div class="col-md-6">
                                        <p class="mb-2"><strong>Mã bệnh nhân:</strong> <%= patient.getPatientCode() %></p>
                                        <p class="mb-2"><strong>Họ tên:</strong> <%= patient.getFullName() %></p>
                                        <p class="mb-0"><strong>Ngày sinh:</strong> <%= patient.getDob() != null ? shortSdf.format(patient.getDob()) : "" %></p>
                                    </div>
                                    <div class="col-md-6">
                                        <p class="mb-2"><strong>Mã hồ sơ:</strong> <%= medicalRecord.getId() %></p>
                                        <p class="mb-2"><strong>Ngày tạo hồ sơ:</strong> <%= medicalRecord.getCreatedAt() != null ? shortSdf.format(medicalRecord.getCreatedAt()) : "" %></p>
                                        <% if (isEdit && invoice != null) { %>
                                        <p class="mb-0"><strong>Mã hóa đơn:</strong> <%= invoice.getInvoiceId() %></p>
                                        <% } %>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <% } %>
                    </div>
                </div>

                <!-- Invoice Form -->
                <div class="invoice-form-container">
                <form method="POST" action="${pageContext.request.contextPath}/doctor/invoices" id="invoiceForm">
                    <input type="hidden" name="action" value="<%= isEdit ? "update" : "add" %>">
                    <input type="hidden" name="medicalRecordId" value="<%= medicalRecord != null ? medicalRecord.getId() : "" %>">
                    <input type="hidden" name="patientId" value="<%= patient != null ? patient.getId() : "" %>">
                    <% if (isEdit && invoice != null) { %>
                    <input type="hidden" name="invoiceId" value="<%= invoice.getInvoiceId() %>">
                    <% } %>
                    
                        <!-- Phiếu thu 1 - Bắt buộc -->
                        <div class="payment-receipt receipt-1" id="receipt1">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-receipt"></i>
                                    Phiếu thu 1 (Bắt buộc)
                                </div>
                                <div class="receipt-toggle">
                                    <small class="text-muted">Phiếu thu này là bắt buộc</small>
                                </div>
                            </div>
                            
                            <div class="receipt-sections">
                                <!-- Services for Receipt 1 -->
                                <div class="receipt-section">
                                    <h6><i class="bi bi-file-medical"></i>Dịch vụ y tế</h6>
                                <div class="item-headers">
                                    <div class="row">
                                        <div class="col-md-5">Tên dịch vụ</div>
                                        <div class="col-md-2 text-center">Số lượng</div>
                                        <div class="col-md-2 text-center">Đơn giá</div>
                                        <div class="col-md-2 text-center">Thành tiền</div>
                                        <div class="col-md-1 text-center">Xóa</div>
                                    </div>
                                    </div>
                                    <div id="receipt1_servicesContainer"></div>
                                    <button type="button" class="btn btn-add-item" onclick="addItemRow('receipt1', 'service')">
                                        <i class="bi bi-plus-circle"></i>Thêm dịch vụ
                                    </button>
                                </div>
                                
                                <!-- Supplies for Receipt 1 -->
                                <div class="receipt-section">
                                    <h6><i class="bi bi-gear"></i>Vật tư y tế</h6>
                                    <div class="item-headers">
                                        <div class="row">
                                            <div class="col-md-5">Tên vật tư</div>
                                            <div class="col-md-2 text-center">Số lượng</div>
                                            <div class="col-md-2 text-center">Đơn giá</div>
                                            <div class="col-md-2 text-center">Thành tiền</div>
                                            <div class="col-md-1 text-center">Xóa</div>
                                </div>
                                    </div>
                                    <div id="receipt1_suppliesContainer"></div>
                                    <button type="button" class="btn btn-add-item" onclick="addItemRow('receipt1', 'supply')">
                                        <i class="bi bi-plus-circle"></i>Thêm vật tư
                                </button>
                            </div>
                            
                                <!-- Medicines for Receipt 1 -->
                                <div class="receipt-section">
                                    <h6><i class="bi bi-capsule"></i>Thuốc</h6>
                                    <div class="item-headers">
                                        <div class="row">
                                            <div class="col-md-5">Tên thuốc</div>
                                            <div class="col-md-2 text-center">Số lượng</div>
                                            <div class="col-md-2 text-center">Đơn giá</div>
                                            <div class="col-md-2 text-center">Thành tiền</div>
                                            <div class="col-md-1 text-center">Xóa</div>
                                        </div>
                                    </div>
                                    <div id="receipt1_medicinesContainer"></div>
                                    <button type="button" class="btn btn-add-item" onclick="addItemRow('receipt1', 'medicine')">
                                        <i class="bi bi-plus-circle"></i>Thêm thuốc
                                    </button>
                                </div>
                            </div>
                            
                            <div class="receipt-totals">
                                <h6>Tổng cộng phiếu thu 1</h6>
                                <div class="row">
                                    <div class="col-md-4">
                                        <label class="form-label">Dịch vụ</label>
                                        <input type="text" class="form-control" id="receipt1_totalServices" readonly>
                                    </div>
                                    <div class="col-md-4">
                                        <label class="form-label">Vật tư & Thuốc</label>
                                        <input type="text" class="form-control" id="receipt1_totalSupplies" readonly>
                                    </div>
                                    <div class="col-md-4">
                                        <label class="form-label fw-bold">Tổng phiếu thu 1</label>
                                        <input type="text" class="form-control fw-bold" id="receipt1_total" readonly>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Phiếu thu 2 - Tùy chọn -->
                        <div class="payment-receipt receipt-2" id="receipt2">
                            <div class="receipt-header">
                                <div class="receipt-title">
                                    <i class="bi bi-receipt"></i>
                                    Phiếu thu 2 (Tùy chọn)
                                </div>
                                <div class="receipt-toggle">
                                    <input type="checkbox" id="enableSecondReceipt" name="enableSecondReceipt" value="true" onchange="toggleSecondReceipt()">
                                    <label for="enableSecondReceipt">Sử dụng phiếu thu 2</label>
                                </div>
                            </div>
                            
                            <div class="receipt-sections" id="receipt2_sections">
                                <!-- Services for Receipt 2 -->
                                <div class="receipt-section">
                                    <h6><i class="bi bi-file-medical"></i>Dịch vụ y tế</h6>
                                <div class="item-headers">
                                    <div class="row">
                                            <div class="col-md-5">Tên dịch vụ</div>
                                        <div class="col-md-2 text-center">Số lượng</div>
                                        <div class="col-md-2 text-center">Đơn giá</div>
                                        <div class="col-md-2 text-center">Thành tiền</div>
                                        <div class="col-md-1 text-center">Xóa</div>
                                    </div>
                                    </div>
                                    <div id="receipt2_servicesContainer"></div>
                                    <button type="button" class="btn btn-add-item" onclick="addItemRow('receipt2', 'service')">
                                        <i class="bi bi-plus-circle"></i>Thêm dịch vụ
                                    </button>
                                </div>
                                
                                <!-- Supplies for Receipt 2 -->
                                <div class="receipt-section">
                                    <h6><i class="bi bi-gear"></i>Vật tư y tế</h6>
                                    <div class="item-headers">
                                        <div class="row">
                                            <div class="col-md-5">Tên vật tư</div>
                                            <div class="col-md-2 text-center">Số lượng</div>
                                            <div class="col-md-2 text-center">Đơn giá</div>
                                            <div class="col-md-2 text-center">Thành tiền</div>
                                            <div class="col-md-1 text-center">Xóa</div>
                                </div>
                                    </div>
                                    <div id="receipt2_suppliesContainer"></div>
                                    <button type="button" class="btn btn-add-item" onclick="addItemRow('receipt2', 'supply')">
                                        <i class="bi bi-plus-circle"></i>Thêm vật tư
                                </button>
                            </div>
                            
                                <!-- Medicines for Receipt 2 -->
                                <div class="receipt-section">
                                    <h6><i class="bi bi-capsule"></i>Thuốc</h6>
                                <div class="item-headers">
                                    <div class="row">
                                        <div class="col-md-5">Tên thuốc</div>
                                        <div class="col-md-2 text-center">Số lượng</div>
                                        <div class="col-md-2 text-center">Đơn giá</div>
                                        <div class="col-md-2 text-center">Thành tiền</div>
                                        <div class="col-md-1 text-center">Xóa</div>
                                        </div>
                                    </div>
                                    <div id="receipt2_medicinesContainer"></div>
                                    <button type="button" class="btn btn-add-item" onclick="addItemRow('receipt2', 'medicine')">
                                        <i class="bi bi-plus-circle"></i>Thêm thuốc
                                    </button>
                                    </div>
                                </div>
                                
                            <div class="receipt-totals">
                                <h6>Tổng cộng phiếu thu 2</h6>
                                <div class="row">
                                    <div class="col-md-4">
                                        <label class="form-label">Dịch vụ</label>
                                        <input type="text" class="form-control" id="receipt2_totalServices" readonly>
                                </div>
                                    <div class="col-md-4">
                                        <label class="form-label">Vật tư & Thuốc</label>
                                        <input type="text" class="form-control" id="receipt2_totalSupplies" readonly>
                                    </div>
                                    <div class="col-md-4">
                                        <label class="form-label fw-bold">Tổng phiếu thu 2</label>
                                        <input type="text" class="form-control fw-bold" id="receipt2_total" readonly>
                                    </div>
                                </div>
                            </div>
                            </div>
                            
                        <!-- Tổng cộng hóa đơn -->
                        <div class="total-section">
                            <h6><i class="bi bi-calculator me-2"></i>Tổng cộng hóa đơn</h6>
                                <div class="row">
                                    <div class="col-md-3">
                                        <label class="form-label">Tổng dịch vụ</label>
                                        <input type="text" class="form-control" id="totalServices" readonly>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Tổng vật tư & thuốc</label>
                                        <input type="text" class="form-control" id="totalSupplies" readonly>
                                    </div>
                                <div class="col-md-2">
                                        <label class="form-label">Giảm giá</label>
                                        <input type="number" class="form-control" id="discountAmount" name="discountAmount" 
                                               value="<%= isEdit && invoice != null && invoice.getDiscountAmount() != null ? invoice.getDiscountAmount().toString() : "0" %>" 
                                               min="0" onchange="calculateTotal()">
                                    </div>
                                <div class="col-md-4">
                                        <label class="form-label fw-bold">Thành tiền</label>
                                    <input type="text" class="form-control fw-bold text-primary fs-5" id="finalAmount" readonly>
                                </div>
                            </div>
                            
                            <div class="row mt-3">
                                <div class="col-md-12">
                                        <label class="form-label">Ghi chú</label>
                                    <textarea class="form-control" name="notes" rows="2" placeholder="Ghi chú thêm về hóa đơn..."><%= isEdit && invoice != null && invoice.getNotes() != null ? invoice.getNotes() : "" %></textarea>
                                    </div>
                                </div>
                            </div>

                            <!-- Form Buttons -->
                        <div class="form-buttons mt-4 text-center">
                            <button type="submit" class="btn btn-primary btn-lg me-3">
                                    <i class="bi bi-check-circle me-2"></i>
                                <%= isEdit ? "Cập nhật hóa đơn" : "Tạo hóa đơn" %>
                                </button>
                            <% if (isEdit && invoice != null) { %>
                                <a href="${pageContext.request.contextPath}/doctor/invoices?action=view&invoiceId=<%= invoice.getInvoiceId() %>" 
                                   class="btn btn-secondary btn-lg">
                                        <i class="bi bi-x-circle me-2"></i>Hủy
                                    </a>
                                <% } else { %>
                                <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%= patient != null ? patient.getId() : "" %>" 
                                   class="btn btn-secondary btn-lg">
                                        <i class="bi bi-x-circle me-2"></i>Hủy
                                    </a>
                                <% } %>
                    </div>
                </form>
                </div>
            </div>
        </div>

        <!-- Scripts -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/invoice-existing-items.js"></script>
        
        <script>
            // Data for JavaScript
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
            
            // Existing items for edit mode
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
                // Initialize sidebar
                initializeSidebar();
                
                // Initialize second receipt
                if (hasSecondReceipt) {
                    document.getElementById('enableSecondReceipt').checked = true;
                    toggleSecondReceipt();
                } else {
                    toggleSecondReceipt();
                }
                
                // Load existing items
                loadExistingItems();
                
                // Calculate totals
                calculateTotal();
            });
            
            function initializeSidebar() {
                const sidebarCollapse = document.getElementById('sidebarCollapse');
                const sidebar = document.getElementById('sidebar');
                const content = document.getElementById('content');

                sidebarCollapse.addEventListener('click', function() {
                    sidebar.classList.toggle('collapsed');
                    content.classList.toggle('expanded');
                });

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
            }
            
            function toggleSecondReceipt() {
                const checkbox = document.getElementById('enableSecondReceipt');
                const receipt2 = document.getElementById('receipt2');
                const receipt2Sections = document.getElementById('receipt2_sections');
                
                if (checkbox.checked) {
                    receipt2.classList.remove('disabled');
                    receipt2Sections.style.display = 'block';
                } else {
                    receipt2.classList.add('disabled');
                    receipt2Sections.style.display = 'none';
                    
                    // Clear all items in receipt 2
                    clearReceiptItems('receipt2');
                }
                
                calculateTotal();
            }
            
            function clearReceiptItems(receiptId) {
                ['service', 'supply', 'medicine'].forEach(itemType => {
                    const container = document.getElementById(receiptId + '_' + getContainerSuffix(itemType) + 'Container');
                    if (container) {
                        container.innerHTML = '';
                    }
                });
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
                
                if (itemType === 'service') {
                    itemsData = servicesData;
                } else if (itemType === 'supply') {
                    itemsData = suppliesData;
                } else if (itemType === 'medicine') {
                    itemsData = medicinesData;
                }
                
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
                        '<div class="col-md-5">' +
                            '<select class="form-select" name="' + receiptId + '_' + itemType + 'Id" ' +
                                   'onchange="updateItemPrice(\'' + rowId + '\', \'' + itemType + '\')" required>' +
                                '<option value="">-- Chọn ' + getItemTypeLabel(itemType) + ' --</option>' +
                                itemOptions +
                            '</select>' +
                            '<input type="hidden" name="' + receiptId + '_' + itemType + 'Name_' + itemId + '" id="' + rowId + '_name">' +
                            '<input type="hidden" name="' + receiptId + '_' + itemType + 'Price_' + itemId + '" id="' + rowId + '_price">' +
                        '</div>' +
                        '<div class="col-md-2">' +
                            '<input type="number" class="form-control" name="' + receiptId + '_' + itemType + 'Quantity" ' +
                                   'value="' + quantity + '" min="1" onchange="calculateRowTotal(\'' + rowId + '\', \'' + itemType + '\')" required>' +
                            '<small class="text-info" id="' + rowId + '_stock"></small>' +
                        '</div>' +
                        '<div class="col-md-2">' +
                            '<input type="text" class="form-control" id="' + rowId + '_unitPrice" readonly>' +
                        '</div>' +
                        '<div class="col-md-2">' +
                            '<input type="text" class="form-control" id="' + rowId + '_total" readonly>' +
                        '</div>' +
                        '<div class="col-md-1 text-center">' +
                            '<button type="button" class="btn btn-delete-item" onclick="removeRow(\'' + rowId + '\')">' +
                                '<i class="bi bi-trash"></i>' +
                            '</button>' +
                        '</div>' +
                    '</div>';
                
                container.appendChild(row);
                
                // Update price if itemId is provided
                if (itemId) {
                    const select = row.querySelector('select');
                    updateItemPrice(rowId, itemType, select);
                }
            }
            
            function updateItemPrice(rowId, itemType, selectElement = null) {
                const select = selectElement || document.querySelector('#' + rowId + ' select');
                const selectedOption = select.options[select.selectedIndex];
                
                if (selectedOption.value) {
                    const price = parseFloat(selectedOption.dataset.price);
                    const name = selectedOption.text;
                    const itemId = selectedOption.value;
                    
                    document.getElementById(rowId + '_unitPrice').value = formatCurrency(price);
                    document.getElementById(rowId + '_name').value = name;
                    document.getElementById(rowId + '_name').name = rowId.split('_')[0] + '_' + itemType + 'Name_' + itemId;
                    document.getElementById(rowId + '_price').value = price;
                    document.getElementById(rowId + '_price').name = rowId.split('_')[0] + '_' + itemType + 'Price_' + itemId;
                    
                    // Handle stock display for supplies and medicines
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
                        } else {
                    document.getElementById(rowId + '_unitPrice').value = '';
                    document.getElementById(rowId + '_total').value = '';
                    const stockDisplay = document.getElementById(rowId + '_stock');
                    if (stockDisplay) {
                        stockDisplay.textContent = '';
                    }
                    calculateTotal();
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
                
                    // Validate stock for supplies and medicines
                    if (itemType === 'supply' || itemType === 'medicine') {
                        validateStock(rowId, itemType);
                    }
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
                            stockDisplay.textContent = 'KHÔNG ĐỦ! Tồn kho: ' + stock + ', Yêu cầu: ' + requestedQuantity;
                            stockDisplay.className = 'text-danger fw-bold';
                            quantityInput.classList.add('is-invalid');
                        } else if (requestedQuantity > 0) {
                            stockDisplay.textContent = 'OK - Tồn kho: ' + stock + ', Dùng: ' + requestedQuantity;
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

            function removeRow(rowId) {
                const row = document.getElementById(rowId);
                if (row) {
                    row.remove();
                    calculateTotal();
                }
            }
            
            function calculateTotal() {
                // Calculate receipt 1 totals
                const receipt1ServiceTotal = calculateReceiptTotal('receipt1', 'service');
                const receipt1SupplyTotal = calculateReceiptTotal('receipt1', 'supply');
                const receipt1MedicineTotal = calculateReceiptTotal('receipt1', 'medicine');
                const receipt1Total = receipt1ServiceTotal + receipt1SupplyTotal + receipt1MedicineTotal;
                
                document.getElementById('receipt1_totalServices').value = formatCurrency(receipt1ServiceTotal);
                document.getElementById('receipt1_totalSupplies').value = formatCurrency(receipt1SupplyTotal + receipt1MedicineTotal);
                document.getElementById('receipt1_total').value = formatCurrency(receipt1Total);
                
                // Calculate receipt 2 totals (if enabled)
                let receipt2ServiceTotal = 0;
                let receipt2SupplyTotal = 0;
                let receipt2MedicineTotal = 0;
                let receipt2Total = 0;
                
                if (document.getElementById('enableSecondReceipt').checked) {
                    receipt2ServiceTotal = calculateReceiptTotal('receipt2', 'service');
                    receipt2SupplyTotal = calculateReceiptTotal('receipt2', 'supply');
                    receipt2MedicineTotal = calculateReceiptTotal('receipt2', 'medicine');
                    receipt2Total = receipt2ServiceTotal + receipt2SupplyTotal + receipt2MedicineTotal;
                }
                
                document.getElementById('receipt2_totalServices').value = formatCurrency(receipt2ServiceTotal);
                document.getElementById('receipt2_totalSupplies').value = formatCurrency(receipt2SupplyTotal + receipt2MedicineTotal);
                document.getElementById('receipt2_total').value = formatCurrency(receipt2Total);
                
                // Calculate grand totals
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
                switch (itemType) {
                    case 'service': return 'dịch vụ';
                    case 'supply': return 'vật tư';
                    case 'medicine': return 'thuốc';
                    default: return '';
                }
            }
            
            function getContainerSuffix(itemType) {
                return itemType === 'supply' ? 'supplies' : itemType + 's';
            }
            
            function loadExistingItems() {
                // Load existing items for receipt 1
                existingFirstReceiptItems.forEach(item => {
                    addItemRow('receipt1', item.type, item.id, item.quantity);
                });
                
                // Load existing items for receipt 2 (if any)
                if (existingSecondReceiptItems.length > 0) {
                    existingSecondReceiptItems.forEach(item => {
                        addItemRow('receipt2', item.type, item.id, item.quantity);
                    });
                }
            }
        </script>
    </body>
</html> 