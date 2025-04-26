package org.platform.service;

import org.platform.entity.FavouriteTag;
import org.platform.model.FavouriteTagDto;

import java.util.List;
import java.util.UUID;

public interface FavouriteTagService {
    FavouriteTag addFavouriteTag(UUID tagId);

    List<FavouriteTagDto> getFavouriteTagForCurrentMember();

    void deleteFavouriteTag(UUID id);
}
