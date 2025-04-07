package com.gls.athena.sdk.log.method;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.sdk.log.domain.MethodDto;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * @author george
 */
@Component
public class MethodManager {

    /**
     * 监听应用上下文刷新事件，扫描所有Bean中的方法，查找带有@MethodLog注解的方法，
     * 并发布方法日志事件。
     *
     * @param event 应用上下文刷新事件，包含刷新后的应用上下文信息
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 获取应用上下文
        ApplicationContext applicationContext = event.getApplicationContext();

        // 获取所有Bean的名称
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        // 遍历所有Bean
        for (String beanName : beanNames) {
            // 获取Bean的Class类型
            Class<?> beanClass = applicationContext.getType(beanName);
            if (beanClass == null) {
                continue;
            }

            // 遍历Bean中的所有方法，查找带有@MethodLog注解的方法
            ReflectionUtils.doWithMethods(beanClass, method -> {
                if (method.isAnnotationPresent(MethodLog.class)) {
                    // 获取@MethodLog注解的详细信息
                    MethodLog methodLog = method.getAnnotation(MethodLog.class);
                    String className = method.getDeclaringClass().getName();
                    String methodName = method.getName();

                    // 发布方法日志事件
                    SpringUtil.publishEvent(new MethodDto()
                            .setCode(methodLog.code())
                            .setName(methodLog.name())
                            .setDescription(methodLog.description())
                            .setApplicationName(applicationContext.getApplicationName())
                            .setClassName(className)
                            .setMethodName(methodName));
                }
            });
        }
    }

}