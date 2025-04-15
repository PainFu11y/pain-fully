package org.platform.model.event.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.platform.entity.event.Event;
import org.platform.enums.event.EventFormat;
import org.platform.model.event.EventCategoryDto;
import org.platform.model.eventTag.EventTagDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {
    @Size(max = 255, message = "Длина title быть не более 255 символов")
    private String title;
    @Size(max = 255, message = "Длина description быть не более 255 символов")
    private String description;
    private UUID organizerId;
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


    public EventCreateRequest(Event event){
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.organizerId = event.getOrganizer().getId();
        this.format = event.getFormat();
        this.location = event.getLocation();
        this.eventCategoryDto = event.toDto().getEventCategoryDto();
        this.latitude = event.getLatitude();
        this.longitude = event.getLongitude();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.contactInfo = event.getContactInfo();
        this.eventTagDtoList = event.toDto().getEventTagDtoList();
    }
}
