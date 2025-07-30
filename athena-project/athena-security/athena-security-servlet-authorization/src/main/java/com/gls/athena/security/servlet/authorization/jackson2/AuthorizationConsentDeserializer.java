package com.gls.athena.security.servlet.authorization.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;

import java.io.IOException;
import java.util.Set;

/**
 * OAuth2AuthorizationConsent 反序列化器
 *
 * @author george
 */
public class AuthorizationConsentDeserializer extends JsonDeserializer<OAuth2AuthorizationConsent> {

    /**
     * 反序列化 OAuth2AuthorizationConsent 对象
     *
     * @param parser  JsonParser 对象
     * @param context DeserializationContext 对象
     * @return OAuth2AuthorizationConsent 对象
     * @throws IOException IO异常
     */
    @Override
    public OAuth2AuthorizationConsent deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        // 获取ObjectMapper
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        // 获取JsonNode
        JsonNode node = mapper.readTree(parser);
        // 获取JsonNode中的字段值
        // 注册的客户端ID
        String registeredClientId = node.get("registeredClientId").asText();
        // 主体名称
        String principalName = node.get("principalName").asText();
        // authorities
        Set<? extends GrantedAuthority> authorities = mapper.convertValue(node.get("authorities"), new TypeReference<>() {
        });
        // 构建OAuth2AuthorizationConsent对象
        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(registeredClientId, principalName);
        // 设置authorities
        authorities.forEach(builder::authority);
        // 返回OAuth2AuthorizationConsent对象
        return builder.build();
    }

}
