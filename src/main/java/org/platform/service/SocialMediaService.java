package org.platform.service;

import org.platform.model.socialMedia.SocialMediaCreateDto;
import org.platform.model.socialMedia.SocialMediaDto;
import org.platform.model.socialMedia.SocialMediaUpdateDto;

import java.util.List;
import java.util.UUID;

public interface SocialMediaService {
    SocialMediaDto createSocialMedia(SocialMediaCreateDto socialMediaDto);
    SocialMediaDto updateSocialMedia(SocialMediaUpdateDto dto);
    SocialMediaDto getSocialMediaById(UUID id);
    SocialMediaDto getSocialMediaByName(String name);
    List<SocialMediaDto> getAllSocialMedia();
    void deleteSocialMedia(UUID id);
}
