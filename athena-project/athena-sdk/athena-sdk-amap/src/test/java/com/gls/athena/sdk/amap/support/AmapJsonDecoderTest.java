package com.gls.athena.sdk.amap.support;

import feign.Response;
import feign.Response.Body;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmapJsonDecoderTest {

    private AmapJsonDecoder decoder;

    @Mock
    private Response response;

    @Mock
    private Body responseBody;

    @BeforeEach
    void setUp() {
        decoder = new AmapJsonDecoder();
    }

    /**
     * 测试正常解码流程
     * 验证：
     * 1. 空数组替换逻辑正确性
     * 2. 反序列化结果正确性
     */
    @Test
    void shouldDecodeNormalResponse() throws Exception {
        // 构造测试数据
        String originalJson = "{\"data\":[],\"code\":200}";

        // 模拟响应对象
        when(response.body()).thenReturn(responseBody);
        when(responseBody.asInputStream())
                .thenReturn(new ByteArrayInputStream(originalJson.getBytes()));

        // 执行解码
        TestResult result = (TestResult) decoder.decode(response, TestResult.class);

        // 验证结果
        assertNotNull(result);
        assertNull(result.getData());
        assertEquals(200, result.getCode());
    }

    /**
     * 测试多个空数组替换场景
     * 验证所有空数组都被正确替换
     */
    @Test
    void shouldReplaceMultipleEmptyArrays() throws Exception {
        String original = "{\"arr1\":[], \"arr2\":[   ], \"nested\":{\"arr3\":[]}}";

        when(response.body()).thenReturn(responseBody);
        when(responseBody.asInputStream())
                .thenReturn(new ByteArrayInputStream(original.getBytes()));

        TestResult result = (TestResult) decoder.decode(response, TestResult.class);

        assertNull(result.getArr1());
        assertNull(result.getArr2());
        assertNull(result.getNested().getArr3());
    }

    /**
     * 测试非空数组不被错误替换
     * 验证正则表达式不会匹配非空数组
     */
    @Test
    void shouldNotReplaceNonEmptyArrays() throws Exception {
        String original = "{\"data\":[1,2,3]}";

        when(response.body()).thenReturn(responseBody);
        when(responseBody.asInputStream())
                .thenReturn(new ByteArrayInputStream(original.getBytes()));

        TestResult result = (TestResult) decoder.decode(response, TestResult.class);

        assertNotNull(result.getData());
        assertEquals(3, result.getData().size());
    }

    /**
     * 测试输入流读取异常场景
     * 验证IOException正确抛出
     */
    @Test
    void shouldThrowIOExceptionWhenReadFails() throws Exception {
        InputStream brokenStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Simulated read error");
            }
        };

        when(response.body()).thenReturn(responseBody);
        when(responseBody.asInputStream()).thenReturn(brokenStream);

        assertThrows(IOException.class, () ->
                decoder.decode(response, TestResult.class));
    }

    /**
     * 测试JSON解析异常场景
     * 验证无效JSON会抛出IOException
     */
    @Test
    void shouldThrowOnInvalidJson() throws Exception {
        String invalidJson = "{corrupted: json}";

        when(response.body()).thenReturn(responseBody);
        when(responseBody.asInputStream())
                .thenReturn(new ByteArrayInputStream(invalidJson.getBytes()));

        assertThrows(IOException.class, () ->
                decoder.decode(response, TestResult.class));
    }

    // 测试用数据模型
    @Data
    static class TestResult {
        private List<Object> data;
        private int code;
        private Object arr1;
        private Object arr2;
        private Nested nested;

    }

    @Data
    static class Nested {
        private Object arr3;
    }
}
