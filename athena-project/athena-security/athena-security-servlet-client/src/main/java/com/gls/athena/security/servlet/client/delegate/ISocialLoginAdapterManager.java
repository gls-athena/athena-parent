package com.gls.athena.security.servlet.client.delegate;

import cn.hutool.core.map.MapUtil;
import com.gls.athena.security.servlet.client.config.IClientConstants;
import jakarta.annotation.Resource;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 社交登录适配器管理器
 *
 * @author george
 */
@Component
public class ISocialLoginAdapterManager {

    @Resource
    private List<ISocialLoginAdapter> adapters;

    /**
     * 根据提供商ID获取相应的社交登录适配器
     *
     * @param providerId 社交登录提供商的唯一标识符
     * @return 如果找到匹配的适配器，则返回一个包含适配器的Optional对象，否则返回空Optional
     */
    public Optional<ISocialLoginAdapter> getAdapter(String providerId) {
        // 从适配器列表中筛选并返回第一个匹配providerId的适配器
        return adapters.stream()
                .filter(adapter -> adapter.test(providerId))
                .findFirst();
    }

    /**
     * 根据客户端注册信息获取对应的社交登录适配器
     * <p>
     * 该方法通过客户端注册信息(ClientRegistration)逐层解析，最终获取提供商标识(PROVIDER_ID)，
     * 并调用内部方法查找对应的社交登录适配器。如果任一步骤结果为null，则返回空的Optional。
     *
     * @param clientRegistration 客户端注册信息，包含提供商的详细配置信息
     * @return Optional包装的社交登录适配器，可能为空(如果无法匹配或参数为null)
     */
    public Optional<ISocialLoginAdapter> getAdapter(ClientRegistration clientRegistration) {
        return Optional.ofNullable(clientRegistration)
                .map(ClientRegistration::getProviderDetails)
                .map(ClientRegistration.ProviderDetails::getConfigurationMetadata)
                .map(metadata -> MapUtil.getStr(metadata, IClientConstants.PROVIDER_ID, "default"))
                .flatMap(this::getAdapter);
    }

}
