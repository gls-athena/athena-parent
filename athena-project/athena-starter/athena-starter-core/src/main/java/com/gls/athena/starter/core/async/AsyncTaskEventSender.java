package com.gls.athena.starter.core.async;

import cn.hutool.extra.spring.SpringUtil;
import com.gls.athena.starter.core.support.AspectUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

/**
 * 异步任务事件发送者
 *
 * @author george
 */
@Component
public class AsyncTaskEventSender {

    /**
     * 发送异步任务开始事件。
     * 该函数用于创建一个异步任务的数据传输对象（DTO），并通过Spring事件机制发布该事件。
     *
     * @param taskId    异步任务的唯一标识符，用于标识当前任务。
     * @param point     ProceedingJoinPoint对象，表示当前正在执行的方法连接点，通常用于获取方法执行的上下文信息。
     * @param asyncTask 异步任务对象，包含任务的具体信息和执行逻辑。
     */
    public void sendAsyncTaskStartEvent(String taskId, ProceedingJoinPoint point, AsyncTask asyncTask) {
        // 创建异步任务的DTO对象，包含任务的相关信息
        AsyncTaskDto asyncTaskDto = createAsyncTaskDto(taskId, point, asyncTask);

        // 通过Spring事件机制发布异步任务开始事件
        SpringUtil.publishEvent(asyncTaskDto);
    }

    /**
     * 创建异步任务数据传输对象（AsyncTaskDto）。
     * <p>
     * 该函数根据传入的任务ID、切点对象和异步任务注解，构建并返回一个异步任务数据传输对象。
     * 该对象包含了任务的基本信息、参数、类型和状态等。
     *
     * @param taskId    异步任务的唯一标识符
     * @param point     切点对象，用于获取方法执行的上下文信息
     * @param asyncTask 异步任务注解，包含任务的代码、名称和描述等信息
     * @return AsyncTaskDto 返回构建好的异步任务数据传输对象
     */
    private AsyncTaskDto createAsyncTaskDto(String taskId, ProceedingJoinPoint point, AsyncTask asyncTask) {
        // 创建异步任务对象并设置基本信息
        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setTaskId(taskId);
        asyncTaskDto.setCode(asyncTask.code());
        asyncTaskDto.setName(asyncTask.name());
        asyncTaskDto.setDescription(asyncTask.description());

        // 从切点对象中获取方法参数并设置到异步任务对象中
        asyncTaskDto.setParams(AspectUtil.getParams(point));

        // 设置异步任务的类型和初始状态
        asyncTaskDto.setType(1);
        asyncTaskDto.setStatus(0);

        return asyncTaskDto;
    }

    /**
     * 发送异步任务错误事件。
     * 该函数用于在异步任务执行过程中发生错误时，创建并发布一个异步任务错误事件。
     *
     * @param taskId    异步任务的唯一标识符，用于标识具体的任务。
     * @param point     ProceedingJoinPoint对象，表示当前执行的连接点，通常用于获取方法执行的上下文信息。
     * @param asyncTask 异步任务对象，包含任务的具体信息和状态。
     * @param throwable 抛出的异常对象，包含错误的具体信息。
     */
    public void sendAsyncTaskErrorEvent(String taskId, ProceedingJoinPoint point, AsyncTask asyncTask, Throwable throwable) {
        // 创建异步任务DTO对象，并设置任务的基本信息
        AsyncTaskDto asyncTaskDto = createAsyncTaskDto(taskId, point, asyncTask);

        // 设置任务类型为错误类型，状态为失败，并记录错误信息
        asyncTaskDto.setType(2);
        asyncTaskDto.setStatus(1);
        asyncTaskDto.setError(throwable.getMessage());

        // 发布异步任务错误事件
        SpringUtil.publishEvent(asyncTaskDto);
    }

    /**
     * 发送异步任务成功事件。
     * 该函数用于在异步任务成功执行后，创建一个异步任务数据传输对象（AsyncTaskDto），
     * 并设置其类型、状态和结果，最后通过Spring事件机制发布该事件。
     *
     * @param taskId    异步任务的唯一标识符，用于标识具体的异步任务。
     * @param point     ProceedingJoinPoint对象，用于获取方法执行的上下文信息。
     * @param asyncTask 异步任务对象，包含任务的相关信息。
     * @param res       异步任务的执行结果，将作为事件的一部分进行传递。
     */
    public void sendAsyncTaskSuccessEvent(String taskId, ProceedingJoinPoint point, AsyncTask asyncTask, Object res) {
        // 创建异步任务数据传输对象，并设置任务的基本信息
        AsyncTaskDto asyncTaskDto = createAsyncTaskDto(taskId, point, asyncTask);

        // 设置任务类型为成功类型，状态为完成，并记录执行结果
        asyncTaskDto.setType(2);
        asyncTaskDto.setStatus(2);
        asyncTaskDto.setResult(res);

        // 发布异步任务成功事件
        SpringUtil.publishEvent(asyncTaskDto);
    }

}
