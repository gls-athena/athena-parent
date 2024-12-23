package com.gls.athena.common.bean.security.jackson2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gls.athena.common.bean.base.BaseVo;
import lombok.experimental.UtilityClass;

import java.util.Date;

/**
 * Jackson工具类
 *
 * @author george
 */
@UtilityClass
public class JacksonUtil {

    /**
     * 反序列化基础实体
     *
     * @param mapper ObjectMapper用于JSON解析
     * @param node   JsonNode包含要解析的数据
     * @param baseVo 基础实体对象，将从JsonNode中获取数据并设置到该对象中
     */
    public void deserializeBaseVo(ObjectMapper mapper, JsonNode node, BaseVo baseVo) {
        // 从JsonNode中提取字段值并分配给BaseVo对象相应的属性

        // 提取主键ID
        Long id = node.get("id").asLong();
        // 提取租户ID
        Long tenantId = node.get("tenantId").asLong();
        // 提取版本号
        Integer version = node.get("version").asInt();
        // 提取删除标记
        Boolean deleted = node.get("deleted").asBoolean();
        // 提取创建人ID
        Long createUserId = node.get("createUserId").asLong();
        // 提取创建人姓名
        String createUserName = node.get("createUserName").asText();
        // 提取创建时间
        Date createTime = mapper.convertValue(node.get("createTime"), Date.class);
        // 提取更新人ID
        Long updateUserId = node.get("updateUserId").asLong();
        // 提取更新人姓名
        String updateUserName = node.get("updateUserName").asText();
        // 提取更新时间
        Date updateTime = mapper.convertValue(node.get("updateTime"), Date.class);

        // 将提取的值设置到BaseVo对象的各个字段
        baseVo.setId(id);
        baseVo.setTenantId(tenantId);
        baseVo.setVersion(version);
        baseVo.setDeleted(deleted);
        baseVo.setCreateUserId(createUserId);
        baseVo.setCreateUserName(createUserName);
        baseVo.setCreateTime(createTime);
        baseVo.setUpdateUserId(updateUserId);
        baseVo.setUpdateUserName(updateUserName);
        baseVo.setUpdateTime(updateTime);
    }
}
