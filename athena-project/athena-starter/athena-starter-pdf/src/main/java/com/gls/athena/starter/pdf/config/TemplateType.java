package com.gls.athena.starter.pdf.config;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author george
 */

@Getter
@RequiredArgsConstructor
public enum TemplateType implements IEnum<String> {
    /**
     * HTML模板
     */
    HTML("html", "HTML模板"),
    /**
     * PDF模板
     */
    PDF("pdf", "PDF模板"),
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
