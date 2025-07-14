package com.gls.athena.starter.data.jpa.base;

import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import com.gls.athena.starter.data.jpa.support.JpaUtil;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * 基础数据访问接口
 *
 * <p>扩展了 Spring Data JPA 的 {@link JpaRepositoryImplementation}，
 * 为继承 {@link BaseEntity} 的实体提供标准的 CRUD 操作和分页查询功能。
 *
 * <p>主要特性：
 * <ul>
 *   <li>基于实体属性的条件查询</li>
 *   <li>统一的分页查询响应格式</li>
 *   <li>与项目通用分页组件的集成</li>
 * </ul>
 *
 * @param <E> 实体类型，必须继承 {@link BaseEntity}
 * @author george
 * @since 1.0
 */
@NoRepositoryBean
public interface IRepository<E extends BaseEntity> extends JpaRepositoryImplementation<E, Long> {

    /**
     * 根据实体条件进行分页查询
     *
     * <p>基于 {@link Example} 的查询匹配，自动处理非空属性作为查询条件。
     *
     * @param criteria 查询条件实体，非空属性将作为过滤条件
     * @param pageable 分页参数
     * @return 分页查询结果
     */
    default Page<E> findAll(E criteria, Pageable pageable) {
        return findAll(Example.of(criteria), pageable);
    }

    /**
     * 根据实体条件查询所有匹配记录
     *
     * <p>基于 {@link Example} 的查询匹配，自动处理非空属性作为查询条件。
     *
     * @param criteria 查询条件实体，非空属性将作为过滤条件
     * @return 匹配的实体列表
     */
    default List<E> findAll(E criteria) {
        return findAll(Example.of(criteria));
    }

    /**
     * 执行分页查询并转换为统一响应格式
     *
     * <p>将项目标准的 {@link PageRequest} 转换为 Spring Data 的 {@link Pageable}，
     * 执行查询后返回统一的 {@link PageResponse} 格式。
     *
     * @param pageRequest 分页查询请求，包含查询条件和分页参数
     * @return 统一格式的分页响应
     */
    default PageResponse<E> findAll(PageRequest<E> pageRequest) {
        // 将分页请求对象转换为Spring Data JPA的Pageable对象
        Pageable pageable = JpaUtil.toPageable(pageRequest);

        // 根据查询参数和分页信息执行查询操作
        Page<E> page = findAll(pageRequest.getParams(), pageable);

        // 将查询结果转换为统一的分页响应格式
        return JpaUtil.toPageResponse(page);
    }

}
