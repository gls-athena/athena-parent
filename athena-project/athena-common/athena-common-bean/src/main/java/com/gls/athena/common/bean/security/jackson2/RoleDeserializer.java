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
     * @param parser  JsonParser JSON解析器
     * @param context DeserializationContext 上下文
     * @return Role 角色对象
     * @throws IOException IO异常
     */
    @Override
    public Role deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        // 获取ObjectMapper
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        // 获取JsonNode
        JsonNode node = mapper.readTree(parser);
        // 创建Role对象
        Role role = new Role();
        // 名称
        Optional.ofNullable(node.get("name")).map(JsonNode::asText).ifPresent(role::setName);
        // 编码
        Optional.ofNullable(node.get("code")).map(JsonNode::asText).ifPresent(role::setCode);
        // 描述
        Optional.ofNullable(node.get("description")).map(JsonNode::asText).ifPresent(role::setDescription);
        // 类型
        Optional.ofNullable(node.get("type")).map(JsonNode::asText).ifPresent(role::setType);
        // 父角色ID
        Optional.ofNullable(node.get("parentId")).map(JsonNode::asLong).ifPresent(role::setParentId);
        // 排序
        Optional.ofNullable(node.get("sort")).map(JsonNode::asInt).ifPresent(role::setSort);
        // 是否默认角色
        Optional.ofNullable(node.get("defaultRole")).map(JsonNode::asBoolean).ifPresent(role::setDefaultRole);
        // 权限列表
        Optional.ofNullable(node.get("permissions"))
                .map(permissionsNode -> mapper.<List<Permission>>convertValue(permissionsNode, new TypeReference<>() {
                })).ifPresent(role::setPermissions);

        // 设置基础实体字段值
        JacksonUtil.deserializeBaseVo(mapper, node, role);
        return role;
    }
}
