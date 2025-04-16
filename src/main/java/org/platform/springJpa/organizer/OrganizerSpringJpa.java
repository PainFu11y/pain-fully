package org.platform.springJpa.organizer;

import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.criteria.Join;
import org.aspectj.weaver.ast.Or;
import org.platform.entity.Member;
import org.platform.entity.Organizer;
import org.platform.entity.SocialMedia;
import org.platform.entity.event.Event;
import org.platform.entity.event.EventTag;
import org.platform.entity.verification.OrganizerVerification;
import org.platform.entity.verification.VerificationToken;
import org.platform.enums.OrganizersVerifyStatus;
import org.platform.model.event.EventDto;
import org.platform.model.event.EventFilterRequest;
import org.platform.model.organizer.OrganizerVerificationDto;
import org.platform.model.organizer.createRequest.OrganizerCreateRequestDto;
import org.platform.model.organizer.OrganizerDto;
import org.platform.model.organizer.createRequest.OrganizerUpdateRequestDto;
import org.platform.model.verify.VerifyRequest;
import org.platform.repository.EventRepository;
import org.platform.repository.OrganizerRepository;
import org.platform.repository.OrganizerVerificationRepository;
import org.platform.repository.SocialMediaRepository;
import org.platform.repository.verification.VerificationTokenRepository;
import org.platform.service.OrganizerService;
import org.platform.service.email.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.criteria.Predicate;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizerSpringJpa implements OrganizerService {

    private final OrganizerRepository organizerRepository;
    private final PasswordEncoder passwordEncoder;
    private final SocialMediaRepository socialMediaRepository;
    private final OrganizerVerificationRepository organizerVerificationRepository;
    private final EventRepository eventRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;


    @Override
    @Transactional
    public OrganizerDto createOrganizer(OrganizerCreateRequestDto dto) {
        try {
            if (organizerRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email уже используется");
            }

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

            sendEmailVerificationCodeForOrganizer(savedOrganizer.getEmail());

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
            currentOrganizer.setEmailVerified(organizerDto.isEmailVerified());


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

    public OrganizerVerificationDto sendVerifyDocument(MultipartFile file) {
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

    @Override
    public List<EventDto> getMyEvents() {
        Organizer currentAuthenticatedOrganizer = getCurrentAuthenticatedOrganizer();

        return currentAuthenticatedOrganizer.getEvents().stream()
                .map(Event::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Event> filterMyEvents(EventFilterRequest filterRequest) {
        Organizer organizer = getCurrentAuthenticatedOrganizer();

        Specification<Event> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("organizer").get("id"), organizer.getId()));

            if (filterRequest.getTitle() != null) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + filterRequest.getTitle().toLowerCase() + "%"));
            }
            if (filterRequest.getDescription() != null) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + filterRequest.getDescription().toLowerCase() + "%"));
            }
            if (filterRequest.getFormat() != null) {
                predicates.add(cb.equal(root.get("format"), filterRequest.getFormat()));
            }
            if (filterRequest.getLocation() != null) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + filterRequest.getLocation().toLowerCase() + "%"));
            }
            if (filterRequest.getCategory() != null) {
                predicates.add(cb.equal(root.get("eventCategory").get("name"), filterRequest.getCategory()));
            }
            if (filterRequest.getTag() != null) {
                Join<Event, EventTag> tagJoin = root.join("eventTagList", JoinType.LEFT);
                predicates.add(cb.equal(cb.lower(tagJoin.get("name")), filterRequest.getTag().toLowerCase()));
            }
            if (filterRequest.getStatus() != null) {
                LocalDateTime now = LocalDateTime.now();
                switch (filterRequest.getStatus()) {
                    case NOT_STARTED -> predicates.add(cb.greaterThan(root.get("startTime"), now));
                    case ONGOING -> predicates.add(cb.and(
                            cb.lessThanOrEqualTo(root.get("startTime"), now),
                            cb.greaterThanOrEqualTo(root.get("endTime"), now)
                    ));
                    case FINISHED -> predicates.add(cb.lessThan(root.get("endTime"), now));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getLimit(), Sort.by("startTime").descending());
        return eventRepository.findAll(spec, pageable);
    }


    public boolean sendEmailVerificationCodeForOrganizer(String email) {
        Optional<Organizer> organizerOpt = organizerRepository.findByEmail(email);

        if (organizerOpt.isEmpty()) {
            throw new IllegalArgumentException("Организатор с таким email не найден");
        }

        String token = String.format("%05d", new Random().nextInt(100000));

        VerificationToken verificationToken = null;
        Optional<VerificationToken> byEmail;
        try {
            byEmail = verificationTokenRepository.findByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Problem while getting verification by email", e);
        }
        if (byEmail.isPresent()) {
            verificationToken  = byEmail.get();

            if (verificationToken.getExpiryDate().minusMinutes(14).isAfter(LocalDateTime.now())) {
                throw new RuntimeException("Код уже был отправлен недавно. Пожалуйста, подождите.");
            }
            verificationToken.setToken(token);
            verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        }else {
            verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setEmail(email);
            verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        }
        try {
            verificationTokenRepository.save(verificationToken);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении токена", e);
        }
        try {
            emailService.sendVerificationEmail(email, token);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при отправке письма", e);
        }
    }



    @Override
    public boolean verifyEmailVerificationCodeForOrganizer(VerifyRequest verifyRequest) {
        String currentEmail = verifyRequest.getEmail();

        return verificationTokenRepository.findByToken(verifyRequest.getCode()).map(verificationToken -> {
            if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                return false;
            }

            String email = verificationToken.getEmail();
            if (!email.equals(currentEmail)) {
                return false;
            }

            Optional<Organizer> organizerOpt = organizerRepository.findByEmail(email);
            if (organizerOpt.isPresent()) {
                Organizer organizer = organizerOpt.get();
                organizer.setEmailVerified(true);
                try {
                    organizerRepository.save(organizer);
                    verificationTokenRepository.deleteByEmail(currentEmail);
                    return true;
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка при подтверждении токена", e);
                }
            }

            return false;
        }).orElse(false);
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
