package com.gls.athena.security.servlet.authorization.authentication;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

/**
 * OAuth2 密码认证提供者
 *
 * @author george
 */
public class PasswordAuthenticationProvider extends BaseAuthenticationProvider {
    /**
     * 用户详情认证提供者
     */
    private final UserDetailsService userDetailsService;
    /**
     * 密码编码器
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * 构造函数
     *
     * @param authorizationService 授权服务
     * @param tokenGenerator       令牌生成器
     * @param userDetailsService   用户服务
     * @param passwordEncoder      密码编码器
     */
    public PasswordAuthenticationProvider(OAuth2AuthorizationService authorizationService,
                                          OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
                                          UserDetailsService userDetailsService,
                                          PasswordEncoder passwordEncoder) {
        super(authorizationService, tokenGenerator);
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 获取用户名密码认证令牌
     *
     * @param baseAuthenticationToken 基础认证令牌
     * @return 用户名密码认证令牌
     */
    @Override
    public UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(BaseAuthenticationToken baseAuthenticationToken) {
        PasswordAuthenticationToken token = (PasswordAuthenticationToken) baseAuthenticationToken;
        UserDetails userDetails = userDetailsService.loadUserByUsername(token.getUsername());
        checkUser(userDetails, token.getPassword());
        return UsernamePasswordAuthenticationToken.authenticated(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

    }

    /**
     * 检查用户
     *
     * @param userDetails 用户详情
     * @param password    密码
     */
    private void checkUser(UserDetails userDetails, String password) {
        if (userDetails == null) {
            throw new BadCredentialsException("用户不存在");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("密码错误");
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
        return PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
