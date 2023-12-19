package com.tdtu.phone_store_java.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;
    // người gửi
    @Value("${spring.mail.username}") private String sender;

    public Boolean sendMail(String token, String email, String username) {
        try {
            String htmlContent =
                    "Tài khoản và mật khẩu của bạn: " + username + "\n" +
                    "Nhấp vào liên kết sau để kích hoạt tài khoản của bạn: http://localhost:8080/auth/active/" + token;
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(sender);
            mail.setTo(email);
            mail.setSubject("Xác thực người dùng.");
            mail.setText(htmlContent);
            javaMailSender.send(mail);
            return true;
        }
        catch (Exception e) {
            System.out.println("Mail Service- Line 20: "+ e.getMessage());
        }
        return false;
    }
}
