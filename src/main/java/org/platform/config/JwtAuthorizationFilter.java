package org.platform.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.platform.entity.Member;
import org.platform.entity.Moderator;
import org.platform.entity.Organizer;
import org.platform.repository.MemberRepository;
import org.platform.repository.ModeratorRepository;
import org.platform.repository.OrganizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter{

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private ModeratorRepository moderatorRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String accessToken = jwtUtil.resolveToken(request);
            if (accessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }
            Claims claims = jwtUtil.resolveClaims(request);

            if (claims != null && jwtUtil.validateClaims(claims)) {
                String email = claims.getSubject();
                String role = (String)claims.get("role");

                Object user = null;
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                switch (role) {
                    case "MEMBER" -> {
                        Member member = memberRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Member not found"));
                        member.setAuthorities(authorities);
                        user = member;
                    }
                    case "ORGANIZER" -> {
                        Organizer organizer = organizerRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Organizer not found"));
                        organizer.setAuthorities(authorities);
                        user = organizer;
                    }
                    case "MODERATOR" -> {
                        Moderator moderator = moderatorRepository.findByUsername(email)
                                .orElseThrow(() -> new RuntimeException("Moderator not found"));
                        moderator.setAuthorities(authorities);
                        user = moderator;
                    }
                }

                if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                           authorities
                    );
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
            return;
        }
        filterChain.doFilter(request, response);
     }




}
