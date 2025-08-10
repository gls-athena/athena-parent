package com.gls.athena.common.bean.security;

import com.gls.athena.common.bean.base.IDomain;
import com.gls.athena.common.bean.base.ITreeNode;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * 角色信息接口
 * <p>
 * 该接口定义了角色的基本信息和权限管理功能，继承了Spring Security的GrantedAuthority接口、
 * 树节点接口ITreeNode和领域模型接口IDomain。角色可以包含多个权限，并支持树形结构组织。
 *
 * @param <P> 权限类型，必须实现IPermission接口
 * @author george
 */
public interface IRole<P extends IPermission> extends GrantedAuthority, ITreeNode, IDomain {
    /**
     * 获取角色编码
     * <p>
     * 该方法实现了Spring Security中GrantedAuthority接口的getAuthority方法，
     * 返回当前角色的唯一编码标识。此编码通常用于权限验证和访问控制。
     *
     * @return 角色编码字符串，用于标识角色的唯一性
     */
    @Override
    default String getAuthority() {
        return this.getCode();
    }

    /**
     * 判断是否为默认角色
     * <p>
     * 该方法用于判断当前角色是否为系统默认分配的角色。
     * 默认角色通常在用户注册或初始化时自动分配。
     *
     * @return Boolean类型，true表示是默认角色，false表示不是默认角色
     */
    Boolean getDefaultRole();

    /**
     * 获取角色关联的权限列表
     * <p>
     * 返回当前角色所拥有的所有权限集合，权限类型由泛型参数P指定。
     *
     * @return 权限列表，包含当前角色的所有权限信息
     */
    List<P> getPermissions();

    /**
     * 设置角色的权限列表
     * <p>
     * 用于为当前角色分配权限集合，替换原有的权限配置。
     *
     * @param permissions 权限列表，类型为P的泛型列表，不能为空
     */
    void setPermissions(List<P> permissions);
}

