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
 * 用于扫描并处理带有 @CacheExpire 注解的方法，
 * 收集缓存名称与过期时间的映射关系
 *
 * @author george
 */
@Data
@Component
public class CacheExpireProcessor implements BeanFactoryPostProcessor {

    /**
     * 存储缓存名称和对应的过期时间配置
     * key: 缓存名称
     * value: 过期时间
     */
    private final Map<String, Duration> expires = new HashMap<>();

    /**
     * 在BeanFactory初始化后，对所有的Bean进行处理，主要处理带有缓存过期注解的Bean。
     * 该方法会遍历BeanFactory中的所有Bean定义，获取每个Bean的Class类型，并调用
     * `processCacheExpireAnnotations`方法处理带有缓存过期注解的Bean。
     *
     * @param beanFactory 可配置的BeanFactory，包含所有的Bean定义。
     * @throws BeansException 如果在处理过程中发生Bean相关的异常。
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 获取BeanFactory中所有Bean定义的名称
        String[] beanNames = beanFactory.getBeanDefinitionNames();

        // 遍历所有Bean名称，获取对应的Class类型，并处理带有缓存过期注解的Bean
        for (String beanName : beanNames) {
            Class<?> beanClass = beanFactory.getType(beanName);
            if (beanClass == null) {
                continue;
            }

            // 处理带有缓存过期注解的Bean
            processCacheExpireAnnotations(beanClass);
        }
    }

    /**
     * 处理类中带有 CacheExpire 注解的方法，并为这些方法对应的缓存设置过期时间。
     * <p>
     * 该方法会遍历指定类中的所有方法，查找带有 CacheExpire 注解的方法。对于每个带有该注解的方法，
     * 它会获取注解中配置的过期时间，并将其应用到对应的缓存上。
     *
     * @param beanClass 要处理的Bean类，该类中的方法将被扫描以查找 CacheExpire 注解。
     */
    private void processCacheExpireAnnotations(Class<?> beanClass) {
        // 使用反射工具遍历类中的所有方法，处理带有 CacheExpire 注解的方法
        ReflectionUtils.doWithMethods(beanClass,
                method -> {
                    // 获取方法上的 CacheExpire 注解
                    CacheExpire cacheExpire = method.getAnnotation(CacheExpire.class);
                    if (cacheExpire != null) {
                        // 获取该方法对应的缓存名称列表
                        List<String> cacheNames = DefaultCacheResolver.getCacheNames(beanClass, method);
                        // 根据注解中的配置，计算缓存的过期时间
                        Duration expireTime = Duration.of(
                                cacheExpire.timeToLive(),
                                cacheExpire.timeUnit().toChronoUnit()
                        );

                        // 为每个缓存名称设置过期时间
                        cacheNames.forEach(cacheName -> expires.put(cacheName, expireTime));
                    }
                },
                // 过滤条件：只处理带有 CacheExpire 注解的方法
                method -> method.isAnnotationPresent(CacheExpire.class)
        );
    }

}
