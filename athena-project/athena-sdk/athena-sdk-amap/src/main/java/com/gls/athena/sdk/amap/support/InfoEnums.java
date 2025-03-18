package com.gls.athena.sdk.amap.support;

import com.gls.athena.common.bean.base.IEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 高德地图API响应状态码枚举
 *
 * <p>该枚举类定义了高德地图API可能返回的所有状态码及其详细信息，包括：
 * <ul>
 *     <li>状态码 - 用于标识具体的响应状态</li>
 *     <li>状态值 - 对应的英文标识</li>
 *     <li>描述信息 - 对状态的中文描述</li>
 *     <li>故障排除建议 - 当出现该状态时的处理建议</li>
 * </ul>
 * </p>
 *
 * @author george
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public enum InfoEnums implements IEnum<String> {
    /**
     * 请求正常
     */
    OK("10000", "OK", "请求正常", "请求正常"),

    /**
     * key不正确或过期
     */
    INVALID_USER_KEY("10001", "INVALID_USER_KEY", "key不正确或过期", "开发者发起请求时，传入的key不正确或者过期"),

    /**
     * 没有权限使用相应的服务或者请求接口的路径拼写错误
     */
    SERVICE_NOT_AVAILABLE("10002", "SERVICE_NOT_AVAILABLE", "没有权限使用相应的服务或者请求接口的路径拼写错误", "1.开发者没有权限使用相应的服务，例如：开发者申请了WEB定位功能的key，却使用该key访问逆地理编码功能时，就会返回该错误。反之亦然。\n2.开发者请求接口的路径拼写错误。例如：正确的https://restapi.amap.com/v3/ip在程序中被拼装写了https://restapi.amap.com/vv3/ip"),

    /**
     * 访问已超出日访问量
     */
    DAILY_QUERY_OVER_LIMIT("10003", "DAILY_QUERY_OVER_LIMIT", "访问已超出日访问量", "开发者的日访问量超限，被系统自动封停，第二天0:00会自动解封。"),

    /**
     * 单位时间内访问过于频繁
     */
    ACCESS_TOO_FREQUENT("10004", "ACCESS_TOO_FREQUENT", "单位时间内访问过于频繁", "开发者的单位时间内（1分钟）访问量超限，被系统自动封停，下一分钟自动解封。"),

    /**
     * IP白名单出错，发送请求的服务器IP不在IP白名单内
     */
    INVALID_USER_IP("10005", "INVALID_USER_IP", "IP白名单出错，发送请求的服务器IP不在IP白名单内", "开发者在LBS官网控制台设置的IP白名单不正确。白名单中未添加对应服务器的出口IP。可到\"控制台>配置\"  中设定IP白名单。"),

    /**
     * 绑定域名无效
     */
    INVALID_USER_DOMAIN("10006", "INVALID_USER_DOMAIN", "绑定域名无效", "开发者绑定的域名无效，需要在官网控制台重新设置"),

    /**
     * 数字签名未通过验证
     */
    INVALID_USER_SIGNATURE("10007", "INVALID_USER_SIGNATURE", "数字签名未通过验证", "开发者签名未通过开发者在key控制台中，开启了“数字签名”功能，但没有按照指定算法生成“数字签名”。"),

    /**
     * MD5安全码未通过验证
     */
    INVALID_USER_SCODE("10008", "INVALID_USER_SCODE", "MD5安全码未通过验证", "需要开发者判定key绑定的SHA1,package是否与sdk包里的一致"),

    /**
     * 请求key与绑定平台不符
     */
    USERKEY_PLAT_NOMATCH("10009", "USERKEY_PLAT_NOMATCH", "请求key与绑定平台不符", "请求中使用的key与绑定平台不符，例如：开发者申请的是js api的key，却用来调web服务接口"),

    /**
     * IP访问超限
     */
    IP_QUERY_OVER_LIMIT("10010", "IP_QUERY_OVER_LIMIT", "IP访问超限", "未设定IP白名单的开发者使用key发起请求，从单个IP向服务器发送的请求次数超出限制，被系统自动封停。封停后无法自动恢复，需要提交工单联系我们。"),

    /**
     * 服务不支持https请求
     */
    NOT_SUPPORT_HTTPS("10011", "NOT_SUPPORT_HTTPS", "服务不支持https请求", "服务不支持https请求，如果需要申请支持，请提交工单联系我们"),

    /**
     * 权限不足，服务请求被拒绝
     */
    INSUFFICIENT_PRIVILEGES("10012", "INSUFFICIENT_PRIVILEGES", "权限不足，服务请求被拒绝", "由于不具备请求该服务的权限，所以服务被拒绝。"),

    /**
     * Key被删除
     */
    USER_KEY_RECYCLED("10013", "USER_KEY_RECYCLED", "Key被删除", "开发者删除了key，key被删除后无法正常使用"),

    /**
     * 云图服务QPS超限
     */
    QPS_HAS_EXCEEDED_THE_LIMIT("10014", "QPS_HAS_EXCEEDED_THE_LIMIT", "云图服务QPS超限", "QPS超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),

    /**
     * 受单机QPS限流限制
     */
    GATEWAY_TIMEOUT("10015", "GATEWAY_TIMEOUT", "受单机QPS限流限制", "受单机QPS限流限制时出现该问题，建议降低请求的QPS或在控制台提工单联系我们"),

    /**
     * 服务器负载过高
     */
    SERVER_IS_BUSY("10016", "SERVER_IS_BUSY", "服务器负载过高", "服务器负载过高，请稍后再试"),

    /**
     * 所请求的资源不可用
     */
    RESOURCE_UNAVAILABLE("10017", "RESOURCE_UNAVAILABLE", "所请求的资源不可用", "所请求的资源不可用"),

    /**
     * 使用的某个服务总QPS超限
     */
    CQPS_HAS_EXCEEDED_THE_LIMIT("10019", "CQPS_HAS_EXCEEDED_THE_LIMIT", "使用的某个服务总QPS超限", "QPS超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),

    /**
     * 某个Key使用某个服务接口QPS超出限制
     */
    CKQPS_HAS_EXCEEDED_THE_LIMIT("10020", "CKQPS_HAS_EXCEEDED_THE_LIMIT", "某个Key使用某个服务接口QPS超出限制", "QPS超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),

    /**
     * 账号使用某个服务接口QPS超出限制
     */
    CUQPS_HAS_EXCEEDED_THE_LIMIT("10021", "CUQPS_HAS_EXCEEDED_THE_LIMIT", "账号使用某个服务接口QPS超出限制", "QPS超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),

    /**
     * 账号处于被封禁状态
     */
    INVALID_REQUEST("10026", "INVALID_REQUEST", "账号处于被封禁状态", "由于违规行为账号被封禁不可用，如有异议请登录控制台提交工单进行申诉"),

    /**
     * 某个Key的QPS超出限制
     */
    ABROAD_DAILY_QUERY_OVER_LIMIT("10029", "ABROAD_DAILY_QUERY_OVER_LIMIT", "某个Key的QPS超出限制", "QPS超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),

    /**
     * 请求的接口权限过期
     */
    NO_EFFECTIVE_INTERFACE("10041", "NO_EFFECTIVE_INTERFACE", "请求的接口权限过期", "开发者发起请求时，请求的接口权限过期。请提交工单联系我们"),

    /**
     * 账号维度日调用量超出限制
     */
    USER_DAILY_QUERY_OVER_LIMIT("10044", "USER_DAILY_QUERY_OVER_LIMIT", "账号维度日调用量超出限制", "账号维度日调用量超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),

    /**
     * 账号维度海外服务日调用量超出限制
     */
    USER_ABROAD_DAILY_QUERY_OVER_LIMIT("10045", "USER_ABROAD_DAILY_QUERY_OVER_LIMIT", "账号维度海外服务日调用量超出限制", "账号维度海外服务接口日调用量超出限制，超出部分的请求被拒绝。限流阈值内的请求依旧会正常返回"),

    /**
     * 请求参数非法
     */
    INVALID_PARAMS("20000", "INVALID_PARAMS", "请求参数非法", "请求参数的值没有按照规范要求填写。例如，某参数值域范围为[1,3],开发者误填了’4’"),

    /**
     * 缺少必填参数
     */
    MISSING_REQUIRED_PARAMS("20001", "MISSING_REQUIRED_PARAMS", "缺少必填参数", "缺少接口中要求的必填参数"),

    /**
     * 请求协议非法
     */
    ILLEGAL_REQUEST("20002", "ILLEGAL_REQUEST", "请求协议非法", "请求协议非法\n比如某接口仅支持get请求，结果用了POST方式"),

    /**
     * 其他未知错误
     */
    UNKNOWN_ERROR("20003", "UNKNOWN_ERROR", "其他未知错误", "其他未知错误"),

    /**
     * 查询坐标或规划点（包括起点、终点、途经点）在海外，但没有海外地图权限
     */
    INSUFFICIENT_ABROAD_PRIVILEGES("20011", "INSUFFICIENT_ABROAD_PRIVILEGES", "查询坐标或规划点（包括起点、终点、途经点）在海外，但没有海外地图权限", "使用逆地理编码接口、输入提示接口、周边搜索接口、路径规划接口时可能出现该问题，规划点（包括起点、终点、途经点）不在中国陆地范围内"),

    /**
     * 查询信息存在非法内容
     */
    ILLEGAL_CONTENT("20012", "ILLEGAL_CONTENT", "查询信息存在非法内容", "使用搜索接口时可能出现该问题，通常是由于查询内容非法导致"),

    /**
     * 规划点（包括起点、终点、途经点）不在中国陆地范围内
     */
    OUT_OF_SERVICE("20800", "OUT_OF_SERVICE", "规划点（包括起点、终点、途经点）不在中国陆地范围内", "使用路径规划服务接口时可能出现该问题，规划点（包括起点、终点、途经点）不在中国大陆陆地范围内"),

    /**
     * 划点（起点、终点、途经点）附近搜不到路
     */
    NO_ROADS_NEARBY("20801", "NO_ROADS_NEARBY", "划点（起点、终点、途经点）附近搜不到路", "使用路径规划服务接口时可能出现该问题，划点（起点、终点、途经点）附近搜不到路"),

    /**
     * 路线计算失败，通常是由于道路连通关系导致
     */
    ROUTE_FAIL("20802", "ROUTE_FAIL", "路线计算失败，通常是由于道路连通关系导致", "使用路径规划服务接口时可能出现该问题，路线计算失败，通常是由于道路连通关系导致"),

    /**
     * 起点终点距离过长
     */
    OVER_DIRECTION_RANGE("20803", "OVER_DIRECTION_RANGE", "起点终点距离过长。", "使用路径规划服务接口时可能出现该问题，路线计算失败，通常是由于道路起点和终点距离过长导致。"),

    /**
     * 服务响应失败
     */
    ENGINE_RESPONSE_DATA_ERROR("30000", "ENGINE_RESPONSE_DATA_ERROR", "服务响应失败。", "出现3开头的错误码，建议先检查传入参数是否正确，若无法解决，请详细描述错误复现信息，提工单给我们。（大数据接口请直接跟负责商务反馈）\n如，30001、30002、30003、32000、32001、32002、32003、32200、32201、32202、32203。"),

    /**
     * 余额耗尽
     */
    QUOTA_PLAN_RUN_OUT("40000", "QUOTA_PLAN_RUN_OUT", "余额耗尽", "所购买服务的余额耗尽，无法继续使用服务"),

    /**
     * 围栏个数达到上限
     */
    GEOFENCE_MAX_COUNT_REACHED("40001", "GEOFENCE_MAX_COUNT_REACHED", "围栏个数达到上限", "Key可创建的地理围栏的数量，已达上限。"),

    /**
     * 购买服务到期
     */
    SERVICE_EXPIRED("40002", "SERVICE_EXPIRED", "购买服务到期", "所购买的服务期限已到，无法继续使用"),

    /**
     * 海外服务余额耗尽
     */
    ABROAD_QUOTA_PLAN_RUN_OUT("40003", "ABROAD_QUOTA_PLAN_RUN_OUT", "海外服务余额耗尽", "所购买服务的海外余额耗尽，无法继续使用服务");

    private final String code;
    private final String name;
    private final String description;
    private final String troubleshooting;

    /**
     * 根据状态值获取对应的枚举实例
     *
     * <p>该方法通过遍历所有枚举值，返回与给定状态值匹配的枚举实例。
     * 如果未找到匹配的枚举值，则返回null。</p>
     *
     * @param info 状态值（英文标识）
     * @return 匹配的枚举实例，如果未找到则返回null
     */
    public static InfoEnums getByValue(String info) {
        // 调用IEnum.fromName方法，根据传入的状态值查找对应的枚举实例
        return IEnum.fromName(InfoEnums.class, info, false);
    }

    /**
     * 根据状态码获取对应的枚举实例
     *
     * <p>该方法通过遍历所有枚举值，返回与给定状态码匹配的枚举实例。
     * 如果未找到匹配的枚举值，则返回null。</p>
     *
     * @param code 状态码，用于匹配枚举实例
     * @return 匹配的枚举实例，如果未找到则返回null
     */
    public static InfoEnums getByCode(String code) {
        // 使用IEnum工具类根据状态码获取对应的枚举实例
        return IEnum.of(InfoEnums.class, code);
    }

}