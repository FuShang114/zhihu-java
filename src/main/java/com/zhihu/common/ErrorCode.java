package com.zhihu.common;

/**
 * 全局业务错误码。约定：0 成功；1xxxx 通用；2xxxx 用户；3xxxx 内容；4xxxx 互动。
 * 与姊妹项目 zhihu-go 保持同一套码表，前端可以无差别对接。
 */
public enum ErrorCode {
    OK(0, "ok"),
    PARAM_ERROR(10001, "参数错误"),
    UNAUTHORIZED(10002, "未登录或令牌失效"),
    FORBIDDEN(10003, "无权限"),
    NOT_FOUND(10004, "资源不存在"),
    RATE_LIMITED(10005, "请求过于频繁"),
    INTERNAL(10500, "内部错误"),
    USERNAME_TAKEN(20001, "用户名已被占用"),
    BAD_CREDENTIALS(20002, "用户名或密码错误"),
    CANNOT_VOTE_OWN(40001, "不能赞同自己的回答"),
    ALREADY_FAVORITED(40009, "已收藏过");

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }
}
