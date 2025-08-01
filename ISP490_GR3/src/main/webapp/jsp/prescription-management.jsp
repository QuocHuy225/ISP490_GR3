<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.PrescriptionMedicine" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý thuốc - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Homepage specific CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <!-- DataTables CSS -->
        <link rel="stylesheet" href="https://cdn.datatables.net/1.13.7/css/dataTables.bootstrap5.min.css">
        <style>
            /* Ensure delete modals have white header */
            #deleteMedicineModal .modal-header {
                background: white !important;
                color: #dc3545 !important;
                border-bottom: 1px solid #dee2e6;
            }
            #deleteMedicineModal .modal-content {
                background-color: white !important;
                background: white !important;
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
                // Try to parse from string
                try {
                    currentRole = User.Role.valueOf(userRole.toString().toUpperCase());
                } catch (Exception e) {
                    // Fallback to parsing from display value
                    currentRole = User.Role.fromString(userRole.toString());
                }
            }
        }
        
        // Default to PATIENT if no role found
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
        List<PrescriptionMedicine> medicines = (List<PrescriptionMedicine>) request.getAttribute("medicines");
        String medicineSearchKeyword = (String) request.getAttribute("medicineSearchKeyword");
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
                    <a href="${pageContext.request.contextPath}/admin/medical-exam-templates">
                        <i class="bi bi-file-text"></i> Mẫu khám bệnh
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/services">
                        <i class="bi bi-file-medical"></i> Quản lý dịch vụ
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/partners">
                        <i class="bi bi-building"></i> Quản lý đối tác
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/medicines">
                        <i class="bi bi-hospital"></i> Quản lý kho thuốc
                    </a>
                </li>
                <li class="active">
                    <a href="${pageContext.request.contextPath}/admin/prescriptions">
                        <i class="bi bi-capsule"></i> Quản lý thuốc
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/medical-supplies">
                        <i class="bi bi-gear-fill"></i> Quản lý vật tư
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/report">
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
                <!-- Page Header -->
                <div class="row mb-4">
                    <div class="col-12">
                        <h2 class="text-primary">
                            <i class="bi bi-capsule me-2"></i>Quản lý thuốc
                        </h2>
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
                    <% if ("medicine_added".equals(success)) { %>
                        Thêm thuốc thành công!
                    <% } else if ("medicine_updated".equals(success)) { %>
                        Cập nhật thông tin thuốc thành công!
                    <% } else if ("medicine_deleted".equals(success)) { %>
                        Xóa thuốc thành công!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <% if ("missing_fields".equals(error)) { %>
                        Vui lòng điền đầy đủ thông tin!
                    <% } else if ("medicine_exists".equals(error)) { %>
                        Tên thuốc này đã tồn tại trong hệ thống!
                    <% } else if ("add_failed".equals(error)) { %>
                        Thêm thuốc thất bại!
                    <% } else if ("update_failed".equals(error)) { %>
                        Cập nhật thất bại!
                    <% } else if ("delete_failed".equals(error)) { %>
                        Xóa thất bại!
                    <% } else if ("invalid_format".equals(error)) { %>
                        Định dạng dữ liệu không hợp lệ!
                    <% } else { %>
                        Có lỗi xảy ra. Vui lòng thử lại!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <!-- Search and Add Medicine Section -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col-md-8">
                                        <form method="GET" action="${pageContext.request.contextPath}/admin/prescriptions" class="d-flex">
                                            <input type="hidden" name="action" value="searchMedicine">
                                            <div class="input-group">
                                                <span class="input-group-text">
                                                    <i class="bi bi-search"></i>
                                                </span>
                                                <input type="text" class="form-control" name="keyword" 
                                                       placeholder="Tìm kiếm thuốc theo tên..." 
                                                       value="<%= medicineSearchKeyword != null ? medicineSearchKeyword : "" %>">
                                                <button class="btn btn-primary" type="submit">
                                                    Tìm kiếm
                                                </button>
                                                <% if (medicineSearchKeyword != null && !medicineSearchKeyword.trim().isEmpty()) { %>
                                                <a href="${pageContext.request.contextPath}/admin/prescriptions" class="btn btn-outline-secondary">
                                                    <i class="bi bi-x-circle"></i>
                                                </a>
                                                <% } %>
                                            </div>
                                        </form>
                                    </div>
                                    <div class="col-md-4">
                                        <button type="button" class="btn btn-success w-100" data-bs-toggle="modal" data-bs-target="#addMedicineModal">
                                            <i class="bi bi-plus-circle me-2"></i>Thêm thuốc
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Medicines Table -->
                <div class="row">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="card-title mb-0">
                                    <i class="bi bi-table me-2"></i>Danh sách thuốc kê về
                                    <% if (medicineSearchKeyword != null && !medicineSearchKeyword.trim().isEmpty()) { %>
                                    <span class="badge bg-primary ms-2">Kết quả tìm kiếm: "<%= medicineSearchKeyword %>"</span>
                                    <% } %>
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table id="medicinesTable" class="table table-striped table-hover">
                                        <thead class="table-primary">
                                            <tr>
                                                <th>ID</th>
                                                <th>Tên thuốc</th>
                                                <th>Đơn vị tính</th>
                                                <th>Đường dùng</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <% if (medicines != null && !medicines.isEmpty()) {
                                                for (PrescriptionMedicine medicine : medicines) { %>
                                                <tr>
                                                    <td><%= medicine.getPreMedicineId() %></td>
                                                    <td><%= medicine.getMedicineName() %></td>
                                                    <td><%= medicine.getUnitOfMeasure() %></td>
                                                    <td><%= medicine.getAdministrationRoute() %></td>
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/admin/prescriptions?editMedicine=<%= medicine.getPreMedicineId() %>" 
                                                           class="btn btn-sm btn-primary me-2" title="Chỉnh sửa thông tin thuốc">
                                                            <i class="bi bi-pencil-square"></i>
                                                        </a>
                                                        <button type="button" 
                                                                class="btn btn-sm btn-outline-danger" 
                                                                data-medicine-id="<%= medicine.getPreMedicineId() %>"
                                                                data-medicine-name="<%= medicine.getMedicineName() %>"
                                                                onclick="deleteMedicine(this.getAttribute('data-medicine-id'), this.getAttribute('data-medicine-name'))" 
                                                                title="Xóa thuốc">
                                                            <i class="bi bi-trash3"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                                <% } %>
                                            <% } else { %>
                                                <% if (medicineSearchKeyword == null || medicineSearchKeyword.trim().isEmpty()) { %>
                                                <tr>
                                                    <td colspan="5" class="text-center">
                                                        <i class="bi bi-inbox me-2"></i>
                                                        Chưa có thuốc nào trong hệ thống
                                                    </td>
                                                </tr>
                                                <% } %>
                                            <% } %>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Add Medicine Modal -->
        <div class="modal fade" id="addMedicineModal" tabindex="-1" aria-labelledby="addMedicineModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addMedicineModalLabel">
                            <i class="bi bi-plus-circle me-2"></i>Thêm thuốc mới
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/prescriptions/medicines">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="add">
                            
                            <div class="mb-3">
                                <label for="addMedicineName" class="form-label">Tên thuốc <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addMedicineName" name="medicineName" placeholder="Nhập tên thuốc" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="addUnitOfMeasure" class="form-label">Đơn vị tính <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addUnitOfMeasure" name="unitOfMeasure" placeholder="Nhập đơn vị tính (viên, ml, gói...)" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="addAdministrationRoute" class="form-label">Đường dùng <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addAdministrationRoute" name="administrationRoute" placeholder="Nhập đường dùng (Uống, tiêm, bôi...)" required>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                            </button>
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-circle me-2"></i>Thêm thuốc
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <%
        // Get edit data for modals
        PrescriptionMedicine editMedicine = (PrescriptionMedicine) request.getAttribute("editMedicine");
        %>

        <!-- Edit Medicine Modal -->
        <div class="modal fade" id="editMedicineModal" tabindex="-1" aria-labelledby="editMedicineModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editMedicineModalLabel">
                            <i class="bi bi-pencil-square me-2"></i>Chỉnh sửa thông tin thuốc
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <% if (editMedicine != null) { %>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/prescriptions/medicines">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="medicineId" value="<%= editMedicine.getPreMedicineId() %>">
                            
                            <div class="mb-3">
                                <label for="editMedicineName" class="form-label">Tên thuốc <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="editMedicineName" name="medicineName" 
                                       value="<%= editMedicine.getMedicineName() %>" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editUnitOfMeasure" class="form-label">Đơn vị tính <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="editUnitOfMeasure" name="unitOfMeasure" 
                                       value="<%= editMedicine.getUnitOfMeasure() %>" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editAdministrationRoute" class="form-label">Đường dùng <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="editAdministrationRoute" name="administrationRoute" 
                                       value="<%= editMedicine.getAdministrationRoute() %>" required>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <a href="${pageContext.request.contextPath}/admin/prescriptions" class="btn btn-secondary">
                                <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check-circle me-2"></i>Cập nhật
                            </button>
                        </div>
                    </form>
                    <% } %>
                </div>
            </div>
        </div>

        <!-- Delete Medicine Confirmation Modal -->
        <div class="modal fade" id="deleteMedicineModal" tabindex="-1" aria-labelledby="deleteMedicineModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title text-danger" id="deleteMedicineModalLabel">
                            <i class="bi bi-exclamation-triangle me-2"></i>Xác nhận xóa thuốc
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>Bạn có chắc chắn muốn xóa thuốc <strong id="deleteMedicineName"></strong> không?</p>
                        <p class="text-danger">
                            <i class="bi bi-exclamation-triangle me-2"></i>
                            Hành động này không thể hoàn tác!
                        </p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                        </button>
                        <form method="POST" action="${pageContext.request.contextPath}/admin/prescriptions/medicines" style="display: inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" id="deleteMedicineId" name="medicineId">
                            <button type="submit" class="btn btn-danger">
                                <i class="bi bi-trash me-2"></i>Xóa
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- JavaScript Libraries -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
        <script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
        <script src="https://cdn.datatables.net/1.13.7/js/dataTables.bootstrap5.min.js"></script>

        <script>
        $(document).ready(function() {
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

            // Initialize DataTable for medicines table (only if it exists and is visible)
            if ($('#medicinesTable').length && $('#medicinesTable').is(':visible')) {
                $('#medicinesTable').DataTable({
                    "searching": false, // Disable built-in search since we have custom search
                    "language": {
                        "lengthMenu": "Hiển thị _MENU_ mục",
                        "zeroRecords": "Không có dữ liệu", // Don't show message when no data
                        "info": "Hiển thị _START_ đến _END_ của _TOTAL_ mục",
                        "infoEmpty": "Hiển thị 0 đến 0 của 0 mục",
                        "infoFiltered": "(lọc từ _MAX_ tổng số mục)",
                        "paginate": {
                            "first": "Đầu",
                            "last": "Cuối",
                            "next": "Tiếp",
                            "previous": "Trước"
                        }
                    },
                    "order": [[ 0, "asc" ]], // Default sort by ID ascending
                    "columnDefs": [
                        { "orderable": false, "targets": 4 } // Disable sorting for action column
                    ]
                });
            }
        });

        // Delete medicine function
        function deleteMedicine(medicineId, medicineName) {
            document.getElementById('deleteMedicineId').value = medicineId;
            document.getElementById('deleteMedicineName').textContent = medicineName;
            new bootstrap.Modal(document.getElementById('deleteMedicineModal')).show();
        }
        </script>

        <% if (editMedicine != null) { %>
        <script>
        $(document).ready(function() {
            $('#editMedicineModal').modal('show');
        });
        </script>
        <% } %>
    </body>
</html> 