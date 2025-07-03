package com.gls.athena.starter.word.converter.impl;

import com.gls.athena.starter.word.converter.HtmlTagConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.nodes.Element;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 图片标签(img)转换器
 *
 * @author lizy19
 */
@Slf4j
@Component
public class ImageConverter implements HtmlTagConverter {

    private static final List<String> SUPPORTED_TAGS = Collections.singletonList("img");

    // 默认图片宽高
    private static final int DEFAULT_IMAGE_WIDTH = 300;
    private static final int DEFAULT_IMAGE_HEIGHT = 200;

    // 图片类型映射
    private static final Map<String, Integer> IMAGE_TYPE_MAPPING = new HashMap<>();

    static {
        // 初始化图片类型映射
        IMAGE_TYPE_MAPPING.put("png", org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG);
        IMAGE_TYPE_MAPPING.put("jpg", org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG);
        IMAGE_TYPE_MAPPING.put("jpeg", org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG);
        IMAGE_TYPE_MAPPING.put("gif", org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_GIF);
        IMAGE_TYPE_MAPPING.put("bmp", org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_BMP);
        IMAGE_TYPE_MAPPING.put("tif", org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_TIFF);
        IMAGE_TYPE_MAPPING.put("tiff", org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_TIFF);

        // MIME类型映射
        IMAGE_TYPE_MAPPING.put("image/png", org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG);
        IMAGE_TYPE_MAPPING.put("image/jpeg", org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG);
        IMAGE_TYPE_MAPPING.put("image/jpg", org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG);
        IMAGE_TYPE_MAPPING.put("image/gif", org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_GIF);
        IMAGE_TYPE_MAPPING.put("image/bmp", org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_BMP);
        IMAGE_TYPE_MAPPING.put("image/tiff", org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_TIFF);
    }

    @Override
    public boolean supports(String tagName) {
        return SUPPORTED_TAGS.contains(tagName);
    }

    @Override
    public void convert(Element element, XWPFDocument document) {
        if (element == null) {
            return;
        }

        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER); // 图片居中显示
        XWPFRun run = paragraph.createRun();

        // 获取图片源路径
        String src = element.attr("src");
        if (src.isEmpty()) {
            run.setText("[图片: " + getAltText(element) + "]");
            return;
        }

