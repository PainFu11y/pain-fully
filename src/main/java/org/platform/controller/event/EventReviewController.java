package org.platform.controller.event;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.enums.constants.RoutConstants;
import org.platform.model.eventReview.EventReviewRequest;
import org.platform.model.eventReview.EventReviewResponse;
import org.platform.service.event.EventReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.VERSION + RoutConstants.EVENT_REVIEW)
@Tag(name = "Оценки мероприятий", description = "Контроллер для управления оценок мероприятиями")
public class EventReviewController {

    private final EventReviewService eventReviewService;

    @PostMapping
    public ResponseEntity<EventReviewResponse> submitReview(
            @RequestBody EventReviewRequest request) {
        return ResponseEntity.ok(eventReviewService.submitReview(request));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<List<EventReviewResponse>> getReviews(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventReviewService.getReviewsForEvent(eventId));
    }

}
