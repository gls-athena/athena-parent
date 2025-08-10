package com.gls.athena.common.bean.security.jackson2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.common.bean.security.Permission;
import com.gls.athena.common.bean.security.Role;

import java.util.List;
import java.util.Optional;

/**
 * Role JSON反序列化器
 * <p>
 * 负责将JSON数据反序列化为Role对象，支持角色的所有字段及关联的权限列表
 *
 * @author george
 */
public class RoleDeserializer extends BaseDeserializer<Role> {

    /**
     * 创建Role实例并填充数据
     * <p>
     * 该方法从给定的JsonNode中提取角色相关字段，并将其设置到新创建的Role对象中。
     * 支持的基本字段包括：name、code、description、type、parentId、sort、defaultRole。
     * 同时也处理permissions字段，将其反序列化为Permission对象列表。
     *
     * @param mapper ObjectMapper实例，用于类型转换
     * @param node   包含角色数据的JsonNode节点
     * @return 填充完成的Role对象
     */
    @Override
    protected Role createInstance(ObjectMapper mapper, JsonNode node) {
        Role role = new Role();

        // 设置基本字段
        Optional.ofNullable(node.get("name")).map(JsonNode::asText).ifPresent(role::setName);
        Optional.ofNullable(node.get("code")).map(JsonNode::asText).ifPresent(role::setCode);
        Optional.ofNullable(node.get("description")).map(JsonNode::asText).ifPresent(role::setDescription);
        Optional.ofNullable(node.get("type")).map(JsonNode::asText).ifPresent(role::setType);
        Optional.ofNullable(node.get("parentId")).map(JsonNode::asLong).ifPresent(role::setParentId);
        Optional.ofNullable(node.get("sort")).map(JsonNode::asInt).ifPresent(role::setSort);
        Optional.ofNullable(node.get("defaultRole")).map(JsonNode::asBoolean).ifPresent(role::setDefaultRole);

        // 设置权限列表
        Optional.ofNullable(node.get("permissions"))
                .map(permissionsNode -> mapper.<List<Permission>>convertValue(permissionsNode, new TypeReference<>() {
                })).ifPresent(role::setPermissions);

        return role;
    }
}
