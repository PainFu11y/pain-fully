package org.platform.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.platform.enums.InvitationStatus;
import org.platform.model.MemberDto;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventInvitationDto {
    private UUID id;
    private MemberDto inviter;
    private MemberDto invitee;
    private EventDto event;
    private InvitationStatus status;
}
