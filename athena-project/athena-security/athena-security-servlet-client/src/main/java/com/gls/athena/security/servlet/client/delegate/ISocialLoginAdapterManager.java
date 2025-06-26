package com.gls.athena.security.servlet.client.delegate;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 社交登录适配器管理器
 *
 * @author lizy19
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

}
