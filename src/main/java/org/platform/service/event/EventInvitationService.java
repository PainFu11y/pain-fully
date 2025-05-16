package org.platform.service.event;

import org.platform.model.event.EventInvitationDto;

import java.util.List;
import java.util.UUID;

public interface EventInvitationService {
    EventInvitationDto createEventInvitation(EventInvitationDto eventInvitationDto);

    EventInvitationDto updateEventInvitation(UUID id, EventInvitationDto eventInvitationDto);

    List<EventInvitationDto> getAllEventInvitations();

    EventInvitationDto getEventInvitation(UUID id);

    void deleteEventInvitation(UUID id);
}
