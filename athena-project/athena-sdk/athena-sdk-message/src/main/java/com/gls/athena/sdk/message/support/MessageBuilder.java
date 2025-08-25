package com.gls.athena.sdk.message.support;

import com.gls.athena.sdk.message.domain.MessageDto;
import com.gls.athena.sdk.message.domain.MessageType;
import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * 消息构建器
 * 职责：专门负责消息对象的构建
 *
 * @author george
 */
@UtilityClass
public class MessageBuilder {

    /**
     * 创建短信消息
     *
     * @param mobile       手机号
     * @param templateCode 模板编号
     * @param params       参数
     * @return 消息对象
     */
    public MessageDto buildSms(String mobile, String templateCode, Map<String, Object> params) {
        return new MessageDto()
                .setType(MessageType.SMS)
                .setReceiver(mobile)
                .setTemplate(templateCode)
                .setParams(params);
    }

    /**
     * 创建邮件消息
     *
     * @param email        邮箱地址
     * @param title        邮件标题
     * @param content      邮件内容
     * @param templateCode 模板编号
     * @param params       参数
     * @return 消息对象
     */
    public MessageDto buildEmail(String email, String title, String content, String templateCode, Map<String, Object> params) {
        return new MessageDto()
                .setType(MessageType.EMAIL)
                .setReceiver(email)
                .setTitle(title)
                .setContent(content)
                .setTemplate(templateCode)
                .setParams(params);
    }

    /**
     * 创建站内信消息
     *
     * @param userId  用户ID
     * @param title   消息标题
     * @param content 消息内容
     * @return 消息对象
     */
    public MessageDto buildSiteMessage(String userId, String title, String content) {
        return new MessageDto()
                .setType(MessageType.SITE_MESSAGE)
                .setReceiver(userId)
                .setTitle(title)
                .setContent(content);
    }

    /**
     * 创建微信消息
     *
     * @param openId       微信OpenID
     * @param templateCode 模板编号
     * @param params       参数
     * @return 消息对象
     */
    public MessageDto buildWechatMessage(String openId, String templateCode, Map<String, Object> params) {
        return new MessageDto()
                .setType(MessageType.WECHAT)
                .setReceiver(openId)
                .setTemplate(templateCode)
                .setParams(params);
    }

    /**
     * 创建钉钉消息
     *
     * @param userId  用户ID
     * @param content 消息内容
     * @return 消息对象
     */
    public MessageDto buildDingTalkMessage(String userId, String content) {
        return new MessageDto()
                .setType(MessageType.DING_TALK)
                .setReceiver(userId)
                .setContent(content);
    }

    /**
     * 创建飞书消息
     *
     * @param userId  用户ID
     * @param content 消息内容
     * @return 消息对象
     */
    public MessageDto buildFeishuMessage(String userId, String content) {
        return new MessageDto()
                .setType(MessageType.FEISHU)
                .setReceiver(userId)
                .setContent(content);
    }

    /**
     * 创建企业微信消息
     *
     * @param userId  用户ID
     * @param content 消息内容
     * @return 消息对象
     */
    public MessageDto buildWechatWorkMessage(String userId, String content) {
        return new MessageDto()
                .setType(MessageType.WECHAT_WORK)
                .setReceiver(userId)
                .setContent(content);
    }
}
