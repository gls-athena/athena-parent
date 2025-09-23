package com.gls.athena.starter.async.util;

import lombok.experimental.UtilityClass;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * AOP切面工具类
 * <p>
 * 提供切面编程中常用的工具方法，包括参数提取、异常堆栈转换等功能
 *
 * @author george
 */
@UtilityClass
public class AopUtil {

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

        if (point == null) {
            return params;
        }

        Object[] args = point.getArgs();
        String[] paramNames = null;

        // 类型安全检查
        if (point.getSignature() instanceof MethodSignature) {
            paramNames = ((MethodSignature) point.getSignature()).getParameterNames();
        }

        // 安全性检查：防止空指针或长度不一致
        if (args == null || paramNames == null || args.length != paramNames.length) {
            return params;
        }

        // 预设容量避免频繁扩容
        params = new HashMap<>((int) (args.length / 0.75f) + 1);

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
        if (throwable == null) {
            return "";
        }
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        } catch (Exception e) {
            return "Error getting stack trace: " + e.getMessage();
        }
    }

    /**
     * 获取方法名称
     *
     * @param point 切点对象
     * @return 方法名称
     */
    public String getMethodName(ProceedingJoinPoint point) {
        return Optional.ofNullable(point)
                .map(ProceedingJoinPoint::getSignature)
                .map(signature -> signature.getName())
                .orElse("");
    }

    /**
     * 获取类名
     *
     * @param point 切点对象
     * @return 类名
     */
    public String getClassName(ProceedingJoinPoint point) {
        return Optional.ofNullable(point)
                .map(ProceedingJoinPoint::getTarget)
                .map(target -> target.getClass().getSimpleName())
                .orElse("");
    }

    /**
     * 获取方法上的注解
     *
     * @param point           切点对象
     * @param annotationClass 注解类型
     * @param <T>             注解类型
     * @return 注解实例，如果不存在则返回null
     */
    public <T extends Annotation> T getMethodAnnotation(ProceedingJoinPoint point, Class<T> annotationClass) {
        if (point == null || annotationClass == null) {
            return null;
        }

        try {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            return method.getAnnotation(annotationClass);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取执行时间信息
     *
     * @param startTime 开始时间（毫秒）
     * @return 执行时间信息字符串
     */
    public String getExecutionTimeInfo(long startTime) {
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        if (executionTime < 1000) {
            return executionTime + "ms";
        } else if (executionTime < 60000) {
            return String.format("%.2fs", executionTime / 1000.0);
        } else {
            long minutes = executionTime / 60000;
            long seconds = (executionTime % 60000) / 1000;
            return minutes + "m" + seconds + "s";
        }
    }
}
