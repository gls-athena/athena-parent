package com.gls.athena.starter.data.jpa.base;

import com.gls.athena.common.bean.base.BaseVo;
import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import com.gls.athena.common.core.base.IConverter;
import com.gls.athena.common.core.base.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 通用基础服务实现类
 * <p>
 * 提供基础的CRUD操作实现，包括单条记录的增删改查、批量操作及分页查询等功能。
 * 所有操作都包含必要的参数校验和异常处理。
 *
 * @param <V> 视图对象类型，继承BaseVo
 * @param <E> 实体对象类型，继承BaseEntity
 * @param <C> 对象转换器类型，用于VO和实体对象的相互转换
 * @param <R> 数据访问仓库类型，用于实体的持久化操作
 * @author george
 * @since 1.0.0
 */
public abstract class BaseService<V extends BaseVo, E extends BaseEntity,
        C extends IConverter<V, E>, R extends IRepository<E>> implements IService<V> {

    /**
     * 对象转换器，用于VO和实体对象的相互转换
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
     * 将视图对象保存到数据库中。会校验对象不为空且ID为空。
     *
     * @param vo 待新增的视图对象，不能为空且ID必须为空
     * @return 新增后的视图对象，包含生成的ID
     * @throws IllegalArgumentException 当vo为空或ID不为空时
     */
    @Override
    public V insert(V vo) {
        Assert.notNull(vo, "待新增对象不能为空");
        Assert.isNull(vo.getId(), "新增对象ID必须为空");

        E entity = converter.convert(vo);
        E savedEntity = repository.save(entity);
        return converter.reverse(savedEntity);
    }

    /**
     * 更新记录
     * <p>
     * 根据ID更新数据库中的记录。会校验对象和ID不为空，且记录必须存在。
     *
     * @param vo 待更新的视图对象，必须包含有效ID
     * @return 更新后的视图对象
     * @throws IllegalArgumentException 当vo为空、ID为空或记录不存在时
     */
    @Override
    public V update(V vo) {
        Assert.notNull(vo, "待更新对象不能为空");
        Assert.notNull(vo.getId(), "更新对象ID不能为空");

        E entity = repository.findById(vo.getId())
                .orElseThrow(() -> new IllegalArgumentException("待更新的记录不存在"));

        E updatedEntity = converter.convert(vo);
        updatedEntity.setId(entity.getId()); // 确保ID一致
        E savedEntity = repository.save(updatedEntity);
        return converter.reverse(savedEntity);
    }

    /**
     * 根据ID删除记录
     *
     * @param id 记录ID，不能为空
     * @return true-删除成功；false-记录不存在
     * @throws IllegalArgumentException 当id为空时
     * @throws RuntimeException         删除过程中发生异常时
     */
    @Override
    public Boolean delete(Long id) {
        Assert.notNull(id, "删除ID不能为空");

        if (!repository.existsById(id)) {
            return false;
        }

        try {
            repository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("删除记录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据ID查询单条记录
     *
     * @param id 记录ID，不能为空
     * @return 查询到的视图对象，不存在时返回null
     * @throws IllegalArgumentException 当id为空时
     */
    @Override
    public V get(Long id) {
        Assert.notNull(id, "查询ID不能为空");

        return repository.findById(id)
                .map(converter::reverse)
                .orElse(null);
    }

    /**
     * 根据条件查询记录列表
     *
     * @param vo 查询条件，为null时返回空列表
     * @return 符合条件的视图对象列表
     */
    @Override
    public List<V> list(V vo) {
        if (vo == null) {
            return Collections.emptyList();
        }

        E entity = converter.convert(vo);
        if (entity == null) {
            return Collections.emptyList();
        }

        List<E> entities = repository.findAll(entity);
        return converter.reverseList(entities);
    }

    /**
     * 分页查询记录
     *
     * @param pageRequest 分页查询请求，包含页码、每页大小、查询条件等
     * @return 分页查询结果，包含总记录数、当前页数据等
     * @throws IllegalArgumentException 当pageRequest为空时
     */
    @Override
    public PageResponse<V> page(PageRequest<V> pageRequest) {
        Assert.notNull(pageRequest, "分页查询请求不能为空");
        Assert.isTrue(pageRequest.getPage() >= 0, "页码不能小于0");
        Assert.isTrue(pageRequest.getSize() > 0, "每页大小必须大于0");

        return Optional.of(pageRequest)
                .map(converter::convertPage)
                .map(repository::findAll)
                .map(converter::reversePage)
                .orElseThrow(() -> new IllegalArgumentException("分页查询请求不能为空"));
    }

    /**
     * 批量保存记录
     * <p>
     * 过滤空值后批量保存视图对象列表。使用事务确保数据一致性。
     *
     * @param list 待保存的视图对象列表
     * @return true-全部保存成功；false-列表为空或无有效数据
     * @throws RuntimeException 保存过程中发生异常时
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Boolean saveBatch(List<V> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }

        try {
            List<E> entities = converter.convertList(list);
            List<E> savedEntities = repository.saveAll(entities);
            return savedEntities.size() == list.size();
        } catch (Exception e) {
            throw new RuntimeException("批量保存记录失败: " + e.getMessage(), e);
        }
    }

}
