# Quản lý Đối tác - Ánh Dương Clinic

## Tổng quan
Chức năng quản lý đối tác cho phép Admin quản lý danh sách các cơ sở y tế đối tác, phòng khám liên kết. Chức năng này chỉ bao gồm CRUD operations trong menu admin.

## Các thành phần đã tạo

### 1. Model
- **Partner.java**: Model đại diện cho đối tác với các trường:
  - `partnerId`: ID đối tác
  - `name`: Tên đối tác
  - `phone`: Số điện thoại
  - `address`: Địa chỉ
  - `description`: Mô tả
  - `createdAt`, `updatedAt`: Thời gian tạo/cập nhật
  - `isdeleted`: Trạng thái xóa

### 2. DAO
- **DAOPartner.java**: Xử lý tất cả thao tác database:
  - `getAllPartners()`: Lấy tất cả đối tác
  - `searchPartners(keyword)`: Tìm kiếm đối tác
  - `getPartnerById(id)`: Lấy đối tác theo ID
  - `addPartner(partner)`: Thêm đối tác mới
  - `updatePartner(partner)`: Cập nhật đối tác
  - `deletePartner(id)`: Xóa đối tác (soft delete)
  - `getActivePartnersForDropdown()`: Lấy đối tác cho dropdown

### 3. Controller
- **PartnerController.java**: Xử lý các request quản lý đối tác
  - URL: `/admin/partners`
  - Chỉ Admin mới được truy cập
  - Các chức năng: list, search, add, update, delete

### 4. JSP
- **partner.jsp**: Giao diện quản lý đối tác
  - Hiển thị danh sách đối tác
  - Tìm kiếm đối tác
  - Thêm/sửa/xóa đối tác
  - Sử dụng DataTables

### 5. Database
- **create_partners_table.sql**: Script tạo bảng và dữ liệu mẫu
  - Tạo bảng `partners`
  - Thêm 5 đối tác mẫu
  - Tạo index tối ưu

## Cách sử dụng

### 1. Cài đặt Database
```sql
-- Chạy script SQL để tạo bảng và dữ liệu mẫu
source create_partners_table.sql
```

### 2. Truy cập chức năng
1. Đăng nhập với tài khoản Admin
2. Vào menu "Quản lý đối tác" trong sidebar
3. URL: `http://localhost:8080/ISP490_GR3/admin/partners`

### 3. Các chức năng chính

#### Xem danh sách đối tác
- Hiển thị tất cả đối tác đang hoạt động
- Sắp xếp theo tên
- Phân trang với DataTables

#### Tìm kiếm đối tác
- Tìm theo tên hoặc số điện thoại
- Kết quả hiển thị real-time

#### Thêm đối tác mới
1. Click nút "Thêm đối tác mới"
2. Điền thông tin bắt buộc:
   - Tên đối tác
   - Số điện thoại
   - Địa chỉ
3. Mô tả (tùy chọn)
4. Click "Thêm đối tác"

#### Sửa đối tác
1. Click nút "Sửa" (biểu tượng bút chì)
2. Thông tin sẽ được load vào modal
3. Chỉnh sửa thông tin
4. Click "Cập nhật"

#### Xóa đối tác
1. Click nút "Xóa" (biểu tượng thùng rác)
2. Xác nhận trong modal
3. Đối tác sẽ được soft delete



## Cấu trúc Menu
Menu Admin đã được cập nhật với thứ tự:
1. Trang chủ
2. Quản lý người dùng
3. Mẫu khám bệnh
4. **Quản lý dịch vụ**
5. **Quản lý đối tác** ← Mới thêm
6. Quản lý kho thuốc
7. Quản lý thuốc
8. Quản lý vật tư
9. Báo cáo thống kê



## Dữ liệu mẫu
5 đối tác mẫu đã được tạo:
1. Yến Nhi Clinic - Chuyên khoa nội tổng hợp
2. Anh Tú Clinic - Chuyên khoa nhi
3. Quốc Huy Clinic - Chuyên khoa tim mạch
4. Minh Phương Clinic - Chuyên khoa da liễu
5. Thành Công Clinic - Chuyên khoa thần kinh

## Lưu ý
- Chỉ Admin mới có quyền truy cập
- Sử dụng soft delete để bảo toàn dữ liệu
- Validation đầy đủ cho form input
- Responsive design với Bootstrap 5
- Tích hợp với DataTables cho UX tốt hơn
- Chức năng này chỉ dành cho quản lý CRUD, không tích hợp với các form khác 