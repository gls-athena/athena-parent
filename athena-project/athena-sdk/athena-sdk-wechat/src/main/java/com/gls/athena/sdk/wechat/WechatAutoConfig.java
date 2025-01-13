package com.gls.athena.sdk.wechat;

import com.gls.athena.sdk.wechat.config.WechatProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 微信自动配置
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(WechatProperties.class)
public class WechatAutoConfig {

}