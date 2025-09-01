package com.gls.athena.starter.data.jpa.base;

import com.gls.athena.common.bean.base.IDomain;
import com.gls.athena.starter.data.jpa.support.DefaultEntityListener;
import com.gls.athena.starter.data.jpa.support.SnowflakeId;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.util.Date;

/**
 * JPA实体基类
 * <p>
 * 提供实体类通用的基础字段，包括：
 * - ID: 主键标识
 * - 租户信息: 支持多租户
 * - 版本控制: 乐观锁
 * - 数据追踪: 创建和更新信息
 * - 逻辑删除: 软删除标记
 * </p>
 *
 * @author george
 */
@Data
@MappedSuperclass
@EntityListeners({DefaultEntityListener.class})
public abstract class BaseEntity implements IDomain {

    /**
     * ID主键, 使用雪花算法生成
     */
    @Id
    @SnowflakeId
    @Comment("主键ID")
    private Long id;

    /**
     * 租户ID, 用于多租户数据隔离
     */
    @Comment("租户标识")
    private Long tenantId;

    /**
     * 版本号, 用于乐观锁控制
     */
    @Version
    @Comment("版本号(乐观锁)")
    private Integer version;

    /**
     * 删除标记(false:正常;true:已删除)
     */
    @Comment("删除标记(false:正常;true:已删除)")
    private Boolean isDelete;

    /**
     * 创建人ID
     */
    @Comment("创建人ID")
    private Long createUserId;

    /**
     * 创建人姓名
     */
    @Comment("创建人姓名")
    private String createUserName;

    /**
     * 创建时间
     */
    @Comment("创建时间")
    private Date createTime;

    /**
     * 最后修改人ID
     */
    @Comment("最后修改人ID")
    private Long updateUserId;

    /**
     * 最后修改人姓名
     */
    @Comment("最后修改人姓名")
    private String updateUserName;

    /**
     * 最后修改时间
     */
    @Comment("最后修改时间")
    private Date updateTime;
}
