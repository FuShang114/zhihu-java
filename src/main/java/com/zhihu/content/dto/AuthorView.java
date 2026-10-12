package com.zhihu.content.dto;

import com.zhihu.user.User;

/** 作者摘要：内容域对用户域的"读模型切片"——只拿展示需要的四个字段（防腐：不暴露 User 全貌）。 */
public record AuthorView(Long id, String username, String nickname, String avatarUrl) {

    public static AuthorView from(User u) {
        if (u == null) {
            return new AuthorView(null, "已注销", "已注销", null);
        }
        return new AuthorView(u.getId(), u.getUsername(), u.getNickname(), u.getAvatarUrl());
    }
}
