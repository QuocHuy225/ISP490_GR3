document.addEventListener('DOMContentLoaded', function() {
    // =========================================================
    // 1. DỮ LIỆU MẪU (SẼ THAY THẾ BẰNG DỮ LIỆU TỪ API THỰC TẾ)
    // =========================================================
    // Khi có API backend, bạn sẽ xóa hoặc comment biến này và lấy dữ liệu từ server.
    const sampleAppointments = [
        {
            id: 1,
            doctorName: "Dr. Nguyễn Văn A",
            date: "15/07/2025",
            time: "09:00 AM",
            service: "Khám tổng quát",
            status: "Sắp tới",
            notes: "Vui lòng đến sớm 15 phút để làm thủ tục."
        },
        {
            id: 2,
            doctorName: "Dr. Trần Thị B",
            date: "20/07/2025",
            time: "02:30 PM",
            service: "Tư vấn dinh dưỡng",
            status: "Đang chờ",
            notes: "Chuẩn bị danh sách câu hỏi."
        },
        {
            id: 3,
            doctorName: "Dr. Lê Văn C",
            date: "10/06/2025",
            time: "10:00 AM",
            service: "Siêu âm",
            status: "Đã hoàn thành",
            notes: ""
        },
        {
            id: 4,
            doctorName: "Dr. Phạm Thị D",
            date: "05/06/2025",
            time: "04:00 PM",
            service: "Khám răng",
            status: "Đã hủy",
            notes: "Do trùng lịch cá nhân."
        },
        {
            id: 5,
            doctorName: "Dr. Nguyễn Văn A",
            date: "25/07/2025",
            time: "11:00 AM",
            service: "Tái khám",
            status: "Sắp tới",
            notes: ""
        },
        {
            id: 6,
            doctorName: "Dr. Trần Thị B",
            date: "01/07/2025",
            time: "08:00 AM",
            service: "Xét nghiệm máu",
            status: "Đã hoàn thành",
            notes: ""
        },
        {
            id: 7,
            doctorName: "Dr. Lê Văn C",
            date: "03/07/2025",
            time: "01:00 PM",
            service: "Kiểm tra huyết áp",
            status: "Đang chờ",
            notes: ""
        },
        {
            id: 8,
            doctorName: "Dr. Phạm Thị D",
            date: "12/05/2025",
            time: "03:00 PM",
            service: "Tiêm phòng",
            status: "Đã hoàn thành",
            notes: ""
        },
        {
            id: 9,
            doctorName: "Dr. Nguyễn Văn A",
            date: "18/07/2025",
            time: "09:30 AM",
            service: "Tư vấn tâm lý",
            status: "Sắp tới",
            notes: "Chuẩn bị nội dung cần tư vấn."
        },
        {
            id: 10,
            doctorName: "Dr. Trần Thị B",
            date: "22/07/2025",
            time: "03:00 PM",
            service: "Kiểm tra sức khỏe định kỳ",
            status: "Sắp tới",
            notes: ""
        },
        {
            id: 11,
            doctorName: "Dr. Lê Văn C",
            date: "28/05/2025",
            time: "09:00 AM",
            service: "Khám chuyên khoa",
            status: "Đã hoàn thành",
            notes: "Chuyển sang điều trị tiếp theo."
        },
        // Thêm một số lịch sử để kiểm tra phân trang lịch sử
        {
            id: 12,
            doctorName: "Dr. Phạm Thị D",
            date: "10/04/2025",
            time: "02:00 PM",
            service: "Tái khám",
            status: "Đã hoàn thành",
            notes: "Không có vấn đề gì."
        },
        {
            id: 13,
            doctorName: "Dr. Nguyễn Văn A",
            date: "01/03/2025",
            time: "10:00 AM",
            service: "Khám tổng quát",
            status: "Đã hoàn thành",
            notes: "Kiểm tra sức khỏe định kỳ."
        },
        {
            id: 14,
            doctorName: "Dr. Trần Thị B",
            date: "15/02/2025",
            time: "04:00 PM",
            service: "Tư vấn dinh dưỡng",
            status: "Đã hủy",
            notes: "Bệnh nhân không đến."
        },
        {
            id: 15,
            doctorName: "Dr. Lê Văn C",
            date: "20/01/2025",
            time: "09:00 AM",
            service: "Siêu âm",
            status: "Đã hoàn thành",
            notes: "Kết quả siêu âm tốt."
        }
    ];

    // === CÁC BIẾN CỐ ĐỊNH ===
    const itemsPerPage = 5;
    let currentPageUpcoming = 1;
    let currentPageHistory = 1;

    // allAppointmentsData sẽ được điền từ localStorage hoặc sampleAppointments
    let allAppointmentsData = [];
    let upcomingAppointments = [];
    let appointmentHistory = [];

    const upcomingAppointmentsList = document.getElementById('upcomingAppointmentsList');
    const appointmentHistoryList = document.getElementById('appointmentHistoryList');
    const upcomingPagination = document.getElementById('upcomingPagination');
    const historyPagination = document.getElementById('historyPagination');
    const noUpcomingAppointments = document.getElementById('noUpcomingAppointments');
    const noHistoryAppointments = document.getElementById('noHistoryAppointments');

    // === CÁC PHẦN TỬ MODAL HIỆN CÓ ===
    const modal = document.getElementById("appointmentDetailModal");
    const closeButton = document.querySelector("#appointmentDetailModal .close-button");
    const btnCloseDetail = document.getElementById("btnCloseDetail");
    const btnCancelAppointment = document.getElementById("btnCancelAppointment");
    const detailContent = document.getElementById("detailContent");

    const cancelConfirmModal = document.getElementById("cancelConfirmModal");
    const closeConfirmModalBtn = document.getElementById("closeConfirmModalBtn");
    const confirmCancelBtn = document.getElementById("confirmCancelBtn");
    const cancelConfirmBtn = document.getElementById("cancelConfirmBtn");
    const confirmAppointmentIdDisplay = document.getElementById("confirmAppointmentIdDisplay");

    const successMessageModal = document.getElementById("successMessageModal");
    const closeSuccessModalBtn = document.getElementById("closeSuccessModalBtn");
    const okSuccessModalBtn = document.getElementById("okSuccessModalBtn");
    const successMessageText = document.getElementById("successMessageText");

    let currentAppointmentIdToCancel = null; // Biến để lưu ID lịch hẹn đang được xem xét hủy

    // === CÁC BIẾN CHO MODAL ĐẶT LỊCH HẸN MỚI (ĐÃ THÊM) ===
    const newAppointmentModal = document.getElementById("newAppointmentModal");
    const closeNewAppointmentModalBtn = document.getElementById("closeNewAppointmentModalBtn");
    const cancelNewAppointmentBtn = document.getElementById("cancelNewAppointmentBtn");
    const newAppointmentForm = document.getElementById("newAppointmentForm");
    const submitNewAppointmentBtn = document.getElementById("submitNewAppointmentBtn");

    // Thêm các element input/select cụ thể cho modal mới
    const newDoctorNameSelect = document.getElementById('newDoctorName');
    const newAppointmentDateInput = document.getElementById('newAppointmentDate');
    const newAppointmentTimeSelect = document.getElementById('newAppointmentTime'); // Bây giờ là <select>

    // Danh sách các khung giờ mặc định khả dụng
    const defaultTimeSlots = [
        "08:00 AM", "08:30 AM", "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
        "11:00 AM", "11:30 AM", "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM",
        "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM"
    ];

    // Thời gian tối thiểu cần để đặt lịch trước (ví dụ: 2 giờ)
    const MIN_BOOKING_LEAD_TIME_HOURS = 2;
    // Giới hạn số ngày tối đa cho phép đặt trước (ví dụ: 90 ngày)
    const MAX_BOOKING_DAYS_IN_ADVANCE = 90;


    // === CÁC HÀM TIỆN ÍCH ===

    // Hàm hiển thị modal thông báo thành công
    function showSuccessMessage(message) {
        if (successMessageText) {
            successMessageText.textContent = message;
            successMessageModal.style.display = "flex";
        }
    }

    // Hàm render danh sách lịch hẹn vào UI
    function renderAppointments(appointments, container, currentPage, paginationContainer) {
        container.innerHTML = ''; // Xóa nội dung hiện tại
        const totalPages = Math.ceil(appointments.length / itemsPerPage);
        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;
        const currentAppointments = appointments.slice(startIndex, endIndex);

        // Hiển thị/Ẩn thông báo "không có lịch hẹn"
        if (currentAppointments.length === 0) {
            if (container.id === 'upcomingAppointmentsList') {
                noUpcomingAppointments.style.display = 'block';
            } else {
                noHistoryAppointments.style.display = 'block';
            }
            paginationContainer.innerHTML = ''; // Ẩn phân trang nếu không có lịch hẹn
            return;
        } else {
             if (container.id === 'upcomingAppointmentsList') {
                noUpcomingAppointments.style.display = 'none';
            } else {
                noHistoryAppointments.style.display = 'none';
            }
        }

        currentAppointments.forEach(appointment => {
            const item = document.createElement('div');
            item.classList.add('appointment-item');
            item.innerHTML = `
                <h4><i class="fas fa-calendar-check"></i> Lịch Hẹn ${appointment.id} - ${appointment.service}</h4>
                <p><strong>Bác sĩ:</strong> ${appointment.doctorName}</p>
                <p><strong>Thời gian:</strong> ${appointment.time} ngày ${appointment.date}</p>
                <p><strong>Trạng thái:</strong> <span class="status ${appointment.status.toLowerCase().replace(' ', '-')}">${appointment.status}</span></p>
            `;
            item.dataset.id = appointment.id; // Thêm data-id để dễ dàng lấy thông tin khi click
            container.appendChild(item);
        });

        setupPagination(totalPages, currentPage, paginationContainer, container.id);
        attachAppointmentItemListeners(); // Đảm bảo listener được gắn lại sau khi render
    }

    // Hàm tạo và quản lý phân trang
    function setupPagination(totalPages, currentPage, paginationContainer, listId) {
        paginationContainer.innerHTML = ''; // Xóa phân trang hiện có

        // Nếu không có lịch hẹn nào, không hiển thị phân trang
        if (totalPages === 0) {
            return;
        }

        // --- Logic cho nút "Trước" (chỉ hiển thị nếu có > 1 trang) ---
        if (totalPages > 1) {
            const prevButton = document.createElement('button');
            prevButton.textContent = 'Trước';
            prevButton.disabled = currentPage === 1;
            prevButton.addEventListener('click', () => {
                if (listId === 'upcomingAppointmentsList') {
                    currentPageUpcoming--;
                    renderAppointments(upcomingAppointments, upcomingAppointmentsList, currentPageUpcoming, upcomingPagination);
                } else {
                    currentPageHistory--;
                    renderAppointments(appointmentHistory, appointmentHistoryList, currentPageHistory, historyPagination);
                }
            });
            paginationContainer.appendChild(prevButton);
        }

        // --- Logic cho các nút số trang (luôn hiển thị nếu có dữ liệu) ---
        for (let i = 1; i <= totalPages; i++) {
            const pageButton = document.createElement('button');
            pageButton.textContent = i;
            pageButton.classList.toggle('active', i === currentPage);
            // Vô hiệu hóa nút nếu chỉ có 1 trang để tránh click vô nghĩa
            if (totalPages === 1) {
                pageButton.disabled = true;
            } else {
                pageButton.addEventListener('click', () => {
                    if (listId === 'upcomingAppointmentsList') {
                        currentPageUpcoming = i;
                        renderAppointments(upcomingAppointments, upcomingAppointmentsList, currentPageUpcoming, upcomingPagination);
                    } else {
                        currentPageHistory = i;
                        renderAppointments(appointmentHistory, appointmentHistoryList, currentPageHistory, historyPagination);
                    }
                });
            }
            paginationContainer.appendChild(pageButton);
        }

        // --- Logic cho nút "Sau" (chỉ hiển thị nếu có > 1 trang) ---
        if (totalPages > 1) {
            const nextButton = document.createElement('button');
            nextButton.textContent = 'Sau';
            nextButton.disabled = currentPage === totalPages;
            nextButton.addEventListener('click', () => {
                if (listId === 'upcomingAppointmentsList') {
                    currentPageUpcoming++;
                    renderAppointments(upcomingAppointments, upcomingAppointmentsList, currentPageUpcoming, upcomingPagination);
                } else {
                    currentPageHistory++;
                    renderAppointments(appointmentHistory, appointmentHistoryList, currentPageHistory, historyPagination);
                }
            });
            paginationContainer.appendChild(nextButton);
        }
    }

    // Hàm hiển thị modal chi tiết lịch hẹn
    function showAppointmentDetail(appointmentData) {
        if (detailContent) {
            detailContent.innerHTML = `
                <p><strong>ID Lịch Hẹn:</strong> ${appointmentData.id || 'N/A'}</p>
                <p><strong>Bác sĩ:</strong> ${appointmentData.doctorName || 'N/A'}</p>
                <p><strong>Ngày:</strong> ${appointmentData.date || 'N/A'}</p>
                <p><strong>Giờ:</strong> ${appointmentData.time || 'N/A'}</p>
                <p><strong>Dịch vụ:</strong> ${appointmentData.service || 'N/A'}</p>
                <p><strong>Trạng thái:</strong> <span class="status ${appointmentData.status.toLowerCase().replace(' ', '-')}">${appointmentData.status || 'N/A'}</span></p>
                ${appointmentData.notes ? `<p><strong>Ghi chú:</strong> ${appointmentData.notes}</p>` : ''}
            `;
            // Hiển thị nút hủy hẹn nếu trạng thái là "Sắp tới"
            if (btnCancelAppointment) {
                if (appointmentData.status && appointmentData.status.toLowerCase() === 'sắp tới') {
                    btnCancelAppointment.style.display = 'inline-block';
                    currentAppointmentIdToCancel = appointmentData.id; // Lưu ID để hủy
                } else {
                    btnCancelAppointment.style.display = 'none';
                    currentAppointmentIdToCancel = null; // Reset nếu không thể hủy
                }
            }
            modal.style.display = "flex"; // Sử dụng flex để căn giữa modal theo CSS
        } else {
            console.error("Element with ID 'detailContent' not found for modal details.");
        }
    }

    // Gán sự kiện click cho mỗi mục lịch hẹn để hiển thị modal chi tiết
    function attachAppointmentItemListeners() {
        document.querySelectorAll('.appointment-item').forEach(item => {
            // Xóa bỏ listener cũ trước khi thêm mới để tránh trùng lặp khi render lại
            item.removeEventListener('click', handleAppointmentItemClick);
            item.addEventListener('click', handleAppointmentItemClick);
        });
    }

    function handleAppointmentItemClick(event) {
        const appointmentId = event.currentTarget.dataset.id;
        // Tìm lịch hẹn trong dữ liệu gốc (allAppointmentsData)
        const selectedAppointment = allAppointmentsData.find(app => app.id == appointmentId);
        if (selectedAppointment) {
            showAppointmentDetail(selectedAppointment);
        }
    }

    // Hàm để điền các slot thời gian trống vào dropdown "Giờ hẹn"
    async function populateAvailableTimeSlots() {
        const selectedDoctorName = newDoctorNameSelect.value;
        const selectedDateStr = newAppointmentDateInput.value; // Dạng YYYY-MM-DD

        // Xóa các option cũ
        newAppointmentTimeSelect.innerHTML = '<option value="">-- Chọn giờ hẹn --</option>';

        if (!selectedDoctorName || !selectedDateStr) {
            return; // Không làm gì nếu chưa chọn bác sĩ hoặc ngày
        }

        const today = new Date();
        today.setHours(0, 0, 0, 0); // Đặt về đầu ngày để so sánh

        const selectedDate = new Date(selectedDateStr);
        selectedDate.setHours(0, 0, 0, 0); // Đặt về đầu ngày để so sánh

        // Kiểm tra ngày quá khứ
        if (selectedDate < today) {
            newAppointmentTimeSelect.innerHTML = '<option value="" disabled>Ngày đã qua</option>';
            return;
        }

        // Kiểm tra giới hạn ngày đặt trước
        const maxDate = new Date();
        maxDate.setDate(today.getDate() + MAX_BOOKING_DAYS_IN_ADVANCE);
        maxDate.setHours(0, 0, 0, 0);

        if (selectedDate > maxDate) {
            newAppointmentTimeSelect.innerHTML = `<option value="" disabled>Chỉ được đặt trước tối đa ${MAX_BOOKING_DAYS_IN_ADVANCE} ngày</option>`;
            return;
        }

        let bookedTimes = [];
        // Lọc các lịch hẹn ĐANG SẮP TỚI hoặc ĐANG CHỜ của bác sĩ được chọn vào ngày được chọn
        // Sử dụng dữ liệu giả lập từ allAppointmentsData
        const appointmentsOnSelectedDate = allAppointmentsData.filter(app => {
            const [day, month, year] = app.date.split('/');
            const appDateFormatted = `${year}-${month}-${day}`; // Chuyển đổi DD/MM/YYYY sang YYYY-MM-DD
            return app.doctorName === selectedDoctorName && appDateFormatted === selectedDateStr &&
                   (app.status === 'Sắp tới' || app.status === 'Đang chờ');
        });

        bookedTimes = appointmentsOnSelectedDate.map(app => app.time);

        console.log(`Các giờ đã đặt cho ${selectedDoctorName} vào ngày ${selectedDateStr}:`, bookedTimes);

        // Lấy thời gian hiện tại
        const now = new Date();
        const currentHour = now.getHours();
        const currentMinute = now.getMinutes();

        // Duyệt qua các slot mặc định để điền vào dropdown
        defaultTimeSlots.forEach(slot => {
            const option = document.createElement('option');
            option.value = slot;
            option.textContent = slot;

            let isDisabled = false;
            let reason = '';

            // Kiểm tra nếu slot đã bị đặt
            if (bookedTimes.includes(slot)) {
                isDisabled = true;
                reason = ' (Đã đầy)';
            }

            // Kiểm tra nếu ngày được chọn là hôm nay và giờ slot đã qua hoặc quá gần (theo MIN_BOOKING_LEAD_TIME_HOURS)
            if (selectedDateStr === `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`) {
                const [slotHourStr, slotMinuteStr] = slot.replace(/ AM| PM/, '').split(':');
                let slotHour = parseInt(slotHourStr);
                const isPM = slot.includes('PM');

                if (isPM && slotHour !== 12) { // 12 PM is 12, 1-11 PM is +12
                    slotHour += 12;
                }
                if (!isPM && slotHour === 12) { // 12 AM (midnight) is 0
                    slotHour = 0;
                }

                const slotTotalMinutes = slotHour * 60 + parseInt(slotMinuteStr);
                const currentTimeTotalMinutes = currentHour * 60 + currentMinute;

                // Tính thời gian còn lại (phút) từ bây giờ đến slot
                const minutesUntilSlot = slotTotalMinutes - currentTimeTotalMinutes;

                if (minutesUntilSlot < (MIN_BOOKING_LEAD_TIME_HOURS * 60)) {
                    isDisabled = true;
                    reason = ' (Không khả dụng)'; // Giờ đã qua hoặc quá sát
                }
            }

            if (isDisabled) {
                option.disabled = true;
                option.textContent += reason;
            }

            newAppointmentTimeSelect.appendChild(option);
        });
    }

    // Hàm chính để tải dữ liệu và cập nhật giao diện
    async function fetchAppointmentsAndRenderUI() {
        console.log("Đang tải dữ liệu lịch hẹn...");
        try {
            // === PHẦN NÀY LÀ NƠI BẠN SẼ GỌI API THỰC TẾ (Nếu có backend) ===
            /*
            const response = await fetch('/api/appointments/patient-appointments');
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            allAppointmentsData = await response.json();
            console.log("allAppointmentsData từ API:", JSON.parse(JSON.stringify(allAppointmentsData)));
            */
            // === KẾT THÚC PHẦN GỌI API ===

            // --- Tạm thời dùng dữ liệu mẫu VÀ lưu/tải từ LocalStorage (cho mục đích demo) ---
            const storedAppointments = localStorage.getItem('patientAppointmentsData');
            if (storedAppointments) {
                allAppointmentsData = JSON.parse(storedAppointments);
                console.log("allAppointmentsData từ LocalStorage:", JSON.parse(JSON.stringify(allAppointmentsData)));
            } else {
                // Nếu không có trong localStorage, sử dụng dữ liệu mẫu ban đầu
                allAppointmentsData = JSON.parse(JSON.stringify(sampleAppointments));
                console.log("allAppointmentsData từ sampleAppointments (lần đầu):", JSON.parse(JSON.stringify(allAppointmentsData)));
                // Lưu vào localStorage lần đầu để các thay đổi sau này được duy trì
                localStorage.setItem('patientAppointmentsData', JSON.stringify(allAppointmentsData));
            }
            // --- Hết phần dữ liệu mẫu + localStorage ---

            // Phân loại và sắp xếp lại dữ liệu sau khi tải
            upcomingAppointments = allAppointmentsData.filter(app =>
                app.status === 'Sắp tới' || app.status === 'Đang chờ'
            ).sort((a, b) => {
                // Chuyển đổi định dạng ngày DD/MM/YYYY sang YYYY-MM-DD để so sánh đúng
                const dateA = new Date(a.date.split('/').reverse().join('-'));
                const dateB = new Date(b.date.split('/').reverse().join('-'));
                return dateA - dateB;
            });
            // console.log("upcomingAppointments sau khi lọc:", JSON.parse(JSON.stringify(upcomingAppointments)));


            appointmentHistory = allAppointmentsData.filter(app =>
                app.status === 'Đã hoàn thành' || app.status === 'Đã hủy'
            ).sort((a, b) => {
                const dateA = new Date(a.date.split('/').reverse().join('-'));
                const dateB = new Date(b.date.split('/').reverse().join('-'));
                return dateB - dateA; // Lịch sử: mới nhất lên đầu
            });
            // console.log("appointmentHistory sau khi lọc:", JSON.parse(JSON.stringify(appointmentHistory)));

            // Sau khi có dữ liệu mới, cập nhật UI
            updateAppointmentsAndRenderUI();

        } catch (error) {
            console.error("Lỗi khi tải lịch hẹn:", error);
            // Hiển thị thông báo lỗi thân thiện
            showSuccessMessage("Không thể tải lịch hẹn của bạn. Vui lòng thử lại sau.");
        }
    }

    // Hàm cập nhật UI dựa trên dữ liệu đã có trong các biến `upcomingAppointments` và `appointmentHistory`
    function updateAppointmentsAndRenderUI() {
            const activeTabButton = document.querySelector('.tab-button.active');
        if (activeTabButton && activeTabButton.dataset.tabId === 'upcomingAppointmentsContent') {
            renderAppointments(upcomingAppointments, upcomingAppointmentsList, currentPageUpcoming, upcomingPagination);
        } else if (activeTabButton && activeTabButton.dataset.tabId === 'appointmentHistoryContent') {
            renderAppointments(appointmentHistory, appointmentHistoryList, currentPageHistory, historyPagination);
        } else {
            // Mặc định render tab sắp tới nếu không có tab nào active (ví dụ, lần đầu tải trang)
            renderAppointments(upcomingAppointments, upcomingAppointmentsList, currentPageUpcoming, upcomingPagination);
            // Đảm bảo nút tab tương ứng cũng active nếu lần đầu tải
            document.querySelector('.tab-button[data-tab-id="upcomingAppointmentsContent"]')?.classList.add('active');
            document.getElementById('upcomingAppointmentsContent')?.classList.add('active-tab-content');
        }
    }


    // === LOGIC XỬ LÝ SỰ KIỆN ===

    // Sidebar collapse
    const sidebar = document.getElementById('sidebar');
    const contentDiv = document.getElementById('content');
    const sidebarCollapse = document.getElementById('sidebarCollapse');

    if (sidebarCollapse && sidebar && contentDiv) {
        sidebarCollapse.addEventListener('click', function () {
            sidebar.classList.toggle('active');
            contentDiv.classList.toggle('active');
        });
    }

    // Tab Controls
    const tabButtons = document.querySelectorAll('.tab-button');
    const tabContents = document.querySelectorAll('.tab-content');

    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active-tab-content'));

            button.classList.add('active');
            const targetTabId = button.dataset.tabId;
            const targetContent = document.getElementById(targetTabId);
            if (targetContent) {
                targetContent.classList.add('active-tab-content');
            }
            // Gọi updateAppointmentsAndRenderUI để làm mới nội dung tab hiện tại
            updateAppointmentsAndRenderUI();
        });
    });

    // Gán sự kiện cho các nút trong modal chi tiết
    if (closeButton) {
        closeButton.onclick = function() {
            modal.style.display = "none";
        }
    }
    if (btnCloseDetail) {
        btnCloseDetail.onclick = function() {
            modal.style.display = "none";
        }
    }

    // XỬ LÝ NÚT "HỦY HẸN" (trong modal chi tiết) -> HIỆN MODAL XÁC NHẬN TÙY CHỈNH
    if (btnCancelAppointment) {
        btnCancelAppointment.onclick = function() {
            if (currentAppointmentIdToCancel !== null) {
                modal.style.display = "none"; // Đóng modal chi tiết
                confirmAppointmentIdDisplay.textContent = currentAppointmentIdToCancel;
                cancelConfirmModal.style.display = "flex"; // Hiển thị modal xác nhận
            }
        }
    }

    // LOGIC CHO MODAL XÁC NHẬN TÙY CHỈNH
    if (closeConfirmModalBtn) {
        closeConfirmModalBtn.onclick = function() {
            cancelConfirmModal.style.display = "none";
            currentAppointmentIdToCancel = null; // Reset biến nếu hủy bỏ
        }
    }

    if (cancelConfirmBtn) {
        cancelConfirmBtn.onclick = function() {
            cancelConfirmModal.style.display = "none";
            currentAppointmentIdToCancel = null; // Reset biến nếu hủy bỏ
        }
    }

    // Xử lý khi người dùng xác nhận hủy lịch hẹn
    if (confirmCancelBtn) {
        confirmCancelBtn.onclick = async function() { // Sử dụng async vì có thể có fetch API
            if (currentAppointmentIdToCancel !== null) {
                console.log(`Đang gửi yêu cầu hủy lịch hẹn ID: ${currentAppointmentIdToCancel} đến server...`);

                try {
                    // === THAY THẾ PHẦN NÀY BẰNG GỌI API HỦY THỰC TẾ CỦA BẠN ===
                    /*
                    const response = await fetch('/api/appointments/cancel', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({ appointmentId: currentAppointmentIdToCancel })
                    });

                    if (!response.ok) {
                        const errorData = await response.json();
                        throw new Error(errorData.message || 'Lỗi khi hủy lịch hẹn trên server.');
                    }
                    // const result = await response.json();
                    */
                    // === KẾT THÚC PHẦN GỌI API ===

                    // --- Giả lập hủy thành công và cập nhật dữ liệu mẫu + LocalStorage ---
                    await new Promise(resolve => setTimeout(resolve, 500)); // Giả lập độ trễ mạng 0.5s

                    const idToCancel = parseInt(currentAppointmentIdToCancel);
                    const indexInAllData = allAppointmentsData.findIndex(app => app.id === idToCancel);

                    if (indexInAllData !== -1) {
                        // Cập nhật trạng thái của lịch hẹn thành "Đã hủy" trong dữ liệu gốc
                        allAppointmentsData[indexInAllData].status = 'Đã hủy';
                        // RẤT QUAN TRỌNG: Lưu lại dữ liệu đã thay đổi vào localStorage
                        localStorage.setItem('patientAppointmentsData', JSON.stringify(allAppointmentsData));
                        console.log(`Lịch hẹn ID ${idToCancel} đã được cập nhật trạng thái trong allAppointmentsData và lưu vào LocalStorage.`);
                    } else {
                        console.warn(`Không tìm thấy lịch hẹn ID ${idToCancel} trong allAppointmentsData để cập nhật trạng thái.`);
                    }
                    // console.log("allAppointmentsData sau khi cập nhật trạng thái hủy:", JSON.parse(JSON.stringify(allAppointmentsData)));
                    // --- Hết phần giả lập ---

                    cancelConfirmModal.style.display = "none"; // Đóng modal xác nhận
                    showSuccessMessage(`Lịch hẹn ID ${currentAppointmentIdToCancel} đã được hủy thành công!`); // HIỆN THÔNG BÁO TÙY CHỈNH

                    // Sau khi hủy thành công, tải lại dữ liệu và cập nhật UI.
                    // Hàm `fetchAppointmentsAndRenderUI()` sẽ tự động đọc lại `allAppointmentsData`
                    // (từ localStorage), phân loại lại, và render lại.
                    currentPageUpcoming = 1; // Reset trang sắp tới về 1 để đảm bảo hiển thị đúng
                    fetchAppointmentsAndRenderUI(); // Gọi lại hàm chính để làm mới toàn bộ dữ liệu và UI

                } catch (error) {
                    console.error('Lỗi khi hủy lịch hẹn:', error);
                    // Hiển thị thông báo lỗi tùy chỉnh
                    showSuccessMessage(`Không thể hủy lịch hẹn: ${error.message || 'Đã xảy ra lỗi không xác định.'}`);
                    cancelConfirmModal.style.display = "none"; // Đóng modal xác nhận ngay cả khi lỗi
                } finally {
                    currentAppointmentIdToCancel = null; // Reset biến sau khi hoàn tất
                }
            }
        }
    }

    // LOGIC CHO MODAL THÔNG BÁO THÀNH CÔNG
    if (closeSuccessModalBtn) {
        closeSuccessModalBtn.onclick = function() {
            successMessageModal.style.display = "none";
        }
    }

    if (okSuccessModalBtn) {
        okSuccessModalBtn.onclick = function() {
            successMessageModal.style.display = "none";
        }
    }

    // === XỬ LÝ SỰ KIỆN CHO MODAL ĐẶT LỊCH HẸN MỚI (ĐÃ THÊM) ===

    // Nút đóng modal đặt lịch hẹn mới
    if (closeNewAppointmentModalBtn) {
        closeNewAppointmentModalBtn.onclick = function() {
            newAppointmentModal.style.display = "none";
        }
    }
    if (cancelNewAppointmentBtn) {
        cancelNewAppointmentBtn.onclick = function() {
            newAppointmentModal.style.display = "none";
        }
    }

    // Gán sự kiện thay đổi cho các trường Bác sĩ và Ngày để cập nhật slot giờ
    if (newDoctorNameSelect) {
        newDoctorNameSelect.addEventListener('change', populateAvailableTimeSlots);
    }
    if (newAppointmentDateInput) {
        newAppointmentDateInput.addEventListener('change', populateAvailableTimeSlots);
    }

    // Xử lý nút "Đặt Hẹn Mới" (hiển thị modal thay vì chuyển hướng)
    const btnNewAppointment = document.getElementById('btnNewAppointment');
    if (btnNewAppointment) {
        btnNewAppointment.addEventListener('click', () => {
            newAppointmentForm.reset(); // Xóa dữ liệu cũ nếu có
            newAppointmentModal.style.display = "flex"; // Hiển thị modal

            // Đặt ngày hiện tại làm giá trị mặc định cho input date
            const today = new Date();
            const year = today.getFullYear();
            const month = String(today.getMonth() + 1).padStart(2, '0'); // Tháng bắt đầu từ 0
            const day = String(today.getDate()).padStart(2, '0');
            const defaultDate = `${year}-${month}-${day}`;
            newAppointmentDateInput.value = defaultDate;

            // Đặt min/max cho input date để giới hạn chọn ngày
            newAppointmentDateInput.min = defaultDate; // Không cho chọn ngày quá khứ
            const maxDate = new Date();
            maxDate.setDate(today.getDate() + MAX_BOOKING_DAYS_IN_ADVANCE);
            const maxYear = maxDate.getFullYear();
            const maxMonth = String(maxDate.getMonth() + 1).padStart(2, '0');
            const maxDay = String(maxDate.getDate()).padStart(2, '0');
            newAppointmentDateInput.max = `${maxYear}-${maxMonth}-${maxDay}`;

            // Gọi hàm populate ngay khi modal hiện ra và ngày được đặt
            populateAvailableTimeSlots();
        });
    }

    // Xử lý khi form đặt lịch hẹn mới được gửi
    if (newAppointmentForm) {
        newAppointmentForm.addEventListener('submit', async function(event) {
            event.preventDefault(); // Ngăn chặn form gửi đi theo cách truyền thống

            const doctorName = newDoctorNameSelect.value;
            const service = document.getElementById('newService').value;
            const appointmentDateStr = newAppointmentDateInput.value; // Dạng YYYY-MM-DD
            const appointmentTime = newAppointmentTimeSelect.value; // Lấy từ <select>
            const notes = document.getElementById('newNotes').value;

            // Basic Validation
            if (!doctorName || !service || !appointmentDateStr || !appointmentTime) {
                showSuccessMessage("Vui lòng điền đầy đủ các thông tin bắt buộc (Bác sĩ, Dịch vụ, Ngày, Giờ).");
                return;
            }

            // Kiểm tra lại slot có khả dụng không (phòng trường hợp người dùng chọn rồi nhưng slot bị đầy do cập nhật nhanh)
            const selectedOption = newAppointmentTimeSelect.options[newAppointmentTimeSelect.selectedIndex];
            if (selectedOption && selectedOption.disabled) {
                showSuccessMessage("Giờ hẹn bạn chọn đã bị đầy hoặc không khả dụng. Vui lòng chọn giờ khác.");
                // Không reset nút submit ở đây, vì showSuccessMessage sẽ đóng và người dùng có thể thử lại
                return; // Ngừng quá trình gửi form
            }

            // Chuyển đổi định dạng ngày từ YYYY-MM-DD sang DD/MM/YYYY để phù hợp với dữ liệu mẫu
            const [year, month, day] = appointmentDateStr.split('-');
            const formattedDate = `${day}/${month}/${year}`;

            // Tạo ID mới. Trong hệ thống thực tế, ID sẽ do backend tạo.
            // Ở đây, giả lập ID lớn nhất + 1.
            const newId = allAppointmentsData.length > 0 ? Math.max(...allAppointmentsData.map(app => app.id)) + 1 : 1;

            const newAppointmentData = {
                id: newId,
                doctorName: doctorName,
                date: formattedDate,
                time: appointmentTime,
                service: service,
                status: "Sắp tới", // Mặc định là 'Sắp tới' cho lịch hẹn mới
                notes: notes
            };

            console.log("Dữ liệu đặt lịch hẹn mới:", newAppointmentData);

            // Tắt nút submit để tránh gửi nhiều lần
            submitNewAppointmentBtn.disabled = true;
            submitNewAppointmentBtn.textContent = 'Đang xử lý...';

            try {
                // --- Giả lập đặt lịch thành công và cập nhật dữ liệu mẫu + LocalStorage ---
                await new Promise(resolve => setTimeout(resolve, 1500)); // Giả lập độ trễ mạng

                // Thêm lịch hẹn mới vào dữ liệu gốc (allAppointmentsData)
                allAppointmentsData.push(newAppointmentData);
                localStorage.setItem('patientAppointmentsData', JSON.stringify(allAppointmentsData));
                console.log("Lịch hẹn mới đã được thêm vào allAppointmentsData và lưu vào LocalStorage.");
                // --- Hết phần giả lập ---

                newAppointmentModal.style.display = "none"; // Đóng modal đặt lịch
                newAppointmentForm.reset(); // Reset form
                showSuccessMessage(`Bạn đã đặt lịch hẹn thành công! Lịch hẹn với ${newAppointmentData.doctorName} vào ngày ${newAppointmentData.date} đã được thêm. Mã: ${newAppointmentData.id}`);

                // Sau khi đặt thành công, tải lại dữ liệu và cập nhật UI.
                currentPageUpcoming = 1; // Reset trang sắp tới về 1
                fetchAppointmentsAndRenderUI(); // Gọi lại hàm chính để làm mới toàn bộ dữ liệu và UI

            } catch (error) {
                console.error('Lỗi khi đặt lịch hẹn:', error);
                showSuccessMessage(`Không thể đặt lịch hẹn: ${error.message || 'Đã xảy ra lỗi không xác định.'}`);
            } finally {
                submitNewAppointmentBtn.disabled = false;
                submitNewAppointmentBtn.textContent = 'Đặt Lịch';
            }
        });
    }

    // Đóng bất kỳ modal nào khi click vào vùng tối bên ngoài modal
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
        if (event.target == cancelConfirmModal) {
            cancelConfirmModal.style.display = "none";
        }
        if (event.target == successMessageModal) {
            successMessageModal.style.display = "none";
        }
        if (event.target == newAppointmentModal) { // Thêm dòng này để đóng modal đặt lịch hẹn mới
            newAppointmentModal.style.display = "none";
        }
    }

    // === KHỞI TẠO TRANG KHI DOM ĐÃ TẢI XONG ===
    fetchAppointmentsAndRenderUI(); // Tải dữ liệu và render UI lần đầu tiên
});