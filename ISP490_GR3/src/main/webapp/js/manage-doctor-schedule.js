// JavaScript for Calendar Logic
const calendarGrid = document.getElementById('calendarGrid');
const currentMonthYearDisplay = document.getElementById('currentMonthYear');
const prevMonthBtn = document.getElementById('prevMonth');
const nextMonthBtn = document.getElementById('nextMonth');
const todayButton = document.getElementById('todayButton');
const addEventButton = document.getElementById('addEventButton');
const viewModeSelect = document.getElementById('viewMode'); // New: View Mode Select

// Modal elements
const scheduleModal = new bootstrap.Modal(document.getElementById('scheduleModal'));
const scheduleModalLabel = document.getElementById('scheduleModalLabel');
const scheduleIdInput = document.getElementById('scheduleId');
const modalWorkDateInput = document.getElementById('modalWorkDate');
const modalDoctorIdSelect = document.getElementById('modalDoctorId');
const modalIsActiveCheckbox = document.getElementById('modalIsActive');
const modalEventNameInput = document.getElementById('modalEventName'); // Input for event name (frontend only)
const saveScheduleBtn = document.getElementById('saveScheduleBtn');
const deleteScheduleBtn = document.getElementById('deleteScheduleBtn');

// Notification Modal elements
const notificationModal = new bootstrap.Modal(document.getElementById('notificationModal'));
const notificationModalLabel = document.getElementById('notificationModalLabel');
const notificationMessage = document.getElementById('notificationMessage');

let currentDate = new Date(); // Current date to track month/year/week/day
let currentEditingScheduleId = null; // To track which schedule is being edited/deleted
let currentViewMode = 'month'; // Default view mode

// IMPORTANT: Define contextPath globally in your JSP file like:
// <script>const contextPath = "<%= request.getContextPath() %>";</script>

// Function to format date to YYYY-MM-DD
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// Function to display a custom notification modal
function showNotification(title, message, isSuccess = true) {
    notificationModalLabel.textContent = title;
    notificationMessage.textContent = message;

    const modalHeader = document.querySelector('#notificationModal .modal-header');
    // Remove previous success/error classes
    modalHeader.classList.remove('success', 'error');

    if (isSuccess) {
        modalHeader.classList.add('success');
    } else {
        modalHeader.classList.add('error');
    }
    notificationModal.show();
}

// Function to get the end date for scheduling based on the specific rule
// - If today is Monday-Thursday: allow scheduling from today until the end of the *current* week (Sunday).
// - If today is Friday, Saturday, Sunday: allow scheduling from today until the end of the *next* week (Sunday).
function getAllowedMaxDate() {
    const today = new Date();
    let limitDate = new Date(today);

    const dayOfWeek = today.getDay(); // 0 = Sunday, 1 = Monday, ..., 5 = Friday, 6 = Saturday

    let endOfCurrentWeek = new Date(today);
    // Go to Sunday of current week (if today is Sunday, it's today)
    endOfCurrentWeek.setDate(today.getDate() + (7 - dayOfWeek) % 7);

    // If today is Friday (5), Saturday (6), or Sunday (0)
    if (dayOfWeek === 5 || dayOfWeek === 6 || dayOfWeek === 0) {
        limitDate = new Date(endOfCurrentWeek);
        limitDate.setDate(limitDate.getDate() + 7); // End of next week
    } else {
        limitDate = endOfCurrentWeek; // End of current week
    }
    return formatDate(limitDate);
}

