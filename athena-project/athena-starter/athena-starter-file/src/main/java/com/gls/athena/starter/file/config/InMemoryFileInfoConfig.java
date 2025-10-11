package com.gls.athena.starter.file.config;

import com.gls.athena.starter.file.manager.IFileInfoManager;
import com.gls.athena.starter.file.manager.IFileStorageManager;
import com.gls.athena.starter.file.manager.InMemoryFileInfoManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 内存文件信息配置类
 * 当配置项 athena.file.info = memory 或未配置时生效
 * 提供基于内存的文件信息管理器实现
 *
 * @author george
 */
@Configuration
@ConditionalOnProperty(prefix = "athena.file", name = "info", havingValue = "memory", matchIfMissing = true)
public class InMemoryFileInfoConfig {

    /**
     * 创建内存文件信息管理器 Bean
     * 当容器中不存在 IFileInfoManager 类型的 Bean 时创建
     *
     * @return 内存文件信息管理器实例
     */
    @Bean
    @ConditionalOnMissingBean(IFileInfoManager.class)
    public IFileInfoManager inMemoryFileInfoManager() {
        return new InMemoryFileInfoManager();
    }
}
