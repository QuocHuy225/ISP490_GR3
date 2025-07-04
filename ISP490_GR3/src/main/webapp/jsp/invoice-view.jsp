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
                                    <a href="${pageContext.request.contextPath}/doctor/medical-records?action=list&patientId=<%= patient.getId() %>">
                                        Hồ sơ bệnh án
                                    </a>
                                </li>
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/doctor/invoices?action=listByMedicalRecord&medicalRecordId=<%= medicalRecord.getId() %>">
                                        Quản lý hóa đơn
                                    </a>
                                </li>
                                <% } %>
                                <li class="breadcrumb-item active" aria-current="page">
                                    Chi tiết hóa đơn
                                </li>
                            </ol>
                        </nav>
                    </div>
                </div>

                <!-- Back Button & Actions -->
                <div class="row mb-4 no-print">
                    <div class="col-12">
                        <% if (invoice != null) { %>
                        <div class="d-flex justify-content-start align-items-center mb-3">
                            <a href="${pageContext.request.contextPath}/doctor/invoices?action=listByMedicalRecord&medicalRecordId=<%= invoice.getMedicalRecordId() %>" 
                               class="btn btn-outline-secondary me-3">
                                <i class="bi bi-arrow-left me-2"></i>Quay lại Danh sách hóa đơn
                            </a>
                            <h4 class="mb-0 text-primary">Chi tiết hóa đơn</h4>
                        </div>
                        <% } %>
                        
                        <div class="d-flex justify-content-end align-items-center">
                            <div class="btn-group">
                                <button type="button" class="btn btn-info" onclick="window.print()">
                                    <i class="bi bi-printer me-2"></i>In hóa đơn
                                </button>
                                <% if (invoice != null) { %>
                                <a href="${pageContext.request.contextPath}/doctor/invoices?action=edit&invoiceId=<%= invoice.getInvoiceId() %>" 
                                   class="btn btn-primary">
                                    <i class="bi bi-pencil-square me-2"></i>Chỉnh sửa
                                </a>
                                <% } %>
                            </div>
                        </div>
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

                <!-- Patient & Invoice Info -->
                <div class="invoice-section invoice-details">
                    <div class="row">
                        <div class="col-md-6">
                            <h6 class="text-primary mb-3"><i class="bi bi-person-fill me-2"></i>Thông tin bệnh nhân</h6>
                            <p class="mb-2"><strong>Mã bệnh nhân:</strong> <%= patient.getPatientCode() %></p>
                            <p class="mb-2"><strong>Họ tên:</strong> <%= patient.getFullName() %></p>
                            <p class="mb-2"><strong>Ngày sinh:</strong> <%= patient.getDob() != null ? shortSdf.format(patient.getDob()) : "" %></p>
                            <p class="mb-2"><strong>Điện thoại:</strong> <%= patient.getPhone() %></p>
                            <p class="mb-0"><strong>Địa chỉ:</strong> <%= patient.getAddress() %></p>
                        </div>
                        <div class="col-md-6">
                            <h6 class="text-primary mb-3"><i class="bi bi-receipt me-2"></i>Thông tin hóa đơn</h6>
                            <p class="mb-2"><strong>Mã hóa đơn:</strong> <%= invoice.getInvoiceId() %></p>
                            <p class="mb-2"><strong>Mã hồ sơ:</strong> <%= invoice.getMedicalRecordId() %></p>
                            <p class="mb-2"><strong>Ngày tạo:</strong> <%= invoice.getCreatedAt() != null ? sdf.format(invoice.getCreatedAt()) : "" %></p>
                        </div>
                    </div>
                </div>

                <!-- Invoice Items -->
                <div class="invoice-section invoice-details">
                    <h6 class="text-primary mb-3"><i class="bi bi-list-ul me-2"></i>Chi tiết dịch vụ</h6>
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
                                int itemIndex = 1;
                                if (invoice.getInvoiceItems() != null && !invoice.getInvoiceItems().isEmpty()) {
                                    for (InvoiceItem item : invoice.getInvoiceItems()) { %>
                                <tr>
                                    <td><%= itemIndex++ %></td>
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
                                <% } 
                                } %>
                                <!-- Exam Fee Row -->
                                <tr class="table-warning">
                                    <td><%= itemIndex %></td>
                                    <td>Phí khám bệnh</td>
                                    <td><span class="badge bg-warning text-dark">Khám bệnh</span></td>
                                    <td class="text-center">1</td>
                                    <td class="text-end">100,000đ</td>
                                    <td class="text-end">100,000đ</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Total Section -->
                <div class="invoice-section total-section">
                    <h6 class="text-primary mb-3"><i class="bi bi-calculator me-2"></i>Tổng cộng</h6>
                    <div class="row">
                        <div class="col-md-8">
                            <% if (invoice.getNotes() != null && !invoice.getNotes().isEmpty()) { %>
                            <div class="mb-3">
                                <strong>Ghi chú:</strong><br>
                                <%= invoice.getNotes() %>
                            </div>
                            <% } %>
                        </div>
                        <div class="col-md-4">
                            <table class="table table-sm">
                                <tr>
                                    <td><strong>Tổng dịch vụ:</strong></td>
                                    <td class="text-end"><%= currencyFormat.format(invoice.getTotalServiceAmount()) %>đ</td>
                                </tr>
                                <tr>
                                    <td><strong>Tổng vật tư & thuốc:</strong></td>
                                    <td class="text-end"><%= currencyFormat.format(invoice.getTotalSupplyAmount()) %>đ</td>
                                </tr>
                                <tr>
                                    <td><strong>Phí khám:</strong></td>
                                    <td class="text-end">100,000đ</td>
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
                    </div>
                </div>

                <!-- Signature Section -->
                <div class="invoice-section print-section">
                    <div class="row text-center">
                        <div class="col-md-4">
                            <p class="mb-5"><strong>Người thanh toán</strong></p>
                            <p>________________________</p>
                            <p class="mb-0"><em>(Ký và ghi rõ họ tên)</em></p>
                        </div>
                        <div class="col-md-4">
                            <p class="mb-5"><strong>Thu ngân</strong></p>
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
                        <p class="mb-0"><strong>Cảm ơn quý khách đã sử dụng dịch vụ!</strong></p>
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