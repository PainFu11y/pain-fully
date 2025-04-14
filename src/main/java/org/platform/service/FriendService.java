package org.platform.service;

import org.platform.enums.FriendshipStatus;
import org.platform.model.FriendDto;

import java.util.List;
import java.util.UUID;

public interface FriendService {
    FriendDto createFriend(UUID userId);

    FriendDto respondToFriendRequest(UUID senderId, FriendshipStatus responseStatus);

    List<FriendDto> getAllFriends();

    List<FriendDto> getMyFriends();

    List<FriendDto> getFriendsByFriendShipStatus(FriendshipStatus friendshipStatus);


    void removeFriend(UUID friendId);

    FriendDto blockFriend(UUID friendId);

    void inviteFriendToEvent(UUID friendId, UUID eventId);

}
