package com.gls.athena.starter.core.async;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 异步任务类型
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum AsyncTaskType implements IEnum<Integer> {
    /**
     * 导入
     */
    IMPORT(0, "导入"),
    /**
     * 导出
     */
    EXPORT(1, "导出"),
    /**
     * 其它
     */
    OTHER(2, "其它");

    /**
     * 异步任务类型
     */
    private final Integer code;
    /**
     * 异步任务类型名称
     */
    private final String name;

    /**
     * 根据给定的代码值获取对应的异步任务类型枚举实例。
     *
     * @param code 异步任务类型的代码值，用于查找对应的枚举实例。
     * @return 返回与给定代码值匹配的异步任务类型枚举实例。如果未找到匹配的枚举实例，则返回null。
     */
    public static AsyncTaskType getByCode(Integer code) {
        return IEnum.of(AsyncTaskType.class, code);
    }

    /**
     * 根据任务名称获取对应的异步任务类型枚举值。
     *
     * @param name 异步任务的名称，用于匹配枚举值。
     * @return 返回与名称匹配的异步任务类型枚举值。如果未找到匹配的枚举值，则返回null。
     */
    public static AsyncTaskType getByName(String name) {
        // 通过IEnum工具类从AsyncTaskType枚举类中根据名称获取对应的枚举值
        return IEnum.fromName(AsyncTaskType.class, name, false);
    }

}
