document.addEventListener('DOMContentLoaded', function () {
    // Javascript cho sidebar toggle
    const sidebarToggle = document.getElementById('sidebarCollapse');
    const sidebar = document.getElementById('sidebar');
    // THAY ĐỔI TỪ 'content' SANG 'main-wrapper'
    const mainWrapper = document.getElementById('main-wrapper');
    const topNavbar = document.querySelector('.top-navbar');

    // Đảm bảo rằng tất cả các phần tử cần thiết đều tồn tại
    if (sidebarToggle && sidebar && mainWrapper && topNavbar) {
        sidebarToggle.addEventListener('click', function () {
            console.log('Sidebar toggle button clicked!'); // Log để kiểm tra
            sidebar.classList.toggle('collapsed');
            mainWrapper.classList.toggle('expanded'); // THAY ĐỔI Ở ĐÂY
            // topNavbar.classList.toggle('navbar-collapsed'); // TopNavbar đã nằm trong mainWrapper, có thể không cần toggle riêng nữa
                                                             // nếu bạn muốn nó chỉ co giãn theo mainWrapper
        });
    }

    // Responsive sidebar
    const RESPONSIVE_BREAKPOINT = 768; // Định nghĩa hằng số cho breakpoint responsive

    function checkWidth() {
        if (window.innerWidth <= RESPONSIVE_BREAKPOINT) {
            // THAY ĐỔI TỪ 'content' SANG 'mainWrapper'
            if (sidebar && mainWrapper && topNavbar) {
                sidebar.classList.add('collapsed');
                mainWrapper.classList.add('expanded'); // THAY ĐỔI Ở ĐÂY
            }
        } else {
            // THAY ĐỔI TỪ 'content' SANG 'mainWrapper'
            if (sidebar && mainWrapper && topNavbar) {
                sidebar.classList.remove('collapsed');
                mainWrapper.classList.remove('expanded'); // THAY ĐỔI Ở ĐÂY
                // topNavbar.classList.remove('navbar-collapsed'); // Tương tự như trên
            }
        }
    }

    // Kiểm tra ban đầu và lắng nghe sự kiện resize cửa sổ
    // THAY ĐỔI TỪ 'content' SANG 'mainWrapper'
    if (sidebar && mainWrapper && topNavbar) {
        checkWidth();
        window.addEventListener('resize', checkWidth);
    }

    // --- Giữ nguyên các phần còn lại của JS (dropdown, toast, delete, update) ---

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
            // Điều hướng đến URL cơ sở để reset tất cả các tham số
            window.location.href = './appointments';
        });
    }

    /**
     * Hàm hiển thị một thông báo Bootstrap Toast.
     * Cần có một phần tử `<div id="toastContainer" class="toast-container"></div>`
     * trong HTML của bạn để chứa các toast.
     * @param {string} message Nội dung thông báo.
     * @param {string} type Loại toast (e.g., 'success', 'danger', 'info', 'warning').
     */
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

            // --- Thu thập các ID DUY NHẤT để đảm bảo không gửi trùng lặp ---
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

    // --- NEW LOGIC FOR SINGLE APPOINTMENT ACTIONS ---

    // Handle "Update" button click to populate the modal
    const updateAppointmentModalEl = document.getElementById('updateAppointmentModal');
    if (updateAppointmentModalEl) {
        updateAppointmentModalEl.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget; // Button that triggered the modal

            // Extract info from data-bs-* attributes
            const id = button.getAttribute('data-id');
            const code = button.getAttribute('data-code');
            const patientId = button.getAttribute('data-patient-id');
            const doctorId = button.getAttribute('data-doctor-id');
            const slotId = button.getAttribute('data-slot-id');
            const status = button.getAttribute('data-status');

            // Get modal elements
            const modalTitle = updateAppointmentModalEl.querySelector('.modal-title');
            const updateAppointmentIdInput = updateAppointmentModalEl.querySelector('#updateAppointmentId');
            const updateAppointmentCodeInput = updateAppointmentModalEl.querySelector('#updateAppointmentCode');
            const updatePatientIdInput = updateAppointmentModalEl.querySelector('#updatePatientId');
            const updateDoctorIdInput = updateAppointmentModalEl.querySelector('#updateDoctorId');
            const updateSlotIdInput = updateAppointmentModalEl.querySelector('#updateSlotId');
            const updateStatusSelect = updateAppointmentModalEl.querySelector('#updateStatus');

            // Populate the modal fields
            if (modalTitle) modalTitle.textContent = `Cập nhật lịch hẹn ID: ${id}`;
            if (updateAppointmentIdInput) updateAppointmentIdInput.value = id;
            if (updateAppointmentCodeInput) updateAppointmentCodeInput.value = code;
            if (updatePatientIdInput) updatePatientIdInput.value = patientId;
            if (updateDoctorIdInput) updateDoctorIdInput.value = doctorId;
            if (updateSlotIdInput) updateSlotIdInput.value = slotId;
            if (updateStatusSelect) updateStatusSelect.value = status;
        });

        // Handle Update Form submission
        const updateAppointmentForm = document.getElementById('updateAppointmentForm');
        if (updateAppointmentForm) {
            updateAppointmentForm.addEventListener('submit', function(event) {
                if (!updateAppointmentForm.checkValidity()) {
                    event.preventDefault(); // Prevent form submission if invalid
                    event.stopPropagation(); // Stop propagation to prevent default behavior
                    updateAppointmentForm.classList.add('was-validated');
                    showBootstrapToast('Vui lòng điền đầy đủ các thông tin bắt buộc để cập nhật.', 'warning');
                } else {
                    // Form is valid, allow submission. No toast here as Controller will redirect with message.
                    // This toast would be hidden quickly by the redirect anyway.
                    // showBootstrapToast('Đang gửi yêu cầu cập nhật...', 'info');
                }
            });
        }
    }


    // Handle "Delete Single" button click to populate the modal
    const confirmSingleDeleteModalEl = document.getElementById('confirmSingleDeleteModal');
    if (confirmSingleDeleteModalEl) {
        confirmSingleDeleteModalEl.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget; // Button that triggered the modal
            const id = button.getAttribute('data-id'); // Extract ID from data-id attribute

            const singleDeleteAppointmentIdSpan = confirmSingleDeleteModalEl.querySelector('#singleDeleteAppointmentIdSpan');
            if (singleDeleteAppointmentIdSpan) {
                singleDeleteAppointmentIdSpan.textContent = id;
            }

            // Set up the click handler for the confirmation button inside the modal
            const confirmSingleDeleteButton = document.getElementById('confirmSingleDeleteButton');
            if (confirmSingleDeleteButton) {
                confirmSingleDeleteButton.onclick = function () {
                    // Create a hidden form to submit the single delete request
                    const singleDeleteForm = document.createElement('form');
                    singleDeleteForm.setAttribute('method', 'post');
                    singleDeleteForm.setAttribute('action', `${window.location.origin}${window.location.pathname}`); // Submits to current servlet

                    const actionInput = document.createElement('input');
                    actionInput.setAttribute('type', 'hidden');
                    actionInput.setAttribute('name', 'action');
                    actionInput.setAttribute('value', 'deleteSingle'); // NEW action for single delete
                    singleDeleteForm.appendChild(actionInput);

                    const idInput = document.createElement('input');
                    idInput.setAttribute('type', 'hidden');
                    idInput.setAttribute('name', 'appointmentId'); // Name must match Controller parameter
                    idInput.setAttribute('value', id);
                    singleDeleteForm.appendChild(idInput);

                    document.body.appendChild(singleDeleteForm); // Append to body to submit
                    confirmSingleDeleteModalEl.modalInstance.hide(); // Hide the Bootstrap modal (if using modal.hide() on the instance)
                    // If you don't have modalInstance, you can manually hide it with jQuery or specific Bootstrap methods:
                    // const modal = bootstrap.Modal.getInstance(confirmSingleDeleteModalEl);
                    // if (modal) modal.hide();

                    singleDeleteForm.submit(); // Submit the form
                };
            }
        });
        // Store the modal instance on the element for easier access if needed
        confirmSingleDeleteModalEl.modalInstance = new bootstrap.Modal(confirmSingleDeleteModalEl);
    }


    // Logic để hiển thị hoặc ẩn bảng dựa trên biến global
    // Biến searchPerformed và hasResults được mong đợi là được thiết lập từ server-side (ví dụ: JSP)
    const isSearchPerformed = window.GLOBAL_IS_SEARCH_PERFORMED;
    const hasResults = window.GLOBAL_HAS_RESULTS;

    const appointmentListSection = document.querySelector('.appointment-list-section');
    const noResultsMessageElement = document.getElementById('noResultsMessage');
    const tableBody = appointmentListSection ? appointmentListSection.querySelector('tbody') : null;

    if (appointmentListSection && noResultsMessageElement && tableBody) {
        // Luôn hiển thị phần bảng (appointmentListSection)
        appointmentListSection.style.display = 'block';

        if (isSearchPerformed) { // Nếu tìm kiếm đã được thực hiện
            if (hasResults) { // Và có kết quả
                noResultsMessageElement.style.display = 'none'; // Ẩn thông báo "Không có kết quả"
            } else { // Tìm kiếm đã thực hiện nhưng không có kết quả
                noResultsMessageElement.style.display = 'block'; // Hiện thông báo "Không có kết quả"
                noResultsMessageElement.innerHTML = 'Không tìm thấy lịch hẹn nào phù hợp với tiêu chí tìm kiếm.';
            }
        } else { // Khi trang tải lần đầu (chưa có tìm kiếm nào được thực hiện)
            noResultsMessageElement.style.display = 'block'; // Hiện thông báo "Vui lòng sử dụng tìm kiếm"
            noResultsMessageElement.innerHTML = 'Vui lòng sử dụng chức năng tìm kiếm để hiển thị danh sách lịch hẹn.';
        }
    }

    // Logic for doctor list scrolling (giữ nguyên hoặc cập nhật nếu có thay đổi trong HTML)
    const doctorListWrapper = document.querySelector('.doctor-list-wrapper');
    const doctorScrollRightBtn = document.querySelector('.doctor-scroll-right');

    if (doctorListWrapper && doctorScrollRightBtn) {
        doctorScrollRightBtn.addEventListener('click', () => {
            doctorListWrapper.scrollBy({
                left: 300,
                behavior: 'smooth'
            });
        });
        // Bạn có thể thêm nút cuộn trái nếu muốn
    }
});