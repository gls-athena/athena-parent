package com.gls.athena.starter.message.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 消息传输对象
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public class MessageDto implements Serializable {
    /**
     * 消息类型
     */
    private MessageType type;
    /**
     * 消息标题
     */
    private String title;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息发送人
     */
    private String sender;
    /**
     * 消息接收人
     */
    private String receiver;
    /**
     * 消息模板
     */
    private String template;
    /**
     * 消息参数
     */
    private Map<String, Object> params;

}
