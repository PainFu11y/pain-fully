package org.platform.model.organizer;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.platform.model.event.EventDto;
import org.platform.model.socialMedia.SocialMediaDto;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizerDto {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private String organizationName;
    private String description;
    @JsonProperty("socialMedias")
    private List<SocialMediaDto> socialMediaDtoList;
    @Schema(hidden = true)
    private boolean accreditationStatus;
    @Schema(hidden = true)
    private boolean isEmailVerified;
    private int status;
    private String sphereOfActivity;
    private List<EventDto> events;

}
