package com.gls.athena.common.bean.base;

import com.alibaba.excel.annotation.ExcelProperty;
import com.gls.athena.common.bean.excel.DeletedConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 基础值对象(VO)类
 * 包含了系统中实体对象的通用属性，如主键、租户ID、创建和更新信息等
 *
 * @author george
 */
@Data
@Schema(title = "基础值对象", description = "包含系统中实体对象的通用属性")
public abstract class BaseVo implements IDomain {
    /**
     * 记录的唯一标识
     */
    @ExcelProperty(value = "ID")
    @Schema(title = "ID", description = "记录的唯一标识")
    private Long id;
    /**
     * 多租户系统中的租户标识
     */
    @ExcelProperty(value = "租户ID")
    @Schema(title = "租户ID", description = "多租户系统中的租户标识")
    private Long tenantId;
    /**
     * 数据版本号，用于乐观锁控制
     */
    @ExcelProperty(value = "版本号")
    @Schema(title = "版本号", description = "用于乐观锁控制的数据版本号")
    private Integer version;
    /**
     * 逻辑删除标记
     * true: 已删除
     * false: 正常
     */
    @ExcelProperty(value = "删除标记", converter = DeletedConverter.class)
    @Schema(title = "删除标记", description = "逻辑删除标记（true:已删除 false:正常）")
    private Boolean deleted;
    /**
     * 创建者的用户ID
     */
    @ExcelProperty(value = "创建人ID")
    @Schema(title = "创建人ID", description = "创建该记录的用户ID")
    private Long createUserId;
    /**
     * 创建者的用户名称
     */
    @ExcelProperty(value = "创建人姓名")
    @Schema(title = "创建人姓名", description = "创建该记录的用户名称")
    private String createUserName;
    /**
     * 记录的创建时间
     */
    @ExcelProperty(value = "创建时间")
    @Schema(title = "创建时间", description = "记录的创建时间")
    private Date createTime;
    /**
     * 最后更新者的用户ID
     */
    @ExcelProperty(value = "更新人ID")
    @Schema(title = "更新人ID", description = "最后更新该记录的用户ID")
    private Long updateUserId;
    /**
     * 最后更新者的用户名称
     */
    @ExcelProperty(value = "更新人姓名")
    @Schema(title = "更新人姓名", description = "最后更新该记录的用户名称")
    private String updateUserName;
    /**
     * 记录的最后更新时间
     */
    @ExcelProperty(value = "更新时间")
    @Schema(title = "更新时间", description = "记录的最后更新时间")
    private Date updateTime;
}
