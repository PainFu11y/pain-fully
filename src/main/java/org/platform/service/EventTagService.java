package org.platform.service;

import org.platform.model.event.EventTagDto;

import java.util.List;
import java.util.UUID;

public interface EventTagService {
    EventTagDto createEventTag(EventTagDto eventDto);

    List<EventTagDto> getAllEventTags();

    EventTagDto getEventTagById(UUID id);

    EventTagDto getEventTagByName(String name);

    EventTagDto updateEventTag(UUID id,EventTagDto updatedEventTagDto);

    void deleteEventTag(UUID id);
}
