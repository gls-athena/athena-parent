package com.gls.athena.common.bean.security;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import com.gls.athena.common.bean.base.ITreeNodeParser;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

/**
 * 登录用户工具类
 * <p>提供获取当前登录用户信息的便捷方法</p>
 *
 * @author george
 */
@UtilityClass
public class LoginUserHelper {

    /**
     * 将OAuth2认证主体转换为用户对象
     *
     * @param oauth2Principal OAuth2认证主体
     * @return 用户对象
     */
    public User toUser(OAuth2AuthenticatedPrincipal oauth2Principal) {
        return BeanUtil.fillBeanWithMap(oauth2Principal.getAttributes(), new User(), true);
    }

    /**
     * 获取当前登录用户
     * <p>支持SocialUser、User和OAuth2AuthenticatedPrincipal三种类型</p>
     *
     * @return 当前用户，如果未登录则返回空
     */
    public Optional<? extends IUser<?, ?, ?>> getCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(principal -> switch (principal) {
                    case SocialUser socialUser -> socialUser.getUser();
                    case User user -> user;
                    case OAuth2AuthenticatedPrincipal oauth2Principal -> toUser(oauth2Principal);
                    default -> null;
                });
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID，如果未登录则返回空
     */
    public Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(IUser::getId);
    }

    /**
     * 获取当前用户的租户ID
     *
     * @return 租户ID，如果未登录则返回空
     */
    public Optional<Long> getCurrentUserTenantId() {
        return getCurrentUser().map(IUser::getTenantId);
    }

    /**
     * 获取当前用户名
     *
     * @return 用户名，如果未登录则返回空
     */
    public Optional<String> getCurrentUsername() {
        return getCurrentUser().map(IUser::getUsername);
    }

    /**
     * 获取当前用户手机号
     *
     * @return 手机号，如果未登录则返回空
     */
    public Optional<String> getCurrentUserMobile() {
        return getCurrentUser().map(IUser::getMobile);
    }

    /**
     * 获取当前用户邮箱
     *
     * @return 邮箱地址，如果未登录则返回空
     */
    public Optional<String> getCurrentUserEmail() {
        return getCurrentUser().map(IUser::getEmail);
    }

    /**
     * 获取当前用户真实姓名
     *
     * @return 真实姓名，如果未登录则返回空
     */
    public Optional<String> getCurrentUserRealName() {
        return getCurrentUser().map(IUser::getRealName);
    }

    /**
     * 获取当前用户昵称
     *
     * @return 昵称，如果未登录则返回空
     */
    public Optional<String> getCurrentUserNickName() {
        return getCurrentUser().map(IUser::getNickName);
    }

    /**
     * 获取当前用户头像URL
     *
     * @return 头像URL，如果未登录则返回空
     */
    public Optional<String> getCurrentUserAvatar() {
        return getCurrentUser().map(IUser::getAvatar);
    }

    /**
     * 获取当前用户语言设置
     *
     * @return 语言设置，如果未登录则返回空
     */
    public Optional<String> getCurrentUserLanguage() {
        return getCurrentUser().map(IUser::getLanguage);
    }

    /**
     * 获取当前用户区域设置
     *
     * @return 区域设置，如果未登录则返回空
     */
    public Optional<Locale> getCurrentUserLocale() {
        return getCurrentUser().map(IUser::getLocale).map(Locale::forLanguageTag);
    }

    /**
     * 获取当前用户时区
     *
     * @return 时区信息，如果未登录则返回空
     */
    public Optional<TimeZone> getCurrentUserTimeZone() {
        return getCurrentUser().map(IUser::getTimeZone).map(TimeZone::getTimeZone);
    }

    /**
     * 获取当前用户的角色信息
     *
     * @return 角色信息，如果未登录则返回空
     */
    public Optional<? extends IRole<?>> getCurrentUserRole() {
        return getCurrentUser().map(IUser::getRole);
    }

    /**
     * 获取当前用户的组织机构信息
     *
     * @return 组织机构信息，如果未登录则返回空
     */
    public Optional<? extends IOrganization> getCurrentUserOrganization() {
        return getCurrentUser().map(IUser::getOrganization);
    }

    /**
     * 获取当前用户的角色列表
     *
     * @return 角色列表，如果未登录则返回空
     */
    public Optional<List<? extends IRole<?>>> getCurrentUserRoles() {
        return getCurrentUser().map(IUser::getRoles);
    }

    /**
     * 获取当前用户所属的组织机构列表
     *
     * @return 组织机构列表，如果未登录则返回空
     */
    public Optional<List<? extends IOrganization>> getCurrentUserOrganizations() {
        return getCurrentUser().map(IUser::getOrganizations);
    }

    /**
     * 获取当前用户的权限列表
     *
     * @return 权限列表，如果未登录则返回空
     */
    public Optional<List<? extends IPermission>> getCurrentUserPermissions() {
        return getCurrentUser()
                .map(IUser::getRoles)
                .map(roles -> roles.stream()
                        .map(IRole::getPermissions)
                        .flatMap(List::stream)
                        .distinct().toList());
    }

    /**
     * 获取当前用户的角色树结构
     *
     * @return 角色树结构，如果未登录则返回空
     */
    public Optional<List<Tree<Long>>> getCurrentUserRoleTree() {
        return getCurrentUserRoles()
                .map(roles -> TreeUtil.build(roles, 0L, new ITreeNodeParser<>()));
    }

    /**
     * 获取当前用户组织机构树
     *
     * @return 组织机构树，如果未登录则返回空
     */
    public Optional<List<Tree<Long>>> getCurrentUserOrganizationTree() {
        return getCurrentUserOrganizations()
                .map(organizations -> TreeUtil.build(organizations, 0L, new ITreeNodeParser<>()));
    }

    /**
     * 获取当前用户的权限树结构
     *
     * @return 权限树结构，如果未登录则返回空
     */
    public Optional<List<Tree<Long>>> getCurrentUserPermissionTree() {
        return getCurrentUserPermissions()
                .map(permissions -> TreeUtil.build(permissions, 0L, new ITreeNodeParser<>()));
    }

}
