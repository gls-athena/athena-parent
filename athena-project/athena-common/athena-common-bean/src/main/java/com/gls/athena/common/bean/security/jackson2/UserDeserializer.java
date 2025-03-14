package com.gls.athena.common.bean.security.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.common.bean.security.Organization;
import com.gls.athena.common.bean.security.Role;
import com.gls.athena.common.bean.security.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 用户反序列化
 *
 * @author george
 */
public class UserDeserializer extends JsonDeserializer<User> {

    /**
     * 将JSON内容反序列化为User对象。
     * 该方法通过解析传入的JSON数据，将其转换为User对象，并设置User对象的各个属性。
     * 如果JSON中的某些字段为空，则对应的User对象属性不会被设置。
     *
     * @param parser  JsonParser JSON解析器，用于解析JSON数据
     * @param context DeserializationContext 反序列化上下文，提供反序列化过程中的上下文信息
     * @return User 反序列化后的User对象
     * @throws IOException 如果在解析JSON数据时发生IO异常，则抛出该异常
     */
    @Override
    public User deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        // 获取ObjectMapper实例，用于后续的JSON解析和转换
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        // 将JSON数据解析为JsonNode对象，便于后续的属性提取
        JsonNode node = mapper.readTree(parser);
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

        // 调用JacksonUtil工具类中的方法，反序列化BaseVo相关的属性
        JacksonUtil.deserializeBaseVo(mapper, node, user);
        return user;
    }

}
