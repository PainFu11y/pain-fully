package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.entity.event.Event;
import org.platform.enums.constants.RoutConstants;
import org.platform.model.event.EventDto;
import org.platform.model.event.EventFilterDto;
import org.platform.model.event.EventFilterRequest;
import org.platform.model.event.request.EventCreateRequest;
import org.platform.model.response.PaginatedResponse;
import org.platform.repository.EventRepository;
import org.platform.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.VERSION + RoutConstants.EVENT)
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventRepository eventRepository;

    @Operation(summary = "Создать мероприятие")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody EventDto createEvent(@RequestBody EventCreateRequest eventDto) {
        log.info("Received request to create event by title: {}", eventDto.getTitle());
        EventDto event = eventService.createEvent(eventDto);
        log.info("Successfully created event with ID: {}", event.getId());
        return event;
    }

    @Operation(summary = "Изменить мероариятие")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public @ResponseBody EventDto updateEvent(@PathVariable UUID id, @RequestBody EventDto eventDto) {
        log.info("Received request to update event with ID: {}", id);
        EventDto updatedEventDto = eventService.updateEvent(id, eventDto);
        log.info("Successfully updated event with ID: {}", eventDto.getId());
        return updatedEventDto;
    }

    @Operation(summary = "Получить мероприятие по id")
    @GetMapping("/{id}")
    public @ResponseBody EventDto getEventById(@PathVariable UUID id) {
        log.info("Received request to get event by ID: {}", id);
        EventDto eventById = eventService.getEventById(id);
        log.info("Successfully fetched event with ID: {}", id);
        return eventById;
    }
    @Operation(summary = "Получить все мероприятия")
    @GetMapping
    public @ResponseBody List<EventDto> getAllEvents() {
        log.info("Received request to get all events");
        List<EventDto> allEvents = eventService.getAllEvents();
        log.info("Successfully fetched all events");
        return allEvents;
    }

    @Operation(summary = "Удалить мероприятие по id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable UUID id) {
        log.info("Received request to delete event with ID: {}", id);
        eventService.deleteEvent(id);
        log.info("Successfully deleted event with ID: {}", id);
    }

    //image snippet
    @Operation(summary = "Загрузить изображение для мероприятия")
    @PostMapping("/upload-img")
    public ResponseEntity<String> uploadImageToDb(@RequestParam("file") MultipartFile file, @RequestParam("evendId") UUID eventId) throws IOException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        byte[] bytes = file.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(bytes);

        eventRepository.save(event);

        return ResponseEntity.ok("Uploaded");
    }

    @Operation(summary = "Получить изображение по id")
    @GetMapping("/image/{id}")
    public ResponseEntity<String> getImage(@PathVariable UUID id) {
       log.info("Received request to get image with ID: {}", id);
        ResponseEntity<String> image = eventService.getImage(id);
        return image;
    }



    @Operation(summary = "Фильтр мероприятий с пагинацией")
    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse<EventFilterDto>> searchEvents(@RequestBody EventFilterRequest filter) {
        Page<Event> events = eventService.searchEventWithFilters(filter);

        List<EventFilterDto> eventDtos = events.getContent()
                .stream()
                .map(Event::toFilterDto)
                .collect(Collectors.toList());

        PaginatedResponse<EventFilterDto> response = PaginatedResponse.<EventFilterDto>builder()
                .content(eventDtos)
                .page(events.getNumber())
                .totalPages(events.getTotalPages())
                .totalItems(events.getTotalElements())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получить количество мероприятий организатора")
    @GetMapping("/count-of-events")
    public ResponseEntity<Long> getCountOfEventsForOrganizer() {
        log.info("Received request to get count of events for organizer");
        Long count = eventService.getCountOfEventsForOrganizer();
        log.info("Successfully fetched count of events for organizer");

        return ResponseEntity.ok(count);
    }





}

