package com.gls.athena.common.core.constant;

import lombok.Data;

import java.util.Map;

/**
 * 基础属性
 * <p>
 * 该类作为属性配置的基类，提供通用的属性配置功能。
 * 所有具体的属性配置类都应该继承此类以获得统一的属性管理能力。
 * </p>
 *
 * @author george
 */
@Data
public abstract class BaseProperties {
    /**
     * 是否生效
     * <p>
     * 控制当前属性配置是否生效，默认为true表示生效。
     * 当设置为false时，该配置将不会被应用。
     * </p>
     */
    private boolean enabled = true;
    /**
     * 扩展属性
     * <p>
     * 用于存储额外的属性配置，提供灵活的配置扩展能力。
     * 可以通过键值对的形式存储任意类型的扩展配置信息。
     * </p>
     */
    private Map<String, Object> extensions;
}

