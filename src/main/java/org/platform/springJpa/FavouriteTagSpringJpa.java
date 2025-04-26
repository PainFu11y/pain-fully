package org.platform.springJpa;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.platform.entity.FavouriteTag;
import org.platform.entity.Member;
import org.platform.entity.event.EventTag;
import org.platform.model.FavouriteTagDto;
import org.platform.repository.EventRepository;
import org.platform.repository.EventTagRepository;
import org.platform.repository.FavouriteTagRepository;
import org.platform.repository.MemberRepository;
import org.platform.service.FavouriteTagService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavouriteTagSpringJpa implements FavouriteTagService {
    private final FavouriteTagRepository favouriteTagRepository;
    private final MemberRepository memberRepository;
    private final EventTagRepository eventTagRepository;

    @Override
    public FavouriteTag addFavouriteTag(UUID tagId) {
        Member member = getCurrentAuthenticatedMember();
        EventTag tag = eventTagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));

        favouriteTagRepository.findByMemberIdAndTagId(member.getId(), tagId).ifPresent(existing -> {
            throw new IllegalStateException("This tag is already in favourites");
        });

        FavouriteTag favouriteTag = new FavouriteTag();
        favouriteTag.setMember(member);
        favouriteTag.setTag(tag);

        return favouriteTagRepository.save(favouriteTag);
    }


    @Override
    public List<FavouriteTagDto> getFavouriteTagForCurrentMember() {
        Member currentMember = getCurrentAuthenticatedMember();
        List<FavouriteTag> favourites;
        try{
            favourites = favouriteTagRepository.findByMemberId(currentMember.getId());
        }catch (Exception e){
            throw new RuntimeException("Problem while getting tag");
        }


        return favourites.stream()
                .map(fav -> {
                    fav.getMember().setPassword(null);
                    FavouriteTagDto dto = new FavouriteTagDto();
                    dto.setId(fav.getId());
                    dto.setMember(fav.getMember().toDto());
                    dto.setTag(fav.getTag().toDto());
                    return dto;
                })
                .toList();
    }

    @Override
    public void deleteFavouriteTag(UUID id) {
        FavouriteTag tag = favouriteTagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Favourite tag not found"));

        Member member = getCurrentAuthenticatedMember();
        if (!tag.getMember().getId().equals(member.getId())) {
            throw new SecurityException("You are not allowed to delete this favourite tag");
        }

        favouriteTagRepository.delete(tag);
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
