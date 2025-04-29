package org.platform.model.moderator;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ModeratorCreateRequest {
    private String username;
    private String password;
    private boolean isAdmin;
}
