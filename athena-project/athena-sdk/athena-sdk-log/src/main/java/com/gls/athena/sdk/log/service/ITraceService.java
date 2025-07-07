package com.gls.athena.sdk.log.service;

/**
 * 跟踪服务接口
 * 职责：专门负责分布式跟踪相关功能
 *
 * @author george
 */
public interface ITraceService {

    /**
     * 获取当前跟踪ID
     *
     * @return 跟踪ID，如果不存在则返回null
     */
    String getCurrentTraceId();
}
