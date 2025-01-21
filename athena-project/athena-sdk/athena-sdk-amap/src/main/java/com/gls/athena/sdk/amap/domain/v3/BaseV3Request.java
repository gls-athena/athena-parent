package com.gls.athena.sdk.amap.domain.v3;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 高德地图API V3版本基础请求参数
 *
 * @author george
 */
@Data
@Accessors(chain = true)
public abstract class BaseV3Request {
    /**
     * 高德开发者Key
     * <p>
     * 在高德开放平台官网申请的Web服务API类型的Key
     * 该参数作为访问高德地图Web服务的必要标识
     */
    private String key;
    /**
     * 数字签名
     * <p>
     * 数字签名用于请求安全性验证
     * 生成规则请参考高德开放平台"数字签名生成和使用说明"
     */
    private String sig;
    /**
     * 返回数据格式
     * <p>
     * 可选值：
     * - JSON：返回JSON格式的数据（默认）
     * - XML：返回XML格式的数据
     */
    private String output = "JSON";
    /**
     * JSONP回调函数名称
     * <p>
     * 仅在output=JSON时生效
     * 用于解决浏览器跨域请求的问题
     */
    private String callback;
}
