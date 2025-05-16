package org.platform.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.enums.constants.RoutConstants;
import org.platform.model.event.EventParticipationDto;
import org.platform.service.event.EventMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.VERSION + RoutConstants.EVENT_MEMBERS)
@RequiredArgsConstructor
@Tag(name = "Участие пользователей в мероприятиях", description = "Управление участием member'ов в мероприятиях")
public class EventMemberController {
    private final EventMemberService eventMemberService;

    @Operation(summary = "Добавить участие в мероприятии для текущего member")
    @PostMapping("/join")
    public ResponseEntity<String> joinEvent(@RequestParam UUID eventId) {
        return eventMemberService.createEvent(eventId);
    }

    @Operation(summary = "Получить список моих мероприятий с их статусами (PARTICIPATED / WILL_PARTICIPATE)")
    @GetMapping("/my-events")
    public ResponseEntity<List<EventParticipationDto>> getMyEvents() {
        List<EventParticipationDto> myEvents = eventMemberService.getMyEvents();
        return ResponseEntity.ok(myEvents);
    }
}
