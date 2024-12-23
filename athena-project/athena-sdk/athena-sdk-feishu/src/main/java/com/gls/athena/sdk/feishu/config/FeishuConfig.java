package com.gls.athena.sdk.feishu.config;

import cn.hutool.core.util.StrUtil;
import com.lark.oapi.Client;
import com.lark.oapi.core.cache.ICache;
import com.lark.oapi.core.enums.AppType;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 飞书配置
 *
 * @author george
 */
@Configuration
@EnableConfigurationProperties(FeishuProperties.class)
public class FeishuConfig {
    /**
     * 缓存
     */
    @Resource
    private ObjectProvider<ICache> cache;
    /**
     * 飞书配置
     */
    @Resource
    private FeishuProperties feishuProperties;

    /**
     * 飞书客户端
     *
     * @return 客户端
     */
    @Bean
    @ConditionalOnMissingBean
    public Client feishuClient() {
        if (StrUtil.isBlank(feishuProperties.getAppId()) || StrUtil.isBlank(feishuProperties.getAppSecret())) {
            throw new IllegalArgumentException("飞书配置错误");
        }
        Client.Builder builder = Client.newBuilder(feishuProperties.getAppId(), feishuProperties.getAppSecret());
        // 应用类型
        if (feishuProperties.getAppType().equals(AppType.MARKETPLACE)) {
            builder.marketplaceApp();
        }
        // 是否开启debug模式
        builder.logReqAtDebug(feishuProperties.isDebugFlag());
        // 是否开启token缓存
        if (!feishuProperties.isTokenCacheFlag()) {
            builder.disableTokenCache();
        }
        // 工作台凭证
        if (StrUtil.isNotBlank(feishuProperties.getHelpDeskId()) && StrUtil.isNotBlank(feishuProperties.getHelpDeskSecret())) {
            builder.helpDeskCredential(feishuProperties.getHelpDeskId(), feishuProperties.getHelpDeskSecret());
        }
        // 超时时间
        if (feishuProperties.getRequestTimeout() > 0 && feishuProperties.getRequestTimeoutUnit() != null) {
            builder.requestTimeout(feishuProperties.getRequestTimeout(), feishuProperties.getRequestTimeoutUnit());
        }
        // 缓存
        cache.ifAvailable(builder::tokenCache);
        // 创建
        return builder.build();
    }
}
