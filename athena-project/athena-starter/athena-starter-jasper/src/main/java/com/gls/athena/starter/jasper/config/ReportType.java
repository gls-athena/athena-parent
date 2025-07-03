package com.gls.athena.starter.jasper.config;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author george
 */

@Getter
@RequiredArgsConstructor
public enum ReportType implements IEnum<String> {
    /**
     * HTML
     */
    HTML("html", "HTML模板", ".html", "text/html"),
    /**
     * PDF
     */
    PDF("pdf", "PDF模板", ".jasper", "application/pdf"),
    /**
     * DOCX
     */
    DOCX("docx", "DOCX模板", ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    ;

    /**
     * 编码
     */
    private final String code;
    /**
     * 名称
     */
    private final String name;
    /**
     * 文件扩展名
     */
    private final String extension;
    /**
     * 内容类型
     */
    private final String contentType;
}
