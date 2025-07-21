<%-- 
    Document   : queue
    Created on : Jul 14, 2025, 10:46:13 AM
    Author     : FPT SHOP
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="com.mycompany.isp490_gr3.model.Appointment" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Queue Page</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/queue.css">
         
    </head>
    <body>
        <%
                    // Get user role for access control
                    Object userRole = session.getAttribute("userRole");
                    boolean isAdmin = false;
                    if (userRole != null) {
                        if (userRole instanceof User.Role) {
                            isAdmin = ((User.Role) userRole) == User.Role.ADMIN;
                        } else {
                            // Fallback for String role if User.Role enum is not used directly
                            isAdmin = "ADMIN".equalsIgnoreCase(userRole.toString());
                        }
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
        %>

        <nav id="sidebar">
            <div class="sidebar-header">
                <h3>MENU</h3>
            </div>
            <ul class="list-unstyled components">
                <% if (userRole == User.Role.DOCTOR) { %>
                <!-- Menu cho Bác sĩ -->
                <li>
                    <a href="${pageContext.request.contextPath}/homepage">
                        <i class="bi bi-speedometer2"></i> Trang chủ
                    </a>
                </li>
                <li class="active">
                    <a href="${pageContext.request.contextPath}/queue">
                        <i class="bi bi-calendar-check"></i> Lịch khám bệnh
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/doctor/patients">
                        <i class="bi bi-people"></i> Hồ sơ bệnh nhân
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/doctor/report">
                        <i class="bi bi-bar-chart-fill"></i> Báo cáo thống kê
                    </a>
                </li>
                <% } else if (userRole == User.Role.RECEPTIONIST) { %>
                 <li>
                    <a href="${pageContext.request.contextPath}/homepage">
                        <i class="bi bi-house-door-fill"></i> Trang chủ
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/appointments">
                        <i class="bi bi-calendar-check-fill"></i> Quản lý đặt lịch
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/checkin">
                        <i class="bi bi-person-check-fill"></i> Quản lý check-in
                    </a>
                </li>
                <li class="active">
                    <a href="${pageContext.request.contextPath}/queue">
                        <i class="bi bi-people-fill"></i> Quản lý hàng đợi
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/doctor/patients">
                        <i class="bi bi-people"></i> Hồ sơ bệnh nhân
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/receptionist/manage-doctor-schedule">
                        <i class="bi bi-calendar-event-fill"></i> Quản lý lịch bác sĩ
                    </a>
                </li> 
                <li>
                    <a href="${pageContext.request.contextPath}/receptionist/report">
                        <i class="bi bi-speedometer2"></i> Báo cáo thống kê
                    </a>
                </li>
                <% } %>
            </ul>
        </nav>

        <div id="content">
            <nav class="navbar navbar-expand-lg top-navbar">
                <div class="container-fluid">
                    <button type="button" id="sidebarCollapse" class="btn btn-primary">
                        <i class="bi bi-list"></i>
                    </button>

                    <div style="margin-left: 60px;">
                        <h3>
                            <span style="color: #007bff;">Ánh Dương</span>
                            <span style="color: #333;">Clinic</span>
                        </h3>
                    </div>

                    <div class="navbar-search mx-auto">
                        <i class="bi bi-search"></i>
                        <input type="text" class="form-control" placeholder="Tìm kiếm bệnh nhân, lịch hẹn, hồ sơ...">
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
                            <% if (isAdmin) { %>
                            <li>
                                <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/authorization">
                                    <i class="bi bi-people-fill"></i>
                                    <span>Quản lý người dùng</span>
                                </a>
                            </li>
                            <li><hr class="dropdown-divider"></li>
                                <% } %>
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

            <!-- Breadcrumb được căn giữa theo chiều dọc -->
            <div class="content-wrapper">
                <div class="breadcrumb-container">
                    <nav aria-label="breadcrumb" style="margin-top:17px;">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/homepage">Trang chủ</a></li>
                            <li class="breadcrumb-item active" aria-current="page">Quản lý hàng đợi</li>
                        </ol>
                    </nav>
                </div>
            </div>                          


            <form id="searchForm" action="${pageContext.request.contextPath}/queue" method="post" class="mb-4 mt-3 p-3 border rounded shadow-sm bg-light">
                <input type="hidden" name="action" value="search" />
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label for="doctorId" class="form-label">Bác sĩ</label>
                        <select class="form-select" id="doctorId" name="doctorId">
                            <option value="">--Chọn--</option>
                            <c:forEach var="doctor" items="${doctorList}">
                                <option value="${doctor.id}" <c:if test="${param.doctorId == doctor.id}">selected</c:if>>${doctor.fullName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label for="slotDate" class="form-label">Ngày hẹn</label>
                        <input type="date" class="form-control" id="slotDate" name="slotDate" value="${empty slotDate ? LocalDate.now().toString() : slotDate}">
                    </div>
                </div>
                <div class="d-flex justify-content-end gap-2">
                    <button type="button" class="btn btn-light border" id="resetFilterButton">
                        <i class="bi bi-arrow-clockwise"></i> Đặt lại bộ lọc
                    </button>
                    <button type="submit" name="submitSearch" class="btn btn-primary">
                        <i class="bi bi-search"></i> Tìm kiếm
                    </button>
                </div>
            </form>

            <div class="appointment-list-section animate-fade-in">
                <div class="appointment-list-header">
                    <h5 id="queueHeader">Danh sách hàng đợi hôm nay  ${empty slotDate ? LocalDate.now().toString() : slotDate}  (<span id="totalRecordsDisplay">0</span> kết quả)</h5>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover table-bordered table-appointments">
                        <thead class="table-primary">
                            <tr>
                                <th>STT</th>
                                <th>Mã lịch hẹn</th>
                                <th>Ngày khám</th>
                                <th>Giờ khám</th>
                                <th>Mã bệnh nhân</th>
                                <th>Họ tên</th>
                                <th>Điện thoại</th>
                                <th>Dịch vụ</th>
                                <th>Ưu tiên</th>
                                <th>Giờ check-in</th>
                                <th>Bác sĩ</th>
                                <th>Trạng thái</th>
                                
                            </tr>
                        </thead>
                        <tbody id="queueTableBody">
                            <!-- Dữ liệu sẽ được tải bằng AJAX -->
                        </tbody>
                    </table>
                </div>

                <div class="pagination-container">
                    <div class="pagination-info" id="paginationInfo">
                        <!-- Sẽ được cập nhật bằng AJAX -->
                    </div>
                    <nav aria-label="Page navigation">
                        <ul class="pagination justify-content-end" id="pagination">
                            <!-- Sẽ được cập nhật bằng AJAX -->
                        </ul>
                    </nav>
                </div>
            </div>


            <script>
                <%
                    String scheme = request.getScheme();
                    String serverName = request.getServerName();
                    int serverPort = request.getServerPort();
                    String contextPath = request.getContextPath();
                    String baseURL = scheme + "://" + serverName + ":" + serverPort + contextPath;
                %>

                document.addEventListener("DOMContentLoaded", function () {
                    const BASE_URL = "<%= baseURL %>";
                    console.log("BASE_URL:", BASE_URL);
                    const searchForm = document.getElementById("searchForm");
                    const resetFilterButton = document.getElementById("resetFilterButton");
                    const tableBody = document.getElementById("queueTableBody");
                    const paginationInfo = document.getElementById("paginationInfo");

                    if (!tableBody) {
                        console.error("Không tìm thấy queueTableBody trong DOM!");
                        return;
                    }

                    // Load initial queue
                    loadQueue();

                    // Search form submit with AJAX
                    searchForm.addEventListener("submit", function (e) {
                        e.preventDefault();
                        loadQueue();
                    });

                    // Reset filter with AJAX
                    resetFilterButton.addEventListener("click", function () {
                        const form = document.getElementById("searchForm");
                        form.reset();
                        document.getElementById("doctorId").value = "";
                        document.getElementById("slotDate").value = "";
                        const actionURL = form.getAttribute("action");
                        const formData = new FormData();
                        formData.append("action", "search");

                        fetch(actionURL, {
                            method: "POST",
                            body: formData
                        }).then(() => {
                            loadQueue();
                        }).catch(err => {
                            console.error('Lỗi khi reset filter at ' + new Date().toLocaleString() + ':', err);
                            alert("Lỗi khi đặt lại bộ lọc: " + err.message);
                        });
                    });

                    // Load danh sách hàng đợi
                    function loadQueue() {
                        const doctorIdElement = document.getElementById('doctorId');
                        const slotDateElement = document.getElementById('slotDate');
                        if (!doctorIdElement || !slotDateElement) {
                            console.error("Không tìm thấy phần tử doctorId hoặc slotDate trong DOM!");
                            return;
                        }

                        const doctorId = doctorIdElement.value.trim();
                        const slotDate = slotDateElement.value.trim();
                        console.log("doctorId value:", doctorId);
                        console.log("slotDate value:", slotDate);

                        const query = new URLSearchParams({
                            doctorId: doctorId,
                            slotDate: slotDate
                        }).toString();
                        const url = BASE_URL + "/api/queue?" + query;
                        console.log("Generated URL:", url);

                        tableBody.innerHTML = '<tr><td colspan="12" class="text-center"><div class="spinner">Đang tải...</div></td></tr>';

                        fetch(url, {
                            method: 'GET',
                            headers: {
                                'Content-Type': 'application/json'
                            }
                        })
                                .then(res => {
                                    console.log('Response status at ' + new Date().toLocaleString() + ':', res.status);
                                    if (res.status === 404) {
                                        throw new Error("404 Not Found: Endpoint /api/queue không tồn tại hoặc không được ánh xạ đúng!");
                                    }
                                    if (res.status === 403) {
                                        throw new Error("403 Forbidden: Bạn không có quyền truy cập hoặc session hết hạn!");
                                    }
                                    if (!res.ok) {
                                        throw new Error(`HTTP error! status: ${res.status} - URL: ${url}`);
                                    }
                                    return res.json();
                                })
                                .then(data => {
                                    console.log("Received data:", JSON.stringify(data, null, 2));
                                    const totalRecordsDisplay = document.getElementById('totalRecordsDisplay');
                                    totalRecordsDisplay.textContent = data.totalRecords || 0;
                                    paginationInfo.innerHTML = 'Hiển thị ' + (data.queueList ? data.queueList.length : 0) + ' / ' + (data.totalRecords || 0) + ' kết quả';

                                    tableBody.innerHTML = '';
                                    if (!data.queueList || data.queueList.length === 0) {
                                        console.log("Không có dữ liệu trong queueList hoặc queueList không tồn tại. Data:", JSON.stringify(data));
                                        tableBody.innerHTML = '<tr><td colspan="12" class="text-center text-muted">Không có hàng đợi nào được tìm thấy.</td></tr>';
                                    } else {
                                        console.log("Bắt đầu render queueList với", data.queueList.length, "bản ghi");
                                        let addedSeparator = false;
                                        data.queueList.forEach((q, index) => {
                                            try {
                                                console.log("Rendering item", index + 1, ":", q);
                                                // Thêm viền ngăn cách khi gặp phần tử đầu tiên sau currentTime
                                                if (!addedSeparator && !q.isBeforeCurrentTime) {
                                                    const separatorRow = document.createElement('tr');
                                                    separatorRow.classList.add('separator');
                                                    separatorRow.innerHTML = `<td colspan="12" style="border-bottom: 3px solid #007bff; text-align: center; font-weight: bold;">Thời gian hiện tại: ${data.currentTime}</td>`;
                                                    tableBody.appendChild(separatorRow);
                                                    addedSeparator = true;
                                                }

                                                const row = document.createElement('tr');
                                                if (q.priority === 1) {
                                                    row.classList.add('priority-high'); // Ưu tiên cao gần viền
                                                }
                                                if (q.isBeforeCurrentTime) {
                                                    row.classList.add('before-current');
                                                } else {
                                                    row.classList.add('after-current');
                                                }
                                                row.innerHTML = '<td>' + (index + 1) + '</td>' +
                                                        '<td>' + (q.appointmentCode || '-') + '</td>' +
                                                        '<td>' + (q.slotDate || '-') + '</td>' +
                                                        '<td>' + (q.slotTimeRange || '-') + '</td>' +
                                                        '<td>' + (q.patientCode || '-') + '</td>' +
                                                        '<td>' + (q.patientName || '-') + '</td>' +
                                                        '<td>' + (q.patientPhone || '-') + '</td>' +
                                                        '<td>' + (q.serviceName || '-') + '</td>' +
                                                        '<td>' + (q.priority === 1 ? '<span class="badge bg-danger">Cao</span>' : '<span class="badge bg-secondary">Trung bình</span>') + '</td>' +
                                                        '<td>' + (q.checkinTime || '-') + '</td>' +
                                                        '<td>' + (q.doctorName || '-') + '</td>' +
                                                        '<td>' +
                                                        (q.status === 'waiting' ? '<span class="badge bg-warning text-dark">Đang chờ</span>' :
                                                                q.status === 'in_progress' ? '<span class="badge bg-info text-dark">Đang khám</span>' :
                                                                q.status === 'completed' ? '<span class="badge bg-success">Hoàn thành</span>' :
                                                                q.status === 'skipped' ? '<span class="badge bg-secondary">Bỏ qua</span>' :
                                                                q.status === 'rejected' ? '<span class="badge bg-danger">Từ chối</span>' : '-') +
                                                        '</td>';
                                                tableBody.appendChild(row);
                                            } catch (error) {
                                                console.error('Lỗi khi render hàng ' + (index + 1) + ':', error, q);
                                            }
                                        });
                                        console.log("Hoàn tất render queueList");
                                    }
                                })
                                .catch(err => {
                                    console.error('Lỗi khi tải danh sách hàng đợi at ' + new Date().toLocaleString() + ':', err);
                                    tableBody.innerHTML = '<tr><td colspan="12" class="text-center text-danger">Lỗi: Không thể tải dữ liệu. URL: ' + url + '</td></tr>';
                                })
                                .finally(() => {
                                    if (tableBody.querySelector('.spinner')) {
                                        tableBody.innerHTML = '';
                                    }
                                });
                    }
                    // Các biến từ Controller
                    window.GLOBAL_IS_SEARCH_PERFORMED = ${requestScope.searchPerformed != null ? requestScope.searchPerformed : false};
                    window.GLOBAL_HAS_RESULTS = ${requestScope.hasResults != null ? requestScope.hasResults : false};
                });
            </script>

            <%!
                              // Hàm trợ giúp để tạo lại các tham số lọc cho phân trang
                              // Lấy các giá trị đã được Controller setAttribute (là các param gốc từ người dùng)
                              private String generateFilterParams(HttpServletRequest request) {
                                  StringBuilder params = new StringBuilder();

                                  // Lấy các giá trị đã được Controller setAttribute
                                  String appointmentCode = (String) request.getAttribute("appointmentCode");
                                  if (appointmentCode != null && !appointmentCode.isEmpty()) {
                                      params.append("&appointmentCode=").append(appointmentCode);
                                  }
                                  String patientId = (String) request.getAttribute("patientId"); // Đây là patient ID String
                                  if (patientId != null && !patientId.isEmpty()) {
                                      params.append("&patientId=").append(patientId);
                                  }
                                  String doctorId = (String) request.getAttribute("doctorId"); // Đây là doctor ID String
                                  if (doctorId != null && !doctorId.isEmpty()) {
                                      params.append("&doctorId=").append(doctorId);
                                  }
                                  String status = (String) request.getAttribute("status");
                                  if (status != null && !status.isEmpty()) {
                                      params.append("&status=").append(status);
                                  }
                                  Boolean showDeleted = (Boolean) request.getAttribute("showDeleted");
                                  if (showDeleted != null && showDeleted) { // Chỉ thêm nếu là true
                                      params.append("&showDeleted=").append(showDeleted);
                                  }
                
                                  // recordsPerPage có thể lấy từ request.getAttribute vì Controller cũng set nó
                                  Integer rp = (Integer) request.getAttribute("recordsPerPage");
                                  if (rp != null) {
                                      params.append("&recordsPerPage=").append(rp);
                                  }

                                  // IMPORTANT: Always append submitSearch=true for pagination links
                                  // This ensures the Controller always considers it a "search" request
                                  // when navigating between pages, even if no explicit filter changed.
                                  params.append("&submitSearch=true");

                                  return params.toString();
                              }
            %>

    </body>
</html>
