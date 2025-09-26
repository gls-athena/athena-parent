package com.gls.athena.sdk.amap.exception;

import com.gls.athena.sdk.amap.support.InfoEnums;
import lombok.Getter;

/**
 * 高德地图API异常基类
 * <p>
 * 该异常类用于封装高德地图API返回的各种错误信息，
 * 提供统一的异常处理机制。
 *
 * @author george
 */
@Getter
public class AmapException extends RuntimeException {

    /**
     * 错误码
     */
    private final String errorCode;

    /**
     * 错误信息
     */
    private final String errorMessage;

    /**
     * API方法名
     */
    private final String methodName;

    public AmapException(String errorCode, String errorMessage) {
        this(errorCode, errorMessage, null);
    }

    public AmapException(String errorCode, String errorMessage, String methodName) {
        super(String.format("高德地图API错误 - 错误码: %s, 错误信息: %s, 方法: %s",
                errorCode, errorMessage, methodName));
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.methodName = methodName;
    }

    public AmapException(InfoEnums infoEnum, String methodName) {
        this(infoEnum.getCode(), infoEnum.getDescription(), methodName);
    }

    /**
     * 判断是否为认证相关错误
     */
    public boolean isAuthError() {
        return "10001".equals(errorCode) || "10005".equals(errorCode) ||
                "10006".equals(errorCode) || "10007".equals(errorCode);
    }

    /**
     * 判断是否为配额限制错误
     */
    public boolean isQuotaError() {
        return "10003".equals(errorCode) || "10004".equals(errorCode) ||
                "10010".equals(errorCode) || "10014".equals(errorCode);
    }

    /**
     * 判断是否为参数错误
     */
    public boolean isParameterError() {
        return "20000".equals(errorCode) || "20001".equals(errorCode) ||
                "20002".equals(errorCode);
    }
}
