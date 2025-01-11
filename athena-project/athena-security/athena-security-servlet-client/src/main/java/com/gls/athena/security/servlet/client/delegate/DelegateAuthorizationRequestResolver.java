package com.gls.athena.security.servlet.client.delegate;

import cn.hutool.core.map.MapUtil;
import com.gls.athena.security.servlet.client.config.IClientConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OAuth2 授权请求委托解析器
 * 负责处理 OAuth2 授权请求的解析和自定义，支持多提供商配置。
 * 该解析器可以根据不同的认证提供商定制化授权请求参数。
 *
 * @author george
 */
@Component
public class DelegateAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    /**
     * OAuth2 客户端注册 ID 参数名
     */
    private final static String REGISTRATION_ID = "registrationId";

    /**
     * OAuth2 授权端点的基础 URI
     */
    private final static String AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization";

    /**
     * 用于匹配授权请求 URL 的路径匹配器
     */
    private final static AntPathRequestMatcher REQUEST_MATCHER = new AntPathRequestMatcher(
            AUTHORIZATION_REQUEST_BASE_URI + "/{" + REGISTRATION_ID + "}");

    /**
     * Spring Security 提供的默认授权请求解析器
     */
    private final DefaultOAuth2AuthorizationRequestResolver resolver;

    /**
     * 自定义授权请求定制器集合
     */
    private final ObjectProvider<IAuthorizationRequestCustomizer> customizers;

    /**
     * OAuth2 客户端注册信息存储库
     */
    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * 构造函数
     *
     * @param clientRegistrationRepository OAuth2 客户端注册信息存储库
     * @param customizers                  授权请求定制器提供者
     */
    public DelegateAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository,
                                                ObjectProvider<IAuthorizationRequestCustomizer> customizers) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.resolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, AUTHORIZATION_REQUEST_BASE_URI);
        this.customizers = customizers;
    }

    /**
     * 解析 HTTP 请求中的授权请求信息
     *
     * @param request HTTP 请求
     * @return OAuth2AuthorizationRequest 如果解析成功；如果无法解析则返回 null
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        // 获取客户端注册标识
        String clientRegistrationId = getClientRegistrationId(request);
        // 自定义解析器
        resolver.setAuthorizationRequestCustomizer(builder -> customizerResolver(builder, request, clientRegistrationId));
        // 解析请求
        return resolver.resolve(request);
    }

    /**
     * 使用指定的客户端注册 ID 解析授权请求
     *
     * @param request              HTTP 请求
     * @param clientRegistrationId 客户端注册 ID
     * @return OAuth2AuthorizationRequest 如果解析成功；如果无法解析则返回 null
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        // 自定义解析器
        resolver.setAuthorizationRequestCustomizer(builder -> customizerResolver(builder, request, clientRegistrationId));
        // 解析请求
        return resolver.resolve(request, clientRegistrationId);
    }

    /**
     * 从请求 URL 中提取客户端注册 ID
     *
     * @param request HTTP 请求
     * @return 客户端注册 ID，如果不匹配则返回 null
     */
    private String getClientRegistrationId(HttpServletRequest request) {
        // 匹配请求
        if (REQUEST_MATCHER.matches(request)) {
            // 获取注册标识
            return REQUEST_MATCHER.matcher(request).getVariables().get(REGISTRATION_ID);
        }
        return null;
    }

    /**
     * 应用自定义授权请求配置
     * 根据提供商类型选择合适的定制器来自定义授权请求参数
     *
     * @param builder              授权请求构建器
     * @param request              HTTP 请求
     * @param clientRegistrationId 客户端注册 ID
     */
    private void customizerResolver(OAuth2AuthorizationRequest.Builder builder,
                                    HttpServletRequest request,
                                    String clientRegistrationId) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        Map<String, Object> metadata = clientRegistration.getProviderDetails().getConfigurationMetadata();
        // 获取提供者
        String provider = MapUtil.getStr(metadata, IClientConstants.PROVIDER_ID);
        // 自定义 OAuth2 授权请求器
        customizers.stream()
                .filter(customizer -> customizer.test(provider))
                .findFirst()
                .ifPresent(customizer -> customizer.accept(builder, request, clientRegistration));
    }

}
