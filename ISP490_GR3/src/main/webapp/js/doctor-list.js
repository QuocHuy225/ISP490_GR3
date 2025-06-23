/* global doctorServerData */

// src/main/webapp/js/doctor-list.js
document.addEventListener('DOMContentLoaded', () => {
    // Lấy dữ liệu từ server-side (doctorServerData được định nghĩa trong JSP)
    const totalDoctors = doctorServerData.totalDoctors;
    let currentPage = doctorServerData.currentPage;
    let itemsPerPage = doctorServerData.itemsPerPage;
    const searchQuery = doctorServerData.searchQuery;
    const contextPath = window.APP_CONTEXT_PATH || ''; // Đảm bảo contextPath được định nghĩa

    const itemsPerPageSelect = document.getElementById('itemsPerPageSelectDoctor');
    const doctorPaginationNav = document.getElementById('doctorPaginationNav');
    const doctorPaginationSummary = document.getElementById('doctorPaginationSummary');

    function updatePagination() {
        const totalPages = Math.ceil(totalDoctors / itemsPerPage);

        // Cập nhật summary
        const startItem = (currentPage - 1) * itemsPerPage + 1;
        const endItem = Math.min(currentPage * itemsPerPage, totalDoctors);
        doctorPaginationSummary.textContent = `Hiển thị kết quả ${startItem} - ${endItem} trên tổng ${totalDoctors} kết quả`;

        // Tạo pagination links
        doctorPaginationNav.innerHTML = ''; // Xóa các link cũ

        // Previous button
        const prevLi = document.createElement('li');
        prevLi.classList.add('page-item');
        if (currentPage === 1) prevLi.classList.add('disabled');
        const prevLink = document.createElement('a');
        prevLink.classList.add('page-link');
        prevLink.href = `${contextPath}/doctors?search=${searchQuery}&page=${currentPage - 1}&limit=${itemsPerPage}`;
        prevLink.setAttribute('aria-label', 'Previous');
        prevLink.innerHTML = '<span aria-hidden="true">&laquo;</span>';
        prevLi.appendChild(prevLink);
        doctorPaginationNav.appendChild(prevLi);

        // Page numbers
        for (let i = 1; i <= totalPages; i++) {
            const pageLi = document.createElement('li');
            pageLi.classList.add('page-item');
            if (currentPage === i) pageLi.classList.add('active');
            const pageLink = document.createElement('a');
            pageLink.classList.add('page-link');
            pageLink.href = `${contextPath}/doctors?search=${searchQuery}&page=${i}&limit=${itemsPerPage}`;
            pageLink.textContent = i;
            pageLi.appendChild(pageLink);
            doctorPaginationNav.appendChild(pageLi);
        }

        // Next button
        const nextLi = document.createElement('li');
        nextLi.classList.add('page-item');
        if (currentPage === totalPages) nextLi.classList.add('disabled');
        const nextLink = document.createElement('a');
        nextLink.classList.add('page-link');
        nextLink.href = `${contextPath}/doctors?search=${searchQuery}&page=${currentPage + 1}&limit=${itemsPerPage}`;
        nextLink.setAttribute('aria-label', 'Next');
        nextLink.innerHTML = '<span aria-hidden="true">&raquo;</span>';
        nextLi.appendChild(nextLink);
        doctorPaginationNav.appendChild(nextLi);
    }

    // Xử lý sự kiện khi thay đổi số lượng mục mỗi trang
    if (itemsPerPageSelect) {
        itemsPerPageSelect.addEventListener('change', (event) => {
            itemsPerPage = parseInt(event.target.value);
            // Chuyển hướng đến trang 1 với số lượng mục mới
            window.location.href = `${contextPath}/doctors?search=${searchQuery}&page=1&limit=${itemsPerPage}`;
        });
    }

    // Xử lý nút "Đặt khám" trên doctor-list.jsp
    const bookDoctorBtns = document.querySelectorAll('.book-doctor-btn');
    bookDoctorBtns.forEach(btn => {
        btn.addEventListener('click', (event) => {
            const doctorId = btn.dataset.doctorId || 'unknown';
            window.location.href = `${contextPath}/book-appointment?doctorId=${doctorId}`;
        });
    });

    // Gọi hàm updatePagination khi DOM đã tải xong
    updatePagination();
});