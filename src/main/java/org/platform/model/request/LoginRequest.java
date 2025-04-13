package org.platform.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.platform.enums.Role;

@Data
@Schema(description = "Запрос на аутентификацию")
@Builder
public class LoginRequest {

    @Schema(description = "mail пользователя", example = "333vahe777@gmail.com")
    private String email;

    @Schema(description = "Пароль", example = "string")
    private String password;

    @Schema(description = "Тип пользователя", example = "MEMBER")
    private Role role;
}
