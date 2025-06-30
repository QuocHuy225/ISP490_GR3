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

let currentDate = new Date(); // Current date to track month/year
let currentEditingScheduleId = null; // To track which schedule is being edited/deleted

// IMPORTANT: Define contextPath globally in your JSP file like:
// <script>const contextPath = "<%= request.getContextPath() %>";</script>
// right before including this script.js file.
// For local testing without a full JSP environment, you might hardcode it:
// const contextPath = "/ISP490_GR3"; // Example: "/YourAppName" or "" if deployed at root

// Function to format date to YYYY-MM-DD
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// Function to populate doctor select dropdown from backend
async function populateDoctorSelect() {
    modalDoctorIdSelect.innerHTML = '<option value="">-- Chọn bác sĩ --</option>';
    try {
        // Use the globally defined contextPath
        const response = await fetch(`${contextPath}/api/doctors`);
        if (!response.ok) {
            throw new Error('Failed to fetch doctors');
        }
        const doctors = await response.json();
        doctors.forEach(doctor => {
            const option = document.createElement('option');
            option.value = doctor.id;
            option.textContent = doctor.fullName; // Use fullName from Doctor model
            modalDoctorIdSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Error fetching doctors:', error);
        // Consider a more user-friendly error display, e.g., a custom modal
        alert('Không thể tải danh sách bác sĩ. Vui lòng thử lại sau.');
    }
}

// Function to render the calendar
async function renderCalendar() {
    calendarGrid.innerHTML = ''; // Clear previous days

    const year = currentDate.getFullYear();
    const month = currentDate.getMonth(); // 0-indexed

    // Set current month and year display
    currentMonthYearDisplay.textContent = `Tháng ${month + 1} ${year}`;

    // Fetch actual events from backend
    let actualEvents = [];
    try {
        // Use the globally defined contextPath
        const response = await fetch(`${contextPath}/api/doctor-schedules?year=${year}&month=${month + 1}`);
        if (!response.ok) {
            throw new Error('Failed to fetch schedules');
        }
        actualEvents = await response.json();
    } catch (error) {
        console.error('Error fetching schedules:', error);
        // Consider a more user-friendly error display
        alert('Không thể tải lịch làm việc. Vui lòng thử lại sau.');
    }

    // Get first day of the month
    const firstDayOfMonth = new Date(year, month, 1);
    // Get last day of the month
    const lastDayOfMonth = new Date(year, month + 1, 0);

    // Get the day of the week for the first day (0=Sunday, 6=Saturday)
    const startDayIndex = firstDayOfMonth.getDay();

    // Calculate number of days in the month
    const daysInMonth = lastDayOfMonth.getDate();

    // Get number of days in the previous month to fill leading empty cells
    const daysInPrevMonth = new Date(year, month, 0).getDate();

    // Fill leading empty cells with days from the previous month
    for (let i = 0; i < startDayIndex; i++) {
        const dayElement = document.createElement('div');
        dayElement.classList.add('calendar-day', 'other-month');
        dayElement.innerHTML = `<div class="calendar-day-number">${daysInPrevMonth - startDayIndex + 1 + i}</div>`;
        calendarGrid.appendChild(dayElement);
    }

    // Fill days of the current month
    for (let day = 1; day <= daysInMonth; day++) {
        const dayElement = document.createElement('div');
        dayElement.classList.add('calendar-day', 'current-month');
        dayElement.innerHTML = `<div class="calendar-day-number">${day}</div>`;

        const currentDay = new Date(year, month, day);
        const today = new Date();
        today.setHours(0, 0, 0, 0); // Normalize today's date

        if (currentDay.getTime() === today.getTime()) {
            dayElement.classList.add('today');
        }

        // Add click listener to each day (for adding new schedule)
        dayElement.addEventListener('click', () => {
            if (!dayElement.classList.contains('other-month')) {
                currentEditingScheduleId = null; // Set to null for new schedule
                scheduleModalLabel.textContent = 'Thêm Lịch Làm Việc';
                modalWorkDateInput.value = formatDate(currentDay);
                modalDoctorIdSelect.value = ''; // Clear selected doctor
                modalIsActiveCheckbox.checked = true; // Default to active
                modalEventNameInput.value = ''; // Clear event name
                saveScheduleBtn.textContent = 'Lưu Lịch';
                deleteScheduleBtn.style.display = 'none'; // Hide delete button for new schedule
                scheduleModal.show();
            }
        });

        // Add events to the day
        const dayEvents = actualEvents.filter(event => {
            const eventDate = new Date(event.workDate); // Use workDate from backend
            return eventDate.getFullYear() === year &&
                   eventDate.getMonth() === month &&
                   eventDate.getDate() === day;
        });

        if (dayEvents.length > 0) {
            const eventsContainer = document.createElement('div');
            eventsContainer.classList.add('calendar-events');
            dayEvents.forEach(event => {
                const eventDiv = document.createElement('div');
                // Use event.name from backend, and a default type or map from backend data
                eventDiv.classList.add('event', event.type || 'meeting'); // Default to 'meeting' if type is not provided
                eventDiv.textContent = event.name || `Lịch BS ${event.doctorName}`; // Use name from backend or construct
                eventDiv.dataset.scheduleId = event.id; // Store ID for editing/deleting

                // Add click listener to each event (for editing/deleting existing schedule)
                eventDiv.addEventListener('click', async (e) => { // Make async to fetch single schedule
                    e.stopPropagation(); // Prevent day click event from firing

                    // Fetch full schedule details for editing
                    try {
                        // Use the globally defined contextPath
                        const response = await fetch(`${contextPath}/api/doctor-schedules/${event.id}`);
                        if (!response.ok) {
                            throw new Error('Failed to fetch schedule details');
                        }
                        const scheduleDetails = await response.json();

                        currentEditingScheduleId = scheduleDetails.id;
                        scheduleModalLabel.textContent = 'Sửa/Xóa Lịch Làm Việc';
                        modalWorkDateInput.value = formatDate(new Date(scheduleDetails.workDate));
                        modalDoctorIdSelect.value = scheduleDetails.doctorId;
                        modalIsActiveCheckbox.checked = scheduleDetails.active; // Use 'active' from backend
                        modalEventNameInput.value = scheduleDetails.name;
                        saveScheduleBtn.textContent = 'Cập nhật Lịch';
                        deleteScheduleBtn.style.display = 'inline-block'; // Show delete button
                        scheduleModal.show();
                    } catch (error) {
                        console.error('Error fetching schedule details:', error);
                        alert('Không thể tải chi tiết lịch. Vui lòng thử lại.');
                    }
                });
                eventsContainer.appendChild(eventDiv);
            });
            dayElement.appendChild(eventsContainer);
        }

        calendarGrid.appendChild(dayElement);
    }

    // Fill trailing empty cells with days from the next month
    const totalCells = startDayIndex + daysInMonth;
    const remainingCells = 42 - totalCells; // Ensure 6 rows (6 * 7 = 42 cells)
    if (totalCells % 7 !== 0) { // If not a full last week
        for (let i = 1; i <= (7 - (totalCells % 7)); i++) {
            const dayElement = document.createElement('div');
            dayElement.classList.add('calendar-day', 'other-month');
            dayElement.innerHTML = `<div class="calendar-day-number">${i}</div>`;
            calendarGrid.appendChild(dayElement);
        }
    }
    // Add more rows if needed to ensure 6 rows always for consistent layout
    const currentRows = Math.ceil(calendarGrid.children.length / 7);
    if (currentRows < 6) {
        const daysToAdd = (6 - currentRows) * 7;
        for (let i = 1; i <= daysToAdd; i++) {
            const dayElement = document.createElement('div');
            dayElement.classList.add('calendar-day', 'other-month');
            dayElement.innerHTML = `<div class="calendar-day-number">${i}</div>`;
            calendarGrid.appendChild(dayElement);
        }
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
    // Clear form and show modal for new schedule
    currentEditingScheduleId = null;
    scheduleModalLabel.textContent = 'Thêm Lịch Làm Việc';
    modalWorkDateInput.value = '';
    modalDoctorIdSelect.value = '';
    modalIsActiveCheckbox.checked = true;
    modalEventNameInput.value = '';
    saveScheduleBtn.textContent = 'Lưu Lịch';
    deleteScheduleBtn.style.display = 'none';
    scheduleModal.show();
});

saveScheduleBtn.addEventListener('click', async () => { // Make async
    const doctorId = modalDoctorIdSelect.value;
    const workDate = modalWorkDateInput.value;
    const isActive = modalIsActiveCheckbox.checked;
    const eventName = modalEventNameInput.value || `Lịch BS ${doctorId} (${workDate})`; // Default name if empty

    if (!doctorId || !workDate) {
        alert('Vui lòng chọn bác sĩ và ngày làm việc.'); // Using alert for simplicity, consider custom modal
        return;
    }

    const scheduleData = {
        doctorId: parseInt(doctorId), // Ensure doctorId is an integer
        workDate: workDate, // YYYY-MM-DD string
        isActive: isActive,
        eventName: eventName
    };

    try {
        let response;
        if (currentEditingScheduleId) {
            // Update existing schedule
            // Use the globally defined contextPath
            response = await fetch(`${contextPath}/api/doctor-schedules/${currentEditingScheduleId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(scheduleData)
            });
        } else {
            // Add new schedule
            // Use the globally defined contextPath
            response = await fetch(`${contextPath}/api/doctor-schedules`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(scheduleData)
            });
        }

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Có lỗi xảy ra khi lưu lịch.');
        }

        alert('Lịch làm việc đã được lưu thành công!'); // Replace with custom modal
        scheduleModal.hide();
        renderCalendar(); // Re-render calendar to show new/updated event
    } catch (error) {
        console.error('Error saving schedule:', error);
        alert(`Lỗi: ${error.message}`); // Replace with custom modal
    }
});

deleteScheduleBtn.addEventListener('click', async () => { // Make async
    if (currentEditingScheduleId) {
        // Replace confirm with a custom confirmation modal
        if (confirm('Bạn có chắc chắn muốn xóa lịch làm việc này không?')) {
            try {
                // Use the globally defined contextPath
                const response = await fetch(`${contextPath}/api/doctor-schedules/${currentEditingScheduleId}`, {
                    method: 'DELETE'
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'Có lỗi xảy ra khi xóa lịch.');
                }

                alert('Lịch làm việc đã được xóa thành công!'); // Replace with custom modal
                scheduleModal.hide();
                renderCalendar(); // Re-render calendar
            } catch (error) {
                console.error('Error deleting schedule:', error);
                alert(`Lỗi: ${error.message}`); // Replace with custom modal
            }
        }
    }
});

// Initial render
window.onload = function() {
    populateDoctorSelect(); // Populate doctors when page loads
    renderCalendar();
};
