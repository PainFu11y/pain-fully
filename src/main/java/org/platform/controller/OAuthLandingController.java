package org.platform.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OAuthLandingController {

    @GetMapping("/dashboard")
    @ResponseBody
    public String dashboard(@AuthenticationPrincipal OAuth2User oauth2User) {
        return "Добро пожаловать, " + oauth2User.getAttribute("name") +
                " (" + oauth2User.getAttribute("email") + ")";
    }
}
