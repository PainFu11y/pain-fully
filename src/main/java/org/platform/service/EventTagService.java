package org.platform.service;

import org.platform.model.eventTag.EventTagCreateRequest;
import org.platform.model.eventTag.EventTagDto;

import java.util.List;
import java.util.UUID;

public interface EventTagService {
    EventTagCreateRequest createEventTag(EventTagCreateRequest eventDto);

    List<EventTagDto> getAllEventTags();

    EventTagDto getEventTagById(UUID id);

    EventTagDto getEventTagByName(String name);

    EventTagDto updateEventTag(UUID id,EventTagDto updatedEventTagDto);

    void deleteEventTag(UUID id);
}
