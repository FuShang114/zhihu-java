package com.zhihu.interaction.dto;

/**
 * 收藏目标摘要：问题给 title，回答给 body 截断——列表页渲染够用即可，
 * 点进去再看全文（避免收藏列表 N+1 拉全文的带宽浪费）。
 */
public record TargetSummary(String title, String body) {
}
