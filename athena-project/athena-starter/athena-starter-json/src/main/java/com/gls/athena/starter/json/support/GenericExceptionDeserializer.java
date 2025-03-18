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
     * <p>
     * 该方法从JSON解析器中读取JSON内容，并将其转换为相应的异常对象。首先，它会验证JSON中是否包含必需的字段，
     * 然后从JSON节点中提取异常类型、异常消息和堆栈跟踪信息，最后根据这些信息创建并返回异常对象。
     *
     * @param jsonParser JSON解析器，用于读取JSON内容
     * @param context    反序列化上下文，提供反序列化过程中所需的配置和环境信息
     * @return 反序列化后的异常对象
     * @throws IOException             当读取JSON内容或处理I/O操作时发生错误时抛出
     * @throws JsonProcessingException 当JSON处理过程中发生错误时抛出
     */
    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        // 从JSON解析器中读取JSON内容并转换为JsonNode对象
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        // 验证JSON节点中是否包含必需的字段
        validateRequiredFields(node);

        // 从JSON节点中提取异常类型、异常消息和堆栈跟踪信息
        String exceptionType = node.get(TYPE_FIELD).asText();
        String message = node.get(MESSAGE_FIELD).asText();
        StackTraceElement[] stackTrace = deserializeStackTrace(node);

        // 根据提取的信息创建并返回异常对象
        return createException(exceptionType, message, stackTrace);
    }

    /**
     * 验证必需的字段是否存在
     * <p>
     * 该方法用于检查传入的JSON节点是否包含必需的字段。如果缺少任何一个必需字段，
     * 将抛出IllegalArgumentException异常。
     *
     * @param node 要验证的JSON节点，通常是一个包含多个字段的JSON对象
     * @throws IllegalArgumentException 当JSON节点中缺少必需的字段时抛出，异常信息会指明具体缺失的字段
     */
    private void validateRequiredFields(JsonNode node) {
        // 检查JSON节点是否包含必需的字段 'type' 和 'message'
        if (!node.has(TYPE_FIELD) || !node.has(MESSAGE_FIELD)) {
            throw new IllegalArgumentException("异常反序列化失败：缺少必需的字段 'type' 或 'message'");
        }
    }

    /**
     * 反序列化堆栈信息
     * <p>
     * 该方法从给定的JSON节点中提取堆栈信息，并将其反序列化为StackTraceElement数组。
     * 如果JSON节点中不包含堆栈信息字段，则返回一个空数组。
     *
     * @param node 包含堆栈信息的JSON节点
     * @return 反序列化后的堆栈信息数组，如果JSON节点中不包含堆栈信息则返回空数组
     */
    private StackTraceElement[] deserializeStackTrace(JsonNode node) {
        // 检查JSON节点是否包含堆栈信息字段
        if (!node.has(STACK_TRACE_FIELD)) {
            return new StackTraceElement[0];
        }

        // 获取堆栈信息节点
        JsonNode stackTraceNode = node.get(STACK_TRACE_FIELD);
        // 初始化堆栈信息数组
        StackTraceElement[] stackTrace = new StackTraceElement[stackTraceNode.size()];

        // 遍历堆栈信息节点，逐个反序列化为StackTraceElement对象
        for (int i = 0; i < stackTraceNode.size(); i++) {
            JsonNode elementNode = stackTraceNode.get(i);
            stackTrace[i] = createStackTraceElement(elementNode);
        }

        return stackTrace;
    }

    /**
     * 根据JSON节点创建单个堆栈元素对象。
     * <p>
     * 该方法从给定的JSON节点中提取类名、方法名和行号，并使用这些信息创建一个StackTraceElement对象。
     * 该方法假设JSON节点包含必要的字段，并且字段的值类型正确。
     *
     * @param elementNode 包含堆栈元素信息的JSON节点，必须包含类名、方法名和行号字段
     * @return 根据JSON节点信息创建的StackTraceElement对象
     */
    private StackTraceElement createStackTraceElement(JsonNode elementNode) {
        // 从JSON节点中提取类名、方法名和行号
        String className = elementNode.get(CLASS_NAME_FIELD).asText();
        String methodName = elementNode.get(METHOD_NAME_FIELD).asText();
        int lineNumber = elementNode.get(LINE_NUMBER_FIELD).asInt();

        // 创建并返回StackTraceElement对象
        return new StackTraceElement(className, methodName, null, lineNumber);
    }

    /**
     * 创建指定类型的异常实例，并设置异常信息和堆栈信息。
     *
     * @param <T>           异常类型泛型参数
     * @param exceptionType 异常类型的全限定名，用于通过反射创建异常实例
     * @param message       异常信息，将作为异常实例的构造参数
     * @param stackTrace    堆栈信息，用于设置异常实例的堆栈跟踪信息，可为空
     * @return 创建的异常实例
     * @throws RuntimeException 如果无法通过反射创建异常实例，则抛出此运行时异常
     */
    @SuppressWarnings("unchecked")
    private T createException(String exceptionType, String message, StackTraceElement[] stackTrace) {
        try {
            // 通过反射创建指定类型的异常实例
            T exception = (T) Class.forName(exceptionType)
                    .getConstructor(String.class)
                    .newInstance(message);

            // 如果提供了堆栈信息，则将其设置到异常实例中
            if (Objects.nonNull(stackTrace)) {
                exception.setStackTrace(stackTrace);
            }

            return exception;
        } catch (ReflectiveOperationException e) {
            // 如果反射操作失败，抛出运行时异常，包含原始异常信息
            throw new RuntimeException("异常反序列化失败：无法创建异常实例 " + exceptionType, e);
        }
    }

}

