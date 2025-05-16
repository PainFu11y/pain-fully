package org.platform.helper;

import lombok.RequiredArgsConstructor;
import org.platform.entity.Organizer;
import org.platform.entity.event.Event;
import org.platform.entity.event.EventCategory;
import org.platform.entity.event.EventTag;
import org.platform.model.event.EventCategoryDto;
import org.platform.model.event.request.EventCreateRequest;
import org.platform.repository.event.EventCategoryRepository;
import org.platform.repository.event.EventTagRepository;
import org.platform.repository.OrganizerRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventHelper {

    private final OrganizerRepository organizerRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final EventTagRepository eventTagRepository;

    public  Event fromEventCreateRequest(EventCreateRequest request, UUID organizerId){
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());

        Optional<Organizer> byId = organizerRepository.findById(organizerId);
        if(byId.isEmpty()){
            throw new RuntimeException("Wrong organizer Id");
        }
        event.setOrganizer(byId.get());
        event.setFormat(request.getFormat());
        event.setLocation(request.getLocation());
        UUID eventCategoryUUID = request.getEventCategoryUUID();
        Optional<EventCategory> eventCategory = eventCategoryRepository.findById(eventCategoryUUID);
        if(eventCategory.isEmpty()){
            throw new RuntimeException("Wrong eventCategory Id");
        }
        EventCategoryDto eventCategoryFromDb = eventCategory.get().toDto();

        event.setEventCategory(EventCategory.fromDto(eventCategoryFromDb));
        event.setLongitude(request.getLongitude());
        event.setLatitude(request.getLatitude());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setContactInfo(request.getContactInfo());

        List<UUID> tagIds = request.getEventTagDtoUUIDList();
        List<EventTag> tags = tagIds.stream()
                .map(id -> eventTagRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Event tag not found: " + id)))
                .collect(Collectors.toList());
        event.setEventTagList(tags);

        event.setModerationStatus(0);
        event.setModerationStatusInfo(null);
        event.setEventMembers(new ArrayList<>());
        event.setImage(null);
        return event;
    }


}
