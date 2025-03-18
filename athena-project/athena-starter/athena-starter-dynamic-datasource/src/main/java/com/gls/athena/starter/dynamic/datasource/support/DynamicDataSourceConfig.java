package com.gls.athena.starter.dynamic.datasource.support;

import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;

/**
 * 动态数据源配置
 *
 * @author george
 */
@AutoConfiguration
public class DynamicDataSourceConfig {

    /**
     * 创建并返回默认的JDBC数据源提供者。
     * 该函数根据配置动态数据源是否启用，决定是否创建默认的JDBC数据源提供者。
     * 如果配置中`athena.dynamic.datasource.enabled`为`true`或未配置，则创建该提供者。
     *
     * @param defaultDataSourceCreator    默认数据源创建器，用于创建数据源实例。
     * @param dataSourceProperties        数据源配置，包含数据源的基本配置信息。
     * @param dynamicDataSourceProperties 动态数据源配置，包含动态数据源的特定配置信息。
     * @return 返回一个配置好的默认JDBC数据源提供者实例。
     */
    @Bean
    @ConditionalOnProperty(prefix = "athena.dynamic.datasource", name = "enabled", havingValue = "true", matchIfMissing = true)
    public DefaultJdbcDataSourceProvider defaultJdbcDataSourceProvider(DefaultDataSourceCreator defaultDataSourceCreator,
                                                                       DataSourceProperties dataSourceProperties,
                                                                       DynamicDataSourceProperties dynamicDataSourceProperties) {
        return new DefaultJdbcDataSourceProvider(defaultDataSourceCreator, dataSourceProperties, dynamicDataSourceProperties);
    }

}
