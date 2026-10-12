package com.zhihu.user.dto;

import com.zhihu.user.User;

import java.time.LocalDateTime;

/**
 * 对外用户视图：从实体里挑字段，天然不含 passwordHash——这正是 Go 版 json:"-" 保险丝的等价物。
 * record 字段名直接决定 JSON key（Jackson 按 record 组件序列化）。
 */
public record UserView(Long id, String username, String nickname, String avatarUrl, String bio,
                       LocalDateTime createdAt) {

    public static UserView from(User u) {
        return new UserView(u.getId(), u.getUsername(), u.getNickname(), u.getAvatarUrl(), u.getBio(),
                u.getCreatedAt());
    }
}
