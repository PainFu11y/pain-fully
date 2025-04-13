package org.platform.springJpa.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.platform.entity.Member;
import org.platform.service.email.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailSpringJpa implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String email, String token) {
//        try{
//            String verifyToken =   token;
//
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(email);
//            message.setSubject("Подтвердите свою почту");
//            message.setText("Ваш код для подтверждения аккаунта: " + verifyToken);
//
//            mailSender.send(message);
//        }catch(Exception e){
//            throw new RuntimeException(e);
//        }
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Подтвердите свою почту");

            String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px; color: #333;">
                    <div style="max-width: 500px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);">
                        <h2 style="color: #4CAF50;">Добро пожаловать!</h2>
                        <p style="font-size: 16px;">Спасибо за регистрацию. Пожалуйста, подтвердите свою почту, используя код ниже:</p>
                        <div style="background-color: #f0f0f0; padding: 15px; border-radius: 5px; font-size: 24px; font-weight: bold; text-align: center; letter-spacing: 3px; color: #333;">
                            %s
                        </div>
                        <a href="https://yourdomain.com/verify?token=%s" style="display: inline-block; margin-top: 20px; padding: 12px 25px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;">
                            Подтвердить
                        </a>
                        <p style="margin-top: 30px; font-size: 12px; color: #888; text-align: center;">
                            Если вы не регистрировались, просто проигнорируйте это письмо.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(token, token);

            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Ошибка при отправке email", e);
        }

    }

    @Override
    public void sendMessage(String email, String message, String subject) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        mailSender.send(simpleMailMessage);
    }
}
