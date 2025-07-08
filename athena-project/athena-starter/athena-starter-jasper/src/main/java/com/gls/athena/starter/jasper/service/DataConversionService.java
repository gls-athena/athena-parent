package com.gls.athena.starter.jasper.service;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 数据转换服务 - 专门负责数据格式转换
 *
 * @author george
 */
@Slf4j
@Service
public class DataConversionService {

    /**
     * 将对象转换为Map格式，用于Jasper报告
     *
     * @param source 源对象
     * @return 转换后的Map数据
     */
    public Map<String, Object> convertToReportData(Object source) {
        if (source == null) {
            log.warn("转换的源对象为null，返回空Map");
            return Map.of();
        }

        try {
            Map<String, Object> result = BeanUtil.beanToMap(source);
            log.debug("成功转换对象到Map，字段数量: {}", result.size());
            return result;
        } catch (Exception e) {
            log.error("转换对象到Map失败", e);
            throw new RuntimeException("数据转换失败", e);
        }
    }

    /**
     * 验证报告数据的完整性
     *
     * @param data 报告数据
     * @return 验证是否通过
     */
    public boolean validateReportData(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            log.warn("报告数据为空或null");
            return false;
        }

        // 可以根据需要添加更多验证逻辑
        log.debug("报告数据验证通过，包含 {} 个字段", data.size());
        return true;
    }
}
