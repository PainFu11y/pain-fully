package org.platform.model.organizer.createRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.platform.model.event.EventDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizerCreateRequestDto {
    @Schema(description = "имя пользователя", example = "Pavel")
    private String username;
    @Schema(description = "mail организатора", example = "avetisyan.vahan2003@gmail.com")
    private String email;
    private String password;
    @Schema(description = "имя организации", example = "Nike")
    private String organizationName;
    @Schema(description = "description", example = "Just do it")
    private String description;
    @JsonProperty("socialMedias")
    private List<OrganizerSocialMediaCreateDto> socialMediaDtoList;
    @Schema(hidden = true)
    private boolean accreditationStatus;
    @Schema(hidden = true)
    private int status;
    private String sphereOfActivity;
    @Schema(hidden = true)
    @JsonProperty("events")
    private List<EventDto> eventDtoList;
}
