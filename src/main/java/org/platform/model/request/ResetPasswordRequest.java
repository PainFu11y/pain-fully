package org.platform.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(description = "Установка нового пароля")
@Builder
public class ResetPasswordRequest {
    private String email;
    private String resetCode;
    private String newPassword;
}
