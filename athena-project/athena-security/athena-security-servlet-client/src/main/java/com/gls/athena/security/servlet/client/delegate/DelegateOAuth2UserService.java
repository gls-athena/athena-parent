package com.gls.athena.security.servlet.client.delegate;

import com.gls.athena.common.bean.security.SocialUser;
import com.gls.athena.security.servlet.client.config.IClientConstants;
import com.gls.athena.security.servlet.client.social.ISocialUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

/**
 * 委托OAuth2用户服务
 *
 * @author george
 */
@Component
public class DelegateOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String ERROR_CODE = "social_user_not_bind";

    private static final String ERROR_MESSAGE = "社交用户未绑定";

    @Resource
    private ISocialUserService socialUserService;

    @Resource
    private ISocialLoginAdapterManager adapterManager;

    @Resource
    private HttpSession session;

    /**
     * 重写loadUser方法以自定义OAuth2用户加载逻辑
     *
     * @param userRequest 包含客户端注册信息和用户信息的请求对象
     * @return 社交用户对象，包含用户详情和绑定状态
     * @throws OAuth2AuthenticationException 当发生认证错误时抛出此异常
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 使用适配器模式，根据客户端注册信息获取相应的用户详情
        OAuth2User oauth2User = adapterManager.loadUser(userRequest);
        // 获取客户端注册ID，用于后续处理
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // 根据客户端注册ID和获取的用户详情，构建并返回社交用户对象
        return getSocialUser(registrationId, oauth2User);
    }

    /**
     * 根据注册ID和OAuth2用户信息获取对应的社交用户对象
     * 如果社交用户不存在，则创建新的社交用户对象
     * 如果社交用户未绑定，将用户信息存储到会话并抛出认证异常
     *
     * @param registrationId 注册ID，用于标识社交平台
     * @param oauth2User     OAuth2用户对象，包含用户信息
     * @return 社交用户对象
     * @throws OAuth2AuthenticationException 如果社交用户未绑定，则抛出OAuth2认证异常
     */
    private SocialUser getSocialUser(String registrationId, OAuth2User oauth2User) {
        // 尝试加载社交用户信息，如果不存在，则根据OAuth2用户信息创建新的社交用户对象
        SocialUser socialUser = socialUserService.loadSocialUser(registrationId, oauth2User.getName());
        if (socialUser == null) {
            socialUser = createSocialUser(oauth2User, registrationId);
        }
        // 检查社交用户的绑定状态，如果未绑定，则存储用户信息到会话并抛出认证异常
        if (!socialUser.isBindStatus()) {
            // 将社交用户信息存储在会话中，以便后续流程可以访问
            session.setAttribute(IClientConstants.SOCIAL_USER_SESSION_KEY, socialUser);
            // 抛出OAuth2认证异常，指示发生了需要处理的认证错误
            throw new OAuth2AuthenticationException(new OAuth2Error(ERROR_CODE, ERROR_MESSAGE, null));
        }
        // 返回加载的社交用户对象
        return socialUser;
    }

    /**
     * 根据OAuth2用户信息获取或创建社交用户
     * 如果社交用户不存在，则基于OAuth2用户信息创建新的社交用户
     *
     * @param oauth2User     OAuth2用户对象，包含用户信息和权限
     * @param registrationId 社交用户注册ID，标识用户来自哪个社交平台
     * @return 返回创建或已存在的社交用户对象
     */
    private SocialUser createSocialUser(OAuth2User oauth2User, String registrationId) {
        // 如果社交用户不存在，则创建新的社交用户
        SocialUser socialUser = new SocialUser();
        // 设置社交用户的注册ID
        socialUser.setRegistrationId(registrationId);
        // 将OAuth2用户的属性复制到社交用户对象中
        socialUser.setAttributes(oauth2User.getAttributes());
        // 设置社交用户的名称
        socialUser.setName(oauth2User.getName());
        // 保存新的社交用户对象，并返回保存后的对象
        return socialUserService.saveSocialUser(socialUser);
    }

    /**
     * 加载OIDC用户信息
     * 本方法通过OIDC用户请求对象获取用户信息，并将其封装成OIDC用户对象返回
     * 主要步骤包括：
     * 1. 使用适配器管理器加载OIDC用户信息
     * 2. 提取客户端注册ID
     * 3. 根据客户端注册ID和用户详情构建社交用户对象
     * 4. 创建并返回OIDC用户对象
     *
     * @param oidcUserRequest OIDC用户请求对象，包含用户信息请求的相关数据
     * @return 返回一个封装好的OIDC用户对象
     */
    public OidcUser loadOidcUser(OidcUserRequest oidcUserRequest) {
        // 使用适配器管理器加载OIDC用户信息
        OidcUser oidcUser = adapterManager.loadOidcUser(oidcUserRequest);
        // 获取客户端注册ID，用于后续处理
        String registrationId = oidcUserRequest.getClientRegistration().getRegistrationId();
        // 根据客户端注册ID和获取的用户详情，构建并返回社交用户对象
        return getSocialUser(registrationId, oidcUser);
    }
}
