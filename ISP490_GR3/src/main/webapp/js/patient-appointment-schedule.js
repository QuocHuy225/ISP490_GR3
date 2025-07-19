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
                const errorData = await response.json().catch(() => ({message: `Lỗi máy chủ: ${response.status}`}));
                throw new Error(errorData.message || errorMessage);
            }
            return await response.json();
        } catch (error) {
            console.error('Lỗi fetch data từ ' + url + ':', error);
            showCustomAlert(errorMessage + ': ' + error.message, false);
            return [];
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
    function renderAppointments(appointments, container, isUpcoming = true) {
        if (!container)
            return;
        container.innerHTML = '';
        const noAppointmentsParagraph = document.getElementById(isUpcoming ? 'noUpcomingAppointments' : 'noHistoryAppointments');
        if (appointments.length === 0) {
            if (noAppointmentsParagraph)
                noAppointmentsParagraph.style.display = 'block';
            return;
        } else {
            if (noAppointmentsParagraph)
                noAppointmentsParagraph.style.display = 'none';
        }

        appointments.forEach(appt => {
            const appointmentElement = document.createElement('div');
            appointmentElement.className = `card mb-3 appointment-item ${appt.status === 'CANCELLED' ? 'border-danger' : ''}`;
            appointmentElement.dataset.appointmentId = appt.id;

            // === THAY ĐỔI QUAN TRỌNG Ở ĐÂY ===
            appointmentElement.innerHTML = `
                <div class="card-body">
                    <h5 class="card-title">${appt.serviceName || 'Dịch vụ không xác định'}</h5>
                    <p class="card-text">
                        <strong>Bác sĩ:</strong> ${appt.doctorFullName}<br>
                       <strong>Thời gian:</strong> ${formatDateForDisplay(appt.appointmentDate)} lúc ${appt.appointmentTime}<br>
                        <strong>Trạng thái:</strong> <span class="badge ${getStatusBadgeClass(appt.status)}">${mapStatusToVietnamese(appt.status)}</span>
                        ${isUpcoming ? `<br><strong>Mã lịch hẹn:</strong> ${appt.appointmentCode || 'N/A'}` : ''}
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
    }

    function showAppointmentDetail(appointment) {
        currentAppointmentData = appointment;
        if (detailContent) {
            detailContent.innerHTML = `
                <p><strong>Mã lịch hẹn:</strong> ${appointment.appointmentCode || 'N/A'}</p>
                <p><strong>Bác sĩ:</strong> ${appointment.doctorFullName}</p>
                <p><strong>Dịch vụ:</strong> ${appointment.serviceName}</p>
                <p><strong>Thời gian:</strong> ${formatDateForDisplay(appointment.appointmentDate)} lúc ${formatTimeForDisplay(appointment.appointmentTime)}</p>
                <p><strong>Ghi chú:</strong> ${appointment.notes || 'Không có'}</p>
                <p><strong>Trạng thái:</strong> <span class="badge ${getStatusBadgeClass(appointment.status)}">${mapStatusToVietnamese(appointment.status)}</span></p>`;
        }
        if (btnCancelAppointment) {
            btnCancelAppointment.style.display = (appointment.status === 'PENDING' || appointment.status === 'CONFIRMED') ? 'inline-block' : 'none';
        }
        showCustomModal(appointmentDetailModal);
    }

    async function fetchAppointmentsAndRenderUI() {
        allAppointmentsData = await fetchAppointments();
        if (!Array.isArray(allAppointmentsData))
            allAppointmentsData = [];
        const now = new Date();
        const upcoming = allAppointmentsData.filter(appt => appt.appointmentDate && new Date(appt.appointmentDate) >= now && (appt.status === 'PENDING' || appt.status === 'CONFIRMED')).sort((a, b) => new Date(a.appointmentDate) - new Date(b.appointmentDate));
        const history = allAppointmentsData.filter(appt => !upcoming.includes(appt)).sort((a, b) => new Date(b.appointmentDate) - new Date(a.appointmentDate));
        renderAppointments(upcoming, upcomingAppointmentsList, true);
        renderAppointments(history, appointmentHistoryList, false);
    }

    // --- Utility Functions ---
    function formatDateForDisplay(dateString) {
        return dateString ? new Date(dateString).toLocaleDateString('vi-VN') : '';
    }

    // === THAY ĐỔI QUAN TRỌNG Ở ĐÂY ===
    function formatTimeForDisplay(timeString) {
        if (!timeString)
            return '';
        const tempDate = new Date(`1970-01-01T${timeString}`);
        if (isNaN(tempDate.getTime()))
            return timeString;
        return tempDate.toLocaleTimeString('vi-VN', {hour: '2-digit', minute: '2-digit', hour12: false});
    }

    function mapStatusToVietnamese(status) {
        return {PENDING: 'Đang chờ', CONFIRMED: 'Đã xác nhận', DONE: 'Đã hoàn thành', CANCELLED: 'Đã hủy', NO_SHOW: 'Vắng mặt'}[status] || status;
    }
    function getStatusBadgeClass(status) {
        return {PENDING: 'bg-warning', CONFIRMED: 'bg-primary', DONE: 'bg-success', CANCELLED: 'bg-danger', NO_SHOW: 'bg-secondary'}[status] || 'bg-secondary';
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
            // Vô hiệu hóa nút ngay khi bấm để tránh click đúp
            newAppointmentBtn.disabled = true;

            try {
                // Các công việc reset form giữ nguyên
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

                // Tải dữ liệu
                doctorsData = await fetchDoctors();
                servicesData = await fetchServices();

                // Đổ dữ liệu vào dropdown
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
                // Xử lý nếu có lỗi xảy ra trong quá trình fetch
                console.error("Lỗi khi mở form đặt hẹn mới:", error);
                showCustomAlert("Không thể tải dữ liệu cho form đặt hẹn. Vui lòng thử lại.", false);
            } finally {
                // Luôn bật lại nút sau khi mọi việc hoàn tất (dù thành công hay thất bại)
                newAppointmentBtn.disabled = false;
            }
        });
    }

    // Other modal close buttons
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

                    // Lấy cả startTime và endTime từ đối tượng slot
                    const startTime = formatTimeForDisplay(slot.startTime);
                    const endTime = formatTimeForDisplay(slot.endTime);

                    // Tạo chuỗi khoảng thời gian
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
                const responseData = await response.json().catch(() => ({message: 'Phản hồi không phải JSON'}));
                if (response.ok) {
                    showCustomAlert('Lịch hẹn đã được tạo thành công!', true);
                    hideCustomModal(newAppointmentModal);
                    fetchAppointmentsAndRenderUI();
                } else {
                    showCustomAlert('Lỗi khi tạo lịch hẹn: ' + (responseData.message || `Lỗi không xác định`), false);
                }
            } catch (error) {
                showCustomAlert('Lỗi mạng khi tạo lịch hẹn.', false);
            }
        });
    }

    if (btnCancelAppointment) {
        btnCancelAppointment.addEventListener('click', function () {
            if (currentAppointmentData) {
                currentAppointmentIdToCancel = currentAppointmentData.id;
                if (confirmAppointmentIdDisplay)
                    confirmAppointmentIdDisplay.textContent = currentAppointmentIdToCancel;
                hideCustomModal(appointmentDetailModal);
                showCustomModal(cancelConfirmModal);
            }
        });
    }

    if (confirmCancelBtn) {
        confirmCancelBtn.addEventListener('click', async function () {
            if (!currentAppointmentIdToCancel)
                return;
            try {
                const response = await fetch(`${API_BASE_URL}/appointments/${currentAppointmentIdToCancel}/cancel`, {method: 'PUT', headers: {'Content-Type': 'application/json'}});
                const responseData = await response.json().catch(() => ({message: 'Phản hồi không phải JSON'}));
                if (response.ok) {
                    showCustomAlert('Lịch hẹn đã được hủy thành công.', true);
                    hideCustomModal(cancelConfirmModal);
                    fetchAppointmentsAndRenderUI();
                } else {
                    showCustomAlert('Lỗi khi hủy lịch hẹn: ' + (responseData.message || `Lỗi không xác định`), false);
                }
            } catch (error) {
                showCustomAlert('Lỗi mạng khi hủy lịch hẹn.', false);
            } finally {
                currentAppointmentIdToCancel = null;
            }
        });
    }

    fetchAppointmentsAndRenderUI();
});