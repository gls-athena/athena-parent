package com.gls.athena.starter.mybatis.base;

import com.baomidou.mybatisplus.annotation.*;
import com.gls.athena.common.bean.base.IDomain;
import lombok.Data;

import java.util.Date;

/**
 * 基础实体类，包含ID、租户ID、版本号等通用字段。
 *
 * @author george
 */
@Data
public abstract class BaseEntity implements IDomain {

    public static final String COL_ID = "id";
    public static final String COL_TENANT_ID = "tenant_id";
    public static final String COL_VERSION = "version";
    public static final String COL_DELETED = "deleted";
    public static final String COL_CREATE_USER_ID = "create_user_id";
    public static final String COL_CREATE_USER_NAME = "create_user_name";
    public static final String COL_CREATE_TIME = "create_time";
    public static final String COL_UPDATE_USER_ID = "update_user_id";
    public static final String COL_UPDATE_USER_NAME = "update_user_name";
    public static final String COL_UPDATE_TIME = "update_time";

    /**
     * 主键ID，使用雪花算法生成。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户ID，用于多租户场景。
     */
    private Long tenantId;

    /**
     * 版本号，用于乐观锁控制。
     */
    @Version
    private Integer version;

    /**
     * 删除标记：0-正常，1-已删除。
     */
    private Boolean deleted;

    /**
     * 创建人ID，自动填充。
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    /**
     * 创建人姓名，自动填充。
     */
    @TableField(fill = FieldFill.INSERT)
    private String createUserName;

    /**
     * 创建时间，不允许插入和更新时手动设置。
     */
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Date createTime;

    /**
     * 修改人ID，自动填充。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;

    /**
     * 修改人姓名，自动填充。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateUserName;

    /**
     * 更新时间，不允许插入和更新时手动设置。
     */
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Date updateTime;
}
