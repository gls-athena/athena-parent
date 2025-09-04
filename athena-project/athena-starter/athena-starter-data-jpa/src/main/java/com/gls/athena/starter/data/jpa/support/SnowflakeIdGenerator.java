package com.gls.athena.starter.data.jpa.support;

import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * Snowflake ID生成器
 * <p>
 * 基于Snowflake算法的分布式唯一ID生成器，用于Hibernate实体的主键生成。
 * 支持通过workerId和datacenterId配置来确保分布式环境下的ID唯一性。
 *
 * @author george
 * @see IdentifierGenerator
 * @see SnowflakeId
 */
@RequiredArgsConstructor
public class SnowflakeIdGenerator implements IdentifierGenerator {

    private final SnowflakeId snowflakeId;

    /**
     * 生成唯一标识符
     * <p>
     * 根据配置的workerId和datacenterId生成分布式唯一ID：
     * <ul>
     *   <li>当workerId和datacenterId均为0时，使用默认Snowflake实例</li>
     *   <li>否则使用指定的workerId和datacenterId创建专用实例</li>
     * </ul>
     *
     * @param session Hibernate会话实现
     * @param object  实体对象
     * @return 生成的唯一ID
     */
    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        long workerId = snowflakeId.workerId();
        long datacenterId = snowflakeId.datacenterId();

        if (workerId == 0 && datacenterId == 0) {
            // 使用默认Snowflake实例
            return IdUtil.getSnowflakeNextId();
        }
        // 使用指定配置的Snowflake实例
        return IdUtil.getSnowflake(workerId, datacenterId).nextId();
    }

}
