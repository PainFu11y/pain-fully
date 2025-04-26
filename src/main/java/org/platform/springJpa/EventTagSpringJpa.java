package org.platform.springJpa;

import lombok.RequiredArgsConstructor;
import org.platform.entity.event.EventTag;
import org.platform.model.eventTag.EventTagCreateRequest;
import org.platform.model.eventTag.EventTagDto;
import org.platform.repository.EventTagRepository;
import org.platform.service.event.EventTagService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventTagSpringJpa implements EventTagService {

    private final EventTagRepository eventTagRepository;

    @Override
    public EventTagCreateRequest createEventTag(EventTagCreateRequest eventDto) {
        try {
            EventTag eventTag = new EventTag();
            eventTag.setName(eventDto.getName());

            eventTagRepository.save(eventTag);
            return eventDto;
        } catch (Exception e) {
            throw new RuntimeException("Error creating event tag", e);
        }
    }

    @Override
    public List<EventTagDto> getAllEventTags() {
        try {
            List<EventTag> eventTags = eventTagRepository.findAll();
            return eventTags.stream()
                    .map(EventTag::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error getting all event tags", e);
        }
    }

    @Override
    public EventTagDto getEventTagById(UUID id) {
        try {
            Optional<EventTag> eventTagById = eventTagRepository.findById(id);
            return eventTagById.map(EventTag::toDto).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Error getting event tag", e);
        }
    }

    @Override
    public EventTagDto getEventTagByName(String name) {
        try {
            Optional<EventTag> eventTag = eventTagRepository.findByName(name);
            return eventTag.map(EventTag::toDto).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Problem during getting event tag by name");
        }
    }

    @Override
    public EventTagDto updateEventTag(UUID id, EventTagDto updatedEventTagDto) {
        try {
            Optional<EventTag> existingEventTagOpt = eventTagRepository.findById(id);
            if (existingEventTagOpt.isPresent()) {
                EventTag existingEventTag = existingEventTagOpt.get();
                existingEventTag.setName(updatedEventTagDto.getName());


                EventTag updatedEventTag = eventTagRepository.save(existingEventTag);
                updatedEventTagDto.setId(id);
                return updatedEventTagDto;
            } else {
               throw new RuntimeException("Error fetching event tag by id: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching event tag by id: " + id, e);
        }
    }

    @Override
    public void deleteEventTag(UUID id) {
        try {
            Optional<EventTag> eventTagOpt = eventTagRepository.findById(id);
            eventTagOpt.ifPresent(eventTagRepository::delete);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting event tag by id: " + id, e);
        }
    }



}
