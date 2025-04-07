package com.gls.athena.starter.core.support;

import lombok.experimental.UtilityClass;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.HashMap;
import java.util.Map;

/**
 * 切面工具类
 *
 * @author george
 */
@UtilityClass
public class AspectUtil {
    /**
     * 获取方法调用的参数名和参数值，并将其封装为Map返回。
     *
     * @param point ProceedingJoinPoint对象，表示当前正在执行的方法调用，包含方法签名和参数信息。
     * @return Map<String, Object> 返回一个Map，其中键为参数名，值为对应的参数值。
     */
    public Map<String, Object> getParams(ProceedingJoinPoint point) {
        // 创建一个HashMap用于存储参数名和参数值
        Map<String, Object> params = new HashMap<>();

        // 获取方法调用的参数值数组
        Object[] args = point.getArgs();

        // 获取方法签名的参数名数组
        String[] paramNames = ((MethodSignature) point.getSignature()).getParameterNames();

        // 遍历参数数组，将参数名和参数值存入Map中
        for (int i = 0; i < args.length; i++) {
            params.put(paramNames[i], args[i]);
        }

        return params;
    }
}
