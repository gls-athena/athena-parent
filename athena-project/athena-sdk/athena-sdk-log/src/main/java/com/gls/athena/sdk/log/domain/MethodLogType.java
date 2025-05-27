package com.gls.athena.sdk.log.domain;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 方法日志类型枚举
 * <p>
 * 该枚举定义方法执行过程中可能产生的日志类型分类，
 * 每个类型对应唯一编码和可读性名称，用于区分正常流程和异常场景的日志记录。
 *
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum MethodLogType implements IEnum<Integer> {

    /**
     * 表示方法正常执行时产生的日志记录
     * <p>
     * 对应编码：1，用于标识未发生异常的常规操作日志
     */
    NORMAL(1, "正常"),

    /**
     * 表示方法执行过程中发生异常时产生的日志记录
     * <p>
     * 对应编码：2，用于标识包含异常堆栈信息的错误日志
     */
    ERROR(2, "异常"),
    ;

    /**
     * 枚举类型编码
     * <p>
     * 符合IEnum接口规范的唯一标识码，用于持久化和序列化场景
     */
    private final Integer code;

    /**
     * 枚举类型显示名称
     * <p>
     * 用于日志展示、界面显示等需要人类可读的场景
     */
    private final String name;
}
