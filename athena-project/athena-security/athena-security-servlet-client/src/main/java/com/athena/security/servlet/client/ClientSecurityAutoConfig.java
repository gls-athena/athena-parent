package com.athena.security.servlet.client;

import com.athena.security.servlet.client.feishu.FeishuProperties;
import com.athena.security.servlet.client.weixin.WeixinProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 客户端安全自动配置
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties({FeishuProperties.class, WeixinProperties.class})
public class ClientSecurityAutoConfig {
}