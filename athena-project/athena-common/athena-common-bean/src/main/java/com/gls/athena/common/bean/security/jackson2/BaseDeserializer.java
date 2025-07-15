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
 * JSON反序列化器基类，用于将JSON数据反序列化为BaseVo类型的对象
 * <p>
 * 该抽象类实现了通用字段的自动解析，包括：
 * <ul>
 *   <li>基础字段：id、tenantId、version、deleted</li>
 *   <li>审计字段：createUserId、createUserName、createTime、updateUserId、updateUserName、updateTime</li>
 * </ul>
 *
 * @param <T> 继承自BaseVo的目标类型
 * @author george
 */
public abstract class BaseDeserializer<T extends BaseVo> extends JsonDeserializer<T> {

    /**
     * 反序列化JSON数据为指定类型的对象
     *
     * @param parser JSON解析器
     * @param ctxt   反序列化上下文
     * @return 反序列化后的对象实例
     * @throws IOException      IO异常
     * @throws JacksonException JSON解析异常
     */
    @Override
    public T deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode node = mapper.readTree(parser);
        T baseVo = createInstance(mapper, node);

        // 解析基础字段
        Optional.ofNullable(node.get("id")).map(JsonNode::asLong).ifPresent(baseVo::setId);
        Optional.ofNullable(node.get("tenantId")).map(JsonNode::asLong).ifPresent(baseVo::setTenantId);
        Optional.ofNullable(node.get("version")).map(JsonNode::asInt).ifPresent(baseVo::setVersion);
        Optional.ofNullable(node.get("deleted")).map(JsonNode::asBoolean).ifPresent(baseVo::setDeleted);

        // 解析创建相关审计字段
        Optional.ofNullable(node.get("createUserId")).map(JsonNode::asLong).ifPresent(baseVo::setCreateUserId);
        Optional.ofNullable(node.get("createUserName")).map(JsonNode::asText).ifPresent(baseVo::setCreateUserName);
        Optional.ofNullable(node.get("createTime")).map(date -> mapper.convertValue(date, Date.class)).ifPresent(baseVo::setCreateTime);

        // 解析更新相关审计字段
        Optional.ofNullable(node.get("updateUserId")).map(JsonNode::asLong).ifPresent(baseVo::setUpdateUserId);
        Optional.ofNullable(node.get("updateUserName")).map(JsonNode::asText).ifPresent(baseVo::setUpdateUserName);
        Optional.ofNullable(node.get("updateTime")).map(date -> mapper.convertValue(date, Date.class)).ifPresent(baseVo::setUpdateTime);

        return baseVo;
    }

    /**
     * 创建目标类型的实例对象
     * <p>
     * 子类需要实现此方法来创建具体的对象实例
     *
     * @param mapper ObjectMapper实例
     * @param node   JSON节点
     * @return 创建的实例对象
     */
    protected abstract T createInstance(ObjectMapper mapper, JsonNode node);
}
