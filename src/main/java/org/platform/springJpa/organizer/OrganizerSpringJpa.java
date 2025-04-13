package org.platform.springJpa.organizer;

import lombok.RequiredArgsConstructor;
import org.platform.entity.Organizer;
import org.platform.entity.SocialMedia;
import org.platform.entity.verification.OrganizerVerification;
import org.platform.enums.OrganizersVerifyStatus;
import org.platform.model.organizer.OrganizerVerificationDto;
import org.platform.model.organizer.createRequest.OrganizerCreateRequestDto;
import org.platform.model.organizer.OrganizerDto;
import org.platform.model.organizer.createRequest.OrganizerUpdateRequestDto;
import org.platform.repository.OrganizerRepository;
import org.platform.repository.OrganizerVerificationRepository;
import org.platform.repository.SocialMediaRepository;
import org.platform.service.OrganizerService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizerSpringJpa implements OrganizerService {

    private final OrganizerRepository organizerRepository;
    private final PasswordEncoder passwordEncoder;
    private final SocialMediaRepository socialMediaRepository;
    private final OrganizerVerificationRepository organizerVerificationRepository;


    @Override
    @Transactional
    public OrganizerDto createOrganizer(OrganizerCreateRequestDto dto) {
        try {
            Organizer organizer = new Organizer();
            organizer.setUsername(dto.getUsername());
            organizer.setEmail(dto.getEmail());
            organizer.setPassword(passwordEncoder.encode(dto.getPassword()));
            organizer.setOrganizationName(dto.getOrganizationName());
            organizer.setDescription(dto.getDescription());
            organizer.setAccreditationStatus(dto.isAccreditationStatus());
            organizer.setStatus(dto.getStatus());
            organizer.setSphereOfActivity(dto.getSphereOfActivity());

            Organizer savedOrganizer = organizerRepository.save(organizer);

            if (dto.getSocialMediaDtoList() != null && !dto.getSocialMediaDtoList().isEmpty()) {
                List<SocialMedia> socialMediaList = dto.getSocialMediaDtoList().stream()
                        .map(SocialMedia::fromDto)
                        .peek(sm -> sm.setOrganizer(savedOrganizer))
                        .map(socialMediaRepository::save)
                        .collect(Collectors.toList());

                savedOrganizer.setSocialMedias(socialMediaList);
            }

            return savedOrganizer.toDto();

        } catch (Exception e) {
            throw new RuntimeException("Error creating Organizer", e);
        }
    }



    @Override
    public OrganizerUpdateRequestDto updateOrganizer(OrganizerUpdateRequestDto organizerDto) {
    try {
        Organizer currentOrganizer = getCurrentAuthenticatedOrganizer();

        currentOrganizer.setUsername(organizerDto.getUsername());
        currentOrganizer.setEmail(organizerDto.getEmail());
        currentOrganizer.setOrganizationName(organizerDto.getOrganizationName());
        currentOrganizer.setDescription(organizerDto.getDescription());
        currentOrganizer.setAccreditationStatus(organizerDto.isAccreditationStatus());
        currentOrganizer.setStatus(organizerDto.getStatus());
        currentOrganizer.setSphereOfActivity(organizerDto.getSphereOfActivity());


        currentOrganizer.setSocialMedias(organizerDto.getSocialMediaDtoList().stream()
                .map(SocialMedia::fromDto)
                .toList());
        organizerRepository.save(currentOrganizer);
        return organizerDto;
    } catch (Exception e) {
        throw new RuntimeException("Error updating Organizer", e);
    }
}

@Override
public OrganizerDto getById(UUID id) {
    try {
        Organizer organizer = organizerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        return organizer.toDto();
    } catch (Exception e) {
        throw new RuntimeException("Error retrieving Organizer", e);
    }
}

@Override
public List<OrganizerDto> getAllOrganizers() {
    try {
        return organizerRepository.findAll().stream()
                .map(Organizer::toDto)
                .collect(Collectors.toList());
    } catch (Exception e) {
        throw new RuntimeException("Error retrieving all Organizers", e);
    }
}

@Override
public void deleteOrganizer(UUID id) {
    try {
        organizerRepository.deleteById(id);
    } catch (Exception e) {
        throw new RuntimeException("Error deleting Organizer", e);
    }
}

/**
 * Получение пользователя по имени пользователя
 * <p>
 * Нужен для Spring Security
 *
 * @return пользователь
 */
public UserDetailsService userDetailsService() {
    return this::getByUsername;
}

/**
 * Получение текущего пользователя
 *
 * @return текущий пользователь
 */
public Organizer getCurrentUser() {
    var username = SecurityContextHolder.getContext().getAuthentication().getName();
    return getByUsername(username);
}

public Organizer getByUsername(String username) {
    return organizerRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

}

public OrganizerVerificationDto sendVerifyDocument(MultipartFile file){
    Organizer organizer = getCurrentAuthenticatedOrganizer();

    try {
        byte[] fileBytes = file.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(fileBytes);

        // Проверка, существует ли уже верификация
        OrganizerVerification verification = organizer.getVerification();
        if (verification == null) {
            verification = OrganizerVerification.builder()
                    .organizer(organizer)
                    .image(base64Image)
                    .status(OrganizersVerifyStatus.IN_PROGRESS)
                    .build();
        } else {
            verification.setImage(base64Image);
            verification.setStatus(OrganizersVerifyStatus.IN_PROGRESS);
        }

        organizer.setVerification(verification);
        organizerVerificationRepository.save(verification);

        return verification.toDto();
    } catch (IOException e) {
        throw new RuntimeException("Не удалось прочитать файл", e);
    }
}


    private Organizer getCurrentAuthenticatedOrganizer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }
        String currentEmail = authentication.getName();
        Optional<Organizer> byEmail = organizerRepository.findByEmail(currentEmail);


        if (byEmail.isPresent()) {
            return organizerRepository.findByEmail(byEmail.get().getEmail())
                    .orElseThrow(() -> new RuntimeException("Authenticated organizer not found"));
        }

        throw new RuntimeException("Unauthorized");
    }
}
