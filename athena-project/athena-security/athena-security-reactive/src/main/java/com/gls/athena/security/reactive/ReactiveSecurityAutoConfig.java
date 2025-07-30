package com.gls.athena.security.reactive;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

/**
 * 响应式安全自动配置
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableWebFluxSecurity
public class ReactiveSecurityAutoConfig {
}
