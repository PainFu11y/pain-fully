-- 1. Members
CREATE TABLE members
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username          VARCHAR(255)        NOT NULL,
    email             VARCHAR(255) UNIQUE NOT NULL,
    password          VARCHAR(255)        NOT NULL,
    is_email_verified BOOLEAN             NOT NULL,
    privacy           INT                 NOT NULL,
    status            INT
);

-- 2. Moderators
CREATE TABLE moderators
(
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN
);

-- 3. Organizers
CREATE TABLE organizers
(
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username             VARCHAR(255)        NOT NULL,
    email                VARCHAR(255) UNIQUE NOT NULL,
    password             VARCHAR(255)        NOT NULL,
    organization_name    VARCHAR(255) UNIQUE NOT NULL,
    description          TEXT,
    accreditation_status BOOLEAN,
    status               INT,
    sphere_of_activity   VARCHAR(255)
);

-- 4. Social Media
CREATE TABLE social_media
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(255) NOT NULL,
    url          VARCHAR(255) NOT NULL,
    organizer_id UUID         NOT NULL REFERENCES organizers (id) ON DELETE CASCADE
);

-- 5. Event Categories
CREATE TABLE event_category
(
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL
);


-- 6. Organizer-SocialMedia relation
CREATE TABLE organizer_social_media
(
    organizer_id    UUID REFERENCES organizers (id) ON DELETE CASCADE,
    social_media_id UUID REFERENCES social_media (id) ON DELETE CASCADE,
    PRIMARY KEY (organizer_id, social_media_id)
);

-- 7. Events
CREATE TABLE events
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title             VARCHAR(255)                    NOT NULL,
    description       TEXT,
    organizer_id      UUID REFERENCES organizers (id) NOT NULL,
    format            VARCHAR(255),
    location          VARCHAR(255),
    event_category_id UUID REFERENCES event_category (id),
    latitude          DECIMAL(10, 6),
    longitude         DECIMAL(10, 6),
    start_time        TIMESTAMP                       NOT NULL,
    end_time          TIMESTAMP                       NOT NULL,
    event_status      VARCHAR(255),
    contact_info      TEXT                            NOT NULL,
    moderation_status INT,
    status_info       TEXT,
    image             TEXT
);

-- 8. Event Tags
CREATE TABLE event_tags
(
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL
);

-- 9. Event-Tag Association
-- Таблица для связи Event и EventTag (Many-to-Many)
CREATE TABLE event_tag_association
(
    event_id UUID REFERENCES events (id) ON DELETE CASCADE,
    tag_id   UUID REFERENCES event_tags (id) ON DELETE CASCADE,
    PRIMARY KEY (event_id, tag_id)
);

-- 10. Event Members
CREATE TABLE event_members
(
    id         UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    event_id   UUID      NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    member_id  UUID      NOT NULL REFERENCES members (id) ON DELETE CASCADE,
    status     VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 11. Event Invitations
CREATE TABLE event_invitations
(
    id         UUID PRIMARY KEY,
    inviter_id UUID REFERENCES members (id),
    invitee_id UUID REFERENCES members (id),
    event_id   UUID REFERENCES events (id),
    status     VARCHAR(20)
);

-- 13. Friends
CREATE TABLE friends
(
    user_id1 UUID NOT NULL,
    user_id2 UUID NOT NULL,
    status   VARCHAR(255),
    PRIMARY KEY (user_id1, user_id2),
    FOREIGN KEY (user_id1) REFERENCES members (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id2) REFERENCES members (id) ON DELETE CASCADE
);



CREATE TABLE favourite_tags
(
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id UUID NOT NULL,
    event_id  UUID,
    FOREIGN KEY (member_id) REFERENCES members (id),
    FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE organizers_verifications
(
    id           UUID PRIMARY KEY,
    organizer_id UUID UNIQUE NOT NULL REFERENCES organizers(id) ON DELETE CASCADE,
    image        TEXT,
    status       VARCHAR(255)
);

CREATE TABLE verification_token
(
    id          BIGSERIAL PRIMARY KEY,
    token       VARCHAR(255),
    email       VARCHAR(255),
    expiry_date TIMESTAMP
);



