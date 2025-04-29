package org.platform.springJpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.entity.Moderator;
import org.platform.entity.Organizer;
import org.platform.entity.event.Event;
import org.platform.entity.verification.OrganizerVerification;
import org.platform.enums.OrganizersVerifyStatus;
import org.platform.model.moderator.ModeratorChangeStatusRequest;
import org.platform.model.moderator.ModeratorCreateRequest;
import org.platform.model.moderator.ModeratorDto;
import org.platform.model.moderator.ModeratorUpdateRequest;
import org.platform.repository.EventRepository;
import org.platform.repository.ModeratorRepository;
import org.platform.repository.OrganizerRepository;
import org.platform.repository.OrganizerVerificationRepository;
import org.platform.service.ModeratorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeratorSpringJpa implements ModeratorService {
    private final ModeratorRepository moderatorRepository;
    private final OrganizerRepository organizerRepository;
    private final OrganizerVerificationRepository organizerVerificationRepository;
    private final EventRepository eventRepository;

    @Override
    public ModeratorDto createModerator(ModeratorCreateRequest request) {
        getCurrentModerator();
        try {
            Optional<Moderator> existing = moderatorRepository.findByUsername(request.getUsername());
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Moderator with username '" + request.getUsername() + "' already exists");
            }

            Moderator moderator = Moderator.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .isAdmin(request.isAdmin())
                    .build();

            Moderator saved = moderatorRepository.save(moderator);
            return ModeratorDto.builder()
                    .id(saved.getId())
                    .username(saved.getUsername())
                    .password(saved.getPassword())
                    .isAdmin(saved.isAdmin())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Could not create moderator", e);
        }
    }

    @Override
    public ModeratorDto updateModerator(ModeratorUpdateRequest request) {
        try {
            Moderator currentModerator = getCurrentModerator();
            Moderator existingModerator = moderatorRepository.findByUsername(currentModerator.getUsername())
                    .orElseThrow(() -> new RuntimeException("Moderator not found"));

            existingModerator.setUsername(request.getUsername());
            existingModerator.setPassword(request.getPassword());

            Moderator saved = moderatorRepository.save(existingModerator);

            return ModeratorDto.builder()
                    .id(saved.getId())
                    .username(saved.getUsername())
                    .password(saved.getPassword())
                    .isAdmin(saved.isAdmin())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Could not update moderator", e);
        }
    }

    @Override
    public ModeratorDto updateModeratorStatus( @RequestBody ModeratorChangeStatusRequest request) {
        try {
            Moderator currentModerator = getCurrentModerator();
            if(!currentModerator.isAdmin()){
                throw new IllegalArgumentException("Moderator is not admin");
            }

            Moderator existingModerator = moderatorRepository.findById(request.getModeratorId())
                    .orElseThrow(() -> new RuntimeException("Moderator not found with given id"));

            existingModerator.setAdmin(request.isAdmin());

            Moderator saved = moderatorRepository.save(existingModerator);

            return ModeratorDto.builder()
                    .id(saved.getId())
                    .username(saved.getUsername())
                    .password(saved.getPassword())
                    .isAdmin(saved.isAdmin())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Could not update moderator", e);
        }
    }



    @Override
    public ModeratorDto getModeratorById(UUID id) {
        Moderator moderator = moderatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moderator not found"));

        return ModeratorDto.builder()
                .id(moderator.getId())
                .username(moderator.getUsername())
                .password(moderator.getPassword())
                .isAdmin(moderator.isAdmin())
                .build();
    }

    @Override
    public ModeratorDto getModeratorByUsername(String username) {
        Moderator moderator = moderatorRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Moderator not found"));

        return ModeratorDto.builder()
                .id(moderator.getId())
                .username(moderator.getUsername())
                .password(moderator.getPassword())
                .isAdmin(moderator.isAdmin())
                .build();
    }

    @Override
    public List<ModeratorDto> getAllModerators() {
        try {
            List<Moderator> moderators = moderatorRepository.findAll();

            return moderators.stream()
                    .map(moderator -> ModeratorDto.builder()
                            .id(moderator.getId())
                            .username(moderator.getUsername())
                            .password(null)//hide password
                            .isAdmin(moderator.isAdmin())
                            .build())
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Could not fetch moderators", e);
        }
    }

    @Override
    public void deleteModerator(ModeratorDto moderatorDto) {
        try {
            UUID id = moderatorDto.getId();
            if (id == null) {
                throw new IllegalArgumentException("Moderator ID must not be null");
            }

            if (!moderatorRepository.existsById(id)) {
                throw new RuntimeException("Moderator not found");
            }

            moderatorRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Could not delete moderator", e);
        }
    }

    @Override
    public boolean changeVerifyStatusForOrganizer(String organizerEmail, OrganizersVerifyStatus verifyStatus) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Unauthenticated request attempted to change verification status");
                throw new RuntimeException("Unauthorized");
            }

            boolean hasModeratorRole = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_MODERATOR"));

            if (!hasModeratorRole) {
                log.warn("User {} tried to change verification status without MODERATOR role", authentication.getName());
                throw new RuntimeException("Forbidden: You do not have moderator permissions");
            }

            Organizer organizer = organizerRepository.findByEmail(organizerEmail)
                    .orElseThrow(() -> {
                        log.warn("Organizer with email {} not found", organizerEmail);
                        return new RuntimeException("Organizer with email " + organizerEmail + " not found");
                    });

            OrganizerVerification verification = organizerVerificationRepository.findByOrganizerId(organizer.getId())
                    .orElseThrow(() -> {
                        log.warn("Verification not found for organizer with ID {}", organizer.getId());
                        return new RuntimeException("Verification not found for organizer with ID " + organizer.getId());
                    });

            if (verification.getStatus() == verifyStatus) {
                log.info("Verification status for organizer {} is already {}", organizerEmail, verifyStatus);
                return false;
            }

            verification.setStatus(verifyStatus);
            organizerVerificationRepository.save(verification);

            log.info("Verification status for organizer {} changed to {}", organizerEmail, verifyStatus);
            return true;
        } catch (Exception e) {
            log.error("Error while changing verification status for organizer {}", organizerEmail, e);
            throw new RuntimeException("Error while changing organizer verification status", e);
        }
    }

    @Override
    public boolean changeVerifyStatusForEvent(UUID eventId, int moderationStatus, String moderationStatusMessage) {
        try {
            // Получение текущего пользователя из JWT через Spring Security
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Unauthenticated request attempted to change verification status");
                throw new RuntimeException("Unauthorized");
            }

            // Проверка, что роль пользователя — MODERATOR
            boolean hasModeratorRole = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_MODERATOR"));

            if (!hasModeratorRole) {
                log.warn("User {} tried to change verification status without MODERATOR role", authentication.getName());
                throw new RuntimeException("Forbidden: You do not have moderator permissions");
            }

            Event byId = eventRepository.findById(eventId)
                    .orElseThrow(() -> {
                        log.warn("Event with id {} not found", eventId);
                        return new RuntimeException("Event with id " + eventId + " not found");
                    });

            if (byId.getModerationStatus() == moderationStatus) {
                log.info("Event moderation status for event by id {} is already {}", eventId, moderationStatus);
                return false;
            }

            byId.setModerationStatus(moderationStatus);
            byId.setModerationStatusInfo(moderationStatusMessage);
            eventRepository.save(byId);

            log.info("Moderation status for event id {} changed to {} with message {}",
                    eventId, moderationStatus, moderationStatusMessage);
            return true;
        } catch (Exception e) {
            log.error("Error while changing verification status for event {}", eventId, e);
            throw new RuntimeException("Error while changing verification status for event " + eventId, e);
        }
    }

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    private Moderator getCurrentModerator() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByModeratorName(username);
    }

    private Moderator getByModeratorName(String moderatorName) {
        return moderatorRepository.findByUsername(moderatorName)
                .orElseThrow(() -> new UsernameNotFoundException("Модератор с именем " + moderatorName +  " не найден"));
    }
}
