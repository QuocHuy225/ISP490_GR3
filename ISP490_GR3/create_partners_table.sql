-- Tạo bảng partners
CREATE TABLE IF NOT EXISTS partners (
    partner_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    isdeleted BOOLEAN DEFAULT FALSE
);

-- Thêm dữ liệu mẫu cho bảng partners
INSERT INTO partners (name, phone, address, description) VALUES
('Yến Nhi Clinic', '02812345678', '123 Đường ABC, Quận XYZ, TP.Lai Châu', 'Phòng khám chuyên khoa nội tổng hợp'),
('Anh Tú Clinic', '02898765432', '456 Đường DEF, Quận QWE, TP.Lai Châu', 'Phòng khám chuyên khoa nhi'),
('Quốc Huy Clinic', '02898555432', '946 Đường DEF, Quận QWE, TP.Lai Châu', 'Phòng khám chuyên khoa tim mạch'),
('Minh Phương Clinic', '02812345679', '789 Đường GHI, Quận RTY, TP.Lai Châu', 'Phòng khám chuyên khoa da liễu'),
('Thành Công Clinic', '02812345680', '321 Đường JKL, Quận UIO, TP.Lai Châu', 'Phòng khám chuyên khoa thần kinh');

-- Tạo index để tối ưu hiệu suất tìm kiếm
CREATE INDEX idx_partners_name ON partners(name);
CREATE INDEX idx_partners_phone ON partners(phone);
CREATE INDEX idx_partners_isdeleted ON partners(isdeleted); 