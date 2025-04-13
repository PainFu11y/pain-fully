package org.platform.entity.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.platform.enums.constants.DatabaseConstants;
import org.platform.model.event.EventTagDto;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.EVENT_TAGS_TABLE,schema = DatabaseConstants.SCHEMA)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventTag {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @ManyToMany(mappedBy = "eventTagList")
    private List<Event> events;

    public EventTagDto toDto(){
        EventTagDto dto = new EventTagDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEventDtoList(events.stream()
                .map(Event::toDto)
                .toList());
        return dto;
    }

    public static EventTag fromDto(EventTagDto dto){
        EventTag tag = new EventTag();
        tag.setId(dto.getId());
        tag.setName(dto.getName());
        tag.setEvents(dto.getEventDtoList().stream().map(Event::fromDto).toList());
        return tag;

    }


}
