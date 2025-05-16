package org.platform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.platform.enums.constants.DatabaseConstants;
import org.platform.enums.Role;
import org.platform.model.member.MemberDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.MEMBERS_TABLE,schema = DatabaseConstants.SCHEMA)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member implements UserDetails {
    @Id
    @GenericGenerator(name = "generator",strategy = "uuid2")
    @GeneratedValue(generator = "generator")
    private UUID id;

    private String username;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_email_verified")
    private boolean isEmailVerified;
    private int privacy;//0-public,1-only friends, 2-no one
    private int status; //0-active,1-blocked

    @Column(name = "location")
    private String location;
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;


    @Transient
    private List<GrantedAuthority> authorities = new ArrayList<>();


    public MemberDto toDto(){
        MemberDto memberDto = new MemberDto();
        memberDto.setId(id);
        memberDto.setUsername(username);
        memberDto.setEmail(email);
        memberDto.setPassword(password);
        memberDto.setEmailVerified(isEmailVerified);
        memberDto.setPrivacy(privacy);
        memberDto.setStatus(status);
        memberDto.setLocation(location);
        memberDto.setLatitude(latitude);
        memberDto.setLongitude(longitude);
        return memberDto;
    }
    public static Member fromDto(MemberDto memberDto){
        Member member = new Member();
        member.setId(memberDto.getId());
        member.setUsername(memberDto.getUsername());
        member.setEmail(memberDto.getEmail());
        member.setPassword(memberDto.getPassword());
        member.setEmailVerified(memberDto.isEmailVerified());
        member.setPrivacy(memberDto.getPrivacy());
        member.setStatus(memberDto.getStatus());
        member.setLocation(memberDto.getLocation());
        member.setLatitude(memberDto.getLatitude());
        member.setLongitude(memberDto.getLongitude());
        return member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(Role.MEMBER.toString()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
