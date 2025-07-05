package com.gls.athena.starter.word.formatter.impl;

import com.gls.athena.starter.word.formatter.ValueFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

/**
 * 默认值格式化器实现
 * 提供丰富的格式化选项
 *
 * @author athena
 */
@Slf4j
@Component
public class DefaultValueFormatter implements ValueFormatter {

    private static final String DEFAULT_NULL_VALUE = "";
    private static final String PERCENT_FORMAT = "%.";

    @Override
    public String format(Object value, String formatSpec) {
        if (value == null) {
            return DEFAULT_NULL_VALUE;
        }

        if (formatSpec == null) {
            return value.toString();
        }

        try {
            return switch (formatSpec.toLowerCase()) {
                case "date" -> formatDate(value, "yyyy-MM-dd");
                case "time" -> formatTime(value, "HH:mm:ss");
                case "datetime" -> formatDateTime(value, "yyyy-MM-dd HH:mm:ss");
                case "upper" -> value.toString().toUpperCase();
                case "lower" -> value.toString().toLowerCase();
                case "title" -> toTitleCase(value.toString());
                case "currency" -> formatCurrency(value);
                case "percent" -> formatPercent(value);
                case "trim" -> value.toString().trim();
                case "length" -> String.valueOf(value.toString().length());
                case "reverse" -> new StringBuilder(value.toString()).reverse().toString();
                case "json" -> formatAsJson(value);
                case "xml" -> formatAsXml(value);
                case "base64" -> encodeBase64(value.toString());
                case "url" -> encodeUrl(value.toString());
                case "html" -> escapeHtml(value.toString());
                default -> handleCustomFormat(value, formatSpec);
            };
        } catch (Exception e) {
            log.debug("Failed to format value with spec {}: {}", formatSpec, e.getMessage());
            return value.toString();
        }
    }

    private String handleCustomFormat(Object value, String formatSpec) {
        // 尝试作为日期格式模式
        if (value instanceof Date || value instanceof LocalDateTime) {
            return formatDateTime(value, formatSpec);
        } else if (value instanceof Number && formatSpec.startsWith(PERCENT_FORMAT)) {
            // 数字格式化
            return String.format(formatSpec, ((Number) value).doubleValue());
        }
        return value.toString();
    }

    private String formatDate(Object value, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        if (value instanceof Date) {
            return formatter.format(((Date) value).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        } else if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(pattern));
        }
        return value.toString();
    }

    private String formatTime(Object value, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        if (value instanceof Date) {
            return formatter.format(((Date) value).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime());
        } else if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(formatter);
        }
        return value.toString();
    }

    private String formatDateTime(Object value, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        if (value instanceof Date) {
            return formatter.format(((Date) value).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        } else if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(formatter);
        }
        return value.toString();
    }

    private String formatCurrency(Object value) {
        if (value instanceof Number) {
            return String.format("¥%.2f", ((Number) value).doubleValue());
        }
        return value.toString();
    }

    private String formatPercent(Object value) {
        if (value instanceof Number) {
            return String.format("%.1f%%", ((Number) value).doubleValue() * 100);
        }
        return value.toString();
    }

    private String toTitleCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : str.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }

    private String formatAsJson(Object value) {
        if (value instanceof String) {
            return "\"" + value.toString().replace("\"", "\\\"") + "\"";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else if (value == null) {
            return "null";
        }
        return "\"" + value.toString().replace("\"", "\\\"") + "\"";
    }

    private String formatAsXml(Object value) {
        return "<value>" + escapeXml(value.toString()) + "</value>";
    }

    private String encodeBase64(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    private String encodeUrl(String str) {
        try {
            return java.net.URLEncoder.encode(str, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return str;
        }
    }

    private String escapeHtml(String str) {
        return str.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }

    private String escapeXml(String str) {
        return str.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&apos;");
    }
}
