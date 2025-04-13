package org.platform.springJpa.organizer;

import lombok.RequiredArgsConstructor;
import org.platform.entity.Organizer;
import org.platform.entity.verification.OrganizerVerification;
import org.platform.model.organizer.OrganizerVerificationDto;
import org.platform.repository.OrganizerRepository;
import org.platform.repository.OrganizerVerificationRepository;
import org.platform.service.OrganizerVerificationService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizerVerificationSpringJpa implements OrganizerVerificationService {
    private final OrganizerVerificationRepository verificationRepository;
    private final OrganizerRepository organizerRepository;


    @Override
    public OrganizerVerificationDto create(OrganizerVerificationDto dto) {
        Organizer organizer = organizerRepository.findById(dto.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        OrganizerVerification verification = OrganizerVerification.fromDto(dto, organizer);
        OrganizerVerificationDto savedDto;
        try{
            savedDto = verificationRepository.save(verification).toDto();
        }catch (Exception e){
            throw new RuntimeException("Problem while saving organizer verification data");
        }
        return savedDto;
    }

    @Override
    public OrganizerVerificationDto getById(UUID id) {
        return verificationRepository.findById(id)
                .map(OrganizerVerification::toDto)
                .orElseThrow(() -> new RuntimeException("Verification data not found"));
    }

    @Override
    public OrganizerVerificationDto getByOrganizerId(UUID organizerId) {
        return verificationRepository.findByOrganizerId(organizerId)
                .map(OrganizerVerification::toDto)
                .orElseThrow(() -> new RuntimeException("Verification data not found"));
    }

    @Override
    public OrganizerVerificationDto update(UUID id, OrganizerVerificationDto dto) {
        OrganizerVerification verification = verificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Verification data not found"));

        verification.setImage(dto.getImage());
        verification.setStatus(dto.getStatus());

        return verificationRepository.save(verification).toDto();
    }

    @Override
    public void delete(UUID id) {
        if (!verificationRepository.existsById(id)) {
            throw new RuntimeException("Verification not found");
        }
        verificationRepository.deleteById(id);
    }
}
