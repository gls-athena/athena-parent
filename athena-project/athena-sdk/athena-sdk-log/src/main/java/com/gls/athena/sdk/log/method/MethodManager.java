package com.gls.athena.sdk.log.method;

import jakarta.annotation.Resource;
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
    @Resource
    private MethodEventSender methodEventSender;

    /**
     * 监听应用上下文刷新事件，扫描所有Bean中的方法，查找带有@MethodLog注解的方法，
     * 并发布方法日志事件。
     * <p>
     * 当Spring应用上下文完成初始化或刷新时触发，用于自动化注册需要日志监控的方法
     *
     * @param event 应用上下文刷新事件对象，包含刷新后的应用上下文信息及关联的Spring容器对象
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 获取当前已完成初始化的Spring应用上下文
        ApplicationContext applicationContext = event.getApplicationContext();

        // 获取容器中所有注册的Bean定义名称
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        // 遍历处理所有Spring管理的Bean对象
        for (String beanName : beanNames) {
            // 获取Bean的元数据类型，用于后续方法扫描
            Class<?> beanClass = applicationContext.getType(beanName);
            if (beanClass == null) {
                continue;
            }

            // 扫描当前Bean类中所有声明的方法，过滤带有MethodLog注解的方法
            ReflectionUtils.doWithMethods(beanClass, method -> {
                if (method.isAnnotationPresent(MethodLog.class)) {
                    // 解析方法上的日志注解配置信息
                    MethodLog methodLog = method.getAnnotation(MethodLog.class);
                    String applicationName = applicationContext.getApplicationName();
                    String className = method.getDeclaringClass().getName();
                    String methodName = method.getName();

                    // 构造并发送方法监控事件到消息队列
                    methodEventSender.sendMethodEvent(applicationName, className, methodName, methodLog);
                }
            });
        }
    }

}
