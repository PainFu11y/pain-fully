package org.platform.service;

import org.platform.model.SocialMediaDto;

import java.util.List;
import java.util.UUID;

public interface SocialMediaService {
    SocialMediaDto createSocialMedia(SocialMediaDto socialMediaDto);
    SocialMediaDto updateSocialMedia(SocialMediaDto socialMediaDto);
    SocialMediaDto getSocialMediaById(UUID id);
    SocialMediaDto getSocialMediaByName(String name);
    List<SocialMediaDto> getAllSocialMedia();
    void deleteSocialMedia(UUID id);
}
