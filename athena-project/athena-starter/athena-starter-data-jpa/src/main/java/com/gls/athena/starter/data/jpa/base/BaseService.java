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
     *
     * @param vo 待新增的视图对象
     * @return 新增后的视图对象（包含生成的ID等信息）
     */
    @Override
    public V insert(V vo) {
        Assert.notNull(vo, "待新增对象不能为空");
        Assert.isNull(vo.getId(), "新增对象ID必须为空");
        return converter.reverse(repository.save(converter.convert(vo)));
    }

    /**
     * 更新记录
     *
     * @param vo 待更新的视图对象（必须包含ID）
     * @return 更新后的视图对象
     * @throws IllegalArgumentException 当vo为空或id为空时抛出
     */
    @Override
    public V update(V vo) {
        Assert.notNull(vo, "待更新对象不能为空");
        Assert.notNull(vo.getId(), "更新对象ID不能为空");
        // 检查记录是否存在
        if (!repository.existsById(vo.getId())) {
            throw new IllegalArgumentException("待更新的记录不存在");
        }
        return converter.reverse(repository.save(converter.convert(vo)));
    }

    /**
     * 根据ID删除记录
     *
     * @param id 记录ID
     * @return true-删除成功；false-记录不存在
     * @throws IllegalArgumentException 当id为空时抛出
     */
    @Override
    public Boolean delete(Long id) {
        Assert.notNull(id, "删除ID不能为空");
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("删除记录失败", e);
        }
    }

    /**
     * 根据ID查询单条记录
     *
     * @param id 记录ID
     * @return 查询到的视图对象，不存在时返回null
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
     * @param vo 查询条件（视图对象形式）
     * @return 符合条件的视图对象列表
     */
    @Override
    public List<V> list(V vo) {
        if (vo == null) {
            return Collections.emptyList();
        }
        return converter.reverseList(repository.findAll(converter.convert(vo)));
    }

    /**
     * 分页查询记录
     *
     * @param pageRequest 分页查询请求（包含页码、每页大小、查询条件等）
     * @return 分页查询结果（包含总记录数、当前页数据等）
     */
    @Override
    public PageResponse<V> page(PageRequest<V> pageRequest) {
        Assert.notNull(pageRequest, "分页请求参数不能为空");
        Assert.notNull(pageRequest.getPage(), "页码不能为空");
        Assert.notNull(pageRequest.getSize(), "每页大小不能为空");
        return converter.reversePage(repository.findAll(converter.convertPage(pageRequest)));
    }

    /**
     * 批量保存记录
     *
     * @param vs 待保存的视图对象列表
     * @return true-全部保存成功；false-存在保存失败的记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveBatch(List<V> vs) {
        if (vs == null || vs.isEmpty()) {
            return false;
        }
        // 过滤空值
        List<V> validList = vs.stream()
                .filter(Objects::nonNull)
                .toList();
        if (validList.isEmpty()) {
            return false;
        }
        try {
            List<E> savedEntities = repository.saveAll(converter.convertList(validList));
            return savedEntities.size() == validList.size();
        } catch (Exception e) {
            throw new RuntimeException("批量保存记录失败", e);
        }
    }
}
