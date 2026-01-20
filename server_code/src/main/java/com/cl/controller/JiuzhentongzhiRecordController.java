package com.cl.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import com.cl.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.annotation.SysLog;

import com.cl.entity.JiuzhentongzhiRecordEntity;
import com.cl.entity.view.JiuzhentongzhiRecordView;

import com.cl.service.JiuzhentongzhiRecordService;
import com.cl.service.TokenService;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import com.cl.utils.MPUtil;
import com.cl.utils.MapUtils;
import com.cl.utils.CommonUtil;

/**
 * 就诊通知发送记录
 * 后端接口
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
@RestController
@RequestMapping("/jiuzhentongzhiRecord")
public class JiuzhentongzhiRecordController {
    @Autowired
    private JiuzhentongzhiRecordService jiuzhentongzhiRecordService;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,JiuzhentongzhiRecordEntity jiuzhentongzhiRecord,
                                                                                                                                                    HttpServletRequest request){
                                    EntityWrapper<JiuzhentongzhiRecordEntity> ew = new EntityWrapper<JiuzhentongzhiRecordEntity>();
                                                                                                                                                                                                                                
        
        
        
        PageUtils page = jiuzhentongzhiRecordService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jiuzhentongzhiRecord), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,JiuzhentongzhiRecordEntity jiuzhentongzhiRecord,
		HttpServletRequest request){
        EntityWrapper<JiuzhentongzhiRecordEntity> ew = new EntityWrapper<JiuzhentongzhiRecordEntity>();

		PageUtils page = jiuzhentongzhiRecordService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jiuzhentongzhiRecord), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( JiuzhentongzhiRecordEntity jiuzhentongzhiRecord){
       	EntityWrapper<JiuzhentongzhiRecordEntity> ew = new EntityWrapper<JiuzhentongzhiRecordEntity>();
      	ew.allEq(MPUtil.allEQMapPre( jiuzhentongzhiRecord, "jiuzhentongzhiRecord")); 
        return R.ok().put("data", jiuzhentongzhiRecordService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(JiuzhentongzhiRecordEntity jiuzhentongzhiRecord){
        EntityWrapper< JiuzhentongzhiRecordEntity> ew = new EntityWrapper< JiuzhentongzhiRecordEntity>();
 		ew.allEq(MPUtil.allEQMapPre( jiuzhentongzhiRecord, "jiuzhentongzhiRecord")); 
		JiuzhentongzhiRecordView jiuzhentongzhiRecordView =  jiuzhentongzhiRecordService.selectView(ew);
		return R.ok("查询就诊通知发送记录成功").put("data", jiuzhentongzhiRecordView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        JiuzhentongzhiRecordEntity jiuzhentongzhiRecord = jiuzhentongzhiRecordService.selectById(id);
		jiuzhentongzhiRecord = jiuzhentongzhiRecordService.selectView(new EntityWrapper<JiuzhentongzhiRecordEntity>().eq("id", id));
        return R.ok().put("data", jiuzhentongzhiRecord);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        JiuzhentongzhiRecordEntity jiuzhentongzhiRecord = jiuzhentongzhiRecordService.selectById(id);
		jiuzhentongzhiRecord = jiuzhentongzhiRecordService.selectView(new EntityWrapper<JiuzhentongzhiRecordEntity>().eq("id", id));
        return R.ok().put("data", jiuzhentongzhiRecord);
    }
    
    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    @SysLog("修改就诊通知发送记录")
    public R update(@RequestBody JiuzhentongzhiRecordEntity jiuzhentongzhiRecord, HttpServletRequest request){
        //ValidatorUtils.validateEntity(jiuzhentongzhiRecord);
        jiuzhentongzhiRecordService.updateById(jiuzhentongzhiRecord);//全部更新
        return R.ok();
    }

    /**
     * 手动重试发送失败的通知
     */
    @RequestMapping("/retry/{id}")
    @Transactional
    @SysLog("重试发送就诊通知")
    public R retry(@PathVariable("id") Long id){
        try {
            JiuzhentongzhiRecordEntity record = jiuzhentongzhiRecordService.selectById(id);
            if (record == null) {
                return R.error("记录不存在");
            }
            
            // 增加重试次数
            record.setRetryCount(record.getRetryCount() + 1);
            
            // 重新发送
            com.cl.utils.NoticeSender noticeSender = new com.cl.utils.NoticeSender();
            boolean success = noticeSender.sendSms(record.getReceiverPhone(), record.getContent());
            
            if (success) {
                record.setSendStatus(1);
                record.setSendTime(new Date());
                record.setFailReason(null);
                record.setNextRetryTime(null);
                jiuzhentongzhiRecordService.updateById(record);
                return R.ok("重发成功");
            } else {
                record.setSendStatus(2);
                record.setFailReason("第" + record.getRetryCount() + "次手动重试失败");
                long retryInterval = 5 * 60 * 1000;
                record.setNextRetryTime(new Date(System.currentTimeMillis() + retryInterval));
                jiuzhentongzhiRecordService.updateById(record);
                return R.error("重发失败，已记录待下次自动重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("重发异常：" + e.getMessage());
        }
    }
    
    /**
     * 删除
     */
    @RequestMapping("/delete")
    @SysLog("删除就诊通知发送记录")
    public R delete(@RequestBody Long[] ids){
        jiuzhentongzhiRecordService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
}
