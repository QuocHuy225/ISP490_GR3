// --- Global Variables (to be populated from JSP/Server) ---
// These values are now read directly from hidden inputs in the JSP,
// which are populated by your server-side logic.
const loggedInPatient = {
    id: document.getElementById('loggedInPatientId')?.value || '', // Reads from hidden input, fallback to empty string
    fullName: document.getElementById('loggedInPatientFullName')?.value || 'Khách' // Reads from hidden input, fallback to 'Khách'
};

// Base URL for your API endpoints. 'contextPath' is provided globally from JSP.
// Changed API_BASE_URL to match the new controller mapping /api/patient/
const API_BASE_URL = `${contextPath}/api/patient`; 

// --- DOM Elements ---
// (These remain the same as they refer to elements in your JSP)
const myAppointmentsTab = document.getElementById('myAppointmentsTab');
const bookAppointmentTab = document.getElementById('bookAppointmentTab');
const myAppointmentsView = document.getElementById('myAppointmentsView');
const bookAppointmentView = document.getElementById('bookAppointmentView');
const appointmentsTableBody = document.getElementById('appointmentsTableBody');
const noAppointmentsMessage = document.getElementById('noAppointmentsMessage');
const newAppointmentForm = document.getElementById('newAppointmentForm');
const patientFullNameInput = document.getElementById('patientFullName');
const newAppointmentDoctorSelect = document.getElementById('newAppointmentDoctor');
const newAppointmentServiceSelect = document.getElementById('newAppointmentService');

// Modal Elements
const appointmentDetailsModal = document.getElementById('appointmentDetailsModal');
const detailAppointmentCode = document.getElementById('detailAppointmentCode');
const detailAppointmentDate = document.getElementById('detailAppointmentDate');
const detailAppointmentTime = document.getElementById('detailAppointmentTime');
const detailDoctorName = document.getElementById('detailDoctorName');
const detailService = document.getElementById('detailService');
const detailStatus = document.getElementById('detailStatus');
const detailNotes = document.getElementById('detailNotes');

const confirmActionModal = document.getElementById('confirmActionModal');
const confirmActionMessage = document.getElementById('confirmActionMessage');
const confirmActionButton = document.getElementById('confirmActionButton');

const messageModal = document.getElementById('messageModal');
const messageModalTitle = document.getElementById('messageModalTitle');
const messageModalContent = document.getElementById('messageModalContent');

// Sidebar and Navbar elements for toggle functionality
const sidebar = document.getElementById('sidebar');
const mainWrapper = document.getElementById('main-wrapper');
const sidebarToggle = document.getElementById('sidebarToggle');
const sidebarOverlay = document.getElementById('sidebarOverlay');


// --- API Fetch Functions ---

/**
 * Fetches appointments for the logged-in patient from the backend.
 * Uses loggedInPatient.id obtained from JSP.
 * @returns {Promise<Array>} A promise that resolves to an array of appointment objects.
 */
async function fetchAppointments() {
    // Ensure patient ID is available before making the request
    if (!loggedInPatient.id) {
        console.warn('Patient ID not available. Cannot fetch appointments.');
        showMessage('error', 'Không thể tải lịch hẹn: Thiếu thông tin bệnh nhân.');
        return [];
    }
    // Updated endpoint: /api/patient/appointments/patient/{id}
    try {
        const response = await fetch(`${API_BASE_URL}/appointments/patient/${loggedInPatient.id}`);
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Không thể tải lịch hẹn.');
        }
        return await response.json();
    } catch (error) {
        console.error('Lỗi khi tải lịch hẹn:', error);
        showMessage('error', `Lỗi khi tải lịch hẹn: ${error.message}`);
        return []; // Return empty array on error
    }
}

/**
 * Fetches the list of doctors from the backend.
 * Updated endpoint: /api/patient/doctors
 * @returns {Promise<Array>} A promise that resolves to an array of doctor objects.
 */
async function fetchDoctors() {
    try {
        const response = await fetch(`${API_BASE_URL}/doctors`);
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Không thể tải danh sách bác sĩ.');
        }
        return await response.json();
    } catch (error) {
        console.error('Lỗi khi tải danh sách bác sĩ:', error);
        showMessage('error', `Lỗi khi tải danh sách bác sĩ: ${error.message}`);
        return [];
    }
}

