package org.platform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.platform.enums.constants.DatabaseConstants;
import org.platform.enums.Role;
import org.platform.model.ModeratorDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.MODERATORS_TABLE,schema = DatabaseConstants.SCHEMA)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Moderator implements UserDetails {
    @Id
    @GenericGenerator(name = "generator",strategy = "uuid2")
    @GeneratedValue(generator = "generator")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @Transient
    private List<GrantedAuthority> authorities = new ArrayList<>();


    public ModeratorDto toDto(){
        ModeratorDto moderatorDto = new ModeratorDto();
        moderatorDto.setId(id);
        moderatorDto.setUsername(username);
        moderatorDto.setPassword(password);
        moderatorDto.setAdmin(isAdmin);
        return moderatorDto;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(Role.MODERATOR.toString()));
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
