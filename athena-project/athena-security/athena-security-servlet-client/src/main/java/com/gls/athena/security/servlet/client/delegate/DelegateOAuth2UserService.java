package com.gls.athena.security.servlet.client.delegate;

import cn.hutool.core.map.MapUtil;
import com.gls.athena.common.bean.security.SocialUser;
import com.gls.athena.security.servlet.client.config.IClientConstants;
import com.gls.athena.security.servlet.client.social.ISocialUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;

/**
 * OAuth2用户信息服务委托类
 * 负责处理OAuth2认证流程中的用户信息加载、转换和社交用户绑定逻辑
 *
 * @author george
 */
@Component
public class DelegateOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    /**
     * OAuth2用户信息服务默认实现
     */
    private static final DefaultOAuth2UserService DEFAULT = new DefaultOAuth2UserService();

    /**
     * 社交用户服务接口，用于处理社交用户的持久化操作
     */
    @Resource
    private ISocialUserService socialUserService;

    /**
     * OAuth2用户服务适配器提供者，用于支持不同社交平台的用户信息适配
     */
    @Resource
    private ObjectProvider<IOAuth2UserServiceAdapter> adapters;

    /**
     * HTTP会话对象，用于存储社交用户信息
     */
    @Resource
    private HttpSession session;

    /**
     * 加载OAuth2用户信息
     *
     * @param userRequest OAuth2用户请求对象，包含客户端注册信息和授权信息
     * @return OAuth2User 已加载的用户信息
     * @throws OAuth2AuthenticationException 当用户未绑定或认证失败时抛出
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 获取注册 ID
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // 获取提供者
        Map<String, Object> metadata = userRequest.getClientRegistration().getProviderDetails().getConfigurationMetadata();
        String provider = MapUtil.getStr(metadata, IClientConstants.PROVIDER_ID);
        // 加载用户
        OAuth2User oauth2User = adapters.stream()
                .filter(adapter -> adapter.test(provider))
                .findFirst()
                .map(adapter -> adapter.loadUser(userRequest))
                .orElseGet(() -> DEFAULT.loadUser(userRequest));
        // 转换为社交用户
        SocialUser socialUser = convetToSocialUser(oauth2User, registrationId);
        // 未绑定
        if (!socialUser.isBindStatus()) {
            // 设置社交用户
            session.setAttribute(IClientConstants.SOCIAL_USER_SESSION_KEY, socialUser);
            // 抛出异常
            throw new OAuth2AuthenticationException(new OAuth2Error("social_user_not_bind", "社交用户未绑定", null));
        }
        // 返回用户
        return socialUser;
    }

    /**
     * 将OAuth2User转换为系统的SocialUser对象
     *
     * @param oauth2User     OAuth2用户对象
     * @param registrationId 客户端注册ID，用于标识社交平台
     * @return SocialUser 转换后的社交用户对象
     */
    private SocialUser convetToSocialUser(OAuth2User oauth2User, String registrationId) {
        SocialUser socialUser = socialUserService.loadSocialUser(registrationId, oauth2User.getName());
        if (socialUser == null) {
            socialUser = new SocialUser();
            socialUser.setRegistrationId(registrationId);
            socialUser.setAttributes(oauth2User.getAttributes());
            socialUser.setAuthorities(new HashSet<>(oauth2User.getAuthorities()));
            socialUser.setName(oauth2User.getName());
            socialUser = socialUserService.saveSocialUser(socialUser);
        }
        return socialUser;
    }

}