/**
 * Fetches the list of medical services from the backend.
 * Updated endpoint: /api/patient/services
 * @returns {Promise<Array>} A promise that resolves to an array of service objects.
 */
async function fetchServices() {
    try {
        const response = await fetch(`${API_BASE_URL}/services`);
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Không thể tải danh sách dịch vụ.');
        }
        return await response.json();
    } catch (error) {
        console.error('Lỗi khi tải danh sách dịch vụ:', error);
        showMessage('error', `Lỗi khi tải danh sách dịch vụ: ${error.message}`);
        return [];
    }
}

/**
 * Sends a new appointment request to the backend.
 * Updated endpoint: /api/patient/appointments
 * @param {Object} appointmentData - The data for the new appointment.
 * @returns {Promise<Object>} A promise that resolves to the created appointment object.
 */
async function createAppointment(appointmentData) {
    try {
        const response = await fetch(`${API_BASE_URL}/appointments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // Include authorization token if your API requires it
                // 'Authorization': `Bearer ${yourAuthToken}`
            },
            body: JSON.stringify(appointmentData)
        });

        const responseData = await response.json();

        if (!response.ok) {
            throw new Error(responseData.message || 'Không thể đặt lịch hẹn.');
        }
        return responseData;
    } catch (error) {
        console.error('Lỗi khi đặt lịch hẹn:', error);
        showMessage('error', `Lỗi khi đặt lịch hẹn: ${error.message}`);
        return null;
    }
}

/**
 * Sends a request to cancel an appointment on the backend.
 * Updated endpoint: /api/patient/appointments/{appointmentId}/cancel
 * @param {string} appointmentId - The ID of the appointment to cancel.
 * @returns {Promise<boolean>} A promise that resolves to true if successful, false otherwise.
 */
async function cancelAppointmentAPI(appointmentId) {
    try {
        const response = await fetch(`${API_BASE_URL}/appointments/${appointmentId}/cancel`, {
            method: 'PUT', // Or 'DELETE' depending on your API design
            headers: {
                'Content-Type': 'application/json',
                // 'Authorization': `Bearer ${yourAuthToken}`
            }
        });

        const responseData = await response.json();

        if (!response.ok) {
            throw new Error(responseData.message || 'Không thể hủy lịch hẹn.');
        }
        return true;
    } catch (error) {
        console.error('Lỗi khi hủy lịch hẹn:', error);
        showMessage('error', `Lỗi khi hủy lịch hẹn: ${error.message}`);
        return false;
    }
}


// --- Initial Data Loading & Setup ---
document.addEventListener('DOMContentLoaded', async () => {
    // Set patient full name in the form from the value read from hidden input
    patientFullNameInput.value = loggedInPatient.fullName;

    // Load initial data for appointments, doctors, and services
    await loadInitialData();

    // Show my appointments tab by default
    showTab('myAppointmentsView');

    // Sidebar toggle logic (moved from JSP to JS)
    if (sidebarToggle && sidebar && mainWrapper && sidebarOverlay) {
        sidebarToggle.addEventListener('click', function() {
            sidebar.classList.toggle('active');
            mainWrapper.classList.toggle('active');
            sidebarOverlay.classList.toggle('active'); // Toggle overlay
        });

        // Close sidebar when clicking overlay
        sidebarOverlay.addEventListener('click', function() {
            sidebar.classList.remove('active');
            mainWrapper.classList.remove('active');
            sidebarOverlay.classList.remove('active');
        });
    }
});

/**
 * Loads initial data (appointments, doctors, services) from the backend.
 * Displays loading messages and updates UI based on fetched data.
 */
async function loadInitialData() {
    // Display loading message in the appointments table
    appointmentsTableBody.innerHTML = `<tr><td colspan="7" class="px-6 py-4 whitespace-nowrap text-center text-gray-500">Đang tải lịch hẹn...</td></tr>`;
    noAppointmentsMessage.classList.add('hidden');

    const [appointmentsData, doctorsData, servicesData] = await Promise.all([
        fetchAppointments(),
        fetchDoctors(),
        fetchServices()
    ]);

    // Render fetched data
    renderAppointments(appointmentsData);
    populateDoctorSelect(doctorsData);
    populateServiceSelect(servicesData);
}

// --- Tab Switching Logic ---
/**
 * Controls which tab content is visible and updates tab button styles.
 * @param {string} tabId - The ID of the tab view to show ('myAppointmentsView' or 'bookAppointmentView').
 */
function showTab(tabId) {
    // Hide all tab contents
    myAppointmentsView.classList.add('hidden');
    bookAppointmentView.classList.add('hidden');

    // Deactivate all tabs
    myAppointmentsTab.classList.remove('border-blue-600', 'text-blue-600');
    myAppointmentsTab.classList.add('border-transparent', 'text-gray-600');
    bookAppointmentTab.classList.remove('border-blue-600', 'text-blue-600');
    bookAppointmentTab.classList.add('border-transparent', 'text-gray-600');

    // Show selected tab content and activate tab button
    if (tabId === 'myAppointmentsView') {
        myAppointmentsView.classList.remove('hidden');
        myAppointmentsTab.classList.add('border-blue-600', 'text-blue-600');
        myAppointmentsTab.classList.remove('border-transparent', 'text-gray-600');
        loadInitialData(); // Re-fetch and render appointments when switching back to this tab
    } else if (tabId === 'bookAppointmentView') {
        bookAppointmentView.classList.remove('hidden');
        bookAppointmentTab.classList.add('border-blue-600', 'text-blue-600');
        bookAppointmentTab.classList.remove('border-transparent', 'text-gray-600');
    }
}

myAppointmentsTab.addEventListener('click', () => showTab('myAppointmentsView'));
bookAppointmentTab.addEventListener('click', () => showTab('bookAppointmentView'));

// --- Render Appointments Table ---
/**
 * Renders the list of appointments in the table.
 * Filters appointments by the logged-in patient's ID.
 * @param {Array} appointmentsData - An array of appointment objects fetched from the backend.
 */
function renderAppointments(appointmentsData) {
    appointmentsTableBody.innerHTML = ''; // Clear existing rows
    const patientAppointments = appointmentsData.filter(app => app.patient_id === loggedInPatient.id); // Use patient_id from schema

    if (patientAppointments.length === 0) {
        noAppointmentsMessage.classList.remove('hidden');
        appointmentsTableBody.innerHTML = `<tr><td colspan="7" class="px-6 py-4 whitespace-nowrap text-center text-gray-500">Bạn chưa có lịch hẹn nào.</td></tr>`;
        return;
    } else {
        noAppointmentsMessage.classList.add('hidden');
    }

    patientAppointments.forEach(appointment => {
        const row = document.createElement('tr');
        row.classList.add('hover:bg-gray-100');

        let statusClass = '';
        // Map backend status (enum) to display text and color class
        let displayStatusText = '';
        switch (appointment.status) {
            case 'PENDING': // Use uppercase as per your Java enum
                statusClass = 'bg-yellow-100', 'text-yellow-800';
                displayStatusText = 'Đang chờ';
                break;
            case 'CONFIRMED': // Use uppercase as per your Java enum
                statusClass = 'bg-green-100', 'text-green-800';
                displayStatusText = 'Đã xác nhận';
                break;
            case 'DONE': // Use uppercase as per your Java enum
                statusClass = 'bg-blue-100', 'text-blue-800';
                displayStatusText = 'Hoàn thành';
                break;
            case 'CANCELLED': // Use uppercase as per your Java enum
                statusClass = 'bg-red-100', 'text-red-800';
                displayStatusText = 'Đã hủy';
                break;
            case 'NO_SHOW': // Use uppercase as per your Java enum
                statusClass = 'bg-gray-200', 'text-gray-800';
                displayStatusText = 'Không đến';
                break;
            case 'ABANDONED': // Use uppercase as per your Java enum
                statusClass = 'bg-purple-100', 'text-purple-800';
                displayStatusText = 'Bị bỏ qua';
                break;
            default:
                statusClass = 'bg-gray-100', 'text-gray-800';
                displayStatusText = appointment.status; // Fallback to raw status
        }

        row.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${appointment.appointmentCode || appointment.id}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">${formatDate(appointment.appointmentDate)}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">${appointment.appointmentTime}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">${appointment.doctorFullName || 'N/A'}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">${appointment.serviceName || 'N/A'}</td>
            <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${statusClass}">
                    ${displayStatusText}
                </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-center text-sm font-medium">
                <button onclick="showAppointmentDetails('${appointment.id}')" class="text-blue-600 hover:text-blue-900 mx-1">
                    <i class="fas fa-eye"></i>
                </button>
                ${(appointment.status === 'PENDING' || appointment.status === 'CONFIRMED') ? `
                <button onclick="confirmCancelAppointment('${appointment.id}')" class="text-red-600 hover:text-red-900 mx-1">
                    <i class="fas fa-times-circle"></i>
                </button>` : ''}
            </td>
        `;
        appointmentsTableBody.appendChild(row);
    });
}

