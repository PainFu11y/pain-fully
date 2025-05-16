package org.platform.model.organizer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.platform.enums.OrganizersVerifyStatus;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizerVerificationDto {
    private UUID id;
    private String image;
    private OrganizersVerifyStatus status;
    private UUID organizerId;
}
