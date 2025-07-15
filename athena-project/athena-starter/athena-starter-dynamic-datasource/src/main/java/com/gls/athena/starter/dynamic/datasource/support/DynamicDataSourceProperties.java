package com.gls.athena.starter.dynamic.datasource.support;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 动态数据源配置属性
 * <p>
 * 用于配置从数据库表中动态加载数据源信息的相关参数。
 * 支持自定义数据库表的列名映射和查询SQL语句。
 * <p>
 * 配置前缀：{@code athena.dynamic.datasource}
 *
 * @author george
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".dynamic.datasource")
public class DynamicDataSourceProperties extends BaseProperties {

    /**
     * 数据源名称字段名
     * <p>
     * 指定数据库表中存储数据源名称的列名
     *
     * @default "name"
     */
    private String dsNameColumn = "name";

    /**
     * 数据源用户名字段名
     * <p>
     * 指定数据库表中存储数据源用户名的列名
     *
     * @default "username"
     */
    private String dsUsernameColumn = "username";

    /**
     * 数据源密码字段名
     * <p>
     * 指定数据库表中存储数据源密码的列名
     *
     * @default "password"
     */
    private String dsPasswordColumn = "password";

    /**
     * 数据源URL字段名
     * <p>
     * 指定数据库表中存储数据源连接URL的列名
     *
     * @default "url"
     */
    private String dsUrlColumn = "url";

    /**
     * 数据源驱动类字段名
     * <p>
     * 指定数据库表中存储数据源驱动类名的列名
     *
     * @default "driver_class_name"
     */
    private String dsDriverColumn = "driver_class_name";

    /**
     * 查询数据源配置的SQL语句
     * <p>
     * 用于从数据库中查询所有有效的数据源配置信息。
     * 默认查询条件为 {@code del_flag = 0}，表示未删除的记录。
     *
     * @default "select * from gen_datasource_conf where del_flag = 0"
     */
    private String queryDsSql = "select * from gen_datasource_conf where del_flag = 0";
}