// --- Populate Doctor and Service Selects ---
/**
 * Populates the doctor select dropdown with data from the backend.
 * @param {Array} doctorsData - An array of doctor objects.
 */
function populateDoctorSelect(doctorsData) {
    newAppointmentDoctorSelect.innerHTML = '<option value="">-- Chọn bác sĩ --</option>';
    doctorsData.forEach(doctor => {
        const option = document.createElement('option');
        option.value = doctor.id; // Use 'id' from doctors table
        option.textContent = doctor.fullName; // Use 'fullName' from doctors table
        newAppointmentDoctorSelect.appendChild(option);
    });
}

/**
 * Populates the service select dropdown with data from the backend.
 * @param {Array} servicesData - An array of service objects.
 */
function populateServiceSelect(servicesData) {
    newAppointmentServiceSelect.innerHTML = '<option value="">-- Chọn dịch vụ --</option>';
    servicesData.forEach(service => {
        const option = document.createElement('option');
        option.value = service.servicesId; // Use 'servicesId' from medical_services table
        option.textContent = service.serviceName; // Use 'serviceName' from medical_services table
        newAppointmentServiceSelect.appendChild(option);
    });
}

// --- Appointment Details Modal Logic ---
/**
 * Displays the details of a specific appointment in a modal.
 * Note: This currently extracts data from the rendered table. For full details
 * (e.g., notes not shown in table), you might need to re-fetch the specific appointment.
 * @param {string} id - The ID of the appointment to display.
 */
