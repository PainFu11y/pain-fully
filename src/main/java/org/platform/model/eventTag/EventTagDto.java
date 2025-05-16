package org.platform.model.eventTag;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.platform.model.event.EventDto;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventTagDto {
    private UUID id;
    private String name;
    @JsonProperty("events")
    private List<EventDto> eventDtoList;
}
