package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.platform.entity.FavouriteTag;
import org.platform.model.FavouriteTagDto;
import org.platform.service.FavouriteTagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/favourite-tags")
@RequiredArgsConstructor
@Tag(name = "Избранные теги", description = "Управление любимыми тегами участника (только для MEMBER)")
public class FavouriteTagController {

    private final FavouriteTagService favouriteTagService;

    @Operation(summary = "Добавить favourite tag конкретному member(только для MEMBER)")
    @PostMapping("/{tagId}")
    public ResponseEntity<FavouriteTagDto> addFavouriteTag(@PathVariable UUID tagId) {
        FavouriteTag added = favouriteTagService.addFavouriteTag(tagId);
        FavouriteTagDto dto = new FavouriteTagDto();
        dto.setId(added.getId());
        dto.setMember(added.getMember().toDto());
        dto.setTag(added.getTag().toDto());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Удалить favourite tag по favouriteTagID(только для MEMBER)")
    @DeleteMapping("/{favouriteTagId}")
    public ResponseEntity<Void> deleteFavouriteTag(@PathVariable UUID favouriteTagId) {
        favouriteTagService.deleteFavouriteTag(favouriteTagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить favourite tags конкретного member(только для MEMBER)")
    @GetMapping("/my-fav-tags")
    public ResponseEntity<List<FavouriteTagDto>> getMyFavouriteTags() {
        return ResponseEntity.ok(favouriteTagService.getFavouriteTagForCurrentMember());
    }
}
