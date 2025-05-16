package org.platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "organizer_subscriptions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "organizer_id"}))
@Getter
@Setter
public class OrganizerSubscription {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    private Member member;

    @ManyToOne(optional = false)
    private Organizer organizer;

    private LocalDateTime subscribedAt = LocalDateTime.now();
}
