package com.gls.athena.common.bean.security.jackson2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.common.bean.security.SocialUser;
import com.gls.athena.common.bean.security.User;

import java.util.Map;
import java.util.Optional;

/**
 * 社交用户反序列化器
 * <p>
 * 用于将JSON数据反序列化为{@link SocialUser}对象，支持解析社交登录用户的相关信息
 *
 * @author lizy19
 * @since 1.0
 */
public class SocialUserDeserializer extends BaseDeserializer<SocialUser> {

    /**
     * 从JSON节点创建SocialUser实例
     * <p>
     * 解析JSON数据并构建完整的社交用户对象，包括用户属性、用户名、注册ID、关联用户和绑定状态
     *
     * @param mapper JSON对象映射器
     * @param node   包含社交用户数据的JSON节点
     * @return 构建完成的SocialUser实例
     */
    @Override
    protected SocialUser createInstance(ObjectMapper mapper, JsonNode node) {
        SocialUser socialUser = new SocialUser();

        // 设置用户属性映射
        Optional.ofNullable(node.get("attributes"))
                .map(attributes -> mapper.convertValue(attributes, Map.class))
                .ifPresent(socialUser::setAttributes);

        // 设置用户名
        Optional.ofNullable(node.get("name"))
                .map(JsonNode::asText)
                .ifPresent(socialUser::setName);

        // 设置社交平台注册ID
        Optional.ofNullable(node.get("registrationId"))
                .map(JsonNode::asText)
                .ifPresent(socialUser::setRegistrationId);

        // 设置关联的系统用户
        Optional.ofNullable(node.get("user"))
                .map(user -> mapper.convertValue(user, User.class))
                .ifPresent(socialUser::setUser);

        // 设置账号绑定状态
        Optional.ofNullable(node.get("bindStatus"))
                .map(JsonNode::asBoolean)
                .ifPresent(socialUser::setBindStatus);

        return socialUser;
    }
}
