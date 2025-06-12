package com.gls.athena.starter.mybatis.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import com.gls.athena.starter.mybatis.support.MybatisUtil;

/**
 * Mapper接口
 *
 * @param <E> 实体类型
 * @author george
 */
public interface IMapper<E> extends BaseMapper<E> {

    /**
     * 分页查询方法
     *
     * @param pageRequest 分页请求参数对象，包含以下要素：
     *                    - 分页参数：页码(page)、每页条数(size)等
     *                    - 查询条件参数：通过getParams()获取实体条件参数
     * @return PageResponse<E> 分页响应对象，包含：
     * - 数据列表：当前页数据集合
     * - 分页信息：总条数、总页数、当前页码等
     * @implNote 方法实现流程：
     * 1. 将自定义分页请求对象转换为MyBatis-Plus分页对象
     * 2. 执行带条件的分页查询（自动将实体参数转换为查询条件）
     * 3. 将MyBatis-Plus分页结果转换回统一分页响应格式
     */
    default PageResponse<E> selectPage(PageRequest<E> pageRequest) {
        // 转换分页请求参数格式
        IPage<E> page = MybatisUtil.toPage(pageRequest);

        // 执行带条件分页查询（自动封装查询条件到QueryWrapper）
        IPage<E> result = selectPage(page, new QueryWrapper<>(pageRequest.getParams()));

        // 转换分页结果格式
        return MybatisUtil.toPageResponse(result);
    }

}
