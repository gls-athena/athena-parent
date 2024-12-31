package com.gls.athena.sdk.amap.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.QueryMapEncoder;

import java.util.Map;

/**
 * JSON 查询映射编码器
 *
 * @author george
 */
public class JsonQueryMapEncoder implements QueryMapEncoder {
    /**
     * 对象映射器
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 编码对象
     *
     * @param object 对象
     * @return Map
     */
    @Override
    public Map<String, Object> encode(Object object) {
        try {
            // 序列化为 JSON 字符串
            String json = objectMapper.writeValueAsString(object);
            // 反序列化为 Map
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
