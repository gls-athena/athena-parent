package com.gls.athena.starter.async.domain;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 任务状态枚举类
 * <p>
 * 定义了异步任务的各种状态，包括待处理、处理中、已完成、失败和取消状态
 * 每个枚举值包含状态码和状态名称，用于任务状态的标识和展示
 * </p>
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum AsyncTaskStatus implements IEnum<Integer> {

    /**
     * 待处理状态
     * 表示任务已创建但尚未开始执行
     */
    PENDING(0, "待处理"),
    /**
     * 处理中状态
     * 表示任务正在执行过程中
     */
    PROCESSING(1, "处理中"),
    /**
     * 已完成状态
     * 表示任务已成功执行完成
     */
    COMPLETED(2, "已完成"),
    /**
     * 取消状态
     * 表示任务被手动取消或系统取消
     */
    CANCELED(3, "取消"),
    /**
     * 失败状态
     * 表示任务执行过程中发生错误导致执行失败
     */
    FAILED(4, "失败"),

    ;
    /**
     * 状态码
     * 用于系统内部标识任务状态的唯一编码
     */
    private final Integer code;

    /**
     * 状态名称
     * 用于展示给用户查看的任务状态描述
     */
    private final String name;

    /**
     * 根据代码值转换为对应的AsyncTaskStatus枚举对象
     *
     * @param code 要转换的代码值
     * @return 对应的AsyncTaskStatus枚举对象，如果未找到匹配项则返回null
     */
    public static AsyncTaskStatus convert(Integer code) {
        // 遍历所有枚举值，查找代码值匹配的枚举对象
        for (AsyncTaskStatus value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

}
