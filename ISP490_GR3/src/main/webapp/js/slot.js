document.addEventListener('DOMContentLoaded', function () {
    const sidebarToggle = document.getElementById('sidebarCollapse');
    const sidebar = document.getElementById('sidebar');
    const content = document.getElementById('content'); // Sửa thành 'content'
    const topNavbar = document.querySelector('.top-navbar');

    if (sidebarToggle && sidebar && content && topNavbar) {
        sidebarToggle.addEventListener('click', function () {
            console.log('Sidebar toggle button clicked!');
            sidebar.classList.toggle('collapsed');
            content.classList.toggle('expanded');
        });
    } else {
        console.log("Một hoặc nhiều phần tử không tồn tại:", { sidebarToggle, sidebar, content, topNavbar });
    }

    const RESPONSIVE_BREAKPOINT = 768;
    function checkWidth() {
        if (window.innerWidth <= RESPONSIVE_BREAKPOINT) {
            if (sidebar && content && topNavbar) {
                sidebar.classList.add('collapsed');
                content.classList.add('expanded');
            }
        } else {
            if (sidebar && content && topNavbar) {
                sidebar.classList.remove('collapsed');
                content.classList.remove('expanded');
            }
        }
    }

    if (sidebar && content && topNavbar) {
        checkWidth();
        window.addEventListener('resize', checkWidth);
    }

    // Bỏ qua các logic không liên quan (dropdown, toast, delete, update) vì không có trong JSP
});
