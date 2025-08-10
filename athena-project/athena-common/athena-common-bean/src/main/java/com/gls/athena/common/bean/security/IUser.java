package com.gls.athena.common.bean.security;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.gls.athena.common.bean.base.IDomain;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 用户接口，定义了用户的基本信息和安全相关的方法。
 * 该接口继承自 Spring Security 的 UserDetails 接口和系统基础领域模型接口 IDomain。
 *
 * @param <R> 角色类型，必须实现 IRole 接口
 * @param <P> 权限类型，必须实现 IPermission 接口
 * @param <O> 组织机构类型，必须实现 IOrganization 接口
 * @author george
 */
public interface IUser<R extends IRole<P>, P extends IPermission, O extends IOrganization> extends UserDetails, IDomain {
    /**
     * 获取用户的手机号码。
     *
     * @return 手机号码字符串
     */
    String getMobile();

    /**
     * 获取用户的电子邮箱地址。
     *
     * @return 邮箱地址字符串
     */
    String getEmail();

    /**
     * 获取用户的真实姓名。
     *
     * @return 真实姓名字符串
     */
    String getRealName();

    /**
     * 获取用户的昵称。
     *
     * @return 昵称字符串
     */
    String getNickName();

    /**
     * 获取用户头像的URL或标识符。
     *
     * @return 头像信息字符串
     */
    String getAvatar();

    /**
     * 获取用户设置的语言偏好（如 zh-CN、en-US）。
     *
     * @return 语言代码字符串
     */
    String getLanguage();

    /**
     * 获取用户所在的区域信息（如国家/地区编码）。
     *
     * @return 区域编码字符串
     */
    String getLocale();

    /**
     * 获取用户所在时区的信息（如 GMT+8）。
     *
     * @return 时区字符串
     */
    String getTimeZone();

    /**
     * 获取当前默认角色。
     * 该方法首先检查当前角色列表是否为空，如果为空则返回null。
     * 如果角色列表不为空，则遍历角色列表，查找并返回第一个标记为默认角色的角色对象。
     * 如果没有找到默认角色，则返回null。
     *
     * @return 默认角色对象，如果不存在则返回null
     */
    default R getRole() {
        // 检查角色列表是否为空
        if (CollUtil.isEmpty(this.getRoles())) {
            return null;
        }
        // 遍历角色列表，查找并返回第一个默认角色
        return this.getRoles().stream()
                .filter(ObjUtil::isNotNull)
                .filter(IRole::getDefaultRole)
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取当前默认的组织机构。
     * 该方法首先检查组织机构列表是否为空，如果为空则返回null。
     * 否则，从组织机构列表中筛选出默认的组织机构并返回。
     *
     * @return 默认的组织机构，如果不存在则返回null
     */
    default O getOrganization() {
        // 检查组织机构列表是否为空
        if (CollUtil.isEmpty(this.getOrganizations())) {
            return null;
        }
        // 从组织机构列表中筛选出默认的组织机构并返回
        return this.getOrganizations().stream()
                .filter(ObjUtil::isNotNull)
                .filter(IOrganization::getDefaultOrganization)
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取用户拥有的所有角色列表。
     *
     * @return 角色列表
     */
    List<R> getRoles();

    /**
     * 获取用户所属的所有组织机构列表。
     *
     * @return 组织机构列表
     */
    List<O> getOrganizations();

    /**
     * 获取当前用户的权限列表。
     * 该方法是接口的默认实现，返回当前用户所拥有的角色列表。
     * 角色列表中的每个角色都实现了 {@link IRole} 接口，并且与权限 {@link P} 相关联。
     *
     * @return 返回一个包含 {@link IRole} 对象的集合，表示当前用户的所有角色。
     */
    @Override
    default Collection<? extends IRole<P>> getAuthorities() {
        return this.getRoles();
    }

}
