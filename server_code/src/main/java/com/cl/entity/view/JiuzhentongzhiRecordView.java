package com.cl.entity.view;

import com.cl.entity.JiuzhentongzhiRecordEntity;
import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * 就诊通知发送记录视图类
 */
@TableName("jiuzhentongzhi_record")
public class JiuzhentongzhiRecordView extends JiuzhentongzhiRecordEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public JiuzhentongzhiRecordView(){
    }
 
    public JiuzhentongzhiRecordView(JiuzhentongzhiRecordEntity jiuzhentongzhiRecordEntity){
        try {
            BeanUtils.copyProperties(this, jiuzhentongzhiRecordEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
