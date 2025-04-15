package org.platform.springJpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.entity.event.Event;
import org.platform.entity.event.EventMember;
import org.platform.entity.Member;
import org.platform.enums.ParticipantStatus;
import org.platform.model.event.EventDto;
import org.platform.model.event.EventParticipationDto;
import org.platform.model.event.EventMemberDto;
import org.platform.repository.EventMemberRepository;
import org.platform.repository.EventRepository;
import org.platform.repository.MemberRepository;
import org.platform.service.EventMemberService;
import org.platform.springJpa.email.EmailSpringJpa;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventMemberJpa implements EventMemberService {
    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final EmailSpringJpa emailSpringJpa;
    private final MemberRepository memberRepository;


    @Override
    public ResponseEntity<String> createEvent(UUID eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean hasMemberRole = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MEMBER"));
        String memberEmail = authentication.getName();

        if (!hasMemberRole) {
            log.warn("User {} tried to change verification status without MEMBER role", authentication.getName());
            throw new RuntimeException("Forbidden: You do not have MEMBER permissions");
        }

        Member currentMember = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> {
                    log.warn("Member with id {} not found", eventId);
                    return new RuntimeException("Member with id " + eventId + " not found");
                });

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Мероприятие не найдено"));

        // Проверяем, существует ли уже EventMember связка
        List<EventMember> existingAssociations = eventMemberRepository.findByEvent(event);
        for (EventMember em : existingAssociations) {
            if (em.getMemberList().stream().anyMatch(m -> m.getId().equals(currentMember.getId()))) {
                log.info("Пользователь {} уже участвует в мероприятии {}", currentMember.getId(), eventId);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Вы уже участвуете в этом мероприятии");
            }
        }

        EventMember eventMember = new EventMember();
        eventMember.setId(UUID.randomUUID());
        eventMember.setEvent(event);
        eventMember.setMemberList(Collections.singletonList(currentMember));

        eventMemberRepository.save(eventMember);
        log.info("Пользователь {} присоединился к мероприятию {}", currentMember.getId(), eventId);


        Locale russian = new Locale("ru");

        String dayOfWeek = event.getStartTime()
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, russian); // воскресенье

        String day = String.valueOf(event.getStartTime().getDayOfMonth()); // 20
        String month = event.getStartTime().getMonth()
                .getDisplayName(TextStyle.FULL, russian); // апреля
        String year = String.valueOf(event.getStartTime().getYear()); // 2025

        String time = event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")); // 15:00

        String formattedDate = String.format("в %s, %s %s %s года в %s", dayOfWeek, day, month, year, time);


        String htmlContent = String.format("""
        <html>
            <body style="font-family: Arial, sans-serif; color: #333;">
                <div style="max-width: 600px; margin: auto; padding: 20px; background-color: #f9f9f9; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                    <h2 style="color: #2F4F4F;">Здравствуйте, %s! 🎉</h2>
                    <p>Вы успешно <strong>присоединились</strong> к мероприятию <strong>«%s»</strong>!</p>

                    <p>Мы рады видеть вас среди участников. Теперь вы можете:</p>
                    <ul>
                        <li>Следить за обновлениями</li>
                        <li>Общаться с другими участниками</li>
                        <li>Готовиться к отличному опыту!</li>
                    </ul>

                    <hr style="margin: 20px 0;">

                    <p><strong>📅 Дата:</strong> %s</p>
                    <p><strong>📍 Место:</strong> %s</p>
                    <p><strong>🔗 О мероприятии:</strong> %s</p>

                    <hr style="margin: 20px 0;">

                    <p>Если у вас возникнут вопросы, вы всегда можете связаться с организатором.</p>

                    <p style="margin-top: 30px;">До скорой встречи!</p>
                    <p style="font-style: italic;">С уважением,<br>Команда Painfully</p>
                </div>
            </body>
        </html>
        """,
                currentMember.getUsername(),
                event.getTitle(),
                formattedDate,
                event.getLocation(),
                event.getDescription()
        );

        emailSpringJpa.sendEventJoinMessage(currentMember.getEmail(),
                "Вы успешно присоединились к мероприятию! 🎉",
                htmlContent);

        return ResponseEntity.ok("Вы успешно присоединились к мероприятию");
    }

    @Override
    public List<EventParticipationDto> getMyEvents() {
        Member currentMember = getCurrentAuthenticatedMember();

        LocalDateTime now = LocalDateTime.now();

        return eventMemberRepository.findByMember(currentMember).stream()
                .map(em -> {
                    Event event = em.getEvent();
                    String status = event.getStartTime().isAfter(now)
                            ? ParticipantStatus.WILL_PARTICIPATE.toString()
                            : ParticipantStatus.PARTICIPATED.toString();
                    EventDto eventDto = event.toDto();
                    eventDto.getOrganizerDto().setPassword(null);
                    return new EventParticipationDto(event.toDto(), status);
                })
                .toList();
    }


    @Override
    public EventMemberDto updateEventMember(UUID id, EventMemberDto eventMemberDto) {
        return null;
    }

    @Override
    public EventMemberDto getEventMember(UUID id) {
        return null;
    }

    @Override
    public List<EventMemberDto> getAllEventMembers() {
        return List.of();
    }

    @Override
    public void deleteEventMember(UUID id) {

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
