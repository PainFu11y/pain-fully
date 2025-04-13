package org.platform.service;

import org.platform.enums.OrganizersVerifyStatus;
import org.platform.enums.event.EventStatus;
import org.platform.model.ModeratorDto;

import java.util.List;
import java.util.UUID;

public interface ModeratorService {
    ModeratorDto createModerator(ModeratorDto moderatorDto);
    ModeratorDto updateModerator(ModeratorDto moderatorDto);
    ModeratorDto getModeratorById(UUID id);
    ModeratorDto getModeratorByUsername(String username);
    List<ModeratorDto> getAllModerators();
    void deleteModerator(ModeratorDto moderatorDto);

    boolean changeVerifyStatusForOrganizer(String organizerEmail, OrganizersVerifyStatus organizersVerifyStatus);
    boolean changeVerifyStatusForEvent(UUID eventId, int moderationStatus, String moderationStatusMessage);
}
