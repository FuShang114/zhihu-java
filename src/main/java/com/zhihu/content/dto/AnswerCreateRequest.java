package com.zhihu.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnswerCreateRequest(
        @NotBlank @Size(max = 1_000_000) String body) {
}
