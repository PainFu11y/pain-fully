package org.platform.service;

import org.platform.model.event.EventMemberDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface EventMemberService {
    ResponseEntity<String> createEvent(UUID eventId);

    EventMemberDto createEventMember(EventMemberDto eventMemberDto);

    EventMemberDto updateEventMember(UUID id, EventMemberDto eventMemberDto);

    EventMemberDto getEventMember(UUID id);

    List<EventMemberDto> getAllEventMembers();

    void deleteEventMember(UUID id);


}
