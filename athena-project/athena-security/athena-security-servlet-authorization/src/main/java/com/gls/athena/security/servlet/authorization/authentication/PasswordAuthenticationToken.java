package com.gls.athena.security.servlet.authorization.authentication;

import com.gls.athena.security.servlet.authorization.config.IAuthorizationConstants;
import lombok.Getter;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Set;

/**
 * OAuth2 密码认证令牌
 *
 * @author george
 */
@Getter
public class PasswordAuthenticationToken extends BaseAuthenticationToken {
    /**
     * 用户名
     */
    private final String username;
    /**
     * 密码
     */
    private final String password;

    /**
     * 构造函数
     *
     * @param clientPrincipal      客户端主体
     * @param additionalParameters 附加参数
     * @param scopes               作用域
     * @param username             用户名
     * @param password             密码
     */
    public PasswordAuthenticationToken(Authentication clientPrincipal, Map<String, Object> additionalParameters, Set<String> scopes, String username, String password) {
        super(IAuthorizationConstants.PASSWORD, clientPrincipal, additionalParameters, scopes);
        this.username = username;
        this.password = password;
    }
}
