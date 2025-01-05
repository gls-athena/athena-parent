package com.gls.athena.common.core.constant;

import lombok.Data;

import java.util.Map;

/**
 * 基础属性
 *
 * @author george
 */
@Data
public abstract class BaseProperties {
    /**
     * 是否生效
     */
    private boolean enabled = true;
    /**
     * 扩展属性
     */
    private Map<String, Object> extensions;
}
