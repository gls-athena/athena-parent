package com.gls.athena.sdk.feishu.config;

import com.gls.athena.sdk.feishu.support.FeishuClient;
import com.lark.oapi.core.cache.ICache;
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
     * 飞书客户端
     *
     * @param feishuProperties 飞书配置
     * @param cache            缓存
     * @return 客户端
     */
    @Bean
    @ConditionalOnMissingBean
    public FeishuClient feishuClient(FeishuProperties feishuProperties, ObjectProvider<ICache> cache) {
        return new FeishuClient(feishuProperties, cache);
    }
}
