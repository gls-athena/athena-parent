package com.gls.athena.starter.data.jpa.support;

import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * Snowflake ID生成器
 * 用于生成分布式环境下的唯一ID
 *
 * @author george
 */
@RequiredArgsConstructor
public class SnowflakeIdGenerator implements IdentifierGenerator {

    private final SnowflakeId snowflakeId;

    /**
     * 生成分布式唯一ID
     * <p>
     * 该方法根据Snowflake算法的配置生成一个全局唯一的ID。如果workerId和datacenterId都为0，
     * 则使用默认的Snowflake实例生成ID；否则，使用指定的workerId和datacenterId创建Snowflake实例并生成ID。
     *
     * @param session Hibernate会话对象，用于与数据库进行交互
     * @param object  需要生成唯一ID的实体对象
     * @return 生成的唯一ID，类型为Object
     */
    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        // 获取当前Snowflake实例的workerId和datacenterId
        long workerId = snowflakeId.workerId();
        long datacenterId = snowflakeId.datacenterId();

        // 根据workerId和datacenterId的值决定使用哪种方式生成唯一ID
        // 使用默认的Snowflake实例生成ID
        if (workerId == 0 && datacenterId == 0) {
            return IdUtil.getSnowflakeNextId();
        }
        // 使用指定的workerId和datacenterId生成ID
        return IdUtil.getSnowflake(workerId, datacenterId).nextId();
    }

}
