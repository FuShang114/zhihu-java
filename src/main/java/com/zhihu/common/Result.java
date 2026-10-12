package com.zhihu.common;

/**
 * 统一响应体：{"code":0,"msg":"ok","data":...}
 * record = 不可变数据载体（Java 平台的标准 DTO 形态），Jackson 自动序列化为 JSON 对象。
 */
public record Result<T>(int code, String msg, T data) {

    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "ok", data);
    }

    public static Result<Void> ok() {
        return new Result<>(0, "ok", null);
    }

    public static Result<Void> fail(ErrorCode ec) {
        return new Result<>(ec.code(), ec.msg(), null);
    }

    public static Result<Void> fail(ErrorCode ec, String overrideMsg) {
        return new Result<>(ec.code(), overrideMsg, null);
    }
}
