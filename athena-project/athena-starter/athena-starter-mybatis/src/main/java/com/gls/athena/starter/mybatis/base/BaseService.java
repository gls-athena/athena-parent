package com.gls.athena.starter.mybatis.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        if (vo == null) {
            log.error("插入操作失败，传入的VO对象为null");
            return null;
        }

        // 将视图对象转换为实体对象并保存
        try {
            E entity = converter.convert(vo);
            boolean saved = save(entity);
            if (!saved) {
                log.warn("插入操作未成功保存实体");
                return null;
            }
            return converter.reverse(entity);
        } catch (Exception e) {
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
        // 参数校验：检查VO对象和转换器是否为null
        if (vo == null || converter == null) {
            log.error("更新操作失败，传入的VO对象或转换器为null");
            return null;
        }

        try {
            // 将VO对象转换为实体对象并获取ID
            E entity = converter.convert(vo);
            Long id = entity.getId();

            // 校验ID的有效性：ID必须存在且大于0，同时数据库中必须存在该ID的记录
            if (id == null || id <= 0L || !existsById(id)) {
                log.warn("更新操作失败，指定ID不存在或非法: {}", id);
                return null;
            }

            // 执行更新操作
            boolean updated = updateById(entity);
            if (!updated) {
                log.warn("更新操作未成功更新实体");
                return null;
            }

            // 更新成功后，将实体对象转换回VO对象并返回
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
            // 验证传入的ID是否有效
            if (id == null || id <= 0L) {
                log.error("删除操作失败，传入的ID为空或小于等于0");
                return false;
            }
            // 执行删除操作
            return removeById(id);
        } catch (Exception e) {
            // 记录删除操作异常
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
        // 参数校验：检查ID是否为空或小于等于0
        if (id == null || id <= 0L) {
            log.error("根据主键ID获取实体视图对象失败，传入的ID为空或小于等于0");
            return null;
        }

        try {
            // 根据ID获取实体对象
            E entity = getById(id);
            if (entity == null) {
                log.warn("未找到ID为 {} 的实体", id);
                return null;
            }
            // 将实体对象转换为视图对象并返回
            return converter.reverse(entity);
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
        if (vo == null || converter == null) {
            log.error("根据查询条件获取VO对象列表失败，输入参数为null");
            return null;
        }
        try {
            // 将视图对象转换为实体查询条件
            E queryCondition = converter.convert(vo);
            // 构造查询条件包装器
            LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>(queryCondition);
            // 可根据实际需求添加字段过滤逻辑
            // 执行查询并将结果转换为视图对象列表
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
        if (pageRequest == null) {
            log.error("分页查询失败，传入的PageRequest对象为null");
            return null;
        }

        try {
            // 校验并设置安全的分页参数，页码最小为1，页面大小范围为1-500
            int pageNum = Math.max(pageRequest.getPage(), 1);
            int pageSize = pageRequest.getSize() > 0 ?
                    Math.min(pageRequest.getSize(), 500) : 10;

            PageRequest<V> safePageRequest = new PageRequest<>();
            safePageRequest.setPage(pageNum);
            safePageRequest.setSize(pageSize);
            safePageRequest.setParams(pageRequest.getParams());

            // 执行分页查询流程：转换请求->数据库查询->转换响应
            return Optional.of(safePageRequest)
                    .map(converter::convertPage)
                    .map(baseMapper::selectPage)
                    .map(converter::reversePage)
                    .orElse(null);
        } catch (Exception e) {
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
        // 参数校验：检查传入的视图对象列表是否为空
        if (vs == null || vs.isEmpty()) {
            log.error("批量插入失败，传入的VO列表为空或为空列表");
            return false;
        }

        try {
            // 将视图对象列表转换为实体对象列表并执行批量保存
            List<E> entities = converter.convertList(vs);
            return saveBatch(entities, 100);
        } catch (Exception e) {
            // 记录批量插入异常日志并返回失败状态
            log.error("批量插入失败", e);
            return false;
        }
    }

    /**
     * 检查指定ID的实体是否存在。
     *
     * @param id 实体ID
     * @return 存在返回true，否则返回false
     */
    private boolean existsById(Long id) {
        // 检查ID是否有效且对应的实体存在
        return id != null && id > 0 && getById(id) != null;
    }

}
