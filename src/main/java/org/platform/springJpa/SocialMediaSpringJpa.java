package org.platform.springJpa;

import lombok.RequiredArgsConstructor;
import org.platform.entity.Organizer;
import org.platform.entity.SocialMedia;
import org.platform.model.socialMedia.SocialMediaCreateDto;
import org.platform.model.socialMedia.SocialMediaDto;
import org.platform.model.socialMedia.SocialMediaUpdateDto;
import org.platform.repository.OrganizerRepository;
import org.platform.repository.SocialMediaRepository;
import org.platform.service.SocialMediaService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialMediaSpringJpa implements SocialMediaService {

    private final SocialMediaRepository socialMediaRepository;
    private final OrganizerRepository organizerRepository;

    @Override
    public SocialMediaDto createSocialMedia(SocialMediaCreateDto socialMediaDto) {
        try {
            Organizer organizer = getCurrentOrganizer();

            SocialMedia socialMedia = new SocialMedia();
            socialMedia.setName(socialMediaDto.getName());
            socialMedia.setUrl(socialMediaDto.getUrl());
            socialMedia.setOrganizer(organizer);

            SocialMedia saved = socialMediaRepository.save(socialMedia);
            return saved.toDto();
        } catch (Exception e) {
            throw new RuntimeException("Error creating Social Media", e);
        }
    }

    @Override
    public SocialMediaDto updateSocialMedia(SocialMediaUpdateDto dto) {
        try {
            UUID currentOrganizerId = getCurrentOrganizerId();


            SocialMedia socialMedia = socialMediaRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Social Media not found"));

            if (!socialMedia.getOrganizer().getId().equals(currentOrganizerId)) {
                throw new AccessDeniedException("You are not allowed to modify this social media entry");
            }

            socialMedia.setName(dto.getName());
            socialMedia.setUrl(dto.getUrl());

            SocialMedia updated = socialMediaRepository.save(socialMedia);
            return updated.toDto();
        } catch (AccessDeniedException ade) {
            throw ade;
        } catch (Exception e) {
            throw new RuntimeException("Error updating Social Media", e);
        }
    }

    @Override
    public SocialMediaDto getSocialMediaById(UUID id) {
        try {
            UUID currentOrganizerId = getCurrentOrganizerId();

            SocialMedia socialMedia = socialMediaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Social Media not found"));

            if (!socialMedia.getOrganizer().getId().equals(currentOrganizerId)) {
                throw new AccessDeniedException("You are not allowed to view this social media entry");
            }

            return socialMedia.toDto();
        } catch (AccessDeniedException ade) {
            throw ade;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving Social Media", e);
        }
    }


    @Override
    public SocialMediaDto getSocialMediaByName(String name) {
        try {
            UUID currentOrganizerId = getCurrentOrganizerId();

            SocialMedia socialMedia = socialMediaRepository.findByNameContainingIgnoreCase(name)
                    .orElseThrow(() -> new RuntimeException("Social Media not found"));

            if (!socialMedia.getOrganizer().getId().equals(currentOrganizerId)) {
                throw new AccessDeniedException("You are not allowed to view this social media entry");
            }

            return socialMedia.toDto();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving Social Media by name", e);
        }
    }

    @Override
    public List<SocialMediaDto> getAllSocialMedia() {
        try {
            return socialMediaRepository.findAll().stream()
                    .map(SocialMedia::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all Social Media", e);
        }
    }

    @Override
    public void deleteSocialMedia(UUID id) {
        try {
            UUID currentOrganizerId = getCurrentOrganizerId();

            SocialMedia socialMedia = socialMediaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Social Media not found with ID: " + id));

            if (!socialMedia.getOrganizer().getId().equals(currentOrganizerId)) {
                throw new AccessDeniedException("You are not allowed to delete this social media entry");
            }

            socialMediaRepository.deleteById(id);
        } catch (AccessDeniedException ade) {
            throw ade;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting Social Media", e);
        }
    }


    private List<SocialMedia> getCurrentOrganizersSocialMedia() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }
        String currentEmail = authentication.getName();
        Optional<Organizer> optionalMember = organizerRepository.findByEmail(currentEmail);
        if (optionalMember.isEmpty()) {
            throw new RuntimeException("Authenticated member not found");
        }

        return optionalMember.get().getSocialMedias();
    }

    private Organizer getCurrentOrganizer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return organizerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Organizer not found by email: " + email));

    }

    private UUID getCurrentOrganizerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }
        String currentEmail = authentication.getName();
        Optional<Organizer> optionalMember = organizerRepository.findByEmail(currentEmail);
        if (optionalMember.isEmpty()) {
            throw new RuntimeException("Authenticated organizer not found");
        }
        return optionalMember.get().getId();
    }
}
