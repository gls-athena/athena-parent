package com.gls.athena.security.servlet.authorization.jackson2;

import com.fasterxml.jackson.annotation.*;
import org.springframework.security.oauth2.core.OAuth2Token;

import java.util.Map;

/**
 * OAuth2 授权令牌混合
 *
 * @param <T> OAuth2Token类型
 * @author george
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationTokenMixin<T extends OAuth2Token> {

    /**
     * 构造函数
     *
     * @param token    令牌
     * @param metadata 元数据
     */
    @JsonCreator
    public AuthorizationTokenMixin(@JsonProperty("token") T token,
                                   @JsonProperty("metadata") Map<String, Object> metadata) {
    }
}
