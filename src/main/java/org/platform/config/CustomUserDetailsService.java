package org.platform.config;

import lombok.SneakyThrows;
import org.platform.entity.Member;
import org.platform.entity.Moderator;
import org.platform.entity.Organizer;
import org.platform.repository.MemberRepository;
import org.platform.repository.ModeratorRepository;
import org.platform.repository.OrganizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrganizerRepository organizerRepository;
    @Autowired
    private ModeratorRepository moderatorRepository;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Moderator moderator;
        Organizer organizer;
        Member member;

        try {
            moderator = moderatorRepository.getByUsername(email);//тут авторизация с username
            organizer = organizerRepository.getByEmail(email);   //тут авторизация с email
            member = memberRepository.getByEmail(email);         //тут авторизация с email
        } catch (Exception ex) {
            throw new RuntimeException("Problem during authorization process");
        }
        if (moderator == null && organizer == null && member == null) {
            throw new RuntimeException("Wrong email or password");
        }
        if (moderator != null && moderator.getUsername() != null) {
            return User.builder()
                    .username(moderator.getUsername())
                    .password(moderator.getPassword())
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_MODERATOR")))
                    .build();
        } else if (organizer != null && organizer.getEmail() != null) {
            return User.builder()
                    .username(organizer.getEmail())
                    .password(organizer.getPassword())
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")))
                    .build();
        } else if (member != null && member.getEmail() != null) {
            return User.builder()
                    .username(member.getEmail())
                    .password(member.getPassword())
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_MEMBER")))
                    .build();
        }
        throw new UsernameNotFoundException("User not found");


    }
}
