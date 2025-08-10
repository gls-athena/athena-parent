package com.gls.athena.common.core.base;

/**
 * 定制器接口，用于对指定类型的对象进行定制化处理
 *
 * @param <T> 定制对象的类型参数
 * @author george
 */
@FunctionalInterface
public interface ICustomizer<T> {
    /**
     * 创建一个默认的定制器实例，该实例不对传入对象进行任何操作
     *
     * @param <T> 定制对象的类型参数
     * @return 返回一个空实现的定制器实例
     */
    static <T> ICustomizer<T> withDefaults() {
        return (t) -> {
        };
    }

    /**
     * 对指定对象进行定制化处理
     *
     * @param t 需要被定制的对象实例
     */
    void customize(T t);
}

