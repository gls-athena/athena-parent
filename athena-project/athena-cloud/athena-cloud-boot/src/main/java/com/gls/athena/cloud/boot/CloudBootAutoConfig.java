package com.gls.athena.cloud.boot;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 云服务启动自动配置类
 * 提供Spring Cloud服务发现与组件扫描的基础配置
 * <p>
 * 该配置类整合以下核心功能：
 * 1. 启用服务注册与发现客户端功能（通过@EnableDiscoveryClient）
 * 2. 自动扫描并注册Spring Bean（通过@ComponentScan）
 * 3. 标记为Spring配置类（通过@Configuration）
 * <p>
 * 注：该类默认使用Spring Boot的自动配置机制
 * 需配合application.yml中的spring.cloud配置项使用
 *
 * @author george
 */
@Configuration
@ComponentScan
@EnableDiscoveryClient
public class CloudBootAutoConfig {
}
