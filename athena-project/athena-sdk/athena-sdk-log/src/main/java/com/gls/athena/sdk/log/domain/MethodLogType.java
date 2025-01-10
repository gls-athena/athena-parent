package com.gls.athena.sdk.log.domain;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author george
 */
@Getter
@RequiredArgsConstructor
public enum MethodLogType implements IEnum<Integer> {

    /**
     * 正常日志
     */
    NORMAL(1, "正常"),
    /**
     * 异常日志
     */
    ERROR(2, "异常"),
    ;

    private final Integer code;

    private final String name;
}
