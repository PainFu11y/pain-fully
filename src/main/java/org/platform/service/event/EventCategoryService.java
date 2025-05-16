package org.platform.service.event;

import org.platform.model.event.EventCategoryDto;

import java.util.List;
import java.util.UUID;

public interface EventCategoryService {
    EventCategoryDto createEventCategory(EventCategoryDto eventcategoryDto);

    EventCategoryDto updateEventCategory(UUID id, EventCategoryDto eventcategoryDto);

    EventCategoryDto getEventCategoryById(UUID id);

    List<EventCategoryDto> getAllEventCategories();

    void deleteEventCategory(UUID id);
}
