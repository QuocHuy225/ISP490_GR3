document.addEventListener('DOMContentLoaded', function () {
    // Javascript cho sidebar toggle
    const sidebarToggle = document.getElementById('sidebarCollapse');
    const sidebar = document.getElementById('sidebar');
    const content = document.getElementById('content');
    const topNavbar = document.querySelector('.top-navbar');

    if (sidebarToggle && sidebar && content && topNavbar) {
        sidebarToggle.addEventListener('click', function () {
            sidebar.classList.toggle('collapsed');
            content.classList.toggle('expanded');
            topNavbar.classList.toggle('navbar-collapsed');
        });
    }

    // Responsive sidebar
    const RESPONSIVE_BREAKPOINT = 768; // Định nghĩa hằng số cho breakpoint responsive

    function checkWidth() {
        if (window.innerWidth <= RESPONSIVE_BREAKPOINT) {
            if (sidebar && content && topNavbar) {
                sidebar.classList.add('collapsed');
                content.classList.add('expanded');
            }
        } else {
            if (sidebar && content && topNavbar) {
                sidebar.classList.remove('collapsed');
                content.classList.remove('expanded');
                topNavbar.classList.remove('navbar-collapsed');
            }
        }
    }

    // Kiểm tra ban đầu và lắng nghe sự kiện resize cửa sổ
    if (sidebar && content && topNavbar) {
        checkWidth();
        window.addEventListener('resize', checkWidth);
    }

    // Enhanced dropdown animations (sử dụng sự kiện của Bootstrap cho animation tùy chỉnh)
    const dropdownMenuLink = document.getElementById('userDropdown');
    if (dropdownMenuLink) {
        // Áp dụng animation khi dropdown hiển thị
        dropdownMenuLink.addEventListener('show.bs.dropdown', function () {
            const dropdownMenu = dropdownMenuLink.nextElementSibling;
            if (dropdownMenu) {
                dropdownMenu.style.opacity = '0';
                dropdownMenu.style.transform = 'translateY(-10px)';
                // Một khoảng thời gian nhỏ cho phép trình duyệt render trạng thái ban đầu
                // trước khi áp dụng các thuộc tính transition, làm cho transition hiển thị.
                setTimeout(() => {
                    dropdownMenu.style.transition = 'all 0.3s ease';
                    dropdownMenu.style.opacity = '1';
                    dropdownMenu.style.transform = 'translateY(0)';
                }, 10); // Đảm bảo thời gian này đủ nhỏ để không gây delay thấy rõ
            }
        });

        // Đặt lại thuộc tính transition khi dropdown ẩn để tránh các animation không mong muốn
        dropdownMenuLink.addEventListener('hidden.bs.dropdown', function () {
            const dropdownMenu = dropdownMenuLink.nextElementSibling;
            if (dropdownMenu) {
                dropdownMenu.style.transition = ''; // Xóa thuộc tính transition
                dropdownMenu.style.opacity = '';    // Đặt lại opacity
                dropdownMenu.style.transform = '';  // Đặt lại transform
            }
        });
    }

    // Logic cho nút "Đặt lại bộ lọc"
    const resetFilterButton = document.getElementById('resetFilterButton');
    if (resetFilterButton) {
        resetFilterButton.addEventListener('click', function (event) {
            event.preventDefault(); // Ngăn chặn hành vi mặc định của HTML.
            // Đảm bảo '${pageContext.request.contextPath}' được phân giải ở phía server nếu đây là file JSP
            // Lưu ý: Trong file JS tĩnh, pageContext không khả dụng. Thay thế bằng path cứng hoặc đặt biến global từ JSP.
            window.location.href = './appointments'; // Hoặc lấy từ biến global đã đặt trong JSP
        });
    }

    function showBootstrapToast(message, type = 'info') {
        const toastContainer = document.getElementById('toastContainer');
        if (!toastContainer) {
            console.warn("Không tìm thấy vùng chứa toast với ID 'toastContainer'. Không thể hiển thị toast.");
            return;
        }

        const toastEl = document.createElement('div');
        toastEl.classList.add('toast', `text-bg-${type}`, 'border-0'); // Bootstrap 5
        toastEl.setAttribute('role', 'alert');
        toastEl.setAttribute('aria-live', 'assertive');
        toastEl.setAttribute('aria-atomic', 'true');
        toastEl.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        `;
        toastContainer.appendChild(toastEl);

        const toast = new bootstrap.Toast(toastEl, {
            autohide: true,
            delay: 3000 // Tự động ẩn sau 3 giây
        });

        toast.show();

        // Xóa phần tử toast khỏi DOM sau khi nó ẩn
        toastEl.addEventListener('hidden.bs.toast', function () {
            toastEl.remove();
        });
    }

    // Xử lý checkbox checkAll (chọn/bỏ chọn tất cả)
    const checkAllCheckbox = document.getElementById('checkAll');
    if (checkAllCheckbox) {
        checkAllCheckbox.addEventListener('change', function () {
            const checkboxes = document.querySelectorAll('input[name="selectedAppointments"]');
            checkboxes.forEach(checkbox => {
                checkbox.checked = checkAllCheckbox.checked;
            });
        });
    }

    // Logic cho nút "Tiếp tục" trong modal Thêm lịch hẹn (sử dụng Bootstrap validation và toast)
    const continueAddAppointmentButton = document.getElementById('continueAddAppointment');

    if (continueAddAppointmentButton) {
        continueAddAppointmentButton.addEventListener('click', function () {
            const addAppointmentForm = document.getElementById('addAppointmentForm');

            // Kiểm tra tính hợp lệ của form bằng validation tích hợp của HTML5
            if (addAppointmentForm.checkValidity()) {
                // Ở đây bạn sẽ gửi dữ liệu form qua AJAX nếu không muốn tải lại trang
                // Để đơn giản, hiện tại form sẽ được gửi thông thường (như trong HTML)
                // và Controller sẽ xử lý.

                // Mô phỏng thành công và ẩn modal như code gốc (nếu bạn vẫn dùng AJAX)
                showBootstrapToast('Form hợp lệ! Lịch hẹn sẽ được xử lý.', 'success');

                const modalElement = document.getElementById('addAppointmentModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) {
                    modal.hide(); // Ẩn modal sau khi xử lý
                }

                // Tùy chọn: đặt lại form sau khi gửi thành công
                addAppointmentForm.reset();
                // Xóa các kiểu validation của Bootstrap sau khi reset
                addAppointmentForm.classList.remove('was-validated');
            } else {
                // Nếu form không hợp lệ, Bootstrap sẽ hiển thị phản hồi validation
                addAppointmentForm.classList.add('was-validated'); // Áp dụng các kiểu validation của Bootstrap
                showBootstrapToast('Vui lòng điền đầy đủ các thông tin bắt buộc.', 'warning');
            }
        });
    }

    // Logic xử lý form xóa nhiều bản ghi (sử dụng Bootstrap modal để xác nhận)
    const deleteMultipleForm = document.getElementById('deleteMultipleForm');
    const confirmDeleteModalEl = document.getElementById('confirmDeleteModal'); // Cần một Bootstrap modal với ID này
    const confirmDeleteButton = document.getElementById('confirmDeleteButton'); // Nút xác nhận bên trong modal

    if (deleteMultipleForm && confirmDeleteModalEl && confirmDeleteButton) {
        const confirmDeleteModal = new bootstrap.Modal(confirmDeleteModalEl);
        const deleteCountSpan = document.getElementById('deleteCount'); // Span để hiển thị số lượng mục trong modal

        deleteMultipleForm.addEventListener('submit', function (event) {
            event.preventDefault(); // Ngăn chặn form gửi đi ngay lập tức

            const selectedCheckboxes = document.querySelectorAll('input[name="selectedAppointments"]:checked');
            
            // --- BỔ SUNG: Thu thập các ID DUY NHẤT để đảm bảo không gửi trùng lặp ---
            const uniqueAppointmentIds = new Set();
            selectedCheckboxes.forEach(checkbox => {
                uniqueAppointmentIds.add(checkbox.value);
            });

            if (uniqueAppointmentIds.size === 0) { // Kiểm tra kích thước của Set các ID duy nhất
                showBootstrapToast('Vui lòng chọn ít nhất một lịch hẹn để xóa.', 'info');
                return; // Dừng thực thi hàm
            }

            // Đặt số lượng vào tin nhắn modal xác nhận (dựa trên số lượng ID duy nhất)
            if (deleteCountSpan) {
                deleteCountSpan.textContent = uniqueAppointmentIds.size;
            }

            // Hiển thị modal xác nhận của Bootstrap
            confirmDeleteModal.show();

            // Thiết lập lắng nghe sự kiện một lần cho nút xác nhận của modal
            // Điều này ngăn chặn việc tạo ra nhiều listener chồng chéo
            confirmDeleteButton.onclick = function () {
                // Xóa bất kỳ input hidden nào từ lần gửi trước để tránh trùng lặp
                const existingHiddenInputs = deleteMultipleForm.querySelectorAll('input[name="selectedAppointments"][type="hidden"]');
                existingHiddenInputs.forEach(input => input.remove());

                // Tạo các input hidden mới cho mỗi ID DUY NHẤT
                uniqueAppointmentIds.forEach(id => { // Lặp qua Set các ID duy nhất
                    const hiddenInput = document.createElement('input');
                    hiddenInput.type = 'hidden';
                    hiddenInput.name = 'selectedAppointments'; // Đảm bảo tên này khớp với Controller của bạn
                    hiddenInput.value = id;
                    deleteMultipleForm.appendChild(hiddenInput);
                });

                confirmDeleteModal.hide(); // Ẩn modal
                deleteMultipleForm.submit(); // Gửi form theo chương trình
            };
        });
    }

    // Logic để hiển thị hoặc ẩn bảng dựa trên biến global
    // Biến shouldShowTable được mong đợi là được thiết lập từ server-side (ví dụ: JSP)
    const isSearchPerformed = window.GLOBAL_IS_SEARCH_PERFORMED;
    const hasAppointments = window.GLOBAL_HAS_APPOINTMENTS;

    const appointmentListSection = document.querySelector('.appointment-list-section');
    const noResultsMessageElement = document.getElementById('noResultsMessage');

    if (appointmentListSection && noResultsMessageElement) {
        if (isSearchPerformed) { // Nếu tìm kiếm đã được thực hiện
            if (hasAppointments) { // Và có kết quả
                appointmentListSection.style.display = 'block';
                noResultsMessageElement.style.display = 'none';
            } else { // Tìm kiếm đã thực hiện nhưng không có kết quả
                appointmentListSection.style.display = 'none';
                noResultsMessageElement.style.display = 'block';
                noResultsMessageElement.innerHTML = 'Không tìm thấy lịch hẹn nào phù hợp với tiêu chí tìm kiếm.';
                noResultsMessageElement.dataset.defaultMessage = 'set';
            }
        } else { // Khi trang tải lần đầu (chưa có tìm kiếm nào được thực hiện)
            appointmentListSection.style.display = 'none';
            noResultsMessageElement.style.display = 'block';
            if (noResultsMessageElement.innerHTML.trim() === '' || noResultsMessageElement.dataset.defaultMessage !== 'set') {
                noResultsMessageElement.innerHTML = 'Vui lòng sử dụng chức năng tìm kiếm để hiển thị danh sách lịch hẹn.';
                noResultsMessageElement.dataset.defaultMessage = 'set';
            }
        }
    }
});
