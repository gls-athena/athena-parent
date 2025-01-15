package com.gls.athena.sdk.wechat.api.feign;

import com.gls.athena.sdk.wechat.api.domain.ClearQuotaRequest;
import com.gls.athena.sdk.wechat.api.domain.ClearQuotaResponse;
import com.gls.athena.sdk.wechat.api.domain.TokenRequest;
import com.gls.athena.sdk.wechat.api.domain.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 微信公众平台基础接口
 * 主要用于access_token的获取和管理
 *
 * @author george
 * @see <a href="https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Get_access_token.html">获取access_token接口说明</a>
 */
@FeignClient(name = "athena-sdk-wechat-api", contextId = "cgi-bin", path = "/cgi-bin")
public interface CgiBinFeign {

    /**
     * 获取全局接口调用凭据access_token
     *
     * @param request 包含appid和secret的请求参数
     * @return TokenResponse 包含access_token和有效期的响应结果
     * @apiNote 该接口调用有效期为2小时，需要定时刷新
     */
    @GetMapping("/token")
    TokenResponse getAccessToken(@SpringQueryMap TokenRequest request);

    /**
     * 重置API调用频率限制
     *
     * @param request 包含appid的请求参数
     * @return ClearQuotaResponse 清除限制的响应结果
     * @apiNote 每个账号每月只能调用10次，请谨慎使用
     */
    @PostMapping("/clear_quota")
    ClearQuotaResponse clearQuota(@RequestBody ClearQuotaRequest request);
}
