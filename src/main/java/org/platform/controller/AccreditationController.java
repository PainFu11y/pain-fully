package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.model.organizer.OrganizerVerificationDto;
import org.platform.service.organizer.OrganizerVerificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/verification")
@RequiredArgsConstructor
@Tag(name = "Аккредитации организаторов", description = "Контроллер для проверки заявок на аккредитацию (только для модераторов)")
public class AccreditationController {
    private final OrganizerVerificationService organizerVerificationService;

    @Operation(summary = "Получить все IN_PROGRESS аккредитации(только для MODERATOR)")
    @GetMapping("/in-progress")
    public List<OrganizerVerificationDto> getInProgressAccreditations() {
        log.info("Received request to get in-progress accreditations");
        List<OrganizerVerificationDto> accreditations = organizerVerificationService.getInProgressAccreditations();
        log.info("Successfully fetched {} in-progress accreditations", accreditations.size());
        return accreditations;
    }

}
