document.addEventListener('DOMContentLoaded', () => {
    const sidebarToggle = document.getElementById('sidebarCollapse');
    const sidebar = document.getElementById('sidebar');
    const mainWrapper = document.getElementById('main-wrapper');
    let sidebarOverlay = document.getElementById('sidebarOverlay');

    if (!sidebarOverlay) {
        sidebarOverlay = document.createElement('div');
        sidebarOverlay.id = 'sidebarOverlay';
        sidebarOverlay.classList.add('sidebar-overlay');
        document.body.appendChild(sidebarOverlay);
    }

    const RESPONSIVE_BREAKPOINT = 991.98;

    function toggleSidebar() {
        if (window.innerWidth <= RESPONSIVE_BREAKPOINT) {
            sidebar.classList.toggle('active');
            sidebarOverlay.classList.toggle('active');
            document.body.classList.toggle('no-scroll', sidebar.classList.contains('active'));
        } else {
            sidebar.classList.remove('active');
            sidebar.classList.toggle('collapsed');
            mainWrapper.classList.toggle('expanded');
            sidebarOverlay.classList.remove('active');
            document.body.classList.remove('no-scroll');
        }
    }

    if (sidebarToggle && sidebar && mainWrapper && sidebarOverlay) {
        sidebarToggle.addEventListener('click', toggleSidebar);
        sidebarOverlay.addEventListener('click', () => {
            if (window.innerWidth <= RESPONSIVE_BREAKPOINT && sidebar.classList.contains('active')) {
                toggleSidebar();
            }
        });
    }

    function checkWidthAndAdjustSidebar() {
        if (sidebar && mainWrapper && sidebarOverlay) {
            if (window.innerWidth <= RESPONSIVE_BREAKPOINT) {
                sidebar.classList.remove('active');
                sidebar.classList.add('collapsed');
                mainWrapper.classList.remove('expanded');
                sidebarOverlay.classList.remove('active');
                document.body.classList.remove('no-scroll');
            } else {
                sidebar.classList.remove('active');
                sidebar.classList.remove('collapsed');
                mainWrapper.classList.remove('expanded');
                sidebarOverlay.classList.remove('active');
                document.body.classList.remove('no-scroll');
            }
        }
    }

    checkWidthAndAdjustSidebar();
    window.addEventListener('resize', checkWidthAndAdjustSidebar);

    const userDropdown = document.getElementById('userDropdown');
    if (userDropdown) {
        userDropdown.addEventListener('show.bs.dropdown', function () {
            const dropdownMenu = userDropdown.nextElementSibling;
            if (dropdownMenu) {
                dropdownMenu.style.opacity = '0';
                dropdownMenu.style.transform = 'translateY(-10px)';
                setTimeout(() => {
                    dropdownMenu.style.transition = 'all 0.3s ease';
                    dropdownMenu.style.opacity = '1';
                    dropdownMenu.style.transform = 'translateY(0)';
                }, 10);
            }
        });

        userDropdown.addEventListener('hidden.bs.dropdown', function () {
            const dropdownMenu = userDropdown.nextElementSibling;
            if (dropdownMenu) {
                dropdownMenu.style.transition = '';
                dropdownMenu.style.opacity = '';
                dropdownMenu.style.transform = '';
            }
        });
    }

    const sidebarMenuItems = document.querySelectorAll('#sidebar ul.components li');
    const contextPath = window.APP_CONTEXT_PATH || '';

    function normalizePath(path) {
        let cleanedPath = path.replace(contextPath, '');
        if (cleanedPath.startsWith('/')) {
            cleanedPath = cleanedPath.substring(1);
        }
        if (cleanedPath.endsWith('/')) {
            cleanedPath = cleanedPath.slice(0, -1);
        }
        if (cleanedPath === '') {
            return 'homepage';
        }
        return cleanedPath;
    }

    function updateSidebarActiveState() {
        const currentNormalizedPath = normalizePath(window.location.pathname);
        sidebarMenuItems.forEach(item => {
            item.classList.remove('active');
        });

        let foundActive = false;
        sidebarMenuItems.forEach(item => {
            if (foundActive) return;
            const link = item.querySelector('a');
            if (link) {
                const linkNormalizedPath = normalizePath(new URL(link.href).pathname);
                if (currentNormalizedPath === linkNormalizedPath) {
                    item.classList.add('active');
                    foundActive = true;
                    return;
                }
                if (linkNormalizedPath !== 'homepage' && currentNormalizedPath.startsWith(linkNormalizedPath + '/')) {
                    item.classList.add('active');
                    foundActive = true;
                    return;
                }
            }
        });
    }

    updateSidebarActiveState();

    sidebarMenuItems.forEach(item => {
        const link = item.querySelector('a');
        if (link) {
            link.addEventListener('click', function(event) {
                // Kiểm tra xem đây có phải là một liên kết điều hướng thực sự không
                // Nếu là '#', nó có thể là một dropdown toggle hoặc một placeholder
                if (this.getAttribute('href') === '#') {
                    // event.preventDefault(); // Giữ nguyên hành vi mặc định nếu là dropdown
                } else {
                    sidebarMenuItems.forEach(li => li.classList.remove('active'));
                    item.classList.add('active');
                }
                if (window.innerWidth <= RESPONSIVE_BREAKPOINT && sidebar.classList.contains('active')) {
                    toggleSidebar();
                }
            });
        }
    });

    const headerNavLinks = document.querySelectorAll('.custom-nav-pills .nav-link');
    headerNavLinks.forEach(link => {
        link.addEventListener('click', function(event) {
            if (this.getAttribute('href') === '#') {
                event.preventDefault();
            }
            headerNavLinks.forEach(navLink => navLink.classList.remove('active'));
            this.classList.add('active');
            console.log(`Navigating to: ${this.textContent}`);
        });
    });

    const doctorListScroll = document.querySelector('.doctor-list-scroll');
    const scrollRightBtn = document.querySelector('.doctor-scroll-right');
    if (scrollRightBtn && doctorListScroll) {
        scrollRightBtn.addEventListener('click', () => {
            const scrollAmount = doctorListScroll.offsetWidth * 0.8;
            doctorListScroll.scrollBy({
                left: scrollAmount,
                behavior: 'smooth'
            });
        });
    }

    const viewAllBtn = document.querySelector('.view-all-btn');
    if (viewAllBtn) {
        viewAllBtn.addEventListener('click', () => {
            // Thay đổi URL chuyển hướng sang /doctors để xem tất cả bác sĩ
            window.location.href = `${contextPath}/doctors`;
        });
    }

    const bookNowBtns = document.querySelectorAll('.book-now-btn');
    bookNowBtns.forEach(btn => {
        btn.addEventListener('click', (event) => {
            const card = event.target.closest('.doctor-card');
            // const doctorName = card.querySelector('.card-title').textContent; // Có thể không cần nếu không dùng
            const doctorId = btn.dataset.doctorId || 'unknown';
            window.location.href = `${contextPath}/book-appointment?doctorId=${doctorId}`;
        });
    });
});