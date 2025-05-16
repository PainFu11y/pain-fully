package org.platform.model.eventReview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventReviewRequest{
        private UUID eventId;
        private int rating;
        private String comment;
}