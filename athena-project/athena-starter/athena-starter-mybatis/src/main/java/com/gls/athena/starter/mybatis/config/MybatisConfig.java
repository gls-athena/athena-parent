package com.gls.athena.starter.mybatis.config;

import com.baomidou.mybatisplus.autoconfigure.SqlSessionFactoryBeanCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.*;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * MyBatis配置类，用于配置MyBatis Plus的拦截器和插件。
 *
 * @author george
 */
@AutoConfiguration
public class MybatisConfig {

    /**
     * 配置乐观锁拦截器。
     *
     * @return 乐观锁拦截器实例
     */
    @Bean
    @Order(1)
    @ConditionalOnProperty(prefix = "athena.mybatis", name = "optimistic-locker", havingValue = "true", matchIfMissing = true)
    public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 配置租户插件拦截器。
     *
     * @param tenantLineHandler 租户处理器
     * @return 租户插件拦截器实例
     */
    @Bean
    @Order(2)
    @ConditionalOnBean(TenantLineHandler.class)
    @ConditionalOnProperty(prefix = "athena.mybatis", name = "tenant", havingValue = "true", matchIfMissing = true)
    public TenantLineInnerInterceptor tenantLineInnerInterceptor(TenantLineHandler tenantLineHandler) {
        return new TenantLineInnerInterceptor(tenantLineHandler);
    }

    /**
     * 配置数据权限拦截器。
     *
     * @param dataPermissionHandler 数据权限处理器
     * @return 数据权限拦截器实例
     */
    @Bean
    @Order(3)
    @ConditionalOnBean(DataPermissionHandler.class)
    @ConditionalOnProperty(prefix = "athena.mybatis", name = "data-permission", havingValue = "true", matchIfMissing = true)
    public DataPermissionInterceptor dataPermissionInnerInterceptor(DataPermissionHandler dataPermissionHandler) {
        return new DataPermissionInterceptor(dataPermissionHandler);
    }

    /**
     * 配置分页插件。
     *
     * @param mybatisProperties MyBatis配置属性
     * @return 分页插件实例
     */
    @Bean
    @Order(4)
    @ConditionalOnProperty(prefix = "athena.mybatis", name = "pagination", havingValue = "true", matchIfMissing = true)
    public PaginationInnerInterceptor paginationInnerInterceptor(MybatisProperties mybatisProperties) {
        return new PaginationInnerInterceptor(mybatisProperties.getDbType());
    }

    /**
     * 配置MyBatis Plus拦截器。
     *
     * @param innerInterceptors 内部拦截器列表
     * @return 配置好的MyBatis Plus拦截器实例
     */
    @Bean
    @ConditionalOnBean(InnerInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor(List<InnerInterceptor> innerInterceptors) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        innerInterceptors.forEach(interceptor::addInnerInterceptor);
        return interceptor;
    }

    /**
     * 自定义SqlSessionFactoryBean。
     *
     * @param typeHandlers 类型处理器列表
     * @return 自定义的SqlSessionFactoryBean实例
     */
    @Bean
    @ConditionalOnBean(TypeHandler.class)
    public SqlSessionFactoryBeanCustomizer sqlSessionFactoryBeanCustomizer(List<TypeHandler<?>> typeHandlers) {
        return sqlSessionFactoryBean -> sqlSessionFactoryBean.setTypeHandlers(typeHandlers.toArray(new TypeHandler<?>[0]));
    }
}