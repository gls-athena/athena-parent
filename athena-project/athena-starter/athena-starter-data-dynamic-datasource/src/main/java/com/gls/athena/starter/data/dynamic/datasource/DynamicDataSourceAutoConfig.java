package com.gls.athena.starter.data.dynamic.datasource;

import com.gls.athena.starter.data.dynamic.datasource.support.DynamicDataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 动态数据源自动配置
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties({DynamicDataSourceProperties.class})
public class DynamicDataSourceAutoConfig {
}