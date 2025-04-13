package org.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.platform.enums.constants.DatabaseConstants;
import org.platform.model.SocialMediaDto;
import org.platform.model.organizer.createRequest.OrganizerSocialMediaCreateDto;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.SOCIAL_MEDIAS_TABLE,schema = DatabaseConstants.SCHEMA)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialMedia {
    @Id
    @GenericGenerator(name = "generator",strategy = "uuid2")
    @GeneratedValue(generator = "generator")
    private UUID id;

    private String name;
    private String url;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private Organizer organizer;


    public SocialMediaDto toDto(){
        SocialMediaDto dto = new SocialMediaDto();
        dto.setId(id);
        dto.setName(name);
        dto.setUrl(url);
        dto.setOrganizerId(organizer != null ? organizer.getId() : null);
        return dto;
    }


   /*
   Parsing SocialMediaDto to SocialMedia
    */
    public static SocialMedia fromDto(SocialMediaDto dto) {
        SocialMedia socialMedia = new SocialMedia();
        socialMedia.setId(dto.getId());
        socialMedia.setName(dto.getName());
        socialMedia.setUrl(dto.getUrl());

        if (dto.getOrganizerId() != null) {
            Organizer organizer = new Organizer();
            organizer.setId(dto.getOrganizerId());
            socialMedia.setOrganizer(organizer);
        }

        return socialMedia;
    }
    /*
   Parsing SocialMediaCreateRequestForOrganizerDto to SocialMedia
    */
    public static SocialMedia fromDto(OrganizerSocialMediaCreateDto dto) {
        SocialMedia socialMedia = new SocialMedia();
        socialMedia.setName(dto.getName());
        socialMedia.setUrl(dto.getUrl());
        return socialMedia;
    }


}
