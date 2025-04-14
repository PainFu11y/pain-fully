package org.platform.service.email;


import org.platform.entity.Member;
import org.platform.entity.event.Event;

public interface EmailService {
    void sendVerificationEmail(String email, String token);
    void sendMessage(String email, String message, String subject);
    void sendEventInvitationEmail(Member friend, Member sender, Event event);

}
