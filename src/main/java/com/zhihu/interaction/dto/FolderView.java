package com.zhihu.interaction.dto;

import com.zhihu.interaction.FavoriteFolder;

import java.time.LocalDateTime;

public record FolderView(Long id, String title, Boolean isPublic, LocalDateTime createdAt) {

    public static FolderView from(FavoriteFolder f) {
        return new FolderView(f.getId(), f.getTitle(), f.getIsPublic(), f.getCreatedAt());
    }
}
