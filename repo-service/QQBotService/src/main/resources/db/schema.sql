-- 群聊表
CREATE TABLE IF NOT EXISTS chat_groups (
    group_id BIGINT PRIMARY KEY COMMENT 'QQ群号',
    join_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '机器人入群时间'
);

-- 消息表
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    group_id BIGINT NOT NULL COMMENT '群号（外键关联 groups.group_id）',
    message_id BIGINT NOT NULL COMMENT 'NapCat 消息ID',
    sender_id BIGINT NOT NULL COMMENT '发送者QQ号',
    plain_text TEXT COMMENT '纯文本内容（不含CQ码）',
    raw_message TEXT COMMENT '原始消息（含CQ码）',

    by_self BOOLEAN DEFAULT FALSE COMMENT '是否机器人自己发送的',
    is_recalled BOOLEAN DEFAULT FALSE COMMENT '是否已被撤回',
    send_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',

    cq_at_list JSON DEFAULT NULL COMMENT 'at列表',
    cq_file_list JSON DEFAULT NULL COMMENT 'minio的url列表',
    
    -- 外键约束
    CONSTRAINT fk_messages_group 
        FOREIGN KEY (group_id) 
        REFERENCES chat_groups(group_id)
        ON DELETE CASCADE 
        ON UPDATE CASCADE,

    -- 确保同一群内的消息ID唯一
    UNIQUE KEY uk_group_message (group_id, message_id),
    
    INDEX idx_group_id (group_id),
    INDEX idx_group_time (group_id, send_time),
    INDEX idx_message_id (message_id),
    INDEX idx_send_time (send_time)
);