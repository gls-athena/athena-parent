package com.gls.athena.starter.mybatis.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.gls.athena.common.bean.security.LoginUserHelper;
import com.gls.athena.common.core.constant.IConstants;
import com.gls.athena.starter.mybatis.config.MybatisProperties;
import jakarta.annotation.Resource;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.stereotype.Component;

/**
 * 默认租户处理器
 *
 * @author george
 */
@Component
public class DefaultTenantLineHandler implements TenantLineHandler {
    /**
     * Mybatis配置
     */
    @Resource
    private MybatisProperties mybatisProperties;

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    @Override
    public Expression getTenantId() {
        // 获取当前租户 ID
        Long tenantId = LoginUserHelper.getCurrentUserTenantId().orElse(IConstants.DEFAULT_TENANT_ID);
        return new LongValue(tenantId);
    }

    /**
     * 获取租户字段
     *
     * @param tableName 表名
     * @return 租户字段
     */
    @Override
    public boolean ignoreTable(String tableName) {
        // 忽略租户字段的表
        return mybatisProperties.getIgnoreTenantTable().contains(tableName);
    }
}
