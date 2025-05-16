package org.platform.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.platform.entity.Friend;
import org.platform.enums.FriendshipStatus;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendDto {
    private UUID userId1;
    private UUID userId2;
    private FriendshipStatus status;

    public FriendDto(Friend friend) {
        this.userId1 = friend.getUserId1();
        this.userId2 = friend.getUserId2();
        this.status = friend.getStatus();
    }
}
