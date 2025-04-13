package org.platform.entity.event;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.platform.entity.Organizer;
import org.platform.entity.SocialMedia;
import org.platform.enums.constants.DatabaseConstants;
import org.platform.enums.event.EventFormat;
import org.platform.enums.event.EventStatus;
import org.platform.model.event.EventCategoryDto;
import org.platform.model.event.EventDto;
import org.platform.model.event.EventFilterDto;
import org.platform.model.organizer.OrganizerDto;
import org.springframework.data.rest.core.annotation.RestResource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.EVENT_TABLE, schema = DatabaseConstants.SCHEMA)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GenericGenerator(name = "generator",strategy = "uuid2")
    @GeneratedValue(generator = "generator")
    private UUID id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private Organizer organizer;
    @Enumerated(EnumType.STRING)
    private EventFormat format; //ONLINE, OFFLINE
    private String location;

    @ManyToOne
    @JoinColumn(name = "event_category_id")
    private EventCategory eventCategory;

    private BigDecimal latitude;
    private BigDecimal longitude;
    @Column(name = "start_time",nullable = false)
    private LocalDateTime startTime;
    @Column(name = "end_time",nullable = false)
    private LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    @Column(name = "event_status",nullable = false)
    private EventStatus eventStatus;
    @Column(name = "contact_info",columnDefinition = "TEXT", nullable = false)
    private String contactInfo;

    @ManyToMany
    @JoinTable(
            name = "event_tag_association",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @RestResource(path = "tags", rel = "tags")
    private List<EventTag> eventTagList;

    @Column(name = "moderation_status")
    private int moderationStatus; //by moderator
    @Column(name = "status_info",columnDefinition = "TEXT")
    private String moderationStatusInfo;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventMember> eventMembers;

    @Column(length = 65535, columnDefinition = "TEXT")
    private String image;


    public  EventDto toDto(){
        EventDto eventDto = new EventDto();
        eventDto.setId(id);
        eventDto.setTitle(title);
        eventDto.setDescription(description);

        OrganizerDto organizerShort = new OrganizerDto();
        organizerShort.setId(organizer.getId());
        organizerShort.setUsername(organizer.getUsername());
        organizerShort.setEmail(organizer.getEmail());
        organizerShort.setOrganizationName(organizer.getOrganizationName());
        organizerShort.setDescription(organizer.getDescription());
        organizerShort.setAccreditationStatus(organizer.isAccreditationStatus());
        organizerShort.setStatus(organizer.getStatus());
        organizerShort.setSphereOfActivity(organizer.getSphereOfActivity());
        if (organizer.getSocialMedias() != null) {
            organizerShort.setSocialMediaDtoList(organizer.getSocialMedias().stream()
                    .map(SocialMedia::toDto)
                    .toList());
        }
        eventDto.setOrganizerDto(organizerShort);

        eventDto.setFormat(format);
        eventDto.setLocation(location);
        eventDto.setContactInfo(contactInfo);
        eventDto.setEventCategoryDto(new EventCategoryDto(
                eventCategory.getId(),
                eventCategory.getName(),
                null
        ));
        eventDto.setLatitude(latitude);
        eventDto.setLongitude(longitude);
        eventDto.setStartTime(startTime);
        eventDto.setEndTime(endTime);
        eventDto.setEventStatus(eventStatus);
        eventDto.setContactInfo(contactInfo);
        eventDto.setModerationStatus(moderationStatus);
        eventDto.setModerationStatusInfo(moderationStatusInfo);
        eventDto.setEventMemberDtoList(
                eventMembers.stream()
                .map(EventMember::toDto)
                .toList());
        eventDto.setImage(image);
        return eventDto;
    }


    public EventFilterDto toFilterDto() {
        EventFilterDto dto = new EventFilterDto();
        dto.setId(id);
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setFormat(format);
        dto.setLocation(location);
        dto.setLatitude(latitude);
        dto.setLongitude(longitude);
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        dto.setEventStatus(eventStatus);
        dto.setContactInfo(contactInfo);
        dto.setModerationStatus(moderationStatus);
        dto.setModerationStatusInfo(moderationStatusInfo);
        dto.setImage(image);

        // Упрощённый organizer
        OrganizerDto organizerShort = new OrganizerDto();
        organizerShort.setId(organizer.getId());
        organizerShort.setUsername(organizer.getUsername());
        organizerShort.setEmail(organizer.getEmail());
        organizerShort.setOrganizationName(organizer.getOrganizationName());
        organizerShort.setDescription(organizer.getDescription());
        organizerShort.setAccreditationStatus(organizer.isAccreditationStatus());
        organizerShort.setStatus(organizer.getStatus());
        organizerShort.setSphereOfActivity(organizer.getSphereOfActivity());
        organizerShort.setSocialMediaDtoList(
                organizer.getSocialMedias().stream().map(SocialMedia::toDto).toList()
        );
        dto.setOrganizerDto(organizerShort);

        // Категория
        dto.setEventCategoryDto(new EventCategoryDto(
                eventCategory.getId(),
                eventCategory.getName(),
                null
        ));

        // Теги
        dto.setEventTagDtoList(
                eventTagList != null ?
                        eventTagList.stream().map(EventTag::toDto).toList() :
                        null
        );

        return dto;
    }

    public static Event fromDto(EventDto eventDto){
        Event event = new Event();
        event.setId(eventDto.getId());
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setFormat(eventDto.getFormat());
        event.setLocation(eventDto.getLocation());
        event.setStartTime(eventDto.getStartTime());
        event.setEndTime(eventDto.getEndTime());
        event.setEventStatus(eventDto.getEventStatus());
        event.setContactInfo(eventDto.getContactInfo());
        event.setModerationStatus(eventDto.getModerationStatus());
        event.setModerationStatusInfo(eventDto.getModerationStatusInfo());
        event.setLatitude(eventDto.getLatitude());
        event.setLongitude(eventDto.getLongitude());
        event.setEventCategory(EventCategory.fromDto(eventDto.getEventCategoryDto()));
        event.setOrganizer(Organizer.fromDto(eventDto.getOrganizerDto()));
        event.setEventMembers(eventDto.getEventMemberDtoList().stream().map(EventMember::fromDto).toList());
        event.setEventTagList(eventDto.getEventTagDtoList().stream().map(EventTag::fromDto).toList());
        event.setImage(eventDto.getImage());
        return event;
    }

}
