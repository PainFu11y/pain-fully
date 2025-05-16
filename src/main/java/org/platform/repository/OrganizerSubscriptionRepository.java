package org.platform.repository;

import org.platform.entity.Member;
import org.platform.entity.Organizer;
import org.platform.entity.OrganizerSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizerSubscriptionRepository extends JpaRepository<OrganizerSubscription, UUID> {

    boolean existsByMemberAndOrganizer(Member member, Organizer organizer);

    Optional<OrganizerSubscription> findByMemberAndOrganizer(Member member, Organizer organizer);

    List<OrganizerSubscription> findAllByMember(Member member);

    List<OrganizerSubscription> findAllByOrganizer(Organizer organizer);

    List<OrganizerSubscription> findByOrganizer(Organizer organizer);
}
