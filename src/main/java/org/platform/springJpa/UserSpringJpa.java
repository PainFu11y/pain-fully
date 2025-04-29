package org.platform.springJpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.entity.Member;
import org.platform.entity.Organizer;
import org.platform.entity.verification.VerificationToken;
import org.platform.model.member.MemberDto;
import org.platform.model.moderator.ModeratorDto;
import org.platform.model.organizer.OrganizerDto;
import org.platform.repository.MemberRepository;
import org.platform.repository.ModeratorRepository;
import org.platform.repository.OrganizerRepository;
import org.platform.repository.verification.VerificationTokenRepository;
import org.platform.service.UserService;
import org.platform.service.email.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSpringJpa implements UserService {
    private final MemberRepository memberRepository;
    private final OrganizerRepository organizerRepository;
    private final ModeratorRepository moderatorRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public ResponseEntity<Object> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            log.warn("Attempt to access user info without authentication");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authenticated");
        }


        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);


        if (role == null) {
            log.error("Failed to determine role for user {}", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User role is not defined");
        }

        log.info("Looking up user with username/email: {} and role: {}", username, role);

        return switch (role) {
            case "ROLE_MEMBER" -> {
                var result = memberRepository.findByEmail(username)
                        .<ResponseEntity<Object>>map(member -> {
                            log.info("Member {} found successfully", username);
                            MemberDto dto = member.toDto();
                            dto.setPassword(null);

                            return ResponseEntity.ok(dto);
                        })
                        .orElseGet(() -> {
                            log.warn("Member with email {} not foun", username);
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found");
                        });
                yield result;
            }
            case "ROLE_ORGANIZER" -> {
                var result = organizerRepository.findByEmail(username)
                        .<ResponseEntity<Object>>map(organizer -> {
                            log.info("Organizer {} found successfully", username);
                            OrganizerDto dto = organizer.toDto();
                            dto.setPassword(null);
                            dto.setEvents(null);

                            return ResponseEntity.ok(dto);
                        })
                        .orElseGet(() -> {
                            log.warn("Organizer with email {} not found", username);
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Organizer not found");
                        });
                yield result;
            }
            case "ROLE_MODERATOR" -> {
                var result = moderatorRepository.findByUsername(username)
                        .<ResponseEntity<Object>>map(moderator -> {
                            log.info("Moderator {} found successfully", username);
                            ModeratorDto dto = moderator.toDto();
                            dto.setPassword(null);

                            return ResponseEntity.ok(dto);
                        })
                        .orElseGet(() -> {
                            log.warn("Moderator with username {} not foun", username);
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Moderator not found");
                        });
                yield result;
            }
            default -> {
                log.warn("Unknown role: {}", role);
                yield ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown user role");
            }
        };
    }

    @Transactional
    @Override
    public boolean sendPasswordResetCode(String email) {
        if (!memberRepository.existsByEmail(email)
                && !organizerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email  " + email + " не найден");
        }
        emailService.sendForgotPasswordCode(email);
        return false;
    }

    @Override
    public boolean resetPassword(String email, String resetCode, String newPassword) {
        try{
            Optional<VerificationToken> codeFromDb = verificationTokenRepository.findByEmail(email);
            if (codeFromDb.isEmpty()) {
                throw new RuntimeException("Verification token not found with email " + email);
            }
            VerificationToken verificationToken = codeFromDb.get();
            if(!verificationToken.getToken().equals(resetCode)){
                throw new RuntimeException("Reset code not match");
            }

            Optional<Member> memberOpt = memberRepository.findByEmail(email);
            Optional<Organizer> organizerOpt = organizerRepository.findByEmail(email);

            if (memberOpt.isEmpty()) {
                 if (organizerOpt.isEmpty()) {
                    throw new RuntimeException("User not found with email " + email);
                }
                 Organizer organizer = organizerOpt.get();
                 organizer.setPassword(passwordEncoder.encode(newPassword));
                 organizerRepository.save(organizer);
                 verificationTokenRepository.deleteById(verificationToken.getId());
             log.info("New password reset successful for organizer");
             return true;
            }
            Member member = memberOpt.get();
            member.setPassword(passwordEncoder.encode(newPassword));
            memberRepository.save(member);
            verificationTokenRepository.deleteById(verificationToken.getId());
            log.info("New password reset successful for member");
            return true;
        }catch (Exception e){
            log.error("Error occurred during password reset", e);
            throw e;
        }
    }

}
