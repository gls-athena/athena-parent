package com.gls.athena.security.servlet.authorization.config;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

/**
 * 授权常量
 *
 * @author george
 */
public interface IAuthorizationConstants {
    /**
     * 密码授权
     */
    AuthorizationGrantType PASSWORD = new AuthorizationGrantType("password");
    /**
     * 短信授权
     */
    AuthorizationGrantType SMS = new AuthorizationGrantType("sms");
    /**
     * 手机号
     */
    String MOBILE = "mobile";
    /**
     * ID_TOKEN
     */
    OAuth2TokenType ID_TOKEN_TOKEN_TYPE = new OAuth2TokenType(OidcParameterNames.ID_TOKEN);
    /**
     * 错误URI
     */
    String ERROR_URI = "https://tools.ietf.org/html/rfc6749#section-4.3.2";
}
