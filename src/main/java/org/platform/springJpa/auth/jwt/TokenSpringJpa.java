package org.platform.springJpa.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.platform.config.JwtUtil;
import org.platform.entity.Member;
import org.platform.entity.Moderator;
import org.platform.entity.Organizer;
import org.platform.enums.Role;
import org.platform.model.request.LoginRequest;
import org.platform.model.verify.VerifyRequest;
import org.platform.repository.MemberRepository;
import org.platform.repository.ModeratorRepository;
import org.platform.repository.OrganizerRepository;
import org.platform.service.email.EmailService;
import org.platform.springJpa.MemberSpringJpa;
import org.platform.springJpa.organizer.OrganizerSpringJpa;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenSpringJpa {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final OrganizerRepository organizerRepository;
    private final ModeratorRepository moderatorRepository;
    private final EmailService emailService;
    private final MemberSpringJpa memberSpringJpa;
    private final OrganizerSpringJpa organizerSpringJpa;

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
                        emailService.send2faCode(email);

                        token = "Your email verification code has been sent to your account";
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
                        emailService.send2faCode(email);

                        token = "Your email verification code has been sent to your account.";
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


    public ResponseEntity<?> verify2fa(VerifyRequest verifyRequest) {

        Role role = determineRoleByEmail(verifyRequest.getEmail());
        if (role == null) {
            return ResponseEntity.status(404).body("Пользователь не найден.");
        }

        boolean isVerified = false;
        if(role == Role.ORGANIZER) {
           isVerified = memberSpringJpa.verifyEmailVerificationCode(verifyRequest);
        }
        else if(role == Role.MEMBER) {
            isVerified = organizerSpringJpa.verifyEmailVerificationCode(verifyRequest);
        }


        if (isVerified) {
            return ResponseEntity.status(401).body("Неверный или истёкший код.");
        }

        LoginRequest login = new LoginRequest();
        login.setEmail(verifyRequest.getEmail());
        login.setPassword("");
        login.setRole(role);

        String token = jwtUtil.createToken(login);
        return ResponseEntity.ok(token);
    }

    private Role determineRoleByEmail(String email) {
        if (memberRepository.existsByEmail(email)) return Role.MEMBER;
        if (organizerRepository.existsByEmail(email)) return Role.ORGANIZER;
        return null;
    }


}
