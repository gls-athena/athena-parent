package com.gls.athena.starter.mybatis.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gls.athena.common.bean.base.BaseVo;
import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import com.gls.athena.common.core.base.IConverter;
import com.gls.athena.common.core.base.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 基础服务类
 *
 * @param <V> 视图对象
 * @param <E> 实体对象
 * @param <C> 转换器
 * @param <M> Mapper
 * @author george
 */
@Slf4j
public abstract class BaseService<V extends BaseVo, E extends BaseEntity,
        C extends IConverter<V, E>, M extends IMapper<E>>
        extends ServiceImpl<M, E> implements IService<V> {
    /**
     * 转换器
     */
    @Autowired
    protected C converter;

    /**
     * 插入一条记录。
     *
     * @param vo 要插入的视图对象，不能为null
     * @return 插入成功后的视图对象，如果插入失败则返回null
     * @throws IllegalArgumentException 如果传入的vo为null
     */
    @Override
    public V insert(V vo) {
        // 检查传入的VO对象是否为null，若为null则抛出异常
        if (vo == null) {
            log.error("插入操作失败，传入的VO对象为null");
            return null;
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
            log.error("插入操作失败", e);
            return null;
        }
    }

    /**
     * 更新一条记录。
     *
     * @param vo 要更新的视图对象，不能为null
     * @return 更新成功后的视图对象，如果更新失败则返回null
     * @throws IllegalArgumentException 如果传入的vo或转换器为null
     */
    @Override
    public V update(V vo) {
        // 非空检查：确保 VO 对象和转换器都不为 null
        if (vo == null || converter == null) {
            log.error("更新操作失败，传入的VO对象或转换器为null");
            return null;
        }

        try {
            // 将 VO 对象转换为实体对象
            E entity = converter.convert(vo);
            // 更新实体对象
            updateById(entity);
            // 将更新后的实体对象转换回 VO 对象并返回
            return converter.reverse(entity);
        } catch (Exception e) {
            log.error("更新操作失败", e);
            return null;
        }
    }

    /**
     * 根据主键ID删除一条记录。
     *
     * @param id 要删除记录的主键ID，必须大于0
     * @return 删除成功返回true，否则返回false
     * @throws IllegalArgumentException 如果传入的id为null或小于等于0
     */
    @Override
    public Boolean delete(Long id) {
        try {
            // 参数有效性校验
            if (id == null || id <= 0L) {
                log.error("删除操作失败，传入的ID为空或小于等于0");
                return false;
            }
            return removeById(id);
        } catch (Exception e) {
            // 记录异常日志（根据实际情况实现日志记录）
            log.error("删除操作失败", e);
            return false;
        }
    }

    /**
     * 根据主键ID获取对应的视图对象。
     *
     * @param id 要查询记录的主键ID，必须大于0
     * @return 查询到的视图对象，如果查询失败则返回null
     * @throws IllegalArgumentException 如果传入的id为null或小于等于0
     */
    @Override
    public V get(Long id) {
        if (id == null || id <= 0L) {
            log.error("根据主键ID获取实体视图对象失败，传入的ID为空或小于等于0");
            return null;
        }

        try {
            return converter.reverse(getById(id));
        } catch (Exception e) {
            log.error("根据主键ID获取实体视图对象失败", e);
            return null;
        }
    }

    /**
     * 根据查询条件获取视图对象列表。
     *
     * @param vo 查询条件对象，不能为null
     * @return 查询到的视图对象列表，如果查询失败则返回null
     * @throws IllegalArgumentException 如果传入的vo或转换器为null
     */
    @Override
    public List<V> list(V vo) {
        // 防御性校验
        if (vo == null || converter == null) {
            log.error("根据查询条件获取VO对象列表失败，输入参数为null");
            return null;
        }
        try {
            // 转换查询条件
            final E queryCondition = converter.convert(vo);

            // 构建查询条件包装器
            QueryWrapper<E> queryWrapper = new QueryWrapper<>(queryCondition);

            // 执行基础查询并转换结果
            return converter.reverseList(list(queryWrapper));
        } catch (Exception e) {
            log.error("根据查询条件获取VO对象列表失败", e);
            return null;
        }
    }

    /**
     * 执行分页查询。
     *
     * @param pageRequest 分页请求对象，不能为null
     * @return 分页响应对象，如果查询失败则返回null
     * @throws IllegalArgumentException 如果传入的pageRequest为null
     */
    @Override
    public PageResponse<V> page(PageRequest<V> pageRequest) {
        // 参数校验
        if (pageRequest == null) {
            log.error("分页查询失败，传入的PageRequest对象为null");
            return null;
        }

        try {
            // 设置默认分页参数
            int pageNum = Math.max(pageRequest.getPage(), 1);
            int pageSize = pageRequest.getSize() > 0 ?
                    Math.min(pageRequest.getSize(), 500) : 10;
            pageRequest.setPage(pageNum);
            pageRequest.setSize(pageSize);
            return Optional.of(pageRequest)
                    .map(converter::convertPage)
                    .map(baseMapper::selectPage)
                    .map(converter::reversePage)
                    .orElse(null);
        } catch (Exception e) {
            // 记录异常日志
            log.error("分页查询失败", e);
            return null;
        }
    }

    /**
     * 批量插入视图对象列表。
     *
     * @param vs 要插入的视图对象列表，不能为null或空列表
     * @return 批量插入成功返回true，否则返回false
     * @throws IllegalArgumentException 如果传入的vs为null或空列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveBatch(List<V> vs) {
        if (vs == null || vs.isEmpty()) {
            log.error("批量插入失败，传入的VO列表为空或为空列表");
            return false;
        }
        try {
            return saveBatch(converter.convertList(vs));
        } catch (Exception e) {
            log.error("批量插入失败", e);
            return false;
        }
    }
}
