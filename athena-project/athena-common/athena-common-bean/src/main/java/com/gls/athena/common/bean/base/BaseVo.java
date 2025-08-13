package com.gls.athena.common.bean.base;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础值对象(Value Object)抽象类
 * <p>
 * 作为系统中所有VO类的基类，封装了通用的业务属性和元数据信息。
 * 该类遵循DDD(领域驱动设计)中的值对象概念，主要用于数据传输和展示。
 * </p>
 *
 * <p>包含的通用属性:</p>
 * <ul>
 *   <li>基础标识: ID、租户ID</li>
 *   <li>数据管理: 版本号、删除标记</li>
 *   <li>审计信息: 创建人、创建时间、更新人、更新时间</li>
 * </ul>
 *
 * @author george
 * @see IDomain
 * @since 1.0.0
 */
@Data
@Schema(title = "基础值对象", description = "系统中所有VO类的基类，包含通用属性和审计信息")
public abstract class BaseVo implements IDomain {

    /**
     * 主键ID
     * <p>记录的唯一标识符，通常为数据库自增主键</p>
     */
    @ExcelProperty(value = "ID")
    @Schema(title = "主键ID", description = "记录的唯一标识符", example = "1001")
    private Long id;

    /**
     * 租户ID
     * <p>多租户架构中用于数据隔离的租户标识符</p>
     */
    @ExcelProperty(value = "租户ID")
    @Schema(title = "租户ID", description = "多租户系统中的租户标识符，用于数据隔离", example = "100")
    private Long tenantId;

    /**
     * 版本号
     * <p>用于实现乐观锁机制，防止并发更新时的数据冲突</p>
     */
    @ExcelProperty(value = "版本号")
    @Schema(title = "版本号", description = "乐观锁版本号，每次更新时自动递增", example = "1")
    private Integer version;

    /**
     * 逻辑删除标记
     * <p>标识记录是否被逻辑删除：</p>
     * <ul>
     *   <li>{@code true} - 已删除（逻辑删除）</li>
     *   <li>{@code false} - 正常状态</li>
     * </ul>
     */
    @ExcelProperty(value = "删除标记")
    @Schema(title = "删除标记", description = "逻辑删除标记（true:已删除，false:正常）", example = "false")
    private Boolean deleted;

    /**
     * 创建人ID
     * <p>记录创建者的用户标识符</p>
     */
    @ExcelProperty(value = "创建人ID")
    @Schema(title = "创建人ID", description = "创建该记录的用户标识符", example = "1001")
    private Long createUserId;

    /**
     * 创建人姓名
     * <p>记录创建者的用户名称，用于显示和审计</p>
     */
    @ExcelProperty(value = "创建人姓名")
    @Schema(title = "创建人姓名", description = "创建该记录的用户姓名", example = "张三")
    private String createUserName;

    /**
     * 创建时间
     * <p>记录的创建时间戳</p>
     */
    @ExcelProperty(value = "创建时间")
    @Schema(title = "创建时间", description = "记录的创建时间", example = "2024-01-01 10:00:00")
    private LocalDateTime createTime;

    /**
     * 更新人ID
     * <p>最后一次更新记录的用户标识符</p>
     */
    @ExcelProperty(value = "更新人ID")
    @Schema(title = "更新人ID", description = "最后更新该记录的用户标识符", example = "1002")
    private Long updateUserId;

    /**
     * 更新人姓名
     * <p>最后一次更新记录的用户名称，用于显示和审计</p>
     */
    @ExcelProperty(value = "更新人姓名")
    @Schema(title = "更新人姓名", description = "最后更新该记录的用户姓名", example = "李四")
    private String updateUserName;

    /**
     * 更新时间
     * <p>记录的最后更新时间戳</p>
     */
    @ExcelProperty(value = "更新时间")
    @Schema(title = "更新时间", description = "记录的最后更新时间", example = "2024-01-02 15:30:00")
    private LocalDateTime updateTime;
}
