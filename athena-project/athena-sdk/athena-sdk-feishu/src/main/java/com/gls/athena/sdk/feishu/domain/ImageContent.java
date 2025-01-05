package com.gls.athena.sdk.feishu.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 图片内容
 *
 * @author george
 */
@Data
public class ImageContent {
    /**
     * 图片key
     */
    @JsonProperty("image_key")
    private String imageKey;
}
