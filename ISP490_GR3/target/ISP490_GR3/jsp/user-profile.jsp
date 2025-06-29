<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông tin cá nhân - Ánh Dương Clinic</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #0066ff;
            --primary-dark: #0052cc;
            --primary-light: #e6f2ff;
            --secondary-color: #00d4ff;
            --success-color: #00c851;
            --warning-color: #ffbb33;
            --danger-color: #ff4444;
            --dark-color: #2c3e50;
            --light-color: #f8f9fc;
            --border-radius: 20px;
            --shadow-light: 0 5px 25px rgba(0, 102, 255, 0.1);
            --shadow-medium: 0 10px 40px rgba(0, 102, 255, 0.15);
            --shadow-heavy: 0 20px 60px rgba(0, 0, 0, 0.1);
            --gradient-primary: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            --gradient-soft: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        * {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
            -webkit-font-smoothing: antialiased;
            -moz-osx-font-smoothing: grayscale;
        }
        
        body {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            min-height: 100vh;
            padding: 2rem 0;
        }
        
        /* Custom Scrollbar */
        ::-webkit-scrollbar {
            width: 8px;
        }
        
        ::-webkit-scrollbar-track {
            background: #f1f3f4;
            border-radius: 10px;
        }
        
        ::-webkit-scrollbar-thumb {
            background: var(--primary-color);
            border-radius: 10px;
        }
        
        ::-webkit-scrollbar-thumb:hover {
            background: var(--primary-dark);
        }
        
        /* Breadcrumb */
        .breadcrumb {
            background: rgba(255, 255, 255, 0.9);
            backdrop-filter: blur(10px);
            border-radius: 15px;
            padding: 1rem 1.5rem;
            border: 1px solid rgba(255, 255, 255, 0.3);
            box-shadow: var(--shadow-light);
        }
        
        .breadcrumb-item a {
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        
        .breadcrumb-item a:hover {
            color: var(--primary-dark);
            transform: translateX(2px);
        }
        
        /* Profile Card */
        .profile-container {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            border-radius: var(--border-radius);
            box-shadow: var(--shadow-heavy);
            overflow: hidden;
            border: 1px solid rgba(255, 255, 255, 0.3);
            position: relative;
        }
        
        .profile-container::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 200px;
            background: var(--gradient-primary);
            z-index: 1;
        }
        
        .profile-header {
            position: relative;
            z-index: 2;
            padding: 3rem 2rem 2rem;
            text-align: center;
            color: white;
        }
        
        .profile-avatar {
            width: 140px;
            height: 140px;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.2);
            backdrop-filter: blur(10px);
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 1.5rem;
            border: 4px solid rgba(255, 255, 255, 0.3);
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }
        
        .profile-avatar::before {
            content: '';
            position: absolute;
            top: -50%;
            left: -50%;
            width: 200%;
            height: 200%;
            background: linear-gradient(45deg, transparent, rgba(255,255,255,0.3), transparent);
            transform: rotate(-45deg);
            transition: all 0.6s ease;
            opacity: 0;
        }
        
        .profile-avatar:hover::before {
            opacity: 1;
            animation: shimmer 1.5s ease-in-out;
        }
        
        @keyframes shimmer {
            0% { transform: translateX(-100%) translateY(-100%) rotate(-45deg); }
            100% { transform: translateX(100%) translateY(100%) rotate(-45deg); }
        }
        
        .profile-avatar:hover {
            transform: scale(1.05);
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.3);
        }
        
        .profile-name {
            font-size: 2.2rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        
        .profile-email {
            font-size: 1.1rem;
            opacity: 0.9;
            margin-bottom: 1rem;
        }
        
        .profile-role-badge {
            background: rgba(255, 255, 255, 0.2);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.3);
            padding: 0.5rem 1.5rem;
            border-radius: 25px;
            font-weight: 600;
            font-size: 0.9rem;
            display: inline-block;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        
        /* Profile Body */
        .profile-body {
            padding: 2.5rem;
            background: white;
            position: relative;
            z-index: 2;
        }
        
        .info-section {
            margin-bottom: 2rem;
        }
        
        .section-title {
            font-size: 1.3rem;
            font-weight: 600;
            color: var(--dark-color);
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .section-title::after {
            content: '';
            flex: 1;
            height: 2px;
            background: linear-gradient(90deg, var(--primary-color), transparent);
        }
        
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 1.5rem;
        }
        
        .info-item {
            background: var(--light-color);
            padding: 1.5rem;
            border-radius: 15px;
            border: 1px solid rgba(0, 102, 255, 0.1);
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }
        
        .info-item::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 4px;
            height: 100%;
            background: var(--gradient-primary);
            transform: scaleY(0);
            transition: transform 0.3s ease;
        }
        
        .info-item:hover {
            transform: translateY(-2px);
            box-shadow: var(--shadow-medium);
            border-color: var(--primary-color);
        }
        
        .info-item:hover::before {
            transform: scaleY(1);
        }
        
        .info-label {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            font-weight: 600;
            color: var(--primary-color);
            margin-bottom: 0.5rem;
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .info-value {
            font-size: 1.1rem;
            color: var(--dark-color);
            font-weight: 500;
        }
        
        .info-value.empty {
            color: #999;
            font-style: italic;
        }
        
        /* Action Buttons */
        .action-buttons {
            display: flex;
            gap: 1rem;
            justify-content: center;
            margin-top: 2.5rem;
            flex-wrap: wrap;
        }
        
        .btn-modern {
            padding: 1rem 2rem;
            border-radius: 50px;
            font-weight: 600;
            font-size: 1rem;
            border: none;
            cursor: pointer;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        
        .btn-modern::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
            transition: all 0.6s ease;
        }
        
        .btn-modern:hover::before {
            left: 100%;
        }
        
        .btn-primary-modern {
            background: var(--gradient-primary);
            color: white;
            box-shadow: 0 5px 20px rgba(0, 102, 255, 0.3);
        }
        
        .btn-primary-modern:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 30px rgba(0, 102, 255, 0.4);
        }
        
        .btn-secondary-modern {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            box-shadow: 0 5px 20px rgba(245, 87, 108, 0.3);
        }
        
        .btn-secondary-modern:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 30px rgba(245, 87, 108, 0.4);
        }
        
        /* Modal Enhancements */
        .modal-content {
            border: none;
            border-radius: var(--border-radius);
            box-shadow: var(--shadow-heavy);
            backdrop-filter: blur(20px);
            overflow: hidden;
        }
        
        .modal-header {
            background: var(--gradient-primary);
            color: white;
            border-bottom: none;
            padding: 2rem;
        }
        
        .modal-title {
            font-weight: 700;
            font-size: 1.5rem;
        }
        
        .modal-body {
            padding: 2.5rem;
            background: white;
        }
        
        .form-floating {
            margin-bottom: 1.5rem;
        }
        
        .form-control, .form-select {
            border-radius: 15px;
            border: 2px solid #e9ecef;
            padding: 1rem;
            font-size: 1rem;
            transition: all 0.3s ease;
            background: var(--light-color);
        }
        
        .form-control:focus, .form-select:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 0.3rem rgba(0, 102, 255, 0.1);
            background: white;
            transform: translateY(-2px);
        }
        
        .form-label {
            font-weight: 600;
            color: var(--dark-color);
            margin-bottom: 0.5rem;
        }
        
        /* Password Field Styling */
        .password-field {
            position: relative;
        }
        
        .password-field .form-control {
            padding-right: 3.5rem;
        }
        
        .password-toggle {
            position: absolute;
            right: 1rem;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            color: var(--primary-color);
            cursor: pointer;
            z-index: 10;
            font-size: 1.2rem;
            transition: all 0.3s ease;
        }
        
        .password-toggle:hover {
            color: var(--primary-dark);
            transform: translateY(-50%) scale(1.1);
        }
        
        /* Alerts */
        .alert {
            border: none;
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 2rem;
            backdrop-filter: blur(10px);
            border-left: 4px solid;
        }
        
        .alert-success {
            background: rgba(0, 200, 81, 0.1);
            color: var(--success-color);
            border-left-color: var(--success-color);
        }
        
        .alert-danger {
            background: rgba(255, 68, 68, 0.1);
            color: var(--danger-color);
            border-left-color: var(--danger-color);
        }
        
        /* Animation Classes */
        .fade-in {
            animation: fadeIn 0.5s ease forwards;
        }
        
        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .slide-up {
            animation: slideUp 0.6s ease forwards;
        }
        
        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        /* Responsive Design */
        @media (max-width: 768px) {
            .profile-header {
                padding: 2rem 1rem;
            }
            
            .profile-body {
                padding: 1.5rem;
            }
            
            .info-grid {
                grid-template-columns: 1fr;
            }
            
            .action-buttons {
                flex-direction: column;
            }
            
            .btn-modern {
                width: 100%;
            }
        }
        
        /* Required field indicator */
        .required::after {
            content: ' *';
            color: var(--danger-color);
        }
        
        /* Info box styling */
        .info-box {
            background: var(--primary-light);
            border: 1px solid rgba(0, 102, 255, 0.2);
            border-radius: 15px;
            padding: 1.5rem;
            margin: 1.5rem 0;
        }
        
        .info-box h6 {
            color: var(--primary-color);
            font-weight: 600;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <div class="container py-4">
        <!-- Navigation -->
        <div class="row mb-4">
            <div class="col">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/jsp/homepage.jsp">
                                <i class="bi bi-house-door me-1"></i>Trang chủ
                            </a>
                        </li>
                        <li class="breadcrumb-item active" aria-current="page">Thông tin cá nhân</li>
                    </ol>
                </nav>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <% 
        String successMessage = (String) session.getAttribute("successMessage");
        String errorMessage = (String) session.getAttribute("errorMessage");
        
        if (successMessage != null) { 
            session.removeAttribute("successMessage");
        %>
            <div class="alert alert-success alert-dismissible fade show fade-in" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>
                <%= successMessage %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>
        
        <% if (errorMessage != null) { 
            session.removeAttribute("errorMessage");
        %>
            <div class="alert alert-danger alert-dismissible fade show fade-in" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                <%= errorMessage %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>

        <!-- Profile Container -->
        <div class="row justify-content-center">
            <div class="col-xl-10">
                <div class="profile-container slide-up">
                    <!-- Profile Header -->
                    <div class="profile-header">
                        <div class="profile-avatar">
                            <i class="bi bi-person-fill" style="font-size: 4rem;"></i>
                        </div>
                    </div>
                    
                    <!-- Profile Body -->
                    <div class="profile-body">
                        <!-- Personal Information Section -->
                        <div class="info-section">
                            <h3 class="section-title">
                                <i class="bi bi-person-lines-fill"></i>
                                Thông tin cá nhân
                            </h3>
                            
                            <div class="info-grid">
                                <div class="info-item">
                                    <div class="info-label">
                                        <i class="bi bi-telephone-fill"></i>
                                        Số điện thoại
                                    </div>
                                    <div class="info-value <%= ((User) request.getAttribute("user")).getPhone() == null ? "empty" : "" %>">
                                        <%= ((User) request.getAttribute("user")).getPhone() != null ? 
                                            ((User) request.getAttribute("user")).getPhone() : "Chưa cập nhật" %>
                                    </div>
                                </div>
                                
                                <div class="info-item">
                                    <div class="info-label">
                                        <i class="bi bi-clock-history"></i>
                                        Ngày tạo tài khoản
                                    </div>
                                    <div class="info-value">
                                        <%= ((User) request.getAttribute("user")).getCreatedAt() != null ? 
                                            ((User) request.getAttribute("user")).getCreatedAt().toString() : "Không xác định" %>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Action Buttons -->
                        <div class="action-buttons">
                            <button type="button" class="btn-modern btn-primary-modern" data-bs-toggle="modal" data-bs-target="#editProfileModal">
                                <i class="bi bi-pencil-square me-2"></i>Chỉnh sửa thông tin
                            </button>
                            <button type="button" class="btn-modern btn-secondary-modern" data-bs-toggle="modal" data-bs-target="#changePasswordModal">
                                <i class="bi bi-key-fill me-2"></i>Đổi mật khẩu
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Edit Profile Modal -->
    <div class="modal fade" id="editProfileModal" tabindex="-1" aria-labelledby="editProfileModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editProfileModalLabel">
                        <i class="bi bi-pencil-square me-2"></i>Chỉnh sửa thông tin cá nhân
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form action="${pageContext.request.contextPath}/user/update-profile" method="post">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-floating mb-3">
                                    <input type="text" class="form-control" id="fullName" name="fullName" 
                                           value="<%= ((User) request.getAttribute("user")).getFullName() != null ? 
                                                   ((User) request.getAttribute("user")).getFullName() : "" %>" 
                                           placeholder="Họ và tên" required>
                                    <label for="fullName" class="required">
                                        <i class="bi bi-person me-1"></i>Họ và tên
                                    </label>
                                </div>
                            </div>
                            
                            <div class="col-md-6">
                                <div class="form-floating mb-3">
                                    <input type="tel" class="form-control" id="phone" name="phone" 
                                           value="<%= ((User) request.getAttribute("user")).getPhone() != null ? 
                                                   ((User) request.getAttribute("user")).getPhone() : "" %>"
                                           placeholder="Số điện thoại">
                                    <label for="phone">
                                        <i class="bi bi-telephone me-1"></i>Số điện thoại
                                    </label>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Read-only fields info -->
                        <div class="info-box">
                            <h6><i class="bi bi-info-circle me-2"></i>Thông tin không thể thay đổi</h6>
                            <div class="row">
                                <div class="col-md-6">
                                    <strong>Email:</strong> <%= ((User) request.getAttribute("user")).getEmail() %>
                                </div>
                                <div class="col-md-6">
                                    <strong>Vai trò:</strong> 
                                    <span class="badge bg-primary ms-1">
                                        <%= ((User) request.getAttribute("user")).getRole() != null ? 
                                            ((User) request.getAttribute("user")).getRole().getValue() : "Patient" %>
                                    </span>
                                </div>
                            </div>
                        </div>
                        
                        <div class="text-end">
                            <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">
                                <i class="bi bi-x-lg me-1"></i>Hủy
                            </button>
                            <button type="submit" class="btn-modern btn-primary-modern">
                                <i class="bi bi-check-lg me-1"></i>Lưu thay đổi
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Change Password Modal -->
    <div class="modal fade" id="changePasswordModal" tabindex="-1" aria-labelledby="changePasswordModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="changePasswordModalLabel">
                        <i class="bi bi-key-fill me-2"></i>Đổi mật khẩu
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <!-- Security Info -->
                    <div class="info-box">
                        <h6><i class="bi bi-shield-check me-2"></i>Bảo mật tài khoản</h6>
                        <p class="mb-0">Hãy chọn mật khẩu mạnh để bảo vệ tài khoản của bạn. Mật khẩu phải có ít nhất 6 ký tự.</p>
                    </div>
                    
                    <form action="${pageContext.request.contextPath}/user/change-password" method="post" id="changePasswordForm">
                        <div class="form-floating mb-3">
                            <div class="password-field">
                                <input type="password" class="form-control" id="currentPassword" name="currentPassword" placeholder="Mật khẩu hiện tại" required>
                                <button type="button" class="password-toggle" onclick="togglePassword('currentPassword')">
                                    <i class="bi bi-eye" id="currentPasswordIcon"></i>
                                </button>
                            </div>
                        </div>
                        
                        <div class="form-floating mb-3">
                            <div class="password-field">
                                <input type="password" class="form-control" id="newPassword" name="newPassword" minlength="6" placeholder="Mật khẩu mới" required>
                                <button type="button" class="password-toggle" onclick="togglePassword('newPassword')">
                                    <i class="bi bi-eye" id="newPasswordIcon"></i>
                                </button>
                            </div>
                        </div>
                        
                        <div class="form-floating mb-3">
                            <div class="password-field">
                                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" minlength="6" placeholder="Xác nhận mật khẩu mới" required>
                                <button type="button" class="password-toggle" onclick="togglePassword('confirmPassword')">
                                    <i class="bi bi-eye" id="confirmPasswordIcon"></i>
                                </button>
                            </div>
                            <div id="passwordMismatch" class="text-danger mt-1" style="display: none;">
                                <i class="bi bi-exclamation-triangle me-1"></i>
                                Mật khẩu xác nhận không khớp
                            </div>
                        </div>
                        
                        <div class="text-end">
                            <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">
                                <i class="bi bi-x-lg me-1"></i>Hủy
                            </button>
                            <button type="submit" class="btn-modern btn-secondary-modern">
                                <i class="bi bi-check-lg me-1"></i>Đổi mật khẩu
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // Auto-hide alerts after 5 seconds
        setTimeout(function() {
            var alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                var bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
        
        // Toggle password visibility
        function togglePassword(fieldId) {
            const field = document.getElementById(fieldId);
            const icon = document.getElementById(fieldId + 'Icon');
            
            if (field.type === 'password') {
                field.type = 'text';
                icon.className = 'bi bi-eye-slash';
            } else {
                field.type = 'password';
                icon.className = 'bi bi-eye';
            }
        }
        
        // Password confirmation validation
        document.getElementById('confirmPassword').addEventListener('input', function() {
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = this.value;
            const mismatchDiv = document.getElementById('passwordMismatch');
            
            if (confirmPassword && newPassword !== confirmPassword) {
                mismatchDiv.style.display = 'block';
                this.classList.add('is-invalid');
            } else {
                mismatchDiv.style.display = 'none';
                this.classList.remove('is-invalid');
            }
        });
        
        // Form submission validation
        document.getElementById('changePasswordForm').addEventListener('submit', function(event) {
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (newPassword !== confirmPassword) {
                event.preventDefault();
                document.getElementById('passwordMismatch').style.display = 'block';
                document.getElementById('confirmPassword').classList.add('is-invalid');
                return false;
            }
        });
        
        // Add animation delays for staggered effect
        document.addEventListener('DOMContentLoaded', function() {
            const infoItems = document.querySelectorAll('.info-item');
            infoItems.forEach((item, index) => {
                item.style.animationDelay = `${index * 0.1}s`;
                item.classList.add('fade-in');
            });
        });
    </script>
</body>
</html> 