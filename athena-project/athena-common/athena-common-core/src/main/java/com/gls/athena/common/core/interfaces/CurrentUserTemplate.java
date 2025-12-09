package com.gls.athena.common.core.interfaces;

import java.util.Map;
import java.util.Set;

/**
 * 当前用户信息模板接口
 * 定义了获取当前用户基本信息和数据权限的相关方法
 *
 * @author george
 */
public interface CurrentUserTemplate {

    /**
     * 获取用户ID
     *
     * @return 用户唯一标识符
     */
    Long getId();

    /**
     * 获取用户真实姓名
     *
     * @return 用户真实姓名
     */
    String getRealName();

    /**
     * 获取租户ID
     *
     * @return 租户唯一标识符
     */
    Long getTenantId();

    /**
     * 获取指定表的数据权限
     * 根据表名获取用户在该表上的数据权限集合
     *
     * @param tableName 表名
     * @return 数据权限映射，键为权限类型，值为权限值集合
     */
    Map<String, Set<String>> getDataPermissions(String tableName);
}

