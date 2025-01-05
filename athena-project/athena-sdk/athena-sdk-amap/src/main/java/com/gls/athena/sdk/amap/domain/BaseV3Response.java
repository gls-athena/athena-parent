package com.gls.athena.sdk.amap.domain;

import lombok.Data;

/**
 * 高德地图API V3版本基础响应对象
 * 封装了API响应的通用字段，所有V3版本的响应类都应继承此类
 *
 * @author george
 */
@Data
public abstract class BaseV3Response {
    /**
     * 返回结果状态值
     * 值为0或1，1表示成功，0表示失败
     */
    private String status;

    /**
     * 返回状态描述
     * 当status为0时，info会返回具体错误原因
     */
    private String info;

    /**
     * 状态码
     * 10000代表正确，其他数值代表错误
     *
     * @see <a href="https://lbs.amap.com/api/webservice/guide/tools/info">错误码参考</a>
     */
    private String infocode;
}
