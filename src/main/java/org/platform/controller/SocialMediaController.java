package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.enums.constants.RoutConstants;
import org.platform.model.socialMedia.SocialMediaCreateDto;
import org.platform.model.socialMedia.SocialMediaDto;
import org.platform.model.socialMedia.SocialMediaUpdateDto;
import org.platform.service.SocialMediaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.VERSION + RoutConstants.SOCIAL_MEDIA)
@RequiredArgsConstructor
@Tag(name = "Социальные сети", description = "Управление записями социальных сетей для организаторов")
public class SocialMediaController {

    private final SocialMediaService socialMediaService;


    @Operation(summary = "Создать запись социальной сети")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody SocialMediaDto createSocialMedia(@RequestBody SocialMediaCreateDto socialMediaDto) {
        log.info("Received request to create social media.");
        try {
            SocialMediaDto createdSocialMedia = socialMediaService.createSocialMedia(socialMediaDto);
            log.info("Social media created successfully: {}", createdSocialMedia);
            return createdSocialMedia;
        } catch (Exception e) {
            log.error("Error creating social media: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create social media", e);
        }
    }
    @Operation(summary = "Обновить запись социальной сети по ID")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public @ResponseBody SocialMediaDto updateSocialMedia(@PathVariable UUID id, @RequestBody SocialMediaUpdateDto socialMediaDto) {
        log.info("Received request to update social media with ID: {}", id);
        try {
            socialMediaDto.setId(id);
            SocialMediaDto updatedSocialMedia = socialMediaService.updateSocialMedia(socialMediaDto);
            log.info("Social media updated successfully: {}", updatedSocialMedia);
            return updatedSocialMedia;
        } catch (Exception e) {
            log.error("Error updating social media: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update social media", e);
        }
    }

    @Operation(summary = "Получить запись социальной сети по ID")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody SocialMediaDto getSocialMediaById(@PathVariable UUID id) {
        log.info("Received request to get social media by ID: {}", id);
        try {
            SocialMediaDto socialMedia = socialMediaService.getSocialMediaById(id);
            log.info("Social media retrieved successfully: {}", socialMedia);
            return socialMedia;
        } catch (Exception e) {
            log.error("Error retrieving social media by ID: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve social media", e);
        }
    }
    @Operation(summary = "Получить запись социальной сети по названию")
    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody SocialMediaDto getSocialMediaByName(@PathVariable String name) {
        log.info("Received request to get social media by name: {}", name);
        try {
            SocialMediaDto socialMedia = socialMediaService.getSocialMediaByName(name);
            log.info("Social media retrieved successfully: {}", socialMedia);
            return socialMedia;
        } catch (Exception e) {
            log.error("Error retrieving social media by name: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve social media", e);
        }
    }
    @Operation(summary = "Получить список всех записей социальных сетей")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<SocialMediaDto> getAllSocialMedia() {
        log.info("Received request to get all social media.");
        try {
            List<SocialMediaDto> socialMediaList = socialMediaService.getAllSocialMedia();
            log.info("Retrieved {} social media records.", socialMediaList.size());
            return socialMediaList;
        } catch (Exception e) {
            log.error("Error retrieving all social media: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve social media", e);
        }
    }

    @Operation(summary = "Удалить запись социальной сети по ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSocialMedia(@PathVariable UUID id) {
        log.info("Received request to delete social media with ID: {}", id);
        try {
            socialMediaService.deleteSocialMedia(id);
            log.info("Social media deleted successfully.");
        } catch (Exception e) {
            log.error("Error deleting social media: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete social media", e);
        }
    }
}
