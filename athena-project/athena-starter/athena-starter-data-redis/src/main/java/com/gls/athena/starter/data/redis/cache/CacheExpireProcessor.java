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
     * 处理类中带有 CacheExpire 注解的方法
     *
     * @param beanClass 要处理的Bean类
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
