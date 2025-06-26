package com.gls.athena.security.servlet.client.delegate;

import cn.hutool.core.map.MapUtil;
import com.gls.athena.security.servlet.client.config.IClientConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
 * 委托授权请求解析器
 *
 * @author george
 */
@Slf4j
@Component
public class DelegateAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final static String REGISTRATION_ID = "registrationId";

    private final static String AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization";

    private final static PathPatternRequestMatcher REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults()
            .matcher(AUTHORIZATION_REQUEST_BASE_URI + "/{" + REGISTRATION_ID + "}");

    private final DefaultOAuth2AuthorizationRequestResolver resolver;

    private final ISocialLoginAdapterManager adapterManager;

    private final ClientRegistrationRepository clientRegistrationRepository;

    public DelegateAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository,
                                                ISocialLoginAdapterManager adapterManager) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.resolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, AUTHORIZATION_REQUEST_BASE_URI);
        this.adapterManager = adapterManager;
    }

    /**
     * 解析并构建OAuth2授权请求
     * <p>
     * 该方法用于从HttpServletRequest中解析出OAuth2授权请求，包含以下处理步骤：
     * 1. 从请求中获取客户端注册标识
     * 2. 设置自定义授权请求构建器
     * 3. 使用解析器生成最终的OAuth2授权请求
     *
     * @param request HTTP请求对象，包含OAuth2授权请求所需参数
     * @return 构建完成的OAuth2AuthorizationRequest对象
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        // 从请求路径或参数中提取客户端注册ID
        String clientRegistrationId = getClientRegistrationId(request);

        // 配置自定义请求构建逻辑，允许对标准授权请求进行扩展
        resolver.setAuthorizationRequestCustomizer(builder -> customizerResolver(builder, request, clientRegistrationId));

        // 使用配置好的解析器生成最终授权请求
        return resolver.resolve(request);
    }

    /**
     * 解析并构建OAuth2授权请求
     * <p>
     * 该方法用于解析HTTP请求并生成OAuth2授权请求对象。首先设置自定义授权请求构建器，
     * 然后使用默认解析器完成请求解析。
     *
     * @param request              HTTP请求对象，包含客户端请求信息
     * @param clientRegistrationId 客户端注册ID，用于识别特定的客户端配置
     * @return OAuth2AuthorizationRequest 构建完成的OAuth2授权请求对象
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        // 设置自定义授权请求构建器，用于添加特定于请求的定制逻辑
        resolver.setAuthorizationRequestCustomizer(builder -> customizerResolver(builder, request, clientRegistrationId));

        // 使用默认解析器解析请求并返回结果
        return resolver.resolve(request, clientRegistrationId);
    }

    /**
     * 从HTTP请求中获取客户端注册ID
     * <p>
     * 该方法通过检查请求是否匹配预定义的匹配规则，来提取客户端注册ID。
     * 如果请求匹配成功，则返回注册ID；否则返回null。
     *
     * @param request HTTP请求对象，包含客户端请求的所有信息
     * @return 客户端注册ID字符串，如果请求不匹配则返回null
     */
    private String getClientRegistrationId(HttpServletRequest request) {
        // 检查请求是否匹配预定义的规则
        if (REQUEST_MATCHER.matches(request)) {
            // 从匹配成功的请求中提取注册ID
            return REQUEST_MATCHER.matcher(request).getVariables().get(REGISTRATION_ID);
        }
        return null;
    }

    /**
     * 自定义OAuth2授权请求解析器
     * <p>
     * 根据客户端注册ID查找对应的ClientRegistration，并通过适配器模式对OAuth2授权请求进行定制化配置
     *
     * @param builder              OAuth2授权请求构建器，用于配置授权请求参数
     * @param request              HTTP请求对象，可用于获取请求相关信息
     * @param clientRegistrationId 客户端注册ID，用于查找对应的ClientRegistration
     */
    private void customizerResolver(OAuth2AuthorizationRequest.Builder builder,
                                    HttpServletRequest request,
                                    String clientRegistrationId) {
        try {
            // 根据注册ID查找客户端注册信息
            ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
            if (Objects.isNull(clientRegistration)) {
                log.warn("未找到客户端注册信息: {}", clientRegistrationId);
                return;
            }

            // 从客户端注册信息中获取提供商元数据
            Map<String, Object> metadata = clientRegistration.getProviderDetails().getConfigurationMetadata();
            String provider = MapUtil.getStr(metadata, IClientConstants.PROVIDER_ID);

            if (provider == null) {
                log.warn("未找到提供商信息: {}", clientRegistrationId);
                return;
            }

            // 通过适配器管理器获取对应的适配器并应用配置
            adapterManager.getAdapter(provider)
                    .ifPresent(adapter -> adapter.accept(builder, request, clientRegistration));
        } catch (Exception e) {
            log.error("自定义授权请求配置失败", e);
        }
    }

}
