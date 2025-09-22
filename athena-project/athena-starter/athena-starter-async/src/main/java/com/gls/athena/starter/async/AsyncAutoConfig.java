package com.gls.athena.starter.async;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 异步处理自动配置
 *
 * @author george
 */
@EnableAsync
@Configuration
@ComponentScan
public class AsyncAutoConfig {
}
