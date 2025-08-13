package com.gls.athena.common.core.base;

import com.gls.athena.common.bean.base.BaseVo;
import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 服务接口，定义了通用的业务操作方法
 *
 * @param <Vo> VO对象类型，必须继承BaseVo
 * @author george
 */
public interface IService<Vo extends BaseVo> {

    /**
     * 新增数据
     *
     * @param vo 待新增的VO对象
     * @return 新增后的VO对象
     */
    @CachePut(key = "#result.id", condition = "#result != null && #result.id != null")
    Vo insert(Vo vo);

    /**
     * 修改数据
     *
     * @param vo 待修改的VO对象
     * @return 修改后的VO对象
     */
    @CachePut(key = "#result.id", condition = "#result != null && #result.id != null")
    Vo update(Vo vo);

    /**
     * 删除数据
     *
     * @param id 待删除数据的ID
     * @return 删除是否成功
     */
    @CacheEvict(key = "#id", condition = "#id != null")
    Boolean delete(Long id);

    /**
     * 根据ID查询数据
     *
     * @param id 数据ID
     * @return 查询到的VO对象
     */
    @Cacheable(key = "#id", condition = "#id != null", unless = "#result == null")
    Vo get(Long id);

    /**
     * 查询数据列表
     *
     * @param vo 查询条件VO对象
     * @return 符合条件的VO对象列表
     */
    List<Vo> list(Vo vo);

    /**
     * 分页查询数据
     *
     * @param pageRequest 分页查询请求参数
     * @return 分页查询结果
     */
    PageResponse<Vo> page(PageRequest<Vo> pageRequest);

    /**
     * 批量保存数据（新增或修改）
     *
     * @param voList 待保存的VO对象列表
     * @return 保存是否成功
     */
    Boolean saveBatch(List<Vo> voList);
}