// Function to populate doctor select dropdown from backend
async function populateDoctorSelect() {
    modalDoctorIdSelect.innerHTML = '<option value="">-- Chọn bác sĩ --</option>';
    try {
        const response = await fetch(`${contextPath}/api/doctors`);
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Không thể tải danh sách bác sĩ.');
        }
        const doctors = await response.json();
        doctors.forEach(doctor => {
            const option = document.createElement('option');
            option.value = doctor.id;
            option.textContent = doctor.fullName;
            modalDoctorIdSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Error fetching doctors:', error);
        showNotification('Lỗi', `Không thể tải danh sách bác sĩ: ${error.message}`, false);
    }
}

// Function to render the calendar based on currentViewMode
async function renderCalendar() {
    calendarGrid.innerHTML = ''; // Clear previous days
    // Remove existing weekday elements that might be dynamically added if changing views
    const existingWeekdays = document.querySelectorAll('.calendar-day-detail-grid .calendar-weekday-name');
    existingWeekdays.forEach(el => el.remove());


    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();
    // For day/week view, we need exact date
    const day = currentDate.getDate();

    let actualEvents = [];
    let fetchUrl = '';
    const weekdaysHeader = document.querySelector('.calendar-weekdays');

    // Adjust UI and fetch URL based on view mode
    if (currentViewMode === 'month') {
        currentMonthYearDisplay.textContent = `Tháng ${month + 1} ${year}`;
        fetchUrl = `${contextPath}/api/doctor-schedules?viewMode=month&year=${year}&month=${month + 1}`;
        calendarGrid.className = 'calendar-day-grid'; // Reset grid class for month view
        if (weekdaysHeader)
            weekdaysHeader.style.display = 'grid'; // Show weekday header
        renderMonthView(year, month);
    } else if (currentViewMode === 'week') {
        // Find the start of the week (Sunday)
        const startOfWeek = new Date(currentDate);
        startOfWeek.setDate(currentDate.getDate() - currentDate.getDay()); // Go to Sunday (0)

        const endOfWeek = new Date(startOfWeek);
        endOfWeek.setDate(startOfWeek.getDate() + 6); // End on Saturday

        currentMonthYearDisplay.textContent = `Tuần ${formatDate(startOfWeek)} - ${formatDate(endOfWeek)}`;
        fetchUrl = `${contextPath}/api/doctor-schedules?viewMode=week&startDate=${formatDate(startOfWeek)}&endDate=${formatDate(endOfWeek)}`;
        calendarGrid.className = 'calendar-week-grid'; // Apply week grid class
        if (weekdaysHeader)
            weekdaysHeader.style.display = 'grid'; // Show weekday header
        renderWeekView(startOfWeek);
    } else if (currentViewMode === 'day') {
        currentMonthYearDisplay.textContent = `Ngày ${formatDate(currentDate)}`;
        fetchUrl = `${contextPath}/api/doctor-schedules?viewMode=day&startDate=${formatDate(currentDate)}&endDate=${formatDate(currentDate)}`;
        calendarGrid.className = 'calendar-day-detail-grid'; // Apply day grid class
        if (weekdaysHeader)
            weekdaysHeader.style.display = 'none'; // Hide weekday header for single day view
        renderDayView(currentDate);
    }

    try {
        const response = await fetch(fetchUrl);
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Không thể tải lịch làm việc.');
        }
        actualEvents = await response.json();
        populateEventsIntoGrid(actualEvents); // Now, populate events into the rendered grid

    } catch (error) {
        console.error('Error fetching schedules:', error);
        showNotification('Lỗi', `Không thể tải lịch làm việc: ${error.message}`, false);
    }
}

