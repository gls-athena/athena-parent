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

    @Override
    public void serialize(T exception, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        writeExceptionContent(exception, gen);
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(T exception, JsonGenerator gen, SerializerProvider serializers,
                                  TypeSerializer typeSer) throws IOException {
        WritableTypeId typeId = typeSer.typeId(exception, JsonToken.START_OBJECT);
        typeSer.writeTypePrefix(gen, typeId);
        writeExceptionContent(exception, gen);
        typeSer.writeTypeSuffix(gen, typeId);
    }

    /**
     * 写入异常的详细内容
     */
    private void writeExceptionContent(T exception, JsonGenerator gen) throws IOException {
        // 序列化异常消息
        gen.writeStringField(FIELD_MESSAGE, exception.getMessage() != null ?
                exception.getMessage() : "");

        // 序列化异常类型
        gen.writeStringField(FIELD_TYPE, exception.getClass().getName());

        // 序列化异常栈追踪
        writeStackTrace(exception, gen);

        // 序列化cause异常
        writeCauseIfPresent(exception, gen);
    }

    /**
     * 写入异常堆栈信息
     */
    private void writeStackTrace(T exception, JsonGenerator gen) throws IOException {
        gen.writeArrayFieldStart(FIELD_STACK_TRACE);
        for (StackTraceElement element : exception.getStackTrace()) {
            gen.writeStartObject();
            gen.writeStringField(FIELD_CLASS_NAME, element.getClassName());
            gen.writeStringField(FIELD_METHOD_NAME, element.getMethodName());
            gen.writeNumberField(FIELD_LINE_NUMBER, element.getLineNumber());
            gen.writeEndObject();
        }
        gen.writeEndArray();
    }

    /**
     * 如果存在cause异常，则写入cause异常信息
     */
    private void writeCauseIfPresent(T exception, JsonGenerator gen) throws IOException {
        Throwable cause = exception.getCause();
        if (cause != null) {
            gen.writeObjectFieldStart(FIELD_CAUSE);
            gen.writeStringField(FIELD_MESSAGE, cause.getMessage() != null ?
                    cause.getMessage() : "");
            gen.writeStringField(FIELD_TYPE, cause.getClass().getName());
            gen.writeEndObject();
        }
    }
}

