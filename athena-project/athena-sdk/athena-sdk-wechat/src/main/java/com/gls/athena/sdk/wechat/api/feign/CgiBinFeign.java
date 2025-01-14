package com.gls.athena.sdk.wechat.api.feign;

import com.gls.athena.sdk.wechat.api.domain.TokenRequest;
import com.gls.athena.sdk.wechat.api.domain.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 微信公众平台 CGI-BIN 接口
 * 该接口封装了微信公众平台基础接口的调用
 * 接口前缀: /cgi-bin
 *
 * @author george
 * @see <a href="https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Get_access_token.html">微信文档</a>
 */
@FeignClient(name = "athena-sdk-wechat-api", contextId = "cgi-bin", path = "/cgi-bin")
public interface CgiBinFeign {

    /**
     * 获取接口调用凭据(access_token)
     * access_token是公众号的全局唯一接口调用凭据，公众号调用各接口时都需使用
     *
     * @param request 包含获取access_token所需的appid和secret参数
     * @return TokenResponse 包含access_token和有效期等信息
     * @see TokenRequest
     * @see TokenResponse
     */
    @GetMapping("/token")
    TokenResponse getAccessToken(@SpringQueryMap TokenRequest request);
}
