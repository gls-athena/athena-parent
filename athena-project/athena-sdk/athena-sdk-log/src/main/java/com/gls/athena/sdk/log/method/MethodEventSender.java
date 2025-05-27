package com.gls.athena.sdk.log.method;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.sdk.log.domain.MethodDto;
import com.gls.athena.sdk.log.domain.MethodLogDto;
import com.gls.athena.sdk.log.domain.MethodLogType;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 方法事件发送器，提供方法执行相关的事件通知功能
 *
 * @author george
 */
@Component
public class MethodEventSender {

    /**
     * 发送基础方法事件，封装方法元数据并触发事件通知
     *
     * @param applicationName 当前应用标识名称
     * @param className       方法所属类的全限定名
     * @param methodName      目标方法名称
     * @param methodLog       方法元数据对象，包含业务定义的方法编码、名称和描述
     */
    public void sendMethodEvent(String applicationName, String className, String methodName, MethodLog methodLog) {
        // 构造基础方法数据对象并触发事件发布
        MethodDto methodDto = new MethodDto();
        publishMethodDto(methodLog, applicationName, className, methodName, methodDto);
    }

    /**
     * 发送方法成功执行事件，记录完整的方法调用轨迹和结果
     *
     * @param traceId         分布式追踪标识符
     * @param args            方法调用参数键值对集合
     * @param startTime       方法调用起始时间戳
     * @param result          方法执行返回结果对象
     * @param methodLog       方法元数据对象
     * @param applicationName 当前应用标识名称
     * @param className       方法所属类的全限定名
     * @param methodName      目标方法名称
     */
    public void sendSuccessEvent(String traceId, Map<String, Object> args, Date startTime, Object result, MethodLog methodLog, String applicationName, String className, String methodName) {
        // 构造完整方法日志对象（包含参数、结果和时间信息）
        MethodLogDto methodLogDto = new MethodLogDto();
        methodLogDto.setArgs(args);
        methodLogDto.setResult(result);
        methodLogDto.setStartTime(startTime);
        methodLogDto.setEndTime(new Date());
        methodLogDto.setType(MethodLogType.NORMAL);
        methodLogDto.setTraceId(traceId);

        // 发布包含完整上下文的方法执行日志
        publishMethodDto(methodLog, applicationName, className, methodName, methodLogDto);
    }

    /**
     * 发送方法执行异常事件，记录错误上下文信息
     *
     * @param traceId         分布式追踪标识符
     * @param args            方法调用参数键值对集合
     * @param startTime       方法调用起始时间戳
     * @param methodLog       方法元数据对象
     * @param applicationName 当前应用标识名称
     * @param className       方法所属类的全限定名
     * @param methodName      目标方法名称
     * @param throwable       方法执行抛出的异常对象
     */
    public void sendErrorEvent(String traceId, Map<String, Object> args, Date startTime, MethodLog methodLog, String applicationName, String className, String methodName, Throwable throwable) {
        // 构造异常日志对象（包含错误信息和异常堆栈）
        MethodLogDto methodLogDto = new MethodLogDto();
        methodLogDto.setArgs(args);
        methodLogDto.setStartTime(startTime);
        methodLogDto.setEndTime(new Date());
        methodLogDto.setType(MethodLogType.ERROR);
        methodLogDto.setTraceId(traceId);
        methodLogDto.setErrorMessage(throwable.getMessage());
        methodLogDto.setThrowable(throwable.getMessage());

        // 发布包含异常信息的方法执行日志
        publishMethodDto(methodLog, applicationName, className, methodName, methodLogDto);
    }

    /**
     * 统一处理方法数据传输对象的属性填充和事件发布
     *
     * @param methodLog       方法元数据对象，提供基础方法信息
     * @param applicationName 当前应用标识名称
     * @param className       方法所属类的全限定名
     * @param methodName      目标方法名称
     * @param methodDto       方法数据传输对象（基础DTO或扩展日志DTO）
     */
    private void publishMethodDto(MethodLog methodLog, String applicationName, String className, String methodName, MethodDto methodDto) {
        // 填充方法基础元数据
        methodDto.setCode(methodLog.code());
        methodDto.setName(methodLog.name());
        methodDto.setDescription(methodLog.description());

        // 设置上下文标识信息
        methodDto.setApplicationName(applicationName);
        methodDto.setClassName(className);
        methodDto.setMethodName(methodName);

        // 触发Spring事件发布机制
        SpringUtil.publishEvent(methodDto);
    }
}
