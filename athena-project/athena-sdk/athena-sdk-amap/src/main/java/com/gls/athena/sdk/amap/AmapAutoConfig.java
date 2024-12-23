package com.gls.athena.sdk.amap;

import com.gls.athena.sdk.amap.config.AmapClientConfig;
import com.gls.athena.sdk.amap.config.AmapProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 高德地图自动配置
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(AmapProperties.class)
@EnableFeignClients(defaultConfiguration = AmapClientConfig.class)
public class AmapAutoConfig {
}
