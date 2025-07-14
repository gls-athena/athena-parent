package com.gls.athena.starter.data.redis.cache;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
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
 * 默认缓存解析器
 * <p>
 * 统一管理缓存名称生成规则，按照 "className:cacheName" 格式命名。
 * 未指定缓存名称时使用 "className:default"。
 *
 * @author george
 */
@Slf4j
@Component
public class DefaultCacheResolver extends AbstractCacheResolver {

    private static final String CACHE_KEY_SEPARATOR = ":";
    private static final char CLASS_NAME_SEPARATOR = '-';
    private static final String DEFAULT_CACHE_NAME = "default";

    public DefaultCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    /**
     * 获取类和方法对应的缓存名称列表
     *
     * @param beanClass 目标类
     * @param method    目标方法
     * @return 格式化的缓存名称列表，格式为 "className:cacheName"
     * @throws NullPointerException 参数为null时抛出
     */
    public static List<String> getCacheNames(Class<?> beanClass, Method method) {
        Objects.requireNonNull(beanClass, "目标类不能为null");
        Objects.requireNonNull(method, "目标方法不能为null");

        // 类名转换为kebab-case格式
        String normalizedClassName = StrUtil.toSymbolCase(beanClass.getSimpleName(), CLASS_NAME_SEPARATOR);

        // 获取方法上的缓存操作
        AnnotationCacheOperationSource operationSource = new AnnotationCacheOperationSource(false);

        // 提取缓存名称并格式化
        return Optional.ofNullable(operationSource.getCacheOperations(method, beanClass))
                .map(operations -> operations.stream()
                        .map(CacheOperation::getCacheNames)
                        .flatMap(Collection::stream)
                        .distinct()
                        .map(name -> normalizedClassName + CACHE_KEY_SEPARATOR + name)
                        .collect(Collectors.toList()))
                .filter(list -> !list.isEmpty())
                .orElse(Collections.singletonList(
                        normalizedClassName + CACHE_KEY_SEPARATOR + DEFAULT_CACHE_NAME));
    }

    /**
     * 解析缓存操作上下文中的缓存名称
     *
     * @param context 缓存操作调用上下文
     * @return 缓存名称集合
     */
    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        List<String> cacheNames = getCacheNames(context.getTarget().getClass(), context.getMethod());
        log.info("缓存名称：{}", cacheNames);
        return cacheNames;
    }
}
