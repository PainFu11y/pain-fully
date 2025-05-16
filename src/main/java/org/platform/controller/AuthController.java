package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.platform.enums.constants.RoutConstants;
import org.platform.model.request.LoginRequest;
import org.platform.model.verify.VerifyRequest;
import org.platform.springJpa.auth.jwt.TokenSpringJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(RoutConstants.AUTH)
@Tag(name = "Аутентификация", description = "Вход и подтверждение 2FA")
public class AuthController {
    @Autowired
    private TokenSpringJpa tokenService;

    @Operation(summary = "Авторизация пользователя")
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public @ResponseBody String signIn(@RequestBody LoginRequest loginRequest) {

        return tokenService.getToken(loginRequest);
    }

    @Operation(summary = "Подтвердить 2fa и войти в аккаунт (MEMBER,ORGANIZER)")
    @PostMapping("/verify-2fa")
    public @ResponseBody ResponseEntity<?> verify2fa(@RequestBody VerifyRequest verifyRequest) {
       return tokenService.verify2fa(verifyRequest);
    }
}

