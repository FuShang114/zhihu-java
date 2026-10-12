package com.zhihu.user.dto;

/** 登录成功响应：通行证 + 用户信息。 */
public record LoginView(String token, UserView user) {
}
