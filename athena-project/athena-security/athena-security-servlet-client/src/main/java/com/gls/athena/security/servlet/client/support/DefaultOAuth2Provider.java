package com.gls.athena.security.servlet.client.support;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.security.servlet.client.config.ClientSecurityProperties;
import com.gls.athena.security.servlet.client.config.IClientConstants;
import com.gls.athena.security.servlet.client.feishu.IFeishuConstants;
import com.gls.athena.security.servlet.client.wechat.IWechatConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 默认OAuth2提供者
 *
 * @author george
 */
@RequiredArgsConstructor
public enum DefaultOAuth2Provider {
    /**
     * 微信开放平台
     */
    WECHAT_OPEN("wechat_open",
            "微信开放平台",
            "https://open.weixin.qq.com/connect/qrconnect",
            "https://api.weixin.qq.com/sns/oauth2/access_token",
            "https://api.weixin.qq.com/sns/userinfo",
            "openid",
            Set.of("snsapi_login")) {
        @Override
        protected Map<String, Object> createMetadata(String registrationId) {
            ClientSecurityProperties properties = SpringUtil.getBean(ClientSecurityProperties.class);
            ClientSecurityProperties.WechatOpen wechatOpen = properties.getWechatOpen()
                    .getOrDefault(registrationId, new ClientSecurityProperties.WechatOpen());

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("lang", wechatOpen.getLang());
            return metadata;
        }
    },
    /**
     * 微信公众平台
     */
    WECHAT_MP("wechat_mp",
            "微信公众号",
            "https://open.weixin.qq.com/connect/oauth2/authorize",
            "https://api.weixin.qq.com/sns/oauth2/access_token",
            "https://api.weixin.qq.com/sns/userinfo",
            "openid",
            Set.of("snsapi_userinfo")) {
        @Override
        protected Map<String, Object> createMetadata(String registrationId) {
            ClientSecurityProperties properties = SpringUtil.getBean(ClientSecurityProperties.class);
            ClientSecurityProperties.WechatMp wechatMp = properties.getWechatMp()
                    .getOrDefault(registrationId, new ClientSecurityProperties.WechatMp());

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("lang", wechatMp.getLang());
            return metadata;
        }
    },
    /**
     * 微信小程序
     */
    WECHAT_MINI("wechat_mini", "微信小程序", "/login/oauth2/code/wechat_mini",
            "https://api.weixin.qq.com/cgi-bin/token", "https://api.weixin.qq.com/sns/jscode2session", "openid",
            Set.of("snsapi_userinfo")) {
        @Override
        protected Map<String, Object> createMetadata(String registrationId) {
            return new HashMap<>();
        }
    },
    /**
     * 企业微信
     */
    WECHAT_WORK("wechat_work",
            "企业微信",
            "https://login.work.weixin.qq.com/wwlogin/sso/login",
            "https://qyapi.weixin.qq.com/cgi-bin/gettoken",
            "https://qyapi.weixin.qq.com/cgi-bin/user/get",
            "id",
            Set.of("snsapi_base")) {
        @Override
        protected Map<String, Object> createMetadata(String registrationId) {
            ClientSecurityProperties properties = SpringUtil.getBean(ClientSecurityProperties.class);
            ClientSecurityProperties.WechatWork wechatWork = properties.getWechatWork()
                    .getOrDefault(registrationId, new ClientSecurityProperties.WechatWork());

            Map<String, Object> metadata = new HashMap<>();
            metadata.put(IWechatConstants.WECHAT_WORK_USER_LOGIN_URI_NAME, "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo");
            metadata.put("loginType", wechatWork.getLoginType().getValue());
            metadata.put("agentId", wechatWork.getAgentId());
            metadata.put("lang", wechatWork.getLang());
            return metadata;
        }
    },
    /**
     * 飞书
     */
    FEISHU("feishu",
            "飞书",
            "https://open.feishu.cn/open-apis/authen/v1/authorize",
            "https://open.feishu.cn/open-apis/authen/v1/oidc/access_token",
            "https://open.feishu.cn/open-apis/authen/v1/user_info",
            "unionId",
            null) {
        @Override
        protected Map<String, Object> createMetadata(String registrationId) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put(IFeishuConstants.APP_ACCESS_TOKEN_URL_NAME, "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal");
            return metadata;
        }
    };
    /**
     * 默认回调地址
     */
    private static final String DEFAULT_REDIRECT_URL = "{baseUrl}/{action}/oauth2/code/{registrationId}";

    private final String providerId;

    private final String clientName;

    private final String authorizationUri;

    private final String tokenUri;

    private final String userInfoUri;

    private final String userNameAttributeName;

    private final Set<String> scopes;

    /**
     * 创建元数据
     */
    protected abstract Map<String, Object> createMetadata(String registrationId);

    /**
     * 获取Builder
     *
     * @param registrationId 注册ID
     * @return Builder
     */
    public final ClientRegistration.Builder getBuilder(String registrationId) {
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);
        builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        builder.redirectUri(DEFAULT_REDIRECT_URL);
        builder.clientName(clientName);
        builder.scope(scopes);
        builder.authorizationUri(authorizationUri);
        builder.tokenUri(tokenUri);
        builder.userInfoUri(userInfoUri);
        builder.userNameAttributeName(userNameAttributeName);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(IClientConstants.PROVIDER_ID, providerId);
        metadata.putAll(createMetadata(registrationId));
        builder.providerConfigurationMetadata(metadata);
        return builder;
    }
}