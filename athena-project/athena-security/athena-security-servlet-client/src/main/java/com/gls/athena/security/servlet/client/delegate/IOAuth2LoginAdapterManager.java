package com.gls.athena.security.servlet.client.delegate;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * OAuth2 登录适配器管理器
 *
 * @author george
 */
@Component
public class IOAuth2LoginAdapterManager {

    @Resource
    private List<IOAuth2LoginAdapter> adapters;

    /**
     * 根据提供方ID查找对应的OAuth2登录适配器
     *
     * @param providerId 提供方唯一标识符，用于匹配适配器
     * @return Optional<IOAuth2LoginAdapter> 包含第一个匹配的适配器的Optional对象，
     * 如果不存在匹配的适配器则返回空Optional
     */
    public Optional<IOAuth2LoginAdapter> getAdapter(String providerId) {
        // 从适配器列表中筛选并返回第一个匹配providerId的适配器
        return adapters.stream()
                .filter(adapter -> adapter.test(providerId))
                .findFirst();
    }

}
