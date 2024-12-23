package com.gls.athena.starter.log.method;

import com.gls.athena.starter.log.domain.MethodDto;
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
