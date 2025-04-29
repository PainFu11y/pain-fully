package org.platform.springJpa;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.platform.entity.Friend;
import org.platform.entity.Member;
import org.platform.entity.verification.VerificationToken;
import org.platform.enums.FriendshipStatus;
import org.platform.model.member.MemberDto;
import org.platform.model.member.MemberRegistrationDto;
import org.platform.model.verify.VerifyRequest;
import org.platform.repository.FriendRepository;
import org.platform.repository.MemberRepository;
import org.platform.repository.OrganizerRepository;
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
    private final FriendRepository friendRepository;
    private final OrganizerRepository organizerRepository;

    @Transactional
    @Override
    public MemberRegistrationDto createMember(MemberRegistrationDto memberDto) {

        try {
            if (memberRepository.existsByEmail(memberDto.getEmail())
                    || organizerRepository.existsByEmail(memberDto.getEmail())) {
                throw new IllegalArgumentException("Email уже используется");
            }

            Member member = new Member();
            member.setUsername(memberDto.getUsername());
            member.setEmail(memberDto.getEmail());
            member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
            member.setEmailVerified(false);
            member.setPrivacy(0);
            member.setStatus(1);

            memberRepository.save(member);
            emailService.sendEmailVerificationCode(member.getEmail());

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
    public MemberDto getMemberProfile(UUID memberId) {
        Member target = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Member viewer = getCurrentAuthenticatedMember();

        boolean isSameUser = viewer.getId().equals(target.getId());
        boolean isFriend = isSameUser || areFriends(viewer.getId(), target.getId());

        MemberDto dto = new MemberDto();
        dto.setId(target.getId());

        switch (target.getPrivacy()) {
            case 0 -> { // PUBLIC
                dto.setUsername(target.getUsername());
                dto.setEmail(target.getEmail());
            }
            case 1 -> { // ONLY FRIENDS
                if (isFriend) {
                    dto.setUsername(target.getUsername());
                    dto.setEmail(target.getEmail());
                } else {
                    dto.setUsername("Скрыто");
                    dto.setEmail("Недоступно");
                }
            }
            case 2 -> { // NO ONE
                if (isSameUser) {
                    dto.setUsername(target.getUsername());
                    dto.setEmail(target.getEmail());
                } else {
                    dto.setUsername("Скрыто");
                    dto.setEmail("Недоступно");
                }
            }
            default -> throw new RuntimeException("Unknown privacy level: " + target.getPrivacy());
        }

        return dto;
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
     * verifying verification token
     */
    public boolean verifyEmailVerificationCode(VerifyRequest verifyRequest) {
        String currentEmail = verifyRequest.getEmail();
        String code = verifyRequest.getCode();

        Optional<VerificationToken> optionalToken = verificationTokenRepository.findByToken(code);
        if (optionalToken.isEmpty()) {
            return false;
        }

        VerificationToken verificationToken = optionalToken.get();

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (!verificationToken.getEmail().equals(currentEmail)) {
            return false;
        }
        Optional<Member> optionalMember = memberRepository.findByEmail(currentEmail);
        if (optionalMember.isEmpty()) {
            return false;
        }

        Member member = optionalMember.get();
        member.setEmailVerified(true);
        try {
            memberRepository.save(member);
            verificationTokenRepository.deleteById(verificationToken.getId());
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Problem during verifying token", e);
        }
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


    public Member getByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    private boolean areFriends(UUID userId1, UUID userId2) {
        List<Friend> friends = friendRepository.findByUserId1OrUserId2(userId1, userId1);
        return friends.stream().anyMatch(f ->
                f.getStatus() == FriendshipStatus.ACCEPTED &&
                        (f.getUserId1().equals(userId2) || f.getUserId2().equals(userId2)));
    }

    private Member getCurrentAuthenticatedMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        String currentEmail = authentication.getName();
        Optional<Member> optionalMember = memberRepository.findByEmail(currentEmail);

        if (optionalMember.isPresent()) {
            return optionalMember.get();
        }

        throw new RuntimeException("Authenticated member not found");
    }


}
