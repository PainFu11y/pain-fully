package org.platform.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.platform.enums.event.EventFormat;
import org.platform.enums.event.EventStatus;
import org.platform.model.organizer.OrganizerDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventFilterDto {

    private UUID id;
    private String title;
    private String description;
    private EventFormat format;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private EventStatus eventStatus;
    private String contactInfo;
    private int moderationStatus;
    private String moderationStatusInfo;
    private String image;

    @JsonProperty("organizer")
    private OrganizerDto organizerDto;

    @JsonProperty("eventCategory")
    private EventCategoryDto eventCategoryDto;

    @JsonProperty("eventTags")
    private List<EventTagDto> eventTagDtoList;

    public EventFilterDto(EventFormat format) {
        this.format = format;
    }
}


