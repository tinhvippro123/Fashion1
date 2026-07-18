package com.fashionshop.service;

import com.fashionshop.model.Setting;
import com.fashionshop.repository.SettingRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SettingRepository settingRepository;

    /**
     * Gửi email chung lấy template từ bảng Setting
     */
    public void sendEmailWithTemplate(String to, String subject, String templateKey, String[] placeholders, String[] values) {
        try {
            // Lấy template HTML từ DB, nếu chưa có thì dùng template mặc định
            String htmlTemplate = settingRepository.findBySettingKey(templateKey)
                    .map(Setting::getSettingValue)
                    .orElse("<p>Xin chào,</p><p>Đây là email từ hệ thống LUXE.</p>");

            // Replace các biến động bằng giá trị thật (Ví dụ: [CUSTOMER_NAME] -> Nguyễn Văn A)
            if (placeholders != null && values != null && placeholders.length == values.length) {
                for (int i = 0; i < placeholders.length; i++) {
                    htmlTemplate = htmlTemplate.replace(placeholders[i], values[i]);
                }
            }

            // Gửi email
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlTemplate, true); // true = isHtml

            javaMailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Lỗi khi gửi email: " + e.getMessage());
        }
    }

    /**
     * Ví dụ: Gửi email chào mừng khi đăng ký
     */
    public void sendWelcomeEmail(String toEmail, String customerName) {
        String[] placeholders = {"[CUSTOMER_NAME]"};
        String[] values = {customerName};
        sendEmailWithTemplate(toEmail, "Chào mừng đến với LUXE Fashion", "email_template_welcome", placeholders, values);
    }
}
