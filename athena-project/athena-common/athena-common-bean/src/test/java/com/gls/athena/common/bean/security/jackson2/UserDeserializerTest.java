package com.gls.athena.common.bean.security.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.common.bean.security.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 测试类，用于验证 UserDeserializer 类的功能。
 */
class UserDeserializerTest {

    private UserDeserializer userDeserializer;
    private JsonParser mockParser;
    private DeserializationContext mockContext;
    private ObjectMapper mockMapper;
    private JsonNode mockNode;

    /**
     * 在每个测试方法执行前初始化测试环境。
     */
    @BeforeEach
    void setUp() {
        userDeserializer = new UserDeserializer();
        mockParser = mock(JsonParser.class);
        mockContext = mock(DeserializationContext.class);
        mockMapper = mock(ObjectMapper.class);
        mockNode = mock(JsonNode.class);
    }

    /**
     * 测试反序列化方法，当所有字段都存在时的场景。
     *
     * @throws IOException 如果反序列化过程中发生IO异常
     */
    @Test
    void testDeserialize_AllFieldsPresent() throws IOException {
        // 模拟 JsonParser 返回 ObjectMapper 和 JsonNode
        when(mockParser.getCodec()).thenReturn(mockMapper);
        when(mockMapper.readTree(mockParser)).thenReturn(mockNode);

        // 模拟 JsonNode 返回各个字段的值
        when(mockNode.get("username")).thenReturn(mock(JsonNode.class));
        when(mockNode.get("username").asText()).thenReturn("testUser");
        when(mockNode.get("password")).thenReturn(mock(JsonNode.class));
        when(mockNode.get("password").asText()).thenReturn("testPassword");
        when(mockNode.get("mobile")).thenReturn(mock(JsonNode.class));
        when(mockNode.get("mobile").asText()).thenReturn("123456789");
        when(mockNode.get("email")).thenReturn(mock(JsonNode.class));
        when(mockNode.get("email").asText()).thenReturn("test@example.com");
        when(mockNode.get("realName")).thenReturn(mock(JsonNode.class));
        when(mockNode.get("realName").asText()).thenReturn("Test User");
        when(mockNode.get("nickName")).thenReturn(mock(JsonNode.class));
        when(mockNode.get("nickName").asText()).thenReturn("TestNick");
        when(mockNode.get("avatar")).thenReturn(mock(JsonNode.class));
        when(mockNode.get("avatar").asText()).thenReturn("avatarUrl");
        when(mockNode.get("language")).thenReturn(mock(JsonNode.class));
        when(mockNode.get("language").asText()).thenReturn("en");
        when(mockNode.get("locale")).thenReturn(mock(JsonNode.class));
        when(mockNode.get("locale").asText()).thenReturn("US");
        when(mockNode.get("timeZone")).thenReturn(mock(JsonNode.class));
        when(mockNode.get("timeZone").asText()).thenReturn("UTC");

        // 模拟角色列表
        when(mockNode.get("roles")).thenReturn(mock(JsonNode.class));

        // 模拟组织列表
        when(mockNode.get("organizations")).thenReturn(mock(JsonNode.class));

        doReturn(mock(List.class)).when(mockMapper).convertValue(any(JsonNode.class), any(TypeReference.class));
        // 调用被测方法
        User user = userDeserializer.deserialize(mockParser, mockContext);

        // 验证结果
        assertEquals("testUser", user.getUsername());
        assertEquals("testPassword", user.getPassword());
        assertEquals("123456789", user.getMobile());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getRealName());
        assertEquals("TestNick", user.getNickName());
        assertEquals("avatarUrl", user.getAvatar());
        assertEquals("en", user.getLanguage());
        assertEquals("US", user.getLocale());
        assertEquals("UTC", user.getTimeZone());
        assertNotNull(user.getRoles());
        assertNotNull(user.getOrganizations());
    }

    /**
     * 测试反序列化方法，当部分字段缺失时的场景。
     *
     * @throws IOException 如果反序列化过程中发生IO异常
     */
    @Test
    void testDeserialize_SomeFieldsMissing() throws IOException {
        // 模拟 JsonParser 返回 ObjectMapper 和 JsonNode
        when(mockParser.getCodec()).thenReturn(mockMapper);
        when(mockMapper.readTree(mockParser)).thenReturn(mockNode);

        // 模拟 JsonNode 返回部分字段的值
        when(mockNode.get("username")).thenReturn(mock(JsonNode.class));
        when(mockNode.get("username").asText()).thenReturn("testUser");
        when(mockNode.get("email")).thenReturn(mock(JsonNode.class));
        when(mockNode.get("email").asText()).thenReturn("test@example.com");

        // 调用被测方法
        User user = userDeserializer.deserialize(mockParser, mockContext);

        // 验证结果
        assertEquals("testUser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getMobile());
        assertNull(user.getRealName());
        assertNull(user.getNickName());
        assertNull(user.getAvatar());
        assertNull(user.getLanguage());
        assertNull(user.getLocale());
        assertNull(user.getTimeZone());
        assertNull(user.getRoles());
        assertNull(user.getOrganizations());
    }

    /**
     * 测试反序列化方法，当角色列表为空时的场景。
     *
     * @throws IOException 如果反序列化过程中发生IO异常
     */
    @Test
    void testDeserialize_RolesEmpty() throws IOException {
        // 模拟 JsonParser 返回 ObjectMapper 和 JsonNode
        when(mockParser.getCodec()).thenReturn(mockMapper);
        when(mockMapper.readTree(mockParser)).thenReturn(mockNode);

        // 模拟 JsonNode 返回部分字段的值
        when(mockNode.get("username")).thenReturn(mock(JsonNode.class));
        when(mockNode.get("username").asText()).thenReturn("testUser");

        // 模拟角色列表为空
        when(mockNode.get("roles")).thenReturn(null);

        // 调用被测方法
        User user = userDeserializer.deserialize(mockParser, mockContext);

        // 验证结果
        assertEquals("testUser", user.getUsername());
        assertNull(user.getRoles());
    }

    /**
     * 测试反序列化方法，当组织列表为空时的场景。
     *
     * @throws IOException 如果反序列化过程中发生IO异常
     */
    @Test
    void testDeserialize_OrganizationsEmpty() throws IOException {
        // 模拟 JsonParser 返回 ObjectMapper 和 JsonNode
        when(mockParser.getCodec()).thenReturn(mockMapper);
        when(mockMapper.readTree(mockParser)).thenReturn(mockNode);

        // 模拟 JsonNode 返回部分字段的值
        when(mockNode.get("username")).thenReturn(mock(JsonNode.class));
        when(mockNode.get("username").asText()).thenReturn("testUser");

        // 模拟组织列表为空
        when(mockNode.get("organizations")).thenReturn(null);

        // 调用被测方法
        User user = userDeserializer.deserialize(mockParser, mockContext);

        // 验证结果
        assertEquals("testUser", user.getUsername());
        assertNull(user.getOrganizations());
    }
}
