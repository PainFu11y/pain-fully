package org.platform.model.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class AuthResponse {
    private String token;
    public AuthResponse(String token) {
        this.token = token;
    }
}
