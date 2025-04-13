package org.platform.service;


import org.platform.entity.event.Event;
import org.platform.model.event.EventDto;
import org.platform.model.event.EventFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface EventService {
    EventDto createEvent(EventDto eventDto);

    List<EventDto> getAllEvents();

    EventDto getEventById(UUID id);

    EventDto updateEvent(UUID id,EventDto updatedEventDto);

    void deleteEvent(UUID id);

    List<Event> getEventStatusONGOINGS();

    Page<Event> searchEventWithFilters(EventFilterRequest eventFilterRequest);

    ResponseEntity<String> getImage(UUID eventId);
    ResponseEntity<List<EventDto>> searchEvents(EventFilterRequest filter);

    Long getCountOfEventsForOrganizer();


}
