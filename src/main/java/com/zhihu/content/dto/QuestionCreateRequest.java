package com.zhihu.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QuestionCreateRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 65535) String body) {
}
