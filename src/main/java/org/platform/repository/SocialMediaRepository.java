package org.platform.repository;

import org.platform.entity.SocialMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SocialMediaRepository extends JpaRepository<SocialMedia, UUID> {
   SocialMedia findByNameContainingIgnoreCase(String name);
}
