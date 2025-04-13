package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.platform.model.request.LoginRequest;
import org.platform.springJpa.jwt.TokenSpringJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private TokenSpringJpa tokenService;

    @Operation(summary = "Авторизация пользователя")
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public @ResponseBody String signIn(@RequestBody LoginRequest loginRequest) {

        return tokenService.getToken(loginRequest);
    }
}
