package org.platform.helper;

import lombok.AllArgsConstructor;
import org.platform.entity.Organizer;
import org.platform.entity.event.Event;
import org.platform.entity.event.EventCategory;
import org.platform.model.event.request.EventCreateRequest;
import org.platform.repository.OrganizerRepository;

import java.util.ArrayList;
import java.util.Optional;

@AllArgsConstructor
public class EventHelper {

    private final OrganizerRepository organizerRepository;

    public  Event fromEventCreateRequest(EventCreateRequest request){
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());

        Optional<Organizer> byId = organizerRepository.findById(request.getOrganizerId());
        if(byId.isEmpty()){
            throw new RuntimeException("Wrong organizer Id");
        }
        event.setOrganizer(byId.get());
        event.setFormat(request.getFormat());
        event.setLocation(request.getLocation());
        event.setEventCategory(EventCategory.fromDto(request.getEventCategoryDto()));
        event.setLongitude(request.getLongitude());
        event.setLatitude(request.getLatitude());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setContactInfo(request.getContactInfo());

        event.setModerationStatus(0);
        event.setModerationStatusInfo(null);
        event.setEventMembers(new ArrayList<>());
        event.setImage(null);
        return event;
    }
}
