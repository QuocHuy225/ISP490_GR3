// Function to format time slots (HH:mm - HH:mm)
// This function now expects a slot object with startTime and endTime properties.
function formatTimeRange(slot) {
    if (!slot || !slot.startTime || !slot.endTime) return '';

    // Convert HH:mm:ss strings to HH:mm for display
    const formattedStartTime = slot.startTime.substring(0, 5); // Take HH:mm
    const formattedEndTime = slot.endTime.substring(0, 5);     // Take HH:mm

    return `${formattedStartTime} - ${formattedEndTime}`;
}

// Global variables for selected date/time
let selectedDate = null;
let selectedSlot = null; // This will now store the full selected slot object

// DOM elements
const bookFinalBtn = document.getElementById('bookFinalBtn');
const timeSlotsContainer = document.getElementById('timeSlotsContainer');
const scheduleNav = document.getElementById('scheduleNav');
const scheduleScrollRight = document.getElementById('scheduleScrollRight');
const scheduleScrollLeft = document.getElementById('scheduleScrollLeft');

// === BẮT ĐẦU: Logic cho Sidebar (Đã sao chép và điều chỉnh từ make-appointment.js) ===
const sidebar = document.getElementById('sidebar');
const mainWrapper = document.getElementById('main-wrapper');
let sidebarOverlay = document.getElementById('sidebarOverlay'); // Changed to let, as it might be created
const sidebarCollapseBtn = document.getElementById('sidebarCollapse'); // Nút để bật/tắt sidebar

// Define the responsive breakpoint as used in make-appointment.css
const RESPONSIVE_BREAKPOINT = 991.98;

// Ensure sidebarOverlay exists
document.addEventListener('DOMContentLoaded', () => {
    sidebarOverlay = document.getElementById('sidebarOverlay'); // Re-get after DOM load
    if (!sidebarOverlay) {
        sidebarOverlay = document.createElement('div');
        sidebarOverlay.id = 'sidebarOverlay';
        // Add classes that might be defined in your make-appointment.css for the overlay
        sidebarOverlay.classList.add('sidebar-overlay'); // Assuming this class exists for base styling
        document.body.appendChild(sidebarOverlay);
    }
});


function toggleSidebar() {
    if (window.innerWidth <= RESPONSIVE_BREAKPOINT) {
        // Mobile behavior: sidebar slides in/out, overlay appears/disappears, body no-scroll
        if (sidebar) sidebar.classList.toggle('active');
        if (sidebarOverlay) sidebarOverlay.classList.toggle('active');
        document.body.classList.toggle('no-scroll', sidebar && sidebar.classList.contains('active'));
        // For mobile, mainWrapper generally doesn't get margin-left, just covered by overlay
        if (mainWrapper) mainWrapper.classList.remove('expanded'); // Ensure desktop collapse state is removed
        if (sidebar) sidebar.classList.remove('collapsed'); // Ensure desktop collapsed state is removed
    } else {
        // Desktop behavior: sidebar collapses/expands, main-wrapper expands/collapses
        if (sidebar) sidebar.classList.toggle('collapsed');
        if (mainWrapper) mainWrapper.classList.toggle('expanded');
        // Ensure mobile active states are removed on desktop
        if (sidebar) sidebar.classList.remove('active');
        if (sidebarOverlay) sidebarOverlay.classList.remove('active');
        document.body.classList.remove('no-scroll');
    }
}

// Event listener for the sidebar toggle button
if (sidebarCollapseBtn) {
    sidebarCollapseBtn.addEventListener('click', toggleSidebar);
}

// Event listener for overlay to close sidebar on mobile
// Make sure sidebarOverlay is correctly referenced after DOM load
document.addEventListener('DOMContentLoaded', () => {
    const currentSidebarOverlay = document.getElementById('sidebarOverlay');
    if (currentSidebarOverlay) {
        currentSidebarOverlay.addEventListener('click', () => {
            if (window.innerWidth <= RESPONSIVE_BREAKPOINT && sidebar && sidebar.classList.contains('active')) {
                toggleSidebar(); // Use toggleSidebar to ensure all states are reset
            }
        });
    }
});


// Function to adjust sidebar state on window resize
function checkWidthAndAdjustSidebar() {
    if (sidebar && mainWrapper && sidebarOverlay) {
        if (window.innerWidth <= RESPONSIVE_BREAKPOINT) {
            // If window resizes to mobile size, ensure desktop states are removed
            sidebar.classList.remove('active'); // Ensure mobile active is off
            sidebar.classList.add('collapsed'); // Set to collapsed on mobile by default (hidden)
            mainWrapper.classList.remove('expanded');
            sidebarOverlay.classList.remove('active');
            document.body.classList.remove('no-scroll');
        } else {
            // If window resizes to desktop size, ensure mobile states are removed
            sidebar.classList.remove('active'); // Ensure mobile active is off
            sidebar.classList.remove('collapsed'); // Remove collapsed to show full sidebar by default
            mainWrapper.classList.remove('expanded'); // Ensure mainWrapper is not expanded by default
            sidebarOverlay.classList.remove('active');
            document.body.classList.remove('no-scroll');
        }
    }
}

