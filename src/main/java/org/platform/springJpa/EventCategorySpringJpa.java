package org.platform.springJpa;

import lombok.RequiredArgsConstructor;
import org.platform.entity.event.Event;
import org.platform.entity.event.EventCategory;
import org.platform.model.event.EventCategoryDto;
import org.platform.repository.EventCategoryRepository;
import org.platform.repository.EventRepository;
import org.platform.service.EventCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventCategorySpringJpa implements EventCategoryService {
    private final EventCategoryRepository repository;
    private final EventRepository eventRepository;
    private final CharacterEncodingFilter characterEncodingFilter;

    @Override
    public EventCategoryDto createEventCategory(EventCategoryDto eventcategoryDto) {
        Optional<EventCategory> byName;
        try{
            byName = repository.findByName(eventcategoryDto.getName());
            if(byName.isPresent()){
                throw new RuntimeException("Event category name already exists");
            }
        }catch (Exception e){
            throw new RuntimeException("Error while creating event category");
        }
        EventCategory eventCategory;
        try{
          eventCategory = repository.save(EventCategory.fromDto(eventcategoryDto));
        }catch (Exception e){
            throw new RuntimeException("Error while creating event category");
        }

        return eventCategory.toDto();

    }

    @Override
    public EventCategoryDto updateEventCategory(UUID id, EventCategoryDto eventcategoryDto) {
        EventCategory eventCategory = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event Category not found"));

        eventCategory.setName(eventcategoryDto.getName());
        eventCategory.setEventList(eventcategoryDto.getEventDtoList().stream().map(Event::fromDto).toList());

        EventCategory updated = repository.save(eventCategory);
        return updated.toDto();
    }

    @Override
    public EventCategoryDto getEventCategoryById(UUID id) {
        return repository.findById(id)
                .map(EventCategory::toDto)
                .orElseThrow(() -> new RuntimeException("Event Category not found"));
    }

    @Override
    public List<EventCategoryDto> getAllEventCategories() {
        return repository.findAll().stream()
                .map(EventCategory::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEventCategory(UUID id) {
        repository.deleteById(id);
    }
}
