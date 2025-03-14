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
 * 用户帮助类
 *
 * @author george
 */
@UtilityClass
public class LoginUserHelper {

    /**
     * 将OAuth2认证主体转换为用户对象。
     * <p>
     * 该方法通过OAuth2认证主体中的属性信息，填充并生成一个新的用户对象。
     *
     * @param oauth2Principal OAuth2认证主体，包含用户的认证信息
     * @return 返回一个填充了属性信息的用户对象
     */
    public User toUser(OAuth2AuthenticatedPrincipal oauth2Principal) {
        // 使用BeanUtil工具类将OAuth2认证主体的属性映射到用户对象中
        return BeanUtil.fillBeanWithMap(oauth2Principal.getAttributes(), new User(), true);
    }

    /**
     * 获取当前用户。
     * <p>
     * 该方法从Spring Security的SecurityContextHolder中获取当前认证信息，并根据认证信息中的主体（Principal）类型，
     * 将其转换为相应的用户对象。支持SocialUser、User和OAuth2AuthenticatedPrincipal三种类型的用户主体。
     *
     * @return 返回一个Optional包装的当前用户对象。如果当前没有用户认证信息或无法识别的主体类型，则返回空的Optional。
     */
    public Optional<? extends IUser<?, ?, ?>> getCurrentUser() {
        // 从SecurityContextHolder中获取当前认证信息，并将其转换为Optional
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                // 从认证信息中提取主体（Principal）
                .map(Authentication::getPrincipal)
                // 根据主体类型进行匹配，转换为相应的用户对象
                .map(principal -> switch (principal) {
                    case SocialUser socialUser -> socialUser.getUser();
                    case User user -> user;
                    case OAuth2AuthenticatedPrincipal oauth2Principal -> toUser(oauth2Principal);
                    default -> null;
                });
    }

    /**
     * 获取当前用户ID
     * <p>
     * 该方法通过调用 {@link #getCurrentUser()} 方法获取当前用户对象，并从中提取用户ID。
     * 如果当前用户存在，则返回其ID；否则返回一个空的 {@link Optional}。
     *
     * @return 当前用户ID的 {@link Optional} 对象，如果用户存在则包含用户ID，否则为空
     */
    public Optional<Long> getCurrentUserId() {
        // 获取当前用户对象并提取用户ID
        return getCurrentUser().map(IUser::getId);
    }

    /**
     * 获取当前用户的租户ID。
     * <p>
     * 该方法通过调用 `getCurrentUser()` 获取当前用户对象，然后从用户对象中提取租户ID。
     * 如果当前用户不存在，则返回一个空的 `Optional` 对象。
     *
     * @return 当前用户的租户ID，封装在 `Optional` 中。如果用户不存在，则返回 `Optional.empty()`。
     */
    public Optional<Long> getCurrentUserTenantId() {
        // 获取当前用户并映射到其租户ID
        return getCurrentUser().map(IUser::getTenantId);
    }

    /**
     * 获取当前用户的用户名。
     * <p>
     * 该方法通过调用 {@link #getCurrentUser()} 获取当前用户对象，然后从中提取用户名。
     * 如果当前用户存在且用户名不为空，则返回包含用户名的 {@link Optional} 对象；否则返回空的 {@link Optional}。
     *
     * @return 包含当前用户用户名的 {@link Optional} 对象，如果用户不存在或用户名为空，则返回空的 {@link Optional}
     */
    public Optional<String> getCurrentUsername() {
        // 获取当前用户对象并提取用户名
        return getCurrentUser().map(IUser::getUsername);
    }

    /**
     * 获取当前用户的手机号。
     * <p>
     * 该方法通过调用 {@link #getCurrentUser()} 获取当前用户对象，然后从用户对象中提取手机号。
     * 如果当前用户存在且手机号不为空，则返回包含手机号的 {@link Optional} 对象；否则返回空的 {@link Optional} 对象。
     *
     * @return 包含当前用户手机号的 {@link Optional} 对象，如果用户不存在或手机号为空，则返回空的 {@link Optional} 对象
     */
    public Optional<String> getCurrentUserMobile() {
        return getCurrentUser().map(IUser::getMobile);
    }

    /**
     * 获取当前用户的邮箱地址。
     * <p>
     * 该方法通过调用 `getCurrentUser()` 获取当前用户对象，然后从用户对象中提取邮箱地址。
     * 如果当前用户存在且邮箱地址不为空，则返回包含邮箱地址的 `Optional<String>` 对象；
     * 如果当前用户不存在或邮箱地址为空，则返回空的 `Optional<String>` 对象。
     *
     * @return 包含当前用户邮箱地址的 `Optional<String>` 对象，如果不存在则返回空的 `Optional`
     */
    public Optional<String> getCurrentUserEmail() {
        return getCurrentUser().map(IUser::getEmail);
    }

