// patient-appointment-schedule.js

document.addEventListener('DOMContentLoaded', function () {

    // Cache DOM elements
    const newAppointmentModal = document.getElementById('newAppointmentModal');
    const cancelConfirmModal = document.getElementById('cancelConfirmModal');
    const successMessageModal = document.getElementById('successMessageModal');
    const appointmentDetailModal = document.getElementById('appointmentDetailModal');

    // Nút đóng/mở
    const closeNewAppointmentModalBtn = document.getElementById('closeNewAppointmentModalBtn');
    const closeConfirmModalBtn = document.getElementById('closeConfirmModalBtn');
    const closeSuccessModalBtn = document.getElementById('closeSuccessModalBtn');
    const okSuccessModalBtn = document.getElementById('okSuccessModalBtn');
    const closeDetailModalBtn = appointmentDetailModal ? appointmentDetailModal.querySelector('.close-button') : null;
    const btnCloseDetail = document.getElementById('btnCloseDetail');

    // Form
    const appointmentForm = document.getElementById('newAppointmentForm');
    const doctorSelect = document.getElementById('newDoctorName');
    const serviceSelect = document.getElementById('newService');
    const appointmentDateInput = document.getElementById('newAppointmentDate');
    const appointmentTimeSelect = document.getElementById('newAppointmentTime');
    const notesInput = document.getElementById('newNotes');
    const timeSlotMessage = document.getElementById('timeSlotMessage');

    const confirmCancelBtn = document.getElementById('confirmCancelBtn');
    const confirmAppointmentIdDisplay = document.getElementById('confirmAppointmentIdDisplay');
    const cancelConfirmBtnElement = document.getElementById('cancelConfirmBtn');

    const btnCancelAppointment = document.getElementById('btnCancelAppointment');
    const detailContent = document.getElementById('detailContent');

    const upcomingAppointmentsList = document.getElementById('upcomingAppointmentsList');
    const appointmentHistoryList = document.getElementById('appointmentHistoryList');
    const noUpcomingAppointmentsParagraph = document.getElementById('noUpcomingAppointments');
    const noHistoryAppointmentsParagraph = document.getElementById('noHistoryAppointments');

    const newAppointmentBtn = document.getElementById('btnNewAppointment');

    const API_BASE_URL = typeof API_BASE_URL_FROM_JSP !== 'undefined' ? API_BASE_URL_FROM_JSP : '/api/patient';
    const USER_ACCOUNT_ID_FROM_JSP = typeof USER_ACCOUNT_ID_FROM_JSP_VAR !== 'undefined' ? USER_ACCOUNT_ID_FROM_JSP_VAR : null;

    let allAppointmentsData = [];
    let doctorsData = [];
    let servicesData = [];
    let currentAppointmentData = null;
    let currentAppointmentIdToCancel = null;

    // --- Biến phân trang ---
    const ITEMS_PER_PAGE = 5;
    let currentPageUpcoming = 1;
    let currentPageHistory = 1;
    let totalPagesUpcoming = 1;
    let totalPagesHistory = 1;

    // --- Modal Functions ---
    function showCustomModal(modalElement) {
        if (modalElement) {
            modalElement.style.display = 'flex';
            requestAnimationFrame(() => modalElement.classList.add('show-modal'));
        }
    }
    function hideCustomModal(modalElement) {
        if (modalElement) {
            modalElement.classList.remove('show-modal');
            setTimeout(() => {
                if (!modalElement.classList.contains('show-modal'))
                    modalElement.style.display = 'none';
            }, 300);
        }
    }
    function showCustomAlert(message, isSuccess = true) {
        if (!successMessageModal)
            return;
        hideCustomModal(newAppointmentModal);
        hideCustomModal(appointmentDetailModal);
        hideCustomModal(cancelConfirmModal);
        const successMessageText = document.getElementById('successMessageText');
        const iconElement = successMessageModal.querySelector('h3 i');
        const titleElement = successMessageModal.querySelector('#successMessageTitle span');
        if (iconElement) {
            iconElement.className = `fas ${isSuccess ? 'fa-check-circle text-success' : 'fa-exclamation-circle text-danger'}`;
        }
        if (titleElement) {
            titleElement.textContent = isSuccess ? 'Thành công!' : 'Thất bại!';
        }
        if (successMessageText) {
            successMessageText.textContent = message;
        }
        showCustomModal(successMessageModal);
    }
    function hideAllModalsAfterSuccess() {
        hideCustomModal(successMessageModal);
        hideCustomModal(newAppointmentModal);
        hideCustomModal(appointmentDetailModal);
        hideCustomModal(cancelConfirmModal);
    }
    if (closeSuccessModalBtn)
        closeSuccessModalBtn.addEventListener('click', hideAllModalsAfterSuccess);
    if (okSuccessModalBtn)
        okSuccessModalBtn.addEventListener('click', hideAllModalsAfterSuccess);

    // --- Fetch Data Functions ---
    async function fetchData(url, errorMessage) {
        try {
            const response = await fetch(url);
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({message: `Phản hồi không phải JSON hoặc server không trả về JSON khi lỗi.`}));
                throw new Error(errorData.message || errorMessage);
            }
            return await response.json();
        } catch (error) {
            console.error('Lỗi fetch data từ ' + url + ':', error);
            throw error; // Ném lại lỗi để các caller có thể bắt và xử lý
        }
    }
    async function fetchAppointments() {
        if (!USER_ACCOUNT_ID_FROM_JSP) {
            console.error("USER_ACCOUNT_ID không có. Không thể tải lịch hẹn.");
            return [];
        }
        return fetchData(`${API_BASE_URL}/appointments/patient/${USER_ACCOUNT_ID_FROM_JSP}`, 'Lỗi tải lịch hẹn.');
    }
    async function fetchDoctors() {
        return fetchData(`${API_BASE_URL}/doctors`, 'Lỗi tải danh sách bác sĩ.');
    }
    async function fetchServices() {
        return fetchData(`${API_BASE_URL}/services`, 'Lỗi tải danh sách dịch vụ.');
    }
    async function fetchAvailableSlots(doctorId, date) {
        if (!doctorId || !date)
            return [];
        return fetchData(`${API_BASE_URL}/slots/available?doctorId=${doctorId}&date=${date}`, 'Lỗi tải khung giờ.');
    }
    async function fetchWorkingDaysForDoctor(doctorId) {
        if (!doctorId)
            return [];
        const url = `${API_BASE_URL}/doctors/${doctorId}/available-dates`; 
        const dates = await fetchData(url, `Lỗi tải ngày làm việc cho bác sĩ.`);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        return dates.filter(dateString => new Date(dateString) >= today).sort((a, b) => new Date(a) - new Date(b));
    }

    // --- Render Functions ---
    function renderAppointments(appointments, container, isUpcoming = true, currentPage, totalPages, paginationContainerId) {
        if (!container)
            return;
        container.innerHTML = '';
        const noAppointmentsParagraph = document.getElementById(isUpcoming ? 'noUpcomingAppointments' : 'noHistoryAppointments');
        if (appointments.length === 0) {
            if (noAppointmentsParagraph)
                noAppointmentsParagraph.style.display = 'block';
        } else {
            if (noAppointmentsParagraph)
                noAppointmentsParagraph.style.display = 'none';
        }

        const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        const endIndex = startIndex + ITEMS_PER_PAGE;
        const appointmentsToRender = appointments.slice(startIndex, endIndex);

        appointmentsToRender.forEach(appt => {
            const appointmentElement = document.createElement('div');
            appointmentElement.className = `card mb-3 appointment-item ${appt.status === 'CANCELLED' ? 'border-danger' : ''}`;
            appointmentElement.dataset.appointmentId = appt.id;

            appointmentElement.innerHTML = `
                <div class="card-body">
                    <h5 class="card-title">${appt.serviceName || 'Dịch vụ không xác định'}</h5>
                    <p class="card-text">
                        <strong>Bác sĩ:</strong> ${appt.doctorFullName}<br>
                        <strong>Thời gian:</strong> ${formatDateForDisplay(appt.appointmentDate)} lúc ${appt.appointmentTime}<br>
                        <strong>Trạng thái:</strong> <span class="badge ${getStatusBadgeClass(appt.status)}">${mapStatusToVietnamese(appt.status)}</span><br>
                        <strong>Mã lịch hẹn:</strong> ${appt.appointmentCode || 'N/A'}<br>
                        <strong>Trạng thái thanh toán:</strong> <span class="badge ${getPaymentStatusBadgeClass(appt.paymentStatus)}">${mapPaymentStatusToVietnamese(appt.paymentStatus)}</span>
                    </p>
                    ${isUpcoming && (appt.status === 'PENDING' || appt.status === 'CONFIRMED') ? `<button class="btn btn-danger btn-sm cancel-btn" data-id="${appt.id}">Hủy hẹn</button>` : ''}
                </div>`;
            container.appendChild(appointmentElement);
        });

        document.querySelectorAll('.cancel-btn').forEach(button => {
            button.addEventListener('click', function (e) {
                e.stopPropagation();
                currentAppointmentIdToCancel = this.dataset.id;
                if (confirmAppointmentIdDisplay)
                    confirmAppointmentIdDisplay.textContent = currentAppointmentIdToCancel;
                showCustomModal(cancelConfirmModal);
            });
        });

        document.querySelectorAll('.appointment-item').forEach(card => {
            card.addEventListener('click', function () {
                const appointment = allAppointmentsData.find(a => a.id == this.dataset.appointmentId);
                if (appointment)
                    showAppointmentDetail(appointment);
            });
        });

        // Render pagination controls
        renderPagination(currentPage, totalPages, paginationContainerId, isUpcoming);
    }

    function renderPagination(currentPage, totalPages, containerId, isUpcoming) {
        const paginationContainer = document.getElementById(containerId);
        if (!paginationContainer) return;

        paginationContainer.innerHTML = ''; // Clear previous buttons

        if (totalPages <= 1) {
            paginationContainer.style.display = 'none'; // Hide if only one page
            return;
        }
        paginationContainer.style.display = 'flex'; // Show pagination

        // Previous button
        const prevButton = document.createElement('button');
        prevButton.className = 'btn btn-sm btn-outline-primary pagination-button';
        prevButton.innerHTML = '&laquo; Trước';
        prevButton.disabled = currentPage === 1;
        prevButton.addEventListener('click', () => {
            if (isUpcoming) {
                currentPageUpcoming--;
            } else {
                currentPageHistory--;
            }
            fetchAppointmentsAndRenderUI();
        });
        paginationContainer.appendChild(prevButton);

        // Page number buttons
        // Only show a limited number of page buttons around the current page
        let startPage = Math.max(1, currentPage - 2);
        let endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            const firstPageButton = document.createElement('button');
            firstPageButton.className = 'btn btn-sm btn-outline-primary pagination-button';
            firstPageButton.textContent = '1';
            firstPageButton.addEventListener('click', () => {
                if (isUpcoming) {
                    currentPageUpcoming = 1;
                } else {
                    currentPageHistory = 1;
                }
                fetchAppointmentsAndRenderUI();
            });
            paginationContainer.appendChild(firstPageButton);
            if (startPage > 2) {
                const dots = document.createElement('span');
                dots.textContent = '...';
                dots.className = 'pagination-dots';
                paginationContainer.appendChild(dots);
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            const pageButton = document.createElement('button');
            pageButton.className = `btn btn-sm ${i === currentPage ? 'btn-primary' : 'btn-outline-primary'} pagination-button`;
            pageButton.textContent = i;
            pageButton.addEventListener('click', () => {
                if (isUpcoming) {
                    currentPageUpcoming = i;
                } else {
                    currentPageHistory = i;
                }
                fetchAppointmentsAndRenderUI();
            });
            paginationContainer.appendChild(pageButton);
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                const dots = document.createElement('span');
                dots.textContent = '...';
                dots.className = 'pagination-dots';
                paginationContainer.appendChild(dots);
            }
            const lastPageButton = document.createElement('button');
            lastPageButton.className = 'btn btn-sm btn-outline-primary pagination-button';
            lastPageButton.textContent = totalPages;
            lastPageButton.addEventListener('click', () => {
                if (isUpcoming) {
                    currentPageUpcoming = totalPages;
                } else {
                    currentPageHistory = totalPages;
                }
                fetchAppointmentsAndRenderUI();
            });
            paginationContainer.appendChild(lastPageButton);
        }


        // Next button
        const nextButton = document.createElement('button');
        nextButton.className = 'btn btn-sm btn-outline-primary pagination-button';
        nextButton.innerHTML = 'Sau &raquo;';
        nextButton.disabled = currentPage === totalPages;
        nextButton.addEventListener('click', () => {
            if (isUpcoming) {
                currentPageUpcoming++;
            } else {
                currentPageHistory++;
            }
            fetchAppointmentsAndRenderUI();
        });
        paginationContainer.appendChild(nextButton);
    }


    function showAppointmentDetail(appointment) {
        currentAppointmentData = appointment;
        if (detailContent) {
            detailContent.innerHTML = `
                <p><strong>Mã lịch hẹn:</strong> ${appointment.appointmentCode || 'N/A'}</p>
                <p><strong>Bác sĩ:</strong> ${appointment.doctorFullName}</p>
                <p><strong>Dịch vụ:</strong> ${appointment.serviceName}</p>
                <p><strong>Thời gian:</strong> ${formatDateForDisplay(appointment.appointmentDate)} lúc ${appointment.appointmentTime}</p>
                <p><strong>Ghi chú:</strong> ${appointment.notes || 'Không có'}</p>
                <p><strong>Trạng thái:</strong> <span class="badge ${getStatusBadgeClass(appointment.status)}">${mapStatusToVietnamese(appointment.status)}</span></p>
                <p><strong>Trạng thái thanh toán:</strong> <span class="badge ${getPaymentStatusBadgeClass(appointment.paymentStatus)}">${mapPaymentStatusToVietnamese(appointment.paymentStatus)}</span></p>`;
        }
        if (btnCancelAppointment) {
            btnCancelAppointment.style.display = (appointment.status === 'PENDING' || appointment.status === 'CONFIRMED') ? 'inline-block' : 'none';
        }
        showCustomModal(appointmentDetailModal);
    }

    async function fetchAppointmentsAndRenderUI() {
        try { 
            allAppointmentsData = await fetchAppointments();
            if (!Array.isArray(allAppointmentsData))
                allAppointmentsData = [];
            
            const now = new Date(); 

            const upcoming = [];
            const history = [];

            allAppointmentsData.forEach(appt => {
                if (!appt.appointmentDate || !appt.appointmentTime) {
                    history.push(appt); 
                    return;
                }

                const timeParts = appt.appointmentTime.split(' - ');
                const startTimeStr = timeParts[0];

                const apptDateTime = new Date(`${appt.appointmentDate}T${startTimeStr}:00`);

                if (apptDateTime >= now && (appt.status === 'PENDING' || appt.status === 'CONFIRMED')) {
                    upcoming.push(appt);
                } 
                else {
                    history.push(appt);
                }
            });

            upcoming.sort((a, b) => {
                const aTimeParts = a.appointmentTime.split(' - ');
                const bTimeParts = b.appointmentTime.split(' - ');
                const aDateTime = new Date(`${a.appointmentDate}T${aTimeParts[0]}:00`);
                const bDateTime = new Date(`${b.appointmentDate}T${bTimeParts[0]}:00`);
                return aDateTime - bDateTime;
            });

            history.sort((a, b) => {
                const aTimeParts = a.appointmentTime.split(' - ');
                const bTimeParts = b.appointmentTime.split(' - ');
                const aDateTime = new Date(`${a.appointmentDate}T${aTimeParts[0]}:00`);
                const bDateTime = new Date(`${b.appointmentDate}T${bTimeParts[0]}:00`);
                return bDateTime - aDateTime;
            });
            
            // Cập nhật tổng số trang
            totalPagesUpcoming = Math.ceil(upcoming.length / ITEMS_PER_PAGE);
            totalPagesHistory = Math.ceil(history.length / ITEMS_PER_PAGE);

            // Đảm bảo trang hiện tại không vượt quá tổng số trang
            if (currentPageUpcoming > totalPagesUpcoming && totalPagesUpcoming > 0) currentPageUpcoming = totalPagesUpcoming;
            if (currentPageUpcoming === 0 && totalPagesUpcoming > 0) currentPageUpcoming = 1;
            if (currentPageHistory > totalPagesHistory && totalPagesHistory > 0) currentPageHistory = totalPagesHistory;
            if (currentPageHistory === 0 && totalPagesHistory > 0) currentPageHistory = 1;


            renderAppointments(upcoming, upcomingAppointmentsList, true, currentPageUpcoming, totalPagesUpcoming, 'upcomingPagination');
            renderAppointments(history, appointmentHistoryList, false, currentPageHistory, totalPagesHistory, 'historyPagination');
        } catch (error) {
            console.error("Lỗi khi tải và render lịch hẹn:", error);
            showCustomAlert("Không thể tải danh sách lịch hẹn. Vui lòng thử lại. Lỗi: " + error.message, false);
        }
    }

    // --- Utility Functions ---
    function formatDateForDisplay(dateString) {
        return dateString ? new Date(dateString).toLocaleDateString('vi-VN') : '';
    }

    function formatTimeForDisplay(timeString) {
        if (!timeString)
            return '';
        const timeParts = timeString.split(' - ');
        if (timeParts.length > 0) {
            const tempDate = new Date(`1970-01-01T${timeParts[0]}`);
            if (!isNaN(tempDate.getTime())) {
                return tempDate.toLocaleTimeString('vi-VN', {hour: '2-digit', minute: '2-digit', hour12: false});
            }
        }
        return timeString;
    }

    function mapStatusToVietnamese(status) {
        return {PENDING: 'Đang chờ', CONFIRMED: 'Đã xác nhận', DONE: 'Đã hoàn thành', CANCELLED: 'Đã hủy', NO_SHOW: 'Vắng mặt'}[status] || status;
    }
    function getStatusBadgeClass(status) {
        return {PENDING: 'bg-warning', CONFIRMED: 'bg-primary', DONE: 'bg-success', CANCELLED: 'bg-danger', NO_SHOW: 'bg-secondary'}[status] || 'bg-secondary';
    }

    function mapPaymentStatusToVietnamese(paymentStatus) {
        return {UNPAID: 'Chưa thanh toán', PAID: 'Đã thanh toán'}[paymentStatus] || paymentStatus;
    }
    function getPaymentStatusBadgeClass(paymentStatus) {
        return {UNPAID: 'bg-danger', PAID: 'bg-success'}[paymentStatus] || 'bg-secondary';
    }


    // --- Event Listeners and Initial Load ---
    document.querySelectorAll('.tab-button').forEach(button => {
        button.addEventListener('click', function () {
            document.querySelectorAll('.tab-button, .tab-content').forEach(el => el.classList.remove('active', 'active-tab-content'));
            this.classList.add('active');
            const targetTabContent = document.getElementById(this.dataset.tabId);
            if (targetTabContent)
                targetTabContent.classList.add('active-tab-content');
        });
    });

    if (newAppointmentBtn) {
        newAppointmentBtn.addEventListener('click', async function () {
            newAppointmentBtn.disabled = true;

            try {
                appointmentForm.reset();
                notesInput.value = '';
                appointmentTimeSelect.innerHTML = '<option value="">-- Chọn giờ hẹn --</option>';
                appointmentTimeSelect.disabled = true;
                if (timeSlotMessage) {
                    timeSlotMessage.textContent = '';
                }
                doctorSelect.innerHTML = '<option value="">-- Chọn bác sĩ --</option>';
                serviceSelect.innerHTML = '<option value="">-- Chọn dịch vụ --</option>';
                appointmentDateInput.innerHTML = '<option value="">-- Vui lòng chọn ngày --</option>';
                appointmentDateInput.disabled = true;

                doctorsData = await fetchDoctors();
                servicesData = await fetchServices();

                doctorsData.forEach(doctor => {
                    const option = document.createElement('option');
                    option.value = doctor.id;
                    option.textContent = doctor.fullName;
                    doctorSelect.appendChild(option);
                });
                servicesData.forEach(service => {
                    const option = document.createElement('option');
                    option.value = service.servicesId;
                    option.textContent = service.serviceName;
                    serviceSelect.appendChild(option);
                });

                showCustomModal(newAppointmentModal);
            } catch (error) {
                console.error("Lỗi khi mở form đặt hẹn mới:", error);
                showCustomAlert("Không thể tải dữ liệu cho form đặt hẹn. Vui lòng thử lại.", false);
            } finally {
                newAppointmentBtn.disabled = false;
            }
        });
    }

    if (closeNewAppointmentModalBtn)
        closeNewAppointmentModalBtn.addEventListener('click', () => hideCustomModal(newAppointmentModal));
    if (document.getElementById('cancelNewAppointmentBtn'))
        document.getElementById('cancelNewAppointmentBtn').addEventListener('click', () => hideCustomModal(newAppointmentModal));
    if (closeConfirmModalBtn)
        closeConfirmModalBtn.addEventListener('click', () => hideCustomModal(cancelConfirmModal));
    if (cancelConfirmBtnElement)
        cancelConfirmBtnElement.addEventListener('click', () => hideCustomModal(cancelConfirmModal));
    if (closeDetailModalBtn)
        closeDetailModalBtn.addEventListener('click', () => hideCustomModal(appointmentDetailModal));
    if (btnCloseDetail)
        btnCloseDetail.addEventListener('click', () => hideCustomModal(appointmentDetailModal));

    if (doctorSelect) {
        doctorSelect.addEventListener('change', async function () {
            const selectedDoctorId = this.value;
            appointmentDateInput.innerHTML = '<option value="">-- Vui lòng chọn ngày --</option>';
            appointmentDateInput.disabled = true;
            appointmentTimeSelect.innerHTML = '<option value="">-- Chọn giờ hẹn --</option>';
            appointmentTimeSelect.disabled = true;
            if (timeSlotMessage)
                timeSlotMessage.textContent = '';

            if (selectedDoctorId) {
                const workingDates = await fetchWorkingDaysForDoctor(selectedDoctorId);
                if (workingDates.length > 0) {
                    workingDates.forEach(date => appointmentDateInput.appendChild(new Option(formatDateForDisplay(date), date)));
                    appointmentDateInput.disabled = false;
                } else {
                    if (timeSlotMessage)
                        timeSlotMessage.textContent = 'Bác sĩ này hiện không có ngày làm việc nào có sẵn.';
                }
            }
        });
    }

    async function populateAvailableTimeSlots() {
        const selectedDoctorId = doctorSelect.value;
        const selectedDate = appointmentDateInput.value;
        appointmentTimeSelect.innerHTML = '<option value="">-- Chọn giờ hẹn --</option>';
        appointmentTimeSelect.disabled = true;
        if (timeSlotMessage) {
            timeSlotMessage.textContent = '';
        }

        if (selectedDoctorId && selectedDate) {
            const slots = await fetchAvailableSlots(selectedDoctorId, selectedDate);
            if (slots.length > 0) {
                slots.forEach(slot => {
                    const option = document.createElement('option');
                    option.value = slot.id;

                    const startTime = formatTimeForDisplay(slot.startTime);
                    const endTime = formatTimeForDisplay(slot.endTime);

                    option.textContent = `${startTime} - ${endTime}`;

                    appointmentTimeSelect.appendChild(option);
                });
                appointmentTimeSelect.disabled = false;
            } else {
                if (timeSlotMessage) {
                    timeSlotMessage.textContent = 'Không có khung giờ trống cho ngày này.';
                }
            }
        }
    }

    if (appointmentDateInput)
        appointmentDateInput.addEventListener('change', populateAvailableTimeSlots);

    if (appointmentForm) {
        appointmentForm.addEventListener('submit', async function (event) {
            event.preventDefault();
            const doctorId = parseInt(doctorSelect.value);
            const serviceId = parseInt(serviceSelect.value);
            const slotId = parseInt(appointmentTimeSelect.value);
            const patientUserAccountId = USER_ACCOUNT_ID_FROM_JSP;

            if (!patientUserAccountId)
                return showCustomAlert('Lỗi: Không tìm thấy ID tài khoản bệnh nhân.', false);
            if (isNaN(doctorId) || isNaN(serviceId) || isNaN(slotId))
                return showCustomAlert('Vui lòng điền đủ thông tin.', false);

            const newAppointmentData = {
                patient_id: patientUserAccountId,
                doctor_id: doctorId, 
                service_id: serviceId,
                slot_id: slotId
            };

            try {
                const response = await fetch(`${API_BASE_URL}/appointments`, {method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(newAppointmentData)});
                const responseData = await response.json().catch(() => ({message: `Phản hồi không phải JSON hoặc server không trả về JSON khi lỗi.`}));
                
                if (response.ok) {
                    showCustomAlert('Lịch hẹn đã được đặt thành công!', true);
                    hideCustomModal(newAppointmentModal);
                    currentPageUpcoming = 1;
                    currentPageHistory = 1;
                    await fetchAppointmentsAndRenderUI(); // CHẮC CHẮN CHỜ HÀM NÀY HOÀN THÀNH
                } else {
                    showCustomAlert(responseData.message || `Lỗi không xác định khi đặt lịch hẹn. Mã lỗi: ${response.status}`, false);
                }

            } catch (error) {
                showCustomAlert('Lỗi mạng khi đặt lịch hẹn hoặc lỗi không xác định: ' + error.message, false);
            }
        });
    }

    if (btnCancelAppointment) {
        btnCancelAppointment.addEventListener('click', async function () { // THÊM ASYNC VÀO ĐÂY
            if (currentAppointmentData) {
                currentAppointmentIdToCancel = currentAppointmentData.id;
                if (confirmAppointmentIdDisplay)
                    confirmAppointmentIdDisplay.textContent = currentAppointmentIdToCancel;
                hideCustomModal(appointmentDetailModal);
                // BỌC GỌI MODAL BẰNG try-catch NẾU CÓ Promise CHƯA ĐƯỢC XỬ LÝ TRƯỚC ĐÓ
                try {
                    showCustomModal(cancelConfirmModal);
                } catch (error) {
                    console.error("Lỗi khi hiển thị modal xác nhận hủy:", error);
                    showCustomAlert("Không thể hiển thị hộp thoại xác nhận hủy. Vui lòng thử lại.", false);
                }
            }
        });
    }

    if (confirmCancelBtn) {
        confirmCancelBtn.addEventListener('click', async function () {
            if (!currentAppointmentIdToCancel)
                return;
            try {
                const response = await fetch(`${API_BASE_URL}/appointments/${currentAppointmentIdToCancel}/cancel`, {method: 'PUT', headers: {'Content-Type': 'application/json'}});
                const responseData = await response.json().catch(() => ({message: `Phản hồi không phải JSON hoặc server không trả về JSON khi lỗi.`}));
                if (response.ok) {
                    showCustomAlert('Lịch hẹn đã được hủy thành công.', true);
                    hideCustomModal(cancelConfirmModal);
                    currentPageUpcoming = 1;
                    currentPageHistory = 1;
                    await fetchAppointmentsAndRenderUI(); // CHẮC CHẮN CHỜ HÀM NÀY HOÀN THÀNH
                } else {
                    showCustomAlert('Lỗi khi hủy lịch hẹn: ' + (responseData.message || `Lỗi không xác định. Mã lỗi: ${response.status}`), false);
                }
            } catch (error) {
                showCustomAlert('Lỗi mạng khi hủy lịch hẹn.', false);
            } finally {
                currentAppointmentIdToCancel = null;
            }
        });
    }

    // Wrap the initial fetchAppointmentsAndRenderUI call in a try-catch to catch initial load errors
    (async () => { // HÀM TỰ GỌI ASYNC ĐỂ BẮT LỖI Promise KHI TẢI TRANG LẦN ĐẦU
        try {
            await fetchAppointmentsAndRenderUI();
        } catch (error) {
            console.error("Lỗi khi khởi tạo giao diện lịch hẹn:", error);
            showCustomAlert("Không thể tải dữ liệu ban đầu. Vui lòng thử lại. Lỗi: " + error.message, false);
        }
    })();
});