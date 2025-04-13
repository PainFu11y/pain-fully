package org.platform.model.event;

import lombok.Getter;
import lombok.Setter;
import org.platform.enums.event.EventFormat;
import org.platform.enums.event.EventStatus;

@Getter
@Setter
public class EventFilterRequest {
    private String title;
    private String description;
    private EventFormat format;
    private String location;
    private String category;
    private String tag;
    private EventStatus status;

    private int page = 0;
    private int limit = 3;

}
