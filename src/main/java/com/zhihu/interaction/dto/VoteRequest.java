package com.zhihu.interaction.dto;

import com.zhihu.interaction.VoteType;
import jakarta.validation.constraints.NotNull;

public record VoteRequest(@NotNull VoteType type) {
}
