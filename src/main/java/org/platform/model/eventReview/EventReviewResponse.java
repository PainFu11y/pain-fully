package org.platform.model.eventReview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventReviewResponse{
        private UUID id;
        private String authorUsername;
        private int rating;
        private String comment;
        private LocalDateTime createdAt;
}
