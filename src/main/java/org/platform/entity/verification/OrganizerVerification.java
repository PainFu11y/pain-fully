package org.platform.entity.verification;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.platform.entity.Organizer;
import org.platform.enums.constants.DatabaseConstants;
import org.platform.enums.OrganizersVerifyStatus;
import org.platform.model.organizer.OrganizerVerificationDto;

import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.ORGANIZERS_VERIFICATION_TABLE,schema = DatabaseConstants.SCHEMA)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizerVerification {

    @Id
    @GenericGenerator(name = "generator", strategy = "uuid2")
    @GeneratedValue(generator = "generator")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "organizer_id", nullable = false, unique = true)
    private Organizer organizer;

    @Enumerated(EnumType.STRING)
    private OrganizersVerifyStatus status;

    @Column(length = 65535, columnDefinition = "TEXT")
    private String image;



    public OrganizerVerificationDto toDto() {
        return OrganizerVerificationDto.builder()
                .id(this.id)
                .image(this.image)
                .status(this.status)
                .organizerId(this.organizer != null ? this.organizer.getId() : null)
                .build();
    }

    public static OrganizerVerification fromDto(OrganizerVerificationDto dto, Organizer organizer) {
        return OrganizerVerification.builder()
                .id(dto.getId())
                .image(dto.getImage())
                .status(dto.getStatus())
                .organizer(organizer)
                .build();
    }
}
