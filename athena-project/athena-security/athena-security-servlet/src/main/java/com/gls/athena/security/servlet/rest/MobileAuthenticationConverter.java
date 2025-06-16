package com.gls.athena.security.servlet.rest;

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
    /**
     * 手机号参数
     */
    private String mobileParameter = "mobile";

    /**
     * 将HTTP请求转换为移动端认证令牌
     * <p>
     * 该方法从HTTP请求中提取手机号，如果手机号存在则创建未认证的移动端认证令牌，
     * 否则返回null表示无法转换
     *
     * @param request HTTP请求对象，包含客户端请求的所有信息
     * @return MobileAuthenticationToken 未认证的移动端认证令牌，
     * 如果请求中不包含手机号则返回null
     */
    @Override
    public Authentication convert(HttpServletRequest request) {
        // 从请求中获取手机号
        String mobile = obtainMobile(request);

        // 手机号不存在则返回null
        if (mobile == null) {
            return null;
        }

        // 创建并返回未认证的移动端认证令牌
        return MobileAuthenticationToken.unauthenticated(mobile);
    }

    /**
     * 获取手机号
     *
     * @param request 请求
     * @return 手机号
     */
    private String obtainMobile(HttpServletRequest request) {
        return WebUtil.getParameter(request, mobileParameter);
    }
}
