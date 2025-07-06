package com.gls.athena.starter.excel.factory;

import com.gls.athena.starter.excel.listener.DefaultReadListener;
import com.gls.athena.starter.excel.listener.IReadListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 读取监听器工厂类
 * <p>
 * 使用工厂模式创建和管理ReadListener实例
 * 支持单例缓存和自定义监听器
 *
 * @author george
 */
@Slf4j
public class ReadListenerFactory {

    /**
     * 监听器实例缓存
     */
    private static final Map<Class<? extends IReadListener>, IReadListener> LISTENER_CACHE = new ConcurrentHashMap<>();

    /**
     * 默认监听器类型
     */
    private static final Class<? extends IReadListener> DEFAULT_LISTENER_CLASS = DefaultReadListener.class;

    /**
     * 创建监听器实例
     *
     * @param listenerClass 监听器类型
     * @return 监听器实例
     */
    @SuppressWarnings("unchecked")
    public static <T> IReadListener<T> createListener(Class<? extends IReadListener> listenerClass) {
        if (listenerClass == null) {
            listenerClass = DEFAULT_LISTENER_CLASS;
        }

        return (IReadListener<T>) LISTENER_CACHE.computeIfAbsent(listenerClass, clazz -> {
            try {
                IReadListener listener = BeanUtils.instantiateClass(clazz);
                log.debug("创建监听器实例: {}", clazz.getSimpleName());
                return listener;
            } catch (Exception e) {
                log.error("创建监听器失败: {}", clazz.getName(), e);
                // 回退到默认监听器
                return BeanUtils.instantiateClass(DEFAULT_LISTENER_CLASS);
            }
        });
    }

    /**
     * 清空缓存
     */
    public static void clearCache() {
        LISTENER_CACHE.clear();
        log.debug("监听器缓存已清空");
    }

    /**
     * 获取缓存大小
     */
    public static int getCacheSize() {
        return LISTENER_CACHE.size();
    }
}
