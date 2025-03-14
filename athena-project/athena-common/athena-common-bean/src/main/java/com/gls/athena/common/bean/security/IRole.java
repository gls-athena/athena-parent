package com.gls.athena.common.bean.security;

import com.gls.athena.common.bean.base.IDomain;
import com.gls.athena.common.bean.base.ITreeNode;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * 角色信息
 *
 * @param <P> 权限类型
 * @author george
 */
public interface IRole<P extends IPermission> extends GrantedAuthority, ITreeNode, IDomain {
    /**
     * 获取角色编码
     * <p>
     * 该方法实现了 `getAuthority` 接口方法，返回当前角色的编码。
     * 通常用于权限管理系统中，标识角色的唯一编码。
     *
     * @return 角色编码，类型为字符串
     */
    @Override
    default String getAuthority() {
        return this.getCode();
    }

    /**
     * 是否默认角色
     *
     * @return 是否默认角色
     */
    Boolean getDefaultRole();

    /**
     * 获取权限列表
     *
     * @return 权限列表
     */
    List<P> getPermissions();

    /**
     * 设置权限列表
     *
     * @param permissions 权限列表
     */
    void setPermissions(List<P> permissions);
}
