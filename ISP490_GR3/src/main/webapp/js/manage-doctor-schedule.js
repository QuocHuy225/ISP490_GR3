document.addEventListener('DOMContentLoaded', () => {
    // contextPath sẽ được định nghĩa trong JSP trước khi tải file JS này.
    // Ví dụ: <script>const contextPath = "${pageContext.request.contextPath}";</script>
    // Đảm bảo dòng này có trong JSP của bạn.
    const contextPath = "${pageContext.request.contextPath}"; // Lấy contextPath từ JSP

    const editScheduleModal = new bootstrap.Modal(document.getElementById('editScheduleModal'));
    const modalDoctorName = document.getElementById('modalDoctorName');
    const modalDoctorId = document.getElementById('modalDoctorId'); // hidden input for doctorId
    const modalDoctorAccountId = document.getElementById('modalDoctorAccountId'); // hidden input for doctorAccountId
    const modalScheduleForm = document.getElementById('modalScheduleForm');
    const saveScheduleBtn = document.getElementById('saveScheduleBtn');
    const modalLoadingSpinner = document.getElementById('modalLoadingSpinner');
    const modalMessageContainer = document.getElementById('modalMessageContainer');

    const periodFuture = document.getElementById('periodFuture');
    const periodRange = document.getElementById('periodRange');
    const dateRangePickerContainer = document.getElementById('dateRangePickerContainer');
    const dateRangePicker = document.getElementById('dateRangePicker');

    const appointmentDurationSelect = document.getElementById('appointmentDuration'); 
    // Khôi phục prepTime để phù hợp với JSP mới nhất
    const prepTimeSelect = document.getElementById('prepTime');

    const weeklyScheduleContainer = document.getElementById('weeklySchedule'); 
    // Các biến cho overlay "Áp dụng tương tự" đã được khôi phục
    const applySimilarOverlay = document.getElementById('applySimilarOverlay');
    const applySimilarCheckboxes = document.getElementById('applySimilarCheckboxes');
    const cancelApplySimilarBtn = document.getElementById('cancelApplySimilarBtn');
    const confirmApplySimilarBtn = document.getElementById('confirmApplySimilarBtn');

    let flatpickrInstance;
    let weeklyScheduleConfig = {}; // Object để lưu trữ cấu hình lịch làm việc hàng tuần

    // Initialize Flatpickr
    flatpickrInstance = flatpickr(dateRangePicker, {
        mode: "range",
        dateFormat: "Y-m-d",
        minDate: "today" 
    });

    // Toggle date range picker visibility based on radio selection
    periodFuture.addEventListener('change', () => {
        if (periodFuture.checked) {
            dateRangePickerContainer.style.display = 'none';
            flatpickrInstance.clear(); 
        }
    });
    periodRange.addEventListener('change', () => {
        if (periodRange.checked) {
            dateRangePickerContainer.style.display = 'block';
        }
    });

    // Days of the week for scheduling (keys must match backend)
    const daysOfWeekDisplay = ['Chủ Nhật', 'Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7'];
    const dayKeys = ['sunday', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday'];

    /**
     * Tạo HTML cho một chip khung giờ.
     * @param {string} start - Thời gian bắt đầu (HH:mm).
     * @param {string} end - Thời gian kết thúc (HH:mm).
     * @param {string} dayKey - Khóa ngày (e.g., 'monday').
     * @param {number} slotIndex - Chỉ số của slot trong mảng của ngày.
     */
    function createSlotChipHTML(start, end, dayKey, slotIndex) {
        return `
            <div class="slot-chip" data-day-key="${dayKey}" data-slot-index="${slotIndex}" data-start="${start}" data-end="${end}">
                ${start} - ${end}
                <button type="button" class="btn-close remove-slot-btn" aria-label="Remove slot"></button>
            </div>
        `;
    }

    /**
     * Render (hoặc re-render) các khung giờ cho một ngày cụ thể dựa trên weeklyScheduleConfig.
     * @param {string} dayKey - Khóa ngày cần render.
     */
    function renderDaySlots(dayKey) {
        const container = document.getElementById(`day-${dayKey}-slots`);
        if (!container) return;

        container.innerHTML = ''; // Xóa tất cả các chip hiện có

        const slots = weeklyScheduleConfig[dayKey] || [];
        if (slots.length === 0) {
            container.innerHTML = `<p class="text-muted text-sm" id="no-slots-${dayKey}">Chưa có khung giờ nào.</p>`;
        } else {
            slots.forEach((slot, index) => {
                container.innerHTML += createSlotChipHTML(slot.start, slot.end, dayKey, index);
            });
        }
    }

    /**
     * Tạo HTML chính cho một ngày trong lịch làm việc hàng tuần (toggle, nút thêm/sao chép).
     * @param {number} dayIndex - Chỉ số ngày trong tuần (0-6).
     */
    function generateDaySectionHTML(dayIndex) {
        const dayKey = dayKeys[dayIndex];
        const isChecked = weeklyScheduleConfig[dayKey] && weeklyScheduleConfig[dayKey].length > 0;
        const displayStyle = isChecked ? 'inline-block' : 'none';

        return `
            <div class="d-flex align-items-center mb-3 border-bottom pb-2">
                <div class="form-check form-switch me-3">
                    <input class="form-check-input day-toggle" type="checkbox" id="toggle-${dayKey}" data-day-key="${dayKey}" ${isChecked ? 'checked' : ''}>
                    <label class="form-check-label fw-bold" for="toggle-${dayKey}">
                        ${daysOfWeekDisplay[dayIndex]}
                    </label>
                </div>
                <div class="ms-auto btn-group" role="group">
                    <button type="button" class="btn btn-sm btn-outline-success add-slot-btn me-2" data-day-key="${dayKey}" style="display: ${displayStyle};">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-plus" viewBox="0 0 16 16">
                          <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/>
                        </svg>
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-info duplicate-slot-btn" data-day-key="${dayKey}" style="display: ${displayStyle};">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-copy" viewBox="0 0 16 16">
                          <path fill-rule="evenodd" d="M4 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V2zM6 2a1 1 0 0 0-1 1v8a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V3a1 1 0 0 0-1-1H6z"/>
                          <path fill-rule="evenodd" d="M2 5a1 1 0 0 0-1 1v8a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1v-1h1v1a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h1v1H2z"/>
                        </svg>
                    </button>
                </div>
            </div>
            <div id="day-${dayKey}-slots" class="mb-3 ms-4" style="display: ${isChecked ? 'block' : 'none'};">
                <!-- Slots will be rendered here by renderDaySlots -->
            </div>
        `;
    }

    /**
     * Khởi tạo hoặc cập nhật toàn bộ giao diện lịch làm việc hàng tuần.
     * @param {Object} config - Cấu hình lịch làm việc (optional, dùng để load dữ liệu ban đầu).
     */
    function populateWeeklySchedules(config = {}) {
        weeklyScheduleConfig = config; 
        weeklyScheduleContainer.innerHTML = '';
        const orderedDayIndices = [1, 2, 3, 4, 5, 6, 0]; 
        
        orderedDayIndices.forEach(index => {
            weeklyScheduleContainer.innerHTML += generateDaySectionHTML(index);
        });
        
        orderedDayIndices.forEach(index => {
            renderDaySlots(dayKeys[index]);
        });
        
        attachAllListeners(); 
    }

    /**
     * Đính kèm tất cả các trình nghe sự kiện động.
     */
    function attachAllListeners() {
        document.querySelectorAll('.day-toggle').forEach(toggle => {
            toggle.removeEventListener('change', handleDayToggleChange); 
            toggle.addEventListener('change', handleDayToggleChange);
        });
        document.querySelectorAll('.add-slot-btn').forEach(btn => {
            btn.removeEventListener('click', handleAddSlotClick); 
            btn.addEventListener('click', handleAddSlotClick);
        });
        document.querySelectorAll('.duplicate-slot-btn').forEach(btn => {
            btn.removeEventListener('click', handleDuplicateSlotClick); 
            btn.addEventListener('click', handleDuplicateSlotClick);
        });
        weeklyScheduleContainer.removeEventListener('click', handleRemoveSlotClick); 
        weeklyScheduleContainer.addEventListener('click', handleRemoveSlotClick); 

        // Các sự kiện cho overlay "Áp dụng tương tự" đã được khôi phục
        if (cancelApplySimilarBtn) {
            cancelApplySimilarBtn.removeEventListener('click', hideApplySimilarOverlay);
            cancelApplySimilarBtn.addEventListener('click', hideApplySimilarOverlay);
        }
        if (confirmApplySimilarBtn) {
            confirmApplySimilarBtn.removeEventListener('click', applySimilarSchedule);
            confirmApplySimilarBtn.addEventListener('click', applySimilarSchedule);
        }
    }

    function handleDayToggleChange(e) {
        const dayKey = e.target.dataset.dayKey; 
        const container = document.getElementById(`day-${dayKey}-slots`);
        const addSlotBtn = e.target.closest('.d-flex').querySelector('.add-slot-btn');
        const duplicateSlotBtn = e.target.closest('.d-flex').querySelector('.duplicate-slot-btn'); 

        if (e.target.checked) {
            container.style.display = 'block';
            addSlotBtn.style.display = 'inline-block';
            duplicateSlotBtn.style.display = 'inline-block'; 
            if (!(weeklyScheduleConfig[dayKey] && weeklyScheduleConfig[dayKey].length > 0)) {
                container.innerHTML = `<p class="text-muted text-sm" id="no-slots-${dayKey}">Chưa có khung giờ nào.</p>`;
            }
        } else {
            container.style.display = 'none';
            addSlotBtn.style.display = 'none';
            duplicateSlotBtn.style.display = 'none'; 
            weeklyScheduleConfig[dayKey] = [];
            renderDaySlots(dayKey); 
        }
    }

    function handleAddSlotClick(e) {
        const dayKey = e.target.dataset.dayKey; 
        const duration = parseInt(appointmentDurationSelect.value);
        if (isNaN(duration) || duration <= 0) {
            displayModalMessage('danger', 'Vui lòng chọn thời gian cuộc hẹn mặc định hợp lệ.');
            return;
        }

        const newSlots = [];
        let currentTime = LocalTime.of(8, 0); 
        const endTimeOfDay = LocalTime.of(17, 0); 

        while (currentTime.plusMinutes(duration).isBefore(endTimeOfDay) || currentTime.plusMinutes(duration).equals(endTimeOfDay)) {
            newSlots.push({
                start: currentTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                end: currentTime.plusMinutes(duration).format(DateTimeFormatter.ofPattern("HH:mm"))
            });
            currentTime = currentTime.plusMinutes(duration);
        }

        weeklyScheduleConfig[dayKey] = newSlots; 
        renderDaySlots(dayKey); 
        const toggle = document.getElementById(`toggle-${dayKey}`);
        if (!toggle.checked) {
            toggle.checked = true; 
            handleDayToggleChange({ target: toggle }); 
        }
        // Removed success message as per user request
        // displayModalMessage('success', `Đã tạo các khung giờ ${duration} phút cho ${daysOfWeekDisplay[dayKeys.indexOf(dayKey)]}.`);
    }

    function handleRemoveSlotClick(e) {
        if (e.target.classList.contains('remove-slot-btn') || e.target.closest('.remove-slot-btn')) {
            const chip = e.target.closest('.slot-chip');
            if (chip) {
                const dayKey = chip.dataset.dayKey;
                const slotIndex = parseInt(chip.dataset.slotIndex);

                if (weeklyScheduleConfig[dayKey] && weeklyScheduleConfig[dayKey].length > slotIndex) {
                    weeklyScheduleConfig[dayKey].splice(slotIndex, 1); 
                    renderDaySlots(dayKey); 
                }
            }
        }
    }

    let sourceDayForDuplication = null; 

    // Các hàm cho overlay "Áp dụng tương tự" (đã được hoàn thiện)
    function handleDuplicateSlotClick(e) {
        sourceDayForDuplication = e.target.closest('.duplicate-slot-btn').dataset.dayKey;
        const sourceDayDisplay = daysOfWeekDisplay[dayKeys.indexOf(sourceDayForDuplication)];

        if (!weeklyScheduleConfig[sourceDayForDuplication] || weeklyScheduleConfig[sourceDayForDuplication].length === 0) {
            displayModalMessage('info', `Không có khung giờ nào để sao chép từ ${sourceDayDisplay}.`);
            sourceDayForDuplication = null; // Reset
            return;
        }

        // Đặt vị trí overlay gần nút đã click
        const button = e.target.closest('.duplicate-slot-btn'); // Đảm bảo nhắm đúng nút
        const buttonRect = button.getBoundingClientRect();
        const modalBody = document.querySelector('.modal-body'); // Lấy modal body để tính offset
        const modalBodyRect = modalBody.getBoundingClientRect();

        // Tính toán vị trí tương đối với modal body
        const topOffset = buttonRect.top - modalBodyRect.top + buttonRect.height + 10; // 10px padding
        // Tính toán left offset để nó xuất hiện bên phải nút
        const leftOffset = buttonRect.left - modalBodyRect.left;

        applySimilarOverlay.style.top = `${topOffset}px`;
        applySimilarOverlay.style.left = `${leftOffset}px`; // Đặt vị trí theo left
        applySimilarOverlay.style.right = 'auto'; // Đảm bảo không bị ảnh hưởng bởi right cũ

        // Đổ dữ liệu checkboxes vào overlay
        applySimilarCheckboxes.innerHTML = '';

        // Add "Select All" checkbox
        const selectAllDiv = document.createElement('div');
        selectAllDiv.className = 'form-check mb-2';
        selectAllDiv.innerHTML = `
            <input class="form-check-input" type="checkbox" id="apply-to-all">
            <label class="form-check-label fw-bold" for="apply-to-all">Chọn tất cả</label>
        `;
        applySimilarCheckboxes.appendChild(selectAllDiv);

        const selectAllCheckbox = document.getElementById('apply-to-all');

        dayKeys.forEach(dayKey => {
            if (dayKey !== sourceDayForDuplication) { // Không hiển thị ngày nguồn
                const dayIndex = dayKeys.indexOf(dayKey);
                const checkboxDiv = document.createElement('div');
                checkboxDiv.className = 'form-check';
                checkboxDiv.innerHTML = `
                    <input class="form-check-input apply-to-day-checkbox" type="checkbox" id="apply-to-${dayKey}" value="${dayKey}">
                    <label class="form-check-label" for="apply-to-${dayKey}">${daysOfWeekDisplay[dayIndex]}</label>
                `;
                applySimilarCheckboxes.appendChild(checkboxDiv);
            }
        });

        // Add listener for "Select All"
        selectAllCheckbox.addEventListener('change', (event) => {
            document.querySelectorAll('.apply-to-day-checkbox').forEach(checkbox => {
                checkbox.checked = event.target.checked;
            });
        });

        applySimilarOverlay.classList.remove('d-none'); // Hiển thị overlay
    }

    function applySimilarSchedule() {
        const selectedTargetDayKeys = Array.from(applySimilarCheckboxes.querySelectorAll('.apply-to-day-checkbox:checked'))
                                           .map(cb => cb.value);

        if (selectedTargetDayKeys.length === 0) {
            displayModalMessage('warning', 'Vui lòng chọn ít nhất một ngày để áp dụng.');
            return;
        }

        if (!sourceDayForDuplication) {
            displayModalMessage('danger', 'Lỗi: Không tìm thấy ngày nguồn để sao chép.');
            hideApplySimilarOverlay();
            return;
        }

        const sourceSlots = JSON.parse(JSON.stringify(weeklyScheduleConfig[sourceDayForDuplication])); // Deep copy

        selectedTargetDayKeys.forEach(targetDayKey => {
            weeklyScheduleConfig[targetDayKey] = sourceSlots; // Gán bản sao
            
            // Đảm bảo toggle của ngày đích được bật
            const targetToggle = document.getElementById(`toggle-${targetDayKey}`);
            if (targetToggle && !targetToggle.checked) {
                targetToggle.checked = true;
                handleDayToggleChange({ target: targetToggle }); // Kích hoạt sự kiện change để hiển thị container và nút
            } else if (targetToggle && targetToggle.checked) {
                // Nếu đã bật, chỉ cần render lại
                renderDaySlots(targetDayKey);
            }
        });

        displayModalMessage('success', `Đã sao chép lịch thành công từ ${daysOfWeekDisplay[dayKeys.indexOf(sourceDayForDuplication)]} sang các ngày đã chọn.`);
        hideApplySimilarOverlay();
    }

    function hideApplySimilarOverlay() {
        applySimilarOverlay.classList.add('d-none');
        sourceDayForDuplication = null; // Reset biến nguồn
    }

    // Event listener for "Chỉnh sửa lịch" buttons in doctor list
    document.querySelectorAll('.edit-schedule-btn').forEach(button => {
        button.addEventListener('click', async () => {
            const currentEditingDoctorId = button.dataset.doctorId;
            const currentEditingDoctorAccountId = button.dataset.doctorAccountId;
            const doctorName = button.dataset.doctorName;

            modalDoctorName.textContent = doctorName;
            modalDoctorId.value = currentEditingDoctorId;
            modalDoctorAccountId.value = currentEditingDoctorAccountId;

            modalScheduleForm.reset();
            periodFuture.checked = true; 
            dateRangePickerContainer.style.display = 'none';
            flatpickrInstance.clear();
            
            weeklyScheduleConfig = {}; 
            populateWeeklySchedules(weeklyScheduleConfig); 
            modalMessageContainer.innerHTML = ''; 
            hideApplySimilarOverlay(); // Đảm bảo overlay ẩn

            await fetchAndPopulateDoctorSchedule(currentEditingDoctorId);

            editScheduleModal.show();
        });
    });

    // Function to fetch and populate existing doctor schedule data
    async function fetchAndPopulateDoctorSchedule(doctorId) {
        modalLoadingSpinner.classList.remove('d-none'); 
        saveScheduleBtn.disabled = true; 

        try {
            // Cập nhật endpoint để lấy cấu hình chi tiết từ DAODoctor thông qua DoctorScheduleController
            // Endpoint trả về một object chứa cả workDates và detailedScheduleConfig
            const response = await fetch(`${contextPath}/doctor/schedule?doctorAccountId=${doctorId}`); 
            
            let responseData;
            let isJson = false;

            try {
                responseData = await response.json();
                isJson = true;
            } catch (jsonError) {
                try {
                    responseData = await response.text();
                } catch (textError) {
                    console.error("Failed to read response body as text after json parse failure:", textError);
                    responseData = "Unknown error (failed to read response body).";
                }
                isJson = false;
            }

            if (!response.ok) {
                let errorMessage = "Đã xảy ra lỗi không xác định.";
                if (isJson && responseData && responseData.error) {
                    errorMessage = responseData.error;
                } else if (!isJson) {
                    errorMessage = `Lỗi HTTP! Trạng thái: ${response.status}. Phản hồi: ${responseData}`;
                } else {
                    errorMessage = `Lỗi HTTP! Trạng thái: ${response.status}.`;
                }
                throw new Error(errorMessage);
            }
            
            // Lấy phần detailedScheduleConfig từ responseData
            const detailedScheduleConfigData = responseData.detailedScheduleConfig || {}; 

            // Cập nhật các trường form
            appointmentDurationSelect.value = detailedScheduleConfigData.appointment_duration || "30";
            // Xử lý prepTime
            prepTimeSelect.value = detailedScheduleConfigData.prep_time || "0"; 

            if (detailedScheduleConfigData.schedule_period === 'range') {
                periodRange.checked = true;
                dateRangePickerContainer.style.display = 'block';
                if (detailedScheduleConfigData.date_range_start && detailedScheduleConfigData.date_range_end) {
                    flatpickrInstance.setDate([detailedScheduleConfigData.date_range_start, detailedScheduleConfigData.date_range_end], true);
                }
            } else {
                periodFuture.checked = true;
                dateRangePickerContainer.style.display = 'none';
                flatpickrInstance.clear();
            }

            // Populate weekly schedules
            populateWeeklySchedules(detailedScheduleConfigData.weekly_schedule || {});

        } catch (error) {
            console.error("Lỗi khi tải lịch làm việc chi tiết:", error);
            displayModalMessage('danger', `Không thể tải lịch làm việc chi tiết: ${error.message}. Vui lòng thử lại.`);
            populateWeeklySchedules({}); 
        } finally {
            modalLoadingSpinner.classList.add('d-none');
            saveScheduleBtn.disabled = false;
        }
    }

    // Hàm hỗ trợ hiển thị thông báo trong modal
    function displayModalMessage(type, message) {
        modalMessageContainer.innerHTML = `
            <div class="alert alert-${type} alert-dismissible fade show rounded-md" role="alert">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
    }

    // Handle form submission inside the modal
    modalScheduleForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        modalMessageContainer.innerHTML = ''; 

        saveScheduleBtn.disabled = true;
        modalLoadingSpinner.classList.remove('d-none');

        const formData = new FormData(modalScheduleForm);
        const dataToSend = {
            doctor_account_id: formData.get('doctor_account_id'),
            appointment_duration: formData.get('appointment_duration'),
            prep_time: formData.get('prep_time'), // Bao gồm prep_time
            schedule_period: formData.get('schedulePeriod')
        };

        if (dataToSend.schedule_period === 'range') {
            const dateRange = flatpickrInstance.selectedDates;
            if (dateRange.length === 2) {
                dataToSend.date_range_start = dateRange[0].toISOString().split('T')[0];
                dataToSend.date_range_end = dateRange[1].toISOString().split('T')[0];
            } else {
                displayModalMessage('danger', 'Vui lòng chọn đầy đủ khoảng ngày.');
                saveScheduleBtn.disabled = false;
                modalLoadingSpinner.classList.add('d-none');
                return;
            }
        }

        dataToSend.weekly_schedule = {}; 
        dayKeys.forEach((dayKey, dayIndex) => {
            const toggle = document.getElementById(`toggle-${dayKey}`); 
            if (toggle && toggle.checked) {
                const container = document.getElementById(`day-${dayKey}-slots`); 
                const slots = [];
                container.querySelectorAll('.slot-chip').forEach(slotDiv => {
                    const start = slotDiv.dataset.start;
                    const end = slotDiv.dataset.end;
                    if (start && end) {
                        slots.push({ start: start, end: end });
                    }
                });
                dataToSend.weekly_schedule[dayKey] = slots;
            } else {
                dataToSend.weekly_schedule[dayKey] = []; 
            }
        });

        try {
            // Endpoint để lưu cấu hình chi tiết là /doctor/schedule (doPost của DoctorScheduleController)
            const response = await fetch(`${contextPath}/doctor/schedule`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json' 
                },
                body: JSON.stringify(dataToSend)
            });

            const result = await response.json();

            if (!response.ok) {
                throw new Error(result.message || `Lỗi HTTP! Trạng thái: ${response.status}`);
            }

            displayModalMessage('success', 'Lịch làm việc đã được lưu thành công!');
        } catch (error) {
            console.error("Lỗi khi lưu lịch:", error);
            displayModalMessage('danger', `Lỗi khi lưu lịch: ${error.message}`);
        } finally {
            saveScheduleBtn.disabled = false;
            modalLoadingSpinner.classList.add('d-none');
        }
    });

    // Polyfill cho LocalTime và DateTimeFormatter đơn giản
    class LocalTime {
        constructor(hours, minutes) {
            this.hours = hours;
            this.minutes = minutes;
        }

        plusMinutes(minutesToAdd) {
            let totalMinutes = this.hours * 60 + this.minutes + minutesToAdd;
            let newHours = Math.floor(totalMinutes / 60);
            let newMinutes = totalMinutes % 60;
            if (newHours >= 24) newHours -= 24; 
            return new LocalTime(newHours, newMinutes);
        }

        isBefore(otherTime) {
            if (this.hours < otherTime.hours) return true;
            if (this.hours > otherTime.hours) return false;
            return this.minutes < otherTime.minutes;
        }

        equals(otherTime) {
            return this.hours === otherTime.hours && this.minutes === otherTime.minutes;
        }

        format(formatter) {
            const h = String(this.hours).padStart(2, '0');
            const m = String(this.minutes).padStart(2, '0');
            if (formatter.pattern === "HH:mm") {
                return `${h}:${m}`;
            }
            return `${h}:${m}`; 
        }

        static of(hours, minutes) {
            return new LocalTime(hours, minutes);
        }
    }

    class DateTimeFormatter {
        constructor(pattern) {
            this.pattern = pattern;
        }
        static ofPattern(pattern) {
            return new DateTimeFormatter(pattern);
        }
    }
});
