package com.gls.athena.common.bean.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

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
}

