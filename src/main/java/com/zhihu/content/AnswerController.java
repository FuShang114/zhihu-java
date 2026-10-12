package com.zhihu.content;

import com.zhihu.common.Result;
import com.zhihu.content.dto.AnswerCreateRequest;
import com.zhihu.content.dto.AnswerView;
import com.zhihu.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions/{questionId}/answers")
public class AnswerController {

    private final ContentService contentService;

    public AnswerController(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping
    public Result<AnswerView> create(@CurrentUser Long userId,
                                     @PathVariable long questionId,
                                     @Valid @RequestBody AnswerCreateRequest req) {
        return Result.ok(contentService.createAnswer(userId, questionId, req));
    }

    @GetMapping
    public Result<List<AnswerView>> list(@PathVariable long questionId) {
        return Result.ok(contentService.listAnswers(questionId));
    }
}
