package com.hanyukeji.weblog.web.service;

import com.hanyukeji.weblog.common.utils.Response;
import com.hanyukeji.weblog.web.model.vo.archive.FindArchiveArticlePageListReqVO;

public interface ArchiveService {
    /**
     * 获取文章归档分页数据
     * @param findArchiveArticlePageListReqVO
     * @return
     */
    Response findArchivePageList(FindArchiveArticlePageListReqVO findArchiveArticlePageListReqVO);
}
