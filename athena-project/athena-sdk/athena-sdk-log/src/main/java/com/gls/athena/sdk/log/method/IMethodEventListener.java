package com.gls.athena.sdk.log.method;

import com.gls.athena.sdk.log.domain.MethodDto;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

/**
 * 方法日志消费者
 *
 * @author george
 */
public interface IMethodEventListener {

    /**
     * 异步处理方法执行事件
     *
     * <p>通过Spring事件监听机制接收方法日志事件，使用@Async注解实现异步处理。当系统产生MethodDto类型的事件时，
     * 该方法会自动触发并处理，适用于需要非阻塞记录方法执行信息的场景。</p>
     *
     * @param methodDto 方法事件数据传输对象
     *                  包含方法执行的元数据信息，如方法签名、执行耗时、入参值、返回结果、异常信息等核心日志属性
     */
    @Async
    @EventListener(MethodDto.class)
    void onMethodEvent(MethodDto methodDto);

}
