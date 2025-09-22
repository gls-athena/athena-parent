-- 异步任务表
drop table if exists t_async_task;
create table t_async_task
(
    id               bigint unsigned auto_increment                                         not null comment '主键id',
    task_id          varchar(50)      default ''                                            not null comment '任务id',
    code             varchar(50)      default ''                                            not null comment '任务编码',
    name             varchar(50)      default ''                                            not null comment '任务名称',
    description      varchar(255)     default ''                                            not null comment '任务描述',
    type             varchar(50)      default ''                                            not null comment '任务类型',
    status           tinyint unsigned default 0                                             not null comment '任务状态 0-待处理 1-处理中 2-已完成 3-已取消 4-处理失败 5-已取消',
    params           json                                                                   not null comment '任务参数',
    result           json                                                                   not null comment '任务结果',
    error_message    varchar(255)     default ''                                            not null comment '错误信息',
    progress         tinyint unsigned default 0                                             not null comment '任务进度',
    start_time       datetime         default current_timestamp                             not null comment '开始时间',
    end_time         datetime         default current_timestamp                             not null comment '结束时间',
    tenant_id        bigint unsigned  default 0                                             not null comment '租户id 0-默认租户',
    version          int unsigned     default 0                                             not null comment '版本号',
    is_delete        tinyint unsigned default 0                                             not null comment '删除标识 0-未删除 1-已删除',
    create_user_id   bigint unsigned  default 0                                             not null comment '创建用户id 0-系统',
    create_user_name varchar(50)      default 'system'                                      not null comment '创建用户名 system-系统',
    create_time      datetime         default current_timestamp                             not null comment '创建时间',
    update_user_id   bigint unsigned  default 0                                             not null comment '更新用户id 0-系统',
    update_user_name varchar(50)      default 'system'                                      not null comment '更新用户名 system-系统',
    update_time      datetime         default current_timestamp on update current_timestamp not null comment '更新时间',
    primary key (id),
    unique key uk_task_id (task_id)
) comment '异步任务表';