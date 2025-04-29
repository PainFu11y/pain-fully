package org.platform.service.email;


import org.platform.entity.Member;
import org.platform.entity.event.Event;

public interface EmailService {
    boolean sendEmailVerificationCode(String email);
    boolean sendForgotPasswordCode(String email);
    void sendEventJoinMessage(String to, String subject, String htmlContent);
    void sendEventInvitationEmail(Member friend, Member sender, Event event);


}
