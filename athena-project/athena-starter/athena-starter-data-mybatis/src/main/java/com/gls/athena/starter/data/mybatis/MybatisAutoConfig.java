package com.gls.athena.starter.data.mybatis;

import com.gls.athena.common.core.constant.IConstants;
import com.gls.athena.starter.data.mybatis.config.MybatisProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis自动配置
 *
 * @author george
 */
@Configuration
@ComponentScan
@MapperScan(basePackages = IConstants.BASE_PACKAGE_PREFIX + ".**.mapper")
@EnableConfigurationProperties({MybatisProperties.class})
public class MybatisAutoConfig {
}