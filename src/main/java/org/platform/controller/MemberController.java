package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platform.enums.constants.RoutConstants;
import org.platform.model.member.MemberDto;
import org.platform.model.member.MemberRegistrationDto;
import org.platform.model.verify.VerifyRequest;
import org.platform.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(RoutConstants.BASE_URL + RoutConstants.VERSION + RoutConstants.MEMBER)
@RequiredArgsConstructor
@Tag(name = "Пользователи (Member)", description = "Управление участниками платформы")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "Создание пользователя")
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody MemberRegistrationDto createMember(@RequestBody MemberRegistrationDto memberDto) {
            log.info("Received request to create member with username: {}", memberDto.getUsername());
            MemberRegistrationDto createdMember = memberService.createMember(memberDto);
            log.info("Successfully created member with username: {}", createdMember.getUsername());
            return createdMember;
        }


    @Operation(summary = "Изменение пользователя")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public @ResponseBody MemberDto updateMember(@PathVariable UUID id, @RequestBody MemberDto memberDto) {
        log.info("Received request to update member with ID: {}", id);
        memberDto.setId(id);
        MemberDto updatedMember = memberService.updateMember(memberDto);
        if (updatedMember != null) {
            log.info("Member with ID {} updated successfully", id);
        } else {
            log.warn("Member with ID {} not found for update", id);
        }
        return updatedMember;
    }

    @Operation(summary = "Получить member по id")
    @GetMapping("/{id}")
    public @ResponseBody MemberDto getMemberById(@PathVariable UUID id) {
        log.info("Received request to get member by ID: {}", id);
        MemberDto member = memberService.getMemberById(id);
        log.info("Successfully fetched member with ID: {}", id);
        return member;
    }

    @Operation(summary = "Получить ползователя по email")
    @GetMapping("/email/{email}")
    public @ResponseBody MemberDto getMemberByEmail(@PathVariable String email) {
        log.info("Received request to get member by email: {}", email);
        MemberDto member = memberService.getMemberByEmail(email);
        log.info("Successfully fetched member with email: {}", email);
        return member;
    }

    @Operation(summary = "Получить ползователя по username")
    @GetMapping("/name/{name}")
    public @ResponseBody MemberDto getMemberByName(@PathVariable String name) {
        log.info("Received request to get member by username: {}", name);
        MemberDto member = memberService.getMemberByName(name);
        log.info("Successfully fetched member with username: {}", name);
        return member;
    }
    @Operation(summary = "Получить всех пользователей")
    @GetMapping
    public @ResponseBody List<MemberDto> getAllMembers() {
        log.info("Received request to get all members");
        List<MemberDto> members = memberService.getMembers();
        log.info("Successfully fetched all members");
        return members;
    }

    @Operation(summary = "Получить профиль участника с учётом настроек приватности")
    @GetMapping("/{memberId}/profile")
    public ResponseEntity<MemberDto> getMemberProfile(@PathVariable UUID memberId) {
        MemberDto dto = memberService.getMemberProfile(memberId);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable UUID id) {
        log.info("Received request to delete member with ID: {}", id);
        MemberDto memberDto = new MemberDto();
        memberDto.setId(id);
        memberService.deleteMember(memberDto);
        log.info("Successfully deleted member with ID: {}", id);
    }

    @Operation(summary = "Верификация email")
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmailVerificationCode(@RequestBody VerifyRequest verifyRequest) {
        if(memberService.verifyEmailVerificationCode(verifyRequest)){
            return ResponseEntity.ok("Verification code verified");
        }else {
            return ResponseEntity.ok("Verification code not verified");
        }
    }


    @Operation(summary = "OAuth2 Dashboard: приветственное сообщение")
    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard(@AuthenticationPrincipal OAuth2User oauthUser) {
        String name = oauthUser.getAttribute("name");
        String email = oauthUser.getAttribute("email");
        return ResponseEntity.ok("👤 Member Dashboard\nДобро пожаловать, " + name + " (" + email + ")");
    }


    @PatchMapping("/location")
    public ResponseEntity<String> updateLocation(@RequestParam String locationRequest) {
        if(memberService.updateLocation(locationRequest)){
            return ResponseEntity.ok("Location updated successfully");
        }else {
            return ResponseEntity.ok("Problem while updating location ");
        }
    }

}
