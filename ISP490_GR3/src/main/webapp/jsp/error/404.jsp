<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Không Tìm Thấy Trang</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .error-container {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            padding: 60px 40px;
            text-align: center;
            max-width: 500px;
            width: 100%;
            animation: slideUp 0.6s ease-out;
        }

        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .error-number {
            font-size: 120px;
            font-weight: 900;
            color: #667eea;
            margin-bottom: 20px;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
            line-height: 1;
        }

        .error-title {
            font-size: 28px;
            font-weight: 700;
            color: #333;
            margin-bottom: 15px;
        }

        .error-message {
            font-size: 16px;
            color: #666;
            margin-bottom: 40px;
            line-height: 1.6;
        }

        .button-container {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }

        .btn {
            padding: 12px 30px;
            border: none;
            border-radius: 50px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }

        .btn::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
            transition: left 0.5s;
        }

        .btn:hover::before {
            left: 100%;
        }

        .btn-primary {
            background: linear-gradient(45deg, #667eea, #764ba2);
            color: white;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
        }

        .btn-secondary {
            background: #f8f9fa;
            color: #333;
            border: 2px solid #e9ecef;
        }

        .btn-secondary:hover {
            background: #e9ecef;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }

        .icon {
            font-size: 18px;
        }

        .floating-shapes {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            overflow: hidden;
            z-index: -1;
        }

        .shape {
            position: absolute;
            opacity: 0.1;
            animation: float 6s ease-in-out infinite;
        }

        .shape:nth-child(1) {
            top: 10%;
            left: 10%;
            animation-delay: 0s;
        }

        .shape:nth-child(2) {
            top: 20%;
            right: 10%;
            animation-delay: 2s;
        }

        .shape:nth-child(3) {
            bottom: 10%;
            left: 20%;
            animation-delay: 4s;
        }

        @keyframes float {
            0%, 100% {
                transform: translateY(0px);
            }
            50% {
                transform: translateY(-20px);
            }
        }

        @media (max-width: 480px) {
            .error-container {
                padding: 40px 20px;
            }

            .error-number {
                font-size: 80px;
            }

            .error-title {
                font-size: 24px;
            }

            .button-container {
                flex-direction: column;
            }

            .btn {
                width: 100%;
                justify-content: center;
            }
        }
    </style>
</head>
<body>
    <div class="floating-shapes">
        <div class="shape">🔍</div>
        <div class="shape">📄</div>
        <div class="shape">🔗</div>
    </div>

    <div class="error-container">
        <div class="error-number">404</div>
        
        <h1 class="error-title">Oops! Không Tìm Thấy Trang</h1>
        
        <div class="error-message">
            Trang bạn đang tìm kiếm có thể đã bị di chuyển, xóa hoặc không tồn tại. 
            Vui lòng kiểm tra lại đường dẫn hoặc quay về trang chính.
        </div>
        
        <div class="button-container">
            <button class="btn btn-primary" onclick="goBack()">
                <span class="icon">←</span>
                Quay Lại
            </button>
            
            <a href="${pageContext.request.contextPath}/jsp/landing.jsp" class="btn btn-secondary">
                <span class="icon">🏠</span>
                Trang Chủ
            </a>
        </div>
    </div>

    <script>
        function goBack() {
            // Kiểm tra xem có history không
            if (window.history.length > 1) {
                window.history.back();
            } else {
                // Nếu không có history, chuyển về trang chủ
                window.location.href = '${pageContext.request.contextPath}/jsp/landing.jsp';
            }
        }

        // Thêm hiệu ứng khi load trang
        window.addEventListener('load', function() {
            document.querySelector('.error-container').style.animation = 'slideUp 0.6s ease-out';
        });

        // Xử lý phím tắt
        document.addEventListener('keydown', function(e) {
            // Nhấn ESC để quay lại
            if (e.key === 'Escape') {
                goBack();
            }
            // Nhấn Enter để về trang chủ
            if (e.key === 'Enter') {
                window.location.href = '${pageContext.request.contextPath}/jsp/landing.jsp';
            }
        });
    </script>
</body>
</html>
