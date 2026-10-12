package com.zhihu.content.dto;

import java.util.List;

/** 分页响应（page/size 版；游标分页在性能优化课对比引入）。 */
public record QuestionPage(List<QuestionView> items, int page, int size, long total) {
}