async function showAppointmentDetails(id) {
    // Fetch all appointments again to find the specific one with full details
    // This is more robust than parsing from the table if not all data is displayed there.
    const allAppointments = await fetchAppointments();
    const appointment = allAppointments.find(app => app.id == id); // Use == for potential type coercion

    if (appointment) {
        detailAppointmentCode.textContent = appointment.appointmentCode || appointment.id;
        detailAppointmentDate.textContent = formatDate(appointment.appointmentDate);
        detailAppointmentTime.textContent = appointment.appointmentTime;
        detailDoctorName.textContent = appointment.doctorFullName || 'N/A';
        detailService.textContent = appointment.serviceName || 'N/A';
        detailNotes.textContent = appointment.notes || 'Không có'; // Assuming 'notes' field exists in backend data

        // Apply status class based on backend status
        detailStatus.className = `font-semibold px-2 inline-flex text-xs leading-5 rounded-full `;
        let displayStatusText = '';
        switch (appointment.status) {
            case 'PENDING':
                detailStatus.classList.add('bg-yellow-100', 'text-yellow-800');
                displayStatusText = 'Đang chờ';
                break;
            case 'CONFIRMED':
                detailStatus.classList.add('bg-green-100', 'text-green-800');
                displayStatusText = 'Đã xác nhận';
                break;
            case 'DONE':
                detailStatus.classList.add('bg-blue-100', 'text-blue-800');
                displayStatusText = 'Hoàn thành';
                break;
            case 'CANCELLED':
                detailStatus.classList.add('bg-red-100', 'text-red-800');
                displayStatusText = 'Đã hủy';
                break;
            case 'NO_SHOW':
                detailStatus.classList.add('bg-gray-200', 'text-gray-800');
                displayStatusText = 'Không đến';
                break;
            case 'ABANDONED':
                detailStatus.classList.add('bg-purple-100', 'text-purple-800');
                displayStatusText = 'Bị bỏ qua';
                break;
            default:
                detailStatus.classList.add('bg-gray-100', 'text-gray-800');
                displayStatusText = appointment.status;
        }
        detailStatus.textContent = displayStatusText;

        openModal('appointmentDetailsModal');
    } else {
        showMessage('error', 'Không tìm thấy chi tiết lịch hẹn.');
    }
}

// --- Confirm Cancel Appointment Logic ---
/**
 * Shows a confirmation modal for cancelling an appointment.
 * @param {string} id - The ID of the appointment to cancel.
 */
