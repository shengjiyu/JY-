package com.hanyukeji.weblog.web.service;

import com.hanyukeji.weblog.common.utils.Response;
import com.hanyukeji.weblog.web.model.vo.article.FindArticleDetailReqVO;
import com.hanyukeji.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;

public interface ArticleService {
    /**
     * 获取首页文章分页数据
     * @param findIndexArticlePageListReqVO
     * @return
     */
    Response findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO);
    /**
     * 获取文章详情
     * @param findArticleDetailReqVO
     * @return
     */
    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

}

