package com.gls.athena.starter.data.jpa.base;

import com.gls.athena.common.bean.base.BaseVo;
import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import com.gls.athena.common.core.base.IConverter;
import com.gls.athena.common.core.base.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 通用基础服务实现类
 * <p>
 * 提供基础的 CRUD 操作实现，包括单条记录的增删改查、批量操作及分页查询等功能
 *
 * @param <V> 视图对象类型，需继承 BaseVo
 * @param <E> 实体对象类型，需继承 BaseEntity
 * @param <C> 对象转换器类型，用于 VO 和实体对象的互相转换
 * @param <R> 数据访问仓库类型，用于实体的持久化操作
 * @author george
 */
public abstract class BaseService<V extends BaseVo, E extends BaseEntity,
        C extends IConverter<V, E>, R extends IRepository<E>> implements IService<V> {

    /**
     * 对象转换器，用于 VO 和实体对象的互相转换
     */
    @Autowired
    protected C converter;

    /**
     * 数据访问仓库，处理实体的持久化操作
     */
    @Autowired
    protected R repository;

    /**
     * 新增记录
     * <p>
     * 该方法用于将传入的视图对象保存到数据库中，并返回包含生成ID等信息的视图对象。
     * 在保存之前，会进行以下校验：
     * 1. 待新增对象不能为空；
     * 2. 新增对象的ID必须为空。
     * 保存过程中，视图对象会被转换为实体对象进行存储，保存后再将实体对象转换回视图对象返回。
     *
     * @param vo 待新增的视图对象，不能为空且ID必须为空
     * @return 新增后的视图对象，包含生成的ID等信息
     */
    @Override
    public V insert(V vo) {
        // 校验待新增对象不能为空
        Assert.notNull(vo, "待新增对象不能为空");
        // 校验新增对象的ID必须为空
        Assert.isNull(vo.getId(), "新增对象ID必须为空");

        // 将视图对象转换为实体对象并保存到数据库，再将保存后的实体对象转换回视图对象返回
        return Optional.of(vo)
                .map(converter::convert)
                .map(repository::save)
                .map(converter::reverse)
                .orElseThrow(() -> new IllegalArgumentException("待新增对象不能为空"));
    }

    /**
     * 更新记录
     * <p>
     * 该方法用于更新数据库中的一条记录。首先会检查传入的视图对象及其ID是否为空，
     * 然后检查数据库中是否存在对应的记录。如果存在，则将视图对象转换为实体对象并保存到数据库，
     * 最后将更新后的实体对象转换回视图对象并返回。
     *
     * @param vo 待更新的视图对象，必须包含ID
     * @return 更新后的视图对象
     * @throws IllegalArgumentException 当vo为空或id为空时抛出，或者待更新的记录不存在时抛出
     */
    @Override
    public V update(V vo) {
        // 检查传入的视图对象及其ID是否为空
        Assert.notNull(vo, "待更新对象不能为空");
        Assert.notNull(vo.getId(), "更新对象ID不能为空");

        // 检查数据库中是否存在对应的记录
        if (!repository.existsById(vo.getId())) {
            throw new IllegalArgumentException("待更新的记录不存在");
        }

        // 将视图对象转换为实体对象并保存到数据库，最后将更新后的实体对象转换回视图对象并返回
        return Optional.of(vo)
                .map(converter::convert)
                .map(repository::save)
                .map(converter::reverse)
                .orElseThrow(() -> new IllegalArgumentException("待更新对象不能为空"));
    }

    /**
     * 根据ID删除记录
     *
     * @param id 记录ID，不能为空
     * @return true-删除成功；false-记录不存在
     * @throws IllegalArgumentException 当id为空时抛出
     * @throws RuntimeException         删除过程中发生异常时抛出
     */
    @Override
    public Boolean delete(Long id) {
        // 检查ID是否为空，若为空则抛出IllegalArgumentException
        Assert.notNull(id, "删除ID不能为空");

        try {
            // 检查记录是否存在，若存在则删除并返回true，否则返回false
            if (repository.existsById(id)) {
                repository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            // 捕获并包装异常，抛出RuntimeException
            throw new RuntimeException("删除记录失败", e);
        }
    }

    /**
     * 根据ID查询单条记录
     * <p>
     * 该方法通过传入的记录ID，从数据仓库中查询对应的记录，并将其转换为视图对象返回。
     * 如果查询的记录不存在，则返回null。
     *
     * @param id 记录ID，不能为空
     * @return 查询到的视图对象，不存在时返回null
     */
    @Override
    public V get(Long id) {
        // 校验传入的ID是否为空，若为空则抛出异常
        Assert.notNull(id, "查询ID不能为空");

        // 从数据仓库中根据ID查询记录，并将其转换为视图对象
        // 如果记录不存在，则返回null
        return repository.findById(id)
                .map(converter::reverse)
                .orElse(null);
    }

    /**
     * 根据条件查询记录列表
     * <p>
     * 该方法接收一个视图对象（VO）作为查询条件，通过转换器将其转换为实体对象，
     * 然后使用仓库（repository）查询符合条件的记录，最后将查询结果转换回视图对象列表。
     * 如果传入的查询条件为null，则返回空列表。
     *
     * @param vo 查询条件，以视图对象（VO）的形式传入
     * @return 符合条件的视图对象列表，如果查询条件为null则返回空列表
     */
    @Override
    public List<V> list(V vo) {
        // 使用Optional处理可能的null值，确保代码的健壮性
        return Optional.ofNullable(vo)
                // 将视图对象（VO）转换为实体对象
                .map(converter::convert)
                // 使用仓库查询符合条件的实体对象列表
                .map(repository::findAll)
                // 将实体对象列表转换回视图对象列表
                .map(converter::reverseList)
                // 如果查询条件为null，返回空列表
                .orElse(Collections.emptyList());
    }

    /**
     * 分页查询记录
     * <p>
     * 该方法根据传入的分页请求对象，执行分页查询操作，并返回分页查询结果。
     * 方法首先检查分页请求对象是否为空，若为空则抛出异常。若不为空，则依次执行以下操作：
     * 1. 使用转换器将分页请求对象转换为适合查询的格式；
     * 2. 调用仓库方法执行查询操作；
     * 3. 使用转换器将查询结果转换为分页响应格式。
     * 最终返回分页查询结果。
     *
     * @param pageRequest 分页查询请求对象，包含页码、每页大小、查询条件等信息
     * @return 分页查询结果对象，包含总记录数、当前页数据等信息
     * @throws IllegalArgumentException 如果分页请求对象为空，则抛出此异常
     */
    @Override
    public PageResponse<V> page(PageRequest<V> pageRequest) {
        // 使用Optional处理分页请求对象，确保其不为空
        return Optional.ofNullable(pageRequest)
                // 将分页请求对象转换为适合查询的格式
                .map(converter::convertPage)
                // 调用仓库方法执行查询操作
                .map(repository::findAll)
                // 将查询结果转换为分页响应格式
                .map(converter::reversePage)
                // 如果分页请求对象为空，抛出异常
                .orElseThrow(() -> new IllegalArgumentException("分页查询请求不能为空"));
    }

    /**
     * 批量保存记录
     * <p>
     * 该方法用于将传入的视图对象列表批量保存到数据库中。如果传入的列表为空或包含空值，则返回 false。
     * 该方法会过滤掉列表中的空值，并将有效的视图对象转换为实体对象后进行保存。
     * 如果保存过程中发生异常，则抛出运行时异常。
     *
     * @param vs 待保存的视图对象列表，允许为空或包含空值
     * @return true-全部保存成功；false-存在保存失败的记录或传入的列表为空
     * @throws RuntimeException 如果保存过程中发生异常，则抛出该异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveBatch(List<V> vs) {
        // 检查传入的列表是否为空或为空列表
        if (vs == null || vs.isEmpty()) {
            return false;
        }

        // 过滤掉列表中的空值，生成有效列表
        List<V> validList = vs.stream()
                .filter(Objects::nonNull)
                .toList();

        // 如果有效列表为空，则返回 false
        if (validList.isEmpty()) {
            return false;
        }

        try {
            // 将视图对象列表转换为实体对象列表并保存到数据库
            List<E> savedEntities = repository.saveAll(converter.convertList(validList));

            // 判断保存的实体数量是否与有效列表数量一致
            return savedEntities.size() == validList.size();
        } catch (Exception e) {
            // 如果保存过程中发生异常，则抛出运行时异常
            throw new RuntimeException("批量保存记录失败", e);
        }
    }

}
