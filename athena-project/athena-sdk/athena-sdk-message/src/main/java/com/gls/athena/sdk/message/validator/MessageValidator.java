package com.gls.athena.sdk.message.validator;

import com.gls.athena.sdk.message.domain.MessageDto;
import com.gls.athena.sdk.message.domain.MessageType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息验证器
 * 职责：专门负责消息对象的验证
 *
 * @author george
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageValidator {

    /**
     * 验证消息对象
     *
     * @param messageDto 消息对象
     * @return 验证结果
     */
    public static boolean validate(MessageDto messageDto) {
        if (messageDto == null) {
            log.warn("消息对象为空");
            return false;
        }

        if (messageDto.getType() == null) {
            log.warn("消息类型为空");
            return false;
        }

        if (messageDto.getReceiver() == null || messageDto.getReceiver().trim().isEmpty()) {
            log.warn("消息接收者为空");
            return false;
        }

        return validateByType(messageDto);
    }

    /**
     * 根据消息类型进行特定验证
     *
     * @param messageDto 消息对象
     * @return 验证结果
     */
    private static boolean validateByType(MessageDto messageDto) {
        MessageType type = messageDto.getType();

        return switch (type) {
            case SMS -> validateSms(messageDto);
            case EMAIL -> validateEmail(messageDto);
            case SITE_MESSAGE -> validateSiteMessage(messageDto);
            case WECHAT, DING_TALK, FEISHU, WECHAT_WORK -> validateInstantMessage(messageDto);
            default -> {
                log.warn("未知的消息类型: {}", type);
                yield false;
            }
        };
    }

    /**
     * 验证短信消息
     *
     * @param messageDto 消息对象
     * @return 验证结果
     */
    private static boolean validateSms(MessageDto messageDto) {
        String receiver = messageDto.getReceiver();
        if (!receiver.matches("^1[3-9]\\d{9}$")) {
            log.warn("短信接收者手机号格式不正确: {}", receiver);
            return false;
        }

        if (messageDto.getTemplate() == null || messageDto.getTemplate().trim().isEmpty()) {
            log.warn("短信模板为空");
            return false;
        }

        return true;
    }

    /**
     * 验证邮件消息
     *
     * @param messageDto 消息对象
     * @return 验证结果
     */
    private static boolean validateEmail(MessageDto messageDto) {
        String receiver = messageDto.getReceiver();
        if (!receiver.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            log.warn("邮件接收者邮箱格式不正确: {}", receiver);
            return false;
        }

        if (messageDto.getTitle() == null || messageDto.getTitle().trim().isEmpty()) {
            log.warn("邮件标题为空");
            return false;
        }

        return true;
    }

    /**
     * 验证站内信消息
     *
     * @param messageDto 消息对象
     * @return 验证结果
     */
    private static boolean validateSiteMessage(MessageDto messageDto) {
        if (messageDto.getTitle() == null || messageDto.getTitle().trim().isEmpty()) {
            log.warn("站内信标题为空");
            return false;
        }

        if (messageDto.getContent() == null || messageDto.getContent().trim().isEmpty()) {
            log.warn("站内信内容为空");
            return false;
        }

        return true;
    }

    /**
     * 验证即时消息
     *
     * @param messageDto 消息对象
     * @return 验证结果
     */
    private static boolean validateInstantMessage(MessageDto messageDto) {
        if (messageDto.getContent() == null || messageDto.getContent().trim().isEmpty()) {
            log.warn("即时消息内容为空");
            return false;
        }

        return true;
    }
}
