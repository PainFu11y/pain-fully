package org.platform.repository;

import org.platform.entity.FavouriteTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavouriteTagRepository extends JpaRepository<FavouriteTag, UUID> {
    List<FavouriteTag> findByMemberId(UUID memberId);
    Optional<FavouriteTag> findByMemberIdAndTagId(UUID memberId, UUID tagId);

}
