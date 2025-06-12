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
public class JpaUtil {
    /**
     * 将通用分页请求转换为JPA的Pageable对象
     *
     * @param pageRequest 通用分页请求对象，包含页码、每页大小、排序字段和排序方式
     * @return JPA的Pageable对象，用于JPA查询中的分页和排序
     */
    public Pageable toPageable(PageRequest<?> pageRequest) {
        // 如果pageRequest为null，返回默认的Pageable对象，页码为0，每页大小为10
        if (pageRequest == null) {
            return org.springframework.data.domain.PageRequest.of(0, 10);
        }

        // 获取排序字段和排序方式
        String sortField = pageRequest.getSort();
        String order = pageRequest.getOrder();

        // 如果排序字段为空，返回不带排序的Pageable对象
        if (sortField == null || sortField.isEmpty()) {
            return org.springframework.data.domain.PageRequest.of(
                    pageRequest.getPage(),
                    pageRequest.getSize()
            );
        }

        // 根据排序方式字符串确定排序方向，默认为ASC（升序）
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        // 返回包含分页和排序信息的Pageable对象
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
     * @param page JPA的分页结果对象，包含分页信息和数据列表
     * @param <E>  实体类型，表示分页数据中的元素类型
     * @return 通用分页响应对象，包含分页信息和数据列表。如果传入的page对象为null，则返回一个空的PageResponse对象
     */
    public <E> PageResponse<E> toPageResponse(Page<E> page) {
        // 如果传入的page对象为null，返回一个空的PageResponse对象
        if (page == null) {
            return new PageResponse<>();
        }

        // 将JPA的Page对象中的分页信息和数据列表提取出来，并设置到PageResponse对象中
        return new PageResponse<E>()
                .setPage(page.getNumber())
                .setSize(page.getSize())
                .setTotal(page.getTotalElements())
                .setPages(page.getTotalPages())
                .setData(page.getContent());
    }

}
