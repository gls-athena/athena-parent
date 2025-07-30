package com.gls.athena.security.servlet.authorization.customizer;

import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

/**
 * 授权令牌自定义
 *
 * @author george
 */
@Component
public class TokenClaimsContextCustomizer implements OAuth2TokenCustomizer<OAuth2TokenClaimsContext> {

    /**
     * 自定义令牌
     *
     * @param context 令牌上下文
     */
    @Override
    public void customize(OAuth2TokenClaimsContext context) {
    }
}
