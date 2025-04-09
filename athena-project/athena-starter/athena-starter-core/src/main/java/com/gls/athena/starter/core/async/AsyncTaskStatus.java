package com.gls.athena.starter.core.async;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 异步任务状态
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum AsyncTaskStatus implements IEnum<Integer> {
    /**
     * 待执行
     */
    WAITING(0, "待执行"),
    /**
     * 执行中
     */
    EXECUTING(1, "执行中"),
    /**
     * 执行成功
     */
    SUCCESS(2, "执行成功"),
    /**
     * 执行失败
     */
    FAIL(3, "执行失败");
    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 状态名称
     */
    private final String name;

    /**
     * 根据给定的状态码获取对应的异步任务状态枚举实例。
     *
     * @param code 异步任务的状态码，用于查找对应的枚举实例。
     * @return 返回与状态码匹配的异步任务状态枚举实例。如果未找到匹配的枚举实例，则返回null。
     */
    public static AsyncTaskStatus getByCode(Integer code) {
        return IEnum.of(AsyncTaskStatus.class, code);
    }

    /**
     * 根据任务状态名称获取对应的异步任务状态枚举值。
     *
     * @param name 任务状态的名称，用于查找对应的枚举值。
     * @return 返回与名称匹配的异步任务状态枚举值。如果未找到匹配的枚举值，则返回null。
     */
    public static AsyncTaskStatus getByName(String name) {
        // 通过IEnum工具类从AsyncTaskStatus枚举类中根据名称查找对应的枚举值
        return IEnum.fromName(AsyncTaskStatus.class, name, false);
    }

}
