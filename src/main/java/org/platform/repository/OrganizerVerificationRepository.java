package org.platform.repository;

import org.platform.entity.verification.OrganizerVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizerVerificationRepository extends JpaRepository<OrganizerVerification, UUID> {
    Optional<OrganizerVerification> findByOrganizerId(UUID organizerId);
}
