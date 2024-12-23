package com.gls.athena.starter.amap.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础请求
 *
 * @author george
 */
@Data
public abstract class BaseRequest implements Serializable {
    /**
     * 高德key
     * 用户在高德地图官网 申请 Web 服务 API 类型 Key
     */
    private String key;
    /**
     * 数字签名
     * 请参考 数字签名获取和使用方法
     */
    private String sig;
    /**
     * 返回数据格式类型
     * 可选输入内容包括：JSON，XML。设置 JSON 返回结果数据将会以 JSON 结构构成；如果设置 XML 返回结果数据将以 XML 结构构成。
     */
    private String output = "JSON";
    /**
     * 回调函数
     * callback 值是用户定义的函数名称，此参数只在 output 参数设置为 JSON 时有效。
     */
    private String callback;
}
