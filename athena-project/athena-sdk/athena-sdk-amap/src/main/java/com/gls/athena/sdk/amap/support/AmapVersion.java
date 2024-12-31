package com.gls.athena.sdk.amap.support;

import java.lang.annotation.*;

/**
 * 高德地图版本
 *
 * @author george
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AmapVersion {
    /**
     * 高德地图版本
     *
     * @return 高德地图版本
     */
    String value() default "v3";
}
