package org.platform.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.platform.model.eventTag.EventTagDto;
import org.platform.model.member.MemberDto;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavouriteTagDto {
    private UUID id;
    private MemberDto member;
    private EventTagDto tag;
}