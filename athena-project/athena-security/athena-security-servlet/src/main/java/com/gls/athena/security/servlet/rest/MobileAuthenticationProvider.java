package com.gls.athena.security.servlet.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 手机号认证提供者
 *
 * @author george
 */
@RequiredArgsConstructor
public class MobileAuthenticationProvider implements AuthenticationProvider {
    /**
     * 用户详情服务
     */
    private final UserDetailsService userDetailsService;

    /**
     * 认证
     *
     * @param authentication 认证
     * @return 认证
     * @throws AuthenticationException 认证异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取手机号码
        String mobile = determineMobile(authentication);
        // 通过手机号码获取用户信息
        UserDetails userDetails = userDetailsService.loadUserByUsername(mobile);
        if (userDetails != null) {
            // 返回认证信息
            return UsernamePasswordAuthenticationToken.authenticated(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        }
        // 认证失败
        throw new BadCredentialsException("Bad credentials");
    }

    /**
     * 获取手机号码
     *
     * @param authentication 认证
     * @return 手机号码
     */
    private String determineMobile(Authentication authentication) {
        return (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
    }

    /**
     * 是否支持
     *
     * @param authentication 认证
     * @return 是否支持
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return MobileAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
