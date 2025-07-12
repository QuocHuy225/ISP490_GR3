<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.Patient" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalRecord" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalRequest" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><%= "update".equals(request.getAttribute("action")) ? "Chỉnh sửa" : "Tạo" %> phiếu chỉ định - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Homepage specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <style>
            /* Enhanced Medical Request Form Styling */
            .medical-request-form-container {
                background: linear-gradient(135deg, #f0f8ff 0%, #ffffff 100%);
                border-radius: 1rem;
                box-shadow: 0 8px 32px rgba(0,0,0,0.1);
                padding: 2rem;
                margin-bottom: 2rem;
            }

            .form-section {
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

            .form-section::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                height: 4px;
                background: linear-gradient(90deg, #17a2b8, #138496);
            }

            .form-section:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 30px rgba(0,0,0,0.12);
            }

            .form-section h6 {
                color: #2c3e50;
                font-weight: 700;
                font-size: 1.1rem;
                margin-bottom: 1.5rem;
                display: flex;
                align-items: center;
                padding-bottom: 0.75rem;
                border-bottom: 1px solid #e9ecef;
            }

            .form-section h6 i {
                background: linear-gradient(135deg, #17a2b8, #138496);
                color: white;
                padding: 0.5rem;
                border-radius: 0.5rem;
                margin-right: 0.75rem;
                font-size: 1rem;
            }

            /* Enhanced Form Controls */
            .form-control, .form-select {
                border: 2px solid #e9ecef;
                border-radius: 0.5rem;
                padding: 0.75rem 1rem;
                font-weight: 500;
                transition: all 0.3s ease;
                background-color: #f8f9fa;
            }

            .form-control:focus, .form-select:focus {
                border-color: #17a2b8;
                box-shadow: 0 0 0 0.2rem rgba(23,162,184,0.25);
                background-color: #ffffff;
            }

            /* Enhanced textarea */
            .form-control[rows] {
                min-height: 120px;
                resize: vertical;
            }

            /* Action Button Styles */
            .action-btn {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 0.5rem 1.5rem;
                border: none;
                border-radius: 8px;
                text-decoration: none;
                font-size: 14px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.3s ease;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                min-width: 120px;
                justify-content: center;
            }

            .action-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0,0,0,0.15);
                text-decoration: none;
            }

            .action-btn i {
                font-size: 16px;
            }

            .btn-text {
                font-size: 15px;
                white-space: nowrap;
            }

            .action-btn-save, .action-btn-update {
                background: linear-gradient(135deg, #007bff, #0056b3);
                color: white;
            }

            .action-btn-save:hover, .action-btn-update:hover {
                background: linear-gradient(135deg, #0056b3, #004085);
                color: white;
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

            @media (max-width: 768px) {
                .action-btn {
                    padding: 0.6rem 1.2rem;
                    min-width: 100px;
                }
                .btn-text {
                    display: none;
                }
                .action-btn i {
                    font-size: 18px;
                }
            }

            /* Alert styling */
            .alert {
                border-radius: 0.75rem;
                border: none;
                padding: 1rem 1.5rem;
                box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            }

            /* Responsive Improvements */
            @media (max-width: 768px) {
                .medical-request-form-container {
                    padding: 1rem;
                }

                .form-section {
                    padding: 1.5rem;
                }

                .form-section h6 {
                    font-size: 1rem;
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
                color: #17a2b8;
                font-weight: bold;
            }

            /* Patient info styling */
            .patient-info {
                background: linear-gradient(135deg, #f8f9fa, #e9ecef);
                border-radius: 0.75rem;
                padding: 1.5rem;
                margin-bottom: 2rem;
                border: 1px solid #dee2e6;
            }

            .patient-info h4 {
                color: #17a2b8;
                margin-bottom: 1rem;
                font-weight: 700;
            }

            .form-floating {
                margin-bottom: 1rem;
            }

            .form-floating label {
                color: #6c757d;
                font-weight: 500;
            }

            .form-floating .form-control:focus ~ label,
            .form-floating .form-control:not(:placeholder-shown) ~ label {
                color: #17a2b8;
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
        MedicalRequest medicalRequest = (MedicalRequest) request.getAttribute("medicalRequest");
        
        // Check if medical record is completed
        boolean isCompleted = medicalRecord != null && "completed".equals(medicalRecord.getStatus());
        
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
                                    <%= isEdit ? "Chỉnh sửa phiếu chỉ định" : "Tạo phiếu chỉ định mới" %>
                                </li>
                            </ol>
                        </nav>

                        <% if (patient != null && medicalRecord != null) { %>
                        <div class="patient-info">
                            <h4>
                                <i class="bi bi-<%= isCompleted ? "eye" : (isEdit ? "pencil-square" : "plus-circle") %> me-2"></i><%= isCompleted ? "Xem phiếu chỉ định (Chỉ đọc)" : (isEdit ? "Chỉnh sửa phiếu chỉ định" : "Tạo phiếu chỉ định mới") %>
                                <% if (isCompleted) { %>
                                <span class="badge bg-warning text-dark ms-2">Hồ sơ đã hoàn thành</span>
                                <% } %>
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
                                    <% if (isEdit && medicalRequest != null) { %>
                                    <p class="mb-0"><strong>Mã phiếu chỉ định:</strong> <%= medicalRequest.getId() %></p>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                        <% } %>
                    </div>
                </div>

                <!-- Alert Messages -->
                <%
                String success = request.getParameter("success");
                String error = request.getParameter("error");
                String message = request.getParameter("message");
                %>

                <% if (success != null) { %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle me-2"></i>
                    <% if ("added".equals(success)) { %>
                    Tạo phiếu chỉ định thành công!
                    <% } else if ("updated".equals(success)) { %>
                    Cập nhật phiếu chỉ định thành công!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <% if ("missing_data".equals(error)) { %>
                    Dữ liệu không đầy đủ. Vui lòng kiểm tra lại!
                    <% } else if ("add_failed".equals(error)) { %>
                    Tạo phiếu chỉ định thất bại!
                    <% } else if ("update_failed".equals(error)) { %>
                    Cập nhật phiếu chỉ định thất bại!
                    <% } else if ("request_exists".equals(error)) { %>
                    Phiếu chỉ định đã tồn tại cho hồ sơ này!
                    <% } else if ("medical_record_completed".equals(error)) { %>
                    Không thể chỉnh sửa phiếu chỉ định vì hồ sơ bệnh án đã được hoàn thành!
                    <% } else if ("system_error".equals(error)) { %>
                    Có lỗi hệ thống xảy ra!
                    <% } else { %>
                    Có lỗi xảy ra. Vui lòng thử lại!
                    <% } %>
                    <% if (message != null) { %>
                    <br><%= message %>
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <!-- Warning for completed medical record -->
                <% if (isCompleted) { %>
                <div class="alert alert-warning alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <strong>Hồ sơ bệnh án đã hoàn thành!</strong> Không thể chỉnh sửa phiếu chỉ định khi hồ sơ bệnh án đã được hoàn thành.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <!-- Medical Request Form -->
                <div class="medical-request-form-container">
                    <form action="${pageContext.request.contextPath}/doctor/medical-requests" method="post" id="medicalRequestForm">
                        <input type="hidden" name="action" value="<%= isEdit ? "update" : "add" %>">
                        <input type="hidden" name="medicalRecordId" value="<%= medicalRecord != null ? medicalRecord.getId() : "" %>">
                        <input type="hidden" name="patientId" value="<%= patient != null ? patient.getId() : "" %>">
                        <% if (isEdit && medicalRequest != null) { %>
                        <input type="hidden" name="requestId" value="<%= medicalRequest.getId() %>">
                        <% } %>

                        <!-- Clinic Information Section -->
                        <div class="form-section">
                            <h6><i class="bi bi-building"></i>Thông tin cơ sở chỉ định</h6>
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-floating">
                                        <input type="text" class="form-control" id="clinicName" name="clinicName" 
                                               placeholder="Tên cơ sở" value="<%= isEdit && medicalRequest != null ? (medicalRequest.getClinicName() != null ? medicalRequest.getClinicName() : "") : "" %>" <%= isCompleted ? "disabled" : "required" %>>
                                        <label for="clinicName">Tên cơ sở chỉ định <span class="text-danger">*</span></label>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-floating">
                                        <input type="text" class="form-control" id="clinicPhone" name="clinicPhone" 
                                               placeholder="Số điện thoại" value="<%= isEdit && medicalRequest != null ? (medicalRequest.getClinicPhone() != null ? medicalRequest.getClinicPhone() : "") : "" %>" <%= isCompleted ? "disabled" : "required" %>>
                                        <label for="clinicPhone">Số điện thoại <span class="text-danger">*</span></label>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-12">
                                    <div class="form-floating">
                                        <textarea class="form-control" id="clinicAddress" name="clinicAddress" 
                                                  placeholder="Địa chỉ" style="height: 100px;" <%= isCompleted ? "disabled" : "required" %>><%= isEdit && medicalRequest != null ? (medicalRequest.getClinicAddress() != null ? medicalRequest.getClinicAddress() : "") : "" %></textarea>
                                        <label for="clinicAddress">Địa chỉ cơ sở <span class="text-danger">*</span></label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Instruction Content Section -->
                        <div class="form-section">
                            <h6><i class="bi bi-clipboard-data"></i>Nội dung chỉ định</h6>
                            <div class="row">
                                <div class="col-12">
                                    <div class="form-floating">
                                        <textarea class="form-control" id="instructionContent" name="instructionContent" 
                                                  placeholder="Nội dung chỉ định" style="height: 150px;" <%= isCompleted ? "disabled" : "required" %>><%= isEdit && medicalRequest != null ? (medicalRequest.getInstructionContent() != null ? medicalRequest.getInstructionContent() : "") : "" %></textarea>
                                        <label for="instructionContent">Nội dung chỉ định <span class="text-danger">*</span></label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Instruction Requirements Section -->
                        <div class="form-section">
                            <h6><i class="bi bi-list-check"></i>Yêu cầu chỉ định</h6>
                            <div class="row">
                                <div class="col-12">
                                    <div class="form-floating">
                                        <textarea class="form-control" id="instructionRequirements" name="instructionRequirements" 
                                                  placeholder="Yêu cầu chỉ định" style="height: 120px;" <%= isCompleted ? "disabled" : "" %>><%= isEdit && medicalRequest != null ? (medicalRequest.getInstructionRequirements() != null ? medicalRequest.getInstructionRequirements() : "") : "" %></textarea>
                                        <label for="instructionRequirements">Yêu cầu chỉ định</label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Notes Section -->
                        <div class="form-section">
                            <h6><i class="bi bi-chat-dots"></i>Ghi chú</h6>
                            <div class="row">
                                <div class="col-12">
                                    <div class="form-floating">
                                        <textarea class="form-control" id="notes" name="notes" 
                                                  placeholder="Ghi chú thêm" style="height: 100px;" <%= isCompleted ? "disabled" : "" %>><%= isEdit && medicalRequest != null ? (medicalRequest.getNotes() != null ? medicalRequest.getNotes() : "") : "" %></textarea>
                                        <label for="notes">Ghi chú thêm</label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Form Buttons -->
                        <div class="form-buttons mt-4">
                            <% if (!isCompleted) { %>
                            <button type="submit" class="btn btn-primary me-2">
                                <i class="bi bi-check-circle me-2"></i>
                                <%= isEdit ? "Cập nhật" : "Tạo phiếu chỉ định" %>
                            </button>
                            <% } %>
                            <% if (isEdit && medicalRequest != null) { %>
                            <a href="${pageContext.request.contextPath}/doctor/medical-requests?action=view&requestId=<%= medicalRequest.getId() %>" 
                               class="btn btn-secondary">
                                <i class="bi bi-<%= isCompleted ? "arrow-left" : "x-circle" %> me-2"></i><%= isCompleted ? "Quay lại" : "Hủy" %>
                            </a>
                            <% } else { %>
                            <a href="${pageContext.request.contextPath}/doctor/medical-records?action=edit&recordId=<%= medicalRecord != null ? medicalRecord.getId() : "" %>" 
                               class="btn btn-secondary">
                                <i class="bi bi-<%= isCompleted ? "arrow-left" : "x-circle" %> me-2"></i><%= isCompleted ? "Quay lại" : "Hủy" %>
                            </a>
                            <% } %>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Bootstrap Bundle with Popper -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <!-- jQuery -->
        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

        <script>
            $(document).ready(function () {
                // Sidebar toggle
                $('#sidebarCollapse').on('click', function () {
                    $('#sidebar').toggleClass('active');
                    $('#content').toggleClass('active');
                });

                // Medical record completion status
                const medicalRecordCompleted = '<%= isCompleted %>' === 'true';

                // Form validation
                document.getElementById('medicalRequestForm').addEventListener('submit', function (e) {
                    if (medicalRecordCompleted) {
                        e.preventDefault();
                        alert('Không thể lưu thay đổi vì hồ sơ bệnh án đã được hoàn thành!');
                        return false;
                    } else {
                        const clinicName = document.getElementById('clinicName').value.trim();
                        const clinicPhone = document.getElementById('clinicPhone').value.trim();
                        const clinicAddress = document.getElementById('clinicAddress').value.trim();
                        const instructionContent = document.getElementById('instructionContent').value.trim();
                        if (!clinicName) {
                            e.preventDefault();
                            alert('Vui lòng nhập tên cơ sở chỉ định!');
                            document.getElementById('clinicName').focus();
                            return false;
                        }
                        if (!clinicPhone) {
                            e.preventDefault();
                            alert('Vui lòng nhập số điện thoại cơ sở chỉ định!');
                            document.getElementById('clinicPhone').focus();
                            return false;
                        }
                        if (!clinicAddress) {
                            e.preventDefault();
                            alert('Vui lòng nhập địa chỉ cơ sở chỉ định!');
                            document.getElementById('clinicAddress').focus();
                            return false;
                        }
                        if (!instructionContent) {
                            e.preventDefault();
                            alert('Vui lòng nhập nội dung chỉ định!');
                            document.getElementById('instructionContent').focus();
                            return false;
                        }
                    }
                });
            });
        </script>
    </body>
</html> 