package com.zhihu.interaction;

import com.zhihu.common.Result;
import com.zhihu.interaction.dto.VoteRequest;
import com.zhihu.interaction.dto.VoteView;
import com.zhihu.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoteController {

    private final InteractionService interactionService;

    public VoteController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    /** 投票（UP/DOWN/NONE 取消）→ 最新赞同数。写操作，由拦截器强制登录。 */
    @PutMapping("/api/v1/answers/{answerId}/vote")
    public Result<VoteView> vote(@CurrentUser Long userId,
                                 @PathVariable long answerId,
                                 @Valid @RequestBody VoteRequest req) {
        return Result.ok(interactionService.vote(userId, answerId, req));
    }
}
