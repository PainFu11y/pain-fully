package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.entity.event.Event;
import org.platform.entity.verification.OrganizerVerification;
import org.platform.enums.constants.RoutConstants;
import org.platform.model.event.EventDto;
import org.platform.model.event.EventFilterDto;
import org.platform.model.event.EventFilterRequest;
import org.platform.model.organizer.OrganizerVerificationDto;
import org.platform.model.organizer.createRequest.OrganizerCreateRequestDto;
import org.platform.model.organizer.OrganizerDto;
import org.platform.model.organizer.createRequest.OrganizerUpdateRequestDto;
import org.platform.model.response.PaginatedResponse;
import org.platform.service.EventService;
import org.platform.service.OrganizerService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.VERSION + RoutConstants.ORGANIZER)
@RequiredArgsConstructor
public class OrganizerController {

    private final OrganizerService organizerService;


    @Operation(summary = "Создание организатора")
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody OrganizerDto createOrganizer(@RequestBody OrganizerCreateRequestDto organizerDto) {
        log.info("Received request to create organizer.");
        try {
            OrganizerDto organizer = organizerService.createOrganizer(organizerDto);
            log.info("Organizer created successfully: {}", organizer);
            return organizer;
        } catch (Exception e) {
            log.error("Error creating organizer: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create organizer", e);
        }
    }

    @Operation(summary = "Изменение организатора")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public @ResponseBody OrganizerUpdateRequestDto updateOrganizer(@RequestBody OrganizerUpdateRequestDto organizerDto) {
        log.info("Received request to update current organizer");
        try {
            OrganizerUpdateRequestDto updatedOrganizer = organizerService.updateOrganizer(organizerDto);
            log.info("Organizer updated successfully: {}", updatedOrganizer);
            return updatedOrganizer;
        } catch (Exception e) {
            log.error("Error updating organizer: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update organizer", e);
        }
    }

    @Operation(summary = "Получить организатора по Id")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody OrganizerDto getOrganizerById(@PathVariable UUID id) {
        log.info("Received request to get organizer by ID: {}", id);
        try {
            OrganizerDto organizer = organizerService.getById(id);
            log.info("Organizer retrieved successfully: {}", organizer);
            return organizer;
        } catch (Exception e) {
            log.error("Error retrieving organizer: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve organizer", e);
        }
    }

    @Operation(summary = "Получить всех организаторов")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<OrganizerDto> getAllOrganizers() {
        log.info("Received request to get all organizers.");
        try {
            List<OrganizerDto> organizers = organizerService.getAllOrganizers();
            log.info("Retrieved {} organizers.", organizers.size());
            return organizers;
        } catch (Exception e) {
            log.error("Error retrieving all organizers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve organizers", e);
        }
    }

    @Operation(summary = "Удаление организатора по Id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrganizer(@PathVariable UUID id) {
        log.info("Received request to delete organizer with ID: {}", id);
        try {
            organizerService.deleteOrganizer(id);
            log.info("Organizer deleted successfully.");
        } catch (Exception e) {
            log.error("Error deleting organizer: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete organizer", e);
        }
    }

    @Operation(summary = "Отправка документа для верификации")
    @PostMapping(value = "/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody OrganizerVerificationDto sendVerifyDocument(
            @RequestParam("file") MultipartFile file
    ) {
        return organizerService.sendVerifyDocument(file);

    }

    @Operation(summary = "Получить все мероприятия текущего организатора")
    @GetMapping("/my-events")
    public ResponseEntity<List<EventDto>> getMyEvents() {
        List<EventDto> events = organizerService.getMyEvents();
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Фильтрация мероприятий текущего организатора")
    @PostMapping("/filter")
    public ResponseEntity<PaginatedResponse<EventFilterDto>> filterMyEvents(@RequestBody EventFilterRequest request) {
        Page<Event> events = organizerService.filterMyEvents(request);

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


}
