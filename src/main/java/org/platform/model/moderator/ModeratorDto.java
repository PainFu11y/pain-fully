package org.platform.model.moderator;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModeratorDto {
    private UUID id;
    private String username;
    private String password;
    private boolean isAdmin;
}
