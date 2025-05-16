package org.platform.repository.event;

import org.platform.entity.Member;
import org.platform.entity.event.Event;
import org.platform.entity.event.EventReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventReviewRepository extends JpaRepository<EventReview, UUID> {
    Optional<EventReview> findByAuthorAndEvent(Member author, Event event);
    List<EventReview> findByEvent(Event event);
}
