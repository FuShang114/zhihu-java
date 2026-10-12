package com.zhihu.content.dto;

import com.zhihu.content.Answer;
import com.zhihu.user.User;

import java.time.LocalDateTime;

public record AnswerView(Long id, Long questionId, String body, AuthorView author,
                         int voteupCount, LocalDateTime createdAt) {

    public static AnswerView from(Answer a, User author) {
        return new AnswerView(a.getId(), a.getQuestionId(), a.getBody(),
                AuthorView.from(author), a.getVoteupCount(), a.getCreatedAt());
    }
}
