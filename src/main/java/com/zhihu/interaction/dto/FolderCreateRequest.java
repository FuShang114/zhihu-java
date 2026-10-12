package com.zhihu.interaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FolderCreateRequest(
        @NotBlank @Size(max = 64) String title,
        @NotNull Boolean isPublic) {
}
