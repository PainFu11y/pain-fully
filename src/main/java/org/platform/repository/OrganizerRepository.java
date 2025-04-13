package org.platform.repository;

import org.platform.entity.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, UUID> {
    Optional<Organizer> findByUsername(String username);

    Optional<Organizer> findByEmail(String username);

    Organizer getByUsername(String username);

    Organizer getByEmail(String email);
}
