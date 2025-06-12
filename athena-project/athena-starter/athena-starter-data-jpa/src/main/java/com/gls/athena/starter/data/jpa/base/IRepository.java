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
 * 提供实体的基本CRUD和分页查询功能
 *
 * @param <E> 实体类型
 * @author george
 */
@NoRepositoryBean
public interface IRepository<E extends BaseEntity> extends JpaRepositoryImplementation<E, Long> {

    /**
     * 根据实体条件进行分页查询
     * <p>
     * 该方法通过传入的查询条件实体和分页参数，执行分页查询操作，并返回分页结果。
     * 内部使用 `Example.of(criteria)` 将查询条件实体转换为 `Example` 对象，
     * 然后调用 `findAll(Example, Pageable)` 方法进行查询。
     *
     * @param criteria 查询条件实体，用于指定查询的过滤条件
     * @param pageable 分页参数，包含分页信息如页码、每页大小等
     * @return 分页结果，包含查询到的数据列表及分页信息
     */
    default Page<E> findAll(E criteria, Pageable pageable) {
        return findAll(Example.of(criteria), pageable);
    }

    /**
     * 根据实体条件查询所有匹配记录
     * <p>
     * 该方法通过传入的实体对象作为查询条件，使用Example.of方法将其转换为Example对象，
     * 并调用findAll方法查询所有符合条件的记录。
     *
     * @param criteria 查询条件实体，用于构建查询条件
     * @return 返回与查询条件匹配的实体列表
     */
    default List<E> findAll(E criteria) {
        return findAll(Example.of(criteria));
    }

    /**
     * 执行分页查询并转换为统一的分页响应格式
     * <p>
     * 该方法接收一个分页查询请求对象，将其转换为Spring Data JPA的Pageable对象，
     * 然后根据查询参数和分页信息执行查询操作，最后将查询结果转换为统一的分页响应格式。
     *
     * @param pageRequest 分页查询请求对象，包含查询条件和分页参数
     * @return 统一格式的分页响应，包含查询结果和分页信息
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
