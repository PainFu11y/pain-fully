package org.platform.entity.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.platform.entity.Member;
import org.platform.enums.constants.DatabaseConstants;

import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.FAVORITE_EVENTS_TABLE, schema = DatabaseConstants.SCHEMA, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "event_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteEvent {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id")
    private Event event;
}
