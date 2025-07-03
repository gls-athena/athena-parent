package com.gls.athena.starter.word.support;

import com.gls.athena.starter.word.annotation.WordResponse;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Map;

/**
 * word处理帮助类
 *
 * @author lizy19
 */
@Component
public class WordHelper {
    public void handleDocxTemplate(Map<String, Object> data, OutputStream outputStream, WordResponse wordResponse) {
    }

    public void handleHtmlTemplate(Map<String, Object> data, OutputStream outputStream, WordResponse wordResponse) {

    }
}
