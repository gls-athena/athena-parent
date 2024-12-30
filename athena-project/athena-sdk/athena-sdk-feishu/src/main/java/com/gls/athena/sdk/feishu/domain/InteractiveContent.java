package com.gls.athena.sdk.feishu.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 卡片交互内容
 *
 * @author george
 */
@Data
public class InteractiveContent implements Serializable {
    /**
     * 类型
     */
    private String type = "template";
    /**
     * 模板数据
     */
    private Template data;

    /**
     * 模板
     */
    @Data
    public static class Template implements Serializable {
        /**
         * 模板ID
         */
        @JsonProperty("template_id")
        private String id;
        /**
         * 模板版本
         */
        @JsonProperty("template_version_name")
        private String version;
        /**
         * 模板变量
         */
        @JsonProperty("template_variable")
        private Map<String, Object> variable;
    }
}
