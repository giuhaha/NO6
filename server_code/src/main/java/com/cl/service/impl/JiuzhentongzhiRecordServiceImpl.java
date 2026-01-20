package com.cl.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;


import com.cl.dao.JiuzhentongzhiRecordDao;
import com.cl.entity.JiuzhentongzhiRecordEntity;
import com.cl.service.JiuzhentongzhiRecordService;
import com.cl.entity.view.JiuzhentongzhiRecordView;

@Service("jiuzhentongzhiRecordService")
public class JiuzhentongzhiRecordServiceImpl extends ServiceImpl<JiuzhentongzhiRecordDao, JiuzhentongzhiRecordEntity> implements JiuzhentongzhiRecordService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<JiuzhentongzhiRecordEntity> page = this.selectPage(
                new Query<JiuzhentongzhiRecordEntity>(params).getPage(),
                new EntityWrapper<JiuzhentongzhiRecordEntity>()
        );
        return new PageUtils(page);
    }

    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<JiuzhentongzhiRecordEntity> wrapper) {
		  Page<JiuzhentongzhiRecordView> page =new Query<JiuzhentongzhiRecordView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
	}

	@Override
	public List<JiuzhentongzhiRecordView> selectListView(Wrapper<JiuzhentongzhiRecordEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public JiuzhentongzhiRecordView selectView(Wrapper<JiuzhentongzhiRecordEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}

}
