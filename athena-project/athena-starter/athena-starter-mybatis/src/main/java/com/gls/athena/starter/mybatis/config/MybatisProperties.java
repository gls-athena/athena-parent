package com.gls.athena.starter.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Mybatis配置
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + ".mybatis")
public class MybatisProperties extends BaseProperties {

    /**
     * 是否开启乐观锁
     */
    private boolean optimisticLocker = true;

    /**
     * 是否开启租户
     */
    private boolean tenant = true;

    /**
     * 是否开启数据权限
     */
    private boolean dataPermission = true;

    /**
     * 是否开启分页
     */
    private boolean pagination = true;

    /**
     * 数据库类型
     */
    private DbType dbType = DbType.MYSQL;

    /**
     * 忽略租户字段的表
     */
    private List<String> ignoreTenantTable = new ArrayList<>();
}
