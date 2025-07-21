package com.mycompany.isp490_gr3.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.Random;

/**
 * Service for sending email verification codes
 */
public class EmailService {
    
    // Email configuration - Thay đổi thông tin này theo Gmail của bạn
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = "tungbeok3@gmail.com"; // Thay bằng email của bạn
    private static final String SENDER_PASSWORD = "gmpg ombp pkbo lshw"; // Thay bằng App Password của Gmail
    
    /**
     * Generate a random 6-digit verification code
     */
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
    
    /**
     * Send verification email to user
     */
    public boolean sendVerificationEmail(String recipientEmail, String verificationCode, String userFullName) {
        try {
            // Setup mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.transport.protocol", "smtp");
            
            // Create authenticator
            Authenticator auth = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            };
            
            // Create session
            Session session = Session.getInstance(props, auth);
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "Clinic System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Xác thực tài khoản - Medical Clinic");
            
            // Create email content
            String emailContent = createEmailContent(userFullName, verificationCode);
            message.setContent(emailContent, "text/html; charset=utf-8");
            
            // Send message
            Transport.send(message);
            
            System.out.println("Verification email sent successfully to: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Send reset password email to user
     */
    public boolean sendResetPasswordEmail(String recipientEmail, String resetToken, String userFullName, String baseUrl) {
        try {
            // Setup mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.transport.protocol", "smtp");
            
            // Create authenticator
            Authenticator auth = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            };
            
            // Create session
            Session session = Session.getInstance(props, auth);
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "Clinic System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Khôi phục mật khẩu - Medical Clinic");
            
            // Create email content
            String emailContent = createResetPasswordEmailContent(userFullName, resetToken, baseUrl);
            message.setContent(emailContent, "text/html; charset=utf-8");
            
            // Send message
            Transport.send(message);
            
            System.out.println("Reset password email sent successfully to: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("Failed to send reset password email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Create HTML email content
     */
    private String createEmailContent(String userFullName, String verificationCode) {
        return "<html>" +
            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;\">" +
                    "<h2 style=\"color: #2c3e50; text-align: center;\">Xác thực tài khoản</h2>" +
                    "<p>Chào <strong>" + userFullName + "</strong>,</p>" +
                    "<p>Cảm ơn bạn đã đăng ký tài khoản tại Medical Clinic.</p>" +
                    "<p>Để hoàn tất việc đăng ký, vui lòng sử dụng mã xác thực sau:</p>" +
                    "<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 5px; text-align: center; margin: 20px 0;\">" +
                        "<h1 style=\"color: #e74c3c; font-size: 36px; margin: 0; letter-spacing: 5px;\">" + verificationCode + "</h1>" +
                    "</div>" +
                    "<p><strong>Lưu ý:</strong> Mã xác thực này có hiệu lực trong 15 phút.</p>" +
                    "<p>Nếu bạn không đăng ký tài khoản này, vui lòng bỏ qua email này.</p>" +
                    "<hr style=\"border: none; border-top: 1px solid #eee; margin: 30px 0;\">" +
                    "<p style=\"font-size: 12px; color: #666;\">" +
                        "Trân trọng,<br>" +
                        "Đội ngũ Medical Clinic" +
                    "</p>" +
                "</div>" +
            "</body>" +
            "</html>";
    }
    
    /**
     * Create HTML email content for reset password
     */
    private String createResetPasswordEmailContent(String userFullName, String resetToken, String baseUrl) {
        String resetUrl = baseUrl + "/auth/reset-password?token=" + resetToken;
        
        return "<html>" +
            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;\">" +
                    "<h2 style=\"color: #2c3e50; text-align: center;\">Khôi phục mật khẩu</h2>" +
                    "<p>Chào <strong>" + userFullName + "</strong>,</p>" +
                    "<p>Chúng tôi đã nhận được yêu cầu khôi phục mật khẩu cho tài khoản của bạn tại Ánh Dương Clinic.</p>" +
                    "<p>Để đặt lại mật khẩu, vui lòng nhấp vào nút bên dưới:</p>" +
                    "<div style=\"text-align: center; margin: 30px 0;\">" +
                        "<a href=\"" + resetUrl + "\" style=\"background-color: #3b82f6; color: white; padding: 15px 30px; text-decoration: none; border-radius: 8px; font-weight: bold; display: inline-block;\">" +
                            "Khôi phục mật khẩu" +
                        "</a>" +
                    "</div>" +
                    "<p>Hoặc copy và paste link sau vào trình duyệt:</p>" +
                    "<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 5px; word-break: break-all; font-family: monospace; font-size: 14px;\">" +
                        resetUrl +
                    "</div>" +
                    "<p><strong>Lưu ý quan trọng:</strong></p>" +
                    "<ul>" +
                        "<li>Link này có hiệu lực trong 30 phút</li>" +
                        "<li>Chỉ sử dụng được một lần</li>" +
                        "<li>Nếu bạn không yêu cầu khôi phục mật khẩu, vui lòng bỏ qua email này</li>" +
                    "</ul>" +
                    "<hr style=\"border: none; border-top: 1px solid #eee; margin: 30px 0;\">" +
                    "<p style=\"font-size: 12px; color: #666;\">" +
                        "Trân trọng,<br>" +
                        "Đội ngũ Medical Clinic<br>" +
                        "Email: info@anhduongclinic.com<br>" +
                        "Hotline: +84765317988" +
                    "</p>" +
                "</div>" +
            "</body>" +
            "</html>";
    }
} 