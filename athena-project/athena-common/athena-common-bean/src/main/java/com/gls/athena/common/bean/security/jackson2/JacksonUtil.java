package com.gls.athena.common.bean.security.jackson2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.common.bean.base.BaseVo;
import lombok.experimental.UtilityClass;

import java.util.Date;
import java.util.Optional;

/**
 * Jackson工具类
 *
 * @author george
 */
@UtilityClass
public class JacksonUtil {

    /**
     * 反序列化基础实体
     * <p>
     * 该函数从给定的JsonNode中提取数据，并将其设置到BaseVo对象的相应属性中。
     * 使用ObjectMapper进行JSON解析，支持提取多种类型的数据，包括Long、Integer、Boolean、String和Date。
     *
     * @param mapper ObjectMapper对象，用于JSON解析和类型转换
     * @param node   JsonNode对象，包含要解析的JSON数据
     * @param baseVo BaseVo对象，将从JsonNode中提取的数据设置到该对象的属性中
     */
    public void deserializeBaseVo(ObjectMapper mapper, JsonNode node, BaseVo baseVo) {
        // 从JsonNode中提取主键ID并设置到BaseVo对象中
        Optional.ofNullable(node.get("id")).map(JsonNode::asLong).ifPresent(baseVo::setId);

        // 从JsonNode中提取租户ID并设置到BaseVo对象中
        Optional.ofNullable(node.get("tenantId")).map(JsonNode::asLong).ifPresent(baseVo::setTenantId);

        // 从JsonNode中提取版本号并设置到BaseVo对象中
        Optional.ofNullable(node.get("version")).map(JsonNode::asInt).ifPresent(baseVo::setVersion);

        // 从JsonNode中提取删除标记并设置到BaseVo对象中
        Optional.ofNullable(node.get("deleted")).map(JsonNode::asBoolean).ifPresent(baseVo::setDeleted);

        // 从JsonNode中提取创建人ID并设置到BaseVo对象中
        Optional.ofNullable(node.get("createUserId")).map(JsonNode::asLong).ifPresent(baseVo::setCreateUserId);

        // 从JsonNode中提取创建人姓名并设置到BaseVo对象中
        Optional.ofNullable(node.get("createUserName")).map(JsonNode::asText).ifPresent(baseVo::setCreateUserName);

        // 从JsonNode中提取创建时间并设置到BaseVo对象中，使用ObjectMapper进行日期类型转换
        Optional.ofNullable(node.get("createTime")).map(date -> mapper.convertValue(date, Date.class))
                .ifPresent(baseVo::setCreateTime);

        // 从JsonNode中提取更新人ID并设置到BaseVo对象中
        Optional.ofNullable(node.get("updateUserId")).map(JsonNode::asLong).ifPresent(baseVo::setUpdateUserId);

        // 从JsonNode中提取更新人姓名并设置到BaseVo对象中
        Optional.ofNullable(node.get("updateUserName")).map(JsonNode::asText).ifPresent(baseVo::setUpdateUserName);

        // 从JsonNode中提取更新时间并设置到BaseVo对象中，使用ObjectMapper进行日期类型转换
        Optional.ofNullable(node.get("updateTime")).map(date -> mapper.convertValue(date, Date.class))
                .ifPresent(baseVo::setUpdateTime);
    }

}
