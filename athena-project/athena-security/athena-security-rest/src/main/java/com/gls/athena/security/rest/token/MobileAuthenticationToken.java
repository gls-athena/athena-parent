package com.gls.athena.security.rest.token;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * 手机认证令牌
 *
 * @author george
 */
public class MobileAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * 主体
     */
    @Getter
    private final Object principal;

    /**
     * 构造函数
     *
     * @param principal 主体
     */
    public MobileAuthenticationToken(Object principal) {
        super(null);
        this.principal = principal;
        setAuthenticated(false);
    }

    /**
     * 未认证
     *
     * @param principal 主体
     * @return 认证
     */
    public static MobileAuthenticationToken unauthenticated(Object principal) {
        return new MobileAuthenticationToken(principal);
    }

    /**
     * 密码
     *
     * @return 密码
     */
    @Override
    public Object getCredentials() {
        return null;
    }
}