    /**
     * 获取当前用户的真实姓名。
     * <p>
     * 该方法通过调用 {@link #getCurrentUser()} 方法获取当前用户对象，
     * 然后使用 {@link IUser#getRealName()} 方法提取用户的真实姓名。
     * 如果当前用户不存在或用户对象为空，则返回一个空的 {@link Optional}。
     *
     * @return 包含当前用户真实姓名的 {@link Optional} 对象，如果用户不存在或用户对象为空，则返回空的 {@link Optional}
     */
    public Optional<String> getCurrentUserRealName() {
        // 获取当前用户并映射到其真实姓名
        return getCurrentUser().map(IUser::getRealName);
    }

    /**
     * 获取当前用户的昵称。
     * <p>
     * 该方法通过调用 {@link #getCurrentUser()} 方法获取当前用户对象，
     * 然后使用 {@link IUser#getNickName()} 方法将用户对象映射为其昵称。
     * 如果当前用户存在且昵称不为空，则返回包含昵称的 {@link Optional} 对象；
     * 否则返回空的 {@link Optional} 对象。
     *
     * @return 包含当前用户昵称的 {@link Optional} 对象，如果用户不存在或昵称为空，则返回空的 {@link Optional}
     */
    public Optional<String> getCurrentUserNickName() {
        return getCurrentUser().map(IUser::getNickName);
    }

    /**
     * 获取当前用户的头像URL。
     * <p>
     * 该方法首先通过调用 {@link #getCurrentUser()} 获取当前用户对象，然后通过 {@link IUser#getAvatar()} 方法获取用户的头像URL。
     * 如果当前用户存在且头像URL不为空，则返回包含头像URL的 {@link Optional} 对象；否则返回空的 {@link Optional} 对象。
     *
     * @return 包含当前用户头像URL的 {@link Optional} 对象，如果用户不存在或头像URL为空，则返回空的 {@link Optional}。
     */
    public Optional<String> getCurrentUserAvatar() {
        return getCurrentUser().map(IUser::getAvatar);
    }

    /**
     * 获取当前用户的语言设置。
     * <p>
     * 该方法通过调用 {@link #getCurrentUser()} 获取当前用户对象，然后从用户对象中提取语言设置。
     * 如果当前用户存在且语言设置不为空，则返回该语言设置；否则返回一个空的 {@link Optional}。
     *
     * @return 当前用户的语言设置，封装在 {@link Optional} 中。如果用户不存在或语言设置为空，则返回空的 {@link Optional}。
     */
    public Optional<String> getCurrentUserLanguage() {
        return getCurrentUser().map(IUser::getLanguage);
    }

    /**
     * 获取当前用户的区域设置（Locale）。
     * 该方法首先获取当前用户，然后从用户对象中提取区域设置的语言标签，并将其转换为Locale对象。
     * 如果当前用户不存在或用户没有设置区域信息，则返回空的Optional对象。
     *
     * @return 当前用户的区域设置，封装在Optional中。如果用户或区域信息不存在，则返回Optional.empty()。
     */
    public Optional<Locale> getCurrentUserLocale() {
        // 获取当前用户，并映射到用户的区域设置语言标签，最后转换为Locale对象
        return getCurrentUser().map(IUser::getLocale).map(Locale::forLanguageTag);
    }

    /**
     * 获取当前用户的时区信息。
     * <p>
     * 该方法通过获取当前用户对象，并从中提取时区信息，最终返回一个包含时区的Optional对象。
     * 如果当前用户不存在或时区信息不可用，则返回一个空的Optional对象。
     *
     * @return 包含当前用户时区的Optional对象，如果时区不可用则返回Optional.empty()
     */
    public Optional<TimeZone> getCurrentUserTimeZone() {
        // 获取当前用户对象，并从中提取时区信息
        return getCurrentUser().map(IUser::getTimeZone).map(TimeZone::getTimeZone);
    }

    /**
     * 获取当前用户的角色信息。
     * <p>
     * 该方法首先通过调用 `getCurrentUser()` 获取当前用户对象，然后通过 `map` 方法提取用户的角色信息。
     * 如果当前用户存在且用户角色信息有效，则返回包含该角色的 `Optional` 对象；否则返回空的 `Optional`。
     *
     * @return 包含当前用户角色的 `Optional` 对象，如果用户或角色不存在则返回空的 `Optional`
     */
    public Optional<? extends IRole<?>> getCurrentUserRole() {
        return getCurrentUser().map(IUser::getRole);
    }

    /**
     * 获取当前用户的组织机构信息。
     * <p>
     * 该方法首先通过调用 {@link #getCurrentUser()} 获取当前用户的可选对象，然后通过 {@link IUser#getOrganization()} 方法
     * 获取该用户的组织机构信息。如果当前用户存在且其组织机构信息不为空，则返回该组织机构的可选对象；否则返回空的可选对象。
     *
     * @return 当前用户的组织机构信息的 {@link Optional} 对象，可能为空
     */
    public Optional<? extends IOrganization> getCurrentUserOrganization() {
        return getCurrentUser().map(IUser::getOrganization);
    }

