package com.gls.athena.common.core.security;

import com.gls.athena.common.core.base.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限信息
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "权限信息", description = "权限信息")
public class Permission extends BaseVo implements IPermission {

    /**
     * 权限名
     */
    @Schema(title = "权限名", description = "权限名")
    private String name;
    /**
     * 权限编码
     */
    @Schema(title = "权限编码", description = "权限编码")
    private String code;
    /**
     * 权限描述
     */
    @Schema(title = "权限描述", description = "权限描述")
    private String description;
    /**
     * 权限类型
     */
    @Schema(title = "权限类型", description = "权限类型")
    private String type;
    /**
     * 父权限ID
     */
    @Schema(title = "父权限ID", description = "父权限ID")
    private Long parentId;
    /**
     * 排序
     */
    @Schema(title = "排序", description = "排序")
    private Integer sort;
    /**
     * 资源指令
     */
    @Schema(title = "资源指令", description = "资源指令")
    private String command;
}
