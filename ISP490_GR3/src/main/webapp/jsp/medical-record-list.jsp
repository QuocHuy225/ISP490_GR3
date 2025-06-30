<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.Patient" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalRecord" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hồ sơ bệnh án - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Homepage specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <style>
            /* Custom Action Buttons Styling */
            .action-buttons-group {
                display: flex;
                gap: 8px;
                justify-content: center;
                align-items: center;
                flex-wrap: wrap;
            }
            
            .action-btn {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 8px 12px;
                border: none;
                border-radius: 8px;
                text-decoration: none;
                font-size: 13px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.3s ease;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                min-width: 70px;
                justify-content: center;
            }
            
            .action-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0,0,0,0.15);
                text-decoration: none;
            }
            
            .action-btn i {
                font-size: 14px;
            }
            
            .btn-text {
                font-size: 12px;
                white-space: nowrap;
            }
            
            /* Specific button colors */
            .action-btn-view {
                background: linear-gradient(135deg, #17a2b8, #138496);
                color: white;
            }
            .action-btn-view:hover {
                background: linear-gradient(135deg, #138496, #117a8b);
                color: white;
            }
            
            .action-btn-edit {
                background: linear-gradient(135deg, #007bff, #0056b3);
                color: white;
            }
            .action-btn-edit:hover {
                background: linear-gradient(135deg, #0056b3, #004085);
                color: white;
            }
            
            .action-btn-invoice {
                background: linear-gradient(135deg, #28a745, #1e7e34);
                color: white;
            }
            .action-btn-invoice:hover {
                background: linear-gradient(135deg, #1e7e34, #155724);
                color: white;
            }
            
            .action-btn-delete {
                background: linear-gradient(135deg, #dc3545, #bd2130);
                color: white;
                border: 2px solid transparent;
            }
            .action-btn-delete:hover {
                background: linear-gradient(135deg, #bd2130, #a71e2a);
                color: white;
                border-color: #dc3545;
            }
            
            .action-btn-prescription {
                background: linear-gradient(135deg, #6f42c1, #563d7c);
                color: white;
            }
            .action-btn-prescription:hover {
                background: linear-gradient(135deg, #563d7c, #452c63);
                color: white;
            }
            
            /* Responsive design */
            @media (max-width: 768px) {
                .action-buttons-group {
                    gap: 4px;
                }
                
                .action-btn {
                    padding: 6px 8px;
                    min-width: 50px;
                }
                
                .btn-text {
                    display: none;
                }
                
                .action-btn i {
                    font-size: 16px;
                }
            }
            
            /* Enhanced table styling */
            .table tbody tr:hover .action-btn {
                transform: scale(1.05);
            }
            
            /* Loading effect */
            .action-btn:active {
                transform: scale(0.95);
                transition: transform 0.1s;
            }
            
            /* Custom tooltips enhancement */
            .action-btn[title]:hover::after {
                content: attr(title);
                position: absolute;
                background: rgba(0,0,0,0.8);
                color: white;
                padding: 4px 8px;
                border-radius: 4px;
                font-size: 11px;
                z-index: 1000;
                margin-top: -35px;
                margin-left: -20px;
                white-space: nowrap;
            }
        </style>
        <!-- DataTables CSS -->
        <link rel="stylesheet" href="https://cdn.datatables.net/1.13.7/css/dataTables.bootstrap5.min.css">
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
        
        // Get patient and medical records data
        Patient patient = (Patient) request.getAttribute("patient");
        List<MedicalRecord> medicalRecords = (List<MedicalRecord>) request.getAttribute("medicalRecords");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat shortSdf = new SimpleDateFormat("dd/MM/yyyy");
        %>

        <!-- Sidebar -->
        <nav id="sidebar">
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
                                <li class="breadcrumb-item active" aria-current="page">
                                    Hồ sơ bệnh án
                                </li>
                            </ol>
                        </nav>
                        
                        <% if (patient != null) { %>
                        <div class="card bg-light">
                            <div class="card-body">
                                <h4 class="text-primary mb-3">
                                    <i class="bi bi-file-medical me-2"></i>Hồ sơ bệnh án
                                </h4>
                                <div class="row">
                                    <div class="col-md-6">
                                        <p class="mb-2"><strong>Mã bệnh nhân:</strong> <%= patient.getPatientCode() %></p>
                                        <p class="mb-2"><strong>Họ tên:</strong> <%= patient.getFullName() %></p>
                                        <p class="mb-0"><strong>Ngày sinh:</strong> <%= patient.getDob() != null ? shortSdf.format(patient.getDob()) : "" %></p>
                                    </div>
                                    <div class="col-md-6">
                                        <p class="mb-2"><strong>Điện thoại:</strong> <%= patient.getPhone() %></p>
                                        <p class="mb-2"><strong>CCCD:</strong> <%= patient.getCccd() %></p>
                                        <p class="mb-0"><strong>Địa chỉ:</strong> <%= patient.getAddress() %></p>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <% } else { %>
                        <div class="alert alert-danger">
                            <i class="bi bi-exclamation-triangle me-2"></i>
                            Không tìm thấy thông tin bệnh nhân!
                        </div>
                        <% } %>
                    </div>
                </div>

                <!-- Alert Messages -->
                <%
                String success = request.getParameter("success");
                String error = request.getParameter("error");
                %>
                <% if (success != null) { %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle me-2"></i>
                    <% if ("added".equals(success)) { %>
                        Thêm hồ sơ bệnh án thành công!
                    <% } else if ("updated".equals(success)) { %>
                        Cập nhật hồ sơ bệnh án thành công!
                    <% } else if ("status_completed".equals(success)) { %>
                        <strong>Chuyển trạng thái thành công!</strong> Hồ sơ bệnh án đã được chuyển sang trạng thái "Hoàn thành". 
                        Từ giờ chỉ có thể chỉnh sửa trường "Ghi chú".
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <% if ("add_failed".equals(error)) { %>
                        Thêm hồ sơ bệnh án thất bại!
                    <% } else if ("update_failed".equals(error)) { %>
                        Cập nhật hồ sơ bệnh án thất bại!
                    <% } else if ("status_change_not_allowed".equals(error)) { %>
                        <strong>Không thể thay đổi trạng thái!</strong> Hồ sơ đã hoàn thành không thể chuyển về trạng thái "Đang điều trị".
                    <% } else if ("system_error".equals(error)) { %>
                        Có lỗi hệ thống xảy ra!
                    <% } else { %>
                        Có lỗi xảy ra. Vui lòng thử lại!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <!-- Back Button & Add Record Button -->
                <% if (patient != null) { %>
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <a href="${pageContext.request.contextPath}/patients" 
                               class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-left me-2"></i>Quay lại Quản lý bệnh nhân
                            </a>
                        </div>
                        
                        <div class="card">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-table me-2"></i>Danh sách hồ sơ bệnh án
                                    </h5>
                                    <a href="${pageContext.request.contextPath}/medical-records?action=new&patientId=<%= patient.getId() %>" 
                                       class="btn btn-success">
                                        <i class="bi bi-plus-circle me-2"></i>Tạo hồ sơ mới
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Medical Records Table -->
                <div class="row">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-body">
                                <div class="table-responsive">
                                    <% if (medicalRecords != null && !medicalRecords.isEmpty()) { %>
                                        <table id="medicalRecordsTable" class="table table-striped table-hover">
                                            <thead class="table-primary">
                                                <tr>
                                                    <th>Mã hồ sơ</th>
                                                    <th>Ngày tạo</th>
                                                    <th>Chẩn đoán</th>
                                                    <th>Trạng thái</th>
                                                    <th>Thao tác</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <% for (MedicalRecord record : medicalRecords) { %>
                                                    <tr>
                                                        <td><%= record.getId() %></td>
                                                        <td><%= record.getCreatedAt() != null ? sdf.format(record.getCreatedAt()) : "" %></td>
                                                        <td>
                                                            <% if (record.getFinalDiagnosis() != null && !record.getFinalDiagnosis().isEmpty()) { %>
                                                                <%= record.getFinalDiagnosis().length() > 50 ? 
                                                                    record.getFinalDiagnosis().substring(0, 50) + "..." : 
                                                                    record.getFinalDiagnosis() %>
                                                            <% } else { %>
                                                                <em class="text-muted">Chưa có chẩn đoán</em>
                                                            <% } %>
                                                        </td>
                                                        <td>
                                                            <% if ("ongoing".equals(record.getStatus())) { %>
                                                                <span class="badge bg-warning text-dark">
                                                                    <i class="bi bi-clock me-1"></i>Đang điều trị
                                                                </span>
                                                            <% } else { %>
                                                                <span class="badge bg-success">
                                                                    <i class="bi bi-check-circle me-1"></i>Hoàn thành
                                                                </span>
                                                            <% } %>
                                                        </td>
                                                        <td>
                                                            <div class="action-buttons-group">
                                                            <a href="${pageContext.request.contextPath}/medical-records?action=view&recordId=<%= record.getId() %>" 
                                                               class="action-btn action-btn-view" title="Xem & In">
                                                                <i class="bi bi-eye"></i>
                                                                <span class="btn-text">Xem</span>
                                                            </a>
                                                            
                                                            <a href="${pageContext.request.contextPath}/medical-records?action=edit&recordId=<%= record.getId() %>" 
                                                               class="action-btn action-btn-edit" 
                                                               title="<%= "completed".equals(record.getStatus()) ? "Chỉnh sửa ghi chú (hồ sơ đã hoàn thành)" : "Chỉnh sửa" %>">
                                                                <i class="bi bi-pencil-square"></i>
                                                                <span class="btn-text"><%= "completed".equals(record.getStatus()) ? "Ghi chú" : "Sửa" %></span>
                                                            </a>
                                                            
                                                            <a href="${pageContext.request.contextPath}/invoices?action=listByMedicalRecord&medicalRecordId=<%= record.getId() %>" 
                                                               class="action-btn action-btn-invoice" title="Quản lý hóa đơn">
                                                                <i class="bi bi-receipt"></i>
                                                                <span class="btn-text">Hóa đơn</span>
                                                            </a>
                                                            
                                                            <a href="${pageContext.request.contextPath}/actual-prescriptions?action=listByMedicalRecord&medicalRecordId=<%= record.getId() %>" 
                                                               class="action-btn action-btn-prescription" title="Quản lý đơn thuốc">
                                                                <i class="bi bi-capsule"></i>
                                                                <span class="btn-text">Đơn thuốc</span>
                                                            </a>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                <% } %>
                                            </tbody>
                                        </table>
                                    <% } else { %>
                                        <div class="text-center py-5">
                                            <i class="bi bi-inbox display-1 text-muted"></i>
                                            <h4 class="text-muted mt-3">Chưa có hồ sơ bệnh án nào</h4>
                                            <p class="text-muted">Bắt đầu bằng cách tạo hồ sơ bệnh án đầu tiên cho bệnh nhân này.</p>
                                        </div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
        </div>

        <!-- Bootstrap Bundle with Popper -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <!-- jQuery -->
        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
        <!-- DataTables JS -->
        <script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
        <script src="https://cdn.datatables.net/1.13.7/js/dataTables.bootstrap5.min.js"></script>
        
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

                // Listen for window resize
                window.addEventListener('resize', checkWidth);

                // Initialize DataTable only if table exists
                if ($('#medicalRecordsTable').length > 0) {
                    $('#medicalRecordsTable').DataTable({
                        "searching": false,
                        "ordering": true,
                        "columnDefs": [
                            { "orderable": false, "targets": [4] }
                        ],
                        "language": {
                            "lengthMenu": "Hiển thị _MENU_ mục",
                            "zeroRecords": "Không tìm thấy dữ liệu",
                            "info": "Hiển thị _START_ đến _END_ của _TOTAL_ mục",
                            "infoEmpty": "Hiển thị 0 đến 0 của 0 mục",
                            "infoFiltered": "(lọc từ _MAX_ tổng số mục)",
                            "paginate": {
                                "first": "Đầu",
                                "last": "Cuối",
                                "next": "Tiếp",
                                "previous": "Trước"
                            }
                        }
                    });
                }
            });
        </script>
    </body>
</html>