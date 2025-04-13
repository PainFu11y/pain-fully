package org.platform.service;

import org.platform.model.FavouriteTagDto;

import java.util.List;
import java.util.UUID;

public interface FavouriteTagService {
    FavouriteTagDto createFavouriteTag(FavouriteTagDto eventDto);

    List<FavouriteTagDto> getAllFavouriteTags();

    FavouriteTagDto getFavouriteTagById(UUID id);

    void deleteFavouriteTag(UUID id);
}
