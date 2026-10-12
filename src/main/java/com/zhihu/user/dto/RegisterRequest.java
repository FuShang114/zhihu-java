package com.zhihu.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 注册请求。bean validation 注解 = Go 版手写空值检查的声明式升级，
 * 失败由 GlobalExceptionHandler 统一翻译成 10001。
 */
public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 32) String username,
        @NotBlank @Size(min = 6, max = 64) String password,
        @Size(max = 64) String nickname) {
}
