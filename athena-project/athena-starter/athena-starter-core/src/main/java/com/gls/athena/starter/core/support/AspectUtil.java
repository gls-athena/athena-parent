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
     * <p>
     * 通过方法签名解析参数名称，并与实际调用参数值进行映射。该方法适用于需要动态获取方法参数元信息的场景。
     *
     * @param point ProceedingJoinPoint对象，提供方法调用的上下文信息，包含方法签名和实际参数值
     * @return Map<String, Object> 参数名作为键，对应参数值作为值的映射集合，保证参数顺序与声明顺序一致
     */
    public Map<String, Object> getParams(ProceedingJoinPoint point) {
        // 创建参数容器，初始容量根据参数数量动态适配
        Map<String, Object> params = new HashMap<>();

        // 提取方法调用的实际参数值列表
        Object[] args = point.getArgs();

        // 通过方法签名类型转换获取参数名称元数据
        String[] paramNames = ((MethodSignature) point.getSignature()).getParameterNames();

        // 构建参数名值映射关系，索引对齐保证同名参数的正确匹配
        for (int i = 0; i < args.length; i++) {
            params.put(paramNames[i], args[i]);
        }

        return params;
    }

}
