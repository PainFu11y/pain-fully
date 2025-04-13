package org.platform.springJpa;

import lombok.RequiredArgsConstructor;
import org.platform.entity.Friend;
import org.platform.entity.Member;
import org.platform.enums.FriendshipStatus;
import org.platform.model.FriendDto;
import org.platform.repository.FriendRepository;
import org.platform.service.FriendService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendSpringJpa implements FriendService {

   private final FriendRepository friendRepository;

    @Override
    public FriendDto createFriend(UUID friendId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Member currentMember = (Member) auth.getPrincipal();
            UUID currentMemberId = currentMember.getId();

            List<Friend> memberById = friendRepository.findByUserId1OrUserId2(currentMemberId, currentMemberId);
            Optional<Friend> matching = memberById.stream()
                    .filter(friend -> friend.getUserId1().equals(friendId) || friend.getUserId2().equals(friendId))
                    .findFirst();
            if(matching.isPresent()) {
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

    @Override
    public FriendDto changeFriendStatus(UUID friendId,FriendshipStatus status) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Member currentMember = (Member) auth.getPrincipal();
            UUID currentMemberId = currentMember.getId();

            List<Friend> friends = friendRepository.findByUserId1OrUserId2(friendId, friendId);
            if (friends.isEmpty()) {
                throw new RuntimeException("Friend not found");
            }

            Optional<Friend> matching = friends.stream()
                    .filter(friend -> friend.getUserId1().equals(currentMemberId) || friend.getUserId2().equals(currentMemberId))
                    .findFirst();
            if (matching.isEmpty()) {
                throw new AccessDeniedException("Вы не можете изменить статус этой дружбы");
            }

            Friend friend = matching.get();
            friend.setStatus(FriendshipStatus.PENDING);
            friend = friendRepository.save(friend);

            return new FriendDto(friend);
        }catch (AccessDeniedException e) {
            throw e; // throws 403
        } catch (Exception e) {
            throw new RuntimeException("Error changing friend status", e);
        }

    }

    /**
     * getting all friends except BLOCKED friends
     */
    @Override
    public List<FriendDto> getAllFriends() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Member currentMember = (Member) auth.getPrincipal();
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
       try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Member currentMember = (Member) auth.getPrincipal();
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
        }catch (Exception e){
           throw new RuntimeException("Error fetching my friends", e);
       }
    }

    @Override
    public List<FriendDto> getMyPendingFriends() {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Member currentMember = (Member) auth.getPrincipal();
            UUID currentUserId = currentMember.getId();

            List<Friend> friendships = friendRepository.findByUserId1OrUserId2(currentUserId, currentUserId);
            return friendships.stream()
                    .filter(friend -> friend.getStatus() == FriendshipStatus.PENDING)
                    .map(friend -> {
                        UUID friendId = friend.getUserId1().equals(currentUserId)
                                ? friend.getUserId2()
                                : friend.getUserId1();
                        return new FriendDto(friendId, currentUserId, friend.getStatus());
                    })
                    .toList();
        }catch (Exception e){
            throw new RuntimeException("Error fetching my friends", e);
        }
    }

    @Override
    public List<FriendDto> getMyBlockedFriends() {

        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Member currentMember = (Member) auth.getPrincipal();
            UUID currentUserId = currentMember.getId();

            List<Friend> friendships = friendRepository.findByUserId1OrUserId2(currentUserId, currentUserId);
            return friendships.stream()
                    .filter(friend -> friend.getStatus() == FriendshipStatus.BLOCKED)
                    .map(friend -> {
                        UUID friendId = friend.getUserId1().equals(currentUserId)
                                ? friend.getUserId2()
                                : friend.getUserId1();
                        return new FriendDto(friendId, currentUserId, friend.getStatus());
                    })
                    .toList();
        }catch (Exception e){
            throw new RuntimeException("Error fetching my friends", e);
        }
    }

    @Override
    public List<FriendDto> getFriendsByFriendShipStatus(FriendshipStatus friendshipStatus) {
        return List.of();
    }

    @Override
    public void deleteFriend(UUID friendId) {

    }
}
