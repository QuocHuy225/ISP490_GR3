// JavaScript for Calendar Logic
const calendarGrid = document.getElementById('calendarGrid');
const currentMonthYearDisplay = document.getElementById('currentMonthYear');
const prevMonthBtn = document.getElementById('prevMonth');
const nextMonthBtn = document.getElementById('nextMonth');
const todayButton = document.getElementById('todayButton');
const addEventButton = document.getElementById('addEventButton');

// Modal elements
const scheduleModal = new bootstrap.Modal(document.getElementById('scheduleModal'));
const scheduleModalLabel = document.getElementById('scheduleModalLabel');
const scheduleIdInput = document.getElementById('scheduleId');
const modalWorkDateInput = document.getElementById('modalWorkDate');
const modalDoctorIdSelect = document.getElementById('modalDoctorId');
const modalIsActiveCheckbox = document.getElementById('modalIsActive');
const modalEventNameInput = document.getElementById('modalEventName'); // New input for event name
const saveScheduleBtn = document.getElementById('saveScheduleBtn');
const deleteScheduleBtn = document.getElementById('deleteScheduleBtn');

// Notification Modal elements
const notificationModal = new bootstrap.Modal(document.getElementById('notificationModal'));
const notificationModalLabel = document.getElementById('notificationModalLabel');
const notificationMessage = document.getElementById('notificationMessage');

let currentDate = new Date(); // Current date to track month/year
let currentEditingScheduleId = null; // To track which schedule is being edited/deleted

// IMPORTANT: Define contextPath globally in your JSP file like:
// <script>const contextPath = "<%= request.getContextPath() %>";</script>
// right before including this script.js file.

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

    // Optionally change modal header/body style based on success/failure
    const modalHeader = document.querySelector('#notificationModal .modal-header');
    // const modalBody = document.querySelector('#notificationModal .modal-body'); // Not used for now, but kept for example

    if (isSuccess) {
        modalHeader.style.backgroundColor = '#d4edda'; // Light green
        modalHeader.style.color = '#155724'; // Dark green text
    } else {
        modalHeader.style.backgroundColor = '#f8d7da'; // Light red
        modalHeader.style.color = '#721c24'; // Dark red text
    }
    notificationModal.show();
}

// Function to get the end date for scheduling based on the specific rule
// - If today is Mon-Thu, Sat, Sun: allow scheduling from today until the end of the *current* week (Sunday).
// - If today is Friday: allow scheduling from today until the end of the *next* week (Sunday).
function getAllowedMaxDate() {
    const today = new Date();
    let limitDate = new Date(today); // Initialize with today's date

    const dayOfWeek = today.getDay(); // 0 = Sunday, 1 = Monday, ..., 5 = Friday, 6 = Saturday

    // Calculate the date of the next Sunday (or today if today is Sunday)
    let endOfCurrentWeek = new Date(today);
    endOfCurrentWeek.setDate(today.getDate() + (7 - dayOfWeek) % 7);

    if (dayOfWeek === 5) { // If today is Friday
        // Allowed until the end of the *next* week
        limitDate = new Date(endOfCurrentWeek); // Start from Sunday of current week
        limitDate.setDate(limitDate.getDate() + 7); // Add 7 days to get Sunday of next week
    } else {
        // For Mon-Thu, Sat, Sun: Allowed until the end of the *current* week
        limitDate = endOfCurrentWeek;
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

// Function to render the calendar
async function renderCalendar() {
    calendarGrid.innerHTML = ''; // Clear previous days

    const year = currentDate.getFullYear();
    const month = currentDate.getMonth(); // 0-indexed

    currentMonthYearDisplay.textContent = `Tháng ${month + 1} ${year}`;

    let actualEvents = [];
    try {
        const response = await fetch(`${contextPath}/api/doctor-schedules?year=${year}&month=${month + 1}`);
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Không thể tải lịch làm việc.');
        }
        actualEvents = await response.json();
    } catch (error) {
        console.error('Error fetching schedules:', error);
        showNotification('Lỗi', `Không thể tải lịch làm việc: ${error.message}`, false);
    }

    const firstDayOfMonth = new Date(year, month, 1);
    const lastDayOfMonth = new Date(year, month + 1, 0);

    const startDayIndex = firstDayOfMonth.getDay();

    const daysInMonth = lastDayOfMonth.getDate();

    const daysInPrevMonth = new Date(year, month, 0).getDate();

    for (let i = 0; i < startDayIndex; i++) {
        const dayElement = document.createElement('div');
        dayElement.classList.add('calendar-day', 'other-month');
        dayElement.innerHTML = `<div class="calendar-day-number">${daysInPrevMonth - startDayIndex + 1 + i}</div>`;
        calendarGrid.appendChild(dayElement);
    }

    for (let day = 1; day <= daysInMonth; day++) {
        const dayElement = document.createElement('div');
        dayElement.classList.add('calendar-day', 'current-month');
        dayElement.innerHTML = `<div class="calendar-day-number">${day}</div>`;

        const currentDay = new Date(year, month, day);
        const today = new Date();
        today.setHours(0, 0, 0, 0); // Normalize today's date for comparison

        if (currentDay.getTime() === today.getTime()) {
            dayElement.classList.add('today');
        }

        // Add click listener to each day (for adding new schedule)
        dayElement.addEventListener('click', () => {
            if (!dayElement.classList.contains('other-month')) {
                currentEditingScheduleId = null;
                scheduleModalLabel.textContent = 'Thêm Lịch Làm Việc';
                modalWorkDateInput.value = formatDate(currentDay);
                modalDoctorIdSelect.value = '';
                modalIsActiveCheckbox.checked = true;
                modalEventNameInput.value = '';
                saveScheduleBtn.textContent = 'Lưu Lịch';
                deleteScheduleBtn.style.display = 'none';

                // Set min/max dates for the date input
                modalWorkDateInput.min = formatDate(today); // Cannot select past dates
                modalWorkDateInput.max = getAllowedMaxDate(); // Apply dynamic max date

                scheduleModal.show();
            }
        });

        // Add events to the day
        const dayEvents = actualEvents.filter(event => {
            const eventDate = new Date(event.workDate);
            return eventDate.getFullYear() === year &&
                   eventDate.getMonth() === month &&
                   eventDate.getDate() === day;
        });

        if (dayEvents.length > 0) {
            const eventsContainer = document.createElement('div');
            eventsContainer.classList.add('calendar-events');
            dayEvents.forEach(event => {
                const eventDiv = document.createElement('div');
                eventDiv.classList.add('event', event.type || 'meeting');
                eventDiv.textContent = event.name || `Lịch BS ${event.doctorName}`;
                eventDiv.dataset.scheduleId = event.id;

                // Add click listener to each event (for editing/deleting existing schedule)
                eventDiv.addEventListener('click', async (e) => {
                    e.stopPropagation();

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
                        modalEventNameInput.value = scheduleDetails.name;
                        saveScheduleBtn.textContent = 'Cập nhật Lịch';
                        deleteScheduleBtn.style.display = 'inline-block';

                        // Set min/max dates for the date input (same rules apply for updates)
                        modalWorkDateInput.min = formatDate(today);
                        modalWorkDateInput.max = getAllowedMaxDate();

                        scheduleModal.show();
                    } catch (error) {
                        console.error('Error fetching schedule details:', error);
                        showNotification('Lỗi', `Không thể tải chi tiết lịch: ${error.message}`, false);
                    }
                });
                eventsContainer.appendChild(eventDiv);
            });
            dayElement.appendChild(eventsContainer);
        }

        calendarGrid.appendChild(dayElement);
    }

    const currentRenderedCells = calendarGrid.children.length;
    const remainingCellsToFill = 42 - currentRenderedCells;

    for (let i = 1; i <= remainingCellsToFill; i++) {
        const dayElement = document.createElement('div');
        dayElement.classList.add('calendar-day', 'other-month');
        dayElement.innerHTML = `<div class="calendar-day-number">${i}</div>`;
        calendarGrid.appendChild(dayElement);
    }
}

