package com.gls.athena.starter.word.processor.impl;

import com.gls.athena.starter.word.processor.PlaceholderProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数学表达式处理器
 * 处理 ${math:expression} 格式的数学运算
 *
 * @author athena
 */
@Slf4j
@Component
public class MathProcessor implements PlaceholderProcessor {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_.\\[\\]]*)");

    @Override
    public boolean supports(String placeholder) {
        return placeholder.startsWith("math:");
    }

    @Override
    public String process(String placeholder, Map<String, Object> data) {
        String expression = placeholder.substring(5).trim(); // 移除 "math:" 前缀

        try {
            double value = evaluateMathExpression(expression, data);
            if (value == (long) value) {
                return String.valueOf((long) value);
            } else {
                return String.format("%.2f", value);
            }
        } catch (Exception e) {
            log.warn("Failed to evaluate math expression: {}", expression, e);
            return "NaN";
        }
    }

    @Override
    public int getPriority() {
        return 30;
    }

    private double evaluateMathExpression(String expression, Map<String, Object> data) {
        // 替换变量
        String processedExpr = replaceVariablesInExpression(expression, data);

        // 处理数学函数
        processedExpr = processMathFunctions(processedExpr);

        // 简单的数学表达式计算
        return evaluateSimpleMath(processedExpr);
    }

    private String replaceVariablesInExpression(String expression, Map<String, Object> data) {
        Matcher matcher = VARIABLE_PATTERN.matcher(expression);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            String varName = matcher.group(1);
            Object value = getNestedValue(data, varName);

            result.append(expression, lastEnd, matcher.start());

            if (value instanceof Number) {
                result.append(((Number) value).doubleValue());
            } else {
                result.append(varName);
            }

            lastEnd = matcher.end();
        }

        result.append(expression.substring(lastEnd));
        return result.toString();
    }

    private String processMathFunctions(String expression) {
        expression = expression.replaceAll("\\babs\\(([^)]+)\\)", "Math.abs($1)");
        expression = expression.replaceAll("\\bsqrt\\(([^)]+)\\)", "Math.sqrt($1)");
        expression = expression.replaceAll("\\bpow\\(([^,]+),\\s*([^)]+)\\)", "Math.pow($1,$2)");
        expression = expression.replaceAll("\\bmax\\(([^,]+),\\s*([^)]+)\\)", "Math.max($1,$2)");
        expression = expression.replaceAll("\\bmin\\(([^,]+),\\s*([^)]+)\\)", "Math.min($1,$2)");
        expression = expression.replaceAll("\\bround\\(([^)]+)\\)", "Math.round($1)");
        expression = expression.replaceAll("\\bceil\\(([^)]+)\\)", "Math.ceil($1)");
        expression = expression.replaceAll("\\bfloor\\(([^)]+)\\)", "Math.floor($1)");
        return expression;
    }

    private double evaluateSimpleMath(String expression) {
        // 这里实现一个简单的数学表达式计算器
        // 为了简化，这里只是一个基础实现
        try {
            // 移除空格
            expression = expression.replaceAll("\\s", "");

            // 简单的四则运算支持
            if (expression.contains("+")) {
                String[] parts = expression.split("\\+");
                double result = 0;
                for (String part : parts) {
                    result += Double.parseDouble(part);
                }
                return result;
            } else if (expression.contains("-")) {
                String[] parts = expression.split("-");
                double result = Double.parseDouble(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    result -= Double.parseDouble(parts[i]);
                }
                return result;
            } else if (expression.contains("*")) {
                String[] parts = expression.split("\\*");
                double result = 1;
                for (String part : parts) {
                    result *= Double.parseDouble(part);
                }
                return result;
            } else if (expression.contains("/")) {
                String[] parts = expression.split("/");
                double result = Double.parseDouble(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    result /= Double.parseDouble(parts[i]);
                }
                return result;
            }

            return Double.parseDouble(expression);
        } catch (Exception e) {
            throw new RuntimeException("Invalid math expression: " + expression, e);
        }
    }

    private Object getNestedValue(Map<String, Object> data, String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }

        String[] parts = key.split("\\.");
        Object current = data;

        for (String part : parts) {
            if (current == null) {
                return null;
            }

            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                try {
                    java.lang.reflect.Field field = current.getClass().getDeclaredField(part);
                    field.setAccessible(true);
                    current = field.get(current);
                } catch (Exception e) {
                    return null;
                }
            }
        }

        return current;
    }
}
