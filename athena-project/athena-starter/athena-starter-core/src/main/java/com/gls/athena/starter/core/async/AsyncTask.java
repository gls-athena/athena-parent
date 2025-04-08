package com.gls.athena.starter.core.async;

import org.springframework.scheduling.annotation.Async;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记异步任务
 *
 * @author george
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Async
public @interface AsyncTask {
    /**
     * 任务编码
     *
     * @return 任务编码
     */
    String code() default "";

    /**
     * 任务名称
     *
     * @return 任务名称
     */
    String name() default "";

    /**
     * 任务描述
     *
     * @return 任务描述
     */
    String description() default "";

    /**
     * 任务类型
     *
     * @return 任务类型
     */
    AsyncTaskType type() default AsyncTaskType.OTHER;
}
