package com.gls.athena.common.bean.security.jackson2;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.common.bean.base.BaseVo;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

/**
 * BaseDeserializer 是一个抽象类，用于反序列化 JSON 数据为 BaseVo 类型的对象。
 *
 * @author lizy19
 */
public abstract class BaseDeserializer<T extends BaseVo> extends JsonDeserializer<T> {
    /**
     * 从JsonParser中反序列化对象
     * 该方法覆盖了父类的deserialize方法，用于将JSON数据解析为指定类型T的对象
     * 主要解析JSON中的通用字段，并将它们设置到实例对象中
     *
     * @param parser JsonParser实例，用于解析JSON数据
     * @param ctxt   DeserializationContext实例，提供反序列化上下文
     * @return 返回解析后的对象实例
     * @throws IOException      如果在读取JSON数据时发生I/O错误
     * @throws JacksonException 如果解析JSON数据时发生错误
     */
    @Override
    public T deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
        // 获取ObjectMapper实例，用于JSON数据的读取和解析
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        // 读取并解析JSON数据为JsonNode对象，便于后续访问和处理
        JsonNode node = mapper.readTree(parser);
        // 创建泛型类的实例对象
        T baseVo = createInstance(mapper, node);

        // 以下代码块用于解析JSON中的通用字段，并将它们设置到实例对象中
        // 使用Optional来处理可能为空的字段，避免空指针异常

        // 解析并设置id字段
        Optional.ofNullable(node.get("id")).map(JsonNode::asLong).ifPresent(baseVo::setId);
        // 解析并设置tenantId字段
        Optional.ofNullable(node.get("tenantId")).map(JsonNode::asLong).ifPresent(baseVo::setTenantId);
        // 解析并设置version字段
        Optional.ofNullable(node.get("version")).map(JsonNode::asInt).ifPresent(baseVo::setVersion);
        // 解析并设置deleted字段
        Optional.ofNullable(node.get("deleted")).map(JsonNode::asBoolean).ifPresent(baseVo::setDeleted);
        // 解析并设置createUserId字段
        Optional.ofNullable(node.get("createUserId")).map(JsonNode::asLong).ifPresent(baseVo::setCreateUserId);
        // 解析并设置createUserName字段
        Optional.ofNullable(node.get("createUserName")).map(JsonNode::asText).ifPresent(baseVo::setCreateUserName);
        // 解析并设置createTime字段，需要将JsonNode转换为Date类型
        Optional.ofNullable(node.get("createTime")).map(date -> mapper.convertValue(date, Date.class)).ifPresent(baseVo::setCreateTime);
        // 解析并设置updateUserId字段
        Optional.ofNullable(node.get("updateUserId")).map(JsonNode::asLong).ifPresent(baseVo::setUpdateUserId);
        // 解析并设置updateUserName字段
        Optional.ofNullable(node.get("updateUserName")).map(JsonNode::asText).ifPresent(baseVo::setUpdateUserName);
        // 解析并设置updateTime字段，需要将JsonNode转换为Date类型
        Optional.ofNullable(node.get("updateTime")).map(date -> mapper.convertValue(date, Date.class)).ifPresent(baseVo::setUpdateTime);

        // 返回填充好数据的实例对象
        return baseVo;
    }

    /**
     * 创建实例对象
     *
     * @param mapper ObjectMapper实例，用于JSON数据的读取和解析
     * @param node   JsonNode对象，用于解析JSON数据
     * @return 返回创建的实例对象
     */
    protected abstract T createInstance(ObjectMapper mapper, JsonNode node);

}
