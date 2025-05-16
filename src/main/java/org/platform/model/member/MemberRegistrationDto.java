package org.platform.model.member;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRegistrationDto {
        private String username;
        private String email;
        private String password;

}
