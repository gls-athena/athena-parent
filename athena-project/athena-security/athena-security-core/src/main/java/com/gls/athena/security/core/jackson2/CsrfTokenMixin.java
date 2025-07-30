package com.gls.athena.security.core.jackson2;

import com.fasterxml.jackson.annotation.*;

/**
 * CSRF Token的Jackson混入类
 * 用于序列化和反序列化CSRF Token对象
 *
 * @author george
 * @since 1.0.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsrfTokenMixin {

    /**
     * CSRF Token混入类的构造函数
     *
     * @param headerName    HTTP请求头中的CSRF token名称
     * @param parameterName 请求参数中的CSRF token名称
     * @param token         CSRF token的值
     */
    @JsonCreator
    public CsrfTokenMixin(@JsonProperty("headerName") String headerName,
                          @JsonProperty("parameterName") String parameterName,
                          @JsonProperty("token") String token) {
    }
}
