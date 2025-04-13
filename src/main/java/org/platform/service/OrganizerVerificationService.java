package org.platform.service;

import org.platform.model.organizer.OrganizerVerificationDto;

import java.util.UUID;

public interface OrganizerVerificationService {
        OrganizerVerificationDto create(OrganizerVerificationDto dto);
        OrganizerVerificationDto getById(UUID id);
        OrganizerVerificationDto getByOrganizerId(UUID organizerId);
        OrganizerVerificationDto update(UUID id, OrganizerVerificationDto dto);
        void delete(UUID id);

}
