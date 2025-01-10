package com.gls.athena.common.bean.base;

/**
 * 通用枚举接口定义
 * <p>
 * 该接口为系统中所有枚举类提供统一的行为规范，包括：
 * 1. 通过编码值获取枚举实例
 * 2. 通过名称获取枚举实例
 * 3. 获取枚举的编码值和显示名称
 * </p>
 *
 * @param <T> 枚举编码值的类型参数，可以是任意对象类型
 * @author george
 */
public interface IEnum<T> {

    /**
     * 根据编码值查找对应的枚举实例
     *
     * @param enumClass 枚举类的Class对象，不能为null
     * @param code      枚举编码值
     * @param <E>       枚举类型，必须实现IEnum接口
     * @param <T>       编码值类型
     * @return 匹配的枚举实例，未找到则返回null
     * @throws IllegalArgumentException 当enumClass为null时抛出
     */
    static <E extends IEnum<T>, T> E of(Class<E> enumClass, T code) {
        if (enumClass == null) {
            throw new IllegalArgumentException("枚举类Class对象不能为null");
        }
        if (code == null) {
            return null;
        }
        for (E item : enumClass.getEnumConstants()) {
            if (code.equals(item.getCode())) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据名称查找对应的枚举实例
     *
     * @param enumClass 枚举类的Class对象，不能为null
     * @param name      枚举名称
     * @param <E>       枚举类型，必须实现IEnum接口
     * @param <T>       编码值类型
     * @return 匹配的枚举实例，未找到则返回null
     * @see #fromName(Class, String, boolean)
     */
    static <E extends IEnum<T>, T> E fromName(Class<E> enumClass, String name) {
        return fromName(enumClass, name, true);
    }

    /**
     * 根据名称查找对应的枚举实例
     *
     * @param enumClass     枚举类的Class对象，不能为null
     * @param name          枚举名称
     * @param caseSensitive 是否区分大小写，true表示区分，false表示不区分
     * @param <E>           枚举类型，必须实现IEnum接口
     * @param <T>           编码值类型
     * @return 匹配的枚举实例，未找到则返回null
     * @throws IllegalArgumentException 当enumClass为null时抛出
     */
    static <E extends IEnum<T>, T> E fromName(Class<E> enumClass, String name, boolean caseSensitive) {
        if (enumClass == null) {
            throw new IllegalArgumentException("枚举类Class对象不能为null");
        }
        if (name == null) {
            return null;
        }
        for (E item : enumClass.getEnumConstants()) {
            String enumName = item.getName();
            if (caseSensitive ? name.equals(enumName) : name.equalsIgnoreCase(enumName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 获取枚举编码值
     *
     * @return 当前枚举实例的编码值，可能为null
     */
    T getCode();

    /**
     * 获取枚举显示名称
     *
     * @return 当前枚举实例的显示名称，不应返回null
     */
    String getName();
}
