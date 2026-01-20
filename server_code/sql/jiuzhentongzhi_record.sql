-- 就诊通知发送记录表
DROP TABLE IF EXISTS `jiuzhentongzhi_record`;
CREATE TABLE `jiuzhentongzhi_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `notice_id` bigint(20) DEFAULT NULL COMMENT '通知 ID',
  `appointment_id` bigint(20) DEFAULT NULL COMMENT '预约 ID',
  `notice_type` int(11) DEFAULT NULL COMMENT '通知类型 (1:预约成功通知，2:就诊前提醒，3:就诊后随访)',
  `notice_method` int(11) DEFAULT NULL COMMENT '通知方式 (1:短信，2:站内信，3:微信推送)',
  `receiver_account` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收人账号',
  `receiver_phone` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收人手机',
  `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '通知内容',
  `send_status` int(11) DEFAULT NULL COMMENT '发送状态 (0:待发送，1:发送成功，2:发送失败)',
  `fail_reason` longtext COLLATE utf8mb4_unicode_ci COMMENT '失败原因',
  `retry_count` int(11) DEFAULT '0' COMMENT '重试次数',
  `next_retry_time` datetime DEFAULT NULL COMMENT '下次重试时间',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='就诊通知发送记录';
