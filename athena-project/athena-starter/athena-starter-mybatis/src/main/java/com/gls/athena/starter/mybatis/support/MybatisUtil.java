package com.gls.athena.starter.mybatis.support;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import lombok.experimental.UtilityClass;

/**
 * Mybatis工具类
 *
 * @author george
 */
@UtilityClass
public class MybatisUtil {
    /**
     * 获取分页参数
     *
     * @param pageRequest 分页查询
     * @param <E>         实体类型
     * @return 分页参数
     */
    public <E> IPage<E> toPage(PageRequest<E> pageRequest) {
        Page<E> page = new Page<>(pageRequest.getPage(), pageRequest.getSize());
        String order = pageRequest.getOrder();
        if ("desc".equalsIgnoreCase(order)) {
            page.addOrder(OrderItem.desc(pageRequest.getSort()));
        } else if ("asc".equalsIgnoreCase(order)) {
            page.addOrder(OrderItem.asc(pageRequest.getSort()));
        }
        return page;
    }

    /**
     * 获取分页结果
     *
     * @param result 分页数据
     * @param <E>    实体类型
     * @return 分页结果
     */
    public <E> PageResponse<E> toPageResponse(IPage<E> result) {
        return new PageResponse<E>()
                .setPage((int) result.getCurrent())
                .setSize((int) result.getSize())
                .setTotal(result.getTotal())
                .setPages((int) result.getPages())
                .setData(result.getRecords());
    }
}
