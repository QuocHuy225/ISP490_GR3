<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Phân quyền - Ánh Dương Clinic</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- Custom Styles -->
        <style>
            :root {
                --primary-color: #0360D9;
                --primary-dark: #0246a3;
                --primary-light: #e3f2fd;
                --white: #ffffff;
                --text-dark: #2B3674;
                --text-light: #A3AED0;
                --body-bg: #f8f9fa;
                --success-color: #28a745;
                --warning-color: #ffc107;
                --danger-color: #dc3545;
                --info-color: #17a2b8;
                --gradient-primary: linear-gradient(135deg, var(--primary-color) 0%, #00d4ff 100%);
                --shadow-light: 0 5px 25px rgba(3, 96, 217, 0.1);
                --shadow-medium: 0 10px 40px rgba(3, 96, 217, 0.15);
            }

            * {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
                -webkit-font-smoothing: antialiased;
                -moz-osx-font-smoothing: grayscale;
            }

            body {
                background-color: var(--body-bg);
                color: var(--text-dark);
                padding: 20px 0;
            }

            .main-container {
                max-width: 1400px;
                margin: 0 auto;
                padding: 0 20px;
            }

            .page-header {
                background: var(--white);
                border-radius: 15px;
                padding: 30px;
                margin-bottom: 30px;
                box-shadow: var(--shadow-light);
                border-left: 5px solid var(--primary-color);
            }

            .page-title {
                font-size: 2.5rem;
                font-weight: 700;
                color: var(--text-dark);
                margin: 0;
                display: flex;
                align-items: center;
                gap: 15px;
            }

            .page-subtitle {
                color: var(--text-light);
                margin-top: 10px;
                font-size: 1.1rem;
            }

            .stats-container {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 20px;
                margin-bottom: 30px;
            }

            .stat-card {
                background: var(--white);
                border-radius: 15px;
                padding: 25px;
                box-shadow: var(--shadow-light);
                transition: all 0.3s ease;
                border-left: 4px solid transparent;
            }

            .stat-card:hover {
                transform: translateY(-5px);
                box-shadow: var(--shadow-medium);
            }

            .stat-card.admin {
                border-left-color: var(--danger-color);
            }

            .stat-card.doctor {
                border-left-color: var(--success-color);
            }

            .stat-card.receptionist {
                border-left-color: var(--warning-color);
            }

            .stat-card.patient {
                border-left-color: var(--info-color);
            }

            .stat-card.total {
                border-left-color: var(--primary-color);
            }

            .stat-number {
                font-size: 3rem;
                font-weight: 700;
                margin: 0;
            }

            .stat-label {
                color: var(--text-light);
                font-size: 1.1rem;
                margin-top: 10px;
            }

            .stat-icon {
                font-size: 2.5rem;
                opacity: 0.3;
                float: right;
            }

            .users-table-container {
                background: var(--white);
                border-radius: 15px;
                box-shadow: var(--shadow-light);
                overflow: hidden;
            }

            .table-header {
                background: var(--gradient-primary);
                color: var(--white);
                padding: 25px 30px;
                font-size: 1.3rem;
                font-weight: 600;
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .table-responsive {
                max-height: 600px;
                overflow-y: auto;
            }

            .users-table {
                margin: 0;
                border: none;
            }

            .users-table th {
                background: #f8f9fa;
                border: none;
                padding: 20px 15px;
                font-weight: 600;
                color: var(--text-dark);
                text-transform: uppercase;
                font-size: 0.85rem;
                letter-spacing: 0.5px;
                position: sticky;
                top: 0;
                z-index: 10;
            }

            .users-table td {
                padding: 20px 15px;
                border-bottom: 1px solid #eee;
                vertical-align: middle;
            }

            .users-table tr:hover {
                background-color: #f8f9fa;
            }

            .role-badge {
                padding: 8px 16px;
                border-radius: 20px;
                font-size: 0.85rem;
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .role-badge.admin {
                background: var(--danger-color);
                color: white;
            }

            .role-badge.doctor {
                background: var(--success-color);
                color: white;
            }

            .role-badge.receptionist {
                background: var(--warning-color);
                color: white;
            }

            .role-badge.patient {
                background: var(--info-color);
                color: white;
            }

            .role-select {
                border-radius: 10px;
                border: 2px solid #e0e0e0;
                padding: 8px 12px;
                font-size: 0.9rem;
                transition: all 0.3s ease;
            }

            .role-select:focus {
                border-color: var(--primary-color);
                box-shadow: 0 0 0 0.2rem rgba(3, 96, 217, 0.25);
            }

            .update-btn {
                background: var(--gradient-primary);
                border: none;
                color: white;
                padding: 8px 20px;
                border-radius: 20px;
                font-size: 0.85rem;
                font-weight: 600;
                transition: all 0.3s ease;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .update-btn:hover {
                transform: translateY(-2px);
                box-shadow: var(--shadow-light);
                color: white;
            }

            .update-btn:disabled {
                background: #ccc;
                cursor: not-allowed;
                transform: none;
                box-shadow: none;
            }

            .user-avatar {
                width: 50px;
                height: 50px;
                border-radius: 50%;
                background: var(--gradient-primary);
                display: flex;
                align-items: center;
                justify-content: center;
                color: white;
                font-weight: 700;
                font-size: 1.2rem;
                margin-right: 15px;
            }

            .user-info {
                display: flex;
                align-items: center;
            }

            .user-details {
                flex: 1;
            }

            .user-name {
                font-weight: 600;
                color: var(--text-dark);
            }

            .user-email {
                color: var(--text-light);
                font-size: 0.9rem;
            }

            .alert {
                border-radius: 15px;
                border: none;
                padding: 20px 25px;
                margin-bottom: 25px;
                box-shadow: var(--shadow-light);
            }

            .alert-success {
                background: #d4edda;
                color: #155724;
                border-left: 4px solid var(--success-color);
            }

            .alert-danger {
                background: #f8d7da;
                color: #721c24;
                border-left: 4px solid var(--danger-color);
            }

            .back-btn {
                background: #6c757d;
                border: none;
                color: white;
                padding: 12px 30px;
                border-radius: 25px;
                font-weight: 600;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 10px;
                transition: all 0.3s ease;
            }

            .back-btn:hover {
                background: #5a6268;
                transform: translateY(-2px);
                color: white;
                text-decoration: none;
            }

            @media (max-width: 768px) {
                .stats-container {
                    grid-template-columns: 1fr;
                }
                
                .page-title {
                    font-size: 2rem;
                }
                
                .users-table th,
                .users-table td {
                    padding: 15px 10px;
                    font-size: 0.9rem;
                }
                
                .user-avatar {
                    width: 40px;
                    height: 40px;
                    font-size: 1rem;
                }
            }
        </style>
    </head>
    <body>
        <%
        // Get user information for access control
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/jsp/homepage.jsp");
            return;
        }
        
        // Get attributes from controller
        List<User> allUsers = (List<User>) request.getAttribute("allUsers");
        Integer doctorCount = (Integer) request.getAttribute("doctorCount");
        Integer receptionistCount = (Integer) request.getAttribute("receptionistCount");
        Integer patientCount = (Integer) request.getAttribute("patientCount");
        Integer adminCount = (Integer) request.getAttribute("adminCount");
        Integer totalUsers = (Integer) request.getAttribute("totalUsers");
        
        String successMessage = (String) request.getAttribute("successMessage");
        String errorMessage = (String) request.getAttribute("errorMessage");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        %>
        
        <div class="main-container">
            <!-- Page Header -->
            <div class="page-header">
                <h1 class="page-title">
                    <i class="bi bi-shield-check"></i>
                    Quản lý Phân quyền
                </h1>
                <p class="page-subtitle">
                    Quản lý và phân quyền cho tất cả người dùng trong hệ thống
                </p>
                <a href="${pageContext.request.contextPath}/jsp/homepage.jsp" class="back-btn">
                    <i class="bi bi-arrow-left"></i>
                    Quay lại trang chủ
                </a>
            </div>
            
            <!-- Success/Error Messages -->
            <% if (successMessage != null) { %>
                <div class="alert alert-success">
                    <i class="bi bi-check-circle"></i>
                    <%= successMessage %>
                </div>
            <% } %>
            
            <% if (errorMessage != null) { %>
                <div class="alert alert-danger">
                    <i class="bi bi-exclamation-circle"></i>
                    <%= errorMessage %>
                </div>
            <% } %>
            
            <!-- Statistics Cards -->
            <div class="stats-container">
                <div class="stat-card total">
                    <div class="stat-icon">
                        <i class="bi bi-people"></i>
                    </div>
                    <h2 class="stat-number"><%= totalUsers != null ? totalUsers : 0 %></h2>
                    <p class="stat-label">Tổng số người dùng</p>
                </div>
                
                <div class="stat-card admin">
                    <div class="stat-icon">
                        <i class="bi bi-shield-fill"></i>
                    </div>
                    <h2 class="stat-number"><%= adminCount != null ? adminCount : 0 %></h2>
                    <p class="stat-label">Quản trị viên</p>
                </div>
                
                <div class="stat-card doctor">
                    <div class="stat-icon">
                        <i class="bi bi-person-fill-check"></i>
                    </div>
                    <h2 class="stat-number"><%= doctorCount != null ? doctorCount : 0 %></h2>
                    <p class="stat-label">Bác sĩ</p>
                </div>
                
                <div class="stat-card receptionist">
                    <div class="stat-icon">
                        <i class="bi bi-person-badge"></i>
                    </div>
                    <h2 class="stat-number"><%= receptionistCount != null ? receptionistCount : 0 %></h2>
                    <p class="stat-label">Lễ tân</p>
                </div>
                
                <div class="stat-card patient">
                    <div class="stat-icon">
                        <i class="bi bi-person"></i>
                    </div>
                    <h2 class="stat-number"><%= patientCount != null ? patientCount : 0 %></h2>
                    <p class="stat-label">Bệnh nhân</p>
                </div>
            </div>
            
            <!-- Users Table -->
            <div class="users-table-container">
                <div class="table-header">
                    <i class="bi bi-table"></i>
                    Danh sách người dùng và phân quyền
                </div>
                
                <% if (allUsers != null && !allUsers.isEmpty()) { %>
                    <div class="table-responsive">
                        <table class="table users-table">
                            <thead>
                                <tr>
                                    <th>Người dùng</th>
                                    <th>Quyền hiện tại</th>
                                    <th>Ngày tạo</th>
                                    <th>Thay đổi quyền</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (User user : allUsers) { 
                                    String avatarLetter = user.getFullName() != null && !user.getFullName().isEmpty() 
                                        ? user.getFullName().substring(0, 1).toUpperCase() 
                                        : "U";
                                    String roleCssClass = user.getRole().getValue().toLowerCase();
                                    boolean isAdmin = user.getRole() == User.Role.ADMIN;
                                    boolean isSelf = user.getId().equals(currentUser.getId());
                                %>
                                    <tr>
                                        <td>
                                            <div class="user-info">
                                                <div class="user-avatar">
                                                    <%= avatarLetter %>
                                                </div>
                                                <div class="user-details">
                                                    <div class="user-name">
                                                        <%= user.getFullName() != null ? user.getFullName() : "N/A" %>
                                                        <% if (isSelf) { %>
                                                            <span class="badge bg-secondary ms-2">Bạn</span>
                                                        <% } %>
                                                    </div>
                                                    <div class="user-email"><%= user.getEmail() %></div>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <span class="role-badge <%= roleCssClass %>">
                                                <%= user.getRole().getValue() %>
                                            </span>
                                        </td>
                                        <td>
                                            <%= user.getCreatedAt() != null ? dateFormat.format(user.getCreatedAt()) : "N/A" %>
                                        </td>
                                        <td>
                                            <% if (isAdmin || isSelf) { %>
                                                <select class="form-select role-select" disabled>
                                                    <option>Không thể thay đổi</option>
                                                </select>
                                            <% } else { %>
                                                <form method="post" action="${pageContext.request.contextPath}/admin/authorization/update" style="display: inline;">
                                                    <input type="hidden" name="userId" value="<%= user.getId() %>">
                                                    <select name="newRole" class="form-select role-select" required>
                                                        <option value="">-- Chọn quyền --</option>
                                                        <option value="doctor" <%= user.getRole() == User.Role.DOCTOR ? "selected" : "" %>>
                                                            Bác sĩ
                                                        </option>
                                                        <option value="receptionist" <%= user.getRole() == User.Role.RECEPTIONIST ? "selected" : "" %>>
                                                            Lễ tân
                                                        </option>
                                                        <option value="patient" <%= user.getRole() == User.Role.PATIENT ? "selected" : "" %>>
                                                            Bệnh nhân
                                                        </option>
                                                    </select>
                                            <% } %>
                                        </td>
                                        <td>
                                            <% if (isAdmin || isSelf) { %>
                                                <button class="btn update-btn" disabled>
                                                    <i class="bi bi-lock"></i>
                                                    Không thể cập nhật
                                                </button>
                                            <% } else { %>
                                                    <button type="submit" class="btn update-btn">
                                                        <i class="bi bi-arrow-repeat"></i>
                                                        Cập nhật
                                                    </button>
                                                </form>
                                            <% } %>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } else { %>
                    <div class="p-5 text-center">
                        <i class="bi bi-people" style="font-size: 4rem; color: var(--text-light);"></i>
                        <h4 class="mt-3">Không có người dùng nào</h4>
                        <p class="text-muted">Danh sách người dùng trống hoặc có lỗi khi tải dữ liệu.</p>
                    </div>
                <% } %>
            </div>
        </div>
        
        <!-- Bootstrap Bundle with Popper -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Add confirmation dialog for role updates
                const updateForms = document.querySelectorAll('form[action*="authorization/update"]');
                updateForms.forEach(form => {
                    form.addEventListener('submit', function (e) {
                        const userId = form.querySelector('input[name="userId"]').value;
                        const newRole = form.querySelector('select[name="newRole"]').value;
                        const userNameElement = form.closest('tr').querySelector('.user-name');
                        const userName = form.closest('tr').querySelector('.user-name').textContent.trim();

                        if (!newRole) {
                            e.preventDefault();
                            alert('Vui lòng chọn quyền hạn mới.');
                            return;
                        }
                        
                        const roleNames = {
                            'doctor': 'Bác sĩ',
                            'receptionist': 'Lễ tân',
                            'patient': 'Bệnh nhân'
                        };
                        
                        const roleName = roleNames[newRole] || 'quyền mới';
                        
                        const confirmed = confirm(
                            'Bạn có chắc chắn muốn thay đổi quyền hạn của "' + userName + '" thành "' + newRole + '"?'
                        );
                        
                        if (!confirmed) {
                            e.preventDefault();
                        }
                    });
                });
                
                // Auto-hide success/error messages after 5 seconds
                setTimeout(() => {
                    const alerts = document.querySelectorAll('.alert');
                    alerts.forEach(alert => {
                        alert.style.transition = 'opacity 0.5s ease';
                        alert.style.opacity = '0';
                        setTimeout(() => {
                            alert.remove();
                        }, 500);
                    });
                }, 5000);
            });
        </script>
    </body>
</html> 