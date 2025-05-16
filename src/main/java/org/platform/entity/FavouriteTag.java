package org.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.platform.entity.event.Event;
import org.platform.entity.event.EventTag;
import org.platform.enums.constants.DatabaseConstants;


import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.FAVOURITE_TAGS_TABLE, schema = DatabaseConstants.SCHEMA)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavouriteTag {
    @Id
    @GenericGenerator(name = "generator",strategy = "uuid2")
    @GeneratedValue(generator = "generator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private EventTag tag;
}
