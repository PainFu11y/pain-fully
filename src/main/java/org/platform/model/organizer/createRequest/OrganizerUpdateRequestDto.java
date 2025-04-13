package org.platform.model.organizer.createRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.platform.model.event.EventDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizerUpdateRequestDto {
    @Schema(description = "имя пользователя", example = "Pavel")
    private String username;
    @Schema(description = "mail организатора", example = "avetisyan.vahan2003@gmail.com")
    private String email;
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
    private List<EventDto> events;
}
