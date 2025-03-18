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
     * 解析目标类和方法的缓存名称，并返回格式化后的缓存名称列表。
     * 该方法首先对目标类名进行规范化处理，然后通过AnnotationCacheOperationSource获取与目标方法相关的缓存操作。
     * 如果存在缓存操作，则提取缓存名称并进行格式化处理；如果不存在缓存操作，则返回默认的缓存名称。
     *
     * @param beanClass 目标类的Class对象，不能为null
     * @param method    目标方法的Method对象，不能为null
     * @return 格式化后的缓存名称列表，包含规范化类名和缓存名称的组合
     * @throws NullPointerException 当beanClass或method为null时抛出
     */
    public static List<String> getCacheNames(Class<?> beanClass, Method method) {
        // 检查参数是否为null，若为null则抛出NullPointerException
        Objects.requireNonNull(beanClass, "目标类不能为null");
        Objects.requireNonNull(method, "目标方法不能为null");

        // 将类名转换为指定格式的符号形式
        String normalizedClassName = StrUtil.toSymbolCase(beanClass.getSimpleName(), CLASS_NAME_SEPARATOR);

        // 创建AnnotationCacheOperationSource实例，用于获取缓存操作
        AnnotationCacheOperationSource operationSource = new AnnotationCacheOperationSource(false);

        // 获取与目标方法相关的缓存操作，并处理缓存名称
        return Optional.ofNullable(operationSource.getCacheOperations(method, beanClass))
                .map(operations -> operations.stream()
                        .map(CacheOperation::getCacheNames) // 提取每个缓存操作的缓存名称
                        .flatMap(Collection::stream) // 将嵌套的集合扁平化
                        .distinct() // 去重
                        .map(name -> normalizedClassName + CACHE_KEY_SEPARATOR + name) // 格式化缓存名称
                        .collect(Collectors.toList())) // 收集为列表
                .orElseGet(() -> Collections.singletonList(
                        normalizedClassName + CACHE_KEY_SEPARATOR + DEFAULT_CACHE_NAME)); // 若没有缓存操作，返回默认缓存名称
    }

    /**
     * 获取与给定缓存操作调用上下文相关的缓存名称集合。
     * 该方法通过调用另一个重载方法 `getCacheNames(Class<?>, Method)` 来实现，
     * 传递目标类和方法的参数。
     *
     * @param context 缓存操作调用上下文，包含目标对象和方法的信息。
     * @return 返回与目标类和方法相关的缓存名称集合。
     */
    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        // 通过目标类和方法获取缓存名称集合
        return getCacheNames(context.getTarget().getClass(), context.getMethod());
    }
}
