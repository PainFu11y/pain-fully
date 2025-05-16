package org.platform.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.platform.entity.event.Event;
import org.platform.enums.constants.RoutConstants;
import org.platform.springJpa.event.FavoriteEventSpringJpa;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.VERSION + RoutConstants.FAVORITE_EVENTS)
@RequiredArgsConstructor
@Tag(name = "Избранные мероприятия", description = "Управление избранными мероприятиями пользователя")
public class FavoriteEventController {

    private final FavoriteEventSpringJpa favoriteEventService;


    @Operation(
            summary = "Добавить мероприятие в избранное",
            description = "Добавляет мероприятие в избранное текущего авторизованного пользователя"
    )
    @PostMapping("/{eventId}")
    public ResponseEntity<?> addToFavorites(@PathVariable UUID eventId) {
        favoriteEventService.addToFavorites(eventId);
        return ResponseEntity.ok("Event added to favorites");
    }

    @Operation(
            summary = "Удалить мероприятие из избранного",
            description = "Удаляет мероприятие из избранного текущего авторизованного пользователя"
    )
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> removeFromFavorites(@PathVariable UUID eventId) {
        favoriteEventService.removeFromFavorites(eventId);
        return ResponseEntity.ok("Event removed from favorites");
    }

    @Operation(
            summary = "Получить избранные мероприятия текущего пользователя",
            description = "Возвращает список всех мероприятий, добавленных в избранное текущим пользователем"
    )
    @GetMapping
    public ResponseEntity<List<Event>> getMyFavorites() {
        List<Event> favorites = favoriteEventService.getMyFavorites();
        return ResponseEntity.ok(favorites);
    }

    @Operation(
            summary = "Получить избранные мероприятия по ID пользователя",
            description = "Возвращает список мероприятий, добавленных в избранное указанным пользователем (по ID)"
    )
    @GetMapping("/by-member/{memberId}")
    public ResponseEntity<List<Event>> getFavoritesByMemberId(@PathVariable UUID memberId) {
        List<Event> favorites = favoriteEventService.getFavoritesByMemberId(memberId);
        return ResponseEntity.ok(favorites);
    }

}
