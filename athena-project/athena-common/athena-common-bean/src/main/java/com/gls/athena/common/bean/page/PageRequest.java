package com.gls.athena.common.bean.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 分页查询请求对象
 * 用于封装分页查询的请求参数，包括页码、每页条数、排序信息等
 *
 * @param <T> 查询参数对象类型
 * @author george
 */
@Data
@Accessors(chain = true)
@Schema(title = "分页查询请求对象", description = "封装分页查询的请求参数")
public class PageRequest<T> implements Serializable {
    /**
     * 当前页码，从1开始
     * 默认值：1
     */
    @Schema(title = "当前页码", description = "分页查询的页码，从1开始", example = "1")
    private Integer page = 1;
    /**
     * 每页显示记录数
     * 默认值：10
     */
    @Schema(title = "每页条数", description = "每页显示的记录数量", example = "10")
    private Integer size = 10;
    /**
     * 排序字段名称
     * 例如：id、createTime等
     */
    @Schema(title = "排序字段", description = "指定排序的字段名称", example = "createTime")
    private String sort;
    /**
     * 排序方式
     * 可选值：asc（升序）、desc（降序）
     */
    @Schema(title = "排序方式", description = "排序方式：asc（升序）、desc（降序）", example = "desc")
    private String order;
    /**
     * 查询参数对象
     * 用于存放具体的查询条件
     */
    @Schema(title = "查询参数", description = "具体的查询条件参数对象")
    private T params;
}
