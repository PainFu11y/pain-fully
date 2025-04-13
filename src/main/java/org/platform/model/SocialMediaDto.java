package org.platform.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.platform.model.organizer.OrganizerDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocialMediaDto {
    private UUID id;
    private String name;
    private String url;
    private UUID organizerId;
}
