document.addEventListener('DOMContentLoaded', () => {
    // contextPath sẽ được định nghĩa trong JSP trước khi tải file JS này.
    const editScheduleModal = new bootstrap.Modal(document.getElementById('editScheduleModal'));
    const modalDoctorName = document.getElementById('modalDoctorName');
    const modalDoctorId = document.getElementById('modalDoctorId'); // input ẩn cho doctorId
    const modalDoctorAccountId = document.getElementById('modalDoctorAccountId'); // input ẩn cho doctorAccountId
    const modalScheduleForm = document.getElementById('modalScheduleForm');
    const saveScheduleBtn = document.getElementById('saveScheduleBtn');
    const modalLoadingSpinner = document.getElementById('modalLoadingSpinner');
    const modalMessageContainer = document.getElementById('modalMessageContainer');

    const periodFuture = document.getElementById('periodFuture');
    const periodRange = document.getElementById('periodRange');
    const dateRangePickerContainer = document.getElementById('dateRangePickerContainer');
    const dateRangePicker = document.getElementById('dateRangePicker');

    const appointmentDurationSelect = document.getElementById('appointmentDuration');

    const weeklyScheduleContainer = document.getElementById('weeklySchedule');
    const applySimilarOverlay = document.getElementById('applySimilarOverlay');
    const applySimilarCheckboxes = document.getElementById('applySimilarCheckboxes');
    const cancelApplySimilarBtn = document.getElementById('cancelApplySimilarBtn');
    const confirmApplySimilarBtn = document.getElementById('confirmApplySimilarBtn');

    let flatpickrInstance;
    let weeklyScheduleConfig = {}; // Đối tượng để lưu trữ cấu hình lịch làm việc hàng tuần

    // Khởi tạo Flatpickr
    flatpickrInstance = flatpickr(dateRangePicker, {
        mode: "range",
        dateFormat: "Y-m-d",
        minDate: "today"
    });

    // Chuyển đổi hiển thị của bộ chọn ngày dựa trên lựa chọn radio
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

    // Các ngày trong tuần để lập lịch (khóa phải khớp với backend)
    const daysOfWeekDisplay = ['Chủ Nhật', 'Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7'];
    const dayKeys = ['sunday', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday'];

    /**
     * Tạo HTML cho một chip khung giờ.
     * @param {string} start - Thời gian bắt đầu (HH:mm).
     * @param {string} end - Thời gian kết thúc (HH:mm).
     * @param {string} dayKey - Khóa ngày (ví dụ: 'monday').
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
     * Render (hoặc render lại) các khung giờ cho một ngày cụ thể dựa trên weeklyScheduleConfig.
     * @param {string} dayKey - Khóa ngày cần render.
     */
    function renderDaySlots(dayKey) {
        const container = document.getElementById(`day-${dayKey}-slots`);
        if (!container) {
            console.warn(`Container for day ${dayKey} not found.`); // Thêm log cảnh báo
            return;
        }

        container.innerHTML = ''; // Xóa tất cả các chip hiện có

        const slots = weeklyScheduleConfig[dayKey] || [];
        if (slots.length === 0) {
            container.innerHTML = `<p class="text-muted text-sm" id="no-slots-${dayKey}">Chưa có khung giờ nào.</p>`;
        } else {
            // Sort slots by start time before rendering
            slots.sort((a, b) => {
                const timeA = a.start.split(':').map(Number);
                const timeB = b.start.split(':').map(Number);
                if (timeA[0] !== timeB[0]) return timeA[0] - timeB[0];
                return timeA[1] - timeB[1];
            });
            slots.forEach((slot, index) => {
                container.innerHTML += createSlotChipHTML(slot.start, slot.end, dayKey, index);
            });
        }
        // Sau khi render lại, cần đính kèm lại sự kiện cho các nút remove-slot-btn
        attachRemoveSlotListeners();

        // Cập nhật trạng thái hiển thị của nút sao chép
        // CHỈ cập nhật khi renderSlots được gọi do thêm/xóa slot,
        // Còn khi toggle thay đổi sẽ có hàm riêng điều khiển.
        // updateDuplicateButtonVisibility(dayKey); // BỎ DÒNG NÀY ĐỂ TRÁNH GHI ĐÈ LOGIC CỦA HANDLE-DAY-TOGGLE-CHANGE
    }

    /**
     * Cập nhật trạng thái hiển thị của nút "Thêm" và "Sao chép" cho một ngày cụ thể.
     * Nút "Thêm" và "Sao chép" hiển thị khi toggle của ngày đó được bật.
     * Nút "Sao chép" vẫn sẽ kiểm tra nếu có slot trước khi cho phép sao chép, nhưng nó sẽ hiện.
     * @param {string} dayKey - Khóa ngày cần cập nhật.
     * @param {boolean} isChecked - Trạng thái của toggle (true nếu bật).
     */
    function updateAddDuplicateButtonVisibility(dayKey, isChecked) {
        const toggle = document.getElementById(`toggle-${dayKey}`);
        const daySectionDiv = toggle ? toggle.closest('.day-section') : null;
        const addSlotBtn = daySectionDiv ? daySectionDiv.querySelector('.add-slot-btn') : null;
        const duplicateSlotBtn = daySectionDiv ? daySectionDiv.querySelector('.duplicate-slot-btn') : null;

        if (addSlotBtn) {
            addSlotBtn.style.display = isChecked ? 'inline-block' : 'none';
        }
        if (duplicateSlotBtn) {
            // Nút sao chép sẽ hiện ngay khi toggle bật, bất kể có slot hay không.
            // Logic kiểm tra có slot sẽ nằm trong handleDuplicateSlotClick
            duplicateSlotBtn.style.display = isChecked ? 'inline-block' : 'none';
        }
    }


    /**
     * Tạo HTML chính cho một ngày trong lịch làm việc hàng tuần (toggle, nút thêm/sao chép).
     * Hàm này trả về một phần tử DOM, không phải chuỗi HTML.
     * @param {number} dayIndex - Chỉ số ngày trong tuần (0-6).
     * @returns {HTMLElement} Phần tử div đại diện cho một ngày.
     */
    function createDaySectionElement(dayIndex) {
        const dayKey = dayKeys[dayIndex];
        const isChecked = weeklyScheduleConfig[dayKey] && weeklyScheduleConfig[dayKey].length > 0;

        // Các nút "Thêm" và "Sao chép" ban đầu sẽ ẩn,
        // sau đó được điều khiển bởi handleDayToggleChange khi populateWeeklySchedules hoàn tất.
        const initialButtonDisplay = 'none';
        const containerDisplayStyle = isChecked ? 'block' : 'none';


        const dayDiv = document.createElement('div');
        dayDiv.className = 'day-section'; // Thêm class để dễ quản lý

        dayDiv.innerHTML = `
            <div class="d-flex align-items-center mb-3 border-bottom pb-2">
                <div class="form-check form-switch me-3">
                    <input class="form-check-input day-toggle" type="checkbox" id="toggle-${dayKey}" data-day-key="${dayKey}" ${isChecked ? 'checked' : ''}>
                    <label class="form-check-label fw-bold" for="toggle-${dayKey}">
                        ${daysOfWeekDisplay[dayIndex]}
                    </label>
                </div>
                <div class="ms-auto btn-group" role="group">
                    <button type="button" class="btn btn-sm btn-outline-success add-slot-btn me-2" data-day-key="${dayKey}" style="display: ${initialButtonDisplay};">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-plus" viewBox="0 0 16 16">
                            <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/>
                        </svg>
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-info duplicate-slot-btn" data-day-key="${dayKey}" style="display: ${initialButtonDisplay};">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-copy" viewBox="0 0 16 16">
                            <path fill-rule="evenodd" d="M4 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V2zM6 2a1 1 0 0 0-1 1v8a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V3a1 1 0 0 0-1-1H6z"/>
                            <path fill-rule="evenodd" d="M2 5a1 1 0 0 0-1 1v8a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1v-1h1v1a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h1v1H2z"/>
                        </svg>
                    </button>
                </div>
            </div>
            <div id="day-${dayKey}-slots" class="mb-3 ms-4" style="display: ${containerDisplayStyle};">
            </div>
        `;
        return dayDiv;
    }

    /**
     * Khởi tạo hoặc cập nhật toàn bộ giao diện lịch làm việc hàng tuần.
     * @param {Object} config - Cấu hình lịch làm việc (tùy chọn, dùng để tải dữ liệu ban đầu).
     */
    function populateWeeklySchedules(config = {}) {
        weeklyScheduleConfig = config;
        weeklyScheduleContainer.innerHTML = ''; // Xóa sạch nội dung cũ

        const orderedDayIndices = [1, 2, 3, 4, 5, 6, 0];
        const fragment = document.createDocumentFragment(); // Sử dụng DocumentFragment để tối ưu hiệu suất DOM

        orderedDayIndices.forEach(index => {
            fragment.appendChild(createDaySectionElement(index));
        });
        weeklyScheduleContainer.appendChild(fragment); // Thêm tất cả các ngày vào DOM một lần

        // Sau khi tất cả HTML đã được thêm vào DOM, render các slot
        orderedDayIndices.forEach(index => {
            const dayKey = dayKeys[index];
            renderDaySlots(dayKey);
            // Sau khi render slots, kiểm tra trạng thái của toggle và cập nhật nút
            const toggle = document.getElementById(`toggle-${dayKey}`);
            if (toggle) {
                updateAddDuplicateButtonVisibility(dayKey, toggle.checked);
            }
        });

        // Đính kèm tất cả listeners sau khi DOM được cập nhật và sẵn sàng
        attachAllListeners();
    }

    /**
     * Đính kèm tất cả các trình nghe sự kiện động.
     */
    function attachAllListeners() {
        // Loại bỏ listener cũ để tránh trùng lặp
        document.querySelectorAll('.day-toggle').forEach(toggle => {
            toggle.removeEventListener('change', handleDayToggleChange);
            toggle.addEventListener('change', handleDayToggleChange);
        });
        document.querySelectorAll('.add-slot-btn').forEach(btn => {
            btn.removeEventListener('click', handleAddSlotClick);
            btn.addEventListener('click', handleAddSlotClick); // e.currentTarget sẽ là nút này
        });
        document.querySelectorAll('.duplicate-slot-btn').forEach(btn => {
            btn.removeEventListener('click', handleDuplicateSlotClick);
            btn.addEventListener('click', handleDuplicateSlotClick);
        });

        // Sử dụng event delegation cho nút remove-slot-btn vì chúng được tạo động
        attachRemoveSlotListeners();

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

    // Tách riêng hàm đính kèm sự kiện remove slot
    function attachRemoveSlotListeners() {
        // Loại bỏ trình nghe cũ để tránh trùng lặp nếu render lại
        weeklyScheduleContainer.removeEventListener('click', handleRemoveSlotClick);
        weeklyScheduleContainer.addEventListener('click', handleRemoveSlotClick);
    }

    function handleDayToggleChange(e) {
        const dayKey = e.target.dataset.dayKey;
        const container = document.getElementById(`day-${dayKey}-slots`);
        // const parentDiv = e.target.closest('.d-flex'); // Không cần truy vấn lại parentDiv ở đây
        // const addSlotBtn = parentDiv ? parentDiv.querySelector('.add-slot-btn') : null;
        // const duplicateSlotBtn = parentDiv ? parentDiv.querySelector('.duplicate-slot-btn') : null;

        updateAddDuplicateButtonVisibility(dayKey, e.target.checked); // Gọi hàm mới để cập nhật hiển thị nút

        if (e.target.checked) {
            if (container) container.style.display = 'block';
            // Khi bật toggle, nếu chưa có slot nào, hiển thị thông báo "Chưa có khung giờ nào."
            if (!(weeklyScheduleConfig[dayKey] && weeklyScheduleConfig[dayKey].length > 0)) {
                if (container) container.innerHTML = `<p class="text-muted text-sm" id="no-slots-${dayKey}">Chưa có khung giờ nào.</p>`;
            } else {
                renderDaySlots(dayKey); // Render lại các slot nếu có
            }
        } else {
            if (container) container.style.display = 'none';
            weeklyScheduleConfig[dayKey] = []; // Xóa tất cả slot khi tắt toggle
            renderDaySlots(dayKey); // Render lại để hiển thị "Chưa có khung giờ nào."
        }
    }

    function handleAddSlotClick(e) {
        const dayKey = e.currentTarget.dataset.dayKey;
        const duration = parseInt(appointmentDurationSelect.value);
        if (isNaN(duration) || duration <= 0) {
            displayModalMessage('danger', 'Vui lòng chọn thời gian cuộc hẹn mặc định hợp lệ.');
            return;
        }

        let newSlots = [];

        // Đảm bảo LocalTime và DateTimeFormatter được định nghĩa hoặc polyfill
        if (typeof LocalTime === 'undefined' || typeof DateTimeFormatter === 'undefined') {
            displayModalMessage('danger', 'Lỗi nội bộ: Không tìm thấy định nghĩa LocalTime hoặc DateTimeFormatter.');
            console.error('LocalTime or DateTimeFormatter is not defined.');
            return;
        }

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

        const toggle = document.getElementById(`toggle-${dayKey}`);
        if (toggle) {
            if (!toggle.checked) {
                toggle.checked = true;
                // Khi bật toggle từ đây, cập nhật lại trạng thái nút
                updateAddDuplicateButtonVisibility(dayKey, true);
            }
            renderDaySlots(dayKey); // Render các slot sau khi đảm bảo toggle đã bật và container hiển thị
            updateAddDuplicateButtonVisibility(dayKey, toggle.checked); // Cập nhật lại hiển thị nút sau khi render slot
        } else {
            console.error(`Error: Toggle for day ${dayKey} not found in DOM.`);
            displayModalMessage('danger', `Lỗi: Không tìm thấy điều khiển cho ngày ${daysOfWeekDisplay[dayKeys.indexOf(dayKey)]}. Vui lòng thử lại.`);
        }
    }

    function handleRemoveSlotClick(e) {
        // Sử dụng closest để xác định xem click có phải trên nút xóa hoặc con của nó không
        if (e.target.classList.contains('remove-slot-btn') || e.target.closest('.remove-slot-btn')) {
            const chip = e.target.closest('.slot-chip');
            if (chip) {
                const dayKey = chip.dataset.dayKey;
                const slotIndex = parseInt(chip.dataset.slotIndex);

                if (weeklyScheduleConfig[dayKey] && weeklyScheduleConfig[dayKey].length > slotIndex) {
                    weeklyScheduleConfig[dayKey].splice(slotIndex, 1);
                    renderDaySlots(dayKey); // Render lại để cập nhật index và HTML
                    const toggle = document.getElementById(`toggle-${dayKey}`);
                    if (toggle) {
                        updateAddDuplicateButtonVisibility(dayKey, toggle.checked); // Cập nhật lại hiển thị nút
                        // Nếu sau khi xóa hết slot mà toggle vẫn bật, giữ nút add/duplicate hiện
                        if (weeklyScheduleConfig[dayKey].length === 0) {
                            // Không làm gì đặc biệt ở đây, vì updateAddDuplicateButtonVisibility đã xử lý.
                            // Container sẽ hiển thị "Chưa có khung giờ nào." do renderDaySlots.
                        }
                    }
                }
            }
        }
    }

    let sourceDayForDuplication = null;

    // Các hàm cho overlay "Áp dụng tương tự" (đã được hoàn thiện)
    function handleDuplicateSlotClick(e) {
        sourceDayForDuplication = e.currentTarget.dataset.dayKey; // Sử dụng currentTarget

        const sourceDayDisplay = daysOfWeekDisplay[dayKeys.indexOf(sourceDayForDuplication)];

        // Kiểm tra xem ngày nguồn có khung giờ nào không TRƯỚC KHI hiển thị popup
        if (!weeklyScheduleConfig[sourceDayForDuplication] || weeklyScheduleConfig[sourceDayForDuplication].length === 0) {
            displayModalMessage('info', `Không có khung giờ nào để sao chép từ ${sourceDayDisplay}. Vui lòng thêm khung giờ trước.`);
            sourceDayForDuplication = null; // Đặt lại
            return;
        }

        // Đặt vị trí overlay gần nút đã click
        const button = e.currentTarget; // Nút đã click
        const buttonRect = button.getBoundingClientRect();
        const modalContent = document.querySelector('.modal-content'); // Lấy modal content để tính offset
        const modalContentRect = modalContent.getBoundingClientRect();

        // Tính toán vị trí tương đối với modal content
        const topOffset = buttonRect.bottom - modalContentRect.top + 5; // 5px dưới nút
        const leftOffset = buttonRect.left - modalContentRect.left;

        applySimilarOverlay.style.position = 'absolute'; // Đảm bảo vị trí tuyệt đối
        applySimilarOverlay.style.top = `${topOffset}px`;
        applySimilarOverlay.style.left = `${leftOffset}px`;
        applySimilarOverlay.style.right = 'auto'; // Đảm bảo không bị ảnh hưởng bởi right cũ

        // Tạm thời hiện overlay để lấy kích thước chính xác
        applySimilarOverlay.classList.remove('d-none');
        const overlayWidth = applySimilarOverlay.offsetWidth;
        const modalContentWidth = modalContentRect.width;

        // Điều chỉnh vị trí nếu popup vượt ra ngoài biên phải của modal
        if (leftOffset + overlayWidth > modalContentWidth - 20) { // 20px padding từ cạnh phải modal
            applySimilarOverlay.style.left = `${modalContentWidth - overlayWidth - 20}px`;
        }


        // Đổ dữ liệu checkboxes vào overlay
        applySimilarCheckboxes.innerHTML = '';

        // Thêm checkbox "Chọn tất cả"
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

        // Thêm trình nghe cho "Chọn tất cả"
        if (selectAllCheckbox) { // Kiểm tra để đảm bảo nó tồn tại
            selectAllCheckbox.removeEventListener('change', handleSelectAllChange); // Loại bỏ listener cũ
            selectAllCheckbox.addEventListener('change', handleSelectAllChange);
        }

        // overlay đã được hiện ở trên để tính toán kích thước, giờ thì chỉ cần để nó hiển thị
        // applySimilarOverlay.classList.remove('d-none');
    }

    function handleSelectAllChange(event) {
        document.querySelectorAll('.apply-to-day-checkbox').forEach(checkbox => {
            checkbox.checked = event.target.checked;
        });
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

        // Sao chép sâu mảng slot nguồn để tránh tham chiếu đến cùng một đối tượng
        // Đảm bảo rằng sourceSlots không bao giờ là undefined hoặc null
        // Kiểm tra lại tính nguyên bản của mảng khi sao chép
        const sourceSlots = weeklyScheduleConfig[sourceDayForDuplication] ?
            JSON.parse(JSON.stringify(weeklyScheduleConfig[sourceDayForDuplication])) : [];

        // Nếu ngày nguồn không có slot nào thì không sao chép gì cả
        if (sourceSlots.length === 0) {
             displayModalMessage('info', `Ngày nguồn (${daysOfWeekDisplay[dayKeys.indexOf(sourceDayForDuplication)]}) không có khung giờ nào để sao chép.`);
             hideApplySimilarOverlay();
             return;
        }


        selectedTargetDayKeys.forEach(targetDayKey => {
            weeklyScheduleConfig[targetDayKey] = sourceSlots; // Gán bản sao

            // Đảm bảo toggle của ngày đích được bật và render lại giao diện
            const targetToggle = document.getElementById(`toggle-${targetDayKey}`);
            if (targetToggle) { // Đảm bảo toggle tồn tại
                if (!targetToggle.checked) {
                    targetToggle.checked = true;
                    // Khi bật toggle từ việc sao chép, cần hiển thị container và nút
                    updateAddDuplicateButtonVisibility(targetDayKey, true);
                }
                renderDaySlots(targetDayKey); // Render lại các slot đã sao chép
                // updateAddDuplicateButtonVisibility(targetDayKey, targetToggle.checked); // Đã gọi ở trên
            }
        });

        displayModalMessage('success', `Đã sao chép lịch thành công từ ${daysOfWeekDisplay[dayKeys.indexOf(sourceDayForDuplication)]} sang các ngày đã chọn.`);
        hideApplySimilarOverlay();
    }

    function hideApplySimilarOverlay() {
        applySimilarOverlay.classList.add('d-none');
        sourceDayForDuplication = null; // Đặt lại biến nguồn
    }

    // Trình nghe sự kiện cho các nút "Chỉnh sửa lịch" trong danh sách bác sĩ
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

            weeklyScheduleConfig = {}; // Reset config khi mở modal mới

            // Đảm bảo populateWeeklySchedules hoàn thành việc tạo DOM trước khi fetch dữ liệu
            populateWeeklySchedules(weeklyScheduleConfig); // Khởi tạo giao diện với config rỗng

            modalMessageContainer.innerHTML = ''; // Xóa bất kỳ thông báo lỗi nào
            hideApplySimilarOverlay(); // Đảm bảo overlay ẩn

            // Bây giờ mới fetch dữ liệu, sau khi DOM đã được tạo
            await fetchAndPopulateDoctorSchedule(currentEditingDoctorId);

            editScheduleModal.show();
        });
    });

    // Hàm để lấy và điền dữ liệu lịch trình bác sĩ hiện có
    async function fetchAndPopulateDoctorSchedule(doctorId) {
        modalLoadingSpinner.classList.remove('d-none');
        saveScheduleBtn.disabled = true;

        try {
            const response = await fetch(`${contextPath}/doctor/schedule/detailed?doctorAccountId=${modalDoctorAccountId.value}`);

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

            const detailedScheduleConfigData = responseData.detailedScheduleConfig || {};

            // Cập nhật các trường form
            appointmentDurationSelect.value = detailedScheduleConfigData.appointment_duration || "30";

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

            // Điền lịch trình hàng tuần
            // Đảm bảo rằng weekly_schedule luôn là một đối tượng, không phải null
            populateWeeklySchedules(detailedScheduleConfigData.weekly_schedule || {});

        } catch (error) {
            console.error("Lỗi khi tải lịch làm việc chi tiết:", error);
            displayModalMessage('danger', `Không thể tải lịch làm việc chi tiết: ${error.message}. Vui lòng thử lại.`);
            populateWeeklySchedules({}); // Khôi phục về trạng thái rỗng nếu có lỗi
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

    // Xử lý gửi biểu mẫu bên trong modal
    modalScheduleForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        modalMessageContainer.innerHTML = '';

        saveScheduleBtn.disabled = true;
        modalLoadingSpinner.classList.remove('d-none');

        const formData = new FormData(modalScheduleForm);
        const dataToSend = {
            doctor_account_id: formData.get('doctor_account_id'),
            appointment_duration: formData.get('appointment_duration'),
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
            // Chỉ gửi dữ liệu cho các ngày được bật toggle VÀ có slot
            if (toggle && toggle.checked) {
                const container = document.getElementById(`day-${dayKey}-slots`);
                const slots = [];
                // Sử dụng querySelectorAll trực tiếp trên container để lấy các chip
                if (container) {
                    container.querySelectorAll('.slot-chip').forEach(slotDiv => {
                        const start = slotDiv.dataset.start;
                        const end = slotDiv.dataset.end;
                        if (start && end) {
                            slots.push({
                                start: start,
                                end: end
                            });
                        }
                    });
                }
                dataToSend.weekly_schedule[dayKey] = slots;
            } else {
                dataToSend.weekly_schedule[dayKey] = [];
            }
        });

        try {
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