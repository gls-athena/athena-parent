package com.gls.athena.starter.aliyun.core.support;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.gls.athena.starter.aliyun.core.config.AliyunCoreProperties;
import lombok.experimental.UtilityClass;

/**
 * 阿里云操作工具类
 * 提供阿里云AK/STS认证方式的客户端创建和角色扮演功能
 *
 * @author george
 */
@UtilityClass
public class AliyunHelper {
    /**
     * 创建阿里云AcsClient客户端
     * 支持AK和STS两种认证方式
     *
     * @param client 阿里云客户端配置，包含认证信息和区域配置
     * @return IAcsClient 阿里云SDK客户端实例
     * @throws IllegalArgumentException 当认证模式不支持时抛出
     */
    public IAcsClient createAcsClient(AliyunCoreProperties.Client client) {
        DefaultProfile profile = switch (client.getAuthMode()) {
            case AS_AK -> DefaultProfile.getProfile(client.getRegionId(),
                    client.getAccessKeyId(), client.getAccessKeySecret());
            case STS -> DefaultProfile.getProfile(client.getRegionId(),
                    client.getAccessKeyId(), client.getAccessKeySecret(),
                    client.getSecurityToken());
        };
        return new DefaultAcsClient(profile);
    }

    /**
     * 获取临时安全凭证(STS Token)
     *
     * @param client  阿里云客户端配置
     * @param request AssumeRole请求参数
     * @return AssumeRoleResponse STS凭证响应
     * @throws RuntimeException 获取STS Token失败时抛出
     */
    public AssumeRoleResponse getAssumeRole(AliyunCoreProperties.Client client,
                                            AssumeRoleRequest request) {
        IAcsClient acsClient = createAcsClient(client);
        try {
            return acsClient.getAcsResponse(request);
        } catch (Exception e) {
            throw new RuntimeException("获取STS Token失败", e);
        }
    }
}
