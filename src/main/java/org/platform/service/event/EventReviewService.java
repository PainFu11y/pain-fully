package org.platform.service.event;

import org.platform.model.eventReview.EventReviewRequest;
import org.platform.model.eventReview.EventReviewResponse;

import java.util.List;
import java.util.UUID;

public interface EventReviewService {
    EventReviewResponse submitReview(EventReviewRequest request);
    List<EventReviewResponse> getReviewsForEvent(UUID eventId);
}
