package org.platform.service;

import org.platform.enums.OrganizersVerifyStatus;
import org.platform.model.moderator.ModeratorChangeStatusRequest;
import org.platform.model.moderator.ModeratorCreateRequest;
import org.platform.model.moderator.ModeratorDto;
import org.platform.model.moderator.ModeratorUpdateRequest;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public interface ModeratorService {
    ModeratorDto createModerator(ModeratorCreateRequest moderatorDto);
    ModeratorDto updateModerator(ModeratorUpdateRequest moderatorDto);
    ModeratorDto updateModeratorStatus( @RequestBody ModeratorChangeStatusRequest request);
    ModeratorDto getModeratorById(UUID id);
    ModeratorDto getModeratorByUsername(String username);
    List<ModeratorDto> getAllModerators();
    void deleteModerator(ModeratorDto moderatorDto);

    boolean changeVerifyStatusForOrganizer(String organizerEmail, OrganizersVerifyStatus organizersVerifyStatus);
    boolean changeVerifyStatusForEvent(UUID eventId, int moderationStatus, String moderationStatusMessage);
}
