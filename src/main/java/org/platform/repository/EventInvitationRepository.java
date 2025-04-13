package org.platform.repository;

import org.platform.entity.event.EventInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventInvitationRepository extends JpaRepository<EventInvitation, UUID> {
}
