package com.athena.security.core.common.code.sms;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.athena.security.core.common.SecurityProperties;
import com.athena.security.core.common.code.base.VerificationCodeGenerator;

/**
 * 短信验证码生成器
 */
public class SmsCodeGenerator implements VerificationCodeGenerator<SmsCode> {
    /**
     * 短信验证码配置
     */
    private final SecurityProperties.Sms smsProperties;

    /**
     * 构造函数
     *
     * @param securityProperties 安全配置
     */
    public SmsCodeGenerator(SecurityProperties securityProperties) {
        this.smsProperties = securityProperties.getCode().getSms();
    }

    /**
     * 生成验证码
     *
     * @return 验证码
     */
    @Override
    public SmsCode generate() {
        SmsCode smsCode = new SmsCode();
        smsCode.setCode(RandomUtil.randomNumbers(smsProperties.getLength()));
        smsCode.setExpireTime(DateUtil.offsetSecond(DateUtil.date(), smsProperties.getExpireIn()));
        return smsCode;
    }

}
