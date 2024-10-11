-- 题目点赞表（硬删除）
create table if not exists question_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    questionId bigint                             not null comment '题目 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    UNIQUE INDEX userId_questionId(userId,questionId)
) comment '题目点赞';
