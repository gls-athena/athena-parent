package com.gls.athena.starter.data.redis.cache;

import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存过期时间处理器
 * <p>
 * 该处理器实现了 {@link BeanFactoryPostProcessor} 接口，在 Spring 容器初始化时
 * 扫描所有 Bean 中带有 {@code @CacheExpire} 注解的方法，收集缓存名称与过期时间的映射关系。
 * <p>
 * 主要功能：
 * <ul>
 *   <li>扫描 Spring 容器中所有 Bean 的方法</li>
 *   <li>识别带有 {@code @CacheExpire} 注解的方法</li>
 *   <li>提取注解配置的过期时间并存储映射关系</li>
 * </ul>
 *
 * @author george
 * @see CacheExpire
 * @see BeanFactoryPostProcessor
 */
@Data
@Component
public class CacheExpireProcessor implements BeanFactoryPostProcessor {

    /**
     * 缓存名称与过期时间的映射表
     * <p>
     * 存储从 {@code @CacheExpire} 注解中解析出的缓存配置信息
     */
    private final Map<String, Duration> expires = new HashMap<>();

    /**
     * Bean 工厂后置处理方法
     * <p>
     * 在 Spring 容器初始化完成后调用，遍历所有 Bean 定义，
     * 扫描并处理带有 {@code @CacheExpire} 注解的方法。
     *
     * @param beanFactory Spring 可配置的 Bean 工厂
     * @throws BeansException 如果 Bean 处理过程中发生异常
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Class<?> beanClass = beanFactory.getType(beanName);
            if (beanClass == null) {
                continue;
            }
            processCacheExpireAnnotations(beanClass);
        }
    }

    /**
     * 处理指定类中的 {@code @CacheExpire} 注解
     * <p>
     * 使用反射遍历类中的所有方法，找到带有 {@code @CacheExpire} 注解的方法，
     * 解析注解配置并将缓存名称与过期时间的映射关系存储到 {@link #expires} 中。
     *
     * @param beanClass 要扫描的 Bean 类
     */
    private void processCacheExpireAnnotations(Class<?> beanClass) {
        ReflectionUtils.doWithMethods(beanClass,
                method -> {
                    CacheExpire cacheExpire = method.getAnnotation(CacheExpire.class);
                    if (cacheExpire != null) {
                        List<String> cacheNames = DefaultCacheResolver.getCacheNames(beanClass, method);
                        Duration expireTime = Duration.of(
                                cacheExpire.timeToLive(),
                                cacheExpire.timeUnit().toChronoUnit()
                        );
                        cacheNames.forEach(cacheName -> expires.put(cacheName, expireTime));
                    }
                },
                method -> method.isAnnotationPresent(CacheExpire.class)
        );
    }

}
