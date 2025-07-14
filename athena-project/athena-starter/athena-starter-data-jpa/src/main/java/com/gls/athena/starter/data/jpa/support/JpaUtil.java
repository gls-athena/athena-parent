package com.gls.athena.starter.data.jpa.support;

import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * JPA分页工具类
 * <p>
 * 提供通用分页对象与Spring Data JPA分页对象之间的转换功能
 *
 * @author george
 */
@UtilityClass
public class JpaUtil {

    /**
     * 转换分页请求为JPA Pageable对象
     *
     * @param pageRequest 分页请求，可为null
     * @return JPA Pageable对象，null时返回默认分页(page=0, size=10)
     */
    public Pageable toPageable(PageRequest<?> pageRequest) {
        if (pageRequest == null) {
            return org.springframework.data.domain.PageRequest.of(0, 10);
        }

        String sortField = pageRequest.getSort();
        String order = pageRequest.getOrder();

        // 无排序字段时仅设置分页参数
        if (sortField == null || sortField.isEmpty()) {
            return org.springframework.data.domain.PageRequest.of(
                    pageRequest.getPage(),
                    pageRequest.getSize()
            );
        }

        // 解析排序方向，默认升序
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
     * 转换JPA分页结果为通用分页响应
     *
     * @param page JPA分页结果，可为null
     * @param <E>  数据元素类型
     * @return 分页响应对象，null时返回空响应
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
