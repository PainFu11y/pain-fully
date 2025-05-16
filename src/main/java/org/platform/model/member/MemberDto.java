package org.platform.model.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {
    @Schema(hidden = true)
    private UUID id;
    private String username;
    private String email;
    private String password;
    private boolean isEmailVerified;
    private int privacy;
    private int status;
    private String location;
    private Double latitude;
    private Double longitude;
}