// Event Listeners
prevMonthBtn.addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar();
});

nextMonthBtn.addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar();
});

todayButton.addEventListener('click', () => {
    currentDate = new Date(); // Reset to today's date
    renderCalendar();
});

addEventButton.addEventListener('click', () => {
    currentEditingScheduleId = null;
    scheduleModalLabel.textContent = 'Thêm Lịch Làm Việc';
    modalWorkDateInput.value = '';
    modalDoctorIdSelect.value = '';
    modalIsActiveCheckbox.checked = true;
    modalEventNameInput.value = '';
    saveScheduleBtn.textContent = 'Lưu Lịch';
    deleteScheduleBtn.style.display = 'none';

    // Set min/max dates for the date input
    const today = new Date();
    modalWorkDateInput.min = formatDate(today);
    modalWorkDateInput.max = getAllowedMaxDate();

    scheduleModal.show();
});

saveScheduleBtn.addEventListener('click', async () => {
    const doctorId = modalDoctorIdSelect.value;
    const workDate = modalWorkDateInput.value;
    const isActive = modalIsActiveCheckbox.checked;
    // Get doctor's full name from selected option's text
    const doctorName = modalDoctorIdSelect.options[modalDoctorIdSelect.selectedIndex].text;
    const eventName = modalEventNameInput.value || `Lịch BS ${doctorName} (${workDate})`;

    if (!doctorId || !workDate) {
        showNotification('Lỗi', 'Vui lòng chọn bác sĩ và ngày làm việc.', false);
        return;
    }

    const scheduleData = {
        doctorId: parseInt(doctorId),
        workDate: workDate,
        isActive: isActive,
        eventName: eventName
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
            renderCalendar();
        } else {
            showNotification('Lỗi', `Có lỗi xảy ra khi lưu lịch: ${responseData.message || 'Lỗi không xác định.'}`, false);
        }
    } catch (error) {
        console.error('Error saving schedule:', error);
        showNotification('Lỗi', `Lỗi kết nối hoặc xử lý dữ liệu: ${error.message}`, false);
    }
});

deleteScheduleBtn.addEventListener('click', async () => {
    if (currentEditingScheduleId) {
        if (confirm('Bạn có chắc chắn muốn xóa lịch làm việc này không?')) {
            try {
                const response = await fetch(`${contextPath}/api/doctor-schedules/${currentEditingScheduleId}`, {
                    method: 'DELETE'
                });

                const responseData = await response.json();

                if (response.ok) {
                    showNotification('Thành công', responseData.message || 'Lịch làm việc đã được xóa thành công!', true);
                    scheduleModal.hide();
                    renderCalendar();
                } else {
                    showNotification('Lỗi', `Có lỗi xảy ra khi xóa lịch: ${responseData.message || 'Lỗi không xác định.'}`, false);
                }
            } catch (error) {
                console.error('Error deleting schedule:', error);
                showNotification('Lỗi', `Lỗi kết nối hoặc xử lý dữ liệu: ${error.message}`, false);
            }
        }
    }
});

// Initial render
window.onload = function() {
    populateDoctorSelect();
    renderCalendar();
};