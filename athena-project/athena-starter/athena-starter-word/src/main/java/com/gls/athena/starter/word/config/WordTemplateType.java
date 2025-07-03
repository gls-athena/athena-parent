package com.gls.athena.starter.word.config;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 模板类型枚举
 *
 * @author george
 */

@Getter
@RequiredArgsConstructor
public enum WordTemplateType implements IEnum<String> {
    /**
     * HTML模板
     */
    HTML("html", "HTML模板"),
    /**
     * Word模板
     */
    DOCX("docx", "Word模板"),
    ;

    /**
     * 编码
     */
    private final String code;
    /**
     * 名称
     */
    private final String name;
}
