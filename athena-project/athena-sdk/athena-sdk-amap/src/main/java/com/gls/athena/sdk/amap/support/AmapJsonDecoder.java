package com.gls.athena.sdk.amap.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.stream.Collectors;

/**
 * 高德地图API响应JSON解码器
 * <p>
 * 该解码器主要用于处理高德地图API的JSON响应数据，具有以下特点：
 * 1. 将空数组([])转换为null值
 * 2. 使用Jackson进行JSON反序列化
 * 3. 支持自定义类型转换
 *
 * @author george
 */
@Slf4j
public class AmapJsonDecoder implements Decoder {
    /**
     * Jackson对象映射器
     * 用于将JSON字符串转换为Java对象
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 解码高德地图API的HTTP响应
     *
     * @param response Feign的HTTP响应对象
     * @param type     目标类型，用于JSON反序列化
     * @return 反序列化后的Java对象
     * @throws IOException 当读取响应体或JSON解析失败时抛出
     */
    @Override
    public Object decode(Response response, Type type) throws IOException {
        // 将响应体读取为字符串
        String body = convertInputStreamToString(response.body().asInputStream());
        log.debug("AmapJsonDecoder body: {}", body);
        // 替换空数组字符串为 null，处理高德地图API的特殊响应格式
        String modifiedBody = body.replaceAll("\\[\\s*\\]", "null");
        log.debug("AmapJsonDecoder modifiedBody: {}", modifiedBody);
        // 使用Jackson将处理后的JSON字符串反序列化为目标类型对象
        return objectMapper.readValue(modifiedBody, objectMapper.constructType(type));
    }

    /**
     * 将输入流转换为字符串
     * <p>
     * 使用BufferedReader和Stream API高效地读取输入流内容
     *
     * @param inputStream HTTP响应体输入流
     * @return 转换后的字符串内容
     * @throws IOException 当读取输入流失败时抛出
     */
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
