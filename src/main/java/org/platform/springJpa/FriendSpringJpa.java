package org.platform.springJpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.entity.Friend;
import org.platform.entity.Member;
import org.platform.entity.event.Event;
import org.platform.enums.FriendshipStatus;
import org.platform.model.FriendDto;
import org.platform.repository.event.EventRepository;
import org.platform.repository.FriendRepository;
import org.platform.repository.MemberRepository;
import org.platform.service.FriendService;
import org.platform.service.email.EmailService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendSpringJpa implements FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final EmailService emailService;


    @Override
    public FriendDto createFriend(UUID friendId) {
        try {
            Member currentMember = getMemberFromContext();
            UUID currentMemberId = currentMember.getId();

            List<Friend> memberById = friendRepository.findByUserId1OrUserId2(currentMemberId, currentMemberId);
            Optional<Friend> matching = memberById.stream()
                    .filter(friend -> friend.getUserId1().equals(friendId) || friend.getUserId2().equals(friendId))
                    .findFirst();
            if (matching.isPresent()) {
                throw new AccessDeniedException("You are not allowed to create a friend");
            }

            Friend friend = new Friend();
            friend.setUserId1(currentMemberId);
            friend.setUserId2(friendId);
            friend.setStatus(FriendshipStatus.PENDING);

            Friend savedFriend = friendRepository.save(friend);

            return new FriendDto(savedFriend);
        } catch (Exception e) {
            throw new RuntimeException("Error creating friend", e);
        }
    }

    public FriendDto respondToFriendRequest(UUID senderId, FriendshipStatus responseStatus) {
        if (responseStatus != FriendshipStatus.ACCEPTED && responseStatus != FriendshipStatus.BLOCKED) {
            throw new IllegalArgumentException("Недопустимый статус для ответа на заявку");
        }

        Member currentMember = getMemberFromContext();
        UUID currentUserId = currentMember.getId();

        Friend friend = friendRepository
                .findByUserId1AndUserId2(senderId, currentUserId)
                .orElseThrow(() -> new RuntimeException("Запрос в друзья не найден"));

        if (!friend.getStatus().equals(FriendshipStatus.PENDING)) {
            throw new RuntimeException("Запрос уже был обработан");
        }

        friend.setStatus(responseStatus);
        friend = friendRepository.save(friend);
        return new FriendDto(friend);
    }


    /**
     * getting all friends except BLOCKED friends
     */
    @Override
    public List<FriendDto> getAllFriends() {
        try {
            Member currentMember = getMemberFromContext();
            UUID currentUserId = currentMember.getId();

            List<Friend> friendships = friendRepository.findByUserId1OrUserId2(currentUserId, currentUserId);

            return friendships.stream()
                    .filter(friend -> friend.getStatus() != FriendshipStatus.BLOCKED)
                    .map(FriendDto::new)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching friends", e);
        }
    }

    @Override
    public List<FriendDto> getMyFriends() {
        try {
            Member currentMember = getMemberFromContext();
            UUID currentUserId = currentMember.getId();

            List<Friend> friendships = friendRepository.findByUserId1OrUserId2(currentUserId, currentUserId);
            return friendships.stream()
                    .filter(friend -> friend.getStatus() == FriendshipStatus.ACCEPTED)
                    .map(friend -> {
                        UUID friendId = friend.getUserId1().equals(currentUserId)
                                ? friend.getUserId2()
                                : friend.getUserId1();
                        return new FriendDto(friendId, currentUserId, friend.getStatus());
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching my friends", e);
        }
    }


    @Override
    public List<FriendDto> getFriendsByFriendShipStatus(FriendshipStatus friendshipStatus) {
        try {
            Member currentMember = getMemberFromContext();
            UUID currentUserId = currentMember.getId();

            List<Friend> friendships = friendRepository.findByUserId1OrUserId2(currentUserId, currentUserId);

            return friendships.stream()
                    .filter(friend -> friend.getStatus() == friendshipStatus)
                    .map(friend -> {
                        UUID friendId = friend.getUserId1().equals(currentUserId)
                                ? friend.getUserId2()
                                : friend.getUserId1();
                        return new FriendDto(friendId, currentUserId, friend.getStatus());
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching friends by status", e);
        }
    }

    public void removeFriend(UUID friendId) {
        Member currentMember = getMemberFromContext();
        UUID currentUserId = currentMember.getId();

        Friend friend = friendRepository
                .findByUserId1AndUserId2(friendId, currentUserId)
                .or(() -> friendRepository.findByUserId1AndUserId2(currentUserId, friendId))
                .orElseThrow(() -> new RuntimeException("Дружба не найдена"));

        if (!friend.getStatus().equals(FriendshipStatus.ACCEPTED)) {
            throw new RuntimeException("Удалять можно только друзей");
        }

        friendRepository.delete(friend);
    }


    public FriendDto blockFriend(UUID friendId) {
        Member currentMember = getMemberFromContext();
        UUID currentUserId = currentMember.getId();

        Friend friend = friendRepository
                .findByUserId1AndUserId2(friendId, currentUserId)
                .or(() -> friendRepository.findByUserId1AndUserId2(currentUserId, friendId))
                .orElseThrow(() -> new RuntimeException("Дружба не найдена"));

        friend.setStatus(FriendshipStatus.BLOCKED);
        friendRepository.save(friend);

        return new FriendDto(friend);
    }

    @Override
    public void inviteFriendToEvent(UUID friendId, UUID eventId) {
        Member currentMember = getMemberFromContext();
        UUID memberId = currentMember.getId();

        List<Friend> friendships = friendRepository.findByUserId1OrUserId2(memberId, memberId);
        boolean isFriend = friendships.stream()
                .anyMatch(f -> f.getStatus() == FriendshipStatus.ACCEPTED &&
                        ((f.getUserId1().equals(memberId) && f.getUserId2().equals(friendId)) ||
                                (f.getUserId2().equals(memberId) && f.getUserId1().equals(friendId))));

        if (!isFriend) {
            throw new AccessDeniedException("You can't invite this member");
        }

        Member friend = memberRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Мероприятие не найдено"));

        emailService.sendEventInvitationEmail(friend, currentMember, event);

    }

    private Member getMemberFromContext(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            log.warn("Unauthenticated request attempted to change verification status");
            throw new RuntimeException("Unauthorized");
        }
        boolean hasMemberRole = auth.getAuthorities().stream()
                .anyMatch(authentication -> authentication.getAuthority().equals("ROLE_MEMBER"));

        if (!hasMemberRole) {
            log.warn("User {} tried to change verification status without MODERATOR role", auth.getName());
            throw new RuntimeException("Forbidden: You do not have member permissions");
        }

        return  memberRepository.findByEmail(auth.getName())
                .orElseThrow(() -> {
                    log.warn("Member with email {} not found", auth.getName());
                    return new RuntimeException("Member with email " + auth.getName() + " not found");
                });

    }
}
