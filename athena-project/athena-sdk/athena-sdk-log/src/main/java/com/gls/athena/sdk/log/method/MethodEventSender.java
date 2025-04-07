package com.gls.athena.sdk.log.method;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.sdk.log.domain.MethodDto;
import com.gls.athena.sdk.log.domain.MethodLogDto;
import com.gls.athena.sdk.log.domain.MethodLogType;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 方法事件发送器
 *
 * @author george
 */
@Component
public class MethodEventSender {
    /**
     * 发送方法事件，将方法日志信息封装为MethodDto对象并发布事件。
     * 该函数通过接收应用程序名称、类名称、方法名称和方法日志对象，创建一个MethodDto对象，
     * 并将其与相关属性一起发布为事件。
     *
     * @param applicationName 应用程序名称，用于标识方法所属的应用
     * @param className       类名称，用于标识方法所属的类
     * @param methodName      方法名称，用于标识具体的方法
     * @param methodLog       方法日志对象，包含方法的代码、名称和描述等信息
     */
    public void sendMethodEvent(String applicationName, String className, String methodName, MethodLog methodLog) {
        // 创建MethodDto对象并设置相关属性
        MethodDto methodDto = new MethodDto();
        publishMethodDto(methodLog, applicationName, className, methodName, methodDto);
    }

    /**
     * 发送成功事件，记录方法调用的日志信息。
     *
     * @param traceId         跟踪ID，用于唯一标识一次请求或操作
     * @param args            方法调用的参数，以键值对的形式存储
     * @param startTime       方法调用的开始时间
     * @param result          方法调用的返回结果
     * @param methodLog       方法日志对象，用于记录方法调用的详细信息
     * @param applicationName 应用程序名称，标识调用方法的来源应用
     * @param className       类名，标识调用方法所在的类
     * @param methodName      方法名，标识被调用的方法
     */
    public void sendSuccessEvent(String traceId, Map<String, Object> args, Date startTime, Object result, MethodLog methodLog, String applicationName, String className, String methodName) {
        // 创建方法日志DTO对象，并设置相关属性
        MethodLogDto methodLogDto = new MethodLogDto();
        methodLogDto.setArgs(args);
        methodLogDto.setResult(result);
        methodLogDto.setStartTime(startTime);
        methodLogDto.setEndTime(new Date());
        methodLogDto.setType(MethodLogType.NORMAL);
        methodLogDto.setTraceId(traceId);

        // 发布方法日志DTO对象，记录方法调用的详细信息
        publishMethodDto(methodLog, applicationName, className, methodName, methodLogDto);
    }

    /**
     * 发送错误事件，将错误信息封装为MethodLogDto对象并发布。
     *
     * @param traceId         用于标识请求的唯一ID，通常用于跟踪和日志记录。
     * @param args            方法调用时的参数列表，以键值对形式存储。
     * @param startTime       方法调用的开始时间。
     * @param methodLog       方法日志对象，用于记录方法调用的详细信息。
     * @param applicationName 应用程序名称，标识当前应用。
     * @param className       类名，标识发生错误的类。
     * @param methodName      方法名，标识发生错误的方法。
     * @param throwable       抛出的异常对象，包含错误信息。
     */
    public void sendErrorEvent(String traceId, Map<String, Object> args, Date startTime, MethodLog methodLog, String applicationName, String className, String methodName, Throwable throwable) {
        // 创建MethodLogDto对象并设置相关属性
        MethodLogDto methodLogDto = new MethodLogDto();
        methodLogDto.setArgs(args);
        methodLogDto.setStartTime(startTime);
        methodLogDto.setEndTime(new Date());
        methodLogDto.setType(MethodLogType.ERROR);
        methodLogDto.setTraceId(traceId);
        methodLogDto.setErrorMessage(throwable.getMessage());
        methodLogDto.setThrowable(throwable.getMessage());

        // 发布方法日志DTO对象
        publishMethodDto(methodLog, applicationName, className, methodName, methodLogDto);
    }

    /**
     * 将方法日志信息发布为方法数据传输对象（MethodDto），并触发事件。
     *
     * @param methodLog       方法日志对象，包含方法的代码、名称和描述等信息。
     * @param applicationName 应用程序名称，用于标识方法所属的应用。
     * @param className       类名称，用于标识方法所属的类。
     * @param methodName      方法名称，用于标识具体的方法。
     * @param methodDto       方法数据传输对象，用于封装方法的相关信息并发布事件。
     */
    private void publishMethodDto(MethodLog methodLog, String applicationName, String className, String methodName, MethodDto methodDto) {
        // 将方法日志中的信息设置到方法数据传输对象中
        methodDto.setCode(methodLog.code());
        methodDto.setName(methodLog.name());
        methodDto.setDescription(methodLog.description());
        methodDto.setApplicationName(applicationName);
        methodDto.setClassName(className);
        methodDto.setMethodName(methodName);

        // 发布方法数据传输对象事件
        SpringUtil.publishEvent(methodDto);
    }
}
