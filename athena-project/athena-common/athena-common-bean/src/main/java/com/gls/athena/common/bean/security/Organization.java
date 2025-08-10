package com.gls.athena.common.bean.security;

import com.gls.athena.common.bean.base.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织信息
 * <p>该类用于表示系统中的组织实体，包含组织的基本信息和层级关系</p>
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "组织信息", description = "组织信息")
public class Organization extends BaseVo implements IOrganization {

    /**
     * 组织名
     * <p>组织的显示名称</p>
     */
    @Schema(title = "组织名", description = "组织名")
    private String name;

    /**
     * 组织编码
     * <p>组织的唯一编码标识</p>
     */
    @Schema(title = "组织编码", description = "组织编码")
    private String code;

    /**
     * 组织描述
     * <p>对组织的详细描述信息</p>
     */
    @Schema(title = "组织描述", description = "组织描述")
    private String description;

    /**
     * 组织类型
     * <p>组织的分类类型标识</p>
     */
    @Schema(title = "组织类型", description = "组织类型")
    private String type;

    /**
     * 父组织ID
     * <p>指向父级组织的ID，用于构建组织树形结构</p>
     */
    @Schema(title = "父组织ID", description = "父组织ID")
    private Long parentId;

    /**
     * 排序
     * <p>组织在同一层级中的显示顺序</p>
     */
    @Schema(title = "排序", description = "排序")
    private Integer sort;

    /**
     * 默认组织 0否 1是
     * <p>标识该组织是否为默认组织</p>
     */
    @Schema(title = "默认组织", description = "默认组织")
    private Boolean defaultOrganization;
}

