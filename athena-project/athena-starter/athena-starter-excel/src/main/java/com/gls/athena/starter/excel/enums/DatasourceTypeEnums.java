package com.gls.athena.starter.excel.enums;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 数据源类型枚举
 * 定义系统支持的数据源类型及其对应的编码
 * 用于统一管理系统中使用的数据库类型
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum DatasourceTypeEnums implements IEnum {
    /**
     * MySQL数据库
     * 广泛使用的开源关系型数据库
     */
    MYSQL(1, "mysql"),

    /**
     * Oracle数据库
     * 商用关系型数据库，适用于大型企业级应用
     */
    ORACLE(2, "oracle"),

    /**
     * SQL Server数据库
     * 微软开发的关系型数据库管理系统
     */
    SQLSERVER(3, "sqlserver"),

    /**
     * PostgreSQL数据库
     * 功能强大的开源对象关系型数据库
     */
    POSTGRESQL(4, "postgresql");

    /**
     * 数据源类型编码
     * 用于在系统中唯一标识数据源类型
     */
    private final Integer code;

    /**
     * 数据源类型名称
     * 数据源的字符串标识，用于配置和显示
     */
    private final String name;

    /**
     * 根据编码查找对应的数据源类型
     * 通过遍历枚举值进行精确匹配
     *
     * @param code 数据源类型编码
     * @return 对应的数据源类型枚举值，未找到则返回null
     */
    public static DatasourceTypeEnums getByCode(Integer code) {
        return IEnum.of(DatasourceTypeEnums.class, code);
    }

    /**
     * 根据名称查找对应的数据源类型（忽略大小写）
     * 支持大小写不敏感的匹配查询
     *
     * @param name 数据源类型名称
     * @return 对应的数据源类型枚举值，未找到则返回null
     */
    public static DatasourceTypeEnums getByName(String name) {
        return IEnum.fromName(DatasourceTypeEnums.class, name, false);
    }
}
