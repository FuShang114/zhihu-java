package com.zhihu.interaction.dto;

public record ItemView(Long id, String targetType, Long targetId, TargetSummary summary) {
}
