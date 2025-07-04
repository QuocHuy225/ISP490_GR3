# Hướng dẫn sử dụng chức năng Quên mật khẩu

## Cài đặt

### 1. Cập nhật Database Schema
Chạy script SQL để thêm các cột cần thiết cho chức năng reset password:

```sql
-- Chạy file database_update_reset_password.sql
```

### 2. Cấu hình Email
Trong file `src/main/java/com/mycompany/isp490_gr3/service/EmailService.java`, cập nhật thông tin email:

```java
private static final String SENDER_EMAIL = "your-email@gmail.com";
private static final String SENDER_PASSWORD = "your-app-password";
```

**Lưu ý:** Sử dụng App Password của Gmail, không phải mật khẩu thường.

## Cách sử dụng

### 1. Quên mật khẩu từ trang đăng nhập

1. Truy cập trang chủ của ứng dụng
2. Nhấp vào nút "Đăng nhập"
3. Trong modal đăng nhập, nhấp vào link "Quên mật khẩu?"
4. Nhập email đã đăng ký
5. Nhấp "Gửi link khôi phục"

### 2. Kiểm tra email

1. Mở email đã nhập
2. Tìm email từ "Ánh Dương Clinic" với tiêu đề "Khôi phục mật khẩu"
3. Nhấp vào nút "Khôi phục mật khẩu" hoặc copy link vào trình duyệt

### 3. Đặt mật khẩu mới

1. Trang sẽ tự động mở modal "Đặt lại mật khẩu"
2. Nhập mật khẩu mới (ít nhất 6 ký tự)
3. Xác nhận mật khẩu mới
4. Nhấp "Đặt lại mật khẩu"

### 4. Đăng nhập với mật khẩu mới

1. Sau khi đặt lại thành công, hệ thống sẽ chuyển về trang đăng nhập
2. Sử dụng mật khẩu mới để đăng nhập

## Bảo mật

- Link reset password có hiệu lực trong **30 phút**
- Mỗi link chỉ sử dụng được **một lần**
- Token được tạo ngẫu nhiên và lưu trữ an toàn trong database
- Sau khi reset thành công, token sẽ bị xóa khỏi database

## Xử lý lỗi

### Email không tồn tại
- Hệ thống sẽ hiển thị thông báo "Không tìm thấy tài khoản với email này"

### Link hết hạn
- Hệ thống sẽ hiển thị thông báo "Link khôi phục mật khẩu không hợp lệ hoặc đã hết hạn"
- Người dùng cần yêu cầu reset password mới

### Lỗi gửi email
- Kiểm tra cấu hình SMTP trong EmailService
- Đảm bảo App Password Gmail đúng
- Kiểm tra kết nối internet

## URLs

- **Quên mật khẩu:** `POST /auth/forgot-password`
- **Reset mật khẩu (GET):** `GET /auth/reset-password?token=xxx`
- **Reset mật khẩu (POST):** `POST /auth/reset-password`

## Cấu trúc Database

Các cột mới được thêm vào bảng `user`:

```sql
reset_password_token VARCHAR(255) NULL     -- Token cho reset password
reset_password_expiry TIMESTAMP NULL       -- Thời gian hết hạn token
```

## UI/UX Features

- **Responsive design:** Hoạt động tốt trên mobile và desktop
- **Beautiful modals:** Thiết kế đẹp với gradient và animation
- **Real-time validation:** Kiểm tra input ngay khi người dùng nhập
- **Clear error messages:** Thông báo lỗi rõ ràng bằng tiếng Việt
- **Auto-hide alerts:** Thông báo tự động ẩn sau 3 giây

## Test Cases

1. **Happy path:** Email hợp lệ → nhận email → click link → đặt mật khẩu mới → đăng nhập thành công
2. **Email không tồn tại:** Hiển thị lỗi phù hợp
3. **Token hết hạn:** Hiển thị lỗi và yêu cầu reset mới
4. **Mật khẩu không khớp:** Validation error
5. **Token đã sử dụng:** Không thể reset lại bằng cùng một token 