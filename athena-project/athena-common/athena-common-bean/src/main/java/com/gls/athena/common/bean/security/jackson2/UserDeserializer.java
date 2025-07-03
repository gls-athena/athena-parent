package com.gls.athena.common.bean.security.jackson2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.common.bean.security.Organization;
import com.gls.athena.common.bean.security.Role;
import com.gls.athena.common.bean.security.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户反序列化
 *
 * @author george
 */
public class UserDeserializer extends BaseDeserializer<User> {

    /**
     * 使用ObjectMapper和JsonNode创建并初始化一个User实例
     * 该方法通过解析JsonNode中的属性来设置User对象的属性，包括用户名、密码、手机号等基本信息，
     * 以及角色和组织等复杂信息
     *
     * @param mapper ObjectMapper实例，用于转换JsonNode到Java对象
     * @param node   包含User信息的JsonNode
     * @return 返回一个新创建并初始化的User实例
     */
    @Override
    protected User createInstance(ObjectMapper mapper, JsonNode node) {
        User user = new User();

        // 从JsonNode中提取并设置User对象的各个属性
        Optional.ofNullable(node.get("username")).map(JsonNode::asText).ifPresent(user::setUsername);
        Optional.ofNullable(node.get("password")).map(JsonNode::asText).ifPresent(user::setPassword);
        Optional.ofNullable(node.get("mobile")).map(JsonNode::asText).ifPresent(user::setMobile);
        Optional.ofNullable(node.get("email")).map(JsonNode::asText).ifPresent(user::setEmail);
        Optional.ofNullable(node.get("realName")).map(JsonNode::asText).ifPresent(user::setRealName);
        Optional.ofNullable(node.get("nickName")).map(JsonNode::asText).ifPresent(user::setNickName);
        Optional.ofNullable(node.get("avatar")).map(JsonNode::asText).ifPresent(user::setAvatar);
        Optional.ofNullable(node.get("language")).map(JsonNode::asText).ifPresent(user::setLanguage);
        Optional.ofNullable(node.get("locale")).map(JsonNode::asText).ifPresent(user::setLocale);
        Optional.ofNullable(node.get("timeZone")).map(JsonNode::asText).ifPresent(user::setTimeZone);

        // 处理角色列表，将其转换为List<Role>并设置到User对象中
        Optional.ofNullable(node.get("roles"))
                .map(rolesNode -> mapper.<List<Role>>convertValue(rolesNode, new TypeReference<>() {
                })).ifPresent(user::setRoles);

        // 处理组织列表，将其转换为List<Organization>并设置到User对象中
        Optional.ofNullable(node.get("organizations"))
                .map(organizationsNode -> mapper.<List<Organization>>convertValue(organizationsNode, new TypeReference<>() {
                })).ifPresent(user::setOrganizations);
        return user;
    }

}
