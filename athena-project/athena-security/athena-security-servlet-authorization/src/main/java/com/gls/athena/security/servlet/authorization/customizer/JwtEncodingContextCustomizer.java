package com.gls.athena.security.servlet.authorization.customizer;

import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

/**
 * jwt自定义
 *
 * @author george
 */
@Component
public class JwtEncodingContextCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    /**
     * 自定义jwt
     *
     * @param context jwt上下文
     */
    @Override
    public void customize(JwtEncodingContext context) {
    }
}
