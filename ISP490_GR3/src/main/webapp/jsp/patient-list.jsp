<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Danh sách bệnh nhân</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 20px;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
            }
            th, td {
                padding: 10px;
                text-align: left;
                border: 1px solid #ddd;
            }
            th {
                background-color: #f2f2f2;
            }
            input, select, button {
                margin: 5px;
                padding: 8px;
            }
            button {
                background-color: #4CAF50;
                color: white;
                border: none;
                cursor: pointer;
            }
            button[type="reset"] {
                background-color: #f44336;
            }
            .add-button {
                background-color: #2196F3;
            }
            .error {
                color: red;
            }
        </style>
    </head>
    <body>
        <h2>Danh sách bệnh nhân</h2>

        <div style="display: flex; justify-content: space-between; align-items: center;">
            <form action="${pageContext.request.contextPath}/patients" method="post">
                Mã BN: <input type="text" name="code" value="${searchCode}" placeholder="Nhập mã bệnh nhân" />
                Họ và tên: <input type="text" name="name" value="${searchName}" placeholder="Nhập họ và tên" />
                SĐT: <input type="text" name="phone" value="${searchPhone}" placeholder="Nhập số điện thoại" />
                <button type="submit">Tìm kiếm</button>
                <button type="reset">Xóa</button>
            </form>
            <button class="add-button" onclick="openModal()">Thêm bệnh nhân mới</button>
        </div>

        <!-- Modal Thêm bệnh nhân -->
        <div class="modal fade" id="addPatientModal" tabindex="-1" aria-labelledby="addPatientModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form id="addPatientForm" action="<c:url value='/patients/add' />" method="post">
                        <div class="modal-header">
                            <h5 class="modal-title" id="addPatientModalLabel">Thêm bệnh nhân</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <div class="mb-3">
                                <label for="fullName" class="form-label">Họ và tên</label>
                                <input type="text" class="form-control" id="fullName" name="fullName" required>
                            </div>
                            <div class="mb-3">
                                <label for="gender" class="form-label">Giới tính</label>
                                <select class="form-select" id="gender" name="gender" required>
                                    <option value="" disabled selected>Chọn giới tính</option>
                                    <option value="0">Nam</option>
                                    <option value="1">Nữ</option>
                                    <option value="2">Khác</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label for="dob" class="form-label">Ngày sinh</label>
                                <input type="date" class="form-control" id="dob" name="dob" required>
                            </div>
                            <div class="mb-3">
                                <label for="phone" class="form-label">Số điện thoại</label>
                                <input type="text" class="form-control" id="phone" name="phone" required pattern="[0-9]{10,11}" title="Số điện thoại phải là 10-11 chữ số">
                            </div>
                            <div class="mb-3">
                                <label for="cccd" class="form-label">CCCD</label>
                                <input type="text" class="form-control" id="cccd" name="cccd" required pattern="[0-9]{12}" title="CCCD phải là 12 chữ số">
                            </div>
                            <div class="mb-3">
                                <label for="address" class="form-label">Địa chỉ</label>
                                <textarea class="form-control" id="address" name="address" required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                            <button type="submit" class="btn btn-primary">Lưu</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <c:if test="${not empty sessionScope.success}">
            <p style="color: green;">${sessionScope.success}</p>
            <c:remove var="success" scope="session"/>
        </c:if>
        <c:if test="${not empty error}">
            <p style="color: red;">${error}</p>
        </c:if>
        <c:if test="${empty patients}">
            <p style="color: red;">Không tìm thấy bệnh nhân với: Mã=[${searchCode}], Tên=[${searchName}], SĐT=[${searchPhone}]</p>
        </c:if>
        <c:if test="${not empty patients}">
            <p>Tìm thấy ${patients.size()} bệnh nhân</p>
            <table>
                <tr>
                    <th>Mã BN</th>
                    <th>Họ và tên</th>
                    <th>Ngày sinh</th>
                    <th>Điện thoại</th>
                </tr>
                <c:forEach var="p" items="${patients}">
                    <tr>
                        <td>${p.patientCode}</td>
                        <td>${p.fullName}</td>
                        <td>${p.dob}</td>
                        <td>${p.phone}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            function openModal() {
                var modal = new bootstrap.Modal(document.getElementById('addPatientModal'));
                modal.show();
            }

            function closeModal() {
                var modal = bootstrap.Modal.getInstance(document.getElementById('addPatientModal'));
                modal.hide();
            }

            // Validate form
            document.getElementById('addPatientForm').addEventListener('submit', function(e) {
                const dob = document.getElementById('dob').value;
                const phone = document.getElementById('phone').value;
                const cccd = document.getElementById('cccd').value;

                if (new Date(dob) > new Date()) {
                    e.preventDefault();
                    alert('Ngày sinh không được là tương lai.');
                }
                if (!/^\d{10,11}$/.test(phone)) {
                    e.preventDefault();
                    alert('Số điện thoại phải là 10-11 chữ số.');
                }
                if (!/^\d{12}$/.test(cccd)) {
                    e.preventDefault();
                    alert('CCCD phải là 12 chữ số.');
                }
            });
        </script>
    </body>
</html>

