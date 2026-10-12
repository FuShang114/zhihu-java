package com.zhihu.content;

import com.zhihu.common.Result;
import com.zhihu.content.dto.QuestionCreateRequest;
import com.zhihu.content.dto.QuestionPage;
import com.zhihu.content.dto.QuestionView;
import com.zhihu.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final ContentService contentService;

    public QuestionController(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping
    public Result<QuestionView> create(@CurrentUser Long userId,
                                       @Valid @RequestBody QuestionCreateRequest req) {
        return Result.ok(contentService.createQuestion(userId, req));
    }

    @GetMapping("/{id}")
    public Result<QuestionView> get(@PathVariable long id) {
        return Result.ok(contentService.getQuestion(id));
    }

    @GetMapping
    public Result<QuestionPage> list(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return Result.ok(contentService.listQuestions(page, size));
    }
}
