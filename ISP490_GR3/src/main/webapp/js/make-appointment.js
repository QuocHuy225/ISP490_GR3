document.addEventListener('DOMContentLoaded', () => {

    const sidebarToggle = document.getElementById('sidebarCollapse');
    const sidebar = document.getElementById('sidebar');
    const mainWrapper = document.getElementById('main-wrapper');
    let sidebarOverlay = document.getElementById('sidebarOverlay');

    // Create sidebar overlay if it doesn't exist (fallback, ideally it's in JSP)
    if (!sidebarOverlay) {
        sidebarOverlay = document.createElement('div');
        sidebarOverlay.id = 'sidebarOverlay';
        sidebarOverlay.classList.add('sidebar-overlay');
        document.body.appendChild(sidebarOverlay);
    }

    const RESPONSIVE_BREAKPOINT = 991.98; // Match CSS breakpoint (Bootstrap 'lg')

    // Function to toggle sidebar state (for both desktop and mobile)
    function toggleSidebar() {
        if (window.innerWidth <= RESPONSIVE_BREAKPOINT) {
            // Mobile: Toggle 'active' class for off-canvas effect
            sidebar.classList.toggle('active');
            sidebarOverlay.classList.toggle('active'); // Show/hide overlay
            // Prevent scrolling of body when sidebar is open on mobile
            document.body.classList.toggle('no-scroll', sidebar.classList.contains('active'));
        } else {
            // Desktop: Toggle 'collapsed' class
            sidebar.classList.remove('active'); // Ensure mobile active is off on desktop
            sidebar.classList.toggle('collapsed');
            mainWrapper.classList.toggle('expanded');
            // Ensure overlay is hidden and body can scroll in desktop mode
            sidebarOverlay.classList.remove('active');
            document.body.classList.remove('no-scroll');
        }
    }

    if (sidebarToggle && sidebar && mainWrapper && sidebarOverlay) {
        sidebarToggle.addEventListener('click', toggleSidebar);

        // Close sidebar when clicking on overlay (mobile only)
        sidebarOverlay.addEventListener('click', () => {
            if (window.innerWidth <= RESPONSIVE_BREAKPOINT && sidebar.classList.contains('active')) {
                toggleSidebar(); // Close sidebar
            }
        });
    }

    // Function to check width and adjust sidebar state initially and on resize
    function checkWidthAndAdjustSidebar() {
        if (sidebar && mainWrapper && sidebarOverlay) {
            if (window.innerWidth <= RESPONSIVE_BREAKPOINT) {
                // On mobile/tablet, ensure sidebar is initially hidden (off-canvas)
                sidebar.classList.remove('active'); // Ensure mobile active is off by default
                sidebar.classList.add('collapsed'); // Use 'collapsed' to hide off-screen (desktop uses it differently)
                mainWrapper.classList.remove('expanded'); // Main content is not expanded relative to desktop collapsed sidebar
                sidebarOverlay.classList.remove('active'); // Ensure overlay is hidden
                document.body.classList.remove('no-scroll'); // Ensure body can scroll
            } else {
                // On desktop, ensure sidebar is open by default, but toggleable to collapsed
                sidebar.classList.remove('active'); // Remove mobile active class
                sidebar.classList.remove('collapsed'); // Sidebar is open by default
                mainWrapper.classList.remove('expanded'); // Main content respects sidebar's margin
                sidebarOverlay.classList.remove('active'); // Ensure overlay is hidden
                document.body.classList.remove('no-scroll');
            }
        }
    }

    // Initial check and attach resize listener
    checkWidthAndAdjustSidebar();
    window.addEventListener('resize', checkWidthAndAdjustSidebar);

    // Enhanced dropdown animations (using Bootstrap events for custom animation)
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
                // Reset styles after animation to avoid conflicts with Bootstrap's own behavior or subsequent displays
                dropdownMenu.style.transition = '';
                dropdownMenu.style.opacity = '';
                dropdownMenu.style.transform = '';
            }
        });
    }

    // ===========================================================
    // CRITICAL: SIDEBAR MENU ACTIVE STATE MANAGEMENT
    // This logic ensures only one menu item is active at a time.
    // ===========================================================
    const sidebarMenuItems = document.querySelectorAll('#sidebar ul.components li');
    const contextPath = window.APP_CONTEXT_PATH || ''; // Get context path from global variable

    // Helper function to normalize paths for consistent comparison
    function normalizePath(path) {
        // Remove context path (e.g., '/mywebapp/homepage' -> '/homepage')
        let cleanedPath = path.replace(contextPath, '');
        
        // Remove leading slash (e.g., '/homepage' -> 'homepage')
        if (cleanedPath.startsWith('/')) {
            cleanedPath = cleanedPath.substring(1);
        }
        // Remove trailing slash (e.g., 'homepage/' -> 'homepage')
        if (cleanedPath.endsWith('/')) {
            cleanedPath = cleanedPath.slice(0, -1);
        }
        
        // Special handling for root/empty path: if it's empty after cleaning, treat as 'homepage'
        // This makes sure our 'homepage' menu item matches the root URL.
        if (cleanedPath === '') {
            return 'homepage'; // Standardize root as 'homepage'
        }
        return cleanedPath;
    }

    function updateSidebarActiveState() {
        const currentNormalizedPath = normalizePath(window.location.pathname);
        
        // Step 1: Remove 'active' from ALL items
        sidebarMenuItems.forEach(item => {
            item.classList.remove('active');
        });

        // Step 2: Iterate and apply 'active' to the single matching item
        let foundActive = false; // Flag to ensure only one is set active
        sidebarMenuItems.forEach(item => {
            if (foundActive) return; // If an active item is already found, skip others

            const link = item.querySelector('a');
            if (link) {
                const linkNormalizedPath = normalizePath(new URL(link.href).pathname);
                
                // Prioritize exact match
                if (currentNormalizedPath === linkNormalizedPath) {
                    item.classList.add('active');
                    foundActive = true; // Mark as found
                    return; 
                } 
                
                // Fallback for sub-paths: if the current path starts with the link's normalized path
                // This is useful if you have /makeappointments/step1 and want /makeappointments to be active.
                // Ensure linkNormalizedPath is not 'homepage' to avoid unintended matches with root.
                if (linkNormalizedPath !== 'homepage' && currentNormalizedPath.startsWith(linkNormalizedPath + '/')) {
                     item.classList.add('active');
                     foundActive = true; // Mark as found
                     return; 
                }
            }
        });
    }

    // 1. Run on initial load to set active state based on current URL
    updateSidebarActiveState();

    // 2. Add click listeners to update active state immediately
    sidebarMenuItems.forEach(item => {
        const link = item.querySelector('a');
        if (link) {
            link.addEventListener('click', function(event) {
                // For links with href="#", prevent default navigation
                if (this.getAttribute('href') === '#') {
                    event.preventDefault();
                    // Manually update active state for hash links as page won't reload
                    sidebarMenuItems.forEach(li => {
                        li.classList.remove('active');
                    });
                    item.classList.add('active');
                } else {
                    // For actual navigation links, allow default behavior (page reload).
                    // We still set it immediately for a better UX (visual feedback before reload).
                    // The updateSidebarActiveState() will run again on DOMContentLoaded
                    // of the new page to re-confirm and correctly set the active state.
                    sidebarMenuItems.forEach(li => {
                        li.classList.remove('active');
                    });
                    item.classList.add('active');
                }

                // If on mobile, close the sidebar after clicking a menu item
                if (window.innerWidth <= RESPONSIVE_BREAKPOINT && sidebar.classList.contains('active')) {
                    toggleSidebar(); 
                }
            });
        }
    });

    // ===========================================================
    // EXISTING LOGIC FOR HEADER NAVIGATION TABS (Unchanged)
    // ===========================================================
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

    // ===========================================================
    // DOCTOR LIST SCROLL AND BUTTONS (Unchanged)
    // ===========================================================
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
            alert('Chuyển đến trang xem tất cả bác sĩ!');
        });
    }

    const bookNowBtns = document.querySelectorAll('.book-now-btn');
    bookNowBtns.forEach(btn => {
        btn.addEventListener('click', (event) => {
            const card = event.target.closest('.doctor-card');
            const doctorName = card.querySelector('.card-title').textContent;
            alert(`Đặt lịch hẹn cho ${doctorName}! (Chuyển đến trang chi tiết đặt lịch)`);
        });
    });
});