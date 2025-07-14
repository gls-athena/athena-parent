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
 * User对象的Jackson反序列化器
 * <p>
 * 继承自BaseDeserializer，负责将JSON数据反序列化为User对象。
 * 支持用户基本信息（用户名、密码、邮箱等）以及关联对象（角色、组织）的反序列化。
 * </p>
 *
 * @author george
 * @see BaseDeserializer
 * @see User
 * @since 1.0
 */
public class UserDeserializer extends BaseDeserializer<User> {

    /**
     * 创建并初始化User实例
     * <p>
     * 从JsonNode中解析用户数据并构建User对象，包括：
     * <ul>
     *   <li>基本信息：用户名、密码、手机号、邮箱、真实姓名、昵称、头像</li>
     *   <li>偏好设置：语言、地区、时区</li>
     *   <li>关联对象：角色列表、组织列表</li>
     * </ul>
     * </p>
     *
     * @param mapper ObjectMapper实例，用于JSON到Java对象的转换
     * @param node   包含用户信息的JsonNode对象
     * @return 构建完成的User实例
     * @throws RuntimeException 当JSON结构不符合预期或转换失败时抛出
     */
    @Override
    protected User createInstance(ObjectMapper mapper, JsonNode node) {
        User user = new User();

        // 设置用户基本信息
        Optional.ofNullable(node.get("username")).map(JsonNode::asText).ifPresent(user::setUsername);
        Optional.ofNullable(node.get("password")).map(JsonNode::asText).ifPresent(user::setPassword);
        Optional.ofNullable(node.get("mobile")).map(JsonNode::asText).ifPresent(user::setMobile);
        Optional.ofNullable(node.get("email")).map(JsonNode::asText).ifPresent(user::setEmail);
        Optional.ofNullable(node.get("realName")).map(JsonNode::asText).ifPresent(user::setRealName);
        Optional.ofNullable(node.get("nickName")).map(JsonNode::asText).ifPresent(user::setNickName);
        Optional.ofNullable(node.get("avatar")).map(JsonNode::asText).ifPresent(user::setAvatar);

        // 设置用户偏好配置
        Optional.ofNullable(node.get("language")).map(JsonNode::asText).ifPresent(user::setLanguage);
        Optional.ofNullable(node.get("locale")).map(JsonNode::asText).ifPresent(user::setLocale);
        Optional.ofNullable(node.get("timeZone")).map(JsonNode::asText).ifPresent(user::setTimeZone);

        // 设置用户角色列表
        Optional.ofNullable(node.get("roles"))
                .map(rolesNode -> mapper.<List<Role>>convertValue(rolesNode, new TypeReference<>() {
                })).ifPresent(user::setRoles);

        // 设置用户所属组织列表
        Optional.ofNullable(node.get("organizations"))
                .map(organizationsNode -> mapper.<List<Organization>>convertValue(organizationsNode, new TypeReference<>() {
                })).ifPresent(user::setOrganizations);

        return user;
    }

}
