package com.gls.athena.sdk.feishu.support;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.sdk.feishu.config.FeishuProperties;
import com.gls.athena.sdk.feishu.domain.ImageContent;
import com.gls.athena.sdk.feishu.domain.InteractiveContent;
import com.gls.athena.sdk.feishu.domain.PostContent;
import com.gls.athena.sdk.feishu.domain.TextContent;
import com.lark.oapi.Client;
import com.lark.oapi.core.cache.ICache;
import com.lark.oapi.core.enums.AppType;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.enums.MsgTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageReq;
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;

/**
 * 飞书客户端
 *
 * @author george
 */
@Slf4j
public class FeishuClient {
    /**
     * 对象映射
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 配置

        // 配置空值不序列化
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 客户端
     */
    @Delegate
    private final Client client;

    /**
     * 构造函数
     *
     * @param feishuProperties 飞书配置
     * @param cache            缓存
     */
    public FeishuClient(FeishuProperties feishuProperties, ObjectProvider<ICache> cache) {
        if (StrUtil.isBlank(feishuProperties.getAppId()) || StrUtil.isBlank(feishuProperties.getAppSecret())) {
            throw new IllegalArgumentException("飞书配置错误");
        }
        Client.Builder builder = Client.newBuilder(feishuProperties.getAppId(), feishuProperties.getAppSecret());
        // 应用类型
        if (feishuProperties.getAppType().equals(AppType.MARKETPLACE)) {
            builder.marketplaceApp();
        }
        // 是否开启debug模式
        builder.logReqAtDebug(feishuProperties.isDebugFlag());
        // 是否开启token缓存
        if (!feishuProperties.isTokenCacheFlag()) {
            builder.disableTokenCache();
        }
        // 工作台凭证
        if (StrUtil.isNotBlank(feishuProperties.getHelpDeskId()) && StrUtil.isNotBlank(feishuProperties.getHelpDeskSecret())) {
            builder.helpDeskCredential(feishuProperties.getHelpDeskId(), feishuProperties.getHelpDeskSecret());
        }
        // 超时时间
        if (feishuProperties.getRequestTimeout() > 0 && feishuProperties.getRequestTimeoutUnit() != null) {
            builder.requestTimeout(feishuProperties.getRequestTimeout(), feishuProperties.getRequestTimeoutUnit());
        }
        // 缓存
        cache.ifAvailable(builder::tokenCache);
        // 创建
        this.client = builder.build();
    }

    /**
     * 发送文本消息
     *
     * @param receiveId     接收者id
     * @param receiveIdType 接收者id类型
     * @param content       消息内容
     */
    public CreateMessageResp sendTextMsg(String receiveId, CreateMessageReceiveIdTypeEnum receiveIdType, TextContent content) throws Exception {
        return sendMsg(receiveId, receiveIdType, MsgTypeEnum.MSG_TYPE_TEXT, content);
    }

    /**
     * 发送富文本消息
     *
     * @param receiveId     接收者id
     * @param receiveIdType 接收者id类型
     * @param content       消息内容
     */
    public CreateMessageResp sendPostMsg(String receiveId, CreateMessageReceiveIdTypeEnum receiveIdType, PostContent content) throws Exception {
        return sendMsg(receiveId, receiveIdType, MsgTypeEnum.MSG_TYPE_POST, content);
    }

    /**
     * 发送图片消息
     *
     * @param receiveId     接收者id
     * @param receiveIdType 接收者id类型
     * @param content       消息内容
     */
    public CreateMessageResp sendImageMsg(String receiveId, CreateMessageReceiveIdTypeEnum receiveIdType, ImageContent content) throws Exception {
        return sendMsg(receiveId, receiveIdType, MsgTypeEnum.MSG_TYPE_IMAGE, content);
    }

    /**
     * 发送卡片消息
     *
     * @param receiveId     接收者id
     * @param receiveIdType 接收者id类型
     * @param content       消息内容
     */
    public CreateMessageResp sendInteractiveMsg(String receiveId, CreateMessageReceiveIdTypeEnum receiveIdType, InteractiveContent content) throws Exception {
        return sendMsg(receiveId, receiveIdType, MsgTypeEnum.MSG_TYPE_INTERACTIVE, content);
    }

    /**
     * 发送消息
     *
     * @param receiveId     接收者id
     * @param receiveIdType 接收者id类型
     * @param msgType       消息类型
     * @param content       消息内容
     */
    private CreateMessageResp sendMsg(String receiveId, CreateMessageReceiveIdTypeEnum receiveIdType, MsgTypeEnum msgType, Object content) throws Exception {
        String contentStr = OBJECT_MAPPER.writeValueAsString(content);
        log.info("发送消息:{}", contentStr);
        return client.im().message().create(CreateMessageReq.newBuilder()
                .receiveIdType(receiveIdType)
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .receiveId(receiveId)
                        .msgType(msgType.getValue())
                        .content(contentStr)
                        .uuid(IdUtil.randomUUID())
                        .build())
                .build());
    }
}
