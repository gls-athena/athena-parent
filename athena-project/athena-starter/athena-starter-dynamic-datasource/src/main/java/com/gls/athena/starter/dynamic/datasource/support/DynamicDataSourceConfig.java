package com.gls.athena.starter.dynamic.datasource.support;

import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;

/**
 * 动态数据源自动配置类
 * <p>
 * 基于Spring Boot自动配置机制，提供动态数据源相关的Bean定义。
 * 支持通过配置项 {@code athena.dynamic.datasource.enabled} 控制是否启用动态数据源功能。
 *
 * @author george
 * @since 1.0.0
 */
@AutoConfiguration
public class DynamicDataSourceConfig {

    /**
     * 配置默认JDBC数据源提供者
     * <p>
     * 当 {@code athena.dynamic.datasource.enabled=true} 或该配置项未设置时，
     * 自动创建默认的JDBC数据源提供者实例。
     *
     * @param defaultDataSourceCreator    数据源创建器
     * @param dataSourceProperties        Spring Boot数据源配置属性
     * @param dynamicDataSourceProperties 动态数据源自定义配置属性
     * @return DefaultJdbcDataSourceProvider实例
     */
    @Bean
    @ConditionalOnProperty(prefix = "athena.dynamic.datasource", name = "enabled", havingValue = "true", matchIfMissing = true)
    public DefaultJdbcDataSourceProvider defaultJdbcDataSourceProvider(DefaultDataSourceCreator defaultDataSourceCreator,
                                                                       DataSourceProperties dataSourceProperties,
                                                                       DynamicDataSourceProperties dynamicDataSourceProperties) {
        return new DefaultJdbcDataSourceProvider(defaultDataSourceCreator, dataSourceProperties, dynamicDataSourceProperties);
    }

}
