// patient-appointment-schedule.js

document.addEventListener('DOMContentLoaded', function () {

    // Cache DOM elements for modals and other frequently accessed elements
    const newAppointmentModal = document.getElementById('newAppointmentModal');
    const cancelConfirmModal = document.getElementById('cancelConfirmModal');
    const successMessageModal = document.getElementById('successMessageModal');
    const appointmentDetailModal = document.getElementById('appointmentDetailModal');

    // Các nút đóng/mở modal tùy chỉnh
    const closeNewAppointmentModalBtn = document.getElementById('closeNewAppointmentModalBtn');
    const closeConfirmModalBtn = document.getElementById('closeConfirmModalBtn');
    const closeSuccessModalBtn = document.getElementById('closeSuccessModalBtn');
    const okSuccessModalBtn = document.getElementById('okSuccessModalBtn');
    const closeDetailModalBtn = appointmentDetailModal ? appointmentDetailModal.querySelector('.close-button') : null;
    const btnCloseDetail = document.getElementById('btnCloseDetail');

    // Form và các trường nhập liệu
    const appointmentForm = document.getElementById('newAppointmentForm');
    const doctorSelect = document.getElementById('newDoctorName');
    const serviceSelect = document.getElementById('newService');
    const appointmentDateInput = document.getElementById('newAppointmentDate');
    const appointmentTimeSelect = document.getElementById('newAppointmentTime');
    const notesInput = document.getElementById('newNotes');

    const confirmCancelBtn = document.getElementById('confirmCancelBtn');
    const confirmAppointmentIdDisplay = document.getElementById('confirmAppointmentIdDisplay');
    const cancelConfirmBtnElement = document.getElementById('cancelConfirmBtn');

    const btnCancelAppointment = document.getElementById('btnCancelAppointment');
    const detailContent = document.getElementById('detailContent');

    // Các thẻ p thông báo không có lịch hẹn
    const upcomingAppointmentsList = document.getElementById('upcomingAppointmentsList');
    const appointmentHistoryList = document.getElementById('appointmentHistoryList');
    const noUpcomingAppointmentsParagraph = document.getElementById('noUpcomingAppointments');
    const noHistoryAppointmentsParagraph = document.getElementById('noHistoryAppointments');

    // Khai báo và gán nút "Đặt Hẹn Mới"
    const newAppointmentBtn = document.getElementById('btnNewAppointment');


    // Lấy API_BASE_URL từ biến đã được JSP truyền vào
    const API_BASE_URL = typeof API_BASE_URL_FROM_JSP !== 'undefined' ? API_BASE_URL_FROM_JSP : '/api/patient';

    let allAppointmentsData = [];
    let doctorsData = [];
    let servicesData = [];
    let currentAppointmentData = null;
    let currentAppointmentIdToCancel = null;

    // --- Các hàm thao tác Modal tùy chỉnh ---
    // Cập nhật để sử dụng class 'show-modal' cho transition mượt mà hơn và căn giữa
    function showCustomModal(modalElement) {
        if (modalElement) {
            modalElement.style.display = 'flex';
            requestAnimationFrame(() => {
                modalElement.classList.add('show-modal');
            });
        }
    }

    function hideCustomModal(modalElement) {
        if (modalElement) {
            modalElement.classList.remove('show-modal');
            setTimeout(() => {
                if (!modalElement.classList.contains('show-modal')) {
                    modalElement.style.display = 'none';
                }
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
            iconElement.className = 'fas'; // Reset all classes
            iconElement.classList.add(isSuccess ? 'fa-check-circle' : 'fa-exclamation-circle');
            iconElement.classList.add(isSuccess ? 'text-success' : 'text-danger');
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
                const errorData = await response.json().catch(() => ({message: `Server error: ${response.status} ${response.statusText}`}));
                throw new Error(errorData.message || errorMessage);
            }
            const data = await response.json();
            return data;
        } catch (error) {
            console.error('Error fetching data from ' + url + ':', error);
            showCustomAlert(errorMessage + ': ' + error.message, false);
            return [];
        }
    }

    async function fetchAppointments() {
        return fetchData(`${API_BASE_URL}/appointments`, 'Lỗi khi tải danh sách lịch hẹn.');
    }

    async function fetchDoctors() {
        return fetchData(`${API_BASE_URL}/doctors`, 'Lỗi khi tải danh sách bác sĩ.');
    }

    async function fetchServices() {
        return fetchData(`${API_BASE_URL}/services`, 'Lỗi khi tải danh sách dịch vụ.');
    }

    async function fetchAvailableSlots(doctorId, date) {
        return fetchData(`${API_BASE_URL}/slots/available?doctorId=${doctorId}&date=${date}`, 'Lỗi khi tải khung giờ trống.');
    }

    // --- Render Functions ---
    function renderAppointments(appointments, container, isUpcoming = true) {
        if (!container) {
            console.error("Container element not found for rendering appointments.");
            return;
        }
        container.innerHTML = '';

        const noAppointmentsParagraph = document.getElementById(isUpcoming ? 'noUpcomingAppointments' : 'noHistoryAppointments');

        if (appointments.length === 0) {
            if (noAppointmentsParagraph) {
                noAppointmentsParagraph.style.display = 'block';
            }
            return;
        } else {
            if (noAppointmentsParagraph) {
                noAppointmentsParagraph.style.display = 'none';
            }
        }

        appointments.forEach(appt => {
            const appointmentElement = document.createElement('div');
            const borderColorClass = appt.status === 'cancelled' ? 'border-danger' : '';
            appointmentElement.className = `card mb-3 appointment-item ${borderColorClass}`;
            appointmentElement.dataset.appointmentId = appt.id;

            // appointmentCode và notes không có trong DB, nên sẽ hiển thị "N/A" hoặc "Không có"
            // Lấy appointmentCode từ getter của model (sẽ tự động tạo nếu id có)
            const displayAppointmentCode = appt.appointmentCode || 'N/A';
            const displayNotes = appt.notes || 'Không có';

            appointmentElement.innerHTML = `
                    <div class="card-body">
                        <h5 class="card-title">${appt.serviceName || 'Dịch vụ không xác định'}</h5>
                        <p class="card-text">
                            <strong>Bác sĩ:</strong> ${appt.doctorName}<br>
                            <strong>Thời gian:</strong> ${formatDateForDisplay(appt.slotDate)} lúc ${formatTimeForDisplay(appt.slotTimeRange)}<br>
                            <strong>Trạng thái:</strong> <span class="badge ${getStatusBadgeClass(appt.status)}">${mapStatusToVietnamese(appt.status)}</span>
                            ${isUpcoming ? `<br><strong>Mã lịch hẹn:</strong> ${displayAppointmentCode}` : ''}
                        </p>
                        ${isUpcoming && (appt.status === 'pending' || appt.status === 'confirmed') ? `<button class="btn btn-danger btn-sm cancel-btn" data-id="${appt.id}">Hủy hẹn</button>` : ''}
                    </div>
                `;
            container.appendChild(appointmentElement);
        });

        if (isUpcoming) {
            document.querySelectorAll('.cancel-btn').forEach(button => {
                button.addEventListener('click', function (e) {
                    e.stopPropagation();
                    currentAppointmentIdToCancel = this.dataset.id;
                    if (confirmAppointmentIdDisplay) {
                        confirmAppointmentIdDisplay.textContent = currentAppointmentIdToCancel;
                    }
                    showCustomModal(cancelConfirmModal);
                });
            });
        }

        document.querySelectorAll('.appointment-item').forEach(card => {
            card.addEventListener('click', function () {
                const apptId = this.dataset.appointmentId;
                const appointment = allAppointmentsData.find(a => a.id == apptId);
                if (appointment) {
                    showAppointmentDetail(appointment);
                }
            });
        });
    }

    function showAppointmentDetail(appointment) {
        currentAppointmentData = appointment;
        // appointmentCode và notes không có trong DB, nên sẽ hiển thị "N/A" hoặc "Không có"
        const displayAppointmentCode = appointment.appointmentCode || 'N/A';
        const displayNotes = appointment.notes || 'Không có';

        if (detailContent) {
            detailContent.innerHTML = `
                    <p><strong>Mã lịch hẹn:</strong> ${displayAppointmentCode}</p>
                    <p><strong>Bác sĩ:</strong> ${appointment.doctorName}</p>
                    <p><strong>Dịch vụ:</strong> ${appointment.serviceName}</p>
                    <p><strong>Thời gian:</strong> ${formatDateForDisplay(appointment.slotDate)} lúc ${formatTimeForDisplay(appointment.slotTimeRange)}</p>
                    <p><strong>Ghi chú:</strong> ${displayNotes}</p>
                    <p><strong>Trạng thái:</strong> <span class="badge ${getStatusBadgeClass(appointment.status)}">${mapStatusToVietnamese(appointment.status)}</span></p>
                `;
        }

        if (btnCancelAppointment) {
            if (appointment.status === 'pending' || appointment.status === 'confirmed') {
                btnCancelAppointment.style.display = 'inline-block';
            } else {
                btnCancelAppointment.style.display = 'none';
            }
        }

        showCustomModal(appointmentDetailModal);
    }

    async function fetchAppointmentsAndRenderUI() {
        allAppointmentsData = await fetchAppointments();
        console.log("Fetched appointments data:", allAppointmentsData);

        if (!Array.isArray(allAppointmentsData)) {
            allAppointmentsData = [];
            console.warn("fetchAppointments did not return an array. Resetting to empty array.");
        }
        const now = new Date();

        const upcoming = allAppointmentsData.filter(appt => {
            if (!appt.slotDate || !appt.slotTimeRange) {
                console.warn(`Missing slotDate or slotTimeRange for appointment ID ${appt.id}. Skipping.`);
                return false;
            }
            const dateTimeString = `${appt.slotDate}T${appt.slotTimeRange.split(' - ')[0]}:00`;
            const appointmentDateTime = new Date(dateTimeString);

            if (isNaN(appointmentDateTime.getTime())) {
                console.warn(`Invalid date format for appointment ID ${appt.id}: ${dateTimeString}. Skipping.`);
                return false;
            }

            return (appointmentDateTime >= now && (appt.status === 'pending' || appt.status === 'confirmed'));
        }).sort((a, b) => {
            const dateTimeA = new Date(`${a.slotDate}T${a.slotTimeRange.split(' - ')[0]}:00`);
            const dateTimeB = new Date(`${b.slotDate}T${b.slotTimeRange.split(' - ')[0]}:00`);
            return dateTimeA - dateTimeB;
        });

        const history = allAppointmentsData.filter(appt => {
            if (!appt.slotDate || !appt.slotTimeRange) {
                console.warn(`Missing slotDate or slotTimeRange for appointment ID (history) ${appt.id}. Skipping.`);
                return false;
            }
            const dateTimeString = `${appt.slotDate}T${appt.slotTimeRange.split(' - ')[0]}:00`;
            const appointmentDateTime = new Date(dateTimeString);

            if (isNaN(appointmentDateTime.getTime())) {
                console.warn(`Invalid date format for appointment ID (history) ${appt.id}: ${dateTimeString}. Skipping.`);
                return false;
            }

            return (appointmentDateTime < now || appt.status === 'cancelled' || appt.status === 'completed' || appt.status === 'no_show' || appt.status === 'done' || appt.status === 'abandoned');
        }).sort((a, b) => {
            const dateTimeA = new Date(`${a.slotDate}T${a.slotTimeRange.split(' - ')[0]}:00`);
            const dateTimeB = new Date(`${b.slotDate}T${b.slotTimeRange.split(' - ')[0]}:00`);
            return dateTimeB - dateTimeA;
        });

        renderAppointments(upcoming, upcomingAppointmentsList, true);
        renderAppointments(history, appointmentHistoryList, false);
    }

    // --- Utility Functions ---
    function formatDateForDisplay(dateString) {
        if (!dateString)
            return '';
        const date = new Date(dateString);
        if (isNaN(date.getTime())) {
            console.warn("Invalid date string for formatting:", dateString);
            return dateString;
        }
        return date.toLocaleDateString('vi-VN');
    }

    function formatTimeForDisplay(timeRangeString) {
        if (!timeRangeString)
            return '';
        const [startTime] = timeRangeString.split(' - ');
        const tempDate = new Date(`2000-01-01T${startTime}`);
        if (isNaN(tempDate.getTime())) {
            console.warn("Invalid time string for formatting:", timeRangeString);
            return timeRangeString;
        }
        return tempDate.toLocaleTimeString('vi-VN', {hour: '2-digit', minute: '2-digit', hour12: true});
    }

    function mapStatusToVietnamese(status) {
        switch (status) {
            case 'pending':
                return 'Đang chờ';
            case 'confirmed':
                return 'Đã xác nhận';
            case 'completed':
                return 'Đã hoàn thành';
            case 'done':
                return 'Đã hoàn thành';
            case 'cancelled':
                return 'Đã hủy';
            case 'no_show':
                return 'Vắng mặt';
            case 'rescheduled':
                return 'Đã đổi lịch';
            case 'abandoned':
                return 'Đã bỏ lỡ';
            default:
                return status;
        }
    }

    function getStatusBadgeClass(status) {
        switch (status) {
            case 'pending':
                return 'bg-warning';
            case 'confirmed':
                return 'bg-primary';
            case 'completed':
                return 'bg-success';
            case 'done':
                return 'bg-success';
            case 'cancelled':
                return 'bg-danger';
            case 'no_show':
                return 'bg-secondary';
            case 'rescheduled':
                return 'bg-info';
            case 'abandoned':
                return 'bg-dark';
            default:
                return 'bg-secondary';
        }
    }

    // --- Event Listeners and Initial Load ---
    document.querySelectorAll('.tab-button').forEach(button => {
        button.addEventListener('click', function () {
            document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active-tab-content'));

            this.classList.add('active');
            const targetTabContent = document.getElementById(this.dataset.tabId);
            if (targetTabContent) {
                targetTabContent.classList.add('active-tab-content');
            }
        });
    });

    if (newAppointmentBtn) {
        newAppointmentBtn.addEventListener('click', async function () {
            appointmentForm.reset();
            notesInput.value = '';
            appointmentTimeSelect.innerHTML = '<option value="">-- Chọn giờ hẹn --</option>';
            appointmentTimeSelect.disabled = true;

            doctorSelect.innerHTML = '<option value="">-- Chọn bác sĩ --</option>';
            serviceSelect.innerHTML = '<option value="">-- Chọn dịch vụ --</option>';

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
                // Đã sửa từ service.services_id thành service.servicesId
                if (typeof service.servicesId === 'number' && !isNaN(service.servicesId)) {
                    option.value = service.servicesId;
                } else {
                    option.value = '';
                    console.warn("Service ID is not a valid number:", service);
                }
                option.textContent = service.serviceName;
                serviceSelect.appendChild(option);
            });

            const today = new Date();
            today.setDate(today.getDate());
            const minDate = today.toISOString().split('T')[0];
            appointmentDateInput.min = minDate;
            appointmentDateInput.value = '';

            showCustomModal(newAppointmentModal);
        });
    } else {
        console.error("Element with ID 'btnNewAppointment' not found. New appointment button functionality will not work.");
    }

    if (closeNewAppointmentModalBtn)
        closeNewAppointmentModalBtn.addEventListener('click', () => hideCustomModal(newAppointmentModal));
    const cancelNewAppointmentBtn = document.getElementById('cancelNewAppointmentBtn');
    if (cancelNewAppointmentBtn)
        cancelNewAppointmentBtn.addEventListener('click', () => hideCustomModal(newAppointmentModal));

    if (closeConfirmModalBtn)
        closeConfirmModalBtn.addEventListener('click', () => hideCustomModal(cancelConfirmModal));
    if (cancelConfirmBtnElement) {
        cancelConfirmBtnElement.addEventListener('click', () => {
            hideCustomModal(cancelConfirmModal);
            hideCustomModal(appointmentDetailModal); // Optional
            hideCustomModal(newAppointmentModal);     // Optional
        });
    }

    if (closeSuccessModalBtn)
        closeSuccessModalBtn.addEventListener('click', () => hideCustomModal(successMessageModal));
    if (okSuccessModalBtn)
        okSuccessModalBtn.addEventListener('click', () => hideCustomModal(successMessageModal));

    if (closeDetailModalBtn)
        closeDetailModalBtn.addEventListener('click', () => hideCustomModal(appointmentDetailModal));
    if (btnCloseDetail)
        btnCloseDetail.addEventListener('click', () => hideCustomModal(appointmentDetailModal));

    if (appointmentDateInput)
        appointmentDateInput.addEventListener('change', populateAvailableTimeSlots);
    if (doctorSelect)
        doctorSelect.addEventListener('change', populateAvailableTimeSlots);

    // Thêm listener cho serviceSelect để debug giá trị
    if (serviceSelect) {
        serviceSelect.addEventListener('change', function () {
            console.log("Service selected! serviceSelect.value:", this.value);
            console.log("Type of serviceSelect.value:", typeof this.value);
        });
    }

    async function populateAvailableTimeSlots() {
        const selectedDoctorId = doctorSelect.value;
        const selectedDate = appointmentDateInput.value;

        appointmentTimeSelect.innerHTML = '<option value="">-- Chọn giờ hẹn --</option>';
        appointmentTimeSelect.disabled = true;

        if (selectedDoctorId && selectedDate) {
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            const selectedDateObj = new Date(selectedDate);
            selectedDateObj.setHours(0, 0, 0, 0);

            if (selectedDateObj < today) {
                showCustomAlert('Không thể đặt lịch cho ngày trong quá khứ.', false);
                appointmentDateInput.value = '';
                return;
            }

            const slots = await fetchAvailableSlots(selectedDoctorId, selectedDate);
            if (slots.length > 0) {
                slots.forEach(slot => {
                    const option = document.createElement('option');
                    // Sử dụng trực tiếp slot.startTime vì Gson đã được cấu hình để serialize LocalTime thành chuỗi
                    const startTimeString = slot.startTime; // Giờ đây startTime sẽ là chuỗi "HH:mm"
                    if (startTimeString) {
                        const [hours, minutes] = startTimeString.split(':');
                        const displayTime = new Date(`2000-01-01T${hours}:${minutes}`).toLocaleTimeString('vi-VN', {hour: '2-digit', minute: '2-digit', hour12: true});

                        option.value = slot.id;
                        option.textContent = displayTime;
                        appointmentTimeSelect.appendChild(option);
                    } else {
                        console.warn("startTime is null for slot:", slot); // Đổi cảnh báo thành startTime
                    }
                });
                appointmentTimeSelect.disabled = false;
            } else {
                showCustomAlert('Không có khung giờ trống cho bác sĩ này vào ngày đã chọn.', false);
            }
        }
    }

    if (appointmentForm) {
        appointmentForm.addEventListener('submit', async function (event) {
            event.preventDefault();

            // Thêm logging để kiểm tra giá trị trước khi parse
            console.log("Giá trị doctorSelect.value:", doctorSelect.value);
            console.log("Giá trị serviceSelect.value:", serviceSelect.value); // Log this value
            console.log("Giá trị appointmentTimeSelect.value:", appointmentTimeSelect.value);

            const doctorId = parseInt(doctorSelect.value);
            const serviceId = parseInt(serviceSelect.value);
            const slotId = parseInt(appointmentTimeSelect.value);
            // notes không được gửi lên DB vì cột không tồn tại
            // const notes = notesInput.value;

            if (isNaN(doctorId) || isNaN(serviceId) || isNaN(slotId)) {
                console.error("Lỗi: Một trong các ID (Bác sĩ, Dịch vụ, Giờ hẹn) không phải là số.");
                showCustomAlert('Vui lòng điền đầy đủ thông tin bắt buộc (Bác sĩ, Dịch vụ, Giờ hẹn).', false);
                return;
            }

            const newAppointmentData = {
                doctor_id: doctorId,
                service_id: serviceId, // Đây là services_id trong DB
                slot_id: slotId
                        // notes không được gửi lên DB
                        // notes: notes
            };

            // Thêm logging để kiểm tra dữ liệu gửi đi
            console.log("Attempting to send new appointment data:", newAppointmentData);
            console.log("JSON payload:", JSON.stringify(newAppointmentData));

            try {
                const response = await fetch(`${API_BASE_URL}/appointments`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(newAppointmentData)
                });
                // Thêm logging để kiểm tra phản hồi từ server
                console.log("Server response status:", response.status, response.statusText);

                const responseData = await response.json().catch(() => ({message: 'Phản hồi không phải JSON'}));

                if (response.ok) {
                    showCustomAlert('Lịch hẹn đã được tạo thành công!', true);
                    hideCustomModal(newAppointmentModal);
                    fetchAppointmentsAndRenderUI();
                } else {
                    showCustomAlert('Lỗi khi tạo lịch hẹn: ' + (responseData.message || `Lỗi không xác định (${response.status})`), false);
                    console.error('Error creating appointment:', responseData);
                }
            } catch (error) {
                console.error('Network error during appointment creation:', error);
                showCustomAlert('Lỗi mạng khi tạo lịch hẹn. Vui lòng thử lại.', false);
            }
        });
    }

    if (btnCancelAppointment) {
        btnCancelAppointment.addEventListener('click', function () {
            if (currentAppointmentData) {
                currentAppointmentIdToCancel = currentAppointmentData.id;
                if (confirmAppointmentIdDisplay) {
                    confirmAppointmentIdDisplay.textContent = currentAppointmentIdToCancel;
                }
                hideCustomModal(appointmentDetailModal);
                showCustomModal(cancelConfirmModal);
            } else {
                showCustomAlert('Không có lịch hẹn nào được chọn để hủy.', false);
            }
        });
    }

    if (confirmCancelBtn) {
        confirmCancelBtn.addEventListener('click', async function () {
            if (!currentAppointmentIdToCancel) {
                showCustomAlert('Không có lịch hẹn nào được chọn để hủy.', false);
                return;
            }

            try {
                const response = await fetch(`${API_BASE_URL}/appointments/${currentAppointmentIdToCancel}/cancel`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                });

                const responseData = await response.json().catch(() => ({message: 'Phản hồi không phải JSON'}));

                if (response.ok) {
                    showCustomAlert('Lịch hẹn đã được hủy thành công.', true);
                    hideCustomModal(cancelConfirmModal);
                    fetchAppointmentsAndRenderUI();
                } else {
                    showCustomAlert('Lỗi khi hủy lịch hẹn: ' + (responseData.message || `Lỗi không xác định (${response.status})`), false);
                    console.error('Error cancelling appointment:', responseData);
                }
            } catch (error) {
                console.error('Network error during appointment cancellation:', error);
                showCustomAlert('Lỗi mạng khi hủy lịch hẹn. Vui lòng thử lại.', false);
            } finally {
                currentAppointmentIdToCancel = null;
            }
        });
    }

    // Tải dữ liệu ban đầu khi trang được tải
    fetchAppointmentsAndRenderUI();
});