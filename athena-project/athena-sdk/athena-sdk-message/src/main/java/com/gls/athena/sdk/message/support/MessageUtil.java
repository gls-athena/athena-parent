package com.gls.athena.sdk.message.support;

import com.gls.athena.sdk.message.domain.MessageDto;
import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * 消息工具类
 * 职责：提供便捷的消息发送方法，组合构建器和发布器
 *
 * @author george
 */
@UtilityClass
public class MessageUtil {

    /**
     * 发送短信
     *
     * @param mobile       手机号
     * @param templateCode 模板编号
     * @param params       参数
     */
    public void sendSms(String mobile, String templateCode, Map<String, Object> params) {
        MessageDto messageDto = MessageBuilder.buildSms(mobile, templateCode, params);
        MessagePublisher.publish(messageDto);
    }

    /**
     * 发送邮件
     *
     * @param email        邮箱地址
     * @param title        邮件标题
     * @param content      邮件内容
     * @param templateCode 模板编号
     * @param params       参数
     */
    public void sendEmail(String email, String title, String content, String templateCode, Map<String, Object> params) {
        MessageDto messageDto = MessageBuilder.buildEmail(email, title, content, templateCode, params);
        MessagePublisher.publish(messageDto);
    }

    /**
     * 发送站内信
     *
     * @param userId  用户ID
     * @param title   消息标题
     * @param content 消息内容
     */
    public void sendSiteMessage(String userId, String title, String content) {
        MessageDto messageDto = MessageBuilder.buildSiteMessage(userId, title, content);
        MessagePublisher.publish(messageDto);
    }

    /**
     * 发送微信消息
     *
     * @param openId       微信OpenID
     * @param templateCode 模板编号
     * @param params       参数
     */
    public void sendWechatMessage(String openId, String templateCode, Map<String, Object> params) {
        MessageDto messageDto = MessageBuilder.buildWechatMessage(openId, templateCode, params);
        MessagePublisher.publish(messageDto);
    }

    /**
     * 发送钉钉消息
     *
     * @param userId  用户ID
     * @param content 消息内容
     */
    public void sendDingTalkMessage(String userId, String content) {
        MessageDto messageDto = MessageBuilder.buildDingTalkMessage(userId, content);
        MessagePublisher.publish(messageDto);
    }

    /**
     * 发送飞书消息
     *
     * @param userId  用户ID
     * @param content 消息内容
     */
    public void sendFeishuMessage(String userId, String content) {
        MessageDto messageDto = MessageBuilder.buildFeishuMessage(userId, content);
        MessagePublisher.publish(messageDto);
    }

    /**
     * 发送企业微信消息
     *
     * @param userId  用户ID
     * @param content 消息内容
     */
    public void sendWechatWorkMessage(String userId, String content) {
        MessageDto messageDto = MessageBuilder.buildWechatWorkMessage(userId, content);
        MessagePublisher.publish(messageDto);
    }
}
