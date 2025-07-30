package com.gls.athena.security.servlet.authorization.authentication;

import cn.hutool.core.util.StrUtil;
import com.gls.athena.security.servlet.authorization.config.IAuthorizationConstants;
import com.gls.athena.security.servlet.authorization.util.AuthenticationUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * OAuth2 短信认证转换器
 *
 * @author george
 */
public class SmsAuthenticationConverter extends BaseAuthenticationConverter {

    /**
     * 转换
     *
     * @param parameterMap    参数
     * @param clientPrincipal 客户端主体
     * @param scopes          范围
     * @return 认证信息
     */
    @Override
    protected Authentication convert(MultiValueMap<String, String> parameterMap, Authentication clientPrincipal, Set<String> scopes) {
        // 手机号 (REQUIRED)
        String mobile = parameterMap.getFirst(IAuthorizationConstants.MOBILE);
        List<String> mobileValues = parameterMap.get(IAuthorizationConstants.MOBILE);
        if (StrUtil.isBlank(mobile) || mobileValues == null || mobileValues.size() != 1) {
            AuthenticationUtil.throwError(OAuth2ErrorCodes.INVALID_REQUEST, IAuthorizationConstants.MOBILE, IAuthorizationConstants.ERROR_URI);
        }

        // 额外参数
        Map<String, Object> additionalParameters = parameterMap.entrySet().stream()
                .filter(entry -> !IAuthorizationConstants.MOBILE.equals(entry.getKey())
                        && !OAuth2ParameterNames.GRANT_TYPE.equals(entry.getKey())
                        && !OAuth2ParameterNames.SCOPE.equals(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().size() > 1 ? entry.getValue() : entry.getValue().getFirst()
                ));

        // 返回 SmsOAuth2AuthenticationToken 对象
        return new SmsAuthenticationToken(clientPrincipal, additionalParameters, scopes, mobile);
    }

    /**
     * 是否支持此convert
     *
     * @param grantType 授权类型
     * @return 是否支持
     */
    @Override
    public boolean support(String grantType) {
        return IAuthorizationConstants.SMS.getValue().equals(grantType);
    }
}
