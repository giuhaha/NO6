package com.cl.dao;

import com.cl.entity.JiuzhentongzhiRecordEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.cl.entity.view.JiuzhentongzhiRecordView;


/**
 * 就诊通知发送记录
 * 
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public interface JiuzhentongzhiRecordDao extends BaseMapper<JiuzhentongzhiRecordEntity> {
	
	List<JiuzhentongzhiRecordView> selectListView(@Param("ew") Wrapper<JiuzhentongzhiRecordEntity> wrapper);

	List<JiuzhentongzhiRecordView> selectListView(Pagination page,@Param("ew") Wrapper<JiuzhentongzhiRecordEntity> wrapper);
	
	JiuzhentongzhiRecordView selectView(@Param("ew") Wrapper<JiuzhentongzhiRecordEntity> wrapper);

}
