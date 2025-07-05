package com.gls.athena.starter.word.generator.impl;

import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.generator.WordDocumentGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

/**
 * 简化版Word文档生成器
 * 专注于处理复杂的List数据结构
 *
 * @author athena
 */
@Slf4j
@Component
public class DefaultWordDocumentGenerator implements WordDocumentGenerator {

    @Override
    public XWPFDocument generate(Object data, WordResponse wordResponse) {
        return null;
    }

    @Override
    public boolean supports(Class<?> dataClass) {
        return false;
    }
}
