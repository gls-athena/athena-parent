package com.gls.athena.sdk.amap.support;

import com.gls.athena.sdk.amap.config.AmapProperties;
import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 高德地图API重试器
 * <p>
 * 该重试器用于处理高德地图API的临时性错误，如网络超时、服务器繁忙等。
 * 采用指数退避策略，避免对服务器造成过大压力。
 * 支持通过配置文件自定义重试参数。
 *
 * @author george
 */
@Slf4j
public class AmapRetryer implements Retryer {

    private final int maxAttempts;
    private final long period;
    private final long maxPeriod;
    private int attempt;
    private long sleptForMillis;

    /**
     * 默认构造函数，使用默认的重试参数初始化重试器
     * <p>
     * 默认最大尝试次数为3次，
     * 初始重试间隔为1秒，
     * 最大重试间隔为5秒。
     */
    public AmapRetryer() {
        this(3, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(5));
    }

    /**
     * 使用指定的重试配置初始化重试器
     *
     * @param retryConfig 重试配置对象，包含最大尝试次数、初始重试间隔和最大重试间隔
     */
    public AmapRetryer(AmapProperties.RetryConfig retryConfig) {
        this(retryConfig.getMaxAttempts(), retryConfig.getPeriod(), retryConfig.getMaxPeriod());
    }

    /**
     * 使用指定参数初始化重试器
     *
     * @param maxAttempts 最大尝试次数（包括首次请求）
     * @param period      初始重试间隔（毫秒）
     * @param maxPeriod   最大重试间隔（毫秒）
     */
    public AmapRetryer(int maxAttempts, long period, long maxPeriod) {
        this.maxAttempts = maxAttempts;
        this.period = period;
        this.maxPeriod = maxPeriod;
        this.attempt = 1;
    }

    /**
     * 根据异常决定是否继续重试或抛出异常
     * <p>
     * 如果已达到最大尝试次数，则记录错误日志并抛出原始异常；
     * 否则根据异常中指定的重试时间或计算出的指数退避时间进行等待后继续重试。
     *
     * @param e 可重试异常，包含重试相关信息
     * @throws RetryableException 当达到最大尝试次数或线程被中断时抛出
     */
    @Override
    public void continueOrPropagate(RetryableException e) {
        // 检查是否超过最大尝试次数
        if (attempt++ >= maxAttempts) {
            log.error("Amap API retry failed after {} attempts", maxAttempts - 1);
            throw e;
        }

        long interval;
        // 如果异常中指定了重试时间，则使用该时间作为间隔
        if (e.retryAfter() != null) {
            interval = e.retryAfter() - System.currentTimeMillis();
            if (interval > maxPeriod) {
                interval = maxPeriod;
            }
            if (interval < 0) {
                return;
            }
        } else {
            // 否则使用指数退避算法计算下次重试间隔
            interval = nextMaxInterval();
        }

        log.warn("Amap API request failed, retrying in {}ms (attempt {}/{}): {}",
                interval, attempt - 1, maxAttempts, e.getMessage());

        try {
            Thread.sleep(interval);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw e;
        }
        sleptForMillis += interval;
    }

    /**
     * 计算下次重试的间隔时间（指数退避）
     * <p>
     * 使用公式：interval = period * (1.5 ^ (attempt - 1))，并确保不超过最大重试间隔
     *
     * @return 下次重试的间隔时间（毫秒）
     */
    long nextMaxInterval() {
        long interval = (long) (period * Math.pow(1.5, attempt - 1));
        return Math.min(interval, maxPeriod);
    }

    /**
     * 创建一个新的重试器实例，用于新的请求重试流程
     *
     * @return 新的重试器实例
     */
    @Override
    public Retryer clone() {
        return new AmapRetryer(maxAttempts, period, maxPeriod);
    }
}
