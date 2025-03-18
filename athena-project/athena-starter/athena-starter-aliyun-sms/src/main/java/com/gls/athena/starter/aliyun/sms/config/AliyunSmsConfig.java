package com.gls.athena.starter.aliyun.sms.config;

import com.aliyuncs.IAcsClient;
import com.gls.athena.starter.aliyun.core.support.AliyunHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 短信客户端配置
 *
 * @author george
 */
@Configuration
public class AliyunSmsConfig {

    /**
     * 创建短信客户端
     * <p>
     * 该方法用于根据阿里云短信配置创建一个短信客户端实例。如果Spring容器中不存在名为 "smsAcsClient" 的Bean，
     * 则会调用该方法创建并注册该Bean。
     *
     * @param aliyunSmsProperties 阿里云短信配置，包含访问阿里云短信服务所需的必要信息，如AccessKey、SecretKey等。
     * @return IAcsClient 返回一个实现了IAcsClient接口的短信客户端实例，用于与阿里云短信服务进行交互。
     */
    @Bean
    @ConditionalOnMissingBean(name = "smsAcsClient")
    public IAcsClient smsAcsClient(AliyunSmsProperties aliyunSmsProperties) {
        // 调用AliyunHelper工具类的方法，根据配置创建并返回一个短信客户端实例
        return AliyunHelper.createAcsClient(aliyunSmsProperties);
    }

}
