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
                align-items: center;
                flex-wrap: nowrap; /* Prevent buttons from wrapping */
                justify-content: flex-start; /* Align buttons to the left */
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

            /* Button colors */
            .action-btn-invoice {
                background: linear-gradient(135deg, #f1c40f, #e67e22); /* Yellow/gold gradient */
                color: white;
            }
            .action-btn-invoice:hover {
                background: linear-gradient(135deg, #d4ac0d, #cb6d1f);
                color: white;
            }

            .action-btn-prescription {
                background: linear-gradient(135deg, #007bff, #0056b3); /* Blue gradient */
                color: white;
            }
            .action-btn-prescription:hover {
                background: linear-gradient(135deg, #0056b3, #004085);
                color: white;
            }

            /* New Back Button Color */
            .back-btn {
                background: linear-gradient(135deg, #6c757d, #5a6268);
                color: white;
                border: none;
                border-radius: 8px;
                padding: 8px 15px;
                text-decoration: none;
                transition: all 0.3s ease;
            }
            .back-btn:hover {
                background: linear-gradient(135deg, #5a6268, #4e555b);
                color: white;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0,0,0,0.15);
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

                .back-btn {
                    padding: 6px 10px;
                    font-size: 12px;
                }
            }

            .badge {
                font-size: 0.95rem;
                padding: 6px 10px;
                font-weight: 600;
            }

            /* Enhanced table styling */
            .table tbody tr:hover .action-btn {
                transform: scale(1.05);
            }

            /* Prevent wrapping for ID and Date columns */
            #medicalRecordsTable td:nth-child(1),
            #medicalRecordsTable td:nth-child(2) {
                white-space: nowrap;
                min-width: 120px; /* Ensure enough space for ID and Date */
            }

            /* Allow wrapping for Diagnosis column */
            #medicalRecordsTable td:nth-child(3) {
                max-width: 200px; /* Limit width but allow wrapping */
                word-wrap: break-word; /* Allow text to wrap */
            }

            /* Ensure Action column has enough space */
            #medicalRecordsTable td:nth-child(5) {
                white-space: nowrap;
                min-width: 160px; /* Ensure enough space for action buttons */
            }

            /* Adjust table layout for better spacing */
            #medicalRecordsTable {
                table-layout: auto;
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

            /* Two-column layout adjustment (1/4 for patient info, 3/4 for medical records) */
            .patient-info-card {
                margin-bottom: 20px;
            }
            .medical-records-card {
                margin-bottom: 20px;
            }
            @media (min-width: 768px) {
                .patient-info-card {
                    flex: 0 0 25%;
                    max-width: 25%;
                }
                .medical-records-card {
                    flex: 0 0 75%;
                    max-width: 75%;
                }
            }

            /* Enhanced Patient Info Styling */
            .patient-info-card .card-body {
                padding: 15px;
            }
            .patient-info-card p {
                margin-bottom: 12px;
                padding: 6px 0;
                border-bottom: 1px solid #e9ecef;
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

            /* Frame Styling */
            .records-frame {
                border: 1px solid #dee2e6;
                border-radius: 8px;
                padding: 15px;
                background-color: #fff;
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
                <!-- Breadcrumb & Patient Info -->
                <div class="row mb-4">
                    <div class="col-12">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item">
                                    <a href="${pageContext.request.contextPath}/doctor/patients">
                                        <i class="bi bi-people me-1"></i>Quản lý bệnh nhân
                                    </a>
                                </li>
                                <li class="breadcrumb-item active" aria-current="page">
                                    Hồ sơ bệnh án
                                </li>
                            </ol>
                        </nav>

                        <% if (patient != null) { %>
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

                        <!-- Frame around both tables -->
                        <div class="records-frame">
                            <div class="row">
                                <!-- Patient Information (1/4 width) -->
                                <div class="col-md-3 patient-info-card">
                                    <div class="card bg-light">
                                        <div class="card-body">
                                            <h4 class="text-primary mb-3">
                                                <i class="bi bi-file-medical me-2"></i>Thông tin bệnh nhân
                                            </h4>
                                            <p class="mb-12"><strong>Mã bệnh nhân:</strong> <%= patient.getPatientCode() %></p>
                                            <p class="mb-12"><strong>Họ tên:</strong> <%= patient.getFullName() %></p>
                                            <p class="mb-12"><strong>Ngày sinh:</strong> <%= patient.getDob() != null ? shortSdf.format(patient.getDob()) : "" %></p>
                                            <p class="mb-12"><strong>Điện thoại:</strong> <%= patient.getPhone() %></p>
                                            <p class="mb-12"><strong>CCCD:</strong> <%= patient.getCccd() %></p>
                                            <p class="mb-0"><strong>Địa chỉ:</strong> <%= patient.getAddress() %></p>
                                        </div>
                                    </div>
                                </div>

                                <!-- Medical Records (3/4 width) -->
                                <div class="col-md-9 medical-records-card">
                                    <div class="card">
                                        <div class="card-body">
                                            <div class="d-flex justify-content-between align-items-center mb-3">
                                                <h4 class="text-primary mb-0">
                                                    <i class="bi bi-table me-2"></i>Lịch sử hồ sơ bệnh án
                                                </h4>
                                                <a href="${pageContext.request.contextPath}/doctor/medical-records?action=new&patientId=<%= patient.getId() %>" 
                                                   class="btn btn-success">
                                                    <i class="bi bi-plus-circle me-2"></i>Tạo hồ sơ mới
                                                </a>
                                            </div>
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
                                                            <td>
                                                                <a href="${pageContext.request.contextPath}/doctor/medical-records?action=edit&recordId=<%= record.getId() %>" 
                                                                   style="color: #007bff; text-decoration: underline;">
                                                                    <%= record.getId() %>
                                                                </a>
                                                            </td>
                                                            <td><%= record.getCreatedAt() != null ? sdf.format(record.getCreatedAt()) : "" %></td>
                                                            <td>
                                                                <% if (record.getFinalDiagnosis() != null && !record.getFinalDiagnosis().isEmpty()) { %>
                                                                <%= record.getFinalDiagnosis() %>
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
                                                                    <a href="${pageContext.request.contextPath}/doctor/invoices?action=listByMedicalRecord&medicalRecordId=<%= record.getId() %>" 
                                                                       class="action-btn action-btn-invoice" title="Quản lý hóa đơn">
                                                                        <i class="bi bi-receipt"></i>
                                                                        <span class="btn-text">Hóa đơn</span>
                                                                    </a>
                                                                    <a href="${pageContext.request.contextPath}/doctor/actual-prescriptions?action=listByMedicalRecord&medicalRecordId=<%= record.getId() %>" 
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
                        </div>
                        <% } else { %>
                        <div class="alert alert-danger">
                            <i class="bi bi-exclamation-triangle me-2"></i>
                            Không tìm thấy thông tin bệnh nhân!
                        </div>
                        <% } %>
                    </div>
                </div>
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
                        "ordering": true, // Enable sorting
                        "columnDefs": [
                            { "orderable": false, "targets": [4] }, // Disable sorting on "Thao tác" column
                            { "orderable": true, "targets": [0, 1, 2, 3] } // Enable sorting on other columns
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