package com.gls.athena.starter.core.support;

import lombok.experimental.UtilityClass;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * AOP切面工具类
 * <p>
 * 提供切面编程中常用的工具方法，包括参数提取、异常堆栈转换等功能
 *
 * @author george
 */
@UtilityClass
public class AspectUtil {

    /**
     * 提取方法调用参数
     * <p>
     * 将方法调用的参数名和参数值封装为Map集合
     *
     * @param point 切点对象，包含方法签名和参数信息
     * @return 参数名为键、参数值为值的Map集合
     */
    public Map<String, Object> getParams(ProceedingJoinPoint point) {
        Map<String, Object> params = new HashMap<>();

        Object[] args = point.getArgs();
        String[] paramNames = ((MethodSignature) point.getSignature()).getParameterNames();

        // 将参数名与参数值建立映射关系
        for (int i = 0; i < args.length; i++) {
            params.put(paramNames[i], args[i]);
        }

        return params;
    }

    /**
     * 将异常堆栈信息转换为字符串
     * <p>
     * 用于日志记录或错误信息展示
     *
     * @param throwable 异常对象
     * @return 格式化的堆栈信息字符串
     */
    public String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
