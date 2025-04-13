package org.platform.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventCategoryDto {
    private UUID id;
    private String name;
    private List<EventDto> eventDtoList;
}
