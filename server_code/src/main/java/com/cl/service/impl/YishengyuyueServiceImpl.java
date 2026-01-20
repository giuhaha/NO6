package com.cl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;


import com.cl.dao.YishengyuyueDao;
import com.cl.entity.YishengyuyueEntity;
import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.JiuzhentongzhiRecordEntity;
import com.cl.service.YishengyuyueService;
import com.cl.service.JiuzhentongzhiService;
import com.cl.service.JiuzhentongzhiRecordService;
import com.cl.entity.view.YishengyuyueView;
import com.cl.utils.NoticeSender;
import com.cl.utils.CommonUtil;

@Service("yishengyuyueService")
public class YishengyuyueServiceImpl extends ServiceImpl<YishengyuyueDao, YishengyuyueEntity> implements YishengyuyueService {

    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;
    
    @Autowired
    private JiuzhentongzhiRecordService jiuzhentongzhiRecordService;
    
    @Autowired
    private NoticeSender noticeSender;

    	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<YishengyuyueEntity> page = this.selectPage(
                new Query<YishengyuyueEntity>(params).getPage(),
                new EntityWrapper<YishengyuyueEntity>()
        );
        return new PageUtils(page);
    }
    
    /**
     * 发送预约成功通知 (立即发送所有后续提醒)
     * @param appointment 预约信息
     */
    private void sendAppointmentNotices(YishengyuyueEntity appointment) {
        try {
            // 1. 创建就诊通知记录
            JiuzhentongzhiEntity notice = new JiuzhentongzhiEntity<>();
            notice.setTongzhibianhao(String.valueOf(new Date().getTime()));
            notice.setYishengzhanghao(appointment.getYishengzhanghao());
            notice.setDianhua(appointment.getDianhua());
            notice.setJiuzhenshijian(appointment.getYuyueshijian());
            notice.setTongzhishijian(new Date());
            notice.setZhanghao(appointment.getZhanghao());
            notice.setShouji(appointment.getShouji());
            notice.setTongzhibeizhu("预约成功，已发送所有提醒");
            jiuzhentongzhiService.insert(notice);
            
            // 2. 立即发送预约成功通知
            sendNoticeRecord(appointment, notice.getId(), 1, "预约成功通知");
            
            // 3. 立即发送就诊前提醒 (模拟提前 1 天)
            sendNoticeRecord(appointment, notice.getId(), 2, "就诊前提醒");
            
            // 4. 立即发送就诊后随访通知 (模拟就诊后)
            sendNoticeRecord(appointment, notice.getId(), 3, "就诊后随访");
            
        } catch (Exception e) {
            e.printStackTrace();
            // 通知发送失败不阻断主流程
        }
    }
    
    /**
     * 发送单条通知记录
     * @param appointment 预约信息
     * @param noticeId 通知 ID
     * @param noticeType 通知类型 (1:预约成功，2:就诊前提醒，3:就诊后随访)
     * @param noticeTypeName 通知类型名称
     */
    private void sendNoticeRecord(YishengyuyueEntity appointment, Long noticeId, Integer noticeType, String noticeTypeName) {
        JiuzhentongzhiRecordEntity record = new JiuzhentongzhiRecordEntity<>();
        record.setNoticeId(noticeId);
        record.setAppointmentId(appointment.getId());
        record.setNoticeType(noticeType);
        record.setNoticeMethod(1); // 1:短信
        record.setReceiverAccount(appointment.getZhanghao());
        record.setReceiverPhone(appointment.getShouji());
        record.setCreateTime(new Date());
        record.setRetryCount(0);
        
        // 构建通知内容
        String content = "";
        if (noticeType == 1) {
            content = noticeSender.buildAppointmentSuccessContent(
                appointment.getZhanghao(), 
                appointment.getYishengzhanghao(), 
                appointment.getYuyueshijian()
            );
        } else if (noticeType == 2) {
            content = noticeSender.buildAppointmentReminderContent(
                appointment.getZhanghao(), 
                appointment.getYishengzhanghao(), 
                appointment.getYuyueshijian()
            );
        } else {
            content = "【医院挂号系统】尊敬的患者，您的就诊已完成。如有不适请及时复诊。";
        }
        record.setContent(content);
        
        // 尝试发送通知
        boolean success = noticeSender.sendSms(appointment.getShouji(), content);
        
        if (success) {
            record.setSendStatus(1); // 发送成功
            record.setSendTime(new Date());
        } else {
            record.setSendStatus(2); // 发送失败
            record.setFailReason("短信发送失败");
            record.setNextRetryTime(new Date(System.currentTimeMillis() + 5 * 60 * 1000)); // 5 分钟后重试
        }
        
        jiuzhentongzhiRecordService.insert(record);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<YishengyuyueEntity> wrapper) {
		  Page<YishengyuyueView> page =new Query<YishengyuyueView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<YishengyuyueView> selectListView(Wrapper<YishengyuyueEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public YishengyuyueView selectView(Wrapper<YishengyuyueEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}

	@Override
    public boolean insert(YishengyuyueEntity entity) {
        boolean result = super.insert(entity);
        if (result) {
            // 预约成功后立即发送所有后续提醒
            sendAppointmentNotices(entity);
        }
        return result;
    }
	
	

    @Override
    public List<Map<String, Object>> selectValue(Map<String, Object> params, Wrapper<YishengyuyueEntity> wrapper) {
        return baseMapper.selectValue(params, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectTimeStatValue(Map<String, Object> params, Wrapper<YishengyuyueEntity> wrapper) {
        return baseMapper.selectTimeStatValue(params, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectGroup(Map<String, Object> params, Wrapper<YishengyuyueEntity> wrapper) {
        return baseMapper.selectGroup(params, wrapper);
    }




}
