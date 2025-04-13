package org.platform.service;

import org.platform.enums.FriendshipStatus;
import org.platform.model.FriendDto;

import java.util.List;
import java.util.UUID;

public interface FriendService {
    FriendDto createFriend(UUID userId);

    FriendDto changeFriendStatus(UUID id, FriendshipStatus status);

    List<FriendDto> getAllFriends();

    List<FriendDto> getMyFriends();

    List<FriendDto> getMyPendingFriends();

    List<FriendDto> getMyBlockedFriends();

    List<FriendDto> getFriendsByFriendShipStatus(FriendshipStatus friendshipStatus);

    void deleteFriend(UUID friendId);
}
