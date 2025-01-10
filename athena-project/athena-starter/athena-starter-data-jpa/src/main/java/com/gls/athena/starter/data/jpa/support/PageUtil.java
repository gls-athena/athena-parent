package com.gls.athena.starter.data.jpa.support;

import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * JPA分页工具类，用于转换通用分页请求/响应与Spring Data JPA的分页对象
 *
 * @author george
 */
@UtilityClass
public class PageUtil {
    /**
     * 将通用分页请求转换为JPA的Pageable对象
     *
     * @param pageRequest 通用分页请求对象，包含页码、每页大小、排序字段和排序方式
     * @return JPA的Pageable对象
     */
    public Pageable toPageable(PageRequest<?> pageRequest) {
        if (pageRequest == null) {
            return org.springframework.data.domain.PageRequest.of(0, 10);
        }

        String sortField = pageRequest.getSort();
        String order = pageRequest.getOrder();

        if (sortField == null || sortField.isEmpty()) {
            return org.springframework.data.domain.PageRequest.of(
                    pageRequest.getPage(),
                    pageRequest.getSize()
            );
        }

        Sort.Direction direction = "desc".equalsIgnoreCase(order) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        return org.springframework.data.domain.PageRequest.of(
                pageRequest.getPage(),
                pageRequest.getSize(),
                direction,
                sortField
        );
    }

    /**
     * 将JPA的Page对象转换为通用分页响应对象
     *
     * @param page JPA的分页结果对象
     * @param <E>  实体类型
     * @return 通用分页响应对象，包含分页信息和数据列表
     */
    public <E> PageResponse<E> toPageResponse(Page<E> page) {
        if (page == null) {
            return new PageResponse<>();
        }

        return new PageResponse<E>()
                .setPage(page.getNumber())
                .setSize(page.getSize())
                .setTotal(page.getTotalElements())
                .setPages(page.getTotalPages())
                .setData(page.getContent());
    }
}
