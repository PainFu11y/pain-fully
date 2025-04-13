package org.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.platform.enums.constants.DatabaseConstants;
import org.platform.enums.FriendshipStatus;
import org.platform.model.FriendId;

import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.FRIENDS_TABLE, schema = DatabaseConstants.SCHEMA)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FriendId.class)
public class Friend {
    @Id
    @Column(name = "user_id1", nullable = false)
    private UUID userId1;
    @Id
    @Column(name = "user_id2", nullable = false)
    private UUID userId2;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;
}
