package com.cl.utils;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/**
 * 通知发送工具类
 */
@Component
public class NoticeSender {

    /**
     * 发送短信通知
     * @param phone 手机号
     * @param content 内容
     * @return 发送结果 (true:成功，false:失败)
     */
    public boolean sendSms(String phone, String content) {
        try {
            // TODO: 集成实际的短信发送服务 (如阿里云、腾讯云等)
            System.out.println("发送短信到：" + phone);
            System.out.println("短信内容：" + content);
            
            // 模拟发送，实际应该调用第三方 API
            // 这里假设总是成功
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送站内信
     * @param userId 用户 ID
     * @param title 标题
     * @param content 内容
     * @return 发送结果
     */
    public boolean send站内信(Long userId, String title, String content) {
        try {
            // TODO: 实现站内信发送逻辑
            System.out.println("发送站内信给用户：" + userId);
            System.out.println("标题：" + title);
            System.out.println("内容：" + content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 构建预约成功通知内容
     */
    public String buildAppointmentSuccessContent(String patientName, String doctorName, Date appointmentTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("【医院挂号系统】尊敬的患者 ").append(patientName).append(":\n");
        sb.append("您已成功预约 ").append(doctorName).append(" 医生的号源。\n");
        sb.append("就诊时间：").append(CommonUtil.dateFormat(appointmentTime)).append("\n");
        sb.append("请按时就诊，如有变动请及时取消预约。");
        return sb.toString();
    }

    /**
     * 构建就诊提醒通知内容
     */
    public String buildAppointmentReminderContent(String patientName, String doctorName, Date appointmentTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("【医院挂号系统】温馨提醒:\n");
        sb.append("尊敬的患者 ").append(patientName).append(",您预约了 ").append(doctorName).append(" 医生的号源。\n");
        sb.append("就诊时间：").append(CommonUtil.dateFormat(appointmentTime)).append("\n");
        sb.append("请提前 15 分钟到达诊室，携带好相关病历资料。");
        return sb.toString();
    }
}
