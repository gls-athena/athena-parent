package com.gls.athena.sdk.amap.domain.v3;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * IP地理位置定位请求对象
 *
 * <p>该类用于封装高德地图 IP 定位 API V3 版本的请求参数。
 * 继承自 {@link BaseV3Request} 基础请求类，包含公共请求参数。</p>
 *
 * @author george
 * @see BaseV3Request
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class IpV3Request extends BaseV3Request {

    /**
     * IP地址
     * <p>要查询的 IP 地址，支持 IPv4 和 IPv6 格式</p>
     * <p>若不传入 IP 地址，则默认使用发起请求的 IP 地址</p>
     */
    private String ip;
}
