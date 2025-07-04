package com.gls.athena.starter.word.generator.render.impl;

import com.gls.athena.starter.word.generator.render.WordElementRenderer;
import com.gls.athena.starter.word.generator.render.WordRenderContext;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.Collection;
import java.util.Map;

/**
 * 键值对渲染器
 *
 * @author athena
 */
public class KeyValueRenderer implements WordElementRenderer {

    @Override
    public void render(Object data, WordRenderContext context) {
        if (!(data instanceof Map)) {
            return;
        }

        Map<?, ?> map = (Map<?, ?>) data;
        if (map.isEmpty()) {
            return;
        }

        XWPFDocument document = context.getDocument();
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            // 如果值是集合类型，我们会用专门的渲染器处理，这里跳过
            if (value instanceof Collection && !((Collection<?>) value).isEmpty()) {
                continue;
            }

            run.setText(key + ": " + (value != null ? value.toString() : ""));
            run.addBreak();
        }
    }

    @Override
    public boolean supports(Object data, String path) {
        return data instanceof Map;
    }
}
