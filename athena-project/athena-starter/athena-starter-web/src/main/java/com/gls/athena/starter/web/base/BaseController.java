package com.gls.athena.starter.web.base;

import com.gls.athena.common.bean.base.BaseVo;
import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import com.gls.athena.common.core.base.IFeign;
import com.gls.athena.common.core.base.IService;
import com.gls.athena.starter.excel.annotation.ExcelRequest;
import com.gls.athena.starter.excel.annotation.ExcelResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 基础控制器
 *
 * @param <Vo> 视图
 * @param <S>  服务
 * @author george
 */
public abstract class BaseController<Vo extends BaseVo, S extends IService<Vo>> implements IFeign<Vo> {
    /**
     * 服务
     */
    @Autowired
    protected S service;

    /**
     * 新增
     *
     * @param vo VO 对象
     * @return 新增结果
     */
    @Override
    @Operation(summary = "新增", description = "新增")
    public Vo insert(@RequestBody @Validated Vo vo) {
        return service.insert(vo);
    }

    /**
     * 更新
     *
     * @param vo VO 对象
     * @return 更新结果
     */
    @Override
    @Operation(summary = "更新", description = "更新")
    public Vo update(@RequestBody @Validated Vo vo) {
        return service.update(vo);
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return 删除结果
     */
    @Override
    @Operation(summary = "删除", description = "删除")
    public Boolean delete(@PathVariable Long id) {
        return service.delete(id);
    }

    /**
     * 查询
     *
     * @param id 主键
     * @return 查询结果
     */
    @Override
    @Operation(summary = "查询", description = "查询")
    public Vo get(@PathVariable Long id) {
        return service.get(id);
    }

    /**
     * 列表查询
     *
     * @param vo VO 对象
     * @return 查询结果
     */
    @Override
    @Operation(summary = "列表查询", description = "列表查询")
    public List<Vo> list(@RequestBody @Validated Vo vo) {
        return service.list(vo);
    }

    /**
     * 分页查询
     *
     * @param pageRequest 分页查询参数
     * @return 分页查询结果
     */
    @Override
    @Operation(summary = "分页查询", description = "分页查询")
    public PageResponse<Vo> page(@RequestBody @Validated PageRequest<Vo> pageRequest) {
        return service.page(pageRequest);
    }

    /**
     * 批量保存
     *
     * @param vos VO 对象
     * @return 保存结果
     */
    @Override
    @Operation(summary = "批量保存", description = "批量保存")
    public Boolean saveBatch(@RequestBody @Validated List<Vo> vos) {
        return service.saveBatch(vos);
    }

    /**
     * 导入
     *
     * @param vos 导入对象
     * @return 导入结果
     */
    @Operation(summary = "导入", description = "导入")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Boolean importExcel(@ExcelRequest List<Vo> vos) {
        return service.saveBatch(vos);
    }

    /**
     * 导出
     *
     * @param vo 查询对象
     * @return 导出结果
     */
    @Operation(summary = "导出", description = "导出")
    @PostMapping(value = "/export")
    @ExcelResponse(filename = "导出数据")
    public List<Vo> exportExcel(@RequestBody @Validated Vo vo) {
        return service.list(vo);
    }
}
