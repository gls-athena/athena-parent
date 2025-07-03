package com.gls.athena.common.bean.security.jackson2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.common.bean.security.SocialUser;
import com.gls.athena.common.bean.security.User;

import java.util.Map;
import java.util.Optional;

/**
 * 社交用户反序列化器
 *
 * @author lizy19
 */
public class SocialUserDeserializer extends BaseDeserializer<SocialUser> {
    /**
     * 根据JSON数据创建SocialUser实例
     * 此方法用于解析表示社交用户信息的JSON数据，并将解析后的数据填充到SocialUser对象中
     * 它处理的JSON数据可能包含用户属性、用户名、注册ID、关联的用户对象以及绑定状态
     *
     * @param mapper ObjectMapper实例，用于将JSON数据映射到Java对象
     * @param node   包含社交用户信息的JsonNode节点
     * @return 返回一个填充了从JSON数据中提取的信息的SocialUser实例
     */
    @Override
    protected SocialUser createInstance(ObjectMapper mapper, JsonNode node) {
        SocialUser socialUser = new SocialUser();
        // 尝试从JSON数据中获取并设置socialUser的attributes属性
        Optional.ofNullable(node.get("attributes")).map(attributes -> mapper.convertValue(attributes, Map.class)).ifPresent(socialUser::setAttributes);
        // 尝试从JSON数据中获取并设置socialUser的name属性
        Optional.ofNullable(node.get("name")).map(JsonNode::asText).ifPresent(socialUser::setName);
        // 尝试从JSON数据中获取并设置socialUser的registrationId属性
        Optional.ofNullable(node.get("registrationId")).map(JsonNode::asText).ifPresent(socialUser::setRegistrationId);
        // 尝试从JSON数据中获取并设置socialUser的user属性
        Optional.ofNullable(node.get("user")).map(user -> mapper.convertValue(user, User.class)).ifPresent(socialUser::setUser);
        // 尝试从JSON数据中获取并设置socialUser的bindStatus属性
        Optional.ofNullable(node.get("bindStatus")).map(JsonNode::asBoolean).ifPresent(socialUser::setBindStatus);
        return socialUser;
    }
}
