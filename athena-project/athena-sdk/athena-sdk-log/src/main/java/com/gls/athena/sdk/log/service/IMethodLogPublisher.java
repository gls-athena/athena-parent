package com.gls.athena.sdk.log.service;

import com.gls.athena.sdk.log.domain.MethodLogDto;

/**
 * 方法日志发布器接口
 * 职责：负责发布方法日志事件
 *
 * @author george
 */
public interface IMethodLogPublisher {

    /**
     * 发布方法日志事件
     * 采用异步方式发布，确保不影响主业务流程
     *
     * @param methodLogDto 方法日志数据
     */
    void publishLog(MethodLogDto methodLogDto);
}