// Function to render month view grid
function renderMonthView(year, month) {
    const firstDayOfMonth = new Date(year, month, 1);
    const lastDayOfMonth = new Date(year, month + 1, 0);

    const startDayIndex = firstDayOfMonth.getDay(); // 0 for Sunday, 1 for Monday...
    const daysInMonth = lastDayOfMonth.getDate();
    const daysInPrevMonth = new Date(year, month, 0).getDate();

    // Days from previous month
    for (let i = 0; i < startDayIndex; i++) {
        const dayElement = document.createElement('div');
        dayElement.classList.add('calendar-day', 'other-month');
        dayElement.innerHTML = `<div class="calendar-day-number">${daysInPrevMonth - startDayIndex + 1 + i}</div>`;
        calendarGrid.appendChild(dayElement);
    }

    // Days of current month
    for (let day = 1; day <= daysInMonth; day++) {
        const dayElement = document.createElement('div');
        dayElement.classList.add('calendar-day', 'current-month');
        dayElement.innerHTML = `<div class="calendar-day-number">${day}</div>`;

        const currentDay = new Date(year, month, day);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (currentDay.getTime() === today.getTime()) {
            dayElement.classList.add('today');
        }

        dayElement.dataset.fullDate = formatDate(currentDay); // Store full date for event lookup
        dayElement.addEventListener('click', (e) => handleDayClick(e, currentDay));
        calendarGrid.appendChild(dayElement);
    }

    // Days from next month (fill up to 42 cells)
    const currentRenderedCells = calendarGrid.children.length;
    const remainingCellsToFill = 42 - currentRenderedCells; // Ensure 6 rows total

    for (let i = 1; i <= remainingCellsToFill; i++) {
        const dayElement = document.createElement('div');
        dayElement.classList.add('calendar-day', 'other-month');
        dayElement.innerHTML = `<div class="calendar-day-number">${i}</div>`;
        calendarGrid.appendChild(dayElement);
    }
}

// Function to render week view grid
function renderWeekView(startOfWeek) {
    for (let i = 0; i < 7; i++) {
        const day = new Date(startOfWeek);
        day.setDate(startOfWeek.getDate() + i);

        const dayElement = document.createElement('div');
        dayElement.classList.add('calendar-day', 'week-view-day');
        dayElement.innerHTML = `
            <div class="calendar-weekday-name">${['Chủ Nhật', 'Thứ Hai', 'Thứ Ba', 'Thứ Tư', 'Thứ Năm', 'Thứ Sáu', 'Thứ Bảy'][day.getDay()]}</div>
            <div class="calendar-day-number">${day.getDate()}</div>
            <div class="calendar-events-placeholder"></div> `;

        const today = new Date();
        today.setHours(0, 0, 0, 0);
        if (day.getTime() === today.getTime()) {
            dayElement.classList.add('today');
        }

        dayElement.dataset.fullDate = formatDate(day);
        dayElement.addEventListener('click', (e) => handleDayClick(e, day));
        calendarGrid.appendChild(dayElement);
    }
}

// Function to render day view grid
function renderDayView(selectedDate) {
    const dayElement = document.createElement('div');
    dayElement.classList.add('calendar-day', 'day-view-single-day');
    dayElement.innerHTML = `
        <div class="calendar-weekday-name">${['Chủ Nhật', 'Thứ Hai', 'Thứ Ba', 'Thứ Tư', 'Thứ Năm', 'Thứ Sáu', 'Thứ Bảy'][selectedDate.getDay()]}</div>
        <div class="calendar-day-number">${selectedDate.getDate()}</div>
        <div class="calendar-events-placeholder"></div> `;

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    if (selectedDate.getTime() === today.getTime()) {
        dayElement.classList.add('today');
    }

    dayElement.dataset.fullDate = formatDate(selectedDate);
    dayElement.addEventListener('click', (e) => handleDayClick(e, selectedDate)); // Allow adding event for current day
    calendarGrid.appendChild(dayElement);
}

