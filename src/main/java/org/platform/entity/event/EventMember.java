package org.platform.entity.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.platform.entity.Member;
import org.platform.enums.constants.DatabaseConstants;
import org.platform.enums.ParticipantStatus;
import org.platform.model.event.EventMemberDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


/**
 *  Пользователи которые связаны с мероприятием
 *
 */
@Entity
@Table(name = DatabaseConstants.EVENTS_MEMBERS_TABLE, schema = DatabaseConstants.SCHEMA)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventMember {
    @Id
    @GenericGenerator(name = "generator",strategy = "uuid2")
    @GeneratedValue(generator = "generator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToMany
    @JoinColumn(name = "member_id", nullable = false)
    private List<Member> memberList;

    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;// в db по WILL_PARTICIPATE, PARTICIPATED,учавствую будем разделять members


    public EventMemberDto toDto(){
        EventMemberDto dto = new EventMemberDto();
        dto.setId(id);
        dto.setEvent(event.toDto());
        dto.setMemberList(memberList.stream().map(Member::toDto).toList());
        dto.setStatus(status);
        return dto;
    }
    public static EventMember fromDto(EventMemberDto eventMemberDto){
        EventMember eventMember = new EventMember();
        eventMember.setId(eventMemberDto.getId());
        eventMember.setEvent(Event.fromDto(eventMemberDto.getEvent()));
        eventMember.setMemberList(eventMemberDto.getMemberList().stream().map(Member::fromDto).toList());
        eventMember.setStatus(eventMemberDto.getStatus());
        return eventMember;
    }
}
