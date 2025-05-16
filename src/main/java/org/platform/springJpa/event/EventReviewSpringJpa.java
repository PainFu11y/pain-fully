package org.platform.springJpa.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.platform.entity.Member;
import org.platform.entity.Moderator;
import org.platform.entity.event.Event;
import org.platform.entity.event.EventReview;
import org.platform.model.eventReview.EventReviewRequest;
import org.platform.model.eventReview.EventReviewResponse;
import org.platform.repository.MemberRepository;
import org.platform.repository.ModeratorRepository;
import org.platform.repository.event.EventMemberRepository;
import org.platform.repository.event.EventRepository;
import org.platform.repository.event.EventReviewRepository;
import org.platform.service.event.EventReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventReviewSpringJpa implements EventReviewService {

    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final EventReviewRepository eventReviewRepository;
    private final ModeratorRepository moderatorRepository;
    private final EventMemberRepository eventMemberRepository;


    @Override
    public EventReviewResponse submitReview(EventReviewRequest request) {
        Member currentAuthenticatedMember = getCurrentAuthenticatedMember();

       Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Not found event with event id"));

        boolean isParticipant = eventMemberRepository
                .findByEventAndMember(event, currentAuthenticatedMember)
                .isPresent();

        if (!isParticipant) {
            throw new RuntimeException("Only participants can leave a review");
        }

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        EventReview review = eventReviewRepository.findByAuthorAndEvent(currentAuthenticatedMember, event)
                .map(existing -> {
                    existing.setRating(request.getRating());
                    existing.setComment(request.getComment());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return existing;
                })
                .orElseGet(() -> {
                    EventReview newReview = new EventReview();
                    newReview.setAuthor(currentAuthenticatedMember);
                    newReview.setEvent(event);
                    newReview.setRating(request.getRating());
                    newReview.setComment(request.getComment());
                    newReview.setCreatedAt(LocalDateTime.now());
                    return newReview;
                });

        eventReviewRepository.save(review);

        return new EventReviewResponse(
                review.getId(),
                currentAuthenticatedMember.getUsername(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    @Override
    public List<EventReviewResponse> getReviewsForEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return eventReviewRepository.findByEvent(event)
                .stream()
                .map(r -> new EventReviewResponse(
                        r.getId(),
                        r.getAuthor().getUsername(),
                        r.getRating(),
                        r.getComment(),
                        r.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public void deleteReview(UUID eventId) {
        Member currentAuthenticatedMember = getCurrentAuthenticatedMember();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        EventReview review = eventReviewRepository.findByAuthorAndEvent(currentAuthenticatedMember, event)
                .orElseThrow(() -> new RuntimeException("You have not reviewed this event"));

        eventReviewRepository.delete(review);
    }

    @Transactional
    public void deleteReviewByModerator(UUID reviewId) {
        Moderator currentAuthenticatedModerator = getCurrentAuthenticatedModerator();

        EventReview eventReview = eventReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found by id: " + reviewId));

        eventReviewRepository.delete(eventReview);
    }

    public double getAverageRating(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<EventReview> reviews = eventReviewRepository.findByEvent(event);

        return reviews.isEmpty()
                ? 0.0
                : reviews.stream().mapToInt(EventReview::getRating).average().orElse(0.0);
    }





    private Member getCurrentAuthenticatedMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        String currentEmail = authentication.getName();
        Optional<Member> optionalMember = memberRepository.findByEmail(currentEmail);

        if (optionalMember.isPresent()) {
            return optionalMember.get();
        }

        throw new RuntimeException("Authenticated member not found");
    }

    private Moderator getCurrentAuthenticatedModerator() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByModeratorName(username);
    }

    private Moderator getByModeratorName(String moderatorName) {
        return moderatorRepository.findByUsername(moderatorName)
                .orElseThrow(() -> new UsernameNotFoundException("Модератор с именем " + moderatorName + " не найден"));
    }
}
