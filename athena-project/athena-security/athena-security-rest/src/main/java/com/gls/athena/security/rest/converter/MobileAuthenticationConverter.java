package com.gls.athena.security.rest.converter;

import cn.hutool.core.util.StrUtil;
import com.gls.athena.security.rest.token.MobileAuthenticationToken;
import com.gls.athena.starter.web.util.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;

/**
 * 手机号认证转换器
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class MobileAuthenticationConverter implements AuthenticationConverter {

    private String mobileParameter = "mobile";

    /**
     * 将HTTP请求转换为认证对象
     *
     * @param request HTTP请求对象，用于获取认证参数
     * @return Authentication 认证对象，如果手机号为空则返回null，否则返回未认证的手机号认证令牌
     */
    @Override
    public Authentication convert(HttpServletRequest request) {
        // 从请求中获取手机号参数
        String mobile = WebUtil.getParameter(request, mobileParameter);
        // 如果手机号为空，则返回null
        if (StrUtil.isBlank(mobile)) {
            return null;
        }
        // 创建并返回未认证的手机号认证令牌
        return MobileAuthenticationToken.unauthenticated(mobile);
    }

}
