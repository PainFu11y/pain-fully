package org.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
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
public class FavouriteTagController {

    private final FavouriteTagService favouriteTagService;

    @Operation(summary = "Добавить favourite tag конкретному member")
    @PostMapping("/{tagId}")
    public ResponseEntity<FavouriteTagDto> addFavouriteTag(@PathVariable UUID tagId) {
        FavouriteTag added = favouriteTagService.addFavouriteTag(tagId);
        FavouriteTagDto dto = new FavouriteTagDto();
        dto.setId(added.getId());
        dto.setMember(added.getMember().toDto());
        dto.setTag(added.getTag().toDto());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "Получить все favourite tags ")
    @GetMapping
    public ResponseEntity<List<FavouriteTagDto>> getAllFavouriteTags() {
        return ResponseEntity.ok(favouriteTagService.getAllFavouriteTags());
    }
    @Operation(summary = "Удалить favourite tag по favouriteTagID")
    @DeleteMapping("/{favouriteTagId}")
    public ResponseEntity<Void> deleteFavouriteTag(@PathVariable UUID favouriteTagId) {
        favouriteTagService.deleteFavouriteTag(favouriteTagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить favourite tags конкретного member")
    @GetMapping("/my-fav-tags")
    public ResponseEntity<List<FavouriteTagDto>> getMyFavouriteTags() {
        return ResponseEntity.ok(favouriteTagService.getFavouriteTagForCurrentMember());
    }
}
