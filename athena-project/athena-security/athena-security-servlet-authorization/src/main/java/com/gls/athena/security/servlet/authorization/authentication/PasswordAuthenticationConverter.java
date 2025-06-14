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
 * OAuth2 密码认证转换器
 *
 * @author george
 */
public class PasswordAuthenticationConverter extends BaseAuthenticationConverter {

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
        // 参数验证
        if (parameterMap == null || clientPrincipal == null || scopes == null) {
            AuthenticationUtil.throwError(OAuth2ErrorCodes.INVALID_REQUEST, "Invalid parameters", IAuthorizationConstants.ERROR_URI);
        }

        // 用户名 (REQUIRED)
        String username = parameterMap.getFirst(OAuth2ParameterNames.USERNAME);
        List<String> usernames = parameterMap.get(OAuth2ParameterNames.USERNAME);
        if (StrUtil.isBlank(username) || usernames == null || usernames.size() != 1) {
            AuthenticationUtil.throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.USERNAME, IAuthorizationConstants.ERROR_URI);
        }

        // 密码 (REQUIRED)
        String password = parameterMap.getFirst(OAuth2ParameterNames.PASSWORD);
        List<String> passwords = parameterMap.get(OAuth2ParameterNames.PASSWORD);
        if (StrUtil.isBlank(password) || passwords == null || passwords.size() != 1) {
            AuthenticationUtil.throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.PASSWORD, IAuthorizationConstants.ERROR_URI);
        }

        // 额外参数
        Map<String, Object> additionalParameters = parameterMap.entrySet().stream()
                .filter(entry -> !OAuth2ParameterNames.GRANT_TYPE.equals(entry.getKey())
                        && !OAuth2ParameterNames.USERNAME.equals(entry.getKey())
                        && !OAuth2ParameterNames.PASSWORD.equals(entry.getKey())
                        && !OAuth2ParameterNames.SCOPE.equals(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().size() > 1 ? entry.getValue() : entry.getValue().getFirst()));

        // 返回 PasswordOAuth2AuthenticationToken 对象
        return new PasswordAuthenticationToken(clientPrincipal, additionalParameters, scopes, username, password);
    }

    /**
     * 是否支持此convert
     *
     * @param grantType 授权类型
     * @return 是否支持
     */
    @Override
    public boolean support(String grantType) {
        return IAuthorizationConstants.PASSWORD.getValue().equals(grantType);
    }
}
