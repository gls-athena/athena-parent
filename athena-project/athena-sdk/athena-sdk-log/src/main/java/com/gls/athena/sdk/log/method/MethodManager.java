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
 * @author george
 */
@Slf4j
@Component
public class MethodManager {

    /**
     * 应用上下文刷新事件处理函数：扫描所有Spring Bean中的方法，收集被@MethodLog注解标记的方法信息并发布事件
     *
     * @param event 上下文刷新事件对象，包含当前应用上下文信息
     * @return 无返回值
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 获取当前应用上下文及所有Bean定义名称
        ApplicationContext applicationContext = event.getApplicationContext();
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        // 提取固定应用名称到循环外部
        final String applicationName = SpringUtil.getApplicationName();

        // 遍历所有Bean进行方法扫描
        for (String beanName : beanNames) {
            Class<?> beanClass = applicationContext.getType(beanName);
            if (beanClass == null) {
                continue; // 跳过无法获取Class定义的Bean
            }

            try {
                // 使用反射工具处理带有MethodLog注解的方法
                ReflectionUtils.doWithMethods(beanClass, method -> {
                            MethodLog methodLog = method.getAnnotation(MethodLog.class);
                            // 显式开启方法访问权限（处理非public方法）
                            method.setAccessible(true);
                            // 构建方法元数据对象并发布事件
                            MethodDto methodDto = new MethodDto()
                                    .setCode(methodLog.code())
                                    .setName(methodLog.name())
                                    .setDescription(methodLog.description())
                                    .setApplicationName(applicationName)
                                    .setClassName(method.getDeclaringClass().getName())
                                    .setMethodName(method.getName());
                            SpringUtil.publishEvent(methodDto);
                        },
                        // 方法过滤条件：存在MethodLog注解且方法属于当前Bean类（排除父类方法）
                        method -> method.isAnnotationPresent(MethodLog.class)
                                && method.getDeclaringClass() == beanClass);
            } catch (Exception e) {
                // 单个Bean处理异常不影响整体流程
                log.error("Process bean [{}] method log failed", beanName, e);
            }
        }
    }
}
