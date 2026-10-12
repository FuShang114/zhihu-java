package com.zhihu.interaction;

/** 投票类型。Jackson 会把请求里的 "UP"/"DOWN"/"NONE" 自动映射到枚举。 */
public enum VoteType {
    UP(1), DOWN(-1), NONE(0);

    private final int value;

    VoteType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
