package org.platform.service;

import org.platform.entity.event.Event;
import org.platform.entity.verification.OrganizerVerification;
import org.platform.model.event.EventDto;
import org.platform.model.event.EventFilterRequest;
import org.platform.model.organizer.OrganizerVerificationDto;
import org.platform.model.organizer.createRequest.OrganizerCreateRequestDto;
import org.platform.model.organizer.OrganizerDto;
import org.platform.model.organizer.createRequest.OrganizerUpdateRequestDto;
import org.platform.model.verify.VerifyRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface OrganizerService {
    OrganizerDto createOrganizer(OrganizerCreateRequestDto organizerDto);
    OrganizerUpdateRequestDto updateOrganizer(OrganizerUpdateRequestDto organizerDto);
    OrganizerDto getById(UUID id);
    List<OrganizerDto> getAllOrganizers();
    void deleteOrganizer(UUID id);
    OrganizerVerificationDto sendVerifyDocument(MultipartFile file);

    List<EventDto> getMyEvents();
    Page<Event> filterMyEvents(EventFilterRequest filterRequest);

    boolean sendEmailVerificationCodeForOrganizer(String email);
    boolean verifyEmailVerificationCodeForOrganizer(VerifyRequest verifyRequest);

}
