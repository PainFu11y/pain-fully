package org.platform.entity.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import org.platform.entity.Member;
import org.platform.enums.constants.DatabaseConstants;
import org.platform.enums.InvitationStatus;

import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.EVENT_INVITATIONS_TABLE, schema = DatabaseConstants.SCHEMA)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventInvitation {
    @Id
    @GenericGenerator(name = "generator",strategy = "uuid2")
    @GeneratedValue(generator = "generator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "inviter_id")
    private Member inviter;

    @ManyToOne
    @JoinColumn(name = "invitee_id")
    private Member invitee;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Enumerated(EnumType.STRING)
    private InvitationStatus status;
}
