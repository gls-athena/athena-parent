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
     *
     * @param session Hibernate会话
     * @param object  实体对象
     * @return 生成的唯一ID
     */
    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        long workerId = snowflakeId.workerId();
        long datacenterId = snowflakeId.datacenterId();

        return (workerId == 0 && datacenterId == 0)
                ? IdUtil.getSnowflakeNextId()
                : IdUtil.getSnowflake(workerId, datacenterId).nextId();
    }
}
