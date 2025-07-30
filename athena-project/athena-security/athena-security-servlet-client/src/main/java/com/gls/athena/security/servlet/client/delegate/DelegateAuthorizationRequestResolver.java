package com.gls.athena.security.servlet.client.delegate;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.stereotype.Component;

/**
 * 委托授权请求解析器
 *
 * @author george
 */
@Slf4j
@Component
public class DelegateAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    /**
     * 注册ID
     */
    private final static String REGISTRATION_ID = "registrationId";
    /**
     * 授权请求基础URI
     */
    private final static String AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization";
    /**
     * 请求匹配器
     */
    private final static PathPatternRequestMatcher REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults()
            .matcher(AUTHORIZATION_REQUEST_BASE_URI + "/{" + REGISTRATION_ID + "}");
    /**
     * 默认授权请求解析器
     */
    private final DefaultOAuth2AuthorizationRequestResolver resolver;
    /**
     * 社交登录适配器管理器
     */
    private final ISocialLoginAdapterManager adapterManager;
    /**
     * 客户端注册信息仓库
     */
    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * 构造委托授权请求解析器
     *
     * @param clientRegistrationRepository 客户端注册信息仓库
     * @param adapterManager               社交登录适配器管理器
     */
    public DelegateAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository,
                                                ISocialLoginAdapterManager adapterManager) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.resolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, AUTHORIZATION_REQUEST_BASE_URI);
        this.adapterManager = adapterManager;
    }

    /**
     * 解析并构建OAuth2授权请求对象。
     * 该方法根据HTTP请求中的客户端注册ID，定制化授权请求构建器，并最终返回解析后的OAuth2授权请求。
     *
     * @param request HTTP请求对象，包含客户端请求信息
     * @return OAuth2AuthorizationRequest 解析后的OAuth2授权请求对象
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        // 从请求中获取客户端注册ID
        String clientRegistrationId = getClientRegistrationId(request);
        // 根据客户端注册ID定制解析器
        customResolver(request, clientRegistrationId);
        // 使用定制后的解析器解析HTTP请求并返回OAuth2授权请求对象
        return resolver.resolve(request);
    }

    /**
     * 重写resolve方法以自定义OAuth2授权请求的解析过程
     * 此方法允许在解析授权请求时考虑特定的客户端注册ID和HTTP请求上下文
     *
     * @param request              当前的HTTP请求对象，包含有关此次请求的信息，如请求头、查询参数等
     * @param clientRegistrationId 客户端应用的注册ID，用于区分不同的客户端应用
     * @return 返回一个解析后的OAuth2授权请求对象，该对象包含了构造授权请求所需的信息
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        // 调用自定义的解析器方法，对授权请求进行额外的处理或验证
        customResolver(request, clientRegistrationId);
        // 使用定制化的设置解析授权请求，并返回解析后的授权请求对象
        return resolver.resolve(request, clientRegistrationId);
    }

    /**
     * 使用自定义解析器处理OAuth2.0授权请求
     * <p>
     * 本方法根据客户端注册ID获取客户端注册信息，并使用自定义的授权请求定制器配置授权请求
     * 主要目的是在用户登录过程中，根据特定的客户端信息和请求上下文，定制化处理授权请求
     *
     * @param request              当前的HTTP请求对象，包含用户请求的详细信息
     * @param clientRegistrationId 客户端注册ID，用于标识和获取特定的客户端配置
     */
    private void customResolver(HttpServletRequest request, String clientRegistrationId) {
        // 根据客户端注册ID获取对应的客户端注册信息
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);

        // 设置自定义的授权请求处理逻辑
        resolver.setAuthorizationRequestCustomizer(builder -> adapterManager.accept(builder, request, clientRegistration));
    }

    /**
     * 获取客户端注册ID
     * 该方法用于从HTTP请求中提取客户端的注册ID，如果请求匹配预定义的模式则进行提取
     *
     * @param request HttpServletRequest对象，包含客户端的请求信息
     * @return 如果请求匹配模式且包含注册ID，则返回注册ID；否则返回null
     */
    private String getClientRegistrationId(HttpServletRequest request) {
        // 检查请求是否匹配预定义的模式
        if (REQUEST_MATCHER.matches(request)) {
            // 如果匹配成功，则从请求中提取并返回注册ID
            return REQUEST_MATCHER.matcher(request).getVariables().get(REGISTRATION_ID);
        }
        // 如果请求不匹配模式，则返回null
        return null;
    }

}
