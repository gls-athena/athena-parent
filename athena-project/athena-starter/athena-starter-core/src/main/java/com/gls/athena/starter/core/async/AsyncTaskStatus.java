package com.gls.athena.starter.core.async;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 异步任务执行状态枚举
 * <p>
 * 定义异步任务在执行过程中的各种状态，用于跟踪和管理异步任务的生命周期。
 * 实现了 {@link IEnum} 接口，支持通过状态码和名称进行查找操作。
 * </p>
 *
 * @author george
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum AsyncTaskStatus implements IEnum<Integer> {

    /**
     * 待执行状态
     * <p>任务已创建但尚未开始执行</p>
     */
    WAITING(0, "待执行"),

    /**
     * 执行中状态
     * <p>任务正在执行过程中</p>
     */
    EXECUTING(1, "执行中"),

    /**
     * 执行成功状态
     * <p>任务已成功完成执行</p>
     */
    SUCCESS(2, "执行成功"),

    /**
     * 执行失败状态
     * <p>任务执行过程中发生错误或异常</p>
     */
    FAIL(3, "执行失败");

    /**
     * 状态码
     * <p>用于唯一标识每个任务状态的数字编码</p>
     */
    private final Integer code;

    /**
     * 状态名称
     * <p>状态的中文描述，便于用户理解和显示</p>
     */
    private final String name;

    /**
     * 根据状态码获取对应的异步任务状态枚举实例
     * <p>
     * 通过 {@link IEnum#of(Class, Object)} 方法实现状态码到枚举实例的转换。
     * 这是一个线程安全的操作，适用于高并发场景。
     * </p>
     *
     * @param code 异步任务的状态码，范围为 0-3
     * @return 与状态码匹配的异步任务状态枚举实例，如果状态码无效则返回 {@code null}
     * @throws IllegalArgumentException 当传入的状态码为负数时抛出
     */
    public static AsyncTaskStatus getByCode(Integer code) {
        return IEnum.of(AsyncTaskStatus.class, code);
    }

    /**
     * 根据状态名称获取对应的异步任务状态枚举实例
     * <p>
     * 通过 {@link IEnum#fromName(Class, String, boolean)} 方法实现名称到枚举实例的转换。
     * 支持精确匹配状态名称，不区分大小写。
     * </p>
     *
     * @param name 任务状态的名称，如 "待执行"、"执行中"、"执行成功"、"执行失败"
     * @return 与名称匹配的异步任务状态枚举实例，如果名称无效则返回 {@code null}
     * @throws IllegalArgumentException 当传入的名称为空字符串时抛出
     */
    public static AsyncTaskStatus getByName(String name) {
        return IEnum.fromName(AsyncTaskStatus.class, name, false);
    }
}
