package org.platform.springJpa.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.platform.entity.Member;
import org.platform.entity.event.Event;
import org.platform.service.email.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class EmailSpringJpa implements EmailService {
    private final JavaMailSender mailSender;
    @Override
    public void sendVerificationEmail(String email, String token) {
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


    public void sendEventJoinMessage(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // ВАЖНО: включаем HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Не удалось отправить письмо", e);
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

    @Override
    public void sendEventInvitationEmail(Member friend, Member sender, Event event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(friend.getEmail());
            helper.setSubject("📩 Приглашение на мероприятие!");

            Locale russian = new Locale("ru");

            String dayOfWeek = event.getStartTime().getDayOfWeek().getDisplayName(TextStyle.FULL, russian);
            String day = String.valueOf(event.getStartTime().getDayOfMonth());
            String month = event.getStartTime().getMonth().getDisplayName(TextStyle.FULL, russian);
            String year = String.valueOf(event.getStartTime().getYear());
            String time = event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));

            String formattedDate = String.format("в %s, %s %s %s года в %s", dayOfWeek, day, month, year, time);

            String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);">
                    <h2 style="color: #4CAF50;">Привет, %s!</h2>
                    <p><strong>%s</strong> пригласил(а) вас на мероприятие:</p>
                    <h3>%s</h3>
                    <p style="font-size: 14px;"><strong>Описание:</strong> %s</p>
                    <p style="font-size: 14px;"><strong>Локация:</strong> %s</p>
                    <p style="font-size: 14px;"><strong>Время:</strong> %s</p>
                    <p style="margin-top: 30px; font-size: 12px; color: #888; text-align: center;">
                        Это письмо создано автоматически. Пожалуйста, не отвечайте на него.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(friend.getUsername(),
                    sender.getUsername(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getLocation(),
                    formattedDate);

            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Не удалось отправить приглашение на мероприятие", e);
        }
    }

}
