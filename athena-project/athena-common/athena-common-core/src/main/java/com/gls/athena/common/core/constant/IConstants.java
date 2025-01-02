package com.gls.athena.common.core.constant;

/**
 * 基础常量
 *
 * @author george
 */
public interface IConstants {
    /**
     * 基础属性前缀
     */
    String BASE_PROPERTIES_PREFIX = "athena";
    /**
     * 基础包前缀
     */
    String BASE_PACKAGE_PREFIX = "com.gls.athena";
    /**
     * CPU核心数
     */
    Integer CPU_NUM = Runtime.getRuntime().availableProcessors();
    /**
     * 默认数据源名称
     */
    String DEFAULT_DATASOURCE_NAME = "master";
    /**
     * 客户端类型
     */
    String CLIENT_TYPE = "client-type";
    /**
     * 默认网关服务ID
     */
    String GATEWAY_SERVICE_ID = "athena-gateway";
    /**
     * 默认网关服务名称
     */
    String GATEWAY_SERVICE_NAME = "网关服务";
    /**
     * 默认认证授权服务ID
     */
    String UAA_SERVICE_ID = "athena-uaa";
    /**
     * 默认认证授权服务名称
     */
    String UAA_SERVICE_NAME = "认证授权服务";
    /**
     * 默认用户权限服务ID
     */
    String UPMS_SERVICE_ID = "athena-upms";
    /**
     * 默认用户权限服务名称
     */
    String UPMS_SERVICE_NAME = "用户权限服务";
    /**
     * 默认用户ID
     */
    Long DEFAULT_USER_ID = 0L;
    /**
     * 默认用户名
     */
    String DEFAULT_USER_USERNAME = "admin";
    /**
     * 默认用户密码
     */
    String DEFAULT_USER_PASSWORD = "admin";
    /**
     * 默认角色ID
     */
    Long DEFAULT_ROLE_ID = 0L;
    /**
     * 默认角色名称
     */
    String DEFAULT_ROLE_NAME = "admin";
    /**
     * 默认角色编码
     */
    String DEFAULT_ROLE_CODE = "admin";
    /**
     * 默认角色描述
     */
    String DEFAULT_ROLE_DESC = "超级管理员";
    /**
     * 默认租户ID
     */
    Long DEFAULT_TENANT_ID = 0L;
    /**
     * 默认租户名称
     */
    String DEFAULT_TENANT_NAME = "admin";
    /**
     * 默认租户编码
     */
    String DEFAULT_TENANT_CODE = "admin";
    /**
     * 默认租户描述
     */
    String DEFAULT_TENANT_DESC = "超级租户";

}
