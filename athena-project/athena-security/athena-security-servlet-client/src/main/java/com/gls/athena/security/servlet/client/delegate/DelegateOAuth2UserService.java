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
 * OAuth2用户信息服务委托实现类
 * <p>
 * 该类主要负责：
 * 1. OAuth2认证流程中的用户信息加载
 * 2. 社交平台用户信息转换
 * 3. 处理用户绑定状态
 * 4. 管理社交用户会话信息
 * </p>
 *
 * @author george
 */
@Component
public class DelegateOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    /**
     * 默认的OAuth2用户信息服务实现
     */
    private static final DefaultOAuth2UserService DEFAULT = new DefaultOAuth2UserService();
    /**
     * 未绑定用户的错误代码
     */
    private static final String ERROR_CODE = "social_user_not_bind";
    /**
     * 未绑定用户的错误消息
     */
    private static final String ERROR_MESSAGE = "社交用户未绑定";

    /**
     * 社交用户服务接口，用于处理社交用户的持久化操作
     */
    @Resource
    private ISocialUserService socialUserService;

    /**
     * OAuth2用户服务适配器提供者，用于支持不同社交平台的用户信息适配
     */
    @Resource
    private IOAuth2LoginAdapterManager adapterManager;

    /**
     * HTTP会话对象，用于存储社交用户信息
     */
    @Resource
    private HttpSession session;

    /**
     * 加载并处理OAuth2用户信息
     * <p>
     * 处理流程：
     * 1. 提取社交平台标识
     * 2. 加载OAuth2用户信息
     * 3. 转换为系统社交用户
     * 4. 处理未绑定状态
     * </p>
     *
     * @param userRequest OAuth2用户请求，包含客户端注册信息和授权信息
     * @return OAuth2User 处理后的用户信息对象
     * @throws OAuth2AuthenticationException 当用户未绑定或认证失败时抛出
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String provider = extractProvider(userRequest);

        OAuth2User oauth2User = loadOAuth2User(userRequest, provider);
        SocialUser socialUser = convertToSocialUser(oauth2User, registrationId);

        if (!socialUser.isBindStatus()) {
            handleUnboundUser(socialUser);
        }

        return socialUser;
    }

    /**
     * 从用户请求中提取社交平台提供者标识
     *
     * @param userRequest OAuth2用户请求对象
     * @return 社交平台提供者标识
     */
    private String extractProvider(OAuth2UserRequest userRequest) {
        return MapUtil.getStr(userRequest.getClientRegistration().getProviderDetails().getConfigurationMetadata(), IClientConstants.PROVIDER_ID);
    }

    /**
     * 根据提供者加载OAuth2用户信息
     * <p>
     * 优先使用适配器加载用户信息，如果没有匹配的适配器则使用默认实现
     * </p>
     *
     * @param userRequest OAuth2用户请求对象
     * @param provider    社交平台提供者标识
     * @return OAuth2User 加载的用户信息
     */
    private OAuth2User loadOAuth2User(OAuth2UserRequest userRequest, String provider) {
        return adapterManager.getAdapter(provider)
                .map(adapter -> adapter.loadUser(userRequest))
                .orElseGet(() -> DEFAULT.loadUser(userRequest));
    }

    /**
     * 处理未绑定的社交用户
     * <p>
     * 将未绑定的用户信息存入会话，并抛出认证异常
     * </p>
     *
     * @param socialUser 未绑定的社交用户对象
     * @throws OAuth2AuthenticationException 包含未绑定状态的错误信息
     */
    private void handleUnboundUser(SocialUser socialUser) {
        session.setAttribute(IClientConstants.SOCIAL_USER_SESSION_KEY, socialUser);
        throw new OAuth2AuthenticationException(
                new OAuth2Error(ERROR_CODE, ERROR_MESSAGE, null)
        );
    }

    /**
     * 将OAuth2用户转换为系统社交用户
     * <p>
     * 如果用户不存在则创建新用户
     * </p>
     *
     * @param oauth2User     OAuth2用户信息
     * @param registrationId 客户端注册ID
     * @return SocialUser 转换后的社交用户对象
     */
    private SocialUser convertToSocialUser(OAuth2User oauth2User, String registrationId) {
        return Optional.ofNullable(socialUserService.loadSocialUser(registrationId, oauth2User.getName()))
                .orElseGet(() -> createNewSocialUser(oauth2User, registrationId));
    }

    /**
     * 创建新的社交用户
     * <p>
     * 将OAuth2用户信息映射到系统社交用户对象，并保存到持久层
     * </p>
     *
     * @param oauth2User     OAuth2用户信息
     * @param registrationId 客户端注册ID
     * @return SocialUser 新创建的社交用户对象
     */
    private SocialUser createNewSocialUser(OAuth2User oauth2User, String registrationId) {
        SocialUser socialUser = new SocialUser();
        socialUser.setRegistrationId(registrationId);
        socialUser.setAttributes(oauth2User.getAttributes());
        socialUser.setAuthorities(new HashSet<>(oauth2User.getAuthorities()));
        socialUser.setName(oauth2User.getName());
        return socialUserService.saveSocialUser(socialUser);
    }
}
