create database if not exists yachiyo_chat;
use yachiyo_chat;
create table spring_ai_chat_memory
(
    conversation_id varchar(36)                                  not null,
    content         text                                         not null,
    type            enum ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL') not null,
    timestamp       timestamp                                    not null
);

create index SPRING_AI_CHAT_MEMORY_CONVERSATION_ID_TIMESTAMP_IDX
    on spring_ai_chat_memory (conversation_id, timestamp);

create table users
(
    user_id  int auto_increment comment '用户 id'
        primary key,
    name     varchar(15)                       default 'NoName'                not null comment '用户名',
    password varchar(32)                       default (md5(_utf8mb4'123456')) not null comment '用户密码',
    role     set ('normal', 'member', 'admin') default 'normal'                not null comment '角色'
)
    comment '用户表';

create table llm_conversations
(
    conversation_id int auto_increment comment '会话 id'
        primary key,
    user_id         int                         not null comment '用户 id',
    title           varchar(50) default (now()) not null comment '标题',
    create_time     datetime    default (now()) not null comment '创建时间',
    update_time     datetime    default (now()) not null comment '更新时间',
    constraint llm_conversations_users_user_id_fk
        foreign key (user_id) references users (user_id)
            on delete cascade
)
    comment '会话表';

create index llm_conversations_conversation_id_create_time_index
    on llm_conversations (conversation_id, create_time);

create index llm_conversations_conversation_id_update_time_index
    on llm_conversations (conversation_id, update_time);

create index llm_conversations_conversation_id_user_id_index
    on llm_conversations (conversation_id, user_id);

create table user_detail
(
    id           int                          not null comment '用户id'
        primary key,
    name         varchar(45) default 'noname' not null,
    city         varchar(8)                   null comment '城市',
    gender       set ('男', '女')             null comment '性别',
    birthday     date                         null comment '生日',
    introduction text                         null comment '简介',
    constraint `user-detail_users_user_id_fk`
        foreign key (id) references users (user_id)
)
    comment '用户表详情';

