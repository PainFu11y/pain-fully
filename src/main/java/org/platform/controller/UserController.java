package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.enums.constants.RoutConstants;
import org.platform.model.request.ResetPasswordRequest;
import org.platform.repository.MemberRepository;
import org.platform.repository.ModeratorRepository;
import org.platform.repository.OrganizerRepository;
import org.platform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.VERSION + RoutConstants.USER)
public class UserController {
    private final UserService userService;

    @Operation(summary = "Получить текущего пользователя")
    @GetMapping("/get-user")
    public ResponseEntity<Object> getUser() {
        log.info("Received request to fetch the current user");
        return userService.getUser();
    }

    @Operation(summary = "Отправить код для восстановления пароля (MEMBER,ORGANIZER)")
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> sendPasswordResetCode(@RequestParam String email) {
        log.info("Received request to send password reset code to email: {}", email);
        userService.sendPasswordResetCode(email);
        log.info("Password reset code successfully sent to email: {}", email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Сбросить пароль (MEMBER, ORGANIZER)")
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        log.info("Received request to reset password for email: {}", request.getEmail());
        userService.resetPassword(request.getEmail(), request.getResetCode(), request.getNewPassword());
        log.info("Password reset successful for email: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
