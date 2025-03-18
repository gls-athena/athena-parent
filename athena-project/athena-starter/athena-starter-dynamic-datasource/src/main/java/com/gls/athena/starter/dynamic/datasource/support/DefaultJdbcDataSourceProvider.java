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
 * 数据源配置提供者，负责从数据库中读取数据源配置信息
 *
 * @author george
 * @since 1.0
 */
public class DefaultJdbcDataSourceProvider extends AbstractJdbcDataSourceProvider {

    private final DataSourceProperties dataSourceProperties;
    private final DynamicDataSourceProperties dynamicDataSourceProperties;

    /**
     * 创建数据源配置提供者实例
     *
     * @param defaultDataSourceCreator    用于创建默认数据源的创建器
     * @param dataSourceProperties        基础数据源配置项
     * @param dynamicDataSourceProperties 动态数据源配置项
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
     * 执行SQL查询并获取数据源配置信息
     * <p>
     * 该方法通过执行预定义的SQL查询语句，从数据库中获取数据源配置信息，并将其转换为数据源配置映射表。
     * 同时，该方法还会添加默认的主数据源配置到映射表中。
     *
     * @param statement 数据库查询语句对象，用于执行SQL查询
     * @return 包含所有数据源配置信息的映射表，键为数据源名称，值为数据源配置属性
     * @throws SQLException 当数据库操作发生异常时抛出
     */
    @Override
    protected Map<String, DataSourceProperty> executeStmt(Statement statement) throws SQLException {
        Map<String, DataSourceProperty> dataSourcePropertiesMap;

        // 执行SQL查询并将结果集转换为数据源配置映射表
        try (ResultSet rs = statement.executeQuery(dynamicDataSourceProperties.getQueryDsSql())) {
            dataSourcePropertiesMap = toDataSourcePropertiesMap(rs);
        }

        // 添加默认主数据源配置到映射表中
        dataSourcePropertiesMap.put(IConstants.DEFAULT_DATASOURCE_NAME, createDefaultDataSourceProperty());

        return dataSourcePropertiesMap;
    }

    /**
     * 将查询结果转换为数据源配置对象
     * <p>
     * 该方法从数据库查询结果集中提取数据源配置信息，并将其转换为数据源配置对象的映射表。
     * 每个数据源配置对象包含驱动类名、URL、用户名、密码等信息，并且设置为懒加载模式。
     *
     * @param rs 数据库查询结果集，包含数据源配置信息
     * @return 数据源配置映射表，键为数据源名称，值为对应的数据源配置对象
     * @throws SQLException 当结果集解析发生异常时抛出
     */
    private Map<String, DataSourceProperty> toDataSourcePropertiesMap(ResultSet rs) throws SQLException {
        // 初始化数据源配置映射表
        Map<String, DataSourceProperty> dataSourcePropertiesMap = new HashMap<>(16);

        // 遍历结果集，提取数据源配置信息
        while (rs.next()) {
            // 创建数据源配置对象并设置属性
            DataSourceProperty property = new DataSourceProperty();
            property.setDriverClassName(rs.getString(dynamicDataSourceProperties.getDsDriverColumn()));
            property.setUrl(rs.getString(dynamicDataSourceProperties.getDsUrlColumn()));
            property.setUsername(rs.getString(dynamicDataSourceProperties.getDsUsernameColumn()));
            property.setPassword(rs.getString(dynamicDataSourceProperties.getDsPasswordColumn()));
            property.setLazy(true);

            // 获取数据源名称并将其作为键，数据源配置对象作为值存入映射表
            String dsName = rs.getString(dynamicDataSourceProperties.getDsNameColumn());
            dataSourcePropertiesMap.put(dsName, property);
        }

        // 返回数据源配置映射表
        return dataSourcePropertiesMap;
    }

    /**
     * 创建默认数据源配置
     * <p>
     * 该方法通过从全局配置中获取数据源的相关属性（如驱动类名、URL、用户名、密码等），
     * 并将其设置到一个新的 {@link DataSourceProperty} 对象中。同时，将数据源的懒加载属性设置为 true。
     *
     * @return 返回一个配置了默认属性的 {@link DataSourceProperty} 对象
     */
    private DataSourceProperty createDefaultDataSourceProperty() {
        // 创建新的数据源配置对象
        DataSourceProperty property = new DataSourceProperty();

        // 从全局配置中获取并设置数据源的相关属性
        property.setDriverClassName(dataSourceProperties.getDriverClassName());
        property.setUrl(dataSourceProperties.getUrl());
        property.setUsername(dataSourceProperties.getUsername());
        property.setPassword(dataSourceProperties.getPassword());

        // 设置数据源的懒加载属性为 true
        property.setLazy(true);

        return property;
    }

}
