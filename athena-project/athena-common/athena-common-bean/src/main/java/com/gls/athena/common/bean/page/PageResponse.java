package com.gls.athena.common.bean.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页查询响应对象
 * 用于封装分页查询的结果数据，包含分页信息和实际数据列表
 *
 * @param <T> 数据实体类型
 * @author george
 */
@Data
@Accessors(chain = true)
@Schema(title = "分页查询响应对象", description = "封装分页查询的结果数据，包含分页信息和实际数据列表")
public class PageResponse<T> {

    /**
     * 当前页码，从1开始
     */
    @Schema(title = "当前页码", description = "当前页码，从1开始")
    private Integer page;

    /**
     * 每页显示记录数
     */
    @Schema(title = "每页记录数", description = "每页显示的记录数量")
    private Integer size;

    /**
     * 总记录数
     */
    @Schema(title = "总记录数", description = "符合条件的记录总数")
    private Long total;

    /**
     * 总页数
     */
    @Schema(title = "总页数", description = "根据总记录数和每页记录数计算得出的总页数")
    private Integer pages;

    /**
     * 当前页数据列表
     */
    @Schema(title = "数据列表", description = "当前页的数据记录列表")
    private List<T> data;

    /**
     * 是否有上一页
     */
    @Schema(title = "是否有上一页", description = "判断是否存在上一页")
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    @Schema(title = "是否有下一页", description = "判断是否存在下一页")
    private Boolean hasNext;

    /**
     * 默认构造函数
     * 初始化空的数据列表
     */
    public PageResponse() {
        this.data = Collections.emptyList();
    }

    /**
     * 构造函数
     *
     * @param page  当前页码（从1开始）
     * @param size  每页大小
     * @param total 总记录数
     * @param data  当前页数据列表
     */
    public PageResponse(Integer page, Integer size, Long total, List<T> data) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.data = data == null ? Collections.emptyList() : data;
        this.calculateDerivedFields();
    }

    /**
     * 创建一个空的分页响应对象
     *
     * @param page 当前页码
     * @param size 每页大小
     * @param <T>  数据类型
     * @return 空的分页响应对象
     */
    public static <T> PageResponse<T> empty(Integer page, Integer size) {
        return new PageResponse<>(page, size, 0L, Collections.emptyList());
    }

    /**
     * 根据 PageRequest 构建分页响应对象
     *
     * @param request 请求参数对象
     * @param total   总记录数
     * @param data    当前页数据列表
     * @param <T>     数据类型
     * @return 分页响应对象
     */
    public static <T> PageResponse<T> of(PageRequest<?> request, Long total, List<T> data) {
        return new PageResponse<>(request.getPage(), request.getSize(), total, data);
    }

    /**
     * 对当前分页数据进行类型转换
     *
     * @param converter 转换函数，将 T 类型转换为 R 类型
     * @param <R>       转换后的数据类型
     * @return 转换后的新分页响应对象
     */
    public <R> PageResponse<R> map(Function<T, R> converter) {
        List<R> convertedData = this.data.stream()
                .map(converter)
                .collect(Collectors.toList());

        PageResponse<R> result = new PageResponse<>();
        result.page = this.page;
        result.size = this.size;
        result.total = this.total;
        result.data = convertedData;
        result.calculateDerivedFields();
        return result;
    }

    /**
     * 计算派生字段：总页数、是否有上一页、是否有下一页
     * 该方法在设置 total 或 data 后自动调用以更新相关字段
     */
    private void calculateDerivedFields() {
        if (size != null && size > 0 && total != null) {
            this.pages = (int) Math.ceil((double) total / size);
            this.hasPrevious = page != null && page > 1;
            this.hasNext = page != null && pages != null && page < pages;
        } else {
            this.pages = 0;
            this.hasPrevious = false;
            this.hasNext = false;
        }
    }

    /**
     * 判断当前分页数据是否为空
     *
     * @return 如果数据列表为空则返回 true，否则返回 false
     */
    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }

    /**
     * 获取当前页数据条数
     *
     * @return 数据条数
     */
    public int getDataSize() {
        return data == null ? 0 : data.size();
    }

    /**
     * 设置当前页数据列表，并重新计算派生字段
     *
     * @param data 数据列表
     * @return 当前 PageResponse 实例，支持链式调用
     */
    public PageResponse<T> setData(List<T> data) {
        this.data = data == null ? Collections.emptyList() : data;
        return this;
    }

    /**
     * 设置总记录数，并重新计算派生字段
     *
     * @param total 总记录数
     * @return 当前 PageResponse 实例，支持链式调用
     */
    public PageResponse<T> setTotal(Long total) {
        this.total = total;
        this.calculateDerivedFields();
        return this;
    }
}
