package com.zhihu.content.dto;

import com.zhihu.content.Question;
import com.zhihu.user.User;

import java.time.LocalDateTime;

public record QuestionView(Long id, String title, String body, AuthorView author,
                           int answerCount, LocalDateTime createdAt) {

    public static QuestionView from(Question q, User author) {
        return new QuestionView(q.getId(), q.getTitle(), q.getBody(),
                AuthorView.from(author), q.getAnswerCount(), q.getCreatedAt());
    }
}
