package com.gls.athena.common.bean.security.jackson2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.common.bean.base.BaseVo;
import lombok.experimental.UtilityClass;

import java.util.Date;
import java.util.Optional;

/**
 * Jackson工具类
 *
 * @author george
 */
@UtilityClass
public class JacksonUtil {

    /**
     * 反序列化基础实体
     *
     * @param mapper ObjectMapper用于JSON解析
     * @param node   JsonNode包含要解析的数据
     * @param baseVo 基础实体对象，将从JsonNode中获取数据并设置到该对象中
     */
    public void deserializeBaseVo(ObjectMapper mapper, JsonNode node, BaseVo baseVo) {
        // 从JsonNode中提取字段值并分配给BaseVo对象相应的属性

        // 提取主键ID
        Optional.ofNullable(node.get("id")).map(JsonNode::asLong).ifPresent(baseVo::setId);
        // 提取租户ID
        Optional.ofNullable(node.get("tenantId")).map(JsonNode::asLong).ifPresent(baseVo::setTenantId);
        // 提取版本号
        Optional.ofNullable(node.get("version")).map(JsonNode::asInt).ifPresent(baseVo::setVersion);
        // 提取删除标记
        Optional.ofNullable(node.get("deleted")).map(JsonNode::asBoolean).ifPresent(baseVo::setDeleted);
        // 提取创建人ID
        Optional.ofNullable(node.get("createUserId")).map(JsonNode::asLong).ifPresent(baseVo::setCreateUserId);
        // 提取创建人姓名
        Optional.ofNullable(node.get("createUserName")).map(JsonNode::asText).ifPresent(baseVo::setCreateUserName);
        // 提取创建时间
        Optional.ofNullable(node.get("createTime")).map(date -> mapper.convertValue(date, Date.class))
                .ifPresent(baseVo::setCreateTime);
        // 提取更新人ID
        Optional.ofNullable(node.get("updateUserId")).map(JsonNode::asLong).ifPresent(baseVo::setUpdateUserId);
        // 提取更新人姓名
        Optional.ofNullable(node.get("updateUserName")).map(JsonNode::asText).ifPresent(baseVo::setUpdateUserName);
        // 提取更新时间
        Optional.ofNullable(node.get("updateTime")).map(date -> mapper.convertValue(date, Date.class))
                .ifPresent(baseVo::setUpdateTime);

    }
}
