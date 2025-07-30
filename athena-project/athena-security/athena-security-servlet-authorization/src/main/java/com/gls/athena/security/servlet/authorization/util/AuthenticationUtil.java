package com.gls.athena.security.servlet.authorization.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;

/**
 * 授权工具类
 *
 * @author george
 */
@UtilityClass
public class AuthenticationUtil {
    /**
     * 获取认证客户端
     *
     * @param authentication 认证
     * @return 认证客户端
     */
    public OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }
        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        }
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
    }

    /**
     * 抛出异常
     *
     * @param errorCode     错误码
     * @param parameterName 参数名
     * @param errorUri      错误URI
     */
    public void throwError(String errorCode, String parameterName, String errorUri) {
        throw new OAuth2AuthenticationException(new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName, errorUri));
    }
}
