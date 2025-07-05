package com.gls.athena.starter.word.processor;

import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.generator.WordDocumentGeneratorFactory;
import jakarta.annotation.Resource;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Word响应处理器
 *
 * @author athena
 */
@Component
public class WordResponseProcessor implements HandlerMethodReturnValueHandler {

    @Resource
    private WordDocumentGeneratorFactory generatorFactory;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(WordResponse.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 标记请求已处理
        mavContainer.setRequestHandled(true);

        // 获取注解
        WordResponse wordResponse = returnType.getMethodAnnotation(WordResponse.class);
        if (wordResponse == null) {
            return;
        }

        // 处理文件名
        String fileName = wordResponse.fileName();
        if (fileName.isEmpty()) {
            fileName = "word-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        }
        if (!fileName.toLowerCase().endsWith(".docx")) {
            fileName += ".docx";
        }

        // 使用工厂获取适当的生成器并生成Word文档
        XWPFDocument document = generatorFactory.getGenerator(returnValue, wordResponse)
                .generate(returnValue, wordResponse);

        // 输出文档到字节流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);
        document.close();

        // 创建响应
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +
                URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray()));

        ResponseEntity<InputStreamResource> responseEntity = ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

        // 设置为响应体
        webRequest.setAttribute("WORD_RESPONSE_ENTITY", responseEntity, NativeWebRequest.SCOPE_REQUEST);
    }
}
