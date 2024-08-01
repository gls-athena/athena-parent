package com.athena.security.authorization.jackson2;

import com.fasterxml.jackson.annotation.*;
import org.springframework.security.oauth2.core.OAuth2Token;

import java.util.Map;

/**
 * OAuth2 授权令牌混合
 *
 * @param <T> OAuth2Token类型
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OAuth2AuthorizationTokenMixin<T extends OAuth2Token> {

    @JsonCreator
    public OAuth2AuthorizationTokenMixin(@JsonProperty("token") T token,
                                         @JsonProperty("metadata") Map<String, Object> metadata) {
    }
}