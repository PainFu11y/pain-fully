package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.enums.FriendshipStatus;
import org.platform.enums.constants.RoutConstants;
import org.platform.model.FriendDto;
import org.platform.service.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(name = RoutConstants.BASE_URL + RoutConstants.VERSION + RoutConstants.FRIEND)
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @Operation(summary = "Добавить пользователя в друзья (отправить заявку)")
    @PostMapping("/add/{friendId}")
    public ResponseEntity<FriendDto> addFriend(@PathVariable UUID friendId) {
        FriendDto friendDto = friendService.createFriend(friendId);
        return ResponseEntity.ok(friendDto);
    }

    @Operation(summary = "Ответ на заявку в друзья (принять или отклонить)")
    @PostMapping("/respond/{senderId}")
    public ResponseEntity<FriendDto> respondToFriendRequest(
            @PathVariable UUID senderId,
            @RequestParam FriendshipStatus status) {
        FriendDto result = friendService.respondToFriendRequest(senderId, status);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Удалить друга")
    @DeleteMapping("/remove/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable UUID friendId) {
        friendService.removeFriend(friendId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Заблокировать пользователя")
    @PostMapping("/block/{friendId}")
    public ResponseEntity<FriendDto> blockFriend(@PathVariable UUID friendId) {
        FriendDto result = friendService.blockFriend(friendId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Получить список всех друзей (кроме заблокированных)")
    @GetMapping
    public ResponseEntity<List<FriendDto>> getAllFriends() {
        return ResponseEntity.ok(friendService.getAllFriends());
    }

    @Operation(summary = "Получить список принятых друзей")
    @GetMapping("/my")
    public ResponseEntity<List<FriendDto>> getMyFriends() {
        return ResponseEntity.ok(friendService.getMyFriends());
    }

    @Operation(summary = "Получить друзей по статусу (например, только PENDING или BLOCKED)")
    @GetMapping("/by-status")
    public ResponseEntity<List<FriendDto>> getFriendsByStatus(@RequestParam FriendshipStatus status) {
        return ResponseEntity.ok(friendService.getFriendsByFriendShipStatus(status));
    }

    @Operation(summary = "Отправить другу приглашение на мероприятие")
    @PostMapping("/invite/{friendId}/event/{eventId}")
    public ResponseEntity<Void> inviteFriendToEvent(
            @PathVariable UUID friendId,
            @PathVariable UUID eventId) {
        friendService.inviteFriendToEvent(friendId, eventId);
        return ResponseEntity.ok().build();
    }



}
