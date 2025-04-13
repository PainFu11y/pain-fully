package org.platform.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.platform.enums.ParticipantStatus;
import org.platform.model.MemberDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventMemberDto {
    private UUID id;
    private EventDto event;
    @JsonProperty("members")
    private List<MemberDto> memberList;
    private ParticipantStatus status;
}
