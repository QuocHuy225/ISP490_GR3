<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.mycompany.isp490_gr3.model.User" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Ánh Dương Clinic - Chăm sóc sức khỏe phụ nữ</title>
        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Font Awesome Icons -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
        <!-- Bootstrap Icons -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
        <!-- AOS Animation -->
        <link href="https://unpkg.com/aos@2.3.1/dist/aos.css" rel="stylesheet">
        <!-- Custom CSS -->
        <link rel="stylesheet" href="css/landing.css">
    </head>
    <body>
        <!-- Header -->
        <header class="navbar navbar-expand-lg navbar-light fixed-top bg-white shadow-sm py-2">
            <div class="container">
                <a class="navbar-brand fw-bold fs-4" href="#">
                    Ánh Dương <span class="text-primary">Clinic</span>
                </a>

                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarNav">
                    <nav class="navbar-nav mx-auto">
                        <a class="nav-link px-3" href="#home">Trang chủ</a>
                        <a class="nav-link px-3" href="#services">Dịch vụ</a>
                        <a class="nav-link px-3" href="#about">Về chúng tôi</a>
                        <a class="nav-link px-3" href="#contact">Liên hệ</a>
                    </nav>

                    <div class="d-flex gap-2">
                        <button class="btn btn-primary px-3" data-bs-toggle="modal" data-bs-target="#loginModal">
                            <i class="bi bi-person-plus me-1"></i>Đăng nhập
                        </button>
                        <button class="btn btn-outline-primary px-3" data-bs-toggle="modal" data-bs-target="#registerModal">
                            <i class="bi bi-box-arrow-in-right me-1"></i>Đăng ký
                        </button>
                    </div>
                </div>
            </div>
        </header>

        <!-- Logout Success Message -->
        <% if (request.getAttribute("logoutSuccess") != null) { %>
            <div class="alert alert-success alert-dismissible fade show position-fixed" 
                 style="top: 80px; right: 20px; z-index: 9999; max-width: 400px;">
                <i class="bi bi-check-circle-fill me-2"></i>
                <%= request.getAttribute("logoutSuccess") %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>

        <!-- Hero Section -->
        <section id="home" class="hero-section bg-gradient position-relative overflow-hidden" style="margin-top: 50px">
            <div class="container h-100">
                <div class="row align-items-center h-100 py-5">
                    <div class="col-lg-6" data-aos="fade-right">
                        <div class="hero-content">
                            <h1 class="display-4 fw-bold mb-4">
                                <span style="color: #007bff;">Ánh Dương</span>
                                <span style="color: #333;">Sản Phụ Khoa</span>
                            </h1>
                            <p class="lead mb-4 text-muted">
                                <i class="bi bi-heart-fill text-danger me-2"></i>
                                Thấu hiểu - Chia sẻ - Đồng hành cùng sức khỏe phụ nữ
                            </p>
                            <div class="d-flex flex-wrap gap-3 mb-4">
                                <button class="btn btn-primary btn-lg px-4 py-3 rounded-pill shadow" data-bs-toggle="modal" data-bs-target="#loginModal">
                                    <i class="bi bi-calendar-plus me-2"></i>Đặt lịch khám
                                </button>
                                <button class="btn btn-outline-primary btn-lg px-4 py-3 rounded-pill" data-bs-toggle="modal" data-bs-target="#loginModal">
                                    <i class="bi bi-telephone me-2"></i>Tư vấn ngay
                                </button>
                            </div>
                            <div class="d-flex align-items-center gap-4">
                                <div class="text-center">
                                    <div class="fw-bold fs-4 text-primary">500+</div>
                                    <small class="text-muted">Bệnh nhân tin tưởng</small>
                                </div>
                                <div class="text-center">
                                    <div class="fw-bold fs-4 text-success">15+</div>
                                    <small class="text-muted">Năm kinh nghiệm</small>
                                </div>
                                <div class="text-center">
                                    <div class="fw-bold fs-4 text-warning">★★★★★</div>
                                    <small class="text-muted">Đánh giá 5 sao</small>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6" data-aos="fade-left">
                        <div class="hero-image text-center">
                            <img src="https://images.unsplash.com/photo-1579684385127-1ef15d508118?w=600&h=400&fit=crop" 
                                 alt="Bác sĩ chăm sóc bệnh nhân" 
                                 class="img-fluid rounded-4 shadow-lg">
                        </div>
                    </div>
                </div>
            </div>
            <!-- Floating elements -->
            <div class="position-absolute top-0 start-0 w-100 h-100" style="z-index: -1;">
                <div class="floating-shape bg-primary opacity-10 rounded-circle position-absolute" style="width: 100px; height: 100px; top: 10%; left: 10%; animation: float 6s ease-in-out infinite;"></div>
                <div class="floating-shape bg-success opacity-10 rounded-circle position-absolute" style="width: 60px; height: 60px; top: 70%; right: 15%; animation: float 4s ease-in-out infinite reverse;"></div>
            </div>
        </section>

        <!-- Services Section -->
        <section id="services" class="py-5 bg-light">
            <div class="container">
                <div class="text-center mb-5" data-aos="fade-up">
                    <h2 class="display-5 fw-bold mb-3">Dịch vụ của chúng tôi</h2>
                    <p class="lead text-muted">Chúng tôi cam kết phục vụ bạn với dịch vụ y tế tốt nhất</p>
                </div>

                <div class="row g-4">
                    <div class="col-lg-3 col-md-6" data-aos="fade-up" data-aos-delay="100">
                        <div class="service-card h-100 bg-white rounded-4 p-4 shadow-sm text-center border-0 hover-lift">
                            <div class="service-icon mb-3">
                                <i class="bi bi-hospital text-primary" style="font-size: 3rem;"></i>
                            </div>
                            <h5 class="fw-bold mb-3">Phòng khám hiện đại</h5>
                            <p class="text-muted">Trang thiết bị y tế tiên tiến, đảm bảo chất lượng khám chữa bệnh tốt nhất</p>
                        </div>
                    </div>

                    <div class="col-lg-3 col-md-6" data-aos="fade-up" data-aos-delay="200">
                        <div class="service-card h-100 bg-white rounded-4 p-4 shadow-sm text-center border-0 hover-lift">
                            <div class="service-icon mb-3">
                                <i class="bi bi-clipboard2-pulse text-success" style="font-size: 3rem;"></i>
                            </div>
                            <h5 class="fw-bold mb-3">Theo dõi sức khỏe</h5>
                            <p class="text-muted">Hệ thống quản lý hồ sơ bệnh án điện tử, theo dõi sức khỏe liên tục</p>
                        </div>
                    </div>

                    <div class="col-lg-3 col-md-6" data-aos="fade-up" data-aos-delay="300">
                        <div class="service-card h-100 bg-white rounded-4 p-4 shadow-sm text-center border-0 hover-lift">
                            <div class="service-icon mb-3">
                                <i class="bi bi-calendar-check text-info" style="font-size: 3rem;"></i>
                            </div>
                            <h5 class="fw-bold mb-3">Đặt lịch online</h5>
                            <p class="text-muted">Đặt lịch khám trực tuyến 24/7, tiện lợi và nhanh chóng</p>
                        </div>
                    </div>

                    <div class="col-lg-3 col-md-6" data-aos="fade-up" data-aos-delay="400">
                        <div class="service-card h-100 bg-white rounded-4 p-4 shadow-sm text-center border-0 hover-lift">
                            <div class="service-icon mb-3">
                                <i class="bi bi-award text-warning" style="font-size: 3rem;"></i>
                            </div>
                            <h5 class="fw-bold mb-3">Bác sĩ chuyên khoa</h5>
                            <p class="text-muted">Đội ngũ bác sĩ giàu kinh nghiệm, chuyên sâu về sản phụ khoa</p>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Appointment & Medical Records Section -->
        <section class="py-5 bg-white">
            <div class="container">
                <div class="row align-items-center g-4">
                    <div class="col-lg-6" data-aos="fade-right">
                        <div class="appointment-card bg-gradient p-4 rounded-4">
                            <h2 class="display-6 fw-bold mb-4">
                                Đặt lịch và theo dõi hồ sơ dễ dàng
                            </h2>
                            <p class="lead mb-4">
                                Bệnh nhân có thể tiết kiệm thời gian, giảm chờ đợi và theo dõi sức khỏe cá nhân một cách thuận tiện nhất.
                            </p>
                            <div class="row g-4">
                                <div class="col-md-6">
                                    <div class="d-flex align-items-center">
                                        <div class="icon-box primary me-3">
                                            <i class="bi bi-calendar-check fs-4"></i>
                                        </div>
                                        <div>
                                            <h6 class="fw-bold mb-1">Đặt lịch hẹn online 24/7</h6>
                                            <p class="small text-muted mb-0">
                                                Bạn có thể chủ động chọn thời gian khám phù hợp, tránh xếp hàng và được ưu tiên khi đến phòng khám.
                                            </p>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="d-flex align-items-center">
                                        <div class="icon-box primary me-3">
                                            <i class="bi bi-file-medical fs-4"></i>
                                        </div>
                                        <div>
                                            <h6 class="fw-bold mb-1">Theo dõi hồ sơ bệnh án</h6>
                                            <p class="small text-muted mb-0">
                                                Xem lại kết quả khám, đơn thuốc, lịch sử tiêm chủng và theo dõi thai kỳ dễ dàng, mọi lúc mọi nơi.
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6" data-aos="fade-left">
                        <img src="${pageContext.request.contextPath}/images/doan2.jpg" alt="Đặt lịch khám" class="img-fluid rounded-4 shadow-lg">
                    </div>
                </div>
            </div>
        </section>

        <!-- About Section -->
        <section id="about" class="py-5 bg-primary text-white">
            <div class="container">
                <div class="row align-items-center">
                    <div class="col-lg-6" data-aos="fade-right">
                        <h2 class="display-5 fw-bold mb-4" style="color: black">Chăm sóc sức khỏe phụ nữ toàn diện</h2>
                        <p style="color: black" class="lead mb-4">
                            Tại Phòng khám Ánh Dương, chúng tôi cung cấp các dịch vụ như khám phụ khoa định kỳ, 
                            tư vấn thai sản, siêu âm 4D, điều trị viêm nhiễm và tầm soát ung thư cổ tử cung.
                        </p>
                        <p class="mb-4">
                            <i class="bi bi-check-circle-fill me-2"></i>
                            Bác sĩ luôn đồng hành cùng bạn từ những giai đoạn nhỏ nhất của sức khỏe sinh sản.
                        </p>
                        <button class="btn btn-light btn-lg px-4 py-3 rounded-pill">
                            <i class="bi bi-arrow-right me-2"></i>Tìm hiểu thêm
                        </button>
                    </div>
                    <div class="col-lg-6" data-aos="fade-left">
                        <div class="position-relative">
                            <img src="${pageContext.request.contextPath}/images/doan1.png" 
                                 alt="Bác sĩ tư vấn" >
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Reviews Section -->
        <section class="py-5">
            <div class="container">
                <div class="text-center mb-5" data-aos="fade-up">
                    <h2 class="display-5 fw-bold mb-3">Phản hồi từ bệnh nhân</h2>
                    <p class="lead text-muted">Những chia sẻ chân thành từ các bệnh nhân đã tin tưởng chúng tôi</p>
                </div>

                <div class="row g-4">
                    <div class="col-lg-4" data-aos="fade-up" data-aos-delay="100">
                        <div class="review-card bg-white rounded-4 p-4 shadow-sm border-0 h-100">
                            <div class="d-flex align-items-center mb-3">
                                <img src="https://images.unsplash.com/photo-1494790108755-2616b612b786?w=60&h=60&fit=crop&crop=face" 
                                     alt="Yến Nhi" class="rounded-circle me-3" width="60" height="60">
                                <div>
                                    <h6 class="fw-bold mb-1">Yến Nhi</h6>
                                    <small class="text-muted">22/4/2023</small>
                                    <div class="text-warning">★★★★★</div>
                                </div>
                            </div>
                            <p class="text-muted mb-0">
                                "Bác sĩ rất nhẹ nhàng, giải thích kỹ lưỡng và luôn quan tâm đến cảm nhận của tôi 
                                trong suốt quá trình điều trị. Tôi thật sự hài lòng!"
                            </p>
                        </div>
                    </div>

                    <div class="col-lg-4" data-aos="fade-up" data-aos-delay="200">
                        <div class="review-card bg-white rounded-4 p-4 shadow-sm border-0 h-100">
                            <div class="d-flex align-items-center mb-3">
                                <img src="https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=60&h=60&fit=crop&crop=face" 
                                     alt="Minh Anh" class="rounded-circle me-3" width="60" height="60">
                                <div>
                                    <h6 class="fw-bold mb-1">Minh Anh</h6>
                                    <small class="text-muted">15/3/2023</small>
                                    <div class="text-warning">★★★★★</div>
                                </div>
                            </div>
                            <p class="text-muted mb-0">
                                "Phòng khám hiện đại, sạch sẽ. Quy trình khám rất chuyên nghiệp. 
                                Tôi sẽ tiếp tục tin tưởng và giới thiệu cho bạn bè."
                            </p>
                        </div>
                    </div>

                    <div class="col-lg-4" data-aos="fade-up" data-aos-delay="300">
                        <div class="review-card bg-white rounded-4 p-4 shadow-sm border-0 h-100">
                            <div class="d-flex align-items-center mb-3">
                                <img src="https://images.unsplash.com/photo-1544725176-7c40e5a71c5e?w=60&h=60&fit=crop&crop=face" 
                                     alt="Thu Hà" class="rounded-circle me-3" width="60" height="60">
                                <div>
                                    <h6 class="fw-bold mb-1">Thu Hà</h6>
                                    <small class="text-muted">8/5/2023</small>
                                    <div class="text-warning">★★★★★</div>
                                </div>
                            </div>
                            <p class="text-muted mb-0">
                                "Việc đặt lịch online rất tiện lợi. Bác sĩ tận tâm, kinh nghiệm cao. 
                                Cảm ơn đội ngũ đã chăm sóc tôi rất chu đáo."
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Contact Section -->
        <footer id="contact" class="bg-dark text-white py-5">
            <div class="container">
                <div class="row g-4">
                    <div class="col-lg-4" data-aos="fade-up">
                        <h3 class="fw-bold mb-4">
                            <i class="bi bi-hospital me-2 text-primary"></i>Ánh Dương Clinic
                        </h3>
                        <p class="mb-3">
                            <i class="bi bi-geo-alt-fill text-primary me-2"></i>
                            Số nhà 123, Lai Châu
                        </p>
                        <p class="mb-3">
                            <i class="bi bi-telephone-fill text-primary me-2"></i>
                            Hotline: +84765317988
                        </p>
                        <p class="mb-4">
                            <i class="bi bi-envelope-fill text-primary me-2"></i>
                            info@anhduongclinic.com
                        </p>
                        <div class="d-flex gap-3">
                            <a href="#" class="btn btn-outline-light rounded-circle">
                                <i class="bi bi-facebook"></i>
                            </a>
                            <a href="#" class="btn btn-outline-light rounded-circle">
                                <i class="bi bi-instagram"></i>
                            </a>
                            <a href="#" class="btn btn-outline-light rounded-circle">
                                <i class="bi bi-youtube"></i>
                            </a>
                        </div>
                    </div>

                    <div class="col-lg-2 col-md-6" data-aos="fade-up" data-aos-delay="100">
                        <h5 class="fw-bold mb-3">Về chúng tôi</h5>
                        <ul class="list-unstyled">
                            <li class="mb-2"><a href="#" class="text-white-50 text-decoration-none">Giới thiệu</a></li>
                            <li class="mb-2"><a href="#" class="text-white-50 text-decoration-none">Bác sĩ</a></li>
                            <li class="mb-2"><a href="#" class="text-white-50 text-decoration-none">Cơ sở vật chất</a></li>
                        </ul>
                    </div>

                    <div class="col-lg-2 col-md-6" data-aos="fade-up" data-aos-delay="200">
                        <h5 class="fw-bold mb-3">Dịch vụ</h5>
                        <ul class="list-unstyled">
                            <li class="mb-2"><a href="#" class="text-white-50 text-decoration-none">Khám tổng quát</a></li>
                            <li class="mb-2"><a href="#" class="text-white-50 text-decoration-none">Khám phụ khoa</a></li>
                            <li class="mb-2"><a href="#" class="text-white-50 text-decoration-none">Xét nghiệm</a></li>
                        </ul>
                    </div>

                    <div class="col-lg-2 col-md-6" data-aos="fade-up" data-aos-delay="300">
                        <h5 class="fw-bold mb-3">Hỗ trợ</h5>
                        <ul class="list-unstyled">
                            <li class="mb-2"><a href="#" class="text-white-50 text-decoration-none">FAQ</a></li>
                            <li class="mb-2"><a href="#" class="text-white-50 text-decoration-none">Bảo mật</a></li>
                            <li class="mb-2"><a href="#" class="text-white-50 text-decoration-none">Điều khoản</a></li>
                        </ul>
                    </div>

                    <div class="col-lg-2 col-md-6" data-aos="fade-up" data-aos-delay="400">
                        <h5 class="fw-bold mb-3">Giờ làm việc</h5>
                        <p class="small text-white-50 mb-1">Thứ 2 - Thứ 6: 8:00 - 17:00</p>
                        <p class="small text-white-50 mb-1">Thứ 7: 8:00 - 12:00</p>
                        <p class="small text-white-50">Chủ nhật: Nghỉ</p>
                    </div>
                </div>

                <hr class="my-4 border-secondary">
                <div class="text-center">
                    <p class="mb-0 text-white-50">
                        © 2023 Ánh Dương Clinic. All rights reserved.
                    </p>
                </div>
            </div>
        </footer>

        <!-- Appointment Modal -->
        <div class="modal fade" id="appointmentModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content rounded-4 border-0">
                    <div class="modal-header border-0">
                        <h5 class="modal-title fw-bold">
                            <i class="bi bi-calendar-plus text-primary me-2"></i>Đặt lịch khám
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body p-4">
                        <div class="text-center mb-4">
                            <i class="bi bi-shield-lock text-primary" style="font-size: 3rem;"></i>
                            <h6 class="mt-3 mb-3">Vui lòng đăng nhập để đặt lịch khám</h6>
                            <p class="text-muted">Bạn cần có tài khoản để sử dụng dịch vụ đặt lịch online</p>
                        </div>
                        <div class="d-grid gap-2">
                            <button class="btn btn-primary btn-lg" data-bs-toggle="modal" data-bs-target="#loginModal" data-bs-dismiss="modal">
                                <i class="bi bi-box-arrow-in-right me-2"></i>Đăng nhập ngay
                            </button>
                            <button class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#registerModal" data-bs-dismiss="modal">
                                <i class="bi bi-person-plus me-2"></i>Tạo tài khoản mới
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Login Modal -->
        <div class="modal fade" id="loginModal" tabindex="-1">
            <div class="modal-dialog modal-xl modal-dialog-centered">
                <div class="modal-content border-0 shadow-lg" style="border-radius: 20px; overflow: hidden; backdrop-filter: blur(10px);">
                    <div class="modal-body p-0">
                        <div class="container-fluid">
                            <div class="row g-0 min-vh-50">
                                <!-- Left side - Image and branding -->
                                <div class="col-lg-6 d-flex">
                                    <div class="w-100 d-flex flex-column justify-content-center align-items-center text-white position-relative" 
                                         style="background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%); min-height: 500px;">
                                        <!-- Decorative shapes -->
                                        <div class="position-absolute" style="top: 10%; left: 10%; width: 60px; height: 60px; background: rgba(255,255,255,0.15); border-radius: 50%; animation: float 3s ease-in-out infinite;"></div>
                                        <div class="position-absolute" style="top: 60%; right: 15%; width: 40px; height: 40px; background: rgba(255,255,255,0.15); border-radius: 50%; animation: float 3s ease-in-out infinite reverse;"></div>
                                        <div class="position-absolute" style="bottom: 20%; left: 20%; width: 30px; height: 30px; background: rgba(255,255,255,0.15); border-radius: 50%; animation: float 2s ease-in-out infinite;"></div>
                                        
                                        <div class="text-center z-index-2 position-relative">
                                            <div class="mb-4">
                                                <i class="bi bi-hospital" style="font-size: 4rem; color: rgba(255,255,255,0.95);"></i>
                                            </div>
                                            <h1 class="display-5 fw-bold mb-3" style="font-family: 'Poppins', sans-serif; color: #ffffff;">Ánh Dương Clinic</h1>
                                            <p class="lead opacity-90 mb-4" style="color: #f1f5f9;">Chăm sóc sức khỏe phụ nữ với tình yêu thương</p>
                                            <div class="d-flex justify-content-center mb-4">
                                                <img src="${pageContext.request.contextPath}/images/doan3.jpg" alt="Doctor" class="img-fluid shadow-lg" 
                                                     style="max-width: 250px; border-radius: 15px; border: 3px solid rgba(255,255,255,0.3);" />
                                            </div>
                                            <div class="d-flex justify-content-center gap-3">
                                                <div class="text-center">
                                                    <div class="fw-bold h4" style="color: #ffffff;">500+</div>
                                                    <small class="opacity-75" style="color: #cbd5e1;">Bệnh nhân</small>
                                                </div>
                                                <div class="text-center">
                                                    <div class="fw-bold h4" style="color: #ffffff;">15+</div>
                                                    <small class="opacity-75" style="color: #cbd5e1;">Năm kinh nghiệm</small>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- Right side - Login form -->
                                <div class="col-lg-6">
                                    <div class="p-5 d-flex flex-column justify-content-center h-100" style="min-height: 500px; background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);">
                                        <div class="text-end mb-3">
                                            <button type="button" class="btn-close btn-lg" data-bs-dismiss="modal" style="background-size: 1.5em;"></button>
                                        </div>
                                        
                                        <div class="mx-auto w-100" style="max-width: 400px;">
                                            <div class="text-center mb-4">
                                                <div class="d-inline-block p-3 rounded-circle mb-3" style="background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);">
                                                    <i class="bi bi-person-fill text-white" style="font-size: 2rem;"></i>
                                                </div>
                                                <h2 class="h3 fw-bold mb-2" style="color: #1f2937;">Chào mừng trở lại!</h2>
                                                <p class="text-muted" style="color: #6b7280;">Đăng nhập để trải nghiệm dịch vụ tốt nhất</p>
                                            </div>
                                            
                                            <!-- Error/Success Messages for Login -->
                                            <% if (request.getAttribute("loginError") != null) { %>
                                                <div class="alert alert-danger mb-3" style="border-radius: 10px; border: none;">
                                                    <i class="bi bi-exclamation-circle me-2"></i>
                                                    <%= request.getAttribute("loginError") %>
                                                </div>
                                            <% } %>
                                            
                                            <% if (request.getAttribute("registerSuccess") != null) { %>
                                                <div class="alert alert-success mb-3" style="border-radius: 10px; border: none;">
                                                    <i class="bi bi-check-circle me-2"></i>
                                                    <%= request.getAttribute("registerSuccess") %>
                                                </div>
                                            <% } %>
                                            
                                            <form action="${pageContext.request.contextPath}/auth/login" method="post" class="needs-validation" novalidate>
                                                <div class="form-floating mb-3">
                                                    <input type="email" name="email" class="form-control border-0 shadow-sm" 
                                                           id="loginEmail" placeholder="name@example.com" required
                                                           value="${loginEmail != null ? loginEmail : ''}"
                                                           style="border-radius: 15px; background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); color: #374151;">
                                                    <label for="loginEmail" style="color: #6b7280;"><i class="bi bi-envelope me-2"></i>Địa chỉ email</label>
                                                    <div class="invalid-feedback">Vui lòng nhập email hợp lệ.</div>
                                                </div>
                                                
                                                <div class="form-floating mb-3">
                                                    <input type="password" name="password" class="form-control border-0 shadow-sm" 
                                                           id="loginPassword" placeholder="Password" required
                                                           style="border-radius: 15px; background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); color: #374151;">
                                                    <label for="loginPassword" style="color: #6b7280;"><i class="bi bi-lock me-2"></i>Mật khẩu</label>
                                                    <div class="invalid-feedback">Vui lòng nhập mật khẩu.</div>
                                                </div>
                                                
                                                <div class="d-flex justify-content-between align-items-center mb-4">
                                                    <div class="form-check">
                                                        <input class="form-check-input" type="checkbox" id="rememberMe">
                                                        <label class="form-check-label small" for="rememberMe" style="color: #6b7280;">
                                                            Ghi nhớ đăng nhập
                                                        </label>
                                                    </div>
                                                    <a href="#" class="text-decoration-none small fw-medium" style="color: #2563eb;">Quên mật khẩu?</a>
                                                </div>
                                                
                                                <button type="submit" class="btn btn-lg w-100 mb-3 fw-medium shadow-sm login-btn" 
                                                        style="border-radius: 15px; background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%); border: none; color: white; transition: all 0.3s ease;">
                                                    <i class="bi bi-box-arrow-in-right me-2"></i>Đăng nhập
                                                </button>
                                                
                                                <div class="text-center">
                                                    <span style="color: #6b7280;">Chưa có tài khoản? </span>
                                                    <a href="#" data-bs-toggle="modal" data-bs-target="#registerModal" data-bs-dismiss="modal" 
                                                       class="text-decoration-none fw-medium" style="color: #2563eb;">Đăng ký ngay</a>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Register Modal -->
        <div class="modal fade" id="registerModal" tabindex="-1">
            <div class="modal-dialog modal-xl modal-dialog-centered">
                <div class="modal-content border-0 shadow-lg" style="border-radius: 20px; overflow: hidden; backdrop-filter: blur(10px);">
                    <div class="modal-body p-0">
                        <div class="container-fluid">
                            <div class="row g-0 min-vh-50">
                                <!-- Left side - Registration form -->
                                <div class="col-lg-6">
                                    <div class="p-5 d-flex flex-column justify-content-center h-100" style="min-height: 550px; background: linear-gradient(135deg, #ffffff 0%, #f1f5f9 100%);">
                                        <div class="text-start mb-3">
                                            <button type="button" class="btn-close btn-lg" data-bs-dismiss="modal" style="background-size: 1.5em;"></button>
                                        </div>
                                        
                                        <div class="mx-auto w-100" style="max-width: 400px;">
                                            <div class="text-center mb-4">
                                                <div class="d-inline-block p-3 rounded-circle mb-3" style="background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%);">
                                                    <i class="bi bi-person-plus-fill text-white" style="font-size: 2rem;"></i>
                                                </div>
                                                <h2 class="h3 fw-bold mb-2" style="color: #1f2937;">Tạo tài khoản mới</h2>
                                                <p class="text-muted" style="color: #6b7280;">Tham gia cộng đồng chăm sóc sức khỏe</p>
                                            </div>
                                            
                                            <!-- Error Messages for Register -->
                                            <% if (request.getAttribute("registerError") != null) { %>
                                                <div class="alert alert-danger mb-3" style="border-radius: 10px; border: none;">
                                                    <i class="bi bi-exclamation-circle me-2"></i>
                                                    <%= request.getAttribute("registerError") %>
                                                </div>
                                            <% } %>
                                            
                                            <form action="${pageContext.request.contextPath}/auth/register" method="post" class="needs-validation" novalidate>
                                                <div class="form-floating mb-3">
                                                    <input type="text" name="fullname" class="form-control border-0 shadow-sm" 
                                                           id="registerFullname" placeholder="Họ và tên" required
                                                           value="${regFullName != null ? regFullName : ''}"
                                                           style="border-radius: 15px; background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); color: #374151;">
                                                    <label for="registerFullname" style="color: #6b7280;"><i class="bi bi-person me-2"></i>Họ và tên</label>
                                                    <div class="invalid-feedback">Vui lòng nhập họ tên.</div>
                                                </div>
                                                
                                                <div class="form-floating mb-3">
                                                    <input type="email" name="email" class="form-control border-0 shadow-sm" 
                                                           id="registerEmail" placeholder="name@example.com" required
                                                           value="${regEmail != null ? regEmail : ''}"
                                                           style="border-radius: 15px; background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); color: #374151;">
                                                    <label for="registerEmail" style="color: #6b7280;"><i class="bi bi-envelope me-2"></i>Địa chỉ email</label>
                                                    <div class="invalid-feedback">Vui lòng nhập email hợp lệ.</div>
                                                </div>
                                                
                                                <div class="form-floating mb-3">
                                                    <input type="password" name="password" class="form-control border-0 shadow-sm" 
                                                           id="registerPassword" placeholder="Password" required minlength="6"
                                                           style="border-radius: 15px; background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); color: #374151;">
                                                    <label for="registerPassword" style="color: #6b7280;"><i class="bi bi-lock me-2"></i>Mật khẩu</label>
                                                    <div class="invalid-feedback">Mật khẩu phải có ít nhất 6 ký tự.</div>
                                                </div>
                                                
                                                <div class="form-floating mb-4">
                                                    <input type="password" name="confirmPassword" class="form-control border-0 shadow-sm" 
                                                           id="confirmPassword" placeholder="Confirm Password" required
                                                           style="border-radius: 15px; background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); color: #374151;">
                                                    <label for="confirmPassword" style="color: #6b7280;"><i class="bi bi-lock-fill me-2"></i>Xác nhận mật khẩu</label>
                                                    <div class="invalid-feedback">Mật khẩu xác nhận không khớp.</div>
                                                </div>
                                                
                                                <!-- Additional fields -->
                                                <div class="form-floating mb-3">
                                                    <input type="tel" name="phone" class="form-control border-0 shadow-sm" 
                                                           id="registerPhone" placeholder="Số điện thoại"
                                                           value="${regPhone != null ? regPhone : ''}"
                                                           style="border-radius: 15px; background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); color: #374151;">
                                                    <label for="registerPhone" style="color: #6b7280;"><i class="bi bi-phone me-2"></i>Số điện thoại</label>
                                                </div>
                           
                                                <button type="submit" class="btn btn-lg w-100 mb-4 fw-medium shadow-sm register-btn" 
                                                        style="border-radius: 15px; background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%); border: none; color: white; transition: all 0.3s ease;">
                                                    <i class="bi bi-person-plus me-2"></i>Tạo tài khoản
                                                </button>
                                                
                                                <div class="text-center">
                                                    <span style="color: #6b7280;">Đã có tài khoản? </span>
                                                    <a href="#" data-bs-toggle="modal" data-bs-target="#loginModal" data-bs-dismiss="modal" 
                                                       class="text-decoration-none fw-medium" style="color: #2563eb;">Đăng nhập</a>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- Right side - Image and branding -->
                                <div class="col-lg-6 d-flex">
                                    <div class="w-100 d-flex flex-column justify-content-center align-items-center text-white position-relative" 
                                         style="background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); min-height: 550px;">
                                        <!-- Decorative shapes -->
                                        <div class="position-absolute" style="top: 15%; right: 10%; width: 50px; height: 50px; background: rgba(255,255,255,0.2); border-radius: 50%; animation: float 4s ease-in-out infinite;"></div>
                                        <div class="position-absolute" style="top: 70%; left: 15%; width: 35px; height: 35px; background: rgba(255,255,255,0.2); border-radius: 50%; animation: float 3s ease-in-out infinite reverse;"></div>
                                        <div class="position-absolute" style="bottom: 15%; right: 20%; width: 25px; height: 25px; background: rgba(255,255,255,0.2); border-radius: 50%; animation: float 2.5s ease-in-out infinite;"></div>
                                        
                                        <div class="text-center z-index-2 position-relative">
                                            <div class="mb-4">
                                                <i class="bi bi-heart-pulse" style="font-size: 4rem; color: #ffffff;"></i>
                                            </div>
                                            <h1 class="display-5 fw-bold mb-3" style="font-family: 'Poppins', sans-serif; color: #ffffff;">Tham Gia Với Chúng Tôi</h1>
                                            <p class="lead mb-4 opacity-90" style="color: #dbeafe;">Bắt đầu hành trình chăm sóc sức khỏe của bạn</p>
                                            <div class="d-flex justify-content-center mb-4">
                                                <img src="${pageContext.request.contextPath}/images/doan3.jpg" alt="Doctor" class="img-fluid shadow-lg" 
                                                     style="max-width: 250px; border-radius: 15px; border: 3px solid rgba(255,255,255,0.3);" />
                                            </div>
                                            <div class="row">
                                                <div class="col-4 text-center">
                                                    <div class="fw-bold h4" style="color: #ffffff;">⭐</div>
                                                    <small class="opacity-75" style="color: #bfdbfe;">Dịch vụ tốt</small>
                                                </div>
                                                <div class="col-4 text-center">
                                                    <div class="fw-bold h4" style="color: #ffffff;">🛡️</div>
                                                    <small class="opacity-75" style="color: #bfdbfe;">An toàn</small>
                                                </div>
                                                <div class="col-4 text-center">
                                                    <div class="fw-bold h4" style="color: #ffffff;">💙</div>
                                                    <small class="opacity-75" style="color: #bfdbfe;">Tận tâm</small>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Email Verification Modal -->
        <div class="modal fade" id="emailVerificationModal" tabindex="-1" data-bs-backdrop="static" data-bs-keyboard="false">
            <div class="modal-dialog modal-xl modal-dialog-centered">
                <div class="modal-content border-0 shadow-lg" style="border-radius: 20px; overflow: hidden; backdrop-filter: blur(10px);">
                    <div class="modal-body p-0">
                        <div class="container-fluid">
                            <div class="row g-0 min-vh-50">
                                <!-- Left side - Image and branding -->
                                <div class="col-lg-6 d-flex">
                                    <div class="w-100 d-flex flex-column justify-content-center align-items-center text-white position-relative" 
                                         style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 500px;">
                                        <!-- Decorative shapes -->
                                        <div class="position-absolute" style="top: 10%; left: 10%; width: 60px; height: 60px; background: rgba(255,255,255,0.15); border-radius: 50%; animation: float 3s ease-in-out infinite;"></div>
                                        <div class="position-absolute" style="top: 60%; right: 15%; width: 40px; height: 40px; background: rgba(255,255,255,0.15); border-radius: 50%; animation: float 3s ease-in-out infinite reverse;"></div>
                                        <div class="position-absolute" style="bottom: 20%; left: 20%; width: 30px; height: 30px; background: rgba(255,255,255,0.15); border-radius: 50%; animation: float 2s ease-in-out infinite;"></div>
                                        
                                        <div class="text-center z-index-2 position-relative">
                                            <div class="mb-4">
                                                <i class="bi bi-envelope-open" style="font-size: 4rem; color: rgba(255,255,255,0.95);"></i>
                                            </div>
                                            <h1 class="display-5 fw-bold mb-3" style="font-family: 'Poppins', sans-serif; color: #ffffff;">Xác Thực Email</h1>
                                            <p class="lead opacity-90 mb-4" style="color: #f1f5f9;">Bước cuối cùng để hoàn tất đăng ký</p>
                                            <div class="d-flex justify-content-center mb-4">
                                                <img src="${pageContext.request.contextPath}/images/doan1.png" alt="Email Verification" class="img-fluid shadow-lg" 
                                                     style="max-width: 250px; border-radius: 15px; border: 3px solid rgba(255,255,255,0.3);" />
                                            </div>
                                            <div class="d-flex justify-content-center gap-3">
                                                <div class="text-center">
                                                    <div class="fw-bold h4" style="color: #ffffff;">📧</div>
                                                    <small class="opacity-75" style="color: #cbd5e1;">Email gửi đi</small>
                                                </div>
                                                <div class="text-center">
                                                    <div class="fw-bold h4" style="color: #ffffff;">🔒</div>
                                                    <small class="opacity-75" style="color: #cbd5e1;">Bảo mật</small>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- Right side - Verification form -->
                                <div class="col-lg-6">
                                    <div class="p-5 d-flex flex-column justify-content-center h-100" style="min-height: 500px; background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);">
                                        <div class="text-end mb-3">
                                            <button type="button" class="btn-close btn-lg" data-bs-dismiss="modal" style="background-size: 1.5em;"></button>
                                        </div>
                                        
                                        <div class="mx-auto w-100" style="max-width: 400px;">
                                            <div class="text-center mb-4">
                                                <div class="d-inline-block p-3 rounded-circle mb-3" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                                                    <i class="bi bi-envelope-check-fill text-white" style="font-size: 2rem;"></i>
                                                </div>
                                                <h2 class="h3 fw-bold mb-2" style="color: #1f2937;">Xác thực Email</h2>
                                                <p class="text-muted mb-3" style="color: #6b7280;">Chúng tôi đã gửi mã xác thực đến:</p>
                                                <p class="fw-bold text-primary" style="color: #667eea !important;" id="verificationEmailDisplay">${verificationEmail}</p>
                                            </div>
                                            
                                            <!-- Error/Success Messages for Email Verification -->
                                            <div id="verificationErrorAlert" class="alert alert-danger mb-3 d-none" style="border-radius: 10px; border: none;">
                                                <i class="bi bi-exclamation-circle me-2"></i>
                                                <span id="verificationErrorMessage"></span>
                                            </div>
                                            
                                            <div id="verificationSuccessAlert" class="alert alert-success mb-3 d-none" style="border-radius: 10px; border: none;">
                                                <i class="bi bi-check-circle me-2"></i>
                                                <span id="verificationSuccessMessage"></span>
                                            </div>
                                            
                                            <form id="emailVerificationForm" action="${pageContext.request.contextPath}/auth/verify-email" method="post" class="needs-validation" novalidate>
                                                <input type="hidden" name="email" id="verificationEmailInput" value="${verificationEmail}">
                                                
                                                <div class="form-floating mb-3">
                                                    <input type="text" name="verificationCode" class="form-control border-0 shadow-sm text-center" 
                                                           id="verificationCode" placeholder="000000" maxlength="6" pattern="[0-9]{6}" required
                                                           style="border-radius: 15px; background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); color: #374151; font-size: 1.5rem; font-weight: 600; letter-spacing: 0.5em;">
                                                    <label for="verificationCode" style="color: #6b7280;"><i class="bi bi-key me-2"></i>Mã xác thực (6 số)</label>
                                                    <div class="invalid-feedback">Vui lòng nhập mã xác thực 6 số.</div>
                                                </div>
                                                
                                                <button type="submit" class="btn btn-lg w-100 mb-3 fw-medium shadow-sm" 
                                                        style="border-radius: 15px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; color: white; transition: all 0.3s ease;">
                                                    <i class="bi bi-check-circle me-2"></i>Xác thực Email
                                                </button>
                                            </form>
                                            
                                            <form id="resendVerificationForm" action="${pageContext.request.contextPath}/auth/resend-verification" method="post">
                                                <input type="hidden" name="email" id="resendEmailInput" value="${verificationEmail}">
                                                <button type="submit" class="btn btn-outline-primary btn-lg w-100 fw-medium" 
                                                        style="border-radius: 15px; border: 2px solid #667eea; color: #667eea; transition: all 0.3s ease;">
                                                    <i class="bi bi-arrow-clockwise me-2"></i>Gửi lại mã xác thực
                                                </button>
                                            </form>
                                            
                                            <div class="text-center mt-4">
                                                <small class="text-muted">
                                                    <i class="bi bi-info-circle me-1"></i>
                                                    Mã xác thực có hiệu lực trong 15 phút
                                                </small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Scripts -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
        <script src="https://unpkg.com/aos@2.3.1/dist/aos.js"></script>

        <!-- Hidden fields for server response status -->
        <input type="hidden" id="loginError" value="${loginError != null ? 'true' : 'false'}">
        <input type="hidden" id="registerSuccess" value="${registerSuccess != null ? 'true' : 'false'}">
        <input type="hidden" id="registerError" value="${registerError != null ? 'true' : 'false'}">
        <input type="hidden" id="logoutSuccess" value="${logoutSuccess != null ? 'true' : 'false'}">
        <input type="hidden" id="needEmailVerification" value="${needEmailVerification != null ? 'true' : 'false'}">
        <input type="hidden" id="verificationError" value="${verificationError}">
        <input type="hidden" id="verificationSuccess" value="${verificationSuccess}">

        <script>
                                // Initialize AOS
                                AOS.init({
                                    duration: 1000,
                                    once: true
                                });

                                // Smooth scrolling
                                document.querySelectorAll('a[href^="#"]').forEach(anchor => {
                                    anchor.addEventListener('click', function (e) {
                                        e.preventDefault();
                                        const target = document.querySelector(this.getAttribute('href'));
                                        if (target) {
                                            target.scrollIntoView({
                                                behavior: 'smooth',
                                                block: 'start'
                                            });
                                        }
                                    });
                                });

                                // Functions
                                function showLoginModal() {
                                    var loginModal = new bootstrap.Modal(document.getElementById('loginModal'));
                                    loginModal.show();
                                }

                                function showRegisterModal() {
                                    var registerModal = new bootstrap.Modal(document.getElementById('registerModal'));
                                    registerModal.show();
                                }

                                function showEmailVerificationModal() {
                                    var emailVerificationModal = new bootstrap.Modal(document.getElementById('emailVerificationModal'));
                                    emailVerificationModal.show();
                                }

                                // Auto-hide alerts
                                window.addEventListener('DOMContentLoaded', function () {
                                    var alerts = document.querySelectorAll('.alert-dismissible');
                                    alerts.forEach(function (alert) {
                                        setTimeout(function () {
                                            alert.classList.remove('show');
                                            alert.classList.add('fade');
                                            setTimeout(function () {
                                                alert.remove();
                                            }, 500);
                                        }, 3000);
                                    });

                                    // Show appropriate modal based on server response
                                    var hasLoginError = document.getElementById('loginError').value === 'true';
                                    var hasRegisterSuccess = document.getElementById('registerSuccess').value === 'true';
                                    var hasRegisterError = document.getElementById('registerError').value === 'true';
                                    var needEmailVerification = document.getElementById('needEmailVerification').value === 'true';
                                    var verificationError = document.getElementById('verificationError').value;
                                    var verificationSuccess = document.getElementById('verificationSuccess').value;
                                    
                                    if (needEmailVerification || verificationError || verificationSuccess) {
                                        showEmailVerificationModal();
                                        
                                        // Show error/success messages in the modal
                                        if (verificationError && verificationError !== 'null' && verificationError !== '') {
                                            document.getElementById('verificationErrorAlert').classList.remove('d-none');
                                            document.getElementById('verificationErrorMessage').textContent = verificationError;
                                        }
                                        if (verificationSuccess && verificationSuccess !== 'null' && verificationSuccess !== '') {
                                            document.getElementById('verificationSuccessAlert').classList.remove('d-none');
                                            document.getElementById('verificationSuccessMessage').textContent = verificationSuccess;
                                        }
                                    } else if (hasLoginError || hasRegisterSuccess) {
                                        showLoginModal();
                                    } else if (hasRegisterError) {
                                        showRegisterModal();
                                    }

                                    // Email verification code input validation
                                    const verificationCodeInput = document.getElementById('verificationCode');
                                    if (verificationCodeInput) {
                                        verificationCodeInput.addEventListener('input', function(e) {
                                            // Only allow numbers
                                            this.value = this.value.replace(/[^0-9]/g, '');
                                            
                                            // Auto-submit when 6 digits are entered
                                            if (this.value.length === 6) {
                                                // Add visual feedback
                                                this.classList.add('is-valid');
                                            } else {
                                                this.classList.remove('is-valid');
                                            }
                                        });
                                        
                                        verificationCodeInput.addEventListener('paste', function(e) {
                                            // Handle paste events
                                            setTimeout(() => {
                                                this.value = this.value.replace(/[^0-9]/g, '').substring(0, 6);
                                            }, 0);
                                        });
                                    }

                                    // Hide alerts when clicking close button in modal
                                    document.addEventListener('click', function(e) {
                                        if (e.target.classList.contains('btn-close')) {
                                            const modal = e.target.closest('.modal');
                                            if (modal) {
                                                const alerts = modal.querySelectorAll('.alert');
                                                alerts.forEach(alert => alert.classList.add('d-none'));
                                            }
                                        }
                                    });
                                });

                                // All styles are now in landing.css
        </script>

        <!-- All custom styles moved to landing.css -->
    </body>
</html>