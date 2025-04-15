package org.platform.model.socialMedia;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocialMediaCreateDto {
    private String name;
    private String url;
}
