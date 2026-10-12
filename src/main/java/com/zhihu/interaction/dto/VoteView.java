package com.zhihu.interaction.dto;

/** 投票结果：返回最新赞同数，前端直接刷新 UI，无需再查一次。 */
public record VoteView(int voteupCount) {
}
