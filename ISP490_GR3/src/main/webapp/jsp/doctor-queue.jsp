<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="com.mycompany.isp490_gr3.model.Doctor" %>
<%@ page import="java.time.LocalDate" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Hàng đợi (Bác sĩ)</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        
        <script src="${pageContext.request.contextPath}/js/queue-doctor.js"></script>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/queue.css">
        <style>
            /* Các kiểu CSS tùy chọn cho giao diện bác sĩ */
            .priority-high {
                background-color: #fff3cd; /* Màu cam nhạt cho ưu tiên cao */
            }
            .before-current {
                background-color: #f8f9fa; /* Nền nhạt hơn cho các lịch đã qua */
                color: #6c757d; /* Chữ xám cho các lịch đã qua */
            }
            .after-current {
                /* Không có nền cụ thể, giữ màu mặc định của hàng bảng */
            }
            .separator {
             
                background-color: #e9ecef; /* Màu xám nhạt cho đường phân cách */
                border-top: 3px solid #007bff !important;
                border-bottom: 3px solid #007bff !important;
            }
            /* Thêm kiểu cho spinner nếu cần */
            .spinner-border {
                display: inline-block;
                width: 2rem;
                height: 2rem;
                vertical-align: -0.125em;
                border: 0.25em solid currentColor;
                border-right-color: transparent;
                border-radius: 50%;
                -webkit-animation: .75s linear infinite spinner-border;
                animation: .75s linear infinite spinner-border;
            }
            @keyframes spinner-border {
                to { -webkit-transform: rotate(360deg);
                transform: rotate(360deg); }
            }
        </style>
    </head>
    <body>
        <nav id="sidebar">
            <div class="sidebar-header">
                <h3>MENU</h3>
            </div>
            <ul class="list-unstyled components">      
                <li><a href="${pageContext.request.contextPath}/homepage"><i class="bi bi-speedometer2"></i> Trang chủ</a></li>
                <li class="active"><a href="${pageContext.request.contextPath}/doctor/queue"><i class="bi bi-calendar-check"></i> Lịch khám bệnh</a></li>
                <li><a href="${pageContext.request.contextPath}/doctor/patients"><i class="bi bi-people"></i> Hồ sơ bệnh nhân</a></li>
                <li><a href="${pageContext.request.contextPath}/doctor/report"><i class="bi bi-bar-chart-fill"></i> Báo cáo thống kê</a></li>
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



                    <div class="dropdown user-dropdown">
                        <button class="btn dropdown-toggle" type="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                            <div class="user-profile-icon">
                                <i class="bi bi-person-fill" style="font-size: 1.5rem;"></i>
                            </div>
                            <div class="user-info d-none d-md-block">
                                <%
                                    Object userObj = session.getAttribute("user");
                                    String userName = "User";
                                    String userRoleDisplay = "Bác sĩ"; 
                                    if (userObj instanceof User) {
                                        User user = (User) userObj;
                                        userName = user.getFullName() != null ? user.getFullName() : user.getEmail();
                                    }
                                %>
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

            <form id="searchForm" action="${pageContext.request.contextPath}/doctor/queue" method="get" class="mb-4 mt-3 p-3 border rounded shadow-sm bg-light">
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label for="doctorId" class="form-label">Bác sĩ</label>
                        <select class="form-select" id="doctorId" name="doctorId">
                            <option value="">--Chọn--</option>
                            <c:forEach var="doctor" items="${doctorList}">
                                <option value="${doctor.id}" <c:if test="${requestScope.doctorId eq doctor.id}">selected</c:if>>${doctor.fullName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label for="slotDate" class="form-label">Ngày hẹn</label>
                        <input type="date" class="form-control" id="slotDate" name="slotDate"
                               value="${empty slotDate ?
'' : slotDate}">
                    </div>
                </div>
                <div class="d-flex justify-content-end gap-2">
                    <button type="button" class="btn btn-light border" id="resetFilterButton">
                        <i class="bi bi-arrow-clockwise"></i> Đặt lại bộ lọc
                    </button>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-search"></i> Tìm kiếm
                    </button>
                </div>
            </form>

            <div class="appointment-list-section animate-fade-in">
                <div class="appointment-list-header">
                    <h5 id="queueHeader">Danh sách hàng đợi hôm nay <span id="currentDateDisplay"></span> (<span id="totalRecordsDisplay">0</span> kết quả)</h5>
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
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody id="queueTableBody">
                            </tbody>
                    </table>
                </div>

                <div class="pagination-container">
                    <div class="pagination-info" id="paginationInfo">
                        </div>
                    <nav aria-label="Page navigation">
                        <ul class="pagination justify-content-end" id="pagination">
                        </ul>
                    </nav>
                </div>
            </div>

            <div class="modal fade" id="statusModal" tabindex="-1" aria-labelledby="statusModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="statusModalLabel">Điều chỉnh trạng thái hàng đợi</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" id="modalQueueId">
                            <div class="mb-3">
                                <label for="newStatusSelect" class="form-label">Chọn trạng thái mới:</label>
                                <select class="form-select" id="newStatusSelect">
                                    <option value="waiting">Đang chờ</option>
                                    <option value="in_progress">Đang khám</option>
                                    <option value="completed">Hoàn thành</option>
                                    <option value="skipped">Bỏ qua</option>
                                    <option value="rejected">Từ chối</option>
                                </select>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="button" class="btn btn-primary" id="saveStatusButton">Lưu thay đổi</button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="modal fade" id="notificationModal" tabindex="-1" aria-labelledby="notificationModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="notificationModalLabel">Thông báo</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body" id="notificationModalBody">
                            </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary" data-bs-dismiss="modal">Đóng</button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="modal fade" id="detailsModal" tabindex="-1" aria-labelledby="detailsModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="detailsModalLabel">Chi tiết Hàng đợi <span id="detailsQueueIdDisplay"></span></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <div id="detailsContent">
                                <p class="text-center text-muted">Đang tải chi tiết...</p>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        </div>
                    </div>
                </div>
            </div>

            <script>
                // Đây là đoạn mã Java (JSP Scriptlet) để lấy BASE_URL và initialPage từ phía server
                // và truyền chúng vào biến JavaScript.
                <%
                    String scheme = request.getScheme();
                    String serverName = request.getServerName();
                    int serverPort = request.getServerPort();
                    String contextPath = request.getContextPath();
                    String baseURL = scheme + "://" + serverName + ":" + serverPort + contextPath;

                    Object currentPageObj = request.getAttribute("currentPage");
                    int initialPageValue = 1;
                    if (currentPageObj instanceof Integer) {
                        initialPageValue = (Integer) currentPageObj;
                    } else if (currentPageObj instanceof String) {
                        try {
                            initialPageValue = Integer.parseInt((String) currentPageObj);
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing currentPage to int: " + currentPageObj + " - " + e.getMessage());
                        }
                    }
                %>

                // Bây giờ, các biến JavaScript này sẽ được khởi tạo với giá trị từ server
                const BASE_URL_FROM_JSP = "<%= baseURL %>";
                const initialPage = <%= initialPageValue %>;

                document.addEventListener("DOMContentLoaded", function () {
                    // Gán các phần tử DOM khi DOM đã tải xong
                    // CHÚ Ý: Các biến này đã được KHAI BÁO bằng 'let' ở đầu file queue-doctor.js
                    // Ở đây chỉ cần GÁN GIÁ TRỊ cho chúng.
                    searchForm = document.getElementById("searchForm");
                    resetFilterButton = document.getElementById("resetFilterButton");
                    tableBody = document.getElementById("queueTableBody");
                    paginationInfo = document.getElementById("paginationInfo");
                    totalRecordsDisplay = document.getElementById('totalRecordsDisplay');
                    paginationUl = document.getElementById('pagination');
                    currentDateDisplay = document.getElementById('currentDateDisplay');

                    if (!tableBody) {
                        console.error("Không tìm thấy queueTableBody trong DOM!");
                        return;
                    }

                    // Tải hàng đợi ban đầu khi trang tải xong
                    loadQueue(initialPage, BASE_URL_FROM_JSP);

                    // Gắn sự kiện cho form tìm kiếm
                    searchForm.addEventListener("submit", function (e) {
                        e.preventDefault(); 
                        loadQueue(1, BASE_URL_FROM_JSP);
                    });

                    // Gắn sự kiện cho nút reset filter
                    resetFilterButton.addEventListener("click", function () {
                        document.getElementById("doctorId").value = "";
                        document.getElementById("slotDate").value = "";
                        loadQueue(1, BASE_URL_FROM_JSP);
                    });
                    // Chuyển đổi sidebar 
                    document.getElementById('sidebarCollapse').addEventListener('click', function () {
                        document.getElementById('sidebar').classList.toggle('active');
                        document.getElementById('content').classList.toggle('active');
                    });
                    // Xử lý Modal Điều chỉnh trạng thái
                    const statusModal = document.getElementById('statusModal');
                    if (statusModal) {
                        statusModal.addEventListener('show.bs.modal', function (event) {
                            const button = event.relatedTarget; 
                            const queueId = button.dataset.queueId;
                            const currentStatus = button.dataset.currentStatus;

                            const modalQueueIdInput = statusModal.querySelector('#modalQueueId');
                            const newStatusSelect = statusModal.querySelector('#newStatusSelect');

                            modalQueueIdInput.value = queueId;
                            newStatusSelect.value = currentStatus;
                        });
                        const saveStatusButton = document.getElementById('saveStatusButton');
                        if (saveStatusButton) {
                            saveStatusButton.addEventListener('click', function() {
                                // Thêm bước xác nhận
                                if (confirm("Bạn có chắc chắn muốn cập nhật trạng thái này không?")) {
                                    const queueId = document.getElementById('modalQueueId').value;
                                    const newStatus = document.getElementById('newStatusSelect').value;
                                    
                                    // Gọi hàm cập nhật trạng thái đã định nghĩa trong queue-doctor.js
                                    window.performStatusUpdate(queueId, newStatus, BASE_URL_FROM_JSP, statusModal);
                                }
                            });
                        }
                    }
                });
            </script>
        </div>
    </body>
</html>