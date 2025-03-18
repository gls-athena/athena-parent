package com.gls.athena.starter.json.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;

/**
 * 通用异常序列化器
 * 将异常对象序列化为包含详细信息的JSON格式
 *
 * @author george
 */
public class GenericExceptionSerializer<T extends Exception> extends JsonSerializer<T> {

    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_STACK_TRACE = "stackTrace";
    private static final String FIELD_CAUSE = "cause";
    private static final String FIELD_CLASS_NAME = "className";
    private static final String FIELD_METHOD_NAME = "methodName";
    private static final String FIELD_LINE_NUMBER = "lineNumber";

    /**
     * 序列化异常对象为JSON格式。
     * <p>
     * 该方法是`JsonSerializer`接口的实现，用于将指定的异常对象序列化为JSON格式。
     * 序列化过程包括生成JSON对象的开始标记、写入异常内容以及生成JSON对象的结束标记。
     *
     * @param exception   需要序列化的异常对象，类型为泛型T。
     * @param gen         JsonGenerator对象，用于生成JSON内容。
     * @param serializers SerializerProvider对象，提供序列化过程中所需的序列化器。
     * @throws IOException 如果在序列化过程中发生I/O错误，则抛出该异常。
     */
    @Override
    public void serialize(T exception, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 开始生成JSON对象
        gen.writeStartObject();

        // 写入异常的具体内容
        writeExceptionContent(exception, gen);

        // 结束生成JSON对象
        gen.writeEndObject();
    }

    /**
     * 序列化带有类型信息的异常对象。
     * 该函数用于将异常对象序列化为JSON格式，并在序列化过程中包含类型信息。
     * 通过使用TypeSerializer，可以在生成的JSON中嵌入类型信息，以便在反序列化时能够正确识别类型。
     *
     * @param exception   需要序列化的异常对象，类型为T。
     * @param gen         JsonGenerator对象，用于生成JSON内容。
     * @param serializers SerializerProvider对象，提供序列化所需的上下文信息。
     * @param typeSer     TypeSerializer对象，用于处理类型信息的序列化。
     * @throws IOException 如果在序列化过程中发生I/O错误，则抛出该异常。
     */
    @Override
    public void serializeWithType(T exception, JsonGenerator gen, SerializerProvider serializers,
                                  TypeSerializer typeSer) throws IOException {
        // 生成类型ID，并指定JSON的起始标记为START_OBJECT
        WritableTypeId typeId = typeSer.typeId(exception, JsonToken.START_OBJECT);

        // 写入类型前缀，开始序列化过程
        typeSer.writeTypePrefix(gen, typeId);

        // 写入异常的具体内容
        writeExceptionContent(exception, gen);

        // 写入类型后缀，完成序列化过程
        typeSer.writeTypeSuffix(gen, typeId);
    }

    /**
     * 将异常的详细信息写入JSON生成器。
     * <p>
     * 该函数负责将异常的各个部分序列化为JSON格式，包括异常消息、异常类型、栈追踪信息以及cause异常（如果存在）。
     *
     * @param <T>       异常类型，必须是Throwable或其子类
     * @param exception 需要序列化的异常对象
     * @param gen       用于生成JSON的JsonGenerator对象
     * @throws IOException 如果在写入JSON过程中发生I/O错误
     */
    private void writeExceptionContent(T exception, JsonGenerator gen) throws IOException {
        // 序列化异常消息，如果异常消息为null，则写入空字符串
        gen.writeStringField(FIELD_MESSAGE, exception.getMessage() != null ?
                exception.getMessage() : "");

        // 序列化异常类型，写入异常类的全限定名
        gen.writeStringField(FIELD_TYPE, exception.getClass().getName());

        // 序列化异常栈追踪信息
        writeStackTrace(exception, gen);

        // 如果存在cause异常，则序列化cause异常
        writeCauseIfPresent(exception, gen);
    }

    /**
     * 将异常对象的堆栈跟踪信息写入JSON生成器。
     * <p>
     * 该方法将异常的堆栈跟踪信息转换为JSON格式，并写入到提供的JsonGenerator中。
     * 每个堆栈跟踪元素被表示为一个JSON对象，包含类名、方法名和行号。
     *
     * @param exception 包含堆栈跟踪信息的异常对象
     * @param gen       用于生成JSON的JsonGenerator对象
     * @throws IOException 如果在写入JSON过程中发生I/O错误
     */
    private void writeStackTrace(T exception, JsonGenerator gen) throws IOException {
        // 开始写入堆栈跟踪信息的JSON数组
        gen.writeArrayFieldStart(FIELD_STACK_TRACE);

        // 遍历异常对象的堆栈跟踪信息
        for (StackTraceElement element : exception.getStackTrace()) {
            // 开始写入单个堆栈跟踪元素的JSON对象
            gen.writeStartObject();
            gen.writeStringField(FIELD_CLASS_NAME, element.getClassName());  // 写入类名
            gen.writeStringField(FIELD_METHOD_NAME, element.getMethodName());  // 写入方法名
            gen.writeNumberField(FIELD_LINE_NUMBER, element.getLineNumber());  // 写入行号
            gen.writeEndObject();  // 结束当前堆栈跟踪元素的JSON对象
        }

        // 结束堆栈跟踪信息的JSON数组
        gen.writeEndArray();
    }

    /**
     * 如果传入的异常对象包含cause异常，则将cause异常的信息写入到JsonGenerator中。
     *
     * @param exception 需要处理的异常对象，该对象可能包含cause异常。
     * @param gen       用于生成JSON数据的JsonGenerator对象。
     * @throws IOException 如果在写入JSON数据时发生I/O错误，则抛出该异常。
     */
    private void writeCauseIfPresent(T exception, JsonGenerator gen) throws IOException {
        // 获取异常对象的cause异常
        Throwable cause = exception.getCause();

        // 如果cause异常存在，则将其信息写入JSON对象
        if (cause != null) {
            // 开始写入一个名为"cause"的JSON对象
            gen.writeObjectFieldStart(FIELD_CAUSE);

            // 写入cause异常的消息，如果消息为null则写入空字符串
            gen.writeStringField(FIELD_MESSAGE, cause.getMessage() != null ?
                    cause.getMessage() : "");

            // 写入cause异常的类型（类名）
            gen.writeStringField(FIELD_TYPE, cause.getClass().getName());

            // 结束当前JSON对象的写入
            gen.writeEndObject();
        }
    }

}

