package com.gls.athena.security.rest.provider;

import com.gls.athena.security.rest.token.MobileAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 手机号码认证提供者
 *
 * @author lizy19
 */
@RequiredArgsConstructor
public class MobileAuthenticationProvider implements AuthenticationProvider {
    /**
     * 用户详细信息服务
     */
    private final UserDetailsService userDetailsService;

    /**
     * 对手机号码认证令牌进行身份验证
     *
     * @param authentication 包含手机号码认证信息的Authentication对象
     * @return 验证成功后返回包含用户详细信息的Authentication对象
     * @throws AuthenticationException 当认证失败时抛出此异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 将传入的authentication转换为手机号码认证令牌
        MobileAuthenticationToken mobileAuthenticationToken = (MobileAuthenticationToken) authentication;
        String mobile = (String) mobileAuthenticationToken.getPrincipal();

        // 根据手机号码加载用户详细信息
        UserDetails userDetails = userDetailsService.loadUserByUsername(mobile);
        if (userDetails != null) {
            // 用户存在，创建已认证的用户名密码认证令牌并返回
            return UsernamePasswordAuthenticationToken.authenticated(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        }
        // 用户不存在，抛出凭证错误异常
        throw new BadCredentialsException("Bad credentials");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MobileAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
