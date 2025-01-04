package com.gls.athena.common.bean.security;

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
 * 用户帮助类
 *
 * @author george
 */
@UtilityClass
public class LoginUserHelper {

    /**
     * 转换为用户
     *
     * @param oauth2Principal OAuth2认证主体
     * @return 用户
     */
    public User toUser(OAuth2AuthenticatedPrincipal oauth2Principal) {
        User user = new User();
        user.setUsername(oauth2Principal.getName());
        user.setPassword(oauth2Principal.getAttribute("password"));
        user.setMobile(oauth2Principal.getAttribute("mobile"));
        user.setEmail(oauth2Principal.getAttribute("email"));
        user.setRealName(oauth2Principal.getAttribute("realName"));
        user.setNickName(oauth2Principal.getAttribute("nickName"));
        user.setAvatar(oauth2Principal.getAttribute("avatar"));
        user.setLanguage(oauth2Principal.getAttribute("language"));
        user.setLocale(oauth2Principal.getAttribute("locale"));
        user.setTimeZone(oauth2Principal.getAttribute("timeZone"));
        user.setRoles(oauth2Principal.getAttribute("roles"));
        user.setOrganizations(oauth2Principal.getAttribute("organizations"));
        user.setId(oauth2Principal.getAttribute("id"));
        user.setTenantId(oauth2Principal.getAttribute("tenantId"));
        user.setVersion(oauth2Principal.getAttribute("version"));
        user.setDeleted(oauth2Principal.getAttribute("deleted"));
        user.setCreateTime(oauth2Principal.getAttribute("createTime"));
        user.setCreateUserId(oauth2Principal.getAttribute("createUserId"));
        user.setCreateUserName(oauth2Principal.getAttribute("createUserName"));
        user.setUpdateTime(oauth2Principal.getAttribute("updateTime"));
        user.setUpdateUserId(oauth2Principal.getAttribute("updateUserId"));
        user.setUpdateUserName(oauth2Principal.getAttribute("updateUserName"));
        return user;
    }

    /**
     * 获取当前用户
     *
     * @return 当前用户
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
     * @return 当前用户ID
     */
    public Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(IUser::getId);
    }

    /**
     * 获取当前用户租户ID
     *
     * @return 当前用户租户ID
     */
    public Optional<Long> getCurrentUserTenantId() {
        return getCurrentUser().map(IUser::getTenantId);
    }

    /**
     * 获取当前用户用户名
     *
     * @return 当前用户用户名
     */
    public Optional<String> getCurrentUsername() {
        return getCurrentUser().map(IUser::getUsername);
    }

    /**
     * 获取当前用户手机号
     *
     * @return 当前用户手机号
     */
    public Optional<String> getCurrentUserMobile() {
        return getCurrentUser().map(IUser::getMobile);
    }

    /**
     * 获取当前用户邮箱
     *
     * @return 当前用户邮箱
     */
    public Optional<String> getCurrentUserEmail() {
        return getCurrentUser().map(IUser::getEmail);
    }

    /**
     * 获取当前用户姓名
     *
     * @return 当前用户姓名
     */
    public Optional<String> getCurrentUserRealName() {
        return getCurrentUser().map(IUser::getRealName);
    }

    /**
     * 获取当前用户昵称
     *
     * @return 当前用户昵称
     */
    public Optional<String> getCurrentUserNickName() {
        return getCurrentUser().map(IUser::getNickName);
    }

    /**
     * 获取当前用户头像
     *
     * @return 当前用户头像
     */
    public Optional<String> getCurrentUserAvatar() {
        return getCurrentUser().map(IUser::getAvatar);
    }

    /**
     * 获取当前用户语言
     *
     * @return 当前用户语言
     */
    public Optional<String> getCurrentUserLanguage() {
        return getCurrentUser().map(IUser::getLanguage);
    }

    /**
     * 获取当前用户区域
     *
     * @return 当前用户区域
     */
    public Optional<Locale> getCurrentUserLocale() {
        return getCurrentUser().map(IUser::getLocale).map(Locale::forLanguageTag);
    }

    /**
     * 获取当前用户时区
     *
     * @return 当前用户时区
     */
    public Optional<TimeZone> getCurrentUserTimeZone() {
        return getCurrentUser().map(IUser::getTimeZone).map(TimeZone::getTimeZone);
    }

    /**
     * 获取当前用户角色
     *
     * @return 当前用户角色
     */
    public Optional<? extends IRole<?>> getCurrentUserRole() {
        return getCurrentUser().map(IUser::getRole);
    }

    /**
     * 获取当前用户组织机构
     *
     * @return 当前用户组织机构
     */
    public Optional<? extends IOrganization> getCurrentUserOrganization() {
        return getCurrentUser().map(IUser::getOrganization);
    }

    /**
     * 获取当前用户角色列表
     *
     * @return 当前用户角色列表
     */
    public Optional<List<? extends IRole<?>>> getCurrentUserRoles() {
        return getCurrentUser().map(IUser::getRoles);
    }

    /**
     * 获取当前用户组织机构列表
     *
     * @return 当前用户组织机构列表
     */
    public Optional<List<? extends IOrganization>> getCurrentUserOrganizations() {
        return getCurrentUser().map(IUser::getOrganizations);
    }

    /**
     * 获取当前用户权限列表
     *
     * @return 当前用户权限列表
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
     * 获取当前用户角色树
     *
     * @return 当前用户角色树
     */
    public Optional<List<Tree<Long>>> getCurrentUserRoleTree() {
        return getCurrentUserRoles()
                .map(roles -> TreeUtil.build(roles, 0L, new ITreeNodeParser<>()));
    }

    /**
     * 获取当前用户组织机构树
     *
     * @return 当前用户组织机构树
     */
    public Optional<List<Tree<Long>>> getCurrentUserOrganizationTree() {
        return getCurrentUserOrganizations()
                .map(organizations -> TreeUtil.build(organizations, 0L, new ITreeNodeParser<>()));
    }

    /**
     * 获取当前用户权限树
     *
     * @return 当前用户权限树
     */
    public Optional<List<Tree<Long>>> getCurrentUserPermissionTree() {
        return getCurrentUserPermissions()
                .map(permissions -> TreeUtil.build(permissions, 0L, new ITreeNodeParser<>()));
    }

}
