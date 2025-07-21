package com.gls.athena.starter.word.generator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.gls.athena.common.core.util.FileUtil;
import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.config.WordProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于模板的Word文档生成器
 *
 * @author athena
 */
@Slf4j
@Component
public class TemplateWordGenerator implements WordGenerator {

    @Resource
    private WordProperties wordProperties;

    /**
     * 基于docx4j实现，根据模板和数据生成Word文档。
     *
     * @param data         需要导出的数据对象
     * @param wordResponse Word导出注解信息，包含模板路径等配置
     * @param outputStream Word文档输出流
     * @throws Exception 生成或渲染过程中发生的异常
     */
    @Override
    public void generate(Object data, WordResponse wordResponse, OutputStream outputStream) throws Exception {
        Map<String, String> dataMap = convertData(data);
        try (InputStream templateStream = FileUtil.getInputStream(wordProperties.getTemplatePath(), wordResponse.template())) {
            // 加载docx模板
            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(templateStream);
            MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
            // 直接替换变量，避免中间Map
            mainDocumentPart.variableReplace(dataMap);
            // 输出到流
            wordPackage.save(outputStream);
        } catch (Docx4JException e) {
            if (log.isErrorEnabled()) {
                log.error("生成Word文档失败，模板路径：{}，数据类型：{}",
                        wordProperties.getTemplatePath(), data.getClass().getName(), e);
            }
            throw new Exception("生成Word文档失败", e);
        }
    }

    /**
     * 将给定的对象转换为字符串键值对的Map
     * 如果提供的数据为null，则返回一个空的Map
     * 此方法旨在处理可能不是Map形式的输入数据，通过将其属性转换为Map，以便于后续处理
     *
     * @param data 要转换的原始数据对象可以是任意类型，但方法专注于将其转换为Map
     * @return 包含字符串键值对的Map，表示转换后的数据如果输入为null，则返回空Map
     */
    private Map<String, String> convertData(Object data) {
        // 检查输入数据是否为null，如果是，则返回一个空的Map
        if (data == null) {
            return Collections.emptyMap();
        }
        // 使用BeanUtil将对象转换为Map，这一步是为了将对象的属性转换为键值对的形式
        Map<String, Object> beanMap = BeanUtil.beanToMap(data);
        // 初始化结果Map，用于存储最终的字符串键值对
        Map<String, String> resultMap = new HashMap<>();
        // 遍历beanMap，将所有键值对转换为字符串形式，并存储到resultMap中
        beanMap.forEach((key, value) -> {
            // 将值转换为字符串，确保所有数据都能以统一的格式处理
            String strValue = StrUtil.toString(value);
            resultMap.put(key, strValue);
        });
        // 返回转换后的Map
        return resultMap;
    }

    /**
     * 判断是否支持当前注解配置（即模板路径不为空）。
     *
     * @param wordResponse Word导出注解信息
     * @return 是否支持
     */
    @Override
    public boolean supports(WordResponse wordResponse) {
        return StrUtil.isNotBlank(wordResponse.template())
                && wordResponse.generator() == WordGenerator.class;
    }

}
