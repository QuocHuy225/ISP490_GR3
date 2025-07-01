<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch Hẹn Của Tôi - Phòng khám Ánh Dương</title>
    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- Google Fonts - Inter -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <!-- Bootstrap Icons (Crucial for sidebar and navbar icons, though Font Awesome is used in image) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/patient-appointment-schedule.css">
    <script> const contextPath = "<%= request.getContextPath() %>";</script>
</head>
<body class="flex flex-col min-h-screen">
    <%
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
        Object userObj = session.getAttribute("user");
        String loggedInPatientFullName = "User";
        String loggedInPatientId = ""; // Initialize loggedInPatientId
        String userRoleDisplay = "Patient";
        if (userObj instanceof User) {
            User user = (User) userObj;
            loggedInPatientFullName = user.getFullName() != null ? user.getFullName() : user.getEmail();
            loggedInPatientId = user.getId(); // Assuming User model has a getId() method for the unique user ID
            userRoleDisplay = user.getRole() != null ? user.getRole().getValue() : "Patient";
        }
        boolean isAdmin = (currentRole == User.Role.ADMIN);
        boolean isReceptionist = (currentRole == User.Role.RECEPTIONIST);
        boolean isDoctor = (currentRole == User.Role.DOCTOR);
        boolean isPatient = (currentRole == User.Role.PATIENT);
    %>

    <!-- Hidden inputs to pass patient data to JavaScript -->
    <input type="hidden" id="loggedInPatientId" value="<%= loggedInPatientId %>">
    <input type="hidden" id="loggedInPatientFullName" value="<%= loggedInPatientFullName %>">

    <!-- Sidebar -->
    <nav id="sidebar">
        <div class="sidebar-header">
            <h3>MENU</h3>
        </div>
        <ul class="list-unstyled components">
            <li>
                <a href="<%= contextPath %>/homepage">
                    <i class="fas fa-home"></i> Trang chủ
                </a>
            </li>
            <li class="active">
                <a href="<%= contextPath %>/makeappointments">
                    <i class="fas fa-calendar-check"></i> Đặt lịch hẹn
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="fas fa-calendar-alt"></i> Lịch hẹn của tôi
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="fas fa-notes-medical"></i> Hồ sơ sức khỏe
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="fas fa-hand-holding-medical"></i> Dịch vụ
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="fas fa-comment-dots"></i> Liên hệ bác sĩ
                </a>
            </li>
            <% if (isReceptionist || isAdmin || isDoctor) { %>
            <li>
                <a href="<%= contextPath %>/appointments">
                    <i class="fas fa-chart-bar"></i> Thống kê lịch hẹn
                </a>
            </li>
            <% } %>
        </ul>
    </nav>

    <!-- Overlay for mobile sidebar -->
    <div id="sidebarOverlay"></div>

    <!-- Main Content Wrapper -->
    <div id="main-wrapper" class="flex-grow flex flex-col">
        <!-- Top Navbar -->
        <nav class="navbar navbar-expand-lg top-navbar">
            <div class="container-fluid flex justify-between items-center w-full">
                <button type="button" id="sidebarToggle" class="toggle-btn">
                    <i class="fas fa-bars"></i> <!-- Toggle button (3 horizontal lines) -->
                </button>

                <a class="navbar-brand-custom" href="#">
                    <span class="text-primary">Ánh Dương</span> <span class="text-dark">Clinic</span>
                </a>

                <div class="navbar-search mx-auto">
                    <i class="fas fa-search search-icon"></i> <!-- Search icon -->
                    <input type="text" class="form-control" placeholder="Tìm kiếm...">
                </div>

                <div class="dropdown user-dropdown">
                    <button class="btn dropdown-toggle" type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                        <div class="user-profile-icon">
                            <i class="fas fa-user"></i> <!-- Avatar icon -->
                        </div>
                        <div class="user-info d-none d-md-block">
                            <div class="user-name"><%= loggedInPatientFullName %></div>
                            <div class="user-role"><%= userRoleDisplay %></div>
                        </div>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                        <% if (isAdmin) { %>
                        <li>
                            <a class="dropdown-item" href="<%= contextPath %>/admin/authorization">
                                <i class="fas fa-users-cog"></i>
                                <span>Quản lý người dùng</span>
                            </a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <% } %>
                        <li>
                            <a class="dropdown-item" href="<%= contextPath %>/user/profile">
                                <i class="fas fa-user-circle"></i>
                                <span>Thông tin cá nhân</span>
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="<%= contextPath %>/user/change-password">
                                <i class="fas fa-key"></i>
                                <span>Đổi mật khẩu</span>
                            </a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <li>
                            <a class="dropdown-item text-danger" id="logoutBtn" href="<%= contextPath %>/auth/logout">
                                <i class="fas fa-sign-out-alt"></i>
                                <span>Đăng xuất</span>
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>

        <!-- Main Content Area -->
        <main class="container mx-auto p-6 flex-grow">
            <div class="bg-white rounded-lg shadow-lg p-6">
                <!-- Tabs Navigation -->
                <div class="flex border-b border-gray-200 mb-6">
                    <button id="myAppointmentsTab" class="px-6 py-3 text-lg font-medium text-blue-600 border-b-2 border-blue-600 focus:outline-none">
                        Lịch hẹn của tôi
                    </button>
                    <button id="bookAppointmentTab" class="px-6 py-3 text-lg font-medium text-gray-600 hover:text-blue-600 hover:border-blue-600 border-b-2 border-transparent focus:outline-none">
                        Đặt lịch hẹn mới
                    </button>
                </div>

                <!-- My Appointments View -->
                <div id="myAppointmentsView" class="tab-content">
                    <h2 class="text-2xl font-semibold text-gray-800 mb-4">Danh sách lịch hẹn</h2>
                    <div class="table-container overflow-x-auto rounded-lg border border-gray-200">
                        <table class="min-w-full divide-y divide-gray-200">
                            <thead class="bg-gray-50">
                                <tr>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mã lịch hẹn</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Ngày</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Giờ</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Bác sĩ</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Dịch vụ</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng thái</th>
                                    <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody id="appointmentsTableBody" class="bg-white divide-y divide-gray-200">
                                <!-- Appointments will be loaded here by JavaScript -->
                                <tr>
                                    <td colspan="7" class="px-6 py-4 whitespace-nowrap text-center text-gray-500">Đang tải lịch hẹn...</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <p id="noAppointmentsMessage" class="text-center text-gray-500 mt-4 hidden">Bạn chưa có lịch hẹn nào.</p>
                </div>

                <!-- Book New Appointment View -->
                <div id="bookAppointmentView" class="tab-content hidden">
                    <h2 class="text-2xl font-semibold text-gray-800 mb-4">Đặt lịch hẹn mới</h2>
                    <form id="newAppointmentForm" class="space-y-4">
                        <div>
                            <label for="patientFullName" class="block text-sm font-medium text-gray-700">Họ và tên bệnh nhân</label>
                            <input type="text" id="patientFullName" name="patientFullName" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm" readonly>
                        </div>
                        <div>
                            <label for="newAppointmentDate" class="block text-sm font-medium text-gray-700">Ngày hẹn</label>
                            <input type="date" id="newAppointmentDate" name="appointmentDate" required class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm">
                        </div>
                        <div>
                            <label for="newAppointmentTime" class="block text-sm font-medium text-gray-700">Giờ hẹn</label>
                            <input type="time" id="newAppointmentTime" name="appointmentTime" required class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm">
                        </div>
                        <div>
                            <label for="newAppointmentDoctor" class="block text-sm font-medium text-gray-700">Bác sĩ</label>
                            <select id="newAppointmentDoctor" name="doctor" required class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm">
                                <option value="">-- Chọn bác sĩ --</option>
                                <!-- Doctors will be loaded here by JavaScript -->
                            </select>
                        </div>
                        <div>
                            <label for="newAppointmentService" class="block text-sm font-medium text-gray-700">Dịch vụ</label>
                            <select id="newAppointmentService" name="service" required class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm">
                                <option value="">-- Chọn dịch vụ --</option>
                                <!-- Services will be loaded here by JavaScript -->
                            </select>
                        </div>
                        <div>
                            <label for="newAppointmentNotes" class="block text-sm font-medium text-gray-700">Ghi chú (tùy chọn)</label>
                            <textarea id="newAppointmentNotes" name="notes" rows="3" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"></textarea>
                        </div>
                        <div class="flex justify-end">
                            <button type="submit" class="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                                <i class="fas fa-plus-circle mr-2"></i>Đặt lịch hẹn
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </main>

        <!-- Footer -->
        <footer class="bg-gray-800 text-white p-4 mt-6">
            <div class="container mx-auto text-center text-sm">
                &copy; 2025 Phòng khám Ánh Dương. Tất cả quyền được bảo lưu.
            </div>
        </footer>
    </div>

    <!-- Modals (kept in JSP for simplicity as they are part of the page structure) -->

    <!-- Appointment Details Modal -->
    <div id="appointmentDetailsModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center hidden">
        <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-md mx-auto">
            <div class="flex justify-between items-center border-b pb-3 mb-4">
                <h3 class="text-xl font-semibold text-gray-800">Chi tiết lịch hẹn</h3>
                <button class="text-gray-500 hover:text-gray-700 text-2xl" onclick="closeModal('appointmentDetailsModal')">&times;</button>
            </div>
            <div class="modal-body-scrollable">
                <p class="mb-2"><strong class="text-gray-700">Mã lịch hẹn:</strong> <span id="detailAppointmentCode"></span></p>
                <p class="mb-2"><strong class="text-gray-700">Ngày:</strong> <span id="detailAppointmentDate"></span></p>
                <p class="mb-2"><strong class="text-gray-700">Giờ:</strong> <span id="detailAppointmentTime"></span></p>
                <p class="mb-2"><strong class="text-gray-700">Bác sĩ:</strong> <span id="detailDoctorName"></span></p>
                <p class="mb-2"><strong class="text-gray-700">Dịch vụ:</strong> <span id="detailService"></span></p>
                <p class="mb-2"><strong class="text-gray-700">Trạng thái:</strong> <span id="detailStatus" class="font-semibold"></span></p>
                <p class="mb-2"><strong class="text-gray-700">Ghi chú:</strong> <span id="detailNotes"></span></p>
            </div>
            <div class="flex justify-end mt-4">
                <button class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-md" onclick="closeModal('appointmentDetailsModal')">Đóng</button>
            </div>
        </div>
    </div>

    <!-- Confirm Action Modal -->
    <div id="confirmActionModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center hidden">
        <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-sm mx-auto">
            <div class="flex justify-between items-center border-b pb-3 mb-4">
                <h3 class="text-xl font-semibold text-gray-800">Xác nhận hành động</h3>
                <button class="text-gray-500 hover:text-gray-700 text-2xl" onclick="closeModal('confirmActionModal')">&times;</button>
            </div>
            <p id="confirmActionMessage" class="mb-4 text-gray-700"></p>
            <div class="flex justify-end space-x-3">
                <button class="bg-gray-300 hover:bg-gray-400 text-gray-800 px-4 py-2 rounded-md" onclick="closeModal('confirmActionModal')">Hủy</button>
                <button id="confirmActionButton" class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-md">Xác nhận</button>
            </div>
        </div>
    </div>

    <!-- Message Modal -->
    <div id="messageModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center hidden">
        <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-sm mx-auto">
            <div class="flex justify-between items-center border-b pb-3 mb-4">
                <h3 id="messageModalTitle" class="text-xl font-semibold text-gray-800">Thông báo</h3>
                <button class="text-gray-500 hover:text-gray-700 text-2xl" onclick="closeModal('messageModal')">&times;</button>
            </div>
            <p id="messageModalContent" class="mb-4 text-gray-700"></p>
            <div class="flex justify-end">
                <button class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-md" onclick="closeModal('messageModal')">Đóng</button>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Custom JavaScript -->
    <script src="${pageContext.request.contextPath}/js/patient-appointment-schedule.js"></script>
</body>
</html>
