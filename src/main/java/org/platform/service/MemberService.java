package org.platform.service;

import org.platform.model.member.MemberDto;
import org.platform.model.member.MemberRegistrationDto;
import org.platform.model.verify.VerifyRequest;

import java.util.List;
import java.util.UUID;

public interface MemberService {
    MemberRegistrationDto createMember(MemberRegistrationDto memberDto);

    MemberDto updateMember(MemberDto memberDto);

    MemberDto getMemberById(UUID memberID);

    MemberDto getMemberByEmail(String email);

    MemberDto getMemberByName(String name);

    List<MemberDto> getMembers();

    void deleteMember(MemberDto memberDto);

    boolean verifyEmailVerificationCode(VerifyRequest verifyRequest);

    MemberDto getMemberProfile(UUID memberId);

    boolean updateLocation(String newLocation);



}
