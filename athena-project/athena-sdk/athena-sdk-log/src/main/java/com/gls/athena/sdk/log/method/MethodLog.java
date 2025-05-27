package com.gls.athena.sdk.log.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法日志注解
 * <p>
 * 该注解用于标记需要记录操作日志的方法，支持定义操作所属模块、业务名称和详细描述。
 * 注解保留策略为运行时，可通过反射机制在运行时获取注解信息。
 * </p>
 *
 * @author george
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodLog {
    /**
     * 操作模块
     * <p>
     * 用于标识方法所属的功能模块（如"订单管理"、"权限控制"），
     * 便于日志分类和统计分析
     * </p>
     *
     * @return 操作模块名称，返回空字符串时表示未配置
     */
    String code() default "";

    /**
     * 操作名称
     * <p>
     * 定义具体的操作行为名称（如"创建订单"、"删除用户"），
     * 用于在日志中清晰展示操作类型
     * </p>
     *
     * @return 操作行为名称，返回空字符串时表示未配置
     */
    String name() default "";

    /**
     * 操作描述
     * <p>
     * 提供操作细节的补充说明（如"根据用户ID批量删除历史数据"），
     * 用于生成更易理解的详细日志记录
     * </p>
     *
     * @return 操作详细描述，返回空字符串时表示未配置
     */
    String description() default "";
}
