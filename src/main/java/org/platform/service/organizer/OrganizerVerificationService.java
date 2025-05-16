package org.platform.service.organizer;

import org.platform.model.organizer.OrganizerVerificationDto;

import java.util.List;
import java.util.UUID;

public interface OrganizerVerificationService {
        OrganizerVerificationDto create(OrganizerVerificationDto dto);
        OrganizerVerificationDto getById(UUID id);
        OrganizerVerificationDto getByOrganizerId(UUID organizerId);
        OrganizerVerificationDto update(UUID id, OrganizerVerificationDto dto);
        public List<OrganizerVerificationDto> getInProgressAccreditations();
        void delete(UUID id);

}
