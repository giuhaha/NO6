package com.cl.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.entity.JiuzhentongzhiRecordEntity;
import com.cl.service.JiuzhentongzhiRecordService;
import com.cl.utils.NoticeSender;

/**
 * 就诊通知重试定时任务
 */
@Component
public class NoticeRetryTask {

    @Autowired
    private JiuzhentongzhiRecordService jiuzhentongzhiRecordService;
    
    @Autowired
    private NoticeSender noticeSender;

    /**
     * 每隔 10 分钟检查一次发送失败的通知记录并重试
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void retryFailedNotices() {
        try {
            // 查询发送失败且需要重试的记录 (重试次数小于 3 次)
            List<JiuzhentongzhiRecordEntity> failedRecords = jiuzhentongzhiRecordService.selectList(
                new EntityWrapper<JiuzhentongzhiRecordEntity>()
                    .eq("send_status", 2) // 发送失败
                    .lt("retry_count", 3) // 重试次数小于 3 次
                    .le("next_retry_time", new Date()) // 重试时间已到或已过
            );
            
            for (JiuzhentongzhiRecordEntity record : failedRecords) {
                // 增加重试次数
                record.setRetryCount(record.getRetryCount() + 1);
                
                // 重新尝试发送
                boolean success = noticeSender.sendSms(record.getReceiverPhone(), record.getContent());
                
                if (success) {
                    record.setSendStatus(1); // 发送成功
                    record.setSendTime(new Date());
                    record.setFailReason(null);
                    record.setNextRetryTime(null);
                } else {
                    record.setSendStatus(2); // 仍然失败
                    record.setFailReason("第" + record.getRetryCount() + "次重试失败");
                    
                    // 计算下次重试时间 (指数退避：5 分钟，10 分钟，20 分钟)
                    long retryInterval = 5 * 60 * 1000 * (long)Math.pow(2, record.getRetryCount() - 1);
                    record.setNextRetryTime(new Date(System.currentTimeMillis() + retryInterval));
                }
                
                // 更新记录
                jiuzhentongzhiRecordService.updateById(record);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
