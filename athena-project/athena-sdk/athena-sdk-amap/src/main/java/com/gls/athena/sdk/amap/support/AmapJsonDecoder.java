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
     * <p>
     * 该方法负责将Feign的HTTP响应对象解码为指定的Java对象。首先将响应体读取为字符串，
     * 然后处理高德地图API返回的特殊空数组格式，最后使用Jackson库将处理后的JSON字符串
     * 反序列化为目标类型的Java对象。
     *
     * @param response Feign的HTTP响应对象，包含从高德地图API获取的原始数据
     * @param type     目标类型，用于指定JSON反序列化后的Java对象类型
     * @return 反序列化后的Java对象，类型由参数type指定
     * @throws IOException 当读取响应体或JSON解析失败时抛出
     */
    @Override
    public Object decode(Response response, Type type) throws IOException {
        // 将响应体读取为字符串，便于后续处理
        String body = convertInputStreamToString(response.body().asInputStream());
        log.debug("AmapJsonDecoder body: {}", body);

        // 处理高德地图API返回的特殊空数组格式，将其替换为null
        String modifiedBody = body.replaceAll("\\[\\s*\\]", "null");
        log.debug("AmapJsonDecoder modifiedBody: {}", modifiedBody);

        // 使用Jackson库将处理后的JSON字符串反序列化为目标类型对象
        return objectMapper.readValue(modifiedBody, objectMapper.constructType(type));
    }

    /**
     * 将输入流转换为字符串
     * <p>
     * 该方法使用BufferedReader和Stream API高效地读取输入流内容，并将其转换为字符串。
     * 通过使用try-with-resources语句，确保BufferedReader在使用完毕后自动关闭，避免资源泄漏。
     *
     * @param inputStream HTTP响应体输入流，需要被转换为字符串的输入流
     * @return 转换后的字符串内容，包含输入流中的所有数据
     * @throws IOException 当读取输入流失败时抛出，可能是由于输入流不可读或读取过程中发生错误
     */
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        // 使用BufferedReader和Stream API读取输入流内容，并将其转换为字符串
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            // 使用Stream API将输入流中的每一行连接成一个字符串，行与行之间使用系统默认的行分隔符分隔
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

}
