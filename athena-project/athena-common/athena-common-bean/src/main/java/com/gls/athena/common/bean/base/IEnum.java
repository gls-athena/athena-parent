package com.gls.athena.common.bean.base;

/**
 * 通用枚举接口
 * 定义系统中所有枚举的标准行为，提供统一的枚举操作方法
 *
 * @author george
 */
public interface IEnum {

    /**
     * 根据编码获取枚举实例
     *
     * @param enumClass 枚举类型的Class对象
     * @param code      枚举编码值
     * @param <E>       枚举类型
     * @return 匹配的枚举实例，未找到返回null
     */
    static <E extends IEnum> E of(Class<E> enumClass, Integer code) {
        if (code == null || enumClass == null) {
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
     * 根据名称获取枚举实例（区分大小写）
     *
     * @param enumClass 枚举类型的Class对象
     * @param name      枚举名称
     * @param <E>       枚举类型
     * @return 匹配的枚举实例，未找到返回null
     */
    static <E extends IEnum> E fromName(Class<E> enumClass, String name) {
        return fromName(enumClass, name, true);
    }

    /**
     * 根据名称获取枚举实例
     *
     * @param enumClass     枚举类型的Class对象
     * @param name          枚举名称
     * @param caseSensitive 是否区分大小写
     * @param <E>           枚举类型
     * @return 匹配的枚举实例，未找到返回null
     */
    static <E extends IEnum> E fromName(Class<E> enumClass, String name, boolean caseSensitive) {
        if (name == null || enumClass == null) {
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
     * 获取枚举编码
     *
     * @return 枚举编码值
     */
    Integer getCode();

    /**
     * 获取枚举名称
     *
     * @return 枚举显示名称
     */
    String getName();
}
