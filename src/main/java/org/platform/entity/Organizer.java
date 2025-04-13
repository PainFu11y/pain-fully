package org.platform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.platform.entity.event.Event;
import org.platform.entity.event.EventMember;
import org.platform.entity.event.EventTag;
import org.platform.entity.verification.OrganizerVerification;
import org.platform.enums.constants.DatabaseConstants;
import org.platform.enums.Role;
import org.platform.model.event.EventDto;
import org.platform.model.organizer.OrganizerDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.ORGANIZERS_TABLE,schema = DatabaseConstants.SCHEMA)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organizer implements UserDetails {
    @Id
    @GenericGenerator(name = "generator",strategy = "uuid2")
    @GeneratedValue(generator = "generator")
    private UUID id;

    private String username;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "organization_name",unique = true, nullable = false)
    private String organizationName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialMedia> socialMedias = new ArrayList<>();

    @Column(name = "accreditation_status")
    private boolean accreditationStatus;

    private int status;//0-active 1-blocked

    private String sphereOfActivity;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;


    @OneToOne(mappedBy = "organizer", cascade = CascadeType.ALL, orphanRemoval = true)
    private OrganizerVerification verification;


    @Transient
    private List<GrantedAuthority> authorities = new ArrayList<>();


    /*
Parsing Organizer to OrganizerDto
 */
    public OrganizerDto toDto(){
        OrganizerDto organizerDto = new OrganizerDto();
        organizerDto.setId(id);
        organizerDto.setUsername(username);
        organizerDto.setEmail(email);
        organizerDto.setPassword(password);
        organizerDto.setOrganizationName(organizationName);
        organizerDto.setDescription(description);
        organizerDto.setAccreditationStatus(accreditationStatus);
        organizerDto.setStatus(status);
        organizerDto.setSphereOfActivity(sphereOfActivity);
        if (socialMedias != null) {
            organizerDto.setSocialMediaDtoList(socialMedias.stream()
                    .map(SocialMedia::toDto)
                    .toList());
        }

        if(events != null){
        organizerDto.setEvents(events.stream().map(event -> {
            EventDto dto = new EventDto();
            dto.setId(event.getId());
            dto.setTitle(event.getTitle());
            dto.setDescription(event.getDescription());
            dto.setFormat(event.getFormat());
            dto.setLocation(event.getLocation());
            dto.setEventCategoryDto(event.getEventCategory().toDto());
            dto.setLatitude(event.getLatitude());
            dto.setLongitude(event.getLongitude());
            dto.setStartTime(event.getStartTime());
            dto.setEndTime(event.getEndTime());
            dto.setEventStatus(event.getEventStatus());
            dto.setContactInfo(event.getContactInfo());
            dto.setModerationStatus(event.getModerationStatus());
            dto.setModerationStatusInfo(event.getModerationStatusInfo());
            dto.setEventTagDtoList(event.getEventTagList().stream().map(EventTag::toDto).toList());
            dto.setEventMemberDtoList(event.getEventMembers().stream().map(EventMember::toDto).toList());
            dto.setImage(event.getImage());
            return dto;
        }).toList());
    } else {
        organizerDto.setEvents(new ArrayList<>());
    }

        return organizerDto;
    }

    /*
    Parsing OrganizerDto to Organizer
     */
    public static Organizer fromDto(OrganizerDto dto) {
        Organizer organizer = new Organizer();
        organizer.setId(dto.getId());
        organizer.setUsername(dto.getUsername());
        organizer.setEmail(dto.getEmail());
        organizer.setPassword(dto.getPassword());
        organizer.setOrganizationName(dto.getOrganizationName());
        organizer.setDescription(dto.getDescription());
        organizer.setAccreditationStatus(dto.isAccreditationStatus());
        organizer.setStatus(dto.getStatus());


        if (dto.getSocialMediaDtoList() != null) {
            organizer.setSocialMedias(dto.getSocialMediaDtoList().stream()
                    .map(SocialMedia::fromDto)
                    .toList());
        }

        organizer.setSphereOfActivity(dto.getSphereOfActivity());

        if (dto.getEvents() != null) {
            organizer.setEvents(dto.getEvents().stream()
                    .map(eventDto -> {
                        Event event = Event.fromDto(eventDto);
                        event.setOrganizer(organizer);
                        return event;
                    })
                    .toList());
        }
        return organizer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(Role.MEMBER.toString()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
