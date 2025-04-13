package org.platform.springJpa;

import lombok.RequiredArgsConstructor;
import org.platform.entity.Organizer;
import org.platform.entity.SocialMedia;
import org.platform.model.SocialMediaDto;
import org.platform.repository.SocialMediaRepository;
import org.platform.service.SocialMediaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialMediaSpringJpa implements SocialMediaService {

    private final SocialMediaRepository socialMediaRepository;

    @Override
    public SocialMediaDto createSocialMedia(SocialMediaDto socialMediaDto) {
        try {
            SocialMedia socialMedia = new SocialMedia();
            socialMedia.setName(socialMediaDto.getName());
            socialMedia.setUrl(socialMediaDto.getUrl());

            if (socialMediaDto.getOrganizerId() != null) {
                Organizer organizer = new Organizer();
                organizer.setId(socialMediaDto.getOrganizerId());
                socialMedia.setOrganizer(organizer);
            } else {
                throw new IllegalArgumentException("Organizer ID is required for social media");
            }

            SocialMedia saved = socialMediaRepository.save(socialMedia);
            return saved.toDto();
        } catch (Exception e) {
            throw new RuntimeException("Error creating Social Media", e);
        }
    }

    @Override
    public SocialMediaDto updateSocialMedia(SocialMediaDto socialMediaDto) {
        try {
            SocialMedia socialMedia = socialMediaRepository.findById(socialMediaDto.getId())
                    .orElseThrow(() -> new RuntimeException("Social Media not found"));

            socialMedia.setName(socialMediaDto.getName());
            socialMedia.setUrl(socialMediaDto.getUrl());

            if (socialMediaDto.getOrganizerId() != null) {
                Organizer organizer = new Organizer();
                organizer.setId(socialMediaDto.getOrganizerId());
                socialMedia.setOrganizer(organizer);
            } else {
                throw new IllegalArgumentException("Organizer ID is required for social media");
            }

            SocialMedia updated = socialMediaRepository.save(socialMedia);
            return updated.toDto();
        } catch (Exception e) {
            throw new RuntimeException("Error updating Social Media", e);
        }
    }

    @Override
    public SocialMediaDto getSocialMediaById(UUID id) {
        try {
            SocialMedia socialMedia = socialMediaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Social Media not found"));
            return socialMedia.toDto();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving Social Media", e);
        }
    }


    @Override
    public SocialMediaDto getSocialMediaByName(String name) {
        try {
            SocialMedia socialMedia = socialMediaRepository.findByNameContainingIgnoreCase(name);
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
            socialMediaRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting Social Media", e);
        }
    }
}
