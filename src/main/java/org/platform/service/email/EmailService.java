package org.platform.service.email;


import org.platform.entity.Member;
import org.platform.entity.event.Event;
import org.platform.model.event.EventDto;
import org.platform.model.member.MemberDto;

public interface EmailService {
    boolean sendEmailVerificationCode(String email);
    boolean sendForgotPasswordCode(String email);
    void sendEventJoinMessage(String to, String subject, String htmlContent);
    void sendEventInvitationEmail(Member friend, Member sender, Event event);
    boolean send2faCode(String email);
    boolean sendNewEvent(String email, EventDto eventDto, MemberDto memberDto);


}
