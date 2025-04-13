package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.enums.constants.RoutConstants;
import org.platform.service.EventMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.VERSION + RoutConstants.EVENT_MEMBERS)
@RequiredArgsConstructor
public class EventMemberController {
    private final EventMemberService eventMemberService;

    @Operation(summary = "Добавить участие в мероприятии для текущего member")
    @PostMapping("/join")
    public ResponseEntity<String> joinEvent(@RequestParam UUID eventId) {
        return eventMemberService.createEvent(eventId);
    }
}
