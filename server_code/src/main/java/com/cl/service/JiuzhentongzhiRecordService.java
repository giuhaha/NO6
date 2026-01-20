package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.utils.PageUtils;
import com.cl.entity.JiuzhentongzhiRecordEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.cl.entity.view.JiuzhentongzhiRecordView;


/**
 * 就诊通知发送记录
 *
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public interface JiuzhentongzhiRecordService extends IService<JiuzhentongzhiRecordEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<JiuzhentongzhiRecordView> selectListView(Wrapper<JiuzhentongzhiRecordEntity> wrapper);
   	
   	JiuzhentongzhiRecordView selectView(@Param("ew") Wrapper<JiuzhentongzhiRecordEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<JiuzhentongzhiRecordEntity> wrapper);
}
