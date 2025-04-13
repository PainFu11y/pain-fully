package org.platform.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.enums.constants.RoutConstants;
import org.platform.model.event.EventTagDto;
import org.platform.service.EventTagService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.VERSION + RoutConstants.EVENT_TAG)
@RequiredArgsConstructor
public class EventTagController {

    private final EventTagService eventTagService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody EventTagDto createEventTag(@RequestBody EventTagDto eventTagDto) {
        log.info("Received request to create event tag: {}", eventTagDto.getName());
        EventTagDto createdEventTag = eventTagService.createEventTag(eventTagDto);
        log.info("Successfully created event tag with name: {}", createdEventTag.getName());
        return createdEventTag;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public @ResponseBody EventTagDto updateEventTag(@PathVariable UUID id, @RequestBody EventTagDto eventTagDto) {
        log.info("Received request to update event tag with ID: {}", id);
        EventTagDto updatedEventTag = eventTagService.updateEventTag(id, eventTagDto);
        log.info("Successfully updated event tag with ID: {}", updatedEventTag.getId());
        return updatedEventTag;
    }

    @GetMapping("/{id}")
    public @ResponseBody EventTagDto getEventTagById(@PathVariable UUID id) {
        log.info("Received request to get event tag by ID: {}", id);
        EventTagDto eventTag = eventTagService.getEventTagById(id);
        log.info("Successfully fetched event tag with ID: {}", eventTag.getId());
        return eventTag;
    }

    @GetMapping("/name/{name}")
    public @ResponseBody EventTagDto getEventTagByName(@PathVariable String name) {
        log.info("Received request to get event tag by name: {}", name);
        EventTagDto eventTag = eventTagService.getEventTagByName(name);
        log.info("Successfully fetched event tag with name: {}", eventTag.getName());
        return eventTag;
    }

    @GetMapping
    public @ResponseBody List<EventTagDto> getAllEventTags() {
        log.info("Received request to get all event tags");
        List<EventTagDto> eventTags = eventTagService.getAllEventTags();
        log.info("Successfully fetched all event tags");
        return eventTags;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEventTag(@PathVariable UUID id) {
        log.info("Received request to delete event tag with ID: {}", id);
        eventTagService.deleteEventTag(id);
        log.info("Successfully deleted event tag with ID: {}", id);
    }
}
