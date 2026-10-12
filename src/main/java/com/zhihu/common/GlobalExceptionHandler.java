package com.zhihu.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常翻译器：业务代码只管抛异常，这里统一决定对外形态。
 * 等价于 Go 版的 ginx.Fail()；HTTP 状态一律 200，业务结果看 body.code（国内主流约定）。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.fail(e.getErrorCode(), e.getMessage());
    }

    /** @Valid 校验失败 → 10001，带第一个字段错误详情（对比 Go 版手写 if 空值检查的升级） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " " + f.getDefaultMessage())
                .findFirst()
                .orElse(ErrorCode.PARAM_ERROR.msg());
        return Result.fail(ErrorCode.PARAM_ERROR, detail);
    }

    /** 路径不存在（如 GET /ping 拼错）→ 语义化的 10004，而不是被兜底成"内部错误" */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNoResource(NoResourceFoundException e) {
        return Result.fail(ErrorCode.NOT_FOUND);
    }

    /** 兜底：非预期异常对外脱敏为 10500，原始堆栈进日志（防信息泄漏，同 Go 版约定） */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleUnknown(Exception e) {
        log.error("未处理异常", e);
        return Result.fail(ErrorCode.INTERNAL);
    }
}
