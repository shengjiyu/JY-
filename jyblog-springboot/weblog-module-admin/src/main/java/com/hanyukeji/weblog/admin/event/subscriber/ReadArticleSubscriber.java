package com.hanyukeji.weblog.admin.event.subscriber;


import com.hanyukeji.weblog.admin.event.ReadArticleEvent;
import com.hanyukeji.weblog.common.domain.dos.StatisticsArticlePVDO;
import com.hanyukeji.weblog.common.domain.mapper.ArticleMapper;
import com.hanyukeji.weblog.common.domain.mapper.StatisticsArticlePVMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Slf4j
public class ReadArticleSubscriber implements ApplicationListener<ReadArticleEvent> {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private StatisticsArticlePVMapper articlePVMapper;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(ReadArticleEvent event) {
        // 在这里处理收到的事件，可以是任何逻辑操作
        Long articleId = event.getArticleId();

        // 获取当前线程名称
        String threadName = Thread.currentThread().getName();

        log.info("==> threadName: {}", threadName);
        log.info("==> 文章阅读事件消费成功，articleId: {}", articleId);

        articleMapper.increaseReadNum(articleId);
        log.info("==> 文章阅读量 +1 操作成功，articleId: {}", articleId);

        // 当日文章 PV 访问量 +1
        LocalDate currDate = LocalDate.now();
        int count = articlePVMapper.increasePVCount(currDate);
        if (count == 0) {
            // 当日记录不存在（如服务在 23 点定时任务之后才启动、或定时任务执行失败），补插一条记录，避免 PV 丢失
            try {
                articlePVMapper.insert(StatisticsArticlePVDO.builder()
                        .pvDate(currDate)
                        .pvCount(1L)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build());
            } catch (DuplicateKeyException e) {
                // 并发情况下其他线程已插入，直接 +1 即可
                articlePVMapper.increasePVCount(currDate);
            }
        }
        log.info("==> 当日文章 PV 访问量 +1 操作成功，date: {}", currDate);
    }
}