package org.platform.repository;

import org.platform.entity.Member;
import org.platform.entity.event.Event;
import org.platform.entity.event.EventMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventMemberRepository extends JpaRepository<EventMember, UUID> {
    List<EventMember> findByEvent(Event event);

    @Query("SELECT em FROM EventMember em JOIN em.memberList m WHERE m = :member")
    List<EventMember> findByMember(@Param("member") Member member);
}
