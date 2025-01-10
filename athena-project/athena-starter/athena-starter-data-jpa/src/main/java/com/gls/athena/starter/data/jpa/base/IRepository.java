package com.gls.athena.starter.data.jpa.base;

import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import com.gls.athena.starter.data.jpa.support.PageUtil;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * 基础数据访问接口
 * 提供实体的基本CRUD和分页查询功能
 *
 * @param <E> 实体类型
 * @author george
 */
@NoRepositoryBean
public interface IRepository<E extends BaseEntity> extends JpaRepositoryImplementation<E, Long> {

    /**
     * 根据实体条件进行分页查询
     *
     * @param criteria 查询条件实体
     * @param pageable 分页参数
     * @return 分页结果
     */
    default Page<E> findAll(E criteria, Pageable pageable) {
        return findAll(Example.of(criteria), pageable);
    }

    /**
     * 根据实体条件查询所有匹配记录
     *
     * @param criteria 查询条件实体
     * @return 匹配的实体列表
     */
    default List<E> findAll(E criteria) {
        return findAll(Example.of(criteria));
    }

    /**
     * 执行分页查询并转换为统一的分页响应格式
     *
     * @param pageRequest 分页查询请求，包含查询条件和分页参数
     * @return 统一格式的分页响应
     */
    default PageResponse<E> findAll(PageRequest<E> pageRequest) {
        Pageable pageable = PageUtil.toPageable(pageRequest);
        Page<E> page = findAll(pageRequest.getParams(), pageable);
        return PageUtil.toPageResponse(page);
    }
}
