package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.repository.MemberRepository;
import org.platform.repository.ModeratorRepository;
import org.platform.repository.OrganizerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final MemberRepository memberRepository;
    private final OrganizerRepository organizerRepository;
    private final ModeratorRepository moderatorRepository;

    @Operation(summary = "Получить текущего пользователя")
    @GetMapping("/get-user")
    public ResponseEntity<Object> getUser() {
        log.info("Received request to fetch the current user");
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User role is not defined");
        }

        log.info("Looking up user with username/email: {} and role: {}", username, role);

        return switch (role) {
            case "ROLE_MEMBER" -> {
                var result = memberRepository.findByEmail(username)
                        .<ResponseEntity<Object>>map(member -> {
                            log.info("Member {} found successfully", username);
                            return ResponseEntity.ok(member.toDto());
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
                            return ResponseEntity.ok(organizer.toDto());
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
                            return ResponseEntity.ok(moderator.toDto());
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

}
