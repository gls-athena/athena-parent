package com.gls.athena.starter.core.async;

import org.springframework.scheduling.annotation.Async;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异步任务标记注解，用于标识需要异步执行的方法
 *
 * <p>该注解结合Spring的@Async注解实现异步任务执行，并扩展了任务元数据配置能力。
 * 被标注的方法将自动提交到线程池执行，适用于需要异步处理的业务场景。</p>
 *
 * @author george
 * @Target ElementType.METHOD 表示该注解仅适用于方法级别
 * @Retention RetentionPolicy.RUNTIME 表示注解信息在运行时保留可通过反射获取
 * @see org.springframework.scheduling.annotation.Async 基于Spring异步执行机制
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Async
public @interface AsyncTask {
    /**
     * 获取异步任务的唯一标识编码
     *
     * <p>用于业务系统中对异步任务进行唯一标识和追踪，建议按照业务模块规范定义编码规则。
     * 未指定时使用空字符串，建议在需要任务追踪的场景中明确指定。</p>
     *
     * @return 任务编码字符串，默认空字符串
     */
    String code() default "";

    /**
     * 获取异步任务的显示名称
     *
     * <p>用于监控界面、日志记录等需要人类可读标识的场景，应简明描述任务功能。
     * 未指定时使用空字符串，建议在需要可视化监控的场景中明确指定。</p>
     *
     * @return 任务名称字符串，默认空字符串
     */
    String name() default "";

    /**
     * 获取异步任务的详细描述
     *
     * <p>包含任务业务逻辑、注意事项等补充说明信息，可用于生成系统文档。
     * 未指定时使用空字符串，建议在需要生成详细文档的场景中明确指定。</p>
     *
     * @return 任务描述字符串，默认空字符串
     */
    String description() default "";

    /**
     * 获取异步任务的分类类型
     *
     * <p>通过AsyncTaskType枚举对任务进行分类管理，可用于：
     * <ul>
     * <li>任务监控统计</li>
     * <li>差异化异常处理</li>
     * <li>执行策略配置</li>
     * </ul>
     * 默认使用OTHER类型，建议根据业务特征选择合适的分类。</p>
     *
     * @return AsyncTaskType枚举值，默认OTHER类型
     * @see AsyncTaskType 异步任务分类枚举定义
     */
    AsyncTaskType type() default AsyncTaskType.OTHER;
}
