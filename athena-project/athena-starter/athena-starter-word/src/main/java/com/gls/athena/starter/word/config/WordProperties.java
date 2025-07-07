package com.gls.athena.starter.word.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Word配置属性
 *
 * @author athena
 */
@Data
@ConfigurationProperties(prefix = "athena.word")
public class WordProperties {

}
