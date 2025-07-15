package com.gls.athena.starter.dynamic.datasource.support;

import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;
import com.gls.athena.common.core.constant.IConstants;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认JDBC数据源提供者
 * <p>从数据库中动态加载数据源配置信息，支持多数据源的动态切换
 *
 * @author george
 * @since 1.0
 */
public class DefaultJdbcDataSourceProvider extends AbstractJdbcDataSourceProvider {

    private final DataSourceProperties dataSourceProperties;
    private final DynamicDataSourceProperties dynamicDataSourceProperties;

    /**
     * 构造函数
     *
     * @param defaultDataSourceCreator    默认数据源创建器
     * @param dataSourceProperties        主数据源配置
     * @param dynamicDataSourceProperties 动态数据源配置
     */
    public DefaultJdbcDataSourceProvider(DefaultDataSourceCreator defaultDataSourceCreator,
                                         DataSourceProperties dataSourceProperties,
                                         DynamicDataSourceProperties dynamicDataSourceProperties) {
        super(defaultDataSourceCreator,
                dataSourceProperties.getDriverClassName(),
                dataSourceProperties.getUrl(),
                dataSourceProperties.getUsername(),
                dataSourceProperties.getPassword());
        this.dataSourceProperties = dataSourceProperties;
        this.dynamicDataSourceProperties = dynamicDataSourceProperties;
    }

    /**
     * 执行SQL查询获取数据源配置
     *
     * @param statement SQL执行对象
     * @return 数据源配置映射，key为数据源名称，value为数据源属性
     * @throws SQLException 数据库操作异常
     */
    @Override
    protected Map<String, DataSourceProperty> executeStmt(Statement statement) throws SQLException {
        Map<String, DataSourceProperty> dataSourcePropertiesMap;

        // 执行查询并转换结果
        try (ResultSet rs = statement.executeQuery(dynamicDataSourceProperties.getQueryDsSql())) {
            dataSourcePropertiesMap = toDataSourcePropertiesMap(rs);
        }

        // 添加默认主数据源
        dataSourcePropertiesMap.put(IConstants.DEFAULT_DATASOURCE_NAME, createDefaultDataSourceProperty());

        return dataSourcePropertiesMap;
    }

    /**
     * 将查询结果转换为数据源配置映射
     *
     * @param rs 数据库查询结果集
     * @return 数据源配置映射
     * @throws SQLException 结果集解析异常
     */
    private Map<String, DataSourceProperty> toDataSourcePropertiesMap(ResultSet rs) throws SQLException {
        Map<String, DataSourceProperty> dataSourcePropertiesMap = new HashMap<>(16);

        while (rs.next()) {
            DataSourceProperty property = new DataSourceProperty();
            property.setDriverClassName(rs.getString(dynamicDataSourceProperties.getDsDriverColumn()));
            property.setUrl(rs.getString(dynamicDataSourceProperties.getDsUrlColumn()));
            property.setUsername(rs.getString(dynamicDataSourceProperties.getDsUsernameColumn()));
            property.setPassword(rs.getString(dynamicDataSourceProperties.getDsPasswordColumn()));
            property.setLazy(true);

            String dsName = rs.getString(dynamicDataSourceProperties.getDsNameColumn());
            dataSourcePropertiesMap.put(dsName, property);
        }

        return dataSourcePropertiesMap;
    }

    /**
     * 创建默认数据源配置
     *
     * @return 默认数据源属性配置
     */
    private DataSourceProperty createDefaultDataSourceProperty() {
        DataSourceProperty property = new DataSourceProperty();
        property.setDriverClassName(dataSourceProperties.getDriverClassName());
        property.setUrl(dataSourceProperties.getUrl());
        property.setUsername(dataSourceProperties.getUsername());
        property.setPassword(dataSourceProperties.getPassword());
        property.setLazy(true);

        return property;
    }

}
