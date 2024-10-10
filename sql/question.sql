
-- 题目表
create table if not exists question
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(256)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    answer     text                               null comment '推荐答案',
    userId     bigint                             not null comment '创建用户 id',
    reviewStatus  int      default 0  not null comment '状态：0-待审核, 1-通过, 2-拒绝',
    reviewMessage varchar(512)        null comment '审核信息',
    reviewerId    bigint              null comment '审核人 id',
    reviewTime    datetime            null comment '审核时间',
    needVip  tinyint  default 0  not null comment '仅会员可见（1 表示仅会员可见）',
    difficult  tinyint  default 0  not null comment '题目难度程度（0简单，中等，困难）',
    viewNum       int      default 0    not null comment '浏览量',
    thumbNum      int      default 0    not null comment '点赞数',
    favourNum     int      default 0    not null comment '收藏数',
    editTime   datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_title (title),
    index idx_userId (userId)
) comment '题目' collate = utf8mb4_unicode_ci;