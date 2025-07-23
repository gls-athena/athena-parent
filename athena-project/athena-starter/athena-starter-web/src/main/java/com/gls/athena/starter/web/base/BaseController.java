package com.gls.athena.starter.web.base;

import com.gls.athena.common.bean.base.BaseVo;
import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import com.gls.athena.common.core.base.IFeign;
import com.gls.athena.common.core.base.IService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 基础控制器
 *
 * @param <Vo> 视图对象类型
 * @param <S>  服务对象类型
 * @author george
 */
public abstract class BaseController<Vo extends BaseVo, S extends IService<Vo>> implements IFeign<Vo> {
    /**
     * 服务对象
     */
    @Autowired
    protected S service;

    /**
     * 新增实体
     *
     * @param vo 待新增的实体视图对象
     * @return 新增后的完整视图对象
     */
    @Override
    @Operation(summary = "新增", description = "新增")
    public Vo insert(@RequestBody @Validated Vo vo) {
        return service.insert(vo);
    }

    /**
     * 更新实体
     *
     * @param vo 包含更新数据的视图对象
     * @return 更新后的完整视图对象
     */
    @Override
    @Operation(summary = "更新", description = "更新")
    public Vo update(@RequestBody @Validated Vo vo) {
        return service.update(vo);
    }

    /**
     * 删除实体
     *
     * @param id 实体的主键ID
     * @return 删除操作结果
     */
    @Override
    @Operation(summary = "删除", description = "删除")
    public Boolean delete(@PathVariable Long id) {
        return service.delete(id);
    }

    /**
     * 查询实体详情
     *
     * @param id 实体的主键ID
     * @return 实体的视图对象
     */
    @Override
    @Operation(summary = "查询", description = "通过主键ID获取实体详细信息")
    public Vo get(@PathVariable Long id) {
        return service.get(id);
    }

    /**
     * 列表查询
     *
     * @param vo 包含查询条件的视图对象
     * @return 查询结果列表
     */
    @Override
    @Operation(summary = "列表查询", description = "列表查询")
    public List<Vo> list(@RequestBody @Validated Vo vo) {
        return service.list(vo);
    }

    /**
     * 分页查询
     *
     * @param pageRequest 分页请求参数对象
     * @return 分页响应对象
     */
    @Override
    @Operation(summary = "分页查询", description = "分页查询")
    public PageResponse<Vo> page(@RequestBody @Validated PageRequest<Vo> pageRequest) {
        return service.page(pageRequest);
    }

    /**
     * 批量保存
     *
     * @param vos 视图对象集合
     * @return 批量保存结果
     */
    @Override
    @Operation(summary = "批量保存", description = "批量保存")
    public Boolean saveBatch(@RequestBody @Validated List<Vo> vos) {
        return service.saveBatch(vos);
    }

}
