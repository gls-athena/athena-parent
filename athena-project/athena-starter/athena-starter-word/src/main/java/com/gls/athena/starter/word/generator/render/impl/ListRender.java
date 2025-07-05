package com.gls.athena.starter.word.generator.render.impl;

import com.gls.athena.starter.word.generator.render.WordElementRender;
import com.gls.athena.starter.word.generator.render.WordRenderContext;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 列表渲染器
 *
 * @author athena
 */
@Component
public class ListRender implements WordElementRender {

    @Override
    public void render(Object data, WordRenderContext context) {
        if (!(data instanceof Collection)) {
            return;
        }

        Collection<?> collection = (Collection<?>) data;
        if (collection.isEmpty()) {
            return;
        }

        XWPFDocument document = context.getDocument();
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();

        for (Object item : collection) {
            run.setText("- " + (item != null ? item.toString() : ""));
            run.addBreak();
        }
    }

    @Override
    public boolean supports(Object data, String path) {
        if (!(data instanceof Collection)) {
            return false;
        }

        Collection<?> collection = (Collection<?>) data;
        if (collection.isEmpty()) {
            return true; // 空集合也支持
        }

        // 获取第一个元素，判断是否为简单类型
        Object firstItem = collection.iterator().next();
        return firstItem == null ||
                firstItem instanceof String ||
                firstItem instanceof Number ||
                firstItem instanceof Boolean ||
                firstItem instanceof Character;
    }
}
