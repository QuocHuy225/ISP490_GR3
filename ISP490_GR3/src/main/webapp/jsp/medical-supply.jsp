<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalSupply" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DecimalFormat" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý vật tư y tế - Ánh Dương Clinic</title>
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
        
        // Get supplies data
        List<MedicalSupply> supplies = (List<MedicalSupply>) request.getAttribute("supplies");
        List<String> supplyGroups = (List<String>) request.getAttribute("supplyGroups");
        String searchKeyword = (String) request.getAttribute("searchKeyword");
        
        // Format for currency display
        DecimalFormat currencyFormatter = new DecimalFormat("#,###");
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
                <li>
                    <a href="${pageContext.request.contextPath}/admin/prescriptions">
                        <i class="bi bi-capsule"></i> Quản lý thuốc
                    </a>
                </li>
                <li class="active">
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
                            <i class="bi bi-gear-fill me-2"></i>Quản lý vật tư y tế
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
                    <% if ("added".equals(success)) { %>
                        Thêm vật tư thành công!
                    <% } else if ("updated".equals(success)) { %>
                        Cập nhật thông tin vật tư thành công!
                    <% } else if ("deleted".equals(success)) { %>
                        Xóa vật tư thành công!
                    <% } else if ("stock_updated".equals(success)) { %>
                        Cập nhật số lượng tồn kho thành công!
                    <% } else if ("stock_added".equals(success)) { %>
                        Thêm số lượng vào kho thành công!
                    <% } else if ("stock_reduced".equals(success)) { %>
                        Giảm số lượng kho thành công!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <% if ("access_denied".equals(error)) { %>
                        Bạn không có quyền truy cập chức năng này!
                    <% } else if ("missing_fields".equals(error)) { %>
                        Vui lòng điền đầy đủ thông tin!
                    <% } else if ("invalid_values".equals(error)) { %>
                        Giá trị nhập vào không hợp lệ!
                    <% } else if ("invalid_format".equals(error)) { %>
                        Định dạng dữ liệu không hợp lệ!
                    <% } else if ("add_failed".equals(error)) { %>
                        Thêm vật tư thất bại!
                    <% } else if ("update_failed".equals(error)) { %>
                        Cập nhật thất bại!
                    <% } else if ("delete_failed".equals(error)) { %>
                        Xóa vật tư thất bại!
                    <% } else if ("invalid_quantity".equals(error)) { %>
                        Số lượng phải lớn hơn 0!
                    <% } else if ("invalid_values".equals(error)) { %>
                        Giá trị nhập vào không hợp lệ! Đơn giá phải lớn hơn 0 và số lượng phải từ 1 trở lên.
                    <% } else if ("supply_exists".equals(error)) { %>
                        Vật tư này đã tồn tại!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <!-- Search and Add Section -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col-md-8">
                                        <form method="GET" action="${pageContext.request.contextPath}/admin/medical-supplies" class="d-flex">
                                            <input type="hidden" name="action" value="search">
                                            <div class="input-group">
                                                <span class="input-group-text">
                                                    <i class="bi bi-search"></i>
                                                </span>
                                                <input type="text" class="form-control" name="keyword" 
                                                       placeholder="Tìm kiếm theo nhóm vật tư hoặc tên vật tư..." 
                                                       value="<%= searchKeyword != null ? searchKeyword : "" %>">
                                                <button class="btn btn-primary" type="submit">
                                                    Tìm kiếm
                                                </button>
                                                <% if (searchKeyword != null && !searchKeyword.trim().isEmpty()) { %>
                                                <a href="${pageContext.request.contextPath}/admin/medical-supplies" class="btn btn-outline-secondary">
                                                    <i class="bi bi-x-circle"></i>
                                                </a>
                                                <% } %>
                                            </div>
                                        </form>
                                    </div>
                                    <div class="col-md-4">
                                        <button type="button" class="btn btn-success w-100" data-bs-toggle="modal" data-bs-target="#addSupplyModal">
                                            <i class="bi bi-plus-circle me-2"></i>Thêm vật tư
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Supplies Table -->
                <div class="row">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="card-title mb-0">
                                    <i class="bi bi-table me-2"></i>Danh sách vật tư y tế
                                    <% if (searchKeyword != null && !searchKeyword.trim().isEmpty()) { %>
                                    <span class="badge bg-primary ms-2">Kết quả tìm kiếm: "<%= searchKeyword %>"</span>
                                    <% } %>
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table id="suppliesTable" class="table table-striped table-hover">
                                        <thead class="table-primary">
                                            <tr>
                                                <th>ID</th>
                                                <th>Nhóm vật tư</th>
                                                <th>Tên vật tư</th>
                                                <th>Đơn giá (VNĐ)</th>
                                                <th>Số lượng tồn kho</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <% if (supplies != null && !supplies.isEmpty()) { %>
                                                <% for (MedicalSupply supply : supplies) { %>
                                                <tr>
                                                    <td><%= supply.getSupplyId() %></td>
                                                    <td><%= supply.getSupplyGroup() %></td>
                                                    <td><%= supply.getSupplyName() %></td>
                                                    <td><%= currencyFormatter.format(supply.getUnitPrice()) %></td>
                                                    <td>
                                                        <span class="badge <%= supply.getStockQuantity() < 10 ? "bg-danger" : supply.getStockQuantity() < 20 ? "bg-warning" : "bg-success" %>">
                                                            <%= supply.getStockQuantity() %>
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/admin/medical-supplies?edit=<%= supply.getSupplyId() %>" 
                                                           class="btn btn-sm btn-primary me-2" title="Chỉnh sửa thông tin vật tư">
                                                            <i class="bi bi-pencil-square"></i>
                                                        </a>
                                                        <button type="button" class="btn btn-sm btn-outline-danger" 
                                                                onclick="deleteSupply(<%= supply.getSupplyId() %>, '<%= supply.getSupplyName() %>')" 
                                                                title="Xóa vật tư">
                                                            <i class="bi bi-trash3"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                                <% } %>
                                            <% } else { %>
                                                <!-- Chỉ hiển thị thông báo khi không có search keyword (tức là lần đầu truy cập trang) -->
                                                <!-- Khi search không có kết quả thì không hiển thị gì (bảng trống) -->
                                                <% if (searchKeyword == null || searchKeyword.trim().isEmpty()) { %>
                                                    <tr>
                                                        <td colspan="6" class="text-center">
                                                            <i class="bi bi-inbox me-2"></i>
                                                            Chưa có vật tư nào trong kho
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

        <!-- Add Supply Modal -->
        <div class="modal fade" id="addSupplyModal" tabindex="-1" aria-labelledby="addSupplyModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addSupplyModalLabel">
                            <i class="bi bi-plus-circle me-2"></i>Thêm vật tư mới
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/medical-supplies">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="add">
                            
                            <div class="mb-3">
                                <label for="addSupplyGroup" class="form-label">Nhóm vật tư <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addSupplyGroup" name="supplyGroup" placeholder="Nhập tên nhóm vật tư" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="addSupplyName" class="form-label">Tên vật tư <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addSupplyName" name="supplyName" placeholder="Nhập tên vật tư" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="addUnitPrice" class="form-label">Đơn giá (VNĐ) <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="addUnitPrice" name="unitPrice" min="0" step="0.01" placeholder="Nhập đơn giá" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="addStockQuantity" class="form-label">Số lượng <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="addStockQuantity" name="stockQuantity" min="1" placeholder="Nhập số lượng" required>
                            </div>
                            

                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                            </button>
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-circle me-2"></i>Thêm vật tư
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Edit Supply Modal -->
        <div class="modal fade" id="editSupplyModal" tabindex="-1" aria-labelledby="editSupplyModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editSupplyModalLabel">
                            <i class="bi bi-pencil-square me-2"></i>Chỉnh sửa thông tin vật tư
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/medical-supplies">
                        <div class="modal-body">
                            <%
                            // Get edit data for modal
                            MedicalSupply editSupply = (MedicalSupply) request.getAttribute("editSupply");
                            boolean isEditSupply = editSupply != null;
                            %>
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="supplyId" value="<%= isEditSupply ? editSupply.getSupplyId() : "" %>">
                            
                            <div class="mb-3">
                                <label for="editSupplyGroup" class="form-label">Nhóm vật tư <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="editSupplyGroup" name="supplyGroup" 
                                       placeholder="Nhập tên nhóm vật tư" value="<%= isEditSupply ? editSupply.getSupplyGroup() : "" %>" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editSupplyName" class="form-label">Tên vật tư <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="editSupplyName" name="supplyName" 
                                       placeholder="Nhập tên vật tư" value="<%= isEditSupply ? editSupply.getSupplyName() : "" %>" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editUnitPrice" class="form-label">Đơn giá (VNĐ) <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="editUnitPrice" name="unitPrice" 
                                       min="0" step="0.01" placeholder="Nhập đơn giá" value="<%= isEditSupply ? editSupply.getUnitPrice() : "" %>" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editStockQuantity" class="form-label">Số lượng <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="editStockQuantity" name="stockQuantity" 
                                       min="1" placeholder="Nhập số lượng" value="<%= isEditSupply ? editSupply.getStockQuantity() : "" %>" required>
                            </div>
                            

                        </div>
                        <div class="modal-footer bg-light">
                            <% if (isEditSupply) { %>
                            <a href="${pageContext.request.contextPath}/admin/medical-supplies" class="btn btn-secondary">
                                <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                            </a>
                            <% } else { %>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                            </button>
                            <% } %>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check-circle me-2"></i>Cập nhật
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Delete Confirmation Modal -->
        <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title text-danger" id="deleteConfirmModalLabel">
                            <i class="bi bi-exclamation-triangle me-2"></i>Xác nhận xóa
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>Bạn có chắc chắn muốn xóa vật tư <strong id="deleteSupplyName"></strong> không?</p>
                        <p class="text-danger">
                            <i class="bi bi-exclamation-triangle me-2"></i>
                            Hành động này không thể hoàn tác!
                        </p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                        </button>
                        <form method="POST" action="${pageContext.request.contextPath}/admin/medical-supplies" style="display: inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" id="deleteSupplyId" name="supplyId">
                            <button type="submit" class="btn btn-danger">
                                <i class="bi bi-trash me-2"></i>Xóa
                            </button>
                        </form>
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

                // Initialize DataTable
                $('#suppliesTable').DataTable({
                    "searching": false, // Disable built-in search
                    "language": {
                        "lengthMenu": "Hiển thị _MENU_ mục",
                        "zeroRecords": "Không có dữ liệu", // Không hiển thị thông báo khi không có dữ liệu
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

            });

            // Delete supply function
            function deleteSupply(supplyId, supplyName) {
                document.getElementById('deleteSupplyId').value = supplyId;
                document.getElementById('deleteSupplyName').textContent = supplyName;
                new bootstrap.Modal(document.getElementById('deleteConfirmModal')).show();
            }
        </script>

        <% if (isEditSupply) { %>
        <script>
        document.addEventListener('DOMContentLoaded', function() {
            new bootstrap.Modal(document.getElementById('editSupplyModal')).show();
        });
        </script>
        <% } %>
    </body>
</html> 