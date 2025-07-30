package com.gls.athena.security.servlet.authorization.authentication;

import com.gls.athena.security.servlet.authorization.config.IAuthorizationConstants;
import lombok.Getter;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Set;

/**
 * OAuth2 短信认证令牌
 *
 * @author george
 */
@Getter
public class SmsAuthenticationToken extends BaseAuthenticationToken {

    /**
     * 手机号
     */
    private final String mobile;

    /**
     * 构造函数
     *
     * @param clientPrincipal      客户端主体
     * @param additionalParameters 附加参数
     * @param scopes               作用域
     * @param mobile               手机号
     */
    public SmsAuthenticationToken(Authentication clientPrincipal, Map<String, Object> additionalParameters, Set<String> scopes, String mobile) {
        super(IAuthorizationConstants.SMS, clientPrincipal, additionalParameters, scopes);
        this.mobile = mobile;
    }
}
