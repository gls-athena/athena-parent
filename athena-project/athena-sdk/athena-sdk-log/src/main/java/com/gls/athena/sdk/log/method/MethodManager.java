package com.gls.athena.sdk.log.method;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.sdk.log.domain.MethodDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * 方法管理器
 * 职责：专门负责在应用启动时扫描并注册带有@MethodLog注解的方法
 *
 * @author george
 */
@Slf4j
@Component
public class MethodManager {

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("开始扫描@MethodLog注解的方法...");

        ApplicationContext applicationContext = event.getApplicationContext();
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        String applicationName = SpringUtil.getApplicationName();

        int methodCount = 0;

        for (String beanName : beanNames) {
            methodCount += scanBeanMethods(applicationContext, beanName, applicationName);
        }

        log.info("@MethodLog注解方法扫描完成，共发现{}个方法", methodCount);
    }

    /**
     * 扫描指定Bean中的方法
     */
    private int scanBeanMethods(ApplicationContext applicationContext, String beanName, String applicationName) {
        Class<?> beanClass = applicationContext.getType(beanName);
        if (beanClass == null || isSystemClass(beanClass)) {
            return 0;
        }

        final int[] count = {0};

        try {
            ReflectionUtils.doWithMethods(beanClass, method -> {
                MethodLog methodLog = method.getAnnotation(MethodLog.class);
                if (methodLog != null) {
                    publishMethodInfo(methodLog, beanClass, method.getName(), applicationName);
                    count[0]++;
                }
            }, method -> method.isAnnotationPresent(MethodLog.class));

        } catch (Exception e) {
            log.warn("扫描Bean [{}] 的方法时发生异常: {}", beanName, e.getMessage());
        }

        return count[0];
    }

    /**
     * 发布方法信息事件
     */
    private void publishMethodInfo(MethodLog methodLog, Class<?> beanClass, String methodName, String applicationName) {
        try {
            MethodDto methodDto = new MethodDto()
                    .setCode(methodLog.code())
                    .setName(methodLog.name())
                    .setDescription(methodLog.description())
                    .setApplicationName(applicationName)
                    .setClassName(beanClass.getName())
                    .setMethodName(methodName);

            SpringUtil.publishEvent(methodDto);
            log.debug("发布方法信息: {}.{}", beanClass.getSimpleName(), methodName);

        } catch (Exception e) {
            log.error("发布方法信息失败: {}.{}, 错误: {}", beanClass.getSimpleName(), methodName, e.getMessage());
        }
    }

    /**
     * 判断是否为系统类，避免扫描Spring框架自身的类
     */
    private boolean isSystemClass(Class<?> clazz) {
        String className = clazz.getName();
        return className.startsWith("org.springframework") ||
                className.startsWith("org.apache") ||
                className.startsWith("com.sun") ||
                className.startsWith("sun.") ||
                className.startsWith("java.") ||
                className.startsWith("javax.");
    }
}
