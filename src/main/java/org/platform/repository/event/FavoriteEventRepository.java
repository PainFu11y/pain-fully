package org.platform.repository.event;

import org.platform.entity.event.FavoriteEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoriteEventRepository extends JpaRepository<FavoriteEvent, UUID> {
    Optional<FavoriteEvent> findByMemberIdAndEventId(UUID memberId, UUID eventId);
    List<FavoriteEvent> findByMemberId(UUID memberId);
    void deleteByMemberIdAndEventId(UUID memberId, UUID eventId);
}
