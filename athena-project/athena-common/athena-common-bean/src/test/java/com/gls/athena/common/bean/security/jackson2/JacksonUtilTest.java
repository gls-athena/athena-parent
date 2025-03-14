package com.gls.athena.common.bean.security.jackson2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gls.athena.common.bean.base.BaseVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JacksonUtilTest {

    private ObjectMapper mapper;
    private JsonNodeFactory nodeFactory;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        nodeFactory = JsonNodeFactory.instance;
    }

    /**
     * 测试当所有字段都存在时，反序列化BaseVo对象应正确设置所有字段。
     */
    @Test
    public void deserializeBaseVo_AllFieldsPresent_ShouldSetAllFields() {
        ObjectNode jsonNode = nodeFactory.objectNode();
        jsonNode.put("id", 1L);
        jsonNode.put("tenantId", 100L);
        jsonNode.put("version", 1);
        jsonNode.put("deleted", false);
        jsonNode.put("createUserId", 2L);
        jsonNode.put("createUserName", "John Doe");
        jsonNode.put("createTime", new Date().getTime());
        jsonNode.put("updateUserId", 3L);
        jsonNode.put("updateUserName", "Jane Doe");
        jsonNode.put("updateTime", new Date().getTime());

        BaseVo baseVo = new BaseVo() {
        };
        JacksonUtil.deserializeBaseVo(mapper, jsonNode, baseVo);

        assertEquals(1L, baseVo.getId());
        assertEquals(100L, baseVo.getTenantId());
        assertEquals(1, baseVo.getVersion());
        assertFalse(baseVo.getDeleted());
        assertEquals(2L, baseVo.getCreateUserId());
        assertEquals("John Doe", baseVo.getCreateUserName());
        assertNotNull(baseVo.getCreateTime());
        assertEquals(3L, baseVo.getUpdateUserId());
        assertEquals("Jane Doe", baseVo.getUpdateUserName());
        assertNotNull(baseVo.getUpdateTime());
    }

    /**
     * 测试当某些字段缺失时，反序列化BaseVo对象应设置默认值（null）。
     */
    @Test
    public void deserializeBaseVo_MissingFields_ShouldSetDefaults() {
        ObjectNode jsonNode = nodeFactory.objectNode();
        jsonNode.put("id", 1L);
        jsonNode.put("tenantId", 100L);

        BaseVo baseVo = new BaseVo() {
        };
        JacksonUtil.deserializeBaseVo(mapper, jsonNode, baseVo);

        assertEquals(1L, baseVo.getId());
        assertEquals(100L, baseVo.getTenantId());
        assertNull(baseVo.getVersion());
        assertNull(baseVo.getDeleted());
        assertNull(baseVo.getCreateUserId());
        assertNull(baseVo.getCreateUserName());
        assertNull(baseVo.getCreateTime());
        assertNull(baseVo.getUpdateUserId());
        assertNull(baseVo.getUpdateUserName());
        assertNull(baseVo.getUpdateTime());
    }

    /**
     * 测试当字段数据类型无效时，反序列化BaseVo对象应抛出IllegalArgumentException异常。
     */
    @Test
    public void deserializeBaseVo_InvalidDataTypes_ShouldThrowException() {
        ObjectNode jsonNode = nodeFactory.objectNode();
        jsonNode.put("id", 1L);
        jsonNode.put("tenantId", 100L);
        jsonNode.put("createTime", "not a date");

        BaseVo baseVo = new BaseVo() {
        };
        assertThrows(IllegalArgumentException.class, () -> {
            JacksonUtil.deserializeBaseVo(mapper, jsonNode, baseVo);
        });
    }
}
