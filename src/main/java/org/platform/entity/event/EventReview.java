package org.platform.entity.event;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.platform.entity.Member;
import org.platform.enums.constants.DatabaseConstants;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.EVENT_REVIEWS_TABLE, schema = DatabaseConstants.SCHEMA)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventReview {

    @Id
    @GenericGenerator(name = "generator",strategy = "uuid2")
    @GeneratedValue(generator = "generator")
    private UUID id;

    @ManyToOne(optional = false)
    private Member author;

    @ManyToOne(optional = false)
    private Event event;

    @Min(1)
    @Max(5)
    private int rating;

    @Column(length = 1000)
    private String comment;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
