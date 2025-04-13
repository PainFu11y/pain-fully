package org.platform.springJpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.entity.event.Event;
import org.platform.entity.event.EventMember;
import org.platform.entity.Member;
import org.platform.enums.ParticipantStatus;
import org.platform.model.event.EventMemberDto;
import org.platform.repository.EventMemberRepository;
import org.platform.repository.EventRepository;
import org.platform.service.EventMemberService;
import org.platform.springJpa.email.EmailSpringJpa;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventMemberJpa implements EventMemberService {
    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final EmailSpringJpa emailSpringJpa;


    @Override
    public ResponseEntity<String> createEvent(UUID eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member currentMember = (Member) authentication.getPrincipal();

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
        eventMember.setStatus(ParticipantStatus.WILL_PARTICIPATE);
        eventMember.setCreatedAt(LocalDateTime.now());

        eventMemberRepository.save(eventMember);
        log.info("Пользователь {} присоединился к мероприятию {}", currentMember.getId(), eventId);

        emailSpringJpa.sendMessage(
                currentMember.getEmail(),
                String.format("""
                        Здравствуйте, %s!
                                                
                        Вы успешно присоединились к мероприятию «%s»!
                                                
                        Мы рады видеть вас среди участников. Теперь вы можете следить за обновлениями и общаться с другими участниками.
                                                
                        🗓️ Дата мероприятия: %s \s
                        📍 Место проведения: %s \s
                        🔗 Подробнее о мероприятии: %s
                                                
                        Если у вас возникнут вопросы, вы всегда можете связаться с организатором.
                                                
                        До скорой встречи!
                                                
                        С уважением, \s
                        Команда Painfully
                                                
                        """, currentMember.getUsername(),event.getTitle(),
                        event.getStartTime().toString(),
                        event.getLocation(), event.getDescription()
                        )
                ,"Вы успешно присоединились к мероприятию! \uD83C\uDF89 ");
        return ResponseEntity.ok("Вы успешно присоединились к мероприятию");
    }

    @Override
    public EventMemberDto createEventMember(EventMemberDto eventMemberDto) {
        return null;
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


}
