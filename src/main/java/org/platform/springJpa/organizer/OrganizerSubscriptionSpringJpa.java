package org.platform.springJpa.organizer;

import lombok.RequiredArgsConstructor;
import org.platform.entity.Member;
import org.platform.entity.Organizer;
import org.platform.entity.OrganizerSubscription;
import org.platform.repository.MemberRepository;
import org.platform.repository.OrganizerRepository;
import org.platform.repository.OrganizerSubscriptionRepository;
import org.platform.service.organizer.OrganizerSubscriptionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizerSubscriptionSpringJpa implements OrganizerSubscriptionService {

    private final OrganizerRepository organizerRepository;
    private final MemberRepository memberRepository;
    private final OrganizerSubscriptionRepository subscriptionRepository;

    public void subscribe(UUID organizerId) {
        UUID memberId = getCurrentAuthenticatedMember().getId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Cannot find member with id " + memberId));

        Organizer organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Cannot find organizer with id " + organizerId));


        if (subscriptionRepository.existsByMemberAndOrganizer(member, organizer)) {
            throw new IllegalStateException("Already subscribed");
        }

        OrganizerSubscription subscription = new OrganizerSubscription();
        subscription.setMember(member);
        subscription.setOrganizer(organizer);
        subscriptionRepository.save(subscription);
    }

    public void unsubscribe(UUID organizerId) {
        UUID memberId = getCurrentAuthenticatedMember().getId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Cannot find member with id " + memberId));

        Organizer organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Cannot find organizer with id " + organizerId));

        OrganizerSubscription subscription = subscriptionRepository
                .findByMemberAndOrganizer(member, organizer)
                .orElseThrow(() -> new RuntimeException("Subscription not found for organizer " + organizerId));

        subscriptionRepository.delete(subscription);
    }


    public boolean isSubscribed(UUID memberId, UUID organizerId) {

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if(optionalMember.isEmpty()) {
            throw new IllegalStateException("Member not found");
        }
        Optional<Organizer> optionalOrganizer = organizerRepository.findById(organizerId);
        if(optionalOrganizer.isEmpty()) {
            throw new IllegalStateException("Organizer not found");
        }

        return subscriptionRepository.existsByMemberAndOrganizer(optionalMember.get(), optionalOrganizer.get());
    }

    public List<Organizer> getSubscribedOrganizers(UUID memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if(optionalMember.isEmpty()) {
            throw new IllegalStateException("Member not found");
        }

        return subscriptionRepository.findAllByMember(optionalMember.get()).stream()
                .map(OrganizerSubscription::getOrganizer)
                .collect(Collectors.toList());
    }


    public List<Organizer> getMySubscribedOrganizers() {
        Member currentAuthenticatedMember = getCurrentAuthenticatedMember();
        return subscriptionRepository.findAllByMember(currentAuthenticatedMember).stream()
                .map(OrganizerSubscription::getOrganizer)
                .collect(Collectors.toList());
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

