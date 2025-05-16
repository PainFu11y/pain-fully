package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.entity.Organizer;
import org.platform.enums.constants.RoutConstants;
import org.platform.service.organizer.OrganizerSubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.VERSION + RoutConstants.SUBSCRIPTIONS)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Подписки", description = "Функционал подписки на организаторов")
public class OrganizerSubscriptionController {

    private final OrganizerSubscriptionService subscriptionService;

    @PostMapping("/{organizerId}")
    @Operation(summary = "Подписаться на организатора")
    public ResponseEntity<Void> subscribe(@PathVariable UUID organizerId) {
        log.info("Попытка подписки на организатора: {}", organizerId);
        subscriptionService.subscribe(organizerId);
        log.info("Успешно подписан на организатора: {}", organizerId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{organizerId}")
    @Operation(summary = "Отписаться от организатора")
    public ResponseEntity<Void> unsubscribe(@PathVariable UUID organizerId) {
        log.info("Попытка отписки от организатора: {}", organizerId);
        subscriptionService.unsubscribe(organizerId);
        log.info("Успешно отписан от организатора: {}", organizerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/is-subscribed")
    @Operation(summary = "Проверить, подписан ли участник на организатора")
    public ResponseEntity<Boolean> isSubscribed(
            @RequestParam UUID memberId,
            @RequestParam UUID organizerId
    ) {
        log.debug("Проверка подписки: memberId={}, organizerId={}", memberId, organizerId);
        boolean result = subscriptionService.isSubscribed(memberId, organizerId);
        log.debug("Результат проверки: {}", result);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-organizers")
    @Operation(summary = "Получить список организаторов, на которых подписан текущий пользователь")
    public ResponseEntity<List<Organizer>> getMySubscribedOrganizers() {
        log.info("Получение списка моих подписок");
        List<Organizer> organizers = subscriptionService.getMySubscribedOrganizers();
        log.info("Получено {} подписок", organizers.size());
        return ResponseEntity.ok(organizers);
    }

    @GetMapping("/by-member/{memberId}")
    @Operation(summary = "Получить список организаторов, на которых подписан конкретный участник по id")
    public ResponseEntity<List<Organizer>> getSubscribedOrganizers(@PathVariable UUID memberId) {
        log.info("Запрос подписок для участника: {}", memberId);
        List<Organizer> organizers = subscriptionService.getSubscribedOrganizers(memberId);
        log.info("Участник {} подписан на {} организаторов", memberId, organizers.size());
        return ResponseEntity.ok(organizers);
    }
}
