package com.zhihu.interaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FavoriteItemRequest(
        @NotBlank String targetType,
        @NotNull Long targetId) {
}
