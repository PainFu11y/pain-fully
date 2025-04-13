package org.platform.service;

import org.platform.model.MemberDto;

import java.util.List;
import java.util.UUID;

public interface MemberService {
    MemberDto createMember(MemberDto memberDto);

    MemberDto updateMember(MemberDto memberDto);

    MemberDto getMemberById(UUID memberID);

    MemberDto getMemberByEmail(String email);

    MemberDto getMemberByName(String name);

    List<MemberDto> getMembers();

    void deleteMember(MemberDto memberDto);

    boolean sendEmailVerificationCode(String email);

    boolean verifyEmailVerificationCode(String token);

}
