package org.platform.springJpa.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.platform.entity.Member;
import org.platform.entity.event.Event;
import org.platform.entity.event.FavoriteEvent;
import org.platform.repository.MemberRepository;
import org.platform.repository.event.EventRepository;
import org.platform.repository.event.FavoriteEventRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteEventSpringJpa {

    private final FavoriteEventRepository favoriteEventRepository;
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;

    @Transactional
    public void addToFavorites(UUID eventId) {
        Member currentAuthenticatedMember = getCurrentAuthenticatedMember();

        if (favoriteEventRepository
                .findByMemberIdAndEventId(currentAuthenticatedMember.getId(), eventId).isPresent()) {
            throw new RuntimeException("Event already in favorites");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        FavoriteEvent favorite = new FavoriteEvent(null, currentAuthenticatedMember, event);
        favoriteEventRepository.save(favorite);
    }

    @Transactional
    public void removeFromFavorites(UUID eventId) {
        Member currentAuthenticatedMember = getCurrentAuthenticatedMember();
        if(favoriteEventRepository
                .findByMemberIdAndEventId(currentAuthenticatedMember.getId(),eventId).isEmpty()){
            throw new RuntimeException("Event already deleted from favorites");
        }
        favoriteEventRepository.deleteByMemberIdAndEventId(currentAuthenticatedMember.getId(), eventId);
    }

    public List<Event> getMyFavorites() {
        Member currentAuthenticatedMember = getCurrentAuthenticatedMember();
        return favoriteEventRepository.findByMemberId(currentAuthenticatedMember.getId()).stream()
                .map(FavoriteEvent::getEvent)
                .toList();
    }

    public List<Event> getFavoritesByMemberId(UUID memberId) {
        return favoriteEventRepository.findByMemberId(memberId).stream()
                .map(FavoriteEvent::getEvent)
                .toList();
    }

    public boolean isFavorite(UUID memberId, UUID eventId) {
        return favoriteEventRepository.findByMemberIdAndEventId(memberId, eventId).isPresent();
    }

    private Member getCurrentAuthenticatedMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        String currentEmail = authentication.getName();
        Optional<Member> optionalMember = memberRepository.findByEmail(currentEmail);

        if (optionalMember.isPresent()) {
            return optionalMember.get();
        }

        throw new RuntimeException("Authenticated member not found");
    }
}
