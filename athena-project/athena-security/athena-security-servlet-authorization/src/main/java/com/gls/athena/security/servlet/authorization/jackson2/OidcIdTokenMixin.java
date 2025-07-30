package com.gls.athena.security.servlet.authorization.jackson2;

import com.fasterxml.jackson.annotation.*;

import java.time.Instant;
import java.util.Map;

/**
 * OIDC ID 令牌混合
 *
 * @author george
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OidcIdTokenMixin {

    /**
     * 构造函数
     *
     * @param tokenValue 令牌值
     * @param issuedAt   发布时间
     * @param expiresAt  过期时间
     * @param claims     声明
     */
    @JsonCreator
    public OidcIdTokenMixin(@JsonProperty("tokenValue") String tokenValue,
                            @JsonProperty("issuedAt") Instant issuedAt,
                            @JsonProperty("expiresAt") Instant expiresAt,
                            @JsonProperty("claims") Map<String, Object> claims) {
    }
}
