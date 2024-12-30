package com.gls.athena.sdk.feishu.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 图片内容
 *
 * @author george
 */
@Data
public class ImageContent implements Serializable {
    /**
     * 图片key
     */
    @JsonProperty("image_key")
    private String imageKey;
}
