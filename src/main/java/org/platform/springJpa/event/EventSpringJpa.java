package org.platform.springJpa.event;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.platform.entity.Organizer;
import org.platform.entity.event.Event;
import org.platform.entity.event.EventTag;
import org.platform.helper.EventHelper;
import org.platform.model.event.EventDto;
import org.platform.model.event.EventFilterRequest;
import org.platform.model.event.request.EventCreateRequest;
import org.platform.repository.event.EventRepository;
import org.platform.repository.OrganizerRepository;
import org.platform.service.event.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventSpringJpa implements EventService {

    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;
    private final EventHelper eventHelper;

    @Override
    public EventDto createEvent(EventCreateRequest eventDto) {
        UUID currentOrganizerId = getCurrentOrganizerId();

        try {
            Event event = eventHelper.fromEventCreateRequest(eventDto, currentOrganizerId);

            Event savedEvent = eventRepository.save(event);
            return new EventDto(savedEvent);
        } catch (Exception e) {
            throw new RuntimeException("Problem during creating event" + e);
        }
    }

    @Override
    public List<EventDto> getAllEvents() {
        try {
           List<Event>  events = eventRepository.findAll();
           List<EventDto> eventDtos = events.stream()
                   .map(event -> {
                       EventDto dto = event.toDto();
                       if (dto.getOrganizerDto() != null) {
                           dto.getOrganizerDto().setPassword(null);
                       }
                       return dto;
                   })
                   .collect(Collectors.toList());

           return  eventDtos;
        } catch (Exception e) {
            throw new RuntimeException("Problem during getting all events" + e);
        }
    }

    @Override
    public EventDto getEventById(UUID id) {
        try {
            Optional<Event> event = eventRepository.findById(id);
            return event.map(Event::toDto).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Problem during getting event by id");
        }
    }

    @Override
    public EventDto getPublicEvent(UUID id) {
        Optional<Event> byPublicId = eventRepository.findByPublicId(id);
        if (byPublicId.isEmpty()) {
            throw new RuntimeException("Invalid event public id");
        }
        Event event = byPublicId.get();
        if(event.getModerationStatus() == 0){
            throw new RuntimeException("Event still on moderation");
        }

        return event.toDto();
    }

    @Override
    public EventDto updateEvent(UUID id, EventDto updatedEventDto) {
        try {
            Optional<Event> optionalExistingEvent = eventRepository.findById(id);
            if (optionalExistingEvent.isPresent()) {
                Event existingEvent = Event.fromDto(updatedEventDto);
                existingEvent.setId(optionalExistingEvent.get().getId());

                Event updatedEvent = eventRepository.save(existingEvent);
                EventDto eventDto = new EventDto(updatedEvent);
                if (eventDto.getOrganizerDto() != null) {
                    eventDto.getOrganizerDto().setPassword(null);
                }

                return eventDto;
            } else {
                throw new RuntimeException("Problem during updating event");
            }
        } catch (Exception e) {
            throw new RuntimeException("Problem during updating event");
        }
    }

    @Override
    public void deleteEvent(UUID id) {
        try {
            Optional<Event> eventOpt = eventRepository.findById(id);
            eventOpt.ifPresent(eventRepository::delete);
        } catch (Exception e) {
            throw new RuntimeException("Problem during deleting event");
        }
    }


    public Page<Event> searchEventWithFilters(EventFilterRequest filter) {
        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTitle() != null) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + filter.getTitle().toLowerCase() + "%"));
            }
            if (filter.getDescription() != null) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + filter.getDescription().toLowerCase() + "%"));
            }
            if (filter.getFormat() != null) {
                predicates.add(cb.equal(root.get("format"), filter.getFormat()));
            }
            if (filter.getLocation() != null) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + filter.getLocation().toLowerCase() + "%"));
            }
            if (filter.getCategory() != null) {
                predicates.add(cb.equal(root.get("eventCategory").get("name"), filter.getCategory())); // предполагается, что category — это имя категории
            }
            if (filter.getTag() != null) {
                Join<Event, EventTag> tagJoin = root.join("eventTagList", JoinType.LEFT);
                predicates.add(cb.equal(cb.lower(tagJoin.get("name")), filter.getTag().toLowerCase()));
            }
            if (filter.getStatus() != null) {
                LocalDateTime now = LocalDateTime.now();

                switch (filter.getStatus()) {
                    case NOT_STARTED -> predicates.add(cb.greaterThan(root.get("startTime"), now));
                    case ONGOING -> predicates.add(cb.and(
                            cb.lessThanOrEqualTo(root.get("startTime"), now),
                            cb.greaterThanOrEqualTo(root.get("endTime"), now)
                    ));
                    case FINISHED -> predicates.add(cb.lessThan(root.get("endTime"), now));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getLimit(), Sort.by("startTime").descending());
        return eventRepository.findAll(spec, pageable);
    }



    @Override
    public ResponseEntity<String> getImage(UUID eventId) {
        Event event;
        try{
            event = eventRepository.findById(eventId).orElseThrow();
        }catch (Exception e){
            throw new RuntimeException("Problem during retrieving image");
        }
        if(event.getImage() == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(event.getImage());
    }

    @Override
    public ResponseEntity<List<EventDto>> searchEvents(EventFilterRequest filter) {
        return null;
    }

    @Override
    public Long getCountOfEventsForOrganizer() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentEmail = authentication.getName();
            Optional<Organizer> byEmail = organizerRepository.findByEmail(currentEmail);
            if(byEmail.isPresent()){
                return eventRepository.countByOrganizerId(byEmail.get().getId());
            }else {
                throw new RuntimeException("Organizer not found for email " + currentEmail);
            }

        }catch (Exception e){
            throw new RuntimeException("Problem during counting of events");
        }

    }

    public List<Event> getEventStatusONGOINGS() {
        List<Event> events = eventRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        List<Event> ongoingEvents = new ArrayList<>();
        for (Event event : events) {
            if (now.isAfter(event.getStartTime()) && now.isBefore(event.getEndTime())) {
                ongoingEvents.add(event);
            }
        }
        return ongoingEvents;
    }


    private UUID getCurrentOrganizerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // username == email
        Organizer organizer = organizerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Organizer not found by email"));
        return organizer.getId();
    }


}
