package com.gls.athena.common.bean.security.jackson2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.common.bean.security.Permission;
import com.gls.athena.common.bean.security.Role;

import java.util.List;
import java.util.Optional;

/**
 * 角色反序列化
 *
 * @author george
 */
public class RoleDeserializer extends BaseDeserializer<Role> {
    /**
     * 重写createInstance方法，用于将JsonNode数据反序列化为Role对象
     * 此方法选择性地处理和设置角色的各个字段，确保数据的完整性和一致性
     *
     * @param mapper ObjectMapper实例，用于转换Json数据
     * @param node   包含角色信息的JsonNode
     * @return 返回一个新创建的Role实例，其字段根据node中的数据进行设置
     */
    @Override
    protected Role createInstance(ObjectMapper mapper, JsonNode node) {
        // 创建Role对象，用于存储反序列化后的数据
        Role role = new Role();

        // 从JsonNode中提取并设置Role对象的各个字段值
        // 使用Optional处理可能为空的字段，避免空指针异常
        Optional.ofNullable(node.get("name")).map(JsonNode::asText).ifPresent(role::setName);
        Optional.ofNullable(node.get("code")).map(JsonNode::asText).ifPresent(role::setCode);
        Optional.ofNullable(node.get("description")).map(JsonNode::asText).ifPresent(role::setDescription);
        Optional.ofNullable(node.get("type")).map(JsonNode::asText).ifPresent(role::setType);
        Optional.ofNullable(node.get("parentId")).map(JsonNode::asLong).ifPresent(role::setParentId);
        Optional.ofNullable(node.get("sort")).map(JsonNode::asInt).ifPresent(role::setSort);
        Optional.ofNullable(node.get("defaultRole")).map(JsonNode::asBoolean).ifPresent(role::setDefaultRole);

        // 从JsonNode中提取权限列表，并将其转换为List<Permission>类型后设置到Role对象中
        // 使用Optional和map方法链式处理，确保转换过程的安全性和简洁性
        Optional.ofNullable(node.get("permissions"))
                .map(permissionsNode -> mapper.<List<Permission>>convertValue(permissionsNode, new TypeReference<>() {
                })).ifPresent(role::setPermissions);

        // 返回初始化完毕的Role对象
        return role;
    }

}
