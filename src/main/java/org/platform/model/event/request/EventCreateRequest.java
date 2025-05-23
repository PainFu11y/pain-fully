package org.platform.model.event.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.platform.entity.event.Event;
import org.platform.entity.event.EventTag;
import org.platform.enums.event.EventFormat;
import org.platform.model.eventTag.EventTagDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {
    @Size(max = 255, message = "Длина title быть не более 255 символов")
    private String title;
    @Size(max = 255, message = "Длина description быть не более 255 символов")
    private String description;
    private EventFormat format;
    private String location;
    private UUID eventCategoryUUID;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String contactInfo;

    @JsonProperty("eventTagsUUID")
    private List<UUID> eventTagDtoUUIDList;


    public EventCreateRequest(Event event){
        this.title = event.getTitle();
        this.description = event.getDescription();

        this.format = event.getFormat();
        this.location = event.getLocation();
        this.eventCategoryUUID = event.toDto().getEventCategoryDto().getId();
        this.latitude = event.getLatitude();
        this.longitude = event.getLongitude();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.contactInfo = event.getContactInfo();
        this.eventTagDtoUUIDList = event.getEventTagList().stream()
                .map(EventTag::getId)
                .collect(Collectors.toList());;
    }

}

