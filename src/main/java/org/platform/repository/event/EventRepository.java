package org.platform.repository.event;

import org.platform.entity.event.Event;
import org.platform.enums.event.EventFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID>, JpaSpecificationExecutor<Event> {
    List<Event> findByTitleContaining(String title);
    List<Event> findByDescriptionContaining(String description);
    List<Event> findByFormat(EventFormat format);
    List<Event> findByLocationContaining(String location);
    List<Event> findAll();
    long countByOrganizerId(UUID organizerId);
    Optional<Event> findByPublicId(UUID eventId);


    List<Event> findByEventCategoryContaining(String category);
}
