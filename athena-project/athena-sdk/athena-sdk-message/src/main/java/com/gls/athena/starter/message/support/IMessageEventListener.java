package com.gls.athena.starter.message.support;

import com.gls.athena.starter.message.domain.MessageDto;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

/**
 * 消息事件监听器
 *
 * @author george
 */
public interface IMessageEventListener {

    /**
     * 消息事件监听
     *
     * @param messageDto 消息事件
     */
    @Async
    @EventListener(MessageDto.class)
    void onMessageEvent(MessageDto messageDto);
}
