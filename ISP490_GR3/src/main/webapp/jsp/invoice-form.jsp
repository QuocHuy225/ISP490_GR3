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
            /* Enhanced Invoice Form Styling */
            .invoice-form-container {
                background: linear-gradient(135deg, #f8f9ff 0%, #ffffff 100%);
                border-radius: 1rem;
                box-shadow: 0 8px 32px rgba(0,0,0,0.1);
                padding: 2rem;
                margin-bottom: 2rem;
            }

            .invoice-section {
                background: white;
                border: none;
                border-radius: 1rem;
                padding: 2rem;
                margin-bottom: 2rem;
                box-shadow: 0 4px 20px rgba(0,0,0,0.08);
                transition: all 0.3s ease;
                position: relative;
                overflow: hidden;
            }

            .invoice-section::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                height: 4px;
                background: linear-gradient(90deg, #007bff, #0056b3);
            }

            .invoice-section:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 30px rgba(0,0,0,0.12);
            }

            .invoice-section h6 {
                color: #2c3e50;
                font-weight: 700;
                font-size: 1.1rem;
                margin-bottom: 1.5rem;
                display: flex;
                align-items: center;
                padding-bottom: 0.75rem;
                border-bottom: 1px solid #e9ecef;
            }

            .invoice-section h6 i {
                background: linear-gradient(135deg, #007bff, #0056b3);
                color: white;
                padding: 0.5rem;
                border-radius: 0.5rem;
                margin-right: 0.75rem;
                font-size: 1rem;
            }

            /* Service Section - Blue Theme */
            .invoice-section:nth-child(1)::before {
                background: linear-gradient(90deg, #007bff, #0056b3);
            }
            .invoice-section:nth-child(1) h6 i {
                background: linear-gradient(135deg, #007bff, #0056b3);
            }

            /* Supply Section - Green Theme */
            .invoice-section:nth-child(2)::before {
                background: linear-gradient(90deg, #28a745, #20c997);
            }
            .invoice-section:nth-child(2) h6 i {
                background: linear-gradient(135deg, #28a745, #20c997);
            }

            /* Medicine Section - Purple Theme */
            .invoice-section:nth-child(3)::before {
                background: linear-gradient(90deg, #6f42c1, #e83e8c);
            }
            .invoice-section:nth-child(3) h6 i {
                background: linear-gradient(135deg, #6f42c1, #e83e8c);
            }

            /* Enhanced Item Rows */
            .item-row {
                background: #f8f9fa;
                border: 2px solid #e9ecef;
                border-radius: 0.75rem;
                padding: 1.5rem;
                margin-bottom: 1rem;
                transition: all 0.3s ease;
                position: relative;
                overflow: hidden;
            }

            .item-row::before {
                content: '';
                position: absolute;
                left: 0;
                top: 0;
                bottom: 0;
                width: 4px;
                background: linear-gradient(180deg, #007bff, #0056b3);
                opacity: 0;
                transition: opacity 0.3s ease;
            }

            .item-row:hover {
                background: white;
                border-color: #007bff;
                box-shadow: 0 4px 15px rgba(0,123,255,0.15);
                transform: translateX(5px);
            }

            .item-row:hover::before {
                opacity: 1;
            }

            /* Enhanced Form Controls */
            .item-row .form-select,
            .item-row .form-control {
                border: 2px solid #e9ecef;
                border-radius: 0.5rem;
                padding: 0.75rem 1rem;
                font-weight: 500;
                transition: all 0.3s ease;
            }

            .item-row .form-select:focus,
            .item-row .form-control:focus {
                border-color: #007bff;
                box-shadow: 0 0 0 0.2rem rgba(0,123,255,0.25);
                background-color: #f8f9ff;
            }

            /* Column Headers */
            .item-headers {
                background: linear-gradient(135deg, #f8f9fa, #e9ecef);
                border-radius: 0.5rem;
                padding: 1rem;
                margin-bottom: 1rem;
                font-weight: 600;
                color: #495057;
                border: 1px solid #dee2e6;
            }

            /* Enhanced Buttons */
            .btn-add-item {
                background: linear-gradient(135deg, #007bff, #0056b3);
                border: none;
                color: white;
                padding: 0.75rem 1.5rem;
                border-radius: 0.75rem;
                font-weight: 600;
                transition: all 0.3s ease;
                box-shadow: 0 4px 15px rgba(0,123,255,0.3);
            }

            .btn-add-item:hover {
                background: linear-gradient(135deg, #0056b3, #004085);
                transform: translateY(-2px);
                box-shadow: 0 6px 20px rgba(0,123,255,0.4);
                color: white;
            }

            .btn-add-item i {
                margin-right: 0.5rem;
            }

            /* Delete Button Enhancement */
            .btn-delete-item {
                background: linear-gradient(135deg, #dc3545, #c82333);
                border: none;
                color: white;
                padding: 0.5rem;
                border-radius: 0.5rem;
                transition: all 0.3s ease;
                width: 40px;
                height: 40px;
                display: flex;
                align-items: center;
                justify-content: center;
            }

            .btn-delete-item:hover {
                background: linear-gradient(135deg, #c82333, #bd2130);
                transform: scale(1.1);
                box-shadow: 0 4px 15px rgba(220,53,69,0.4);
            }

            /* Price Display Enhancement */
            .price-display {
                background: linear-gradient(135deg, #e3f2fd, #bbdefb);
                border: 2px solid #2196f3;
                color: #0d47a1;
                font-weight: 700;
                text-align: center;
            }

            .total-display {
                background: linear-gradient(135deg, #fff3e0, #ffcc02);
                border: 2px solid #ff9800;
                color: #e65100;
                font-weight: 700;
                text-align: center;
            }

            /* Special Sections */
            .exam-fee-row {
                background: linear-gradient(135deg, #fff3e0, #ffe0b2);
                border: 2px solid #ff9800;
                border-radius: 1rem;
            }

            .exam-fee-row::before {
                background: linear-gradient(90deg, #ff9800, #f57c00);
            }

            .exam-fee-row h6 i {
                background: linear-gradient(135deg, #ff9800, #f57c00);
            }

            .total-section {
                background: linear-gradient(135deg, #e8f5e8, #c8e6c9);
                border: 2px solid #4caf50;
                border-radius: 1rem;
            }

            .total-section::before {
                background: linear-gradient(90deg, #4caf50, #388e3c);
            }

            .total-section h6 i {
                background: linear-gradient(135deg, #4caf50, #388e3c);
            }

            /* Responsive Improvements */
            @media (max-width: 768px) {
                .invoice-form-container {
                    padding: 1rem;
                }
                
                .invoice-section {
                    padding: 1.5rem;
                }
                
                .item-row {
                    padding: 1rem;
                }
                
                .invoice-section h6 {
                    font-size: 1rem;
                }
            }

            /* Animation Enhancements */
            .item-row.adding {
                animation: slideInFromLeft 0.5s ease-out;
            }

            @keyframes slideInFromLeft {
                from {
                    opacity: 0;
                    transform: translateX(-30px);
                }
                to {
                    opacity: 1;
                    transform: translateX(0);
                }
            }

            /* Enhanced breadcrumb styling */
            .breadcrumb {
                background: linear-gradient(135deg, #f8f9fa, #e9ecef);
                border-radius: 0.75rem;
                padding: 1rem 1.5rem;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            }

            .breadcrumb-item + .breadcrumb-item::before {
                color: #007bff;
                font-weight: bold;
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
                                <% if (patient != null) { %>
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%= patient.getId() %>">
                                        Hồ sơ bệnh án
                                    </a>
                                </li>
                                <% } %>
                                <li class="breadcrumb-item active" aria-current="page">
                                    <%= isEdit ? "Chỉnh sửa hóa đơn" : "Tạo hóa đơn mới" %>
                                </li>
                            </ol>
                        </nav>
                        
                        <% if (patient != null && medicalRecord != null) { %>
                        <div class="card bg-light">
                            <div class="card-body">
                                <h4 class="text-primary mb-3">
                                    <i class="bi bi-<%= isEdit ? "pencil-square" : "plus-circle" %> me-2"></i><%= isEdit ? "Chỉnh sửa hóa đơn" : "Tạo hóa đơn mới" %>
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

                <!-- Alert Messages -->
                <%
                    String error = request.getParameter("error");
                    String success = request.getParameter("success");
                    String message = request.getParameter("message");
                %>
                
                <% if (success != null) { %>
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="bi bi-check-circle-fill me-2"></i>
                            <% if ("added".equals(success)) { %>
                                Tạo hóa đơn thành công!
                            <% } else if ("updated".equals(success)) { %>
                                Cập nhật hóa đơn thành công!
                            <% } else { %>
                                Thao tác thành công!
                            <% } %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </div>
                </div>
                <% } %>
                
                <% if (error != null) { %>
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>
                            <% if ("insufficient_stock".equals(error)) { %>
                                <strong>Không đủ tồn kho!</strong><br>
                                <% if (message != null) { %>
                                    <%= java.net.URLDecoder.decode(message, "UTF-8") %>
                                <% } else { %>
                                    Số lượng vật tư hoặc thuốc trong kho không đủ để thực hiện giao dịch.
                                <% } %>
                            <% } else if ("add_failed".equals(error)) { %>
                                Không thể tạo hóa đơn. Vui lòng thử lại!
                            <% } else if ("update_failed".equals(error)) { %>
                                Không thể cập nhật hóa đơn. Vui lòng thử lại!
                            <% } else if ("invalid_data".equals(error)) { %>
                                Dữ liệu không hợp lệ. Vui lòng kiểm tra lại!
                            <% } else if ("system_error".equals(error)) { %>
                                Lỗi hệ thống. Vui lòng liên hệ quản trị viên!
                            <% } else { %>
                                Có lỗi xảy ra. Vui lòng thử lại!
                            <% } %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </div>
                </div>
                <% } %>

                <!-- Invoice Form -->
                <div class="invoice-form-container">
                <form method="POST" action="${pageContext.request.contextPath}/doctor/invoices" id="invoiceForm">
                    <input type="hidden" name="action" value="<%= isEdit ? "update" : "add" %>">
                    <input type="hidden" name="medicalRecordId" value="<%= medicalRecord != null ? medicalRecord.getId() : "" %>">
                    <input type="hidden" name="patientId" value="<%= patient != null ? patient.getId() : "" %>">
                    <% if (isEdit && invoice != null) { %>
                    <input type="hidden" name="invoiceId" value="<%= invoice.getInvoiceId() %>">
                    <% } %>
                    
                    <div class="row">
                        <div class="col-12">
                            <!-- Services Section -->
                            <div class="invoice-section">
                                <h6><i class="bi bi-file-medical me-2"></i>Dịch vụ y tế</h6>
                                
                                <!-- Column Headers -->
                                <div class="item-headers">
                                    <div class="row">
                                        <div class="col-md-5">Tên dịch vụ</div>
                                        <div class="col-md-2 text-center">Số lượng</div>
                                        <div class="col-md-2 text-center">Đơn giá</div>
                                        <div class="col-md-2 text-center">Thành tiền</div>
                                        <div class="col-md-1 text-center">Xóa</div>
                                    </div>
                                </div>
                                
                                <div id="servicesContainer">
                                    <!-- Service items will be added here -->
                                </div>
                                <button type="button" class="btn btn-add-item" onclick="addServiceRow()">
                                    <i class="bi bi-plus-circle"></i> Thêm dịch vụ
                                </button>
                            </div>
                            
                            <!-- Supplies Section -->
                            <div class="invoice-section">
                                <h6><i class="bi bi-gear me-2"></i>Vật tư y tế</h6>
                                
                                <!-- Column Headers -->
                                <div class="item-headers">
                                    <div class="row">
                                        <div class="col-md-5">Tên vật tư</div>
                                        <div class="col-md-2 text-center">Số lượng</div>
                                        <div class="col-md-2 text-center">Đơn giá</div>
                                        <div class="col-md-2 text-center">Thành tiền</div>
                                        <div class="col-md-1 text-center">Xóa</div>
                                    </div>
                                </div>
                                
                                <div id="suppliesContainer">
                                    <!-- Supply items will be added here -->
                                </div>
                                <button type="button" class="btn btn-add-item" onclick="addSupplyRow()">
                                    <i class="bi bi-plus-circle"></i> Thêm vật tư
                                </button>
                            </div>
                            
                            <!-- Medicines Section -->
                            <div class="invoice-section">
                                <h6><i class="bi bi-capsule me-2"></i>Thuốc</h6>
                                
                                <!-- Column Headers -->
                                <div class="item-headers">
                                    <div class="row">
                                        <div class="col-md-5">Tên thuốc</div>
                                        <div class="col-md-2 text-center">Số lượng</div>
                                        <div class="col-md-2 text-center">Đơn giá</div>
                                        <div class="col-md-2 text-center">Thành tiền</div>
                                        <div class="col-md-1 text-center">Xóa</div>
                                    </div>
                                </div>
                                
                                <div id="medicinesContainer">
                                    <!-- Medicine items will be added here -->
                                </div>
                                <button type="button" class="btn btn-add-item" onclick="addMedicineRow()">
                                    <i class="bi bi-plus-circle"></i> Thêm thuốc
                                </button>
                            </div>
                            
                            <!-- Exam Fee Section -->
                            <div class="invoice-section exam-fee-row">
                                <h6><i class="bi bi-stethoscope me-2"></i>Phí khám bệnh</h6>
                                <div class="row">
                                    <div class="col-md-6">
                                        <label class="form-label">Phí khám cố định</label>
                                        <input type="text" class="form-control" value="100,000đ" readonly>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Ghi chú</label>
                                        <input type="text" class="form-control" value="Phí khám bệnh cơ bản" readonly>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Total Section -->
                            <div class="invoice-section total-section">
                                <h6><i class="bi bi-calculator me-2"></i>Tổng cộng</h6>
                                <div class="row">
                                    <div class="col-md-3">
                                        <label class="form-label">Tổng dịch vụ</label>
                                        <input type="text" class="form-control" id="totalServices" readonly>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Tổng vật tư & thuốc</label>
                                        <input type="text" class="form-control" id="totalSupplies" readonly>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Giảm giá</label>
                                        <input type="number" class="form-control" id="discountAmount" name="discountAmount" 
                                               value="<%= isEdit && invoice != null && invoice.getDiscountAmount() != null ? invoice.getDiscountAmount().toString() : "0" %>" 
                                               min="0" onchange="calculateTotal()">
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label fw-bold">Thành tiền</label>
                                        <input type="text" class="form-control fw-bold text-primary" id="finalAmount" readonly>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Payment Info Section -->
                            <div class="invoice-section">
                                <h6><i class="bi bi-credit-card me-2"></i>Ghi chú</h6>
                                <div class="row">
                                    <div class="col-md-4">
                                        <label class="form-label">Ghi chú</label>
                                        <textarea class="form-control" name="notes" rows="1"><%= isEdit && invoice != null && invoice.getNotes() != null ? invoice.getNotes() : "" %></textarea>
                                    </div>
                                </div>
                            </div>

                            <!-- Form Buttons -->
                            <div class="form-buttons mt-4">
                                <button type="submit" class="btn btn-primary me-2">
                                    <i class="bi bi-check-circle me-2"></i>
                                    <%= "add".equals(action) ? "Tạo hóa đơn" : "Cập nhật" %>
                                </button>
                                <% if ("add".equals(action)) { %>
                                    <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%= medicalRecord.getPatientId() %>" 
                                       class="btn btn-secondary">
                                        <i class="bi bi-x-circle me-2"></i>Hủy
                                    </a>
                                <% } else { %>
                                    <a href="${pageContext.request.contextPath}/doctor/invoices?action=view&invoiceId=<%= invoice.getInvoiceId() %>" 
                                       class="btn btn-secondary">
                                        <i class="bi bi-x-circle me-2"></i>Hủy
                                    </a>
                                <% } %>
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
        
        <!-- Data for JavaScript -->
        <script>
            // Convert server data to JavaScript objects
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
            
            <% if (isEdit && invoice != null && invoice.getInvoiceItems() != null) { %>
            const existingItems = [
                <% for (int i = 0; i < invoice.getInvoiceItems().size(); i++) { 
                   InvoiceItem item = invoice.getInvoiceItems().get(i); %>
                {
                    type: '<%= item.getItemType() %>',
                    id: <%= item.getItemId() %>,
                    name: '<%= item.getItemName().replace("'", "\\'") %>',
                    quantity: <%= item.getQuantity() %>,
                    price: <%= item.getUnitPrice() %>
                }<%= i < invoice.getInvoiceItems().size() - 1 ? "," : "" %>
                <% } %>
            ];
            <% } else { %>
            const existingItems = [];
            <% } %>
        </script>
        
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
                window.addEventListener('resize', checkWidth);
                
                // Load existing items if editing
                loadExistingItems();
                
                // Calculate initial total
                calculateTotal();
            });

            let serviceRowIndex = 0;
            let supplyRowIndex = 0;
            let medicineRowIndex = 0;

            function addServiceRow(serviceId = '', quantity = 1) {
                const container = document.getElementById('servicesContainer');
                const rowId = 'service_' + serviceRowIndex++;
                
                const row = document.createElement('div');
                row.className = 'item-row';
                row.id = rowId;
                
                var serviceOptions = '';
                for (var i = 0; i < servicesData.length; i++) {
                    var service = servicesData[i];
                    var selected = (serviceId == service.id) ? 'selected' : '';
                    serviceOptions += '<option value="' + service.id + '" ' + selected + ' data-price="' + service.price + '">' + service.name + '</option>';
                }
                
                row.innerHTML = 
                    '<div class="row align-items-center">' +
                        '<div class="col-md-5">' +
                            '<select class="form-select" name="serviceId" onchange="updateServicePrice(this, \'' + rowId + '\')" required>' +
                                '<option value="">-- Chọn dịch vụ --</option>' +
                                serviceOptions +
                            '</select>' +
                            '<input type="hidden" name="serviceName_' + serviceId + '" id="serviceName_' + rowId + '">' +
                            '<input type="hidden" name="servicePrice_' + serviceId + '" id="servicePrice_' + rowId + '">' +
                        '</div>' +
                        '<div class="col-md-2">' +
                            '<input type="number" class="form-control" name="serviceQuantity" value="' + quantity + '" min="1" ' +
                                   'onchange="calculateRowTotal(\'' + rowId + '\')" required>' +
                        '</div>' +
                        '<div class="col-md-2">' +
                            '<input type="text" class="form-control price-display" id="serviceUnitPrice_' + rowId + '" readonly>' +
                        '</div>' +
                        '<div class="col-md-2">' +
                            '<input type="text" class="form-control total-display" id="serviceTotal_' + rowId + '" readonly>' +
                        '</div>' +
                        '<div class="col-md-1 text-center">' +
                            '<button type="button" class="btn btn-delete-item" onclick="removeRow(\'' + rowId + '\')" title="Xóa dịch vụ">' +
                                '<i class="bi bi-trash"></i>' +
                            '</button>' +
                        '</div>' +
                    '</div>';
                
                row.classList.add('adding');
                container.appendChild(row);
                
                // Trigger price update if serviceId is provided
                if (serviceId) {
                    const select = row.querySelector('select[name="serviceId"]');
                    updateServicePrice(select, rowId);
                }
                
                // Remove animation class after animation completes
                setTimeout(() => {
                    row.classList.remove('adding');
                }, 500);
            }

            function addSupplyRow(supplyId = '', quantity = 1) {
                const container = document.getElementById('suppliesContainer');
                const rowId = 'supply_' + supplyRowIndex++;
                
                const row = document.createElement('div');
                row.className = 'item-row';
                row.id = rowId;
                
                var supplyOptions = '';
                for (var i = 0; i < suppliesData.length; i++) {
                    var supply = suppliesData[i];
                    var selected = (supplyId == supply.id) ? 'selected' : '';
                    var stockInfo = supply.stock ? ' (Còn: ' + supply.stock + ')' : ' (Hết hàng)';
                    var stockClass = supply.stock > 0 ? '' : ' class="text-danger"';
                    supplyOptions += '<option value="' + supply.id + '" ' + selected + ' data-price="' + supply.price + '" data-stock="' + supply.stock + '"' + stockClass + '>' + supply.name + stockInfo + '</option>';
                }
                
                row.innerHTML = 
                    '<div class="row align-items-center">' +
                        '<div class="col-md-5">' +
                            '<select class="form-select" name="supplyId" onchange="updateSupplyPrice(this, \'' + rowId + '\')" required>' +
                                '<option value="">-- Chọn vật tư --</option>' +
                                supplyOptions +
                            '</select>' +
                            '<input type="hidden" name="supplyName_' + supplyId + '" id="supplyName_' + rowId + '">' +
                            '<input type="hidden" name="supplyPrice_' + supplyId + '" id="supplyPrice_' + rowId + '">' +
                        '</div>' +
                        '<div class="col-md-2">' +
                            '<input type="number" class="form-control" name="supplyQuantity" value="' + quantity + '" min="1" ' +
                                   'onchange="validateSupplyStock(\'' + rowId + '\')" required>' +
                            '<small class="text-info" id="supplyStock_' + rowId + '"></small>' +
                        '</div>' +
                        '<div class="col-md-2">' +
                            '<input type="text" class="form-control price-display" id="supplyUnitPrice_' + rowId + '" readonly>' +
                        '</div>' +
                        '<div class="col-md-2">' +
                            '<input type="text" class="form-control total-display" id="supplyTotal_' + rowId + '" readonly>' +
                        '</div>' +
                        '<div class="col-md-1 text-center">' +
                            '<button type="button" class="btn btn-delete-item" onclick="removeRow(\'' + rowId + '\')" title="Xóa vật tư">' +
                                '<i class="bi bi-trash"></i>' +
                            '</button>' +
                        '</div>' +
                    '</div>';
                
                container.appendChild(row);
                
                // Trigger price update if supplyId is provided
                if (supplyId) {
                    const select = row.querySelector('select[name="supplyId"]');
                    updateSupplyPrice(select, rowId);
                }
            }

            function addMedicineRow(medicineId = '', quantity = 1) {
                const container = document.getElementById('medicinesContainer');
                const rowId = 'medicine_' + medicineRowIndex++;
                
                const row = document.createElement('div');
                row.className = 'item-row';
                row.id = rowId;
                
                var medicineOptions = '';
                for (var i = 0; i < medicinesData.length; i++) {
                    var medicine = medicinesData[i];
                    var selected = (medicineId == medicine.id) ? 'selected' : '';
                    var stockInfo = medicine.stock ? ' (Còn: ' + medicine.stock + ')' : ' (Hết hàng)';
                    var stockClass = medicine.stock > 0 ? '' : ' class="text-danger"';
                    medicineOptions += '<option value="' + medicine.id + '" ' + selected + ' data-price="' + medicine.price + '" data-stock="' + medicine.stock + '"' + stockClass + '>' + medicine.name + stockInfo + '</option>';
                }
                
                row.innerHTML = 
                    '<div class="row align-items-center">' +
                        '<div class="col-md-5">' +
                            '<select class="form-select" name="medicineId" onchange="updateMedicinePrice(this, \'' + rowId + '\')" required>' +
                                '<option value="">-- Chọn thuốc --</option>' +
                                medicineOptions +
                            '</select>' +
                            '<input type="hidden" name="medicineName_' + medicineId + '" id="medicineName_' + rowId + '">' +
                            '<input type="hidden" name="medicinePrice_' + medicineId + '" id="medicinePrice_' + rowId + '">' +
                        '</div>' +
                        '<div class="col-md-2">' +
                            '<input type="number" class="form-control" name="medicineQuantity" value="' + quantity + '" min="1" ' +
                                   'onchange="validateMedicineStock(\'' + rowId + '\')" required>' +
                            '<small class="text-info" id="medicineStock_' + rowId + '"></small>' +
                        '</div>' +
                        '<div class="col-md-2">' +
                            '<input type="text" class="form-control price-display" id="medicineUnitPrice_' + rowId + '" readonly>' +
                        '</div>' +
                        '<div class="col-md-2">' +
                            '<input type="text" class="form-control total-display" id="medicineTotal_' + rowId + '" readonly>' +
                        '</div>' +
                        '<div class="col-md-1 text-center">' +
                            '<button type="button" class="btn btn-delete-item" onclick="removeRow(\'' + rowId + '\')" title="Xóa thuốc">' +
                                '<i class="bi bi-trash"></i>' +
                            '</button>' +
                        '</div>' +
                    '</div>';
                
                container.appendChild(row);
                
                // Trigger price update if medicineId is provided
                if (medicineId) {
                    const select = row.querySelector('select[name="medicineId"]');
                    updateMedicinePrice(select, rowId);
                }
            }

            function updateServicePrice(select, rowId) {
                const selectedOption = select.options[select.selectedIndex];
                if (selectedOption.value) {
                    const price = parseFloat(selectedOption.dataset.price);
                    const name = selectedOption.text;
                    const serviceId = selectedOption.value;
                    
                    document.getElementById('serviceUnitPrice_' + rowId).value = formatCurrency(price);
                    document.getElementById('serviceName_' + rowId).value = name;
                    document.getElementById('serviceName_' + rowId).name = 'serviceName_' + serviceId;
                    document.getElementById('servicePrice_' + rowId).value = price;
                    document.getElementById('servicePrice_' + rowId).name = 'servicePrice_' + serviceId;
                    
                    calculateRowTotal(rowId);
                } else {
                    document.getElementById('serviceUnitPrice_' + rowId).value = '';
                    document.getElementById('serviceTotal_' + rowId).value = '';
                    calculateTotal();
                }
            }

            function updateSupplyPrice(select, rowId) {
                const selectedOption = select.options[select.selectedIndex];
                if (selectedOption.value) {
                    const price = parseFloat(selectedOption.dataset.price);
                    const stock = parseInt(selectedOption.dataset.stock);
                    const name = selectedOption.text;
                    const supplyId = selectedOption.value;
                    
                    document.getElementById('supplyUnitPrice_' + rowId).value = formatCurrency(price);
                    document.getElementById('supplyName_' + rowId).value = name;
                    document.getElementById('supplyName_' + rowId).name = 'supplyName_' + supplyId;
                    document.getElementById('supplyPrice_' + rowId).value = price;
                    document.getElementById('supplyPrice_' + rowId).name = 'supplyPrice_' + supplyId;
                    
                    // Hiển thị thông tin tồn kho
                    const stockDisplay = document.getElementById('supplyStock_' + rowId);
                    if (stockDisplay) {
                        if (stock > 0) {
                            stockDisplay.textContent = 'Tồn kho: ' + stock;
                            stockDisplay.className = 'text-info';
                        } else {
                            stockDisplay.textContent = 'Hết hàng';
                            stockDisplay.className = 'text-danger';
                        }
                    }
                    
                    validateSupplyStock(rowId);
                } else {
                    document.getElementById('supplyUnitPrice_' + rowId).value = '';
                    document.getElementById('supplyTotal_' + rowId).value = '';
                    const stockDisplay = document.getElementById('supplyStock_' + rowId);
                    if (stockDisplay) {
                        stockDisplay.textContent = '';
                    }
                    calculateTotal();
                }
            }

            function updateMedicinePrice(select, rowId) {
                const selectedOption = select.options[select.selectedIndex];
                if (selectedOption.value) {
                    const price = parseFloat(selectedOption.dataset.price);
                    const stock = parseInt(selectedOption.dataset.stock);
                    const name = selectedOption.text;
                    const medicineId = selectedOption.value;
                    
                    document.getElementById('medicineUnitPrice_' + rowId).value = formatCurrency(price);
                    document.getElementById('medicineName_' + rowId).value = name;
                    document.getElementById('medicineName_' + rowId).name = 'medicineName_' + medicineId;
                    document.getElementById('medicinePrice_' + rowId).value = price;
                    document.getElementById('medicinePrice_' + rowId).name = 'medicinePrice_' + medicineId;
                    
                    // Hiển thị thông tin tồn kho
                    const stockDisplay = document.getElementById('medicineStock_' + rowId);
                    if (stockDisplay) {
                        if (stock > 0) {
                            stockDisplay.textContent = 'Tồn kho: ' + stock;
                            stockDisplay.className = 'text-info';
                        } else {
                            stockDisplay.textContent = 'Hết hàng';
                            stockDisplay.className = 'text-danger';
                        }
                    }
                    
                    validateMedicineStock(rowId);
                } else {
                    document.getElementById('medicineUnitPrice_' + rowId).value = '';
                    document.getElementById('medicineTotal_' + rowId).value = '';
                    const stockDisplay = document.getElementById('medicineStock_' + rowId);
                    if (stockDisplay) {
                        stockDisplay.textContent = '';
                    }
                    calculateTotal();
                }
            }

            function calculateRowTotal(rowId) {
                const row = document.getElementById(rowId);
                const quantityInput = row.querySelector('input[name$="Quantity"]');
                const unitPriceInput = row.querySelector('input[id$="UnitPrice_' + rowId + '"]');
                const totalInput = row.querySelector('input[id$="Total_' + rowId + '"]');
                
                if (quantityInput && unitPriceInput && totalInput) {
                    const quantity = parseInt(quantityInput.value) || 0;
                    const unitPrice = parseFloat(unitPriceInput.value.replace(/[,.đ\s]/g, '')) || 0;
                    const total = quantity * unitPrice;
                    
                    totalInput.value = formatCurrency(total);
                }
                
                calculateTotal();
            }

            function removeRow(rowId) {
                const row = document.getElementById(rowId);
                if (row) {
                    row.remove();
                    calculateTotal();
                }
            }

            function calculateTotal() {
                let totalServices = 0;
                let totalSupplies = 0;
                
                // Calculate services total
                document.querySelectorAll('input[id^="serviceTotal_"]').forEach(input => {
                    const value = parseFloat(input.value.replace(/[,.đ\s]/g, '')) || 0;
                    totalServices += value;
                });
                
                // Calculate supplies total
                document.querySelectorAll('input[id^="supplyTotal_"]').forEach(input => {
                    const value = parseFloat(input.value.replace(/[,.đ\s]/g, '')) || 0;
                    totalSupplies += value;
                });
                
                // Calculate medicines total
                document.querySelectorAll('input[id^="medicineTotal_"]').forEach(input => {
                    const value = parseFloat(input.value.replace(/[,.đ\s]/g, '')) || 0;
                    totalSupplies += value;
                });
                
                // Add exam fee (100,000)
                const examFee = 100000;
                const discount = parseFloat(document.getElementById('discountAmount').value) || 0;
                const finalAmount = totalServices + totalSupplies + examFee - discount;
                
                // Update display
                document.getElementById('totalServices').value = formatCurrency(totalServices);
                document.getElementById('totalSupplies').value = formatCurrency(totalSupplies);
                document.getElementById('finalAmount').value = formatCurrency(finalAmount);
            }

            function formatCurrency(amount) {
                return new Intl.NumberFormat('vi-VN').format(amount) + 'đ';
            }

            function loadExistingItems() {
                existingItems.forEach(item => {
                    if (item.type === 'service') {
                        addServiceRow(item.id, item.quantity);
                    } else if (item.type === 'supply') {
                        addSupplyRow(item.id, item.quantity);
                    } else if (item.type === 'medicine') {
                        addMedicineRow(item.id, item.quantity);
                    }
                });
            }

            // Validation functions for stock checking
            function validateSupplyStock(rowId) {
                const row = document.getElementById(rowId);
                const select = row.querySelector('select[name="supplyId"]');
                const quantityInput = row.querySelector('input[name="supplyQuantity"]');
                const stockDisplay = document.getElementById('supplyStock_' + rowId);
                
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
                
                calculateRowTotal(rowId);
            }

            function validateMedicineStock(rowId) {
                const row = document.getElementById(rowId);
                const select = row.querySelector('select[name="medicineId"]');
                const quantityInput = row.querySelector('input[name="medicineQuantity"]');
                const stockDisplay = document.getElementById('medicineStock_' + rowId);
                
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
                
                calculateRowTotal(rowId);
            }
        </script>
    </body>
</html> 