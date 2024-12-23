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

/**
 * 用户反序列化
 *
 * @author george
 */
public class UserDeserializer extends JsonDeserializer<User> {

    /**
     * 将JSON内容反序列化为User对象
     *
     * @param parser  JsonParser JSON解析器
     * @param context DeserializationContext 上下文
     * @return User 用户对象
     * @throws IOException IO异常
     */
    @Override
    public User deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        // 获取ObjectMapper
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        // 获取JsonNode
        JsonNode node = mapper.readTree(parser);

        // 用户名
        String username = node.get("username").asText();
        // 密码
        String password = node.get("password").asText();
        // 手机号
        String mobile = node.get("mobile").asText();
        // 邮箱
        String email = node.get("email").asText();
        // 真实姓名
        String realName = node.get("realName").asText();
        // 昵称
        String nickName = node.get("nickName").asText();
        // 头像
        String avatar = node.get("avatar").asText();
        // 语言
        String language = node.get("language").asText();
        // 区域
        String locale = node.get("locale").asText();
        // 时区
        String timeZone = node.get("timeZone").asText();
        // 角色列表
        List<Role> roles = mapper.convertValue(node.get("roles"), new TypeReference<>() {
        });
        // 组织列表
        List<Organization> organizations = mapper.convertValue(node.get("organizations"), new TypeReference<>() {
        });

        // 构建User对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setMobile(mobile);
        user.setEmail(email);
        user.setRealName(realName);
        user.setNickName(nickName);
        user.setAvatar(avatar);
        user.setLanguage(language);
        user.setLocale(locale);
        user.setTimeZone(timeZone);
        user.setRoles(roles);
        user.setOrganizations(organizations);
        JacksonUtil.deserializeBaseVo(mapper, node, user);
        return user;
    }
}
