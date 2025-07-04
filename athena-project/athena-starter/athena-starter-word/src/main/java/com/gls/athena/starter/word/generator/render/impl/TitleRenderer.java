package com.gls.athena.starter.word.generator.render.impl;

import com.gls.athena.starter.word.generator.render.WordElementRenderer;
import com.gls.athena.starter.word.generator.render.WordRenderContext;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * 标题渲染器
 *
 * @author athena
 */
public class TitleRenderer implements WordElementRenderer {

    @Override
    public void render(Object data, WordRenderContext context) {
        String title = data.toString();
        int level = Math.min(context.getDepth() + 1, 3); // 根据深度确定标题级别，最大3级

        XWPFDocument document = context.getDocument();
        XWPFParagraph paragraph = document.createParagraph();

        // 根据级别设置对齐方式
        if (level == 1) {
            paragraph.setAlignment(ParagraphAlignment.CENTER);
        } else {
            paragraph.setAlignment(ParagraphAlignment.LEFT);
        }

        XWPFRun run = paragraph.createRun();
        run.setText(title);
        run.setBold(true);

        // 根据标题级别设置字体大小
        if (level == 1) {
            run.setFontSize(16);
        } else if (level == 2) {
            run.setFontSize(14);
        } else {
            run.setFontSize(12);
        }

        run.addBreak();
    }

    @Override
    public boolean supports(Object data, String path) {
        if (!(data instanceof String)) {
            return false;
        }

        // 对于根路径或指定为标题的数据，如果是字符串类型则支持渲染为标题
        return path.equals("/") ||
                path.endsWith("/title");
    }

    @Override
    public int getOrder() {
        return 10; // 标题应该优先渲染
    }
}
