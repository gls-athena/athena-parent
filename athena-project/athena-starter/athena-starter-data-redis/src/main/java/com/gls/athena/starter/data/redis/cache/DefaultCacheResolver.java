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
 * 自定义缓存名称解析器
 * 用于统一管理缓存名称的生成规则，默认格式为: className:cacheName
 *
 * @author george
 */
@Component
public class DefaultCacheResolver extends AbstractCacheResolver {

    private static final String CACHE_NAME_DELIMITER = ":";
    private static final char CLASS_NAME_DELIMITER = '-';
    private static final String DEFAULT_CACHE_SUFFIX = "default";

    /**
     * 通过CacheManager初始化缓存解析器
     *
     * @param cacheManager Spring缓存管理器
     */
    public DefaultCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    /**
     * 解析指定类和方法的缓存名称
     * 如果方法上没有显式指定缓存名称，将使用默认名称(className:default)
     *
     * @param beanClass 目标类
     * @param method    目标方法
     * @return 解析后的缓存名称列表
     */
    public static List<String> getCacheNames(Class<?> beanClass, Method method) {
        Objects.requireNonNull(beanClass, "BeanClass must not be null");
        Objects.requireNonNull(method, "Method must not be null");

        String className = StrUtil.toSymbolCase(beanClass.getSimpleName(), CLASS_NAME_DELIMITER);
        AnnotationCacheOperationSource cacheOperationSource = new AnnotationCacheOperationSource(false);

        return Optional.ofNullable(cacheOperationSource.getCacheOperations(method, beanClass))
                .map(cacheOperations -> cacheOperations.stream()
                        .map(CacheOperation::getCacheNames)
                        .flatMap(Collection::stream)
                        .distinct()
                        .map(cacheName -> className + CACHE_NAME_DELIMITER + cacheName)
                        .collect(Collectors.toList()))
                .orElseGet(() -> Collections.singletonList(className + CACHE_NAME_DELIMITER + DEFAULT_CACHE_SUFFIX));
    }

    /**
     * 实现父类方法，解析缓存操作上下文中的缓存名称
     *
     * @param context 缓存操作上下文
     * @return 解析后的缓存名称集合
     */
    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        return getCacheNames(context.getTarget().getClass(), context.getMethod());
    }
}
