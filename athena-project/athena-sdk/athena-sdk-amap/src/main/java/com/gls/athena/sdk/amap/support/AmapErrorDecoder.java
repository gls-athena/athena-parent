package com.gls.athena.sdk.amap.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.sdk.amap.exception.AmapException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 高德地图API错误解码器
 * <p>
 * 该解码器用于解析高德地图API返回的错误响应，并将其转换为具体的异常类型。
 * 根据高德地图的错误码和错误信息，生成相应的业务异常。
 *
 * @author george
 */
@Slf4j
public class AmapErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String errorBody = getErrorBody(response);
            log.error("Amap API error response: {}", errorBody);

            JsonNode jsonNode = objectMapper.readTree(errorBody);
            String infocode = jsonNode.path("infocode").asText("");
            String info = jsonNode.path("info").asText("");

            // 使用统一的异常类
            return new AmapException(infocode, info, methodKey);

        } catch (Exception e) {
            log.error("Failed to decode amap error response", e);
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }

    /**
     * 获取错误响应体内容
     */
    private String getErrorBody(Response response) throws IOException {
        if (response.body() == null) {
            return "{}";
        }

        try (InputStream inputStream = response.body().asInputStream()) {
            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}
