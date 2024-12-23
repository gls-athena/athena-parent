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
     * 方法事件监听
     *
     * @param methodDto 方法事件
     */
    @Async
    @EventListener(MethodDto.class)
    void onMethodEvent(MethodDto methodDto);

}
