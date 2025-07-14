package com.gls.athena.starter.core.async;

import org.springframework.scheduling.annotation.Async;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异步任务标记注解
 *
 * <p>结合 Spring 的 @Async 注解实现异步任务执行，并提供任务元数据配置能力。
 * 被标注的方法将自动提交到线程池执行。
 *
 * @author george
 * @see org.springframework.scheduling.annotation.Async
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Async
public @interface AsyncTask {
    /**
     * 任务唯一标识编码
     *
     * <p>用于任务追踪和标识，建议按业务模块规范定义编码规则。
     *
     * @return 任务编码，默认空字符串
     */
    String code() default "";

    /**
     * 任务显示名称
     *
     * <p>用于监控界面和日志记录，应简明描述任务功能。
     *
     * @return 任务名称，默认空字符串
     */
    String name() default "";

    /**
     * 任务详细描述
     *
     * <p>包含任务业务逻辑、注意事项等补充说明信息。
     *
     * @return 任务描述，默认空字符串
     */
    String description() default "";

    /**
     * 任务分类类型
     *
     * <p>用于任务监控统计、差异化异常处理和执行策略配置。
     *
     * @return 任务类型枚举，默认 OTHER
     * @see AsyncTaskType
     */
    AsyncTaskType type() default AsyncTaskType.OTHER;
}
