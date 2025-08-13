package com.gls.athena.common.bean.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

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
public class PageRequest<T> {

    /**
     * 默认页码
     */
    public static final Integer DEFAULT_PAGE = 1;

    /**
     * 默认每页条数
     */
    public static final Integer DEFAULT_SIZE = 10;

    /**
     * 最大每页条数限制
     */
    public static final Integer MAX_SIZE = 1000;

    /**
     * 当前页码，从1开始
     * 默认值：1
     */
    @Schema(title = "当前页码", description = "分页查询的页码，从1开始", example = "1")
    private Integer page = DEFAULT_PAGE;

    /**
     * 每页显示记录数
     * 默认值：10
     */
    @Schema(title = "每页条数", description = "每页显示的记录数量", example = "10")
    private Integer size = DEFAULT_SIZE;

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

    /**
     * 创建默认分页请求
     *
     * @param <T> 请求参数类型
     * @return 默认配置的分页请求对象
     */
    public static <T> PageRequest<T> of() {
        return new PageRequest<>();
    }

    /**
     * 创建指定页码和大小的分页请求
     *
     * @param page 页码
     * @param size 每页条数
     * @param <T>  请求参数类型
     * @return 配置了页码和大小的分页请求对象
     */
    public static <T> PageRequest<T> of(Integer page, Integer size) {
        PageRequest<T> request = new PageRequest<>();
        request.setPage(page);
        request.setSize(size);
        return request.validate();
    }

    /**
     * 创建带排序的分页请求
     *
     * @param page  页码
     * @param size  每页条数
     * @param sort  排序字段
     * @param order 排序方式（asc/desc）
     * @param <T>   请求参数类型
     * @return 配置了页码、大小和排序信息的分页请求对象
     */
    public static <T> PageRequest<T> of(Integer page, Integer size, String sort, String order) {
        PageRequest<T> request = new PageRequest<>();
        request.setPage(page);
        request.setSize(size);
        request.setSort(sort);
        request.setOrder(order);
        return request.validate();
    }

    /**
     * 验证并修正分页参数
     * 包括页码、每页条数以及排序方式的有效性校验
     *
     * @return 经过验证和修正后的当前对象
     */
    public PageRequest<T> validate() {
        // 验证页码
        if (page == null || page < 1) {
            page = DEFAULT_PAGE;
        }

        // 验证每页条数
        if (size == null || size < 1) {
            size = DEFAULT_SIZE;
        } else if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }

        // 验证排序方式
        if (order != null && !order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")) {
            order = "desc"; // 默认降序
        }

        return this;
    }

    /**
     * 计算偏移量（用于SQL查询）
     *
     * @return 偏移量值，即 (page - 1) * size
     */
    public int getOffset() {
        return (page - 1) * size;
    }

    /**
     * 获取限制条数（用于SQL查询）
     *
     * @return 每页显示的记录数量
     */
    public int getLimit() {
        return size;
    }

    /**
     * 判断是否有排序
     *
     * @return 如果存在排序字段则返回 true，否则返回 false
     */
    public boolean hasSort() {
        return sort != null && !sort.trim().isEmpty();
    }

    /**
     * 判断是否升序
     *
     * @return 如果排序方式为升序则返回 true，否则返回 false
     */
    public boolean isAsc() {
        return "asc".equalsIgnoreCase(order);
    }

    /**
     * 判断是否降序
     *
     * @return 如果排序方式为降序则返回 true，否则返回 false
     */
    public boolean isDesc() {
        return "desc".equalsIgnoreCase(order);
    }
}
