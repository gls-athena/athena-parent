package com.gls.athena.sdk.amap.support;

import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

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
        String body = getBody(response);
        log.debug("AmapJsonDecoder body: {}", body);

        // 处理高德地图API返回的特殊空数组格式，将其替换为null
        String modifiedBody = body.replaceAll("\\[\\s*\\]", "null");
        log.debug("AmapJsonDecoder modifiedBody: {}", modifiedBody);

        // 使用Jackson库将处理后的JSON字符串反序列化为目标类型对象
        return objectMapper.readValue(modifiedBody, objectMapper.constructType(type));
    }

    /**
     * 从HTTP响应中获取响应体内容并转换为字符串
     *
     * @param response HTTP响应对象，包含需要读取的响应体
     * @return 响应体的字符串形式，使用UTF-8编码解析
     * @throws IOException 当读取响应体内容过程中发生I/O异常时抛出
     */
    private String getBody(Response response) throws IOException {
        try {
            // 将响应体转换为输入流，并通过工具类读取流内容
            InputStream inputStream = response.body().asInputStream();
            return IoUtil.read(inputStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 统一将异常封装为IOException抛出
            throw new IOException(e);
        }
    }

}
