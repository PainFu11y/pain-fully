package org.platform.springJpa.jwt;

import lombok.RequiredArgsConstructor;
import org.platform.config.JwtUtil;
import org.platform.entity.Member;
import org.platform.entity.Moderator;
import org.platform.entity.Organizer;
import org.platform.enums.Role;
import org.platform.model.request.LoginRequest;
import org.platform.repository.MemberRepository;
import org.platform.repository.ModeratorRepository;
import org.platform.repository.OrganizerRepository;
import org.platform.service.MemberService;
import org.platform.service.OrganizerService;
import org.platform.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenSpringJpa {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrganizerRepository organizerRepository;
    @Autowired
    private ModeratorRepository moderatorRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OrganizerService organizerService;

    public String getToken(LoginRequest loginRequest) {
        String token = null;

        try {
            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            String email = authenticate.getName();
            if (loginRequest.getRole().equals(Role.MEMBER)) {
                Optional<Member> member = memberRepository.findByEmail(email);
                if (member.isPresent()) {
                    loginRequest.setPassword("");
                    if (member.get().isEmailVerified()) {
                        token = jwtUtil.createToken(loginRequest);
                    } else {
                        emailService.sendEmailVerificationCode(email);
                        token = "Your email verification code has been sent to your account.";
                    }

                } else {
                    throw new BadCredentialsException("Invalid username or password");
                }
            } else if (loginRequest.getRole().equals(Role.ORGANIZER)) {
                Optional<Organizer> organizer = organizerRepository.findByEmail(email);
                if (organizer.isPresent()) {
                    loginRequest.setPassword("");
                    if (organizer.get().isEmailVerified()) {
                        token = jwtUtil.createToken(loginRequest);
                    } else {
                         emailService.sendEmailVerificationCode(email);
                        token = "Your email verification code has been sent to your account.";
                    }

                }
            } else if (loginRequest.getRole().equals(Role.MODERATOR)) {
                Optional<Moderator> moderator = moderatorRepository.findByUsername(email);
                if (moderator.isPresent()) {
                    loginRequest.setPassword("");
                    token = jwtUtil.createToken(loginRequest);
                }
            }

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        } catch (Exception e) {
            throw new RuntimeException("problem during getting token");
        }

        return token;
    }
}
