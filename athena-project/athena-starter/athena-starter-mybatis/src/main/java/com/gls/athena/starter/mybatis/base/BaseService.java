package com.gls.athena.starter.mybatis.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gls.athena.common.bean.base.BaseVo;
import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import com.gls.athena.common.core.base.IConverter;
import com.gls.athena.common.core.base.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 基础服务类
 *
 * @param <V> 视图对象
 * @param <E> 实体对象
 * @param <C> 转换器
 * @param <M> Mapper
 * @author george
 */
public abstract class BaseService<V extends BaseVo, E extends BaseEntity,
        C extends IConverter<V, E>, M extends IMapper<E>>
        extends ServiceImpl<M, E> implements IService<V> {
    /**
     * 转换器
     */
    @Autowired
    protected C converter;

    /**
     * 插入一个VO对象并将其转换为实体对象进行保存，最后将保存后的实体对象转换回VO对象返回。
     *
     * @param vo 要插入的VO对象，不能为null
     * @return 保存后的VO对象
     * @throws IllegalArgumentException 如果传入的VO对象为null，抛出此异常
     * @throws RuntimeException         如果插入操作过程中发生异常，抛出此异常
     */
    @Override
    public V insert(V vo) {
        // 检查传入的VO对象是否为null，若为null则抛出异常
        if (vo == null) {
            throw new IllegalArgumentException("VO对象不能为null");
        }

        try {
            // 将VO对象转换为实体对象
            E entity = converter.convert(vo);
            // 保存实体对象
            save(entity);
            // 将保存后的实体对象转换回VO对象并返回
            return converter.reverse(entity);
        } catch (Exception e) {
            // 记录日志或抛出自定义异常
            throw new RuntimeException("插入操作失败", e);
        }
    }

    /**
     * 更新 VO 对象对应的实体，并返回更新后的 VO 对象。
     *
     * @param vo 需要更新的 VO 对象，不能为 null
     * @return 更新后的 VO 对象
     * @throws IllegalArgumentException 如果 VO 对象或转换器为 null，抛出此异常
     * @throws RuntimeException         如果更新操作过程中发生异常，抛出此异常
     */
    @Override
    public V update(V vo) {
        // 非空检查：确保 VO 对象和转换器都不为 null
        if (vo == null || converter == null) {
            throw new IllegalArgumentException("VO对象或转换器不能为空");
        }

        try {
            // 将 VO 对象转换为实体对象
            E entity = converter.convert(vo);
            // 更新实体对象
            updateById(entity);
            // 将更新后的实体对象转换回 VO 对象并返回
            return converter.reverse(entity);
        } catch (Exception e) {
            // 异常处理：捕获并包装异常，抛出运行时异常
            throw new RuntimeException("更新操作失败", e);
        }
    }

    /**
     * 删除
     *
     * @param id ID 主键
     * @return 是否成功
     */
    @Override
    public Boolean delete(Long id) {
        return removeById(id);
    }

    /**
     * 获取
     *
     * @param id ID 主键
     * @return VO 对象
     */
    @Override
    public V get(Long id) {
        return converter.reverse(getById(id));
    }

    /**
     * 列表
     *
     * @param vo VO 对象
     * @return VO 对象列表
     */
    @Override
    public List<V> list(V vo) {
        return converter.reverseList(list(new QueryWrapper<>(converter.convert(vo))));
    }

    /**
     * 分页
     *
     * @param pageRequest 分页请求
     * @return 分页响应
     */
    @Override
    public PageResponse<V> page(PageRequest<V> pageRequest) {
        return converter.reversePage(baseMapper.selectPage(converter.convertPage(pageRequest)));
    }

    /**
     * 批量插入
     *
     * @param vs VO列表
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveBatch(List<V> vs) {
        return saveBatch(converter.convertList(vs));
    }
}
