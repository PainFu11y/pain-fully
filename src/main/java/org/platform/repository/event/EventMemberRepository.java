package org.platform.repository.event;

import org.platform.entity.Member;
import org.platform.entity.event.Event;
import org.platform.entity.event.EventMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventMemberRepository extends JpaRepository<EventMember, UUID> {
    List<EventMember> findByEvent(Event event);
    List<EventMember> findByMember(Member member);
    Optional<EventMember> findByEventAndMember(Event event, Member member);
}
