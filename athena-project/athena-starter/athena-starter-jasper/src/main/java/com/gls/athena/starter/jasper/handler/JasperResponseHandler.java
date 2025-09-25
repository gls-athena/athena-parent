package com.gls.athena.starter.jasper.handler;

import com.gls.athena.starter.file.base.BaseFileResponseHandler;
import com.gls.athena.starter.file.base.BaseFileResponseWrapper;
import com.gls.athena.starter.jasper.annotation.JasperResponse;
import com.gls.athena.starter.jasper.generator.JasperGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Jasper响应处理器 - 专门负责Spring MVC返回值处理
 *
 * @author george
 */
@Slf4j
public class JasperResponseHandler extends BaseFileResponseHandler<JasperGenerator, JasperResponse> {

    /**
     * 构造函数，初始化Jasper响应处理器
     *
     * @param jasperGenerators Jasper生成器列表，用于处理报表生成
     */
    public JasperResponseHandler(List<JasperGenerator> jasperGenerators) {
        super(jasperGenerators);
    }

    /**
     * 获取响应注解的类类型
     *
     * @return JasperResponse类对象
     */
    @Override
    protected Class<JasperResponse> getResponseClass() {
        return JasperResponse.class;
    }

    @Override
    protected BaseFileResponseWrapper<JasperResponse> getResponseWrapper(JasperResponse jasperResponse) {
        return null;
    }

}