        try {
            // 从图片元素获取宽高
            int width = getImageWidth(element);
            int height = getImageHeight(element);
            String altText = getAltText(element);

            // 尝试处理图片
            if (src.startsWith("data:image")) {
                // 处理Base64编码的图片
                processBase64Image(src, run, altText, width, height);
            } else if (src.startsWith("http://") || src.startsWith("https://")) {
                // 处理网络图片
                processUrlImage(src, run, altText, width, height);
            } else {
                // 处理本地图片
                processLocalImage(src, run, altText, width, height);
            }
        } catch (Exception e) {
            log.error("插入图片失败: {}", src, e);
            run.setText("[图片处理失败: " + getAltText(element) + "]");
        }
    }

    /**
     * 获取图片的替代文本
     *
     * @param imgElement 图片元素
     * @return 替代文本
     */
    private String getAltText(Element imgElement) {
        return Optional.ofNullable(imgElement.attr("alt"))
                .filter(alt -> !alt.isEmpty())
                .orElse("无描述");
    }

    /**
     * 获取图片宽度
     *
     * @param imgElement 图片元素
     * @return 图片宽度
     */
    private int getImageWidth(Element imgElement) {
        // 首先尝试从width属性获取
        String widthAttr = imgElement.attr("width");
        if (!widthAttr.isEmpty()) {
            try {
                // 处理可能的单位，如px
                widthAttr = widthAttr.replaceAll("[^0-9]", "");
                return Integer.parseInt(widthAttr);
            } catch (NumberFormatException e) {
                log.warn("无效的图片宽度属性: {}", widthAttr);
            }
        }

        // 尝试从样式中获取
        String style = imgElement.attr("style");
        if (!style.isEmpty()) {
            Pattern widthPattern = Pattern.compile("width:\\s*(\\d+)px");
            Matcher widthMatcher = widthPattern.matcher(style);
            if (widthMatcher.find()) {
                try {
                    return Integer.parseInt(widthMatcher.group(1));
                } catch (NumberFormatException e) {
                    log.warn("���效的样式宽度: {}", widthMatcher.group(1));
                }
            }
        }

        // 使用默认值
        return DEFAULT_IMAGE_WIDTH;
    }

    /**
     * 获取图片高度
     *
     * @param imgElement 图片元素
     * @return 图片高度
     */
    private int getImageHeight(Element imgElement) {
        // 首先尝试从height属性获取
        String heightAttr = imgElement.attr("height");
        if (!heightAttr.isEmpty()) {
            try {
                // 处理可能的单位，如px
                heightAttr = heightAttr.replaceAll("[^0-9]", "");
                return Integer.parseInt(heightAttr);
            } catch (NumberFormatException e) {
                log.warn("无效的图片高度属性: {}", heightAttr);
            }
        }

        // 尝试从样式中获取
        String style = imgElement.attr("style");
        if (!style.isEmpty()) {
            Pattern heightPattern = Pattern.compile("height:\\s*(\\d+)px");
            Matcher heightMatcher = heightPattern.matcher(style);
            if (heightMatcher.find()) {
                try {
                    return Integer.parseInt(heightMatcher.group(1));
                } catch (NumberFormatException e) {
                    log.warn("无效的样式高度: {}", heightMatcher.group(1));
                }
            }
        }

        // 使用默认值
        return DEFAULT_IMAGE_HEIGHT;
    }

    /**
     * 处理Base64编码的图片
     *
     * @param src     Base64编码的图片数据
     * @param run     文档运行对象
     * @param altText 图片替代文本
     * @param width   图片宽度
     * @param height  图片高度
     */
    private void processBase64Image(String src, XWPFRun run, String altText, int width, int height) {
        try {
            // 解析Base64图片数据
            String base64Data = src.substring(src.indexOf(",") + 1);
            byte[] imageData = Base64.getDecoder().decode(base64Data);

            // 确定图片类型
            int pictureType = getPictureTypeFromBase64(src);

            // 添加图片到文档
            run.addPicture(
                    new java.io.ByteArrayInputStream(imageData),
                    pictureType,
                    altText,
                    Units.toEMU(width),
                    Units.toEMU(height)
            );
        } catch (Exception e) {
            log.error("处理Base64图片失败", e);
            run.setText("[Base64图片: " + altText + "]");
        }
    }

    /**
     * 从Base64图片数据中获取图片类型
     *
     * @param base64Image Base64编码的图片数据
     * @return POI中定义的图片类型常量
     */
    private int getPictureTypeFromBase64(String base64Image) {
        String mimeType = base64Image.substring(base64Image.indexOf("data:") + 5, base64Image.indexOf(";"));
        return IMAGE_TYPE_MAPPING.getOrDefault(mimeType.toLowerCase(), org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG);
    }

    /**
     * 处理网络图片
     *
     * @param src     图片URL
     * @param run     文档运行对象
     * @param altText 图片替代文本
     * @param width   图片宽度
     * @param height  图片高度
     */
    private void processUrlImage(String src, XWPFRun run, String altText, int width, int height) {
        try (InputStream imageStream = new java.net.URL(src).openStream()) {
            // 确定图片类型
            int pictureType = getPictureTypeFromUrl(src);

            // 添加图片到文档
            run.addPicture(
                    imageStream,
                    pictureType,
                    altText,
                    Units.toEMU(width),
                    Units.toEMU(height)
            );
        } catch (Exception e) {
            log.error("处理网络图片失败: {}", src, e);
            run.setText("[网络图片: " + altText + "]");
        }
    }

    /**
     * 处理本地图片
     *
     * @param src     图片路径
     * @param run     文档运行对象
     * @param altText 图片替代文本
     * @param width   图片宽度
     * @param height  图片高度
     */
    private void processLocalImage(String src, XWPFRun run, String altText, int width, int height) {
        try {
            // 尝试作为类路径资源加载
            ClassPathResource resource = new ClassPathResource(src);
            if (!resource.exists()) {
                // 如果类路径资源不存在，尝试作为文件系统路径加载
                java.nio.file.Path path = Paths.get(src);
                if (!Files.exists(path)) {
                    run.setText("[未找到图片: " + altText + "]");
                    return;
                }

                try (InputStream is = Files.newInputStream(path)) {
                    addPictureToRun(is, src, run, altText, width, height);
                }
            } else {
                // 作为类路径资源加载
                try (InputStream is = resource.getInputStream()) {
                    addPictureToRun(is, src, run, altText, width, height);
                }
            }
        } catch (Exception e) {
            log.error("处理本地图片失败: {}", src, e);
            run.setText("[本地图片: " + altText + "]");
        }
    }

    /**
     * 将图片添加到文档运行对象中
     *
     * @param is       图片输入流
     * @param filename 文件名
     * @param run      文档运行对象
     * @param altText  图片替代文本
     * @param width    图片宽度
     * @param height   图片高度
     * @throws Exception 如果添加图片过程中发生异常
     */
    private void addPictureToRun(InputStream is, String filename, XWPFRun run, String altText, int width, int height) throws Exception {
        // 确定图片类型
        int pictureType = getPictureTypeFromFilename(filename);

        // 添加图片到文档
        run.addPicture(
                is,
                pictureType,
                altText,
                Units.toEMU(width),
                Units.toEMU(height)
        );
    }

    /**
     * 从URL中获取图片类型
     *
     * @param url 图片URL
     * @return POI中定义的图片类型常量
     */
    private int getPictureTypeFromUrl(String url) {
        return getPictureTypeFromFilename(url);
    }

    /**
     * 从文件名获取图片类型
     *
     * @param filename 文件名
     * @return POI中定义的图片类型常量
     */
    private int getPictureTypeFromFilename(String filename) {
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i + 1).toLowerCase();
        }

        return IMAGE_TYPE_MAPPING.getOrDefault(extension, org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG);
    }
}
