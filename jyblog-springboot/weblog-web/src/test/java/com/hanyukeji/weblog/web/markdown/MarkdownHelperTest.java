package com.hanyukeji.weblog.web.markdown;

import com.hanyukeji.weblog.common.model.BasePageQuery;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 纯单元测试（不启动 Spring 容器、不依赖数据库）
 */
class MarkdownHelperTest {

    @Test
    void imageTitleShouldBeEscaped() {
        String html = MarkdownHelper.convertMarkdown2Html("![a](/x.png \"\\\" onerror=\\\"alert(1)\")");
        assertFalse(html.contains("onerror=\"alert(1)\""), html);
        assertTrue(html.contains("&quot;"), html);
    }

    @Test
    void imageWidthHeightRendered() {
        String html = MarkdownHelper.convertMarkdown2Html("![t](/u.png \"标题\"){width=100 height=50}");
        assertTrue(html.contains("width=\"100\""), html);
        assertTrue(html.contains("height=\"50\""), html);
        assertTrue(html.contains("<span class=\"image-caption\">标题</span>"), html);
    }

    @Test
    void pageSizeShouldBeClamped() {
        BasePageQuery query = new BasePageQuery();
        query.setSize(100000L);
        query.setCurrent(-5L);
        assertEquals(100L, query.getSize());
        assertEquals(1L, query.getCurrent());

        query.setSize(0L);
        assertEquals(10L, query.getSize());
    }
}