// Attach resize listener
window.addEventListener('resize', checkWidthAndAdjustSidebar);

// Initial adjustment on DOMContentLoaded
document.addEventListener('DOMContentLoaded', checkWidthAndAdjustSidebar);

// Event listener to close sidebar when clicking a menu item (on mobile/overlay active)
if (sidebar) {
    sidebar.querySelectorAll('.list-unstyled.components a').forEach(item => {
        item.addEventListener('click', function() {
            // Only close sidebar if it's currently active (on mobile)
            if (window.innerWidth <= RESPONSIVE_BREAKPOINT && sidebar.classList.contains('active')) {
                toggleSidebar();
            }
        });
    });
}
// === KẾT THÚC: Logic cho Sidebar ===


// Function to render time slots for a given date
function updateTimeSlots(dateStr) {
    timeSlotsContainer.innerHTML = ''; // Clear existing slots

    const scheduleForDate = doctorData.schedules.find(s => s.workingDate === dateStr);

    if (scheduleForDate && scheduleForDate.timeSlots && scheduleForDate.timeSlots.length > 0) {
        // Sort time slots based on startTime
        const sortedTimeSlots = scheduleForDate.timeSlots.sort((a, b) => {
            if (a.startTime < b.startTime) return -1;
            if (a.startTime > b.startTime) return 1;
            return 0;
        });

        // Create containers for Morning and Afternoon slots
        const morningSectionDiv = document.createElement('div');
        morningSectionDiv.classList.add('time-section');
        const morningHeader = document.createElement('h5');
        morningHeader.innerHTML = '<i class="bi bi-sun-fill me-2"></i>Buổi sáng'; // Icon for morning
        morningSectionDiv.appendChild(morningHeader);
        const morningSlotsDiv = document.createElement('div');
        morningSlotsDiv.classList.add('time-slots-grid'); // New class for grid layout
        morningSectionDiv.appendChild(morningSlotsDiv);

        const afternoonSectionDiv = document.createElement('div');
        afternoonSectionDiv.classList.add('time-section', 'mt-4'); // Added mt-4 for spacing
        const afternoonHeader = document.createElement('h5');
        afternoonHeader.innerHTML = '<i class="bi bi-moon-fill me-2"></i>Buổi chiều'; // Icon for afternoon
        afternoonSectionDiv.appendChild(afternoonHeader);
        const afternoonSlotsDiv = document.createElement('div');
        afternoonSlotsDiv.classList.add('time-slots-grid'); // New class for grid layout
        afternoonSectionDiv.appendChild(afternoonSlotsDiv);

        let hasMorningSlots = false;
        let hasAfternoonSlots = false;

        sortedTimeSlots.forEach(slot => {
            const hour = parseInt(slot.startTime.substring(0, 2)); // Get hour from "HH:mm:ss" string
            const button = document.createElement('a');
            button.href = "#";
            button.classList.add('time-slot-btn');
            
            // Store the full slot object in data attribute for easy retrieval
            button.setAttribute('data-slot-json', JSON.stringify(slot)); 
            
            // Set data-patients-info for hover effect
            button.setAttribute('data-patients-info', `${slot.bookedPatients}/${slot.maxPatients}`);

            // Display formatted time range only initially
            let buttonText = formatTimeRange(slot);
            const isFull = slot.bookedPatients >= slot.maxPatients;

            if (isFull) {
                button.classList.add('disabled'); // Add disabled class for styling
                button.disabled = true; // Disable the button interaction
                buttonText += ' (Đã đầy)'; // Still show "Đã đầy" for full slots
            }
            button.textContent = buttonText; // Set button text without patients info

            if (hour < 12) { // Morning until 11:59
                morningSlotsDiv.appendChild(button);
                hasMorningSlots = true;
            } else { // Afternoon from 12:00 onwards
                afternoonSlotsDiv.appendChild(button);
                hasAfternoonSlots = true;
            }
        });

        // Append sections only if they contain slots
        if (hasMorningSlots) {
            timeSlotsContainer.appendChild(morningSectionDiv);
        }
        if (hasAfternoonSlots) {
            timeSlotsContainer.appendChild(afternoonSectionDiv);
        }

        if (!hasMorningSlots && !hasAfternoonSlots) {
            const noSlotsParagraph = document.createElement('p');
            noSlotsParagraph.classList.add('text-muted');
            noSlotsParagraph.textContent = 'Không có khung giờ nào cho ngày này.';
            timeSlotsContainer.appendChild(noSlotsParagraph);
        }

    } else {
        const noSlotsParagraph = document.createElement('p');
        noSlotsParagraph.classList.add('text-muted');
        noSlotsParagraph.textContent = 'Không có khung giờ nào cho ngày này.';
        timeSlotsContainer.appendChild(noSlotsParagraph);
    }

    // Reset selected slot and update booking button state
    selectedSlot = null;
    if (bookFinalBtn) {
        bookFinalBtn.disabled = true;
        bookFinalBtn.textContent = 'Chọn ngày và giờ để đặt lịch';
    }

    // Add click listener to newly rendered time slots
    document.querySelectorAll('.time-slot-btn:not(.disabled)').forEach(button => { // Only listen to non-disabled buttons
        button.addEventListener('click', function(event) {
            event.preventDefault(); // Prevent default link behavior
            // Remove 'selected' class from all other time slots
            document.querySelectorAll('.time-slot-btn').forEach(btn => btn.classList.remove('selected'));
            // Add 'selected' class to the clicked time slot
            this.classList.add('selected');
            selectedSlot = JSON.parse(this.getAttribute('data-slot-json')); // Parse the full slot object

            // Update the final booking button text and enable it
            if (selectedDate && selectedSlot && bookFinalBtn) {
                const dateParts = selectedDate.split('-'); //YYYY-MM-DD
                const displayDate = `${dateParts[2]}-${dateParts[1]}-${dateParts[0]}`; // DD-MM-YYYY
                bookFinalBtn.disabled = false;
                bookFinalBtn.textContent = `Đặt lịch vào ${formatTimeRange(selectedSlot)} ngày ${displayDate}`; 
            }
        });
    });
}

