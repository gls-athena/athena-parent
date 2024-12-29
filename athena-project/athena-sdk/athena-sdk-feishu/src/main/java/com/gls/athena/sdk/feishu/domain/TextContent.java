package com.gls.athena.sdk.feishu.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 文本内容
 *
 * @author george
 */
@Data
public class TextContent implements Serializable {
    /**
     * 文本
     */
    private String text;
}
