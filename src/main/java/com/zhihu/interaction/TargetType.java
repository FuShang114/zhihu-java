package com.zhihu.interaction;

import com.zhihu.common.BusinessException;
import com.zhihu.common.ErrorCode;

/** 可收藏对象类型。解析失败 → 10001。 */
public enum TargetType {
    ANSWER, QUESTION;

    public static TargetType parse(String s) {
        try {
            return valueOf(s.toUpperCase());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "未知的收藏目标类型: " + s);
        }
    }
}