    /**
     * 获取当前用户的角色列表。
     * <p>
     * 该方法首先通过调用 `getCurrentUser()` 获取当前用户对象，然后通过 `map` 方法将用户对象映射为其角色列表。
     * 如果当前用户存在，则返回其角色列表；否则返回一个空的 `Optional`。
     *
     * @return 当前用户的角色列表，封装在 `Optional` 中。如果用户不存在，则返回 `Optional.empty()`。
     */
    public Optional<List<? extends IRole<?>>> getCurrentUserRoles() {
        // 获取当前用户并映射到其角色列表
        return getCurrentUser().map(IUser::getRoles);
    }

    /**
     * 获取当前用户所属的组织机构列表。
     * <p>
     * 该方法首先通过调用 `getCurrentUser()` 获取当前用户对象，然后通过 `map` 方法将用户对象映射为其所属的组织机构列表。
     * 如果当前用户存在，则返回其组织机构列表；否则返回一个空的 `Optional` 对象。
     *
     * @return 返回一个包含当前用户组织机构列表的 `Optional` 对象。如果当前用户不存在，则返回 `Optional.empty()`。
     */
    public Optional<List<? extends IOrganization>> getCurrentUserOrganizations() {
        return getCurrentUser().map(IUser::getOrganizations);
    }

    /**
     * 获取当前用户的权限列表。
     * 该方法首先获取当前用户，然后从用户角色中提取权限，并确保权限列表中的元素唯一。
     *
     * @return 包含当前用户权限列表的 {@link Optional} 对象。如果当前用户不存在，则返回空的 {@link Optional}。
     */
    public Optional<List<? extends IPermission>> getCurrentUserPermissions() {
        // 获取当前用户，并映射到用户的角色列表
        return getCurrentUser()
                .map(IUser::getRoles)
                // 将角色列表映射到权限列表，并确保权限唯一
                .map(roles -> roles.stream()
                        .map(IRole::getPermissions)
                        .flatMap(List::stream)
                        .distinct().toList());
    }

    /**
     * 获取当前用户的角色树结构。
     * <p>
     * 该方法首先调用 `getCurrentUserRoles()` 获取当前用户的角色列表，然后使用 `TreeUtil.build()` 方法
     * 将这些角色构建成一个树形结构。树的根节点ID为0L，并使用 `ITreeNodeParser<>()` 进行节点解析。
     *
     * @return 返回一个包含当前用户角色树的 `Optional` 对象。如果当前用户没有角色，则返回 `Optional.empty()`。
     */
    public Optional<List<Tree<Long>>> getCurrentUserRoleTree() {
        // 获取当前用户的角色列表，并将其构建成树形结构
        return getCurrentUserRoles()
                .map(roles -> TreeUtil.build(roles, 0L, new ITreeNodeParser<>()));
    }

    /**
     * 获取当前用户组织机构树
     * <p>
     * 该方法通过调用 {@link #getCurrentUserOrganizations()} 获取当前用户的组织机构列表，
     * 然后使用 {@link TreeUtil#build(List, Long, ITreeNodeParser)} 方法将组织机构列表转换为树形结构。
     * 如果当前用户没有组织机构信息，则返回一个空的 {@link Optional}。
     * </p>
     *
     * @return 当前用户组织机构树的 {@link Optional} 对象，包含一个 {@link List} 的 {@link Tree} 结构，
     * 如果当前用户没有组织机构信息，则返回 {@link Optional#empty()}。
     */
    public Optional<List<Tree<Long>>> getCurrentUserOrganizationTree() {
        // 获取当前用户的组织机构列表，并将其转换为树形结构
        return getCurrentUserOrganizations()
                .map(organizations -> TreeUtil.build(organizations, 0L, new ITreeNodeParser<>()));
    }

    /**
     * 获取当前用户的权限树结构。
     * <p>
     * 该方法首先调用 `getCurrentUserPermissions()` 获取当前用户的权限列表，然后使用 `TreeUtil.build()` 方法
     * 将权限列表转换为树形结构。树的根节点 ID 为 0L，并使用 `ITreeNodeParser<>()` 进行节点解析。
     *
     * @return 返回一个 `Optional` 对象，包含当前用户的权限树结构。如果当前用户没有权限，则返回 `Optional.empty()`。
     */
    public Optional<List<Tree<Long>>> getCurrentUserPermissionTree() {
        // 获取当前用户的权限列表，并将其转换为树形结构
        return getCurrentUserPermissions()
                .map(permissions -> TreeUtil.build(permissions, 0L, new ITreeNodeParser<>()));
    }

}
