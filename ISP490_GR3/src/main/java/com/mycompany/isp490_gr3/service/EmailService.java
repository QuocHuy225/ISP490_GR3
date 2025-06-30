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
    private static final String SENDER_EMAIL = "tunpa2k3@gmail.com"; // Thay bằng email của bạn
    private static final String SENDER_PASSWORD = "gxnw cooq tnaj ggsu"; // Thay bằng App Password của Gmail
    
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
            message.setFrom(new InternetAddress(SENDER_EMAIL, "ISP490 Medical System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Xác thực tài khoản - ISP490 Medical System");
            
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
     * Create HTML email content
     */
    private String createEmailContent(String userFullName, String verificationCode) {
        return "<html>" +
            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;\">" +
                    "<h2 style=\"color: #2c3e50; text-align: center;\">Xác thực tài khoản</h2>" +
                    "<p>Chào <strong>" + userFullName + "</strong>,</p>" +
                    "<p>Cảm ơn bạn đã đăng ký tài khoản tại ISP490 Medical System.</p>" +
                    "<p>Để hoàn tất việc đăng ký, vui lòng sử dụng mã xác thực sau:</p>" +
                    "<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 5px; text-align: center; margin: 20px 0;\">" +
                        "<h1 style=\"color: #e74c3c; font-size: 36px; margin: 0; letter-spacing: 5px;\">" + verificationCode + "</h1>" +
                    "</div>" +
                    "<p><strong>Lưu ý:</strong> Mã xác thực này có hiệu lực trong 15 phút.</p>" +
                    "<p>Nếu bạn không đăng ký tài khoản này, vui lòng bỏ qua email này.</p>" +
                    "<hr style=\"border: none; border-top: 1px solid #eee; margin: 30px 0;\">" +
                    "<p style=\"font-size: 12px; color: #666;\">" +
                        "Trân trọng,<br>" +
                        "Đội ngũ ISP490 Medical System" +
                    "</p>" +
                "</div>" +
            "</body>" +
            "</html>";
    }
} 