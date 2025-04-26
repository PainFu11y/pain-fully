package org.platform.entity.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.platform.entity.Member;
import org.platform.enums.constants.DatabaseConstants;
import org.platform.model.member.MemberDto;
import org.platform.model.event.EventDto;
import org.platform.model.event.EventMemberDto;

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



    public EventMemberDto toDto(){
        EventMemberDto dto = new EventMemberDto();
        dto.setId(id);

        EventDto shortEventDto = new EventDto();
        shortEventDto.setId(event.getId());
        shortEventDto.setTitle(event.getTitle());
        shortEventDto.setStartTime(event.getStartTime());
        shortEventDto.setEndTime(event.getEndTime());
        dto.setEvent(shortEventDto);

        dto.setMemberList(memberList.stream()
                .map(member -> {
                    MemberDto mDto = new MemberDto();
                    mDto.setId(member.getId());
                    mDto.setUsername(member.getUsername());
                    mDto.setEmail(member.getEmail());
                    return mDto;
                }).toList());
        return dto;
    }
    public static EventMember fromDto(EventMemberDto eventMemberDto){
        EventMember eventMember = new EventMember();
        eventMember.setId(eventMemberDto.getId());
        eventMember.setEvent(Event.fromDto(eventMemberDto.getEvent()));
        eventMember.setMemberList(eventMemberDto.getMemberList().stream().map(Member::fromDto).toList());
        return eventMember;
    }
}
