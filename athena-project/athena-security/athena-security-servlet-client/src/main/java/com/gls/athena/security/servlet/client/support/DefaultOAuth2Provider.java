package com.gls.athena.security.servlet.client.support;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.security.servlet.client.config.ClientSecurityProperties;
import com.gls.athena.security.servlet.client.feishu.IFeishuConstants;
import com.gls.athena.security.servlet.client.wechat.IWechatConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.util.Map;
import java.util.Set;

/**
 * OAuth2认证服务提供者枚举
 * 包含各主流认证平台的配置信息，如微信开放平台、微信公众号、企业微信、飞书等
 * <p>
 * 每个枚举值代表一个具体的OAuth2认证服务提供者，通过实现getConfig方法来提供具体的配置信息。
 * 配置信息包括：
 * - providerId: 提供者唯一标识
 * - clientName: 客户端名称
 * - authorizationUri: 授权端点URL
 * - tokenUri: 令牌端点URL
 * - userInfoUri: 用户信息端点URL
 * - userNameAttributeName: 用户标识属性名
 * - scopes: 授权范围
 * - metadata: 额外的元数据信息
 *
 * @author george
 */
@RequiredArgsConstructor
public enum DefaultOAuth2Provider {
    /**
     * 微信开放平台
     * 用于网站应用扫码登录，适用于PC端网站
     * <p>
     * 主要特点：
     * - 使用QR码扫描方式登录
     * - 返回用户openid和基本信息
     * - 需要在微信开放平台注册应用
     */
    WECHAT_OPEN {
        @Override
        protected ProviderConfiguration getConfig(String registrationId) {
            ClientSecurityProperties properties = SpringUtil.getBean(ClientSecurityProperties.class);
            ClientSecurityProperties.WechatOpen wechatOpen = properties.getWechatOpen()
                    .getOrDefault(registrationId, new ClientSecurityProperties.WechatOpen());
            return new ProviderConfiguration()
                    .setProviderId(IWechatConstants.WECHAT_OPEN_PROVIDER_ID)
                    .setClientName("微信开放平台")
                    .setAuthorizationUri("https://open.weixin.qq.com/connect/qrconnect")
                    .setTokenUri("https://api.weixin.qq.com/sns/oauth2/access_token")
                    .setUserInfoUri("https://api.weixin.qq.com/sns/userinfo")
                    .setUserNameAttributeName("openid")
                    .setScopes(Set.of("snsapi_login"))
                    .setMetadata(Map.of("lang", wechatOpen.getLang()));
        }
    },
    /**
     * 微信公众平台
     * 用于微信内H5网页授权登录，适用于微信浏览器内访问
     * <p>
     * 主要特点：
     * - 仅支持微信内访问
     * - 支持静默授权和用户信息授权
     * - 需要在微信公众平台注册应用
     */
    WECHAT_MP {
        @Override
        protected ProviderConfiguration getConfig(String registrationId) {
            ClientSecurityProperties properties = SpringUtil.getBean(ClientSecurityProperties.class);
            ClientSecurityProperties.WechatMp wechatMp = properties.getWechatMp()
                    .getOrDefault(registrationId, new ClientSecurityProperties.WechatMp());
            return new ProviderConfiguration()
                    .setProviderId(IWechatConstants.WECHAT_MP_PROVIDER_ID)
                    .setClientName("微信公众平台")
                    .setAuthorizationUri("https://open.weixin.qq.com/connect/oauth2/authorize")
                    .setTokenUri("https://api.weixin.qq.com/sns/oauth2/access_token")
                    .setUserInfoUri("https://api.weixin.qq.com/sns/userinfo")
                    .setUserNameAttributeName("openid")
                    .setScopes(Set.of("snsapi_userinfo"))
                    .setMetadata(Map.of("lang", wechatMp.getLang()));
        }
    },
    /**
     * 微信小程序
     * 用于小程序登录，支持code换取session_key和openid
     * <p>
     * 主要特点：
     * - 专用于微信小程序
     * - 使用code换取session_key
     * - 支持加密数据解密
     */
    WECHAT_MINI {
        @Override
        protected ProviderConfiguration getConfig(String registrationId) {
            return new ProviderConfiguration()
                    .setProviderId(IWechatConstants.WECHAT_MINI_PROVIDER_ID)
                    .setClientName("微信小程序")
                    .setAuthorizationUri("/login/oauth2/code/wechat_mini")
                    .setTokenUri("https://api.weixin.qq.com/cgi-bin/token")
                    .setUserInfoUri("https://api.weixin.qq.com/sns/jscode2session")
                    .setUserNameAttributeName("openid")
                    .setScopes(Set.of("snsapi_userinfo"));
        }
    },
    /**
     * 企业微信
     * 支持企业微信内部应用和第三方应用的身份认证
     * <p>
     * 主要特点：
     * - 支持扫码登录和企业微信内打开两种方式
     * - 可配置登录类型和应用ID
     * - 返回用户企业ID和身份信息
     */
    WECHAT_WORK {
        @Override
        protected ProviderConfiguration getConfig(String registrationId) {
            ClientSecurityProperties properties = SpringUtil.getBean(ClientSecurityProperties.class);
            ClientSecurityProperties.WechatWork wechatWork = properties.getWechatWork()
                    .getOrDefault(registrationId, new ClientSecurityProperties.WechatWork());
            return new ProviderConfiguration()
                    .setProviderId(IWechatConstants.WECHAT_WORK_PROVIDER_ID)
                    .setClientName("企业微信")
                    .setAuthorizationUri("https://login.work.weixin.qq.com/wwlogin/sso/login")
                    .setTokenUri("https://qyapi.weixin.qq.com/cgi-bin/gettoken")
                    .setUserInfoUri("https://qyapi.weixin.qq.com/cgi-bin/user/get")
                    .setUserNameAttributeName("id")
                    .setScopes(Set.of("snsapi_base"))
                    .setMetadata(Map.of(IWechatConstants.WECHAT_WORK_USER_LOGIN_URI_NAME, "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo",
                            "loginType", wechatWork.getLoginType().getValue(),
                            "agentId", wechatWork.getAgentId(),
                            "lang", wechatWork.getLang()));
        }
    },
    /**
     * 飞书
     * 支持飞书应用的身份认证
     * <p>
     * 主要特点：
     * - 支持网页和移动应用场景
     * - 使用unionId作为用户标识
     * - 需要获取app_access_token
     */
    FEISHU {
        @Override
        protected ProviderConfiguration getConfig(String registrationId) {
            return new ProviderConfiguration()
                    .setProviderId(IFeishuConstants.PROVIDER_ID)
                    .setClientName("飞书")
                    .setAuthorizationUri("https://open.feishu.cn/open-apis/authen/v1/authorize")
                    .setTokenUri("https://open.feishu.cn/open-apis/authen/v1/oidc/access_token")
                    .setUserInfoUri("https://open.feishu.cn/open-apis/authen/v1/user_info")
                    .setUserNameAttributeName("unionId")
                    .setMetadata(Map.of(IFeishuConstants.APP_ACCESS_TOKEN_URL_NAME, "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal"));
        }
    };

    /**
     * 创建OAuth2服务提供者配置
     *
     * @param registrationId 客户端注册ID，用于区分同一类型下的不同应用配置
     * @return ProviderConfiguration 包含完整OAuth2提供者配置信息的对象
     */
    protected abstract ProviderConfiguration getConfig(String registrationId);

    /**
     * 获取OAuth2客户端注册构建器
     *
     * @param registrationId 客户端注册ID，用于区分同一类型下的不同应用配置
     * @return ClientRegistration.Builder 用于构建OAuth2客户端注册信息的构建器
     * @see org.springframework.security.oauth2.client.registration.ClientRegistration
     */
    public ClientRegistration.Builder getBuilder(String registrationId) {
        ProviderConfiguration configuration = getConfig(registrationId);
        return configuration.createBuilder(registrationId);
    }

}