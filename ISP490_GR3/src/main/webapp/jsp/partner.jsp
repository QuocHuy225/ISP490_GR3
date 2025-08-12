<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="com.mycompany.isp490_gr3.model.Partner" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý đối tác - Ánh Dương Clinic</title>
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
        
        // Get partners data
        List<Partner> partners = (List<Partner>) request.getAttribute("partners");
        String searchKeyword = (String) request.getAttribute("searchKeyword");
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
                <li class="active">
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
                            <i class="bi bi-building me-2"></i>Quản lý đối tác
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
                        Thêm đối tác thành công!
                    <% } else if ("updated".equals(success)) { %>
                        Cập nhật thông tin đối tác thành công!
                    <% } else if ("deleted".equals(success)) { %>
                        Xóa đối tác thành công!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <% if ("missing_fields".equals(error)) { %>
                        Vui lòng điền đầy đủ thông tin!
                    <% } else if ("invalid_phone".equals(error)) { %>
                        Số điện thoại không hợp lệ! Vui lòng nhập số điện thoại Việt Nam (10-11 chữ số, bắt đầu bằng số 0).
                    <% } else if ("partner_exists".equals(error)) { %>
                        Đối tác này đã tồn tại trong hệ thống!
                    <% } else if ("add_failed".equals(error)) { %>
                        Thêm đối tác thất bại!
                    <% } else if ("update_failed".equals(error)) { %>
                        Cập nhật đối tác thất bại!
                    <% } else if ("delete_failed".equals(error)) { %>
                        Xóa đối tác thất bại!
                    <% } else if ("invalid_format".equals(error)) { %>
                        Định dạng dữ liệu không hợp lệ!
                    <% } else { %>
                        Có lỗi xảy ra. Vui lòng thử lại!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <!-- Search and Add Partner Section -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col-md-8">
                                        <form method="GET" action="${pageContext.request.contextPath}/admin/partners" class="d-flex">
                                            <input type="hidden" name="action" value="search">
                                            <div class="input-group">
                                                <span class="input-group-text">
                                                    <i class="bi bi-search"></i>
                                                </span>
                                                <input type="text" class="form-control" name="keyword" 
                                                       placeholder="Tìm kiếm theo tên hoặc số điện thoại..." 
                                                       value="<%= searchKeyword != null ? searchKeyword : "" %>">
                                                <button class="btn btn-primary" type="submit">
                                                    Tìm kiếm
                                                </button>
                                                <% if (searchKeyword != null && !searchKeyword.trim().isEmpty()) { %>
                                                <a href="${pageContext.request.contextPath}/admin/partners" class="btn btn-outline-secondary">
                                                    <i class="bi bi-x-circle"></i>
                                                </a>
                                                <% } %>
                                            </div>
                                        </form>
                                    </div>
                                    <div class="col-md-4">
                                        <button type="button" class="btn btn-success w-100" data-bs-toggle="modal" data-bs-target="#addPartnerModal">
                                            <i class="bi bi-plus-circle me-2"></i>Thêm đối tác
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Partners Table -->
                <div class="row">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="card-title mb-0">
                                    <i class="bi bi-table me-2"></i>Danh sách đối tác
                                    <% if (searchKeyword != null && !searchKeyword.trim().isEmpty()) { %>
                                    <span class="badge bg-primary ms-2">Kết quả tìm kiếm: "<%= searchKeyword %>"</span>
                                    <% } %>
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table id="partnersTable" class="table table-striped table-hover">
                                        <thead class="table-primary">
                                            <tr>
                                                <th>ID</th>
                                                <th>Tên đối tác</th>
                                                <th>Số điện thoại</th>
                                                <th>Địa chỉ</th>
                                                <th>Mô tả</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <% if (partners != null && !partners.isEmpty()) {
                                                for (Partner partner : partners) { %>
                                                <tr>
                                                    <td><%= partner.getPartnerId() %></td>
                                                    <td><strong><%= partner.getName() %></strong></td>
                                                    <td><%= partner.getPhone() %></td>
                                                    <td><%= partner.getAddress() %></td>
                                                    <td><%= partner.getDescription() != null ? partner.getDescription() : "" %></td>
                                                    <td>
                                                        <button type="button" class="btn btn-sm btn-primary me-2" 
                                                                onclick="editPartner(<%= partner.getPartnerId() %>)" 
                                                                title="Chỉnh sửa đối tác">
                                                            <i class="bi bi-pencil-square"></i>
                                                        </button>
                                                        <button type="button" class="btn btn-sm btn-outline-danger" 
                                                                onclick="deletePartner(<%= partner.getPartnerId() %>, '<%= partner.getName() %>')" 
                                                                title="Xóa đối tác">
                                                            <i class="bi bi-trash3"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                                <% } %>
                                            <% } else { %>
                                                <!-- Chỉ hiển thị thông báo khi không có search keyword -->
                                                <% String keyword = (String) request.getAttribute("searchKeyword"); %>
                                                <% if (keyword == null || keyword.trim().isEmpty()) { %>
                                                    <tr>
                                                        <td colspan="6" class="text-center">
                                                            <i class="bi bi-inbox me-2"></i>
                                                            Chưa có đối tác nào trong hệ thống
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

        <!-- Add Partner Modal -->
        <div class="modal fade" id="addPartnerModal" tabindex="-1" aria-labelledby="addPartnerModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addPartnerModalLabel">
                            <i class="bi bi-plus-circle me-2"></i>Thêm đối tác mới
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/partners">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="add">
                            
                            <div class="mb-3">
                                <label for="addName" class="form-label">Tên đối tác <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addName" name="name" placeholder="Nhập tên đối tác" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="addPhone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="addPhone" name="phone" placeholder="Nhập số điện thoại" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="addAddress" class="form-label">Địa chỉ <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="addAddress" name="address" rows="2" placeholder="Nhập địa chỉ" required></textarea>
                            </div>
                            
                            <div class="mb-3">
                                <label for="addDescription" class="form-label">Mô tả</label>
                                <textarea class="form-control" id="addDescription" name="description" rows="3" placeholder="Nhập mô tả (tùy chọn)"></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                            </button>
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-circle me-2"></i>Thêm đối tác
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Edit Partner Modal -->
        <div class="modal fade" id="editPartnerModal" tabindex="-1" aria-labelledby="editPartnerModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editPartnerModalLabel">
                            <i class="bi bi-pencil-square me-2"></i>Chỉnh sửa thông tin đối tác
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/partners">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="partnerId" id="editPartnerId">
                            
                            <div class="mb-3">
                                <label for="editName" class="form-label">Tên đối tác <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="editName" name="name" placeholder="Nhập tên đối tác" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editPhone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="editPhone" name="phone" placeholder="Nhập số điện thoại" required>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editAddress" class="form-label">Địa chỉ <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="editAddress" name="address" rows="2" placeholder="Nhập địa chỉ" required></textarea>
                            </div>
                            
                            <div class="mb-3">
                                <label for="editDescription" class="form-label">Mô tả</label>
                                <textarea class="form-control" id="editDescription" name="description" rows="3" placeholder="Nhập mô tả (tùy chọn)"></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                            </button>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check-circle me-2"></i>Cập nhật
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Delete Confirmation Modal -->
        <div class="modal fade" id="deletePartnerModal" tabindex="-1" aria-labelledby="deletePartnerModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="deletePartnerModalLabel">
                            <i class="bi bi-exclamation-triangle text-danger me-2"></i>Xác nhận xóa
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>Bạn có chắc chắn muốn xóa đối tác <strong id="deletePartnerName"></strong>?</p>
                        <p class="text-muted"><small>Hành động này không thể hoàn tác.</small></p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="bi bi-x-circle me-2"></i>Hủy bỏ
                        </button>
                        <form method="POST" action="${pageContext.request.contextPath}/admin/partners" style="display: inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="partnerId" id="deletePartnerId">
                            <button type="submit" class="btn btn-danger">
                                <i class="bi bi-trash3 me-2"></i>Xóa
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- Scripts -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
        <script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
        <script src="https://cdn.datatables.net/1.13.7/js/dataTables.bootstrap5.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/homepage.js"></script>
        
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
                $('#partnersTable').DataTable({
                    language: {
                        url: 'https://cdn.datatables.net/plug-ins/1.13.7/i18n/vi.json'
                    },
                    pageLength: 10,
                    order: [[0, 'desc']],
                    dom: 'lrtip' // Remove search box (f), keep length, processing, info, pagination
                });
            });

            function editPartner(partnerId) {
                // Fetch partner data and populate modal
                fetch('${pageContext.request.contextPath}/admin/partners?action=get&id=' + partnerId)
                    .then(response => response.json())
                    .then(partner => {
                        document.getElementById('editPartnerId').value = partner.partnerId;
                        document.getElementById('editName').value = partner.name;
                        document.getElementById('editPhone').value = partner.phone;
                        document.getElementById('editAddress').value = partner.address;
                        document.getElementById('editDescription').value = partner.description || '';
                        
                        new bootstrap.Modal(document.getElementById('editPartnerModal')).show();
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('Có lỗi xảy ra khi tải thông tin đối tác!');
                    });
            }

            function deletePartner(partnerId, partnerName) {
                document.getElementById('deletePartnerId').value = partnerId;
                document.getElementById('deletePartnerName').textContent = partnerName;
                new bootstrap.Modal(document.getElementById('deletePartnerModal')).show();
            }
        </script>
    </body>
</html> 