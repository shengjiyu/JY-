package com.hanyukeji.weblog.common.model;

import lombok.Data;

@Data
public class BasePageQuery {
    /**
     * 单页最大条数，防止一次性拉取过多数据
     */
    private static final long MAX_SIZE = 100L;

    /**
     * 当前页码, 默认第一页
     */
    private Long current = 1L;
    /**
     * 每页展示的数据数量，默认每页展示 10 条数据
     */
    private Long size = 10L;

    public Long getCurrent() {
        return (current == null || current < 1) ? 1L : current;
    }

    public Long getSize() {
        if (size == null || size < 1) {
            return 10L;
        }
        return Math.min(size, MAX_SIZE);
    }
}
