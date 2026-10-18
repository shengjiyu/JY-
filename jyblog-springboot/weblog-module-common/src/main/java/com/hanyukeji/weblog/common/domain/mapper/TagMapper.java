package com.hanyukeji.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hanyukeji.weblog.common.domain.dos.TagDO;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @author: 犬小哈
 * @url: www.hanyukeji.com
 * @date: 2023-08-22 17:06
 * @description: TODO
 **/
public interface TagMapper extends BaseMapper<TagDO> {

    /**
     * 分页查询
     * @param current
     * @param size
     * @param name
     * @param startDate
     * @param endDate
     * @return
     */
    default Page<TagDO> selectPageList(long current, long size, String name, LocalDate startDate, LocalDate endDate) {
        // 分页对象
        Page<TagDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<TagDO> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .like(StringUtils.isNotBlank(name), TagDO::getName, Objects.isNull(name) ? null : name.trim()) // 模糊查询
                .ge(Objects.nonNull(startDate), TagDO::getCreateTime, startDate) // 大于等于开始时间
                .lt(Objects.nonNull(endDate), TagDO::getCreateTime, Objects.nonNull(endDate) ? endDate.plusDays(1) : null) // 小于结束日期次日 0 点（包含结束当天）
                .orderByDesc(TagDO::getCreateTime); // order by create_time desc

        return selectPage(page, wrapper);
    }

    /**
     * 根据标签模糊查询
     * @param key
     * @return
     */
    default List<TagDO> selectByKey(String key) {
        LambdaQueryWrapper<TagDO> wrapper = new LambdaQueryWrapper<>();

        // 构造模糊查询的条件
        wrapper.like(TagDO::getName, key).orderByDesc(TagDO::getCreateTime);

        return selectList(wrapper);
    }
    /**
     * 根据标签 ID 批量查询
     * @param tagIds
     * @return
     */
    default List<TagDO> selectByIds(List<Long> tagIds) {
        // 空集合直接返回，避免生成非法 SQL: IN ()
        if (tagIds == null || tagIds.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return selectList(Wrappers.<TagDO>lambdaQuery()
                .in(TagDO::getId, tagIds));
    }
}
