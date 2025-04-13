package org.platform.entity.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.platform.enums.constants.DatabaseConstants;
import org.platform.model.event.EventCategoryDto;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.EVENT_CATEGORY_TABLE, schema = DatabaseConstants.SCHEMA)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventCategory {
    @Id
    @GenericGenerator(name = "generator",strategy = "uuid2")
    @GeneratedValue(generator = "generator")
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "eventCategory")
    @RestResource(path = "events", rel = "events")
    private List<Event> eventList;

    public EventCategoryDto toDto(){
        EventCategoryDto dto = new EventCategoryDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEventDtoList(eventList.stream().map(Event::toDto).toList());
        return dto;
    }
    public static EventCategory fromDto(EventCategoryDto dto){
        EventCategory eventCategory = new EventCategory();
        eventCategory.setId(dto.getId());
        eventCategory.setName(dto.getName());
        eventCategory.setEventList(dto.getEventDtoList().stream().map(Event::fromDto).toList());
        return eventCategory;
    }
}
