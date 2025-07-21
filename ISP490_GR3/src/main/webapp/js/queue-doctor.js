// web/js/queue-doctor.js

// Khai báo các phần tử DOM cần thiết ở phạm vi toàn cục.
// Chúng sẽ được GÁN GIÁ TRỊ trong khối DOMContentLoaded của JSP.
// Điều này giúp tránh lỗi "redeclaration" và cho phép các hàm truy cập chúng.
let searchForm;
let resetFilterButton;
let tableBody;
let paginationInfo;
let totalRecordsDisplay;
let paginationUl;
let currentDateDisplay;

// NEW: Hàm để hiển thị modal thông báo tùy chỉnh của Bootstrap
function showNotificationModal(message) {
    const notificationModal = document.getElementById('notificationModal');
    const notificationModalBody = document.getElementById('notificationModalBody');

    if (notificationModal && notificationModalBody) {
        notificationModalBody.textContent = message; // Đặt nội dung thông báo
        const bsModal = new bootstrap.Modal(notificationModal); // Tạo instance modal Bootstrap
        bsModal.show(); // Hiển thị modal
    } else {
        console.error("Notification modal elements not found, falling back to alert.");
        alert(message); // Fallback về alert nếu không tìm thấy các phần tử modal
    }
}


// Hàm toàn cục để tải dữ liệu hàng đợi qua AJAX
// Tham số 'baseURL' sẽ được truyền từ JSP
function loadQueue(page = 1, baseURL) {
    // Đảm bảo các phần tử DOM đã được gán giá trị trước khi sử dụng
    // (Chúng được gán trong JSP's DOMContentLoaded block)
    const doctorIdElement = document.getElementById('doctorId');
    const slotDateElement = document.getElementById('slotDate');
    
    const doctorId = doctorIdElement.value.trim();
    const slotDate = slotDateElement.value.trim();
    
    if (slotDate === '') {
         const today = new Date();
         const yyyy = today.getFullYear();
         const mm = String(today.getMonth() + 1).padStart(2, '0');
         const dd = String(today.getDate()).padStart(2, '0');
         currentDateDisplay.textContent = `${yyyy}-${mm}-${dd}`;
    } else {
        currentDateDisplay.textContent = slotDate;
    }

    const queryParams = new URLSearchParams({
        doctorId: doctorId,
        slotDate: slotDate,
        page: page
    }).toString();

    const url = baseURL + "/api/doctor/queue?" + queryParams;
    console.log("Generated AJAX URL:", url);

    tableBody.innerHTML = '<tr><td colspan="13" class="text-center"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Đang tải...</span></div><p>Đang tải...</p></td></tr>';

    fetch(url, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(res => {
        console.log('Response status at ' + new Date().toLocaleString() + ':', res.status);
        if (!res.ok) {
            return res.json().then(err => {
                throw new Error(err.message || `HTTP error! status: ${res.status}`);
            }).catch(() => {
                throw new Error(`HTTP error! status: ${res.status}`);
            });
        }
        return res.json();
    })
    .then(data => {
        console.log("Received data:", JSON.stringify(data, null, 2));
        totalRecordsDisplay.textContent = data.totalRecords || 0;
        
        const isDoctor = data.isDoctor; 

        tableBody.innerHTML = '';
        if (!data.queueList || data.queueList.length === 0) {
            console.log("Không có dữ liệu trong queueList hoặc queueList không tồn tại. Data:", JSON.stringify(data));
            tableBody.innerHTML = '<tr><td colspan="13" class="text-center text-muted">Không có hàng đợi nào được tìm thấy.</td></tr>';
        } else {
            console.log("Bắt đầu render queueList với", data.queueList.length, "bản ghi");
            let addedSeparator = false;
            data.queueList.forEach((q, index) => {
                try {
                    if (!addedSeparator && !q.isBeforeCurrentTime) {
                        const separatorRow = document.createElement('tr');
                        separatorRow.classList.add('separator');
                        separatorRow.innerHTML = `<td colspan="13" style="text-align: center; font-weight: bold; background-color: #e0f2f7;">Thời gian hiện tại: ${data.currentTime}</td>`;
                        tableBody.appendChild(separatorRow);
                        addedSeparator = true;
                    }

                    const row = document.createElement('tr');
                    if (q.priority === 1) {
                        row.classList.add('priority-high');
                    }
                    if (q.isBeforeCurrentTime) {
                        row.classList.add('before-current');
                    } else {
                        row.classList.add('after-current');
                    }

                    let statusDisplayContent;
                    if (q.status === 'waiting') {
                        statusDisplayContent = '<span class="badge bg-warning text-dark">Đang chờ</span>';
                    } else if (q.status === 'in_progress') {
                        statusDisplayContent = '<span class="badge bg-info text-dark">Đang khám</span>';
                    } else if (q.status === 'completed') {
                        statusDisplayContent = '<span class="badge bg-success">Hoàn thành</span>';
                    } else if (q.status === 'skipped') {
                        statusDisplayContent = '<span class="badge bg-secondary">Bỏ qua</span>';
                    } else if (q.status === 'rejected') {
                        statusDisplayContent = '<span class="badge bg-danger">Từ chối</span>';
                    } else {
                        statusDisplayContent = '-';
                    }

                    let actionButtons = `
                        <button class="btn btn-sm btn-outline-info me-1" onclick="viewDetails(${q.queueId}, '${baseURL}')">
                            <i class="bi bi-info-circle"></i> Chi tiết
                        </button>
                    `;
                    
                    if (isDoctor && (q.status === 'waiting' || q.status === 'in_progress')) {
                        actionButtons += `
                            <button class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#statusModal"
                                    data-queue-id="${q.queueId}" data-current-status="${q.status}">
                                <i class="bi bi-pencil-square"></i> Điều chỉnh
                            </button>
                        `;
                    }
                    else if (isDoctor && (q.status === 'completed' || q.status === 'rejected' || q.status === 'skipped')) {
                         actionButtons += `
                            <button class="btn btn-sm btn-outline-secondary" onclick="viewHistory(${q.queueId}, '${baseURL}')">
                                <i class="bi bi-clock-history"></i> Lịch sử
                            </button>
                        `;
                    }


                    row.innerHTML = `
                        <td>${index + 1 + (page - 1) * 10}</td>
                        <td>${q.appointmentCode || '-'}</td>
                        <td>${q.slotDate || '-'}</td>
                        <td>${q.slotTimeRange || '-'}</td>
                        <td>${q.patientCode || '-'}</td>
                        <td>${q.patientName || '-'}</td>
                        <td>${q.patientPhone || '-'}</td>
                        <td>${q.serviceName || '-'}</td>
                        <td>${q.priority === 1 ? '<span class="badge bg-danger">Cao</span>' : '<span class="badge bg-secondary">Trung bình</span>'}</td>
                        <td>${q.checkinTime || '-'}</td>
                        <td>${q.doctorName || '-'}</td>
                        <td>${statusDisplayContent}</td>
                        <td>${actionButtons}</td>
                    `;
                    tableBody.appendChild(row);
                } catch (error) {
                    console.error('Lỗi khi render hàng ' + (index + 1) + ':', error, q);
                }
            });
            console.log("Hoàn tất render queueList");
        }
        updatePagination(data.totalRecords, page, 10, baseURL);
    })
    .catch(err => {
        console.error('Lỗi khi tải danh sách hàng đợi lúc ' + new Date().toLocaleString() + ':', err);
        tableBody.innerHTML = '<tr><td colspan="13" class="text-center text-danger">Lỗi: ' + (err.message || 'Không thể tải dữ liệu.') + '</td></tr>';
        totalRecordsDisplay.textContent = 0;
        paginationInfo.innerHTML = 'Hiển thị 0 / 0 kết quả';
        paginationUl.innerHTML = '';
    })
    .finally(() => {
        // Any final cleanup if needed
    });
}

function updatePagination(totalRecords, currentPage, limit, baseURL) {
    const totalPages = Math.ceil(totalRecords / limit);
    paginationUl.innerHTML = '';

    if (totalPages > 1) {
        if (currentPage > 1) {
            paginationUl.innerHTML += `<li class="page-item"><a class="page-link" href="#" data-page="${currentPage - 1}">Trước</a></li>`;
        }
        for (let i = 1; i <= totalPages; i++) {
            paginationUl.innerHTML += `<li class="page-item ${currentPage === i ? 'active' : ''}"><a class="page-link" href="#" data-page="${i}">${i}</a></li>`;
        }
        if (currentPage < totalPages) {
            paginationUl.innerHTML += `<li class="page-item"><a class="page-link" href="#" data-page="${currentPage + 1}">Sau</a></li>`;
        }
    }

    paginationUl.querySelectorAll('.page-link').forEach(link => {
        link.addEventListener('click', function (e) {
            e.preventDefault();
            const newPage = parseInt(this.dataset.page);
            loadQueue(newPage, baseURL);
        });
    });
}

// Hàm cập nhật trạng thái (SẼ GỌI KHI NHẤN NÚT TRONG MODAL)
window.performStatusUpdate = function(queueId, newStatus, baseURL, modalElement) {
    fetch(baseURL + '/api/doctor/queue/status', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `queueId=${queueId}&newStatus=${newStatus}`
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => {
                throw new Error(err.message || 'Có lỗi xảy ra khi cập nhật trạng thái.');
            }).catch(() => {
                throw new Error(`HTTP error! status: ${response.status}`);
            });
        }
        return response.json();
    })
    .then(data => {
        if (data.status === 'success') {
            showNotificationModal(data.message); // Sử dụng modal thông báo tùy chỉnh
            // Ẩn modal điều chỉnh trạng thái
            if (modalElement) {
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
            }
            loadQueue(1, baseURL); // Tải lại danh sách để cập nhật trạng thái và sắp xếp (nếu cần)
        } else {
            showNotificationModal('Lỗi: ' + data.message); // Sử dụng modal thông báo cho lỗi
        }
    })
    .catch(error => {
        console.error('Lỗi khi cập nhật trạng thái hàng đợi:', error);
        showNotificationModal('Đã xảy ra lỗi trong quá trình gửi yêu cầu: ' + error.message); // Sử dụng modal thông báo cho lỗi mạng
    });
};

window.viewDetails = function(queueId, baseURL) {
    showNotificationModal('Xem chi tiết hàng đợi ID: ' + queueId); // Sử dụng modal thông báo
};

window.viewHistory = function(queueId, baseURL) {
    showNotificationModal('Xem lịch sử cho hàng đợi ID: ' + queueId); // Sử dụng modal thông báo
    // Triển khai chức năng xem lịch sử thực tế
}