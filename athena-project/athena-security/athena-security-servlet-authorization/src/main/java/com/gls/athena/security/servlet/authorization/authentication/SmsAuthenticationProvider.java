package com.gls.athena.security.servlet.authorization.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

/**
 * OAuth2 短信认证提供者
 *
 * @author george
 */
@Slf4j
public class SmsAuthenticationProvider extends BaseAuthenticationProvider {
    /**
     * 用户服务
     */
    private final UserDetailsService userDetailsService;

    /**
     * 构造函数
     *
     * @param authorizationService 认证服务
     * @param tokenGenerator       令牌生成器
     * @param userDetailsService   用户服务
     */
    public SmsAuthenticationProvider(OAuth2AuthorizationService authorizationService,
                                     OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
                                     UserDetailsService userDetailsService) {
        super(authorizationService, tokenGenerator);
        this.userDetailsService = userDetailsService;
    }

    /**
     * 获取用户名密码认证令牌
     *
     * @param baseAuthenticationToken 基础认证令牌
     * @return 用户名密码认证令牌
     */
    @Override
    public UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(BaseAuthenticationToken baseAuthenticationToken) {
        SmsAuthenticationToken token = (SmsAuthenticationToken) baseAuthenticationToken;
        UserDetails userDetails = userDetailsService.loadUserByUsername(token.getMobile());
        checkUser(userDetails);
        return UsernamePasswordAuthenticationToken.authenticated(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    /**
     * 检查用户
     *
     * @param userDetails 用户详情
     */
    private void checkUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new BadCredentialsException("用户不存在");
        }
    }

    /**
     * 是否支持
     *
     * @param authentication 认证
     * @return 是否支持
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
