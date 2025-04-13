package org.platform.service.email;


public interface EmailService {
    void sendVerificationEmail(String email, String token);
    void sendMessage(String email, String message, String subject);
}
