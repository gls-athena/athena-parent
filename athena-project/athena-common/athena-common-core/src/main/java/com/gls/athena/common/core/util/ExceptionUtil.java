package com.gls.athena.common.core.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 异常处理工具类
 * <p>
 * 提供统一的异常处理方法，支持函数式编程风格的异常处理
 *
 * @author george
 */
@Slf4j
@UtilityClass
public class ExceptionUtil {

    /**
     * 安全执行操作，捕获并记录异常
     *
     * @param operation 要执行的操作
     * @param errorMsg  错误消息
     */
    public void safeExecute(Runnable operation, String errorMsg) {
        try {
            operation.run();
        } catch (Exception e) {
            log.error("{}: {}", errorMsg, e.getMessage(), e);
        }
    }

    /**
     * 安全执行操作，捕获异常并返回默认值
     *
     * @param supplier     供应商函数
     * @param defaultValue 默认值
     * @param errorMsg     错误消息
     * @param <T>          返回类型
     * @return 执行结果或默认值
     */
    public <T> T safeExecute(Supplier<T> supplier, T defaultValue, String errorMsg) {
        try {
            return supplier.get();
        } catch (Exception e) {
            log.error("{}: {}", errorMsg, e.getMessage(), e);
            return defaultValue;
        }
    }

    /**
     * 安全执行操作，捕获异常并使用异常处理器
     *
     * @param supplier         供应商函数
     * @param exceptionHandler 异常处理器
     * @param <T>              返回类型
     * @return 执行结果
     */
    public <T> Optional<T> safeExecute(Supplier<T> supplier, Consumer<Exception> exceptionHandler) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (Exception e) {
            exceptionHandler.accept(e);
            return Optional.empty();
        }
    }

    /**
     * 重试执行操作
     *
     * @param operation  要执行的操作
     * @param maxRetries 最大重试次数
     * @param retryDelay 重试间隔（毫秒）
     * @param errorMsg   错误消息
     * @param <T>        返回类型
     * @return 执行结果
     * @throws RuntimeException 如果所有重试都失败
     */
    public <T> T retryExecute(Supplier<T> operation, int maxRetries, long retryDelay, String errorMsg) {
        Exception lastException = null;

        // 循环尝试执行操作，最多重试 maxRetries 次
        for (int i = 0; i <= maxRetries; i++) {
            try {
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                if (i < maxRetries) {
                    log.warn("{}，第{}次重试失败: {}", errorMsg, i + 1, e.getMessage());
                    if (retryDelay > 0) {
                        try {
                            Thread.sleep(retryDelay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("重试被中断", ie);
                        }
                    }
                } else {
                    log.error("{}，所有重试均失败", errorMsg, e);
                }
            }
        }

        throw new RuntimeException(errorMsg + "，重试" + maxRetries + "次后仍然失败", lastException);
    }

    /**
     * 转换异常类型
     *
     * @param operation       要执行的操作
     * @param exceptionMapper 异常转换器
     * @param <T>             返回类型
     * @param <E>             异常类型
     * @return 执行结果
     * @throws E 转换后的异常
     */
    public <T, E extends Exception> T transformException(Supplier<T> operation,
                                                         Function<Exception, E> exceptionMapper) throws E {
        try {
            return operation.get();
        } catch (Exception e) {
            throw exceptionMapper.apply(e);
        }
    }

    /**
     * 获取异常的根本原因
     *
     * @param throwable 异常对象
     * @return 根本原因
     */
    public Throwable getRootCause(Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        Throwable rootCause = throwable;
        // 遍历异常链，直到找到最深层的根本原因
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    /**
     * 获取异常消息链
     *
     * @param throwable 异常对象
     * @return 消息链字符串
     */
    public String getMessageChain(Throwable throwable) {
        if (throwable == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Throwable current = throwable;

        // 遍历异常链，构建消息链字符串
        while (current != null) {
            if (sb.length() > 0) {
                sb.append(" -> ");
            }
            sb.append(current.getClass().getSimpleName());
            if (current.getMessage() != null) {
                sb.append(": ").append(current.getMessage());
            }
            current = current.getCause();

            // 防止循环引用
            if (current == throwable) {
                break;
            }
        }

        return sb.toString();
    }
}
