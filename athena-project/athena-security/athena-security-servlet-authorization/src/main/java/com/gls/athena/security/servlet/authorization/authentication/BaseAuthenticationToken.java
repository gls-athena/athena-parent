package com.gls.athena.security.servlet.authorization.authentication;

import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Map;
import java.util.Set;

/**
 * OAuth2 基础认证令牌
 *
 * @author george
 */
@Getter
public abstract class BaseAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    /**
     * 作用域
     */
    private final Set<String> scopes;

    /**
     * 构造函数
     *
     * @param authorizationGrantType 授权类型
     * @param clientPrincipal        客户端主体
     * @param additionalParameters   附加参数
     * @param scopes                 作用域
     */
    public BaseAuthenticationToken(AuthorizationGrantType authorizationGrantType, Authentication clientPrincipal, Map<String, Object> additionalParameters, Set<String> scopes) {
        super(authorizationGrantType, clientPrincipal, additionalParameters);
        this.scopes = scopes;
    }
}