// Populate events into the correct day elements after fetching
function populateEventsIntoGrid(events) {
    events.forEach(event => {
        const eventDate = new Date(event.workDate);
        const eventDateString = formatDate(eventDate);
        // Find the specific day element using its data-full-date attribute
        const dayElement = document.querySelector(`.calendar-day[data-full-date="${eventDateString}"]`);

        if (dayElement) {
            let eventsContainer = dayElement.querySelector('.calendar-events');
            if (!eventsContainer) {
                // For week/day views, we added a placeholder, replace it.
                // For month view, create it if it doesn't exist.
                const placeholder = dayElement.querySelector('.calendar-events-placeholder');
                if (placeholder) {
                    placeholder.remove();
                }
                eventsContainer = document.createElement('div');
                eventsContainer.classList.add('calendar-events');
                dayElement.appendChild(eventsContainer);
            }

            const eventDiv = document.createElement('div');
            eventDiv.classList.add('event', event.type || 'meeting'); // Use 'meeting' as default type
            // Always construct event name from doctorName and workDate as eventName is not from DB
            eventDiv.textContent = `Lịch BS ${event.doctorName} (${formatDate(new Date(event.workDate))})`;
            eventDiv.dataset.scheduleId = event.id;

            eventDiv.addEventListener('click', async (e) => {
                e.stopPropagation(); // Prevent day click from firing

                try {
                    const response = await fetch(`${contextPath}/api/doctor-schedules/${event.id}`);
                    if (!response.ok) {
                        const errorData = await response.json();
                        throw new Error(errorData.message || 'Không thể tải chi tiết lịch.');
                    }
                    const scheduleDetails = await response.json();

                    currentEditingScheduleId = scheduleDetails.id;
                    scheduleModalLabel.textContent = 'Sửa/Xóa Lịch Làm Việc';
                    modalWorkDateInput.value = formatDate(new Date(scheduleDetails.workDate));
                    modalDoctorIdSelect.value = scheduleDetails.doctorId;
                    modalIsActiveCheckbox.checked = scheduleDetails.active;
                    // eventName is not from DB, so construct for display in modal
                    modalEventNameInput.value = `Lịch BS ${scheduleDetails.doctorName} (${formatDate(new Date(scheduleDetails.workDate))})`;
                    saveScheduleBtn.textContent = 'Cập nhật Lịch';
                    deleteScheduleBtn.style.display = 'inline-block';

                    const today = new Date();
                    modalWorkDateInput.min = formatDate(today);
                    modalWorkDateInput.max = getAllowedMaxDate();

                    scheduleModal.show();
                } catch (error) {
                    console.error('Error fetching schedule details:', error);
                    showNotification('Lỗi', `Không thể tải chi tiết lịch: ${error.message}`, false);
                }
            });
            eventsContainer.appendChild(eventDiv);
        }
    });
}

// Handler for clicking on a day (to add new schedule)
function handleDayClick(e, clickedDate) {
    // Only allow clicking on current-month days (for month view)
    // For week/day view, all displayed days are "clickable"
    if (currentViewMode === 'month' && e.currentTarget.classList.contains('other-month')) {
        return;
    }

    currentEditingScheduleId = null;
    scheduleModalLabel.textContent = 'Thêm Lịch Làm Việc';
    modalWorkDateInput.value = formatDate(clickedDate); // Pre-fill with clicked date
    modalDoctorIdSelect.value = '';
    modalIsActiveCheckbox.checked = true;
    modalEventNameInput.value = ''; // Event name is not saved, so clear for new entry
    saveScheduleBtn.textContent = 'Lưu Lịch';
    deleteScheduleBtn.style.display = 'none';

    const today = new Date();
    modalWorkDateInput.min = formatDate(today);
    modalWorkDateInput.max = getAllowedMaxDate();

    scheduleModal.show();
}


// Event Listeners
prevMonthBtn.addEventListener('click', () => {
    if (currentViewMode === 'month') {
        currentDate.setMonth(currentDate.getMonth() - 1);
    } else if (currentViewMode === 'week') {
        currentDate.setDate(currentDate.getDate() - 7);
    } else if (currentViewMode === 'day') {
        currentDate.setDate(currentDate.getDate() - 1);
    }
    renderCalendar();
});

nextMonthBtn.addEventListener('click', () => {
    if (currentViewMode === 'month') {
        currentDate.setMonth(currentDate.getMonth() + 1);
    } else if (currentViewMode === 'week') {
        currentDate.setDate(currentDate.getDate() + 7);
    } else if (currentViewMode === 'day') {
        currentDate.setDate(currentDate.getDate() + 1);
    }
    renderCalendar();
});

todayButton.addEventListener('click', () => {
    currentDate = new Date(); // Reset to today's date
    renderCalendar();
});

