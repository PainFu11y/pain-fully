package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Получить текущего пользователя")
    @GetMapping("/get-user")
    public ResponseEntity<Object> getUser() {
        log.info("Received request to fetch the current user");
        return userService.getUser();
    }

}
