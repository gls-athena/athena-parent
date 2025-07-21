package com.gls.athena.starter.word.generator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.gls.athena.common.core.util.FileUtil;
import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.config.WordProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * 基于POI-TL的模板Word生成器
 *
 * @author athena
 */
@Slf4j
@Component
public class TemplateWordGenerator implements WordGenerator {

    @Resource
    private WordProperties wordProperties;

    /**
     * 根据模板和数据生成Word文档，支持自定义POI-TL配置。
     *
     * @param data         需要导出的数据对象
     * @param wordResponse Word导出注解信息，包含模板路径等配置
     * @param outputStream Word文档输出流
     * @throws Exception 生成或渲染过程中发生的异常
     */
    @Override
    public void generate(Object data, WordResponse wordResponse, OutputStream outputStream) throws Exception {

        Map<String, Object> dataMap = BeanUtil.beanToMap(data);
        Configure configure = Configure.builder()
                .useSpringEL(false)
                .build();
        try (InputStream templateStream = FileUtil.getInputStream(wordProperties.getTemplatePath(), wordResponse.template());
             XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateStream, configure)) {
            xwpfTemplate.render(dataMap);
            xwpfTemplate.write(outputStream);
        } catch (Exception e) {
            log.error("生成Word文档失败", e);
            throw new RuntimeException("生成Word文档失败", e);
        }
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
