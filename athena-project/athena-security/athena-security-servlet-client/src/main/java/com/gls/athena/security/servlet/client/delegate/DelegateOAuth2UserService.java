package com.gls.athena.security.servlet.client.delegate;

import cn.hutool.core.map.MapUtil;
import com.gls.athena.common.bean.security.SocialUser;
import com.gls.athena.security.servlet.client.config.IClientConstants;
import com.gls.athena.security.servlet.client.social.ISocialUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;

/**
 * 委托OAuth2用户服务
 *
 * @author george
 */
@Component
public class DelegateOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final DefaultOAuth2UserService DEFAULT = new DefaultOAuth2UserService();

    private static final String ERROR_CODE = "social_user_not_bind";

    private static final String ERROR_MESSAGE = "社交用户未绑定";

    @Resource
    private ISocialUserService socialUserService;

    @Resource
    private ISocialLoginAdapterManager adapterManager;

    @Resource
    private HttpSession session;

    /**
     * 加载并处理OAuth2用户信息
     * <p>
     * 该方法实现了OAuth2UserService接口的loadUser方法，用于从OAuth2提供方获取用户信息，
     * 并将其转换为系统内部的SocialUser对象。如果用户未绑定系统账号，则进行相应处理。
     *
     * @param userRequest OAuth2用户请求对象，包含客户端注册信息和访问令牌
     * @return 处理后的SocialUser对象，包含OAuth2用户信息和绑定状态
     * @throws OAuth2AuthenticationException 如果用户加载或处理过程中出现认证异常
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 获取客户端注册ID和提供方信息
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String provider = extractProvider(userRequest);

        // 从OAuth2提供方加载用户信息并转换为系统用户对象
        OAuth2User oauth2User = loadOAuth2User(userRequest, provider);
        SocialUser socialUser = convertToSocialUser(oauth2User, registrationId);

        // 处理未绑定系统账号的用户
        if (!socialUser.isBindStatus()) {
            handleUnboundUser(socialUser);
        }

        return socialUser;
    }

    /**
     * 从OAuth2用户请求中提取提供商标识
     *
     * @param userRequest OAuth2用户请求对象，包含客户端注册信息和提供方详情
     * @return 提供商标识字符串，从提供方配置元数据中获取。如果不存在则返回null
     */
    private String extractProvider(OAuth2UserRequest userRequest) {
        // 从客户端注册信息中获取提供方配置元数据，并提取PROVIDER_ID对应的值
        return MapUtil.getStr(userRequest.getClientRegistration().getProviderDetails().getConfigurationMetadata(), IClientConstants.PROVIDER_ID);
    }

    /**
     * 根据服务提供者加载OAuth2用户信息
     * <p>
     * 此方法通过适配器模式从指定的服务提供者获取用户信息如果指定的适配器不存在，
     * 则使用默认适配器处理请求这样做的目的是确保无论指定的提供者是否支持，都能尝试获取用户信息
     *
     * @param userRequest 包含用户请求信息的OAuth2UserRequest对象
     * @param provider    服务提供者的标识符，用于确定使用哪个适配器来处理请求
     * @return 返回一个OAuth2User对象，包含用户信息
     */
    private OAuth2User loadOAuth2User(OAuth2UserRequest userRequest, String provider) {
        return adapterManager.getAdapter(provider)
                .map(adapter -> adapter.loadUser(userRequest))
                .orElseGet(() -> DEFAULT.loadUser(userRequest));
    }

    /**
     * 处理未绑定社交用户的场景
     * 当社交用户未绑定时，此方法将社交用户信息存储在会话中，并抛出OAuth2认证异常
     *
     * @param socialUser 社交用户对象，代表当前未绑定的用户
     */
    private void handleUnboundUser(SocialUser socialUser) {
        // 将社交用户信息存储在会话中，以便后续流程可以访问
        session.setAttribute(IClientConstants.SOCIAL_USER_SESSION_KEY, socialUser);

        // 抛出OAuth2认证异常，指示发生了需要处理的认证错误
        throw new OAuth2AuthenticationException(
                new OAuth2Error(ERROR_CODE, ERROR_MESSAGE, null)
        );
    }

    /**
     * 将OAuth2用户转换为社交用户
     * 如果社交用户已存在，则直接加载；否则，创建新的社交用户
     *
     * @param oauth2User     OAuth2用户信息，包含用户标识和属性
     * @param registrationId 客户端注册标识，用于区分不同的社交平台
     * @return 返回一个社交用户对象
     */
    private SocialUser convertToSocialUser(OAuth2User oauth2User, String registrationId) {
        // 尝试加载已存在的社交用户，如果不存在，则创建并返回新的社交用户
        return Optional.ofNullable(socialUserService.loadSocialUser(registrationId, oauth2User.getName()))
                .orElseGet(() -> createNewSocialUser(oauth2User, registrationId));
    }

    /**
     * 根据OAuth2用户信息创建新的社交用户
     *
     * @param oauth2User     OAuth2用户对象，包含用户认证信息和属性
     * @param registrationId 社交平台的注册ID，用于标识社交平台
     * @return 返回新创建并保存的社交用户对象
     */
    private SocialUser createNewSocialUser(OAuth2User oauth2User, String registrationId) {
        // 创建一个新的SocialUser对象
        SocialUser socialUser = new SocialUser();

        // 设置社交用户的注册ID
        socialUser.setRegistrationId(registrationId);

        // 将OAuth2用户的属性复制到社交用户对象中
        socialUser.setAttributes(oauth2User.getAttributes());

        // 将OAuth2用户的权限复制到社交用户对象中，使用HashSet确保权限的唯一性
        socialUser.setAuthorities(new HashSet<>(oauth2User.getAuthorities()));

        // 设置社交用户的名称
        socialUser.setName(oauth2User.getName());

        // 保存新的社交用户对象，并返回保存后的对象
        return socialUserService.saveSocialUser(socialUser);
    }
}
