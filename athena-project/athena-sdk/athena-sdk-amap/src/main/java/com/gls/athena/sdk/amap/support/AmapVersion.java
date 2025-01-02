package com.gls.athena.sdk.amap.support;

import java.lang.annotation.*;

/**
 * 高德地图API版本注解
 * 用于标注接口所使用的高德地图API版本
 *
 * @author george
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AmapVersion {
    /**
     * 指定高德地图API版本号
     *
     * @return API版本号，默认返回"v3"
     */
    String value() default "v3";
}
