package com.cl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.service.JiuzhentongzhiService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;

import java.util.List;

@Component
public class NotificationTask {

    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;

    /**
     * 定时检查并重试发送失败的通知
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void retryFailedNotifications() {
        // 查询发送失败且重试次数小于3的通知
        EntityWrapper<JiuzhentongzhiEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("tongzhizhuangtai", 2) // 2-发送失败
               .lt("chongshicishu", 3); // 重试次数小于3

        List<JiuzhentongzhiEntity> failedNotifications = jiuzhentongzhiService.selectList(wrapper);

        for (JiuzhentongzhiEntity notification : failedNotifications) {
            // 重试发送
            boolean sendSuccess = sendNotification(notification);

            // 更新通知状态
            if (sendSuccess) {
                notification.setTongzhizhuangtai(1); // 1-发送成功
                notification.setShibaoyuanyin(null);
            } else {
                notification.setChongshicishu(notification.getChongshicishu() + 1);
                notification.setShibaoyuanyin("重试失败，已尝试" + notification.getChongshicishu() + "次");
            }
            jiuzhentongzhiService.updateById(notification);
        }
    }

    /**
     * 发送通知的实际逻辑
     */
    private boolean sendNotification(JiuzhentongzhiEntity notification) {
        try {
            // 这里添加实际的通知发送逻辑，比如短信、邮件等
            // 模拟发送成功
            System.out.println("重试发送通知：" + notification.getTongzhibeizhu());
            return true;
        } catch (Exception e) {
            // 发送失败，记录日志
            System.err.println("通知重试失败：" + e.getMessage());
            return false;
        }
    }
}
