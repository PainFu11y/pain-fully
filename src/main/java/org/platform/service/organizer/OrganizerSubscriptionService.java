package org.platform.service.organizer;

import org.platform.entity.Organizer;

import java.util.List;
import java.util.UUID;

public interface OrganizerSubscriptionService {
    void subscribe(UUID organizerId);
    void unsubscribe(UUID organizerId);
    boolean isSubscribed(UUID memberId, UUID organizerId);
    List<Organizer> getSubscribedOrganizers(UUID memberId);
    List<Organizer> getMySubscribedOrganizers();
}
