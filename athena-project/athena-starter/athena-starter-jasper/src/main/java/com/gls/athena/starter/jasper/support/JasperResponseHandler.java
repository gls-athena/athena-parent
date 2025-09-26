package com.gls.athena.starter.jasper.support;

import com.gls.athena.starter.file.base.FileResponseHandler;
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
public class JasperResponseHandler extends FileResponseHandler<JasperGenerator, JasperResponse> {

    /**
     * 构造函数，初始化Jasper响应处理器
     *
     * @param jasperGenerators Jasper生成器列表，用于处理报表生成
     */
    public JasperResponseHandler(List<JasperGenerator> jasperGenerators) {
        super(jasperGenerators);
    }

}

