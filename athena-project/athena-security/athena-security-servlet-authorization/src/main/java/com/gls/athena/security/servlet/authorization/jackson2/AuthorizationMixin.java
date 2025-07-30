package com.gls.athena.security.servlet.authorization.jackson2;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * oauth2授权混合
 *
 * @author george
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonDeserialize(using = AuthorizationDeserializer.class)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationMixin {
}