function confirmCancelAppointment(id) {
    confirmActionMessage.textContent = `Bạn có chắc chắn muốn hủy lịch hẹn ${id} này không?`;
    confirmActionButton.onclick = async () => {
        const success = await cancelAppointmentAPI(id);
        if (success) {
            showMessage('success', `Lịch hẹn ${id} đã được hủy thành công.`);
            await loadInitialData(); // Re-fetch and render appointments after successful cancellation
        } else {
            // Error message already shown by cancelAppointmentAPI
        }
        closeModal('confirmActionModal');
    };
    confirmActionButton.classList.remove('bg-red-500', 'hover:bg-red-600');
    confirmActionButton.classList.add('bg-red-500', 'hover:bg-red-600'); // Ensure red color
    openModal('confirmActionModal');
}

// --- New Appointment Form Submission ---
newAppointmentForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Collect data from the form
    const newAppointmentData = {
        patient_id: loggedInPatient.id, // Patient ID from JSP
        appointment_date: document.getElementById('newAppointmentDate').value,
        appointment_time: document.getElementById('newAppointmentTime').value,
        doctor_id: document.getElementById('newAppointmentDoctor').value, // Doctor ID from select
        services_id: document.getElementById('newAppointmentService').value, // Service ID from select
        notes: document.getElementById('newAppointmentNotes').value,
        // Backend should handle status, created_at, etc.
    };

    // Basic client-side validation
    if (!newAppointmentData.appointment_date || !newAppointmentData.appointment_time || !newAppointmentData.doctor_id || !newAppointmentData.services_id) {
        showMessage('error', 'Vui lòng điền đầy đủ các trường bắt buộc (Ngày, Giờ, Bác sĩ, Dịch vụ).');
        return;
    }

    const createdApp = await createAppointment(newAppointmentData);
    if (createdApp) {
        showMessage('success', `Lịch hẹn của bạn đã được đặt thành công! Mã lịch hẹn: ${createdApp.appointmentCode || createdApp.id || 'N/A'}`);
        newAppointmentForm.reset(); // Clear form
        patientFullNameInput.value = loggedInPatient.fullName; // Re-fill patient name
        showTab('myAppointmentsView'); // Switch back to my appointments view and re-load data
    } else {
        // Error message already shown by createAppointment
    }
});

// --- General Modal Functions ---
/**
 * Opens a modal by removing its 'hidden' class.
 * @param {string} modalId - The ID of the modal element.
 */
function openModal(modalId) {
    document.getElementById(modalId).classList.remove('hidden');
}

/**
 * Closes a modal by adding its 'hidden' class.
 * @param {string} modalId - The ID of the modal element.
 */
function closeModal(modalId) {
    document.getElementById(modalId).classList.add('hidden');
}

/**
 * Displays a message in the generic message modal.
 * @param {string} type - Type of message ('success', 'error', 'info').
 * @param {string} message - The message content.
 */
function showMessage(type, message) {
    messageModalTitle.textContent = type === 'success' ? 'Thành công!' : (type === 'error' ? 'Lỗi!' : 'Thông báo!');
    messageModalTitle.classList.remove('text-red-800', 'text-green-800', 'text-gray-800');
    if (type === 'success') {
        messageModalTitle.classList.add('text-green-800');
    } else if (type === 'error') {
        messageModalTitle.classList.add('text-red-800');
    } else {
        messageModalTitle.classList.add('text-gray-800');
    }
    messageModalContent.textContent = message;
    openModal('messageModal');
}

// --- Utility Functions ---
/**
 * Formats a date string into 'DD/MM/YYYY' format.
 * @param {string} dateString - The date string to format (e.g., '2025-07-01').
 * @returns {string} The formatted date string or 'N/A' if invalid.
 */
function formatDate(dateString) {
    // Ensure dateString is valid for Date constructor
    if (!dateString) return 'N/A';
    try {
        const date = new Date(dateString);
        // Check if date parsing resulted in a valid date
        if (isNaN(date.getTime())) {
            return dateString; // Return original if parsing fails
        }
        const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
        return date.toLocaleDateString('vi-VN', options);
    } catch (e) {
        return dateString; // Return original if an error occurs
    }
}

// --- Logout Button ---
document.getElementById('logoutBtn').addEventListener('click', () => {
    showMessage('info', 'Bạn đã đăng xuất.');
    // In a real app, this would send a request to your logout endpoint
    // and then redirect to a login page or clear session.
    console.log('Đăng xuất thành công!');
    // Example: window.location.href = `${contextPath}/auth/logout`;
});
