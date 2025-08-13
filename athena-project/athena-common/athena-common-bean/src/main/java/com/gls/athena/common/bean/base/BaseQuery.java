package com.gls.athena.common.bean.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 基础查询条件对象
 * <p>
 * 作为系统中所有查询条件类的基类，封装了通用的查询条件。
 * 支持常见的查询场景，如时间范围查询、租户过滤等。
 * </p>
 *
 * @author george
 * @since 1.0.0
 */
@Data
@Schema(title = "基础查询条件", description = "系统中所有查询条件类的基类")
public abstract class BaseQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID列表查询
     */
    @Schema(title = "ID列表", description = "根据ID列表查询", example = "[1001, 1002, 1003]")
    private List<Long> ids;

    /**
     * 租户ID
     */
    @Schema(title = "租户ID", description = "租户标识符", example = "100")
    private Long tenantId;

    /**
     * 租户ID列表
     */
    @Schema(title = "租户ID列表", description = "多租户查询", example = "[100, 101, 102]")
    private List<Long> tenantIds;

    /**
     * 是否包含已删除数据
     */
    @Schema(title = "包含已删除", description = "是否包含逻辑删除的数据", example = "false")
    private Boolean includeDeleted = false;

    /**
     * 创建时间开始
     */
    @Schema(title = "创建时间开始", description = "创建时间范围查询-开始时间", example = "2024-01-01 00:00:00")
    private Date createTimeStart;

    /**
     * 创建时间结束
     */
    @Schema(title = "创建时间结束", description = "创建时间范围查询-结束时间", example = "2024-01-31 23:59:59")
    private Date createTimeEnd;

    /**
     * 更新时间开始
     */
    @Schema(title = "更新时间开始", description = "更新时间范围查询-开始时间", example = "2024-01-01 00:00:00")
    private Date updateTimeStart;

    /**
     * 更新时间结束
     */
    @Schema(title = "更新时间结束", description = "更新时间范围查询-结束时间", example = "2024-01-31 23:59:59")
    private Date updateTimeEnd;

    /**
     * 关键字搜索
     */
    @Schema(title = "关键字", description = "模糊搜索关键字", example = "张三")
    private String keyword;

    /**
     * 判断是否有ID列表查询条件
     */
    public boolean hasIds() {
        return ids != null && !ids.isEmpty();
    }

    /**
     * 判断是否有租户ID查询条件
     */
    public boolean hasTenantId() {
        return tenantId != null;
    }

    /**
     * 判断是否有多租户查询条件
     */
    public boolean hasTenantIds() {
        return tenantIds != null && !tenantIds.isEmpty();
    }

    /**
     * 判断是否有创建时间范围查询
     */
    public boolean hasCreateTimeRange() {
        return createTimeStart != null || createTimeEnd != null;
    }

    /**
     * 判断是否有更新时间范围查询
     */
    public boolean hasUpdateTimeRange() {
        return updateTimeStart != null || updateTimeEnd != null;
    }

    /**
     * 判断是否有关键字搜索
     */
    public boolean hasKeyword() {
        return keyword != null && !keyword.trim().isEmpty();
    }
}
