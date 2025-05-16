package org.platform.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.platform.enums.Role;
import org.platform.repository.MemberRepository;
import org.platform.repository.OrganizerRepository;
import org.platform.config.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final OrganizerRepository organizerRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {


        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        Role role;

        if (organizerRepository.existsByEmail(email)) {
            role = Role.ORGANIZER;
        } else if (memberRepository.existsByEmail(email)) {
            role = Role.MEMBER;
        } else {
            throw new RuntimeException("Пользователь не найден в БД после входа через OAuth2");
        }

        String jwt = jwtUtil.createToken(email, role);
        if(!response.isCommitted()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"token\": \"" + jwt + "\"}");
            response.getWriter().flush();
        }

    }
}
