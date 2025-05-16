package org.platform.entity.verification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.platform.enums.constants.DatabaseConstants;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.EMAIL_TOKENS, schema = DatabaseConstants.SCHEMA)
@Getter
@Setter
public class VerificationToken {

    @Id
    @GenericGenerator(name = "generator",strategy = "uuid2")
    @GeneratedValue(generator = "generator")
    private UUID id;

    private String token;


    private String email;

    private LocalDateTime expiryDate;

}