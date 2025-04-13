package org.platform.springJpa;

import lombok.RequiredArgsConstructor;
import org.platform.entity.Member;
import org.platform.entity.verification.VerificationToken;
import org.platform.model.MemberDto;
import org.platform.repository.MemberRepository;
import org.platform.repository.verification.VerificationTokenRepository;
import org.platform.service.MemberService;
import org.platform.service.email.EmailService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberSpringJpa implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    @Override
    public MemberDto createMember(MemberDto memberDto) {
        try {
            Member member = new Member();
            member.setUsername(memberDto.getUsername());
            member.setEmail(memberDto.getEmail());
            member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
            member.setEmailVerified(false);
            member.setPrivacy(0);
            member.setStatus(1);

            memberRepository.save(member);
            return memberDto;
        } catch (Exception e) {
            throw new RuntimeException("Error creating member", e);
        }
    }

    @Override
    public MemberDto updateMember(MemberDto memberDto) {
        try {
            Optional<Member> existingMemberOpt = memberRepository.findById(memberDto.getId());
            if (existingMemberOpt.isPresent()) {
                Member existingMember = existingMemberOpt.get();
                existingMember.setUsername(memberDto.getUsername());
                existingMember.setEmail(memberDto.getEmail());
                existingMember.setPassword(passwordEncoder.encode(memberDto.getPassword()));
                existingMember.setEmailVerified(memberDto.isEmailVerified());
                existingMember.setPrivacy(memberDto.getPrivacy());
                existingMember.setStatus(memberDto.getStatus());

                memberRepository.save(existingMember);
                return memberDto;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Could not update member", e);
        }
    }

    @Override
    public MemberDto getMemberById(UUID memberID) {
        try {
            Optional<Member> member = memberRepository.findById(memberID);
            return member.map(Member::toDto).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Could not find member with ID: " + memberID, e);
        }
    }

    @Override
    public MemberDto getMemberByEmail(String email) {
        try {
            Optional<Member> member = memberRepository.findByEmail(email);
            return member.map(Member::toDto).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Could not find member with email: " + email);
        }
    }

    @Override
    public MemberDto getMemberByName(String name) {
        try {
            Optional<Member> member = memberRepository.findByUsername(name);
            return member.map(Member::toDto).orElse(null);
        } catch (Exception e) {
           throw new RuntimeException("Could not find member with name: " + name);
        }
    }

    @Override
    public List<MemberDto> getMembers() {
        try {
            List<Member> members = memberRepository.findAll();
            return members.stream().map(Member::toDto).collect(Collectors.toList());
        } catch (Exception e) {
           throw new RuntimeException("Error getting members from Jpa");
        }
    }

    @Override
    public void deleteMember(MemberDto memberDto) {
        try {
            Optional<Member> member = memberRepository.findById(memberDto.getId());
            member.ifPresent(memberRepository::delete);
        } catch (Exception e) {
           throw new RuntimeException("Error deleting member");
        }
    }

    /**
     * sending verification token to email
     *
     */
    @Override
    public boolean sendEmailVerificationCode(String email) {

        String token = String.format("%05d", new Random().nextInt(100000));

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setEmail(email);
        verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        try {
            verificationTokenRepository.save(verificationToken);
        }catch(Exception e) {
            throw new RuntimeException("Error storing verification token");
        }
        try{
            emailService.sendVerificationEmail(email, token);
            return true;
        }catch(Exception e){
            throw new RuntimeException("Error sending verification email");
        }

    }

    /**
     * verifying verification token
     *
     */
    public boolean verifyEmailVerificationCode(String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        return verificationTokenRepository.findByToken(token).map(verificationToken -> {
            if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                return false;
            }

            String email = verificationToken.getEmail();
            if(!email.equals(currentEmail)){
                return false;
            }

            Optional<Member> byEmail = memberRepository.findByEmail(verificationToken.getEmail());
            if(byEmail.isPresent()) {
                Member member = byEmail.get();
                member.setEmailVerified(true);
                try{
                    memberRepository.save(member);
                    verificationTokenRepository.deleteByToken(token);
                    return true;
                }catch(Exception e){
                    throw new RuntimeException("Problem during verifying token");
                }
            }
            return true;
        }).orElse(false);
    }

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    public Member getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    public Member getByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }
}
