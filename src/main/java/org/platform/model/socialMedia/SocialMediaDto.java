package org.platform.model.socialMedia;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
