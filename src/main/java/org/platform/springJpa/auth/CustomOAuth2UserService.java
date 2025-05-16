package org.platform.springJpa.auth;

import lombok.RequiredArgsConstructor;
import org.platform.entity.Member;
import org.platform.entity.Organizer;
import java.util.List;
import org.platform.repository.MemberRepository;
import org.platform.repository.OrganizerRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final OrganizerRepository organizerRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = new DefaultOAuth2UserService().loadUser(userRequest);
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // Получаем роль из параметра state
        String requestedRole = Optional.ofNullable(userRequest.getAdditionalParameters().get("state"))
                .map(Object::toString)
                .orElse("member");


        if ("organizer".equals(requestedRole) && memberRepository.existsByEmail(email)) {
            throw new OAuth2AuthenticationException("Этот email уже зарегистрирован как УЧАСТНИК (Member)");
        }

        if ("member".equals(requestedRole) && organizerRepository.existsByEmail(email)) {
            throw new OAuth2AuthenticationException("Этот email уже зарегистрирован как ОРГАНИЗАТОР (Organizer)");
        }


        String actualRole;
        if (organizerRepository.existsByEmail(email)) {
            actualRole = "organizer";
        } else if (memberRepository.existsByEmail(email)) {
            actualRole = "member";
        } else {
            // пользователь новый — создаём по запрошенной роли
            if ("organizer".equals(requestedRole)) {
                Organizer newOrganizer = Organizer.builder()
                        .email(email)
                        .username(name)
                        .password(UUID.randomUUID().toString())
                        .organizationName("Unnamed Organization")
                        .description("OAuth2 Organizer")
                        .accreditationStatus(false)
                        .isEmailVerified(true)
                        .status(0)
                        .sphereOfActivity("Unspecified")
                        .build();
                organizerRepository.save(newOrganizer);
                actualRole = "organizer";
            } else {
                Member newMember = Member.builder()
                        .email(email)
                        .username(name)
                        .password(UUID.randomUUID().toString())
                        .isEmailVerified(true)
                        .privacy(0)
                        .status(0)
                        .build();
                memberRepository.save(newMember);
                actualRole = "member";
            }
        }

        String authority = "ROLE_" + actualRole.toUpperCase();

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(authority)),
                oauthUser.getAttributes(),
                "email"
        );
    }
}
