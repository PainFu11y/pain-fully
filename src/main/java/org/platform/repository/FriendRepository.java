package org.platform.repository;

import org.platform.entity.Friend;
import org.platform.enums.FriendshipStatus;
import org.platform.model.FriendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FriendRepository extends JpaRepository<Friend, FriendId> {
    List<Friend> findByUserId1OrUserId2(UUID userId1, UUID userId2);
    List<Friend> findByStatus(FriendshipStatus status);
    List<Friend> findByUserId1(UUID userId1);
    List<Friend> findByUserId2(UUID userId2);
}
