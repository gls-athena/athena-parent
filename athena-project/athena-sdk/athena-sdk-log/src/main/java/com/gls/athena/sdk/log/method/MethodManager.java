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
     * 方法日志事件监听
     *
     * @param event 方法日志事件
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Class<?> beanClass = applicationContext.getType(beanName);
            if (beanClass == null) {
                continue;
            }
            ReflectionUtils.doWithMethods(beanClass, method -> {
                if (method.isAnnotationPresent(MethodLog.class)) {
                    MethodLog methodLog = method.getAnnotation(MethodLog.class);
                    String className = method.getDeclaringClass().getName();
                    String methodName = method.getName();
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