addEventButton.addEventListener('click', () => {
    currentEditingScheduleId = null;
    scheduleModalLabel.textContent = 'Thêm Lịch Làm Việc';
    // Pre-fill date with current selected date/viewed date
    modalWorkDateInput.value = formatDate(currentDate);
    modalDoctorIdSelect.value = '';
    modalIsActiveCheckbox.checked = true;
    modalEventNameInput.value = ''; // Clear for new schedule, as it's not saved
    saveScheduleBtn.textContent = 'Lưu Lịch';
    deleteScheduleBtn.style.display = 'none';

    const today = new Date();
    modalWorkDateInput.min = formatDate(today);
    modalWorkDateInput.max = getAllowedMaxDate();

    scheduleModal.show();
});

// Event listener for view mode selection
viewModeSelect.addEventListener('change', (e) => {
    currentViewMode = e.target.value;
    renderCalendar(); // Re-render calendar with new view mode
});

saveScheduleBtn.addEventListener('click', async () => {
    const doctorId = modalDoctorIdSelect.value;
    const workDate = modalWorkDateInput.value;
    const isActive = modalIsActiveCheckbox.checked;
    // eventName is only for frontend display, not sent to backend.
    // const doctorName = modalDoctorIdSelect.options[modalDoctorIdSelect.selectedIndex].text; // Not strictly needed here
    // const eventName = modalEventNameInput.value || `Lịch BS ${doctorName} (${workDate})`; // Not sent to backend

    if (!doctorId || !workDate) {
        showNotification('Lỗi', 'Vui lòng chọn bác sĩ và ngày làm việc.', false);
        return;
    }

    const scheduleData = {
        doctorId: parseInt(doctorId),
        workDate: workDate,
        isActive: isActive
                // eventName is NOT included here as it's not saved to DB
    };

    try {
        let response;
        if (currentEditingScheduleId) {
            response = await fetch(`${contextPath}/api/doctor-schedules/${currentEditingScheduleId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(scheduleData)
            });
        } else {
            response = await fetch(`${contextPath}/api/doctor-schedules`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(scheduleData)
            });
        }

        const responseData = await response.json();

        if (response.ok) {
            showNotification('Thành công', responseData.message || 'Lịch làm việc đã được lưu thành công!', true);
            scheduleModal.hide();
            renderCalendar(); // Re-render to show updated/new schedule
        } else {
            showNotification('Lỗi', `Có lỗi xảy ra khi lưu lịch: ${responseData.message || 'Lỗi không xác định.'}`, false);
        }
    } catch (error) {
        console.error('Error saving schedule:', error);
        showNotification('Lỗi', `Lỗi kết nối hoặc xử lý dữ liệu: ${error.message}`, false);
    }
});

deleteScheduleBtn.addEventListener('click', async () => {
    // Use custom confirmation modal instead of browser's confirm()
    const confirmationModalInstance = new bootstrap.Modal(document.getElementById('confirmationModal'));
    document.getElementById('confirmationMessage').textContent = 'Bạn có chắc chắn muốn xóa lịch làm việc này không?';
    confirmationModalInstance.show();

    document.getElementById('confirmDeleteBtn').onclick = async () => {
        confirmationModalInstance.hide(); // Hide confirmation modal

        if (currentEditingScheduleId) {
            try {
                const response = await fetch(`${contextPath}/api/doctor-schedules/${currentEditingScheduleId}`, {
                    method: 'DELETE'
                });

                const responseData = await response.json();

                if (response.ok) {
                    showNotification('Thành công', responseData.message || 'Lịch làm việc đã được xóa thành công!', true);
                    scheduleModal.hide(); // Hide main schedule modal
                    renderCalendar(); // Re-render to reflect deletion
                } else {
                    showNotification('Lỗi', `Có lỗi xảy ra khi xóa lịch: ${responseData.message || 'Lỗi không xác định.'}`, false);
                }
            } catch (error) {
                console.error('Error deleting schedule:', error);
                showNotification('Lỗi', `Lỗi kết nối hoặc xử lý dữ liệu: ${error.message}`, false);
            }
        }
    };
});


// Initial render when the window loads
window.onload = function () {
    populateDoctorSelect(); // Populate the doctor dropdown
    renderCalendar(); // Render the calendar initially (default month view)
};