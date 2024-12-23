package com.gls.athena.starter.log.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法日志注解
 *
 * @author george
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodLog {
    /**
     * 操作模块
     *
     * @return 操作模块
     */
    String code() default "";

    /**
     * 操作名称
     *
     * @return 操作名称
     */
    String name() default "";

    /**
     * 操作描述
     *
     * @return 操作描述
     */
    String description() default "";

}
