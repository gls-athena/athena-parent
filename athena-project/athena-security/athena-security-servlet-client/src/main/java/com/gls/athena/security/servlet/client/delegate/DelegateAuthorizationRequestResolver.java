package com.gls.athena.security.servlet.client.delegate;

import cn.hutool.core.map.MapUtil;
import com.gls.athena.security.servlet.client.config.IClientConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * OAuth2授权请求委托解析器
 * <p>
 * 主要职责：
 * 1. 解析OAuth2授权请求
 * 2. 支持多认证提供商的自定义配置
 * 3. 管理授权请求的定制化处理
 * <p>
 * 工作流程：
 * 1. 接收授权请求
 * 2. 提取客户端注册ID
 * 3. 根据提供商类型选择合适的定制器
 * 4. 应用自定义配置
 *
 * @author george
 */
@Slf4j
@Component
public class DelegateAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    /**
     * 客户端注册ID在URL中的参数名
     */
    private final static String REGISTRATION_ID = "registrationId";

    /**
     * OAuth2授权请求的基础路径
     */
    private final static String AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization";

    /**
     * 用于解析授权请求URL的路径匹配器
     */
    private final static PathPatternRequestMatcher REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults()
            .matcher(AUTHORIZATION_REQUEST_BASE_URI + "/{" + REGISTRATION_ID + "}");

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
     * 解析HTTP请求中的OAuth2授权请求
     *
     * @param request HTTP请求对象
     * @return 解析后的OAuth2授权请求对象，如果无法解析则返回null
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
     * 使用指定的客户端注册ID解析OAuth2授权请求
     *
     * @param request              HTTP请求对象
     * @param clientRegistrationId 客户端注册ID
     * @return 解析后的OAuth2授权请求对象，如果无法解析则返回null
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        // 自定义解析器
        resolver.setAuthorizationRequestCustomizer(builder -> customizerResolver(builder, request, clientRegistrationId));
        // 解析请求
        return resolver.resolve(request, clientRegistrationId);
    }

    /**
     * 从请求URL中提取客户端注册ID
     *
     * @param request HTTP请求对象
     * @return 提取的客户端注册ID，如果URL不匹配则返回null
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
     * 应用授权请求的自定义配置
     * <p>
     * 处理流程：
     * 1. 获取客户端注册信息
     * 2. 从元数据中提取提供商标识
     * 3. 查找并应用匹配的定制器
     * 4. 处理异常情况
     *
     * @param builder              授权请求构建器
     * @param request              HTTP请求对象
     * @param clientRegistrationId 客户端注册ID
     */
    private void customizerResolver(OAuth2AuthorizationRequest.Builder builder,
                                    HttpServletRequest request,
                                    String clientRegistrationId) {
        try {
            // 获取客户端注册信息
            ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
            if (Objects.isNull(clientRegistration)) {
                log.warn("未找到客户端注册信息: {}", clientRegistrationId);
                return;
            }

            // 从元数据中获取提供商标识
            Map<String, Object> metadata = clientRegistration.getProviderDetails().getConfigurationMetadata();
            String provider = MapUtil.getStr(metadata, IClientConstants.PROVIDER_ID);

            if (provider == null) {
                log.warn("未找到提供商信息: {}", clientRegistrationId);
                return;
            }

            // 查找并应用匹配的定制器
            customizers.stream()
                    .filter(customizer -> customizer.test(provider))
                    .findFirst()
                    .ifPresentOrElse(
                            customizer -> {
                                log.debug("应用自定义授权请求配置: provider={}", provider);
                                customizer.accept(builder, request, clientRegistration);
                            },
                            () -> log.debug("未找到匹配的授权请求定制器: provider={}", provider)
                    );
        } catch (Exception e) {
            log.error("自定义授权请求配置失败", e);
        }
    }

}
