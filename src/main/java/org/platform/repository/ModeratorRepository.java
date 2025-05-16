package org.platform.repository;

import org.platform.entity.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModeratorRepository extends JpaRepository<Moderator, UUID> {
    Optional<Moderator> findByUsername(String username);

    Moderator getByUsername(String username);
}