// Function to update scroll button visibility
function updateScrollButtons() {
    if (!scheduleNav || !scheduleScrollLeft || !scheduleScrollRight) return;

    if (scheduleNav.scrollWidth > scheduleNav.clientWidth) {
        // Show right button if not at the end
        scheduleScrollRight.style.display = (scheduleNav.scrollLeft + scheduleNav.clientWidth + 5 < scheduleNav.scrollWidth) ? 'block' : 'none';
        // Show left button if not at the beginning
        scheduleScrollLeft.style.display = scheduleNav.scrollLeft > 5 ? 'block' : 'none'; // Add a small threshold
    } else {
        // Hide both if no overflow
        scheduleScrollRight.style.display = 'none';
        scheduleScrollLeft.style.display = 'none';
    }
}

document.addEventListener('DOMContentLoaded', function() {
    // Event listener for schedule-item clicks (date selection)
    if (scheduleNav) {
        scheduleNav.addEventListener('click', function(event) {
            const target = event.target.closest('.schedule-item');
            if (target) {
                // Remove 'selected' class from all other items
                document.querySelectorAll('.schedule-item').forEach(el => el.classList.remove('selected'));
                // Add 'selected' class to clicked item
                target.classList.add('selected');
                selectedDate = target.getAttribute('data-date');
                updateTimeSlots(selectedDate); // Update time slots for the new date
            }
        });
    }

    // Scroll buttons for schedule navigation
    if (scheduleScrollRight) {
        scheduleScrollRight.addEventListener('click', function() {
            scheduleNav.scrollBy({ left: 200, behavior: 'smooth' }); // Adjust scroll amount as needed
        });
    }
    
    if (scheduleScrollLeft) {
        scheduleScrollLeft.addEventListener('click', function() {
            scheduleNav.scrollBy({ left: -200, behavior: 'smooth' }); // Adjust scroll amount as needed
        });
    }

    // Update button visibility on scroll and resize
    if (scheduleNav) {
        scheduleNav.addEventListener('scroll', updateScrollButtons);
    }
    window.addEventListener('resize', updateScrollButtons); // Already has this

    // Event listener for the final booking button click
    if (bookFinalBtn) {
        bookFinalBtn.addEventListener('click', function(e) {
            e.preventDefault();
            if (selectedDate && selectedSlot) { // Check selectedSlot object
                // Construct the URL for the booking servlet/controller
                const bookingUrl = window.APP_CONTEXT_PATH + '/makeappointments?doctorId=' + doctorData.id +
                                   '&date=' + selectedDate + '&time=' + selectedSlot.startTime; // Send only start time to backend
                
                // Redirect to the booking page or open a modal, etc.
                window.location.href = bookingUrl;
            } else {
                alert('Vui lòng chọn ngày và giờ khám.'); // Consider a custom modal instead of alert
            }
        });
    }

    // Initial render for the first selected date (Today)
    const initialSelectedScheduleItem = document.querySelector('#scheduleNav .schedule-item.selected');
    if (initialSelectedScheduleItem) {
        selectedDate = initialSelectedScheduleItem.getAttribute('data-date');
        updateTimeSlots(selectedDate);
    }
    
    // Initial check for scroll buttons visibility on load
    updateScrollButtons();
});
