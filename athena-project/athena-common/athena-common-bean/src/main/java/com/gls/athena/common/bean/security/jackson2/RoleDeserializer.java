package com.gls.athena.common.bean.security.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.common.bean.security.Permission;
import com.gls.athena.common.bean.security.Role;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 角色反序列化
 *
 * @author george
 */
public class RoleDeserializer extends JsonDeserializer<Role> {
    /**
     * 将JSON内容反序列化为Role对象
     *
     * @param parser  JsonParser JSON解析器，用于解析JSON数据
     * @param context DeserializationContext 反序列化上下文，提供反序列化过程中的上下文信息
     * @return Role 反序列化后的角色对象
     * @throws IOException 当解析过程中发生IO异常时抛出
     */
    @Override
    public Role deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        // 获取ObjectMapper实例，用于处理JSON数据
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        // 将JSON数据解析为JsonNode对象，便于后续操作
        JsonNode node = mapper.readTree(parser);
        // 创建Role对象，用于存储反序列化后的数据
        Role role = new Role();

        // 从JsonNode中提取并设置Role对象的各个字段值
        Optional.ofNullable(node.get("name")).map(JsonNode::asText).ifPresent(role::setName);
        Optional.ofNullable(node.get("code")).map(JsonNode::asText).ifPresent(role::setCode);
        Optional.ofNullable(node.get("description")).map(JsonNode::asText).ifPresent(role::setDescription);
        Optional.ofNullable(node.get("type")).map(JsonNode::asText).ifPresent(role::setType);
        Optional.ofNullable(node.get("parentId")).map(JsonNode::asLong).ifPresent(role::setParentId);
        Optional.ofNullable(node.get("sort")).map(JsonNode::asInt).ifPresent(role::setSort);
        Optional.ofNullable(node.get("defaultRole")).map(JsonNode::asBoolean).ifPresent(role::setDefaultRole);

        // 从JsonNode中提取权限列表，并将其转换为List<Permission>类型后设置到Role对象中
        Optional.ofNullable(node.get("permissions"))
                .map(permissionsNode -> mapper.<List<Permission>>convertValue(permissionsNode, new TypeReference<>() {
                })).ifPresent(role::setPermissions);

        // 设置Role对象的基础实体字段值
        JacksonUtil.deserializeBaseVo(mapper, node, role);
        return role;
    }

}
