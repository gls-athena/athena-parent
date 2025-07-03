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
    @Override
    protected SocialUser createInstance(ObjectMapper mapper, JsonNode node) {
        SocialUser socialUser = new SocialUser();
        Optional.ofNullable(node.get("attributes")).map(attributes -> mapper.convertValue(attributes, Map.class)).ifPresent(socialUser::setAttributes);
        Optional.ofNullable(node.get("name")).map(JsonNode::asText).ifPresent(socialUser::setName);
        Optional.ofNullable(node.get("registrationId")).map(JsonNode::asText).ifPresent(socialUser::setRegistrationId);
        Optional.ofNullable(node.get("user")).map(user -> mapper.convertValue(user, User.class)).ifPresent(socialUser::setUser);
        Optional.ofNullable(node.get("bindStatus")).map(JsonNode::asBoolean).ifPresent(socialUser::setBindStatus);
        return socialUser;
    }
}
