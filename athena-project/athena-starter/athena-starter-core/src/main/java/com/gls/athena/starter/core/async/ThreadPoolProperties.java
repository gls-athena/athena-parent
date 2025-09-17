package com.gls.athena.starter.core.async;

import com.gls.athena.common.core.constant.BaseProperties;
import com.gls.athena.common.core.constant.IConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 线程池属性
 *
 * @author george
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = IConstants.BASE_PROPERTIES_PREFIX + "thread-pool")
public class ThreadPoolProperties extends BaseProperties {

    /**
     * 核心线程数 默认为CPU核数乘以2
     */
    private Integer corePoolSize = IConstants.CPU_NUM * 2;

    /**
     * 最大线程数 默认为CPU核数乘以4
     */
    private Integer maxPoolSize = IConstants.CPU_NUM * 4;

    /**
     * 队列容量
     */
    private Integer queueCapacity = 500;

    /**
     * 线程名前缀
     */
    private String threadNamePrefix = "athena-async-";

    /**
     * 空闲线程存活时间（秒）
     */
    private Integer keepAliveSeconds = 60;

    /**
     * 等待任务完成再关闭
     */
    private boolean waitForTasksToCompleteOnShutdown = true;

    /**
     * 等待时间（秒）
     */
    private Integer awaitTerminationSeconds = 60;

}
