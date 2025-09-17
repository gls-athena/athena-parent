package com.gls.athena.common.core.constant;

/**
 * 基础常量接口，定义系统中使用的全局常量。
 *
 * @author george
 */
public interface IConstants {
    /**
     * 基础属性前缀，用于配置文件中的属性分组标识。
     */
    String BASE_PROPERTIES_PREFIX = "athena";

    /**
     * 基础包前缀，用于标识项目的根包路径。
     */
    String BASE_PACKAGE_PREFIX = "com.gls.athena";

    /**
     * CPU核心数，获取当前运行环境的可用处理器数量。
     */
    Integer CPU_NUM = Runtime.getRuntime().availableProcessors();

    /**
     * 默认线程池名称，用于标识默认线程池的名称。
     */
    String DEFAULT_THREAD_POOL_NAME = "athena-async-executor";

    /**
     * 默认数据源名称，用于标识主数据源。
     */
    String DEFAULT_DATASOURCE_NAME = "master";

    /**
     * 客户端类型，用于标识请求来源的客户端类型。
     */
    String CLIENT_TYPE = "client-type";

    /**
     * 默认网关服务ID，用于标识网关服务的唯一标识。
     */
    String GATEWAY_SERVICE_ID = "athena-gateway";

    /**
     * 默认网关服务名称，用于显示网关服务的中文名称。
     */
    String GATEWAY_SERVICE_NAME = "网关服务";

    /**
     * 默认认证授权服务ID，用于标识认证授权服务的唯一标识。
     */
    String UAA_SERVICE_ID = "athena-uaa";

    /**
     * 默认认证授权服务名称，用于显示认证授权服务的中文名称。
     */
    String UAA_SERVICE_NAME = "认证授权服务";

    /**
     * 默认用户权限服务ID，用于标识用户权限服务的唯一标识。
     */
    String UPMS_SERVICE_ID = "athena-upms";

    /**
     * 默认用户权限服务名称，用于显示用户权限服务的中文名称。
     */
    String UPMS_SERVICE_NAME = "用户权限服务";

    /**
     * 默认用户ID，用于标识系统默认用户的唯一标识。
     */
    Long DEFAULT_USER_ID = 0L;

    /**
     * 默认用户名，用于标识系统默认用户的登录名。
     */
    String DEFAULT_USER_USERNAME = "admin";

    /**
     * 默认用户密码，用于标识系统默认用户的登录密码。
     */
    String DEFAULT_USER_PASSWORD = "admin";

    /**
     * 默认角色ID，用于标识系统默认角色的唯一标识。
     */
    Long DEFAULT_ROLE_ID = 0L;

    /**
     * 默认角色名称，用于标识系统默认角色的名称。
     */
    String DEFAULT_ROLE_NAME = "admin";

    /**
     * 默认角色编码，用于标识系统默认角色的编码。
     */
    String DEFAULT_ROLE_CODE = "admin";

    /**
     * 默认角色描述，用于描述系统默认角色的功能说明。
     */
    String DEFAULT_ROLE_DESC = "超级管理员";

    /**
     * 默认租户ID，用于标识系统默认租户的唯一标识。
     */
    Long DEFAULT_TENANT_ID = 0L;

    /**
     * 默认租户名称，用于标识系统默认租户的名称。
     */
    String DEFAULT_TENANT_NAME = "admin";

    /**
     * 默认租户编码，用于标识系统默认租户的编码。
     */
    String DEFAULT_TENANT_CODE = "admin";

    /**
     * 默认租户描述，用于描述系统默认租户的功能说明。
     */
    String DEFAULT_TENANT_DESC = "超级租户";

}
