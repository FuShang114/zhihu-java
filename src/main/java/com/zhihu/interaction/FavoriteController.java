package com.zhihu.interaction;

import com.zhihu.common.Result;
import com.zhihu.interaction.dto.FavoriteItemRequest;
import com.zhihu.interaction.dto.FolderCreateRequest;
import com.zhihu.interaction.dto.FolderView;
import com.zhihu.interaction.dto.ItemView;
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
@RequestMapping("/api/v1/favorite-folders")
public class FavoriteController {

    private final InteractionService interactionService;

    public FavoriteController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping
    public Result<FolderView> createFolder(@CurrentUser Long userId,
                                           @Valid @RequestBody FolderCreateRequest req) {
        return Result.ok(interactionService.createFolder(userId, req));
    }

    @GetMapping
    public Result<List<FolderView>> listMine(@CurrentUser Long userId) {
        return Result.ok(interactionService.listFolders(userId));
    }

    @PostMapping("/{folderId}/items")
    public Result<ItemView> addItem(@CurrentUser Long userId,
                                    @PathVariable long folderId,
                                    @Valid @RequestBody FavoriteItemRequest req) {
        return Result.ok(interactionService.addItem(userId, folderId, req));
    }

    @GetMapping("/{folderId}/items")
    public Result<List<ItemView>> listItems(@CurrentUser Long userId,
                                            @PathVariable long folderId) {
        return Result.ok(interactionService.listItems(userId, folderId));
    }
}
