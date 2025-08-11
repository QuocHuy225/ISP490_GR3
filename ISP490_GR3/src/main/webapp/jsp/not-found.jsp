<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Không tìm thấy kết quả</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        
        .error-container {
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 40px;
            text-align: center;
            max-width: 400px;
            width: 90%;
        }
        
        .error-icon {
            font-size: 60px;
            color: #e74c3c;
            margin-bottom: 20px;
        }
        
        .error-title {
            font-size: 24px;
            font-weight: bold;
            color: #333;
            margin-bottom: 15px;
        }
        
        .error-message {
            font-size: 16px;
            color: #666;
            margin-bottom: 30px;
        }
        
        .back-button {
            background-color: #3498db;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            transition: background-color 0.3s;
        }
        
        .back-button:hover {
            background-color: #2980b9;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">⚠️</div>
        
        <h1 class="error-title">Không tìm thấy kết quả</h1>
        
        <div class="error-message">
            <c:choose>
                <c:when test="${param.type == 'medical-record'}">
                    Hồ sơ bệnh án với mã "${param.id}" không tồn tại.
                </c:when>
                <c:when test="${param.type == 'patient'}">
                    Bệnh nhân với mã "${param.id}" không tồn tại.
                </c:when>
                <c:when test="${param.type == 'invoice'}">
                    Hóa đơn với mã "${param.id}" không tồn tại.
                </c:when>
                <c:when test="${param.type == 'prescription'}">
                    Đơn thuốc với mã "${param.id}" không tồn tại.
                </c:when>
                <c:when test="${param.type == 'medical-request'}">
                    Yêu cầu y tế với mã "${param.id}" không tồn tại.
                </c:when>
                <c:otherwise>
                    Thông tin bạn đang tìm kiếm không tồn tại.
                </c:otherwise>
            </c:choose>
        </div>
        
        <button class="back-button" onclick="history.back()">
            ← Quay lại
        </button>
    </div>
</body>
</html>
