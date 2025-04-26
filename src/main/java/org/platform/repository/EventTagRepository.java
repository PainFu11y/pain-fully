package org.platform.repository;

import org.platform.entity.event.EventTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventTagRepository extends JpaRepository<EventTag, UUID> {
    Optional<EventTag> findByName(String name);
    EventTag findById(String name);
}
