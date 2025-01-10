package com.gls.athena.starter.data.redis.cache;

import cn.hutool.core.util.StrUtil;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.interceptor.AbstractCacheResolver;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 默认缓存名称解析器
 * <p>
 * 用于统一管理缓存键的生成规则，遵循 "className:cacheName" 的命名约定。
 * 当未指定缓存名称时，默认使用 "className:default" 作为缓存键。
 *
 * @author george
 */
@Component
public class DefaultCacheResolver extends AbstractCacheResolver {

    private static final String CACHE_KEY_SEPARATOR = ":";
    private static final char CLASS_NAME_SEPARATOR = '-';
    private static final String DEFAULT_CACHE_NAME = "default";

    public DefaultCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    /**
     * 解析目标类和方法的缓存名称
     *
     * @param beanClass 目标类Class对象
     * @param method    目标方法对象
     * @return 格式化后的缓存名称列表
     * @throws NullPointerException 当beanClass或method为null时抛出
     */
    public static List<String> getCacheNames(Class<?> beanClass, Method method) {
        Objects.requireNonNull(beanClass, "目标类不能为null");
        Objects.requireNonNull(method, "目标方法不能为null");

        String normalizedClassName = StrUtil.toSymbolCase(beanClass.getSimpleName(), CLASS_NAME_SEPARATOR);
        AnnotationCacheOperationSource operationSource = new AnnotationCacheOperationSource(false);

        return Optional.ofNullable(operationSource.getCacheOperations(method, beanClass))
                .map(operations -> operations.stream()
                        .map(CacheOperation::getCacheNames)
                        .flatMap(Collection::stream)
                        .distinct()
                        .map(name -> normalizedClassName + CACHE_KEY_SEPARATOR + name)
                        .collect(Collectors.toList()))
                .orElseGet(() -> Collections.singletonList(
                        normalizedClassName + CACHE_KEY_SEPARATOR + DEFAULT_CACHE_NAME));
    }

    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        return getCacheNames(context.getTarget().getClass(), context.getMethod());
    }
}
