package com.gls.athena.starter.core.async;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 异步任务类型枚举
 * <p>
 * 定义系统中支持的异步任务类型，包括数据导入、导出等操作类型。
 * 每个枚举值都包含对应的编码和名称，用于在系统中标识不同类型的异步任务。
 * </p>
 *
 * @author george
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum AsyncTaskType implements IEnum<Integer> {
    /**
     * 数据导入任务
     * <p>用于标识批量数据导入相关的异步任务</p>
     */
    IMPORT(0, "导入"),

    /**
     * 数据导出任务
     * <p>用于标识批量数据导出相关的异步任务</p>
     */
    EXPORT(1, "导出"),

    /**
     * 其他类型任务
     * <p>用于标识除导入导出外的其他异步任务类型</p>
     */
    OTHER(2, "其它");

    /**
     * 异步任务类型编码
     * <p>每个任务类型的唯一标识码</p>
     */
    private final Integer code;

    /**
     * 异步任务类型名称
     * <p>任务类型的中文描述名称</p>
     */
    private final String name;

    /**
     * 根据编码获取异步任务类型枚举实例
     *
     * @param code 异步任务类型编码，不能为null
     * @return 对应的异步任务类型枚举实例，如果编码不存在则返回null
     * @see IEnum#of(Class, Object)
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
