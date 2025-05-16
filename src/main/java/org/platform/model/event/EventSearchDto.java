package org.platform.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import org.platform.enums.event.EventFormat;
import org.platform.enums.event.EventStatus;
import org.platform.model.eventTag.EventTagDto;
import org.platform.model.organizer.OrganizerDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EventSearchDto {
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
    private EventStatus eventStatus;
    private String contactInfo;

    @JsonProperty("eventTags")
    private List<EventTagDto> eventTagDtoList;
    private int moderationStatus;

    private String image;
}
