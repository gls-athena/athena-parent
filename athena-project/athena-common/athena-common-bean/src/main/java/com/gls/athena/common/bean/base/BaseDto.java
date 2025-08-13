package com.gls.athena.common.bean.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础数据传输对象(Data Transfer Object)抽象类
 * <p>
 * 作为系统中所有DTO类的基类，封装了通用的业务属性。
 * 该类主要用于系统间的数据传输，不包含业务逻辑。
 * </p>
 *
 * <p>包含的通用属性:</p>
 * <ul>
 *   <li>基础标识: ID、租户ID</li>
 *   <li>数据管理: 版本号、删除标记</li>
 *   <li>审计信息: 创建时间、更新时间</li>
 * </ul>
 *
 * @author george
 * @since 1.0.0
 */
@Data
@Schema(title = "基础数据传输对象", description = "系统中所有DTO类的基类，包含通用属性")
public abstract class BaseDto implements Serializable, IDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(title = "主键ID", description = "记录的唯一标识符", example = "1001")
    private Long id;

    /**
     * 租户ID
     */
    @Schema(title = "租户ID", description = "多租户系统中的租户标识符", example = "100")
    private Long tenantId;

    /**
     * 版本号
     */
    @Schema(title = "版本号", description = "乐观锁版本号", example = "1")
    private Integer version;

    /**
     * 逻辑删除标记
     */
    @Schema(title = "删除标记", description = "逻辑删除标记", example = "false")
    private Boolean deleted;

    /**
     * 创建时间
     */
    @Schema(title = "创建时间", description = "记录的创建时间", example = "2024-01-01 10:00:00")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(title = "更新时间", description = "记录的最后更新时间", example = "2024-01-01 10:00:00")
    private Date updateTime;
}
