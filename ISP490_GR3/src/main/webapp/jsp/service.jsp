<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.MedicalService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DecimalFormat" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý dịch vụ y tế - Ánh Dương Clinic</title>
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
        
        // Get services data
        List<MedicalService> services = (List<MedicalService>) request.getAttribute("services");
        List<String> serviceGroups = (List<String>) request.getAttribute("serviceGroups");
        String searchKeyword = (String) request.getAttribute("searchKeyword");
        String selectedGroup = (String) request.getAttribute("selectedGroup");
        
        DecimalFormat df = new DecimalFormat("#,###");
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
                <li class="active">
                    <a href="${pageContext.request.contextPath}/admin/services">
                        <i class="bi bi-file-medical"></i> Quản lý dịch vụ
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin/medicines">
                        <i class="bi bi-hospital"></i> Quản lý kho thuốc
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-capsule"></i> Quản lý đơn thuốc
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
                    <a href="#">
                        <i class="bi bi-file-medical"></i> Hồ sơ bệnh án
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="bi bi-people"></i> Danh sách bệnh nhân
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
                    <a href="#">
                        <i class="bi bi-calendar-plus"></i> Đặt lịch hẹn
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/appointments">
                        <i class="bi bi-calendar-check"></i> Quản lý lịch hẹn
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
                    <a href="#">
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
                <!-- Page Header -->
                <div class="row mb-4">
                    <div class="col-12">
                        <h2 class="text-primary">
                            <i class="bi bi-file-medical me-2"></i>Quản lý dịch vụ y tế
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
                        Thêm dịch vụ thành công!
                    <% } else if ("updated".equals(success)) { %>
                        Cập nhật thông tin dịch vụ thành công!
                    <% } else if ("deleted".equals(success)) { %>
                        Xóa dịch vụ thành công!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <% if ("missing_fields".equals(error)) { %>
                        Vui lòng điền đầy đủ thông tin!
                    <% } else if ("invalid_price".equals(error)) { %>
                        Giá dịch vụ phải lớn hơn 0!
                    <% } else if ("service_exists".equals(error)) { %>
                        Dịch vụ này đã tồn tại trong hệ thống!
                    <% } else if ("add_failed".equals(error)) { %>
                        Thêm dịch vụ thất bại!
                    <% } else if ("update_failed".equals(error)) { %>
                        Cập nhật dịch vụ thất bại!
                    <% } else if ("delete_failed".equals(error)) { %>
                        Xóa dịch vụ thất bại!
                    <% } else if ("invalid_format".equals(error)) { %>
                        Định dạng dữ liệu không hợp lệ!
                    <% } else { %>
                        Có lỗi xảy ra. Vui lòng thử lại!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <!-- Search and Add Service Section -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-body">
                                <div class="row g-3">
                                    <div class="col-md-8">
                                        <form method="GET" action="${pageContext.request.contextPath}/admin/services" class="d-flex">
                                            <input type="hidden" name="action" value="search">
                                            <div class="input-group">
                                                <span class="input-group-text">
                                                    <i class="bi bi-search"></i>
                                                </span>
                                                <input type="text" class="form-control" name="keyword" 
                                                       placeholder="Tìm kiếm theo tên dịch vụ..." 
                                                       value="<%= searchKeyword != null ? searchKeyword : "" %>">
                                                <button class="btn btn-primary" type="submit">
                                                    Tìm kiếm
                                                </button>
                                                <% if (searchKeyword != null && !searchKeyword.trim().isEmpty()) { %>
                                                <a href="${pageContext.request.contextPath}/admin/services" class="btn btn-outline-secondary">
                                                    <i class="bi bi-x-circle"></i>
                                                </a>
                                                <% } %>
                                            </div>
                                        </form>
                                    </div>
                                    <div class="col-md-4">
                                        <button type="button" class="btn btn-success w-100" data-bs-toggle="modal" data-bs-target="#addServiceModal">
                                            <i class="bi bi-plus-circle me-2"></i>Thêm dịch vụ
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Services Table -->
                <div class="row">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="card-title mb-0">
                                    <i class="bi bi-table me-2"></i>Danh sách dịch vụ y tế
                                    <% if (searchKeyword != null && !searchKeyword.trim().isEmpty()) { %>
                                    <span class="badge bg-primary ms-2">Kết quả tìm kiếm: "<%= searchKeyword %>"</span>
                                    <% } %>
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table id="servicesTable" class="table table-striped table-hover">
                                        <thead class="table-dark">
                                            <tr>
                                                <th>ID</th>
                                                <th>Nhóm dịch vụ</th>
                                                <th>Tên dịch vụ</th>
                                                <th>Giá (VNĐ)</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <% if (services != null && !services.isEmpty()) {
                                                for (MedicalService service : services) { %>
                                                <tr>
                                                    <td><%= service.getServicesId() %></td>
                                                    <td><%= service.getServiceGroup() %></td>
                                                    <td><%= service.getServiceName() %></td>
                                                    <td><%= df.format(service.getPrice()) %></td>
                                                    <td>
                                                        <button type="button" class="btn btn-sm btn-primary me-1" 
                                                                onclick="editService(<%= service.getServicesId() %>)" 
                                                                title="Chỉnh sửa">
                                                            <i class="bi bi-pencil"></i>
                                                        </button>
                                                        <button type="button" class="btn btn-sm btn-danger" 
                                                                onclick="deleteService(<%= service.getServicesId() %>, '<%= service.getServiceName() %>')" 
                                                                title="Xóa">
                                                            <i class="bi bi-trash"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                                <% } %>
                                            <% } else { %>
                                                <!-- Chỉ hiển thị thông báo khi không có search keyword và không có filter group -->
                                                <!-- Khi search/filter không có kết quả thì không hiển thị gì (bảng trống) -->
                                                <% String keyword = (String) request.getAttribute("searchKeyword");
                                                   String group = (String) request.getAttribute("selectedGroup"); %>
                                                <% if ((keyword == null || keyword.trim().isEmpty()) && (group == null || group.trim().isEmpty())) { %>
                                                    <tr>
                                                        <td colspan="5" class="text-center">
                                                            <i class="bi bi-inbox me-2"></i>
                                                            Chưa có dịch vụ nào trong hệ thống
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

        <!-- Add Service Modal -->
        <div class="modal fade" id="addServiceModal" tabindex="-1" aria-labelledby="addServiceModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addServiceModalLabel">
                            <i class="bi bi-plus-circle me-2"></i>Thêm dịch vụ
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/services">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="add">
                            
                            <div class="mb-3">
                                <label for="addServiceGroup" class="form-label">Nhóm dịch vụ <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addServiceGroup" name="serviceGroup" placeholder="Nhập tên nhóm dịch vụ" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="addServiceName" class="form-label">Tên dịch vụ <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addServiceName" name="serviceName" placeholder="Nhập tên dịch vụ" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="addPrice" class="form-label">Giá (VNĐ) <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="addPrice" name="price" min="0" step="0.01" placeholder="Nhập giá dịch vụ" required>
                            </div>
                            
                            <div class="alert alert-info">
                                <i class="bi bi-info-circle me-2"></i>
                                <strong>Lưu ý:</strong> Chức năng này để thêm dịch vụ y tế mới vào hệ thống.
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-circle me-2"></i>Thêm dịch vụ
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Edit Service Modal -->
        <div class="modal fade" id="editServiceModal" tabindex="-1" aria-labelledby="editServiceModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editServiceModalLabel">
                            <i class="bi bi-pencil me-2"></i>Chỉnh sửa dịch vụ
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/services">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" id="editServiceId" name="serviceId">
                            
                            <div class="mb-3">
                                <label for="editServiceGroup" class="form-label">Nhóm dịch vụ <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="editServiceGroup" name="serviceGroup" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editServiceName" class="form-label">Tên dịch vụ <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="editServiceName" name="serviceName" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editPrice" class="form-label">Giá (VNĐ) <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" id="editPrice" name="price" min="0" step="0.01" required>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
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
                        <p>Bạn có chắc chắn muốn xóa dịch vụ <strong id="deleteServiceName"></strong> không?</p>
                        <p class="text-danger">
                            <i class="bi bi-exclamation-triangle me-2"></i>
                            Hành động này không thể hoàn tác!
                        </p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <form method="POST" action="${pageContext.request.contextPath}/admin/services" style="display: inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" id="deleteServiceId" name="serviceId">
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
                $('#servicesTable').DataTable({
                    "searching": false, // Disable built-in search
                    "language": {
                        "lengthMenu": "Hiển thị _MENU_ mục",
                        "zeroRecords": "", // Không hiển thị thông báo khi không có dữ liệu
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

            // Edit service function
            function editService(serviceId) {
                var contextPath = '<%= request.getContextPath() %>';
                fetch(contextPath + '/admin/services?action=get&id=' + serviceId)
                    .then(response => response.json())
                    .then(data => {
                        document.getElementById('editServiceId').value = data.servicesId;
                        document.getElementById('editServiceGroup').value = data.serviceGroup;
                        document.getElementById('editServiceName').value = data.serviceName;
                        document.getElementById('editPrice').value = data.price;
                        
                        new bootstrap.Modal(document.getElementById('editServiceModal')).show();
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('Không thể tải thông tin dịch vụ!');
                    });
            }

            // Delete service function
            function deleteService(serviceId, serviceName) {
                document.getElementById('deleteServiceId').value = serviceId;
                document.getElementById('deleteServiceName').textContent = serviceName;
                new bootstrap.Modal(document.getElementById('deleteConfirmModal')).show();
            }
        </script>
    </body>
</html> 