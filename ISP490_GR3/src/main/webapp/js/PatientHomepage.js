// Đảm bảo APP_CONTEXT_PATH được định nghĩa trong JSP trước khi script này chạy,
// ví dụ: <script>window.APP_CONTEXT_PATH = '<%= request.getContextPath() %>';</script>
const contextPath = window.APP_CONTEXT_PATH || '';

// Lấy tất cả các mục menu trong sidebar
const sidebarMenuItems = document.querySelectorAll('#sidebar ul.components li');

// Hàm trợ giúp để chuẩn hóa đường dẫn để so sánh nhất quán
function normalizePath(path) {
    // Xóa context path (ví dụ: '/mywebapp/homepage' -> '/homepage')
    let cleanedPath = path.replace(contextPath, '');

    // Xóa dấu gạch chéo đầu (ví dụ: '/homepage' -> 'homepage')
    if (cleanedPath.startsWith('/')) {
        cleanedPath = cleanedPath.substring(1);
    }
    // Xóa dấu gạch chéo cuối (ví dụ: 'homepage/' -> 'homepage')
    if (cleanedPath.endsWith('/')) {
        cleanedPath = cleanedPath.slice(0, -1);
    }

    // Xử lý đặc biệt cho đường dẫn gốc/trống: nếu trống sau khi làm sạch, coi là 'homepage'
    // Điều này đảm bảo mục menu 'homepage' khớp với URL gốc.
    if (cleanedPath === '') {
        return 'homepage'; // Chuẩn hóa gốc thành 'homepage'
    }

    return cleanedPath;
}

// Hàm cập nhật trạng thái active của sidebar
function updateSidebarActiveState() {
    const currentPathname = window.location.pathname;
    const currentNormalizedPath = normalizePath(currentPathname);
    console.log("Current Browser Pathname:", currentPathname);
    console.log("Current Normalized Path:", currentNormalizedPath);

    // Bước 1: Xóa class 'active' khỏi TẤT CẢ các mục trước
    sidebarMenuItems.forEach(item => {
        item.classList.remove('active');
    });

    let activeItemFound = false;

    // Bước 2: Tìm và đặt 'active' cho mục phù hợp nhất
    // Ưu tiên khớp chính xác với URL trình duyệt từ các liên kết có href thực sự
    for (let i = 0; i < sidebarMenuItems.length; i++) {
        const item = sidebarMenuItems[i];
        const link = item.querySelector('a');

        if (link) {
            const hrefValue = link.getAttribute('href');

            // Bỏ qua các liên kết chỉ có '#' trong bước này để ưu tiên các URL thực
            if (hrefValue === '#') {
                continue;
            }

            let linkUrl;
            try {
                // Tạo URL tuyệt đối để trích xuất pathname một cách đáng tin cậy
                linkUrl = new URL(hrefValue, window.location.origin + contextPath);
            } catch (e) {
                console.error("Invalid URL in sidebar link:", hrefValue, e);
                continue;
            }

            const linkNormalizedPath = normalizePath(linkUrl.pathname);
            console.log(`Checking link "${link.textContent.trim()}" (href: ${hrefValue}) -> Normalized: ${linkNormalizedPath}`);


            // Khớp chính xác hoàn toàn (ví dụ: /homepage khớp với /homepage)
            if (currentNormalizedPath === linkNormalizedPath) {
                item.classList.add('active');
                activeItemFound = true;
                console.log(`Active: Exact match for "${link.textContent.trim()}"`);
                break; // Tìm thấy khớp chính xác, thoát vòng lặp
            }

            // Khớp đường dẫn con (ví dụ: /admin/users khớp với /admin)
            // Chỉ áp dụng nếu đường dẫn chuẩn hóa của link không phải là 'homepage'
            // để tránh các trường hợp như /homepage/something khiến homepage active không mong muốn
            if (linkNormalizedPath !== 'homepage' && currentNormalizedPath.startsWith(linkNormalizedPath + '/')) {
                 item.classList.add('active');
                 activeItemFound = true;
                 console.log(`Active: Partial match (parent path) for "${link.textContent.trim()}"`);
                 break; // Tìm thấy khớp đường dẫn con, thoát vòng lặp
            }
        }
    }

    // Bước 3: Nếu không tìm thấy mục nào active từ các URL thực,
    // đảm bảo mục "Trang chủ" được active nếu chúng ta đang ở URL gốc hoặc /homepage
    if (!activeItemFound && currentNormalizedPath === 'homepage') {
        sidebarMenuItems.forEach(item => {
            const link = item.querySelector('a');
            if (link && normalizePath(new URL(link.href, window.location.origin + contextPath).pathname) === 'homepage') {
                item.classList.add('active');
                activeItemFound = true;
                console.log("Active: Fallback to 'Trang chủ' as no other match found.");
                // Không break ở đây để đảm bảo vòng lặp hoàn thành, nhưng activeItemFound sẽ ngăn các xử lý khác
            }
        });
    }

    // Bước 4 (Tùy chọn): Xử lý các liên kết href="#" nếu bạn muốn chúng active trong một số trường hợp cụ thể.
    // Nếu bạn muốn một liên kết href="#" được active, bạn phải có một logic riêng biệt để xác định khi nào.
    // Ví dụ: dựa trên một số trạng thái UI, hoặc một data attribute trên chính liên kết đó.
    // HIỆN TẠI, CÁC LIÊN KẾT HREF="#" SẼ CHỈ ĐƯỢC ACTIVE KHI NGƯỜI DÙNG CLICK VÀO CHÚNG (xem event listener bên dưới).
    // Chúng không tự động active khi tải trang trừ khi URL của trang hiện tại trùng khớp.
}


// Thêm event listener cho mỗi mục menu để xử lý click và cập nhật active
sidebarMenuItems.forEach(item => {
    const link = item.querySelector('a');
    if (link) {
        link.addEventListener('click', function(event) {
            // Xóa class 'active' khỏi tất cả các mục trước
            sidebarMenuItems.forEach(li => {
                li.classList.remove('active');
            });
            // Thêm class 'active' vào mục được click
            item.classList.add('active');

            // Nếu liên kết là '#' thì ngăn hành vi mặc định của trình duyệt để tránh nhảy lên đầu trang
            if (this.getAttribute('href') === '#') {
                event.preventDefault();
            }

            // Nếu đây là liên kết thực, sau khi click, nó sẽ tải trang mới và updateSidebarActiveState sẽ chạy lại
            // Nếu đây là liên kết hash/javascript, chúng ta đã cập nhật trạng thái active thủ công
        });
    }
});

// Chạy hàm cập nhật trạng thái active khi DOM đã tải hoàn chỉnh
document.addEventListener('DOMContentLoaded', updateSidebarActiveState);

// Chạy hàm cập nhật trạng thái active mỗi khi lịch sử trình duyệt thay đổi (ví dụ: nút back/forward)
// Điều này hữu ích cho các ứng dụng SPA hoặc khi sử dụng history.pushState
window.addEventListener('popstate', updateSidebarActiveState);