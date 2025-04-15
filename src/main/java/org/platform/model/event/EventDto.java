package org.platform.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.platform.entity.event.Event;
import org.platform.enums.event.EventFormat;
import org.platform.model.eventTag.EventTagDto;
import org.platform.model.organizer.OrganizerDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
  private UUID id;
  @Size(max = 255, message = "Длина title быть не более 255 символов")
  private String title;
  @Size(max = 255, message = "Длина description быть не более 255 символов")
  private String description;
  @JsonProperty("organizer")
  private OrganizerDto organizerDto;
  private EventFormat format;
  private String location;
  @JsonProperty("eventCategory")
  private EventCategoryDto eventCategoryDto;
  private BigDecimal latitude;
  private BigDecimal longitude;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String contactInfo;

  @JsonProperty("eventTags")
  private List<EventTagDto> eventTagDtoList;
  private int moderationStatus;
  private String moderationStatusInfo;
  @JsonProperty("eventMembers")
  private List<EventMemberDto> eventMemberDtoList;
  private String image;

  public EventDto(Event event){
    this.id = event.getId();
    this.title = event.getTitle();
    this.description = event.getDescription();
    this.organizerDto = event.toDto().getOrganizerDto();
    this.format = event.getFormat();
    this.location = event.getLocation();
    this.eventCategoryDto = event.toDto().getEventCategoryDto();
    this.latitude = event.getLatitude();
    this.longitude = event.getLongitude();
    this.startTime = event.getStartTime();
    this.endTime = event.getEndTime();
    this.contactInfo = event.getContactInfo();
    this.eventTagDtoList = event.toDto().getEventTagDtoList();
    this.moderationStatus = event.getModerationStatus();
    this.moderationStatusInfo = event.getModerationStatusInfo();
    this.eventMemberDtoList = event.toDto().getEventMemberDtoList();
    this.image = event.getImage();
  }

}
