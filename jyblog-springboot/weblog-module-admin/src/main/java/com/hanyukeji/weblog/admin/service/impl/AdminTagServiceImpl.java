package com.hanyukeji.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanyukeji.weblog.admin.model.vo.tag.*;
import com.hanyukeji.weblog.admin.service.AdminTagService;
import com.hanyukeji.weblog.common.domain.dos.ArticleTagRelDO;
import com.hanyukeji.weblog.common.domain.dos.TagDO;
import com.hanyukeji.weblog.common.domain.mapper.ArticleTagRelMapper;
import com.hanyukeji.weblog.common.domain.mapper.TagMapper;
import com.hanyukeji.weblog.common.enums.ResponseCodeEnum;
import com.hanyukeji.weblog.common.exception.BizException;
import com.hanyukeji.weblog.common.model.vo.SelectRspVO;
import com.hanyukeji.weblog.common.utils.PageResponse;
import com.hanyukeji.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: 犬小哈
 * @url: www.hanyukeji.com
 * @date: 2023-09-15 14:03
 * @description: TODO
 **/
@Service
@Slf4j
public class AdminTagServiceImpl extends ServiceImpl<TagMapper, TagDO> implements AdminTagService {

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    /**
     * 添加标签集合
     *
     * @param addTagReqVO
     * @return
     */
    @Override
    public Response addTags(AddTagReqVO addTagReqVO) {
        // 查询表中已有标签名（小写），用于过滤重复标签
        Set<String> existedLowerNames = tagMapper.selectList(Wrappers.emptyWrapper()).stream()
                .map(tagDO -> tagDO.getName().toLowerCase())
                .collect(Collectors.toSet());

        // vo 转 do：去空白、去重（忽略大小写）、过滤已存在的标签
        // 原实现直接 saveBatch 并吞掉异常：只要有一个标签重复，整批都会回滚，但接口仍返回成功
        List<TagDO> tagDOS = addTagReqVO.getTags().stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .filter(tagName -> existedLowerNames.add(tagName.toLowerCase()))
                .map(tagName -> TagDO.builder()
                        .name(tagName)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(tagDOS)) {
            throw new BizException(ResponseCodeEnum.TAG_CANT_DUPLICATE);
        }

        // 批量插入
        saveBatch(tagDOS);
        return Response.success();
    }

    /**
     * 查询标签分页
     *
     * @param findTagPageListReqVO
     * @return
     */
    @Override
    public PageResponse findTagPageList(FindTagPageListReqVO findTagPageListReqVO) {
        // 分页参数、条件参数
        Long current = findTagPageListReqVO.getCurrent();
        Long size = findTagPageListReqVO.getSize();
        String name = findTagPageListReqVO.getName();
        LocalDate startDate = findTagPageListReqVO.getStartDate();
        LocalDate endDate = findTagPageListReqVO.getEndDate();

        // 分页查询
        Page<TagDO> page = tagMapper.selectPageList(current, size, name, startDate, endDate);

        List<TagDO> records = page.getRecords();

        // do 转 vo
        List<FindTagPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(records)) {
            vos = records.stream().map(tagDO -> FindTagPageListRspVO.builder()
                    .id(tagDO.getId())
                    .name(tagDO.getName())
                    .createTime(tagDO.getCreateTime())
                    .build()).collect(Collectors.toList());
        }

        return PageResponse.success(page, vos);
    }

    /**
     * 根据标签关键词模糊查询
     *
     * @param searchTagsReqVO
     * @return
     */
    @Override
    public Response searchTags(SearchTagsReqVO searchTagsReqVO) {
        String key = searchTagsReqVO.getKey();

        // 执行模糊查询
        List<TagDO> tagDOS = tagMapper.selectByKey(key);

        // do 转 vo
        List<SelectRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            vos = tagDOS.stream()
                    .map(tagDO -> SelectRspVO.builder()
                            .label(tagDO.getName())
                            .value(tagDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }

    @Override
    public Response findTagSelectList() {
        // 查询所有标签, Wrappers.emptyWrapper() 表示查询条件为空
        List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());

        // DO 转 VO
        List<SelectRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            vos = tagDOS.stream()
                    .map(tagDO -> SelectRspVO.builder()
                            .label(tagDO.getName())
                            .value(tagDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }

    /**
     * 删除标签
     *
     * @param deleteTagReqVO
     * @return
     */
    @Override
    public Response deleteTag(DeleteTagReqVO deleteTagReqVO) {
        // 标签 ID
        Long tagId = deleteTagReqVO.getId();

        // 校验该标签下是否有关联的文章，若有，则不允许删除，提示用户需要先删除标签下的文章
        ArticleTagRelDO articleTagRelDO = articleTagRelMapper.selectOneByTagId(tagId);

        if (Objects.nonNull(articleTagRelDO)) {
            log.warn("==> 此标签下包含文章，无法删除，tagId: {}", tagId);
            throw new BizException(ResponseCodeEnum.TAG_CAN_NOT_DELETE);
        }

        // 根据标签 ID 删除
        int count = tagMapper.deleteById(tagId);

        return count == 1 ? Response.success() : Response.fail(ResponseCodeEnum.TAG_NOT_EXISTED);
    }

}
