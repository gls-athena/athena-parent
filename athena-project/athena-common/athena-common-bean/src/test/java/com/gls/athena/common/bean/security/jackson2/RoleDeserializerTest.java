package com.gls.athena.common.bean.security.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.common.bean.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RoleDeserializerTest {

    @Mock
    private JsonParser parser;

    @Mock
    private DeserializationContext context;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private JsonNode node;

    private RoleDeserializer roleDeserializer;

    /**
     * 在每个测试方法执行之前进行初始化操作。
     * 该方法主要用于初始化 Mockito 注解，并创建 RoleDeserializer 实例，以便在后续的测试中使用。
     *
     * @throws Exception 如果在初始化过程中发生任何异常，将抛出该异常。
     */
    @BeforeEach
    public void setUp() throws Exception {
        // 初始化 Mockito 注解，并创建 RoleDeserializer 实例
        MockitoAnnotations.openMocks(this).close();
        roleDeserializer = new RoleDeserializer();
    }

    /**
     * 测试反序列化器在所有字段都存在的情况下的处理能力。
     * 该测试用例模拟了一个包含所有字段的 JSON 数据，并验证反序列化器是否能够正确地将 JSON 数据转换为目标对象。
     *
     * @throws IOException 如果 JSON 解析过程中发生 I/O 错误
     */
    @Test
    public void testDeserialize_AllFieldsPresent() throws IOException {
        // 模拟 JSON 解析器返回包含所有字段的 JSON 节点
        when(parser.getCodec()).thenReturn(mapper);
        when(mapper.readTree(parser)).thenReturn(node);
        when(node.get("name")).thenReturn(mock(JsonNode.class));
        when(node.get("code")).thenReturn(mock(JsonNode.class));
        when(node.get("description")).thenReturn(mock(JsonNode.class));
        when(node.get("type")).thenReturn(mock(JsonNode.class));
        when(node.get("parentId")).thenReturn(mock(JsonNode.class));
        when(node.get("sort")).thenReturn(mock(JsonNode.class));
        when(node.get("defaultRole")).thenReturn(mock(JsonNode.class));
        when(node.get("permissions")).thenReturn(mock(JsonNode.class));

        // 为每个字段设置模拟值，确保反序列化后的对象包含正确的值
        when(node.get("name").asText()).thenReturn("Admin");
        when(node.get("code").asText()).thenReturn("ADMIN");
        when(node.get("description").asText()).thenReturn("Administrator role");
        when(node.get("type").asText()).thenReturn("SYSTEM");
        when(node.get("parentId").asLong()).thenReturn(1L);
        when(node.get("sort").asInt()).thenReturn(1);
        when(node.get("defaultRole").asBoolean()).thenReturn(true);
        // 使用 doReturn 避免严格模式下的参数匹配问题，模拟权限列表的转换
        doReturn(mock(List.class)).when(mapper).convertValue(any(JsonNode.class), any(TypeReference.class));

        // 执行反序列化操作，验证反序列化器能够正确处理完整数据
        Role role = roleDeserializer.deserialize(parser, context);

        // 验证反序列化后的对象包含所有字段且值正确，确保数据完整性
        assertNotNull(role);
        assertEquals("Admin", role.getName());
        assertEquals("ADMIN", role.getCode());
        assertEquals("Administrator role", role.getDescription());
        assertEquals("SYSTEM", role.getType());
        assertEquals(Long.valueOf(1L), role.getParentId());
        assertEquals(Integer.valueOf(1), role.getSort());
        assertTrue(role.getDefaultRole());
        assertNotNull(role.getPermissions());
    }

    /**
     * 测试反序列化器在部分字段缺失情况下的处理能力。
     * 该测试用例模拟 JSON 数据中部分字段缺失的场景，验证反序列化器能够正确处理不完整的数据结构，
     * 并确保反序列化后的对象包含正确的字段值，缺失字段为 null。
     *
     * @throws IOException 如果反序列化过程中发生 I/O 错误
     */
    @Test
    public void testDeserialize_SomeFieldsMissing() throws IOException {
        // 模拟 JSON 解析器返回部分字段缺失的 JSON 节点
        when(parser.getCodec()).thenReturn(mapper);
        when(mapper.readTree(parser)).thenReturn(node);
        when(node.get("name")).thenReturn(mock(JsonNode.class));
        when(node.get("code")).thenReturn(mock(JsonNode.class));
        when(node.get("description")).thenReturn(null);
        when(node.get("type")).thenReturn(null);
        when(node.get("parentId")).thenReturn(null);
        when(node.get("sort")).thenReturn(null);
        when(node.get("defaultRole")).thenReturn(null);
        when(node.get("permissions")).thenReturn(null);

        // 模拟存在字段的值，确保反序列化后的对象包含正确的值
        when(node.get("name").asText()).thenReturn("User");
        when(node.get("code").asText()).thenReturn("USER");

        // 执行反序列化操作，验证反序列化器能够正确处理部分缺失的数据
        Role role = roleDeserializer.deserialize(parser, context);

        // 验证反序列化后的对象包含存在的字段且缺失字段为 null，确保数据处理的正确性
        assertNotNull(role);
        assertEquals("User", role.getName());
        assertEquals("USER", role.getCode());
        assertNull(role.getDescription());
        assertNull(role.getType());
        assertNull(role.getParentId());
        assertNull(role.getSort());
        assertNull(role.getDefaultRole());
        assertNull(role.getPermissions());
    }

    /**
     * 测试反序列化器在 JSON 数据为空时的处理能力。
     * 该测试用例模拟了 JSON 数据为空的情况，验证反序列化器是否能够正确处理空数据，
     * 并确保反序列化后的对象所有字段均为 null。
     *
     * @throws IOException 如果反序列化过程中发生 I/O 错误
     */
    @Test
    public void testDeserialize_EmptyJson() throws IOException {
        // 模拟 JSON 数据为空，确保反序列化器能够正确处理空数据
        // 设置 mock 行为，模拟 JSON 解析器返回空的 JSON 节点
        when(parser.getCodec()).thenReturn(mapper);
        when(mapper.readTree(parser)).thenReturn(node);
        when(node.get("name")).thenReturn(null);
        when(node.get("code")).thenReturn(null);
        when(node.get("description")).thenReturn(null);
        when(node.get("type")).thenReturn(null);
        when(node.get("parentId")).thenReturn(null);
        when(node.get("sort")).thenReturn(null);
        when(node.get("defaultRole")).thenReturn(null);
        when(node.get("permissions")).thenReturn(null);

        // 执行反序列化操作，验证反序列化器能够正确处理空数据
        Role role = roleDeserializer.deserialize(parser, context);

        // 验证反序列化后的对象所有字段为 null，确保空数据处理的正确性
        assertNotNull(role);
        assertNull(role.getName());
        assertNull(role.getCode());
        assertNull(role.getDescription());
        assertNull(role.getType());
        assertNull(role.getParentId());
        assertNull(role.getSort());
        assertNull(role.getDefaultRole());
        assertNull(role.getPermissions());
    }

    /**
     * 测试反序列化器在遇到无效 JSON 数据时的异常处理能力。
     * 该测试用例模拟 JSON 数据格式错误的情况，确保反序列化器能够正确处理并抛出预期的异常。
     *
     * @throws IOException 当反序列化过程中发生 IO 异常时抛出
     */
    @Test
    public void testDeserialize_InvalidJson() throws IOException {
        // 模拟 JSON 数据格式错误，确保反序列化器能够正确处理异常情况
        // 设置 mock 行为，模拟 JSON 解析器抛出异常
        when(parser.getCodec()).thenReturn(mapper);
        when(mapper.readTree(parser)).thenThrow(new JsonProcessingException("Invalid JSON") {
        });

        // 验证反序列化操作抛出 IOException 异常，确保异常处理的正确性
        IOException exception = assertThrows(IOException.class, () -> {
            roleDeserializer.deserialize(parser, context);
        });
        assertEquals("Invalid JSON", exception.getMessage());
    }
}
