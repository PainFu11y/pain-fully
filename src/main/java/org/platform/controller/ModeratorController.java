package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.enums.OrganizersVerifyStatus;
import org.platform.enums.constants.RoutConstants;
import org.platform.model.ModeratorDto;
import org.platform.service.ModeratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.VERSION + RoutConstants.MODERATOR)
@RequiredArgsConstructor
@Slf4j
public class ModeratorController {

    private final ModeratorService moderatorService;

    @Operation(summary = "Создание модератора")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody ModeratorDto createModerator(@RequestBody ModeratorDto moderatorDto) {
        log.info("Received request to create a new moderator.");
        ModeratorDto createdModerator = moderatorService.createModerator(moderatorDto);
        log.info("Moderator created successfully: {}", createdModerator);
        return createdModerator;
    }

    @Operation(summary = "Изменение модератора")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public @ResponseBody ModeratorDto updateModerator(@PathVariable UUID id, @RequestBody ModeratorDto moderatorDto) {
        log.info("Received request to update moderator with ID: {}", id);
        moderatorDto.setId(id);
        ModeratorDto updatedModerator = moderatorService.updateModerator(moderatorDto);
        log.info("Moderator updated successfully: {}", updatedModerator);
        return updatedModerator;
    }

    @Operation(summary = "Получить модератора по id")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody ModeratorDto getModeratorById(@PathVariable UUID id) {
        log.info("Received request to get moderator by ID: {}", id);
        ModeratorDto moderator = moderatorService.getModeratorById(id);
        log.info("Moderator retrieved successfully: {}", moderator);
        return moderator;
    }

    @Operation(summary = "Получить модератора по username")
    @GetMapping("/username/{username}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody ModeratorDto getModeratorByUsername(@PathVariable String username) {
        log.info("Received request to get moderator by username: {}", username);
        ModeratorDto moderator = moderatorService.getModeratorByUsername(username);
        log.info("Moderator retrieved successfully: {}", moderator);
        return moderator;
    }

    @Operation(summary = "Получить всех модераторов")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<ModeratorDto> getAllModerators() {
        log.info("Received request to get all moderators.");
        List<ModeratorDto> moderators = moderatorService.getAllModerators();
        log.info("Retrieved {} moderators.", moderators.size());
        return moderators;
    }
    @Operation(summary = "Удалить модератора по id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteModerator(@PathVariable UUID id) {
        log.info("Received request to delete moderator with ID: {}", id);
        ModeratorDto moderatorDto = moderatorService.getModeratorById(id);
        moderatorService.deleteModerator(moderatorDto);
        log.info("Moderator deleted successfully.");
    }

    @Operation(summary = "Изменить статус верификации организатора (только для MODERATOR)")
    @PutMapping("/organizer-status")
    public ResponseEntity<String> changeVerifyStatusFroOrganizer(
            @RequestParam String email,
            @RequestParam OrganizersVerifyStatus status
    ) {
        boolean updated = moderatorService.changeVerifyStatusForOrganizer(email, status);
        if (updated) {
            return ResponseEntity.ok("Статус верификации успешно обновлён");
        } else {
            return ResponseEntity.ok("Статус уже был установлен");
        }
    }

    @Operation(summary = "Изменение статуса модерации события (только для MODERATOR)")
    @PutMapping("/event-status")
    public ResponseEntity<String> changeEventModerationStatus(
            @RequestParam UUID eventId,
            @RequestParam int status,
            @RequestParam(required = false) String message
    ) {
        boolean updated = moderatorService.changeVerifyStatusForEvent(eventId, status, message);

        if (updated) {
            return ResponseEntity.ok("Модерационный статус события обновлён");
        } else {
            return ResponseEntity.ok("Модерационный статус уже был установлен");
        }

    }

}
