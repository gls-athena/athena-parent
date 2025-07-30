package com.gls.athena.security.servlet.client.config;

/**
 * 客户端安全相关常量接口
 * 定义了社交登录相关的会话键值和提供者标识
 *
 * @author george
 * @since 1.0.0
 */
public interface IClientConstants {
    /**
     * 社交用户会话键名
     * 用于在会话中存储社交用户信息
     */
    String SOCIAL_USER_SESSION_KEY = "SOCIAL_USER";

    /**
     * 社交登录提供者标识
     * 用于标识不同的社交登录服务提供商
     */
    String PROVIDER_ID = "providerId";
}

