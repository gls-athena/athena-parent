package com.gls.athena.starter.json.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Objects;

/**
 * 通用异常反序列化器
 * 用于将JSON格式的异常信息反序列化为对应的异常对象
 *
 * @param <T> 异常类型参数，必须是Exception的子类
 * @author george
 */
public class GenericExceptionDeserializer<T extends Exception> extends JsonDeserializer<T> {

    private static final String TYPE_FIELD = "type";
    private static final String MESSAGE_FIELD = "message";
    private static final String STACK_TRACE_FIELD = "stackTrace";
    private static final String CLASS_NAME_FIELD = "className";
    private static final String METHOD_NAME_FIELD = "methodName";
    private static final String LINE_NUMBER_FIELD = "lineNumber";

    /**
     * 将JSON内容反序列化为异常对象
     *
     * @param jsonParser JSON解析器，用于读取JSON内容
     * @param context    反序列化上下文
     * @return 反序列化后的异常对象
     * @throws IOException             当发生I/O错误时抛出
     * @throws JsonProcessingException 当JSON处理过程中发生错误时抛出
     */
    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        validateRequiredFields(node);

        String exceptionType = node.get(TYPE_FIELD).asText();
        String message = node.get(MESSAGE_FIELD).asText();
        StackTraceElement[] stackTrace = deserializeStackTrace(node);

        return createException(exceptionType, message, stackTrace);
    }

    /**
     * 验证必需的字段是否存在
     *
     * @param node JSON节点
     * @throws IllegalArgumentException 当必需字段缺失时抛出
     */
    private void validateRequiredFields(JsonNode node) {
        if (!node.has(TYPE_FIELD) || !node.has(MESSAGE_FIELD)) {
            throw new IllegalArgumentException("异常反序列化失败：缺少必需的字段 'type' 或 'message'");
        }
    }

    /**
     * 反序列化堆栈信息
     *
     * @param node JSON节点
     * @return 堆栈信息数组，如果没有堆栈信息则返回空数组
     */
    private StackTraceElement[] deserializeStackTrace(JsonNode node) {
        if (!node.has(STACK_TRACE_FIELD)) {
            return new StackTraceElement[0];
        }

        JsonNode stackTraceNode = node.get(STACK_TRACE_FIELD);
        StackTraceElement[] stackTrace = new StackTraceElement[stackTraceNode.size()];

        for (int i = 0; i < stackTraceNode.size(); i++) {
            JsonNode elementNode = stackTraceNode.get(i);
            stackTrace[i] = createStackTraceElement(elementNode);
        }

        return stackTrace;
    }

    /**
     * 创建单个堆栈元素
     *
     * @param elementNode 堆栈元素的JSON节点
     * @return 堆栈元素对象
     */
    private StackTraceElement createStackTraceElement(JsonNode elementNode) {
        String className = elementNode.get(CLASS_NAME_FIELD).asText();
        String methodName = elementNode.get(METHOD_NAME_FIELD).asText();
        int lineNumber = elementNode.get(LINE_NUMBER_FIELD).asInt();
        return new StackTraceElement(className, methodName, null, lineNumber);
    }

    /**
     * 创建异常实例
     *
     * @param exceptionType 异常类型
     * @param message       异常信息
     * @param stackTrace    堆栈信息
     * @return 异常实例
     */
    @SuppressWarnings("unchecked")
    private T createException(String exceptionType, String message, StackTraceElement[] stackTrace) {
        try {
            T exception = (T) Class.forName(exceptionType)
                    .getConstructor(String.class)
                    .newInstance(message);

            if (Objects.nonNull(stackTrace)) {
                exception.setStackTrace(stackTrace);
            }

            return exception;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("异常反序列化失败：无法创建异常实例 " + exceptionType, e);
        }
    }
}

