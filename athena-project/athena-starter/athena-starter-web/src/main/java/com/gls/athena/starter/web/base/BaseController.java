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
     * 新增VO对象到系统
     * <p>
     * 该方法接收经过验证的VO对象，通过服务层将数据持久化到数据库，
     * 返回包含完整信息的新增结果（如生成的主键等持久化数据）
     *
     * @param vo 包含待新增数据的VO对象，需要符合预设的数据校验规则
     * @return 新增成功后的完整VO对象实例，包含数据库生成的字段信息
     */
    @Override
    @Operation(summary = "新增", description = "新增")
    public Vo insert(@RequestBody @Validated Vo vo) {
        // 调用服务层完成核心业务逻辑
        return service.insert(vo);
    }

    /**
     * 根据传入的VO对象进行数据更新操作
     *
     * @param vo 包含更新数据的VO对象，通过请求体接收并使用Spring Validation进行校验
     *           {@code @RequestBody} 注解表示从请求体反序列化参数
     *           {@code @Validated} 注解会触发JSR-380校验规则
     * @return 包含最新更新结果的VO对象，包含服务端生成的更新后数据
     */
    @Override
    @Operation(summary = "更新", description = "更新")
    public Vo update(@RequestBody @Validated Vo vo) {
        // 调用服务层完成核心业务逻辑，返回更新后的完整数据对象
        return service.update(vo);
    }

    /**
     * 根据主键删除指定实体
     *
     * @param id 要删除实体的唯一标识符（主键），类型为Long
     * @return Boolean类型，true表示删除成功，false表示删除失败
     */
    @Override
    @Operation(summary = "删除", description = "删除")
    public Boolean delete(@PathVariable Long id) {
        return service.delete(id);
    }

    /**
     * 根据主键ID查询指定实体详情
     *
     * @param id 要查询的实体主键ID，需符合数据库约束规则
     * @return 包含完整实体信息的Vo对象，包含以下字段：
     * - id：实体唯一标识
     * - name：实体名称
     * - createTime：实体创建时间
     */
    @Override
    @Operation(summary = "查询", description = "通过主键ID获取实体详细信息")
    public Vo get(@PathVariable Long id) {
        // 调用服务层获取持久层数据并封装为值对象
        return service.get(id);
    }

    /**
     * 列表查询接口方法，根据条件对象查询匹配的数据结果集
     *
     * @param vo 包含查询条件的值对象（VO），通过请求体传递并执行参数校验
     *           需包含有效的查询参数字段定义，使用@Validated注解触发参数校验
     * @return 包含查询结果的Vo对象列表，按业务逻辑排序的完整数据集
     * 当无匹配结果时返回空列表（empty list）而非null值
     */
    @Override
    @Operation(summary = "列表查询", description = "列表查询")
    public List<Vo> list(@RequestBody @Validated Vo vo) {
        // 调用服务层方法进行实际查询操作，返回未经修改的原始结果集
        return service.list(vo);
    }

    /**
     * 分页查询接口 - 根据条件分页查询数据
     *
     * @param pageRequest 分页请求参数对象，包含以下要素：
     *                    - pageNum: 当前页码（从1开始计数）
     *                    - pageSize: 每页数据条数
     *                    - sortBy: 排序字段（可选）
     *                    - sortOrder: 排序方向（asc/desc）（可选）
     *                    - queryParams: 具体业务查询条件参数
     *                    （通过@Validated注解进行参数校验）
     * @return PageResponse<Vo> 分页响应对象，包含：
     * - total: 总数据条数
     * - pages: 总页数
     * - list: 当前页数据列表
     * - currentPage: 当前页码
     * - pageSize: 每页数据量
     * @see PageRequest 分页请求基础结构定义
     * @see PageResponse 分页响应基础结构定义
     */
    @Override
    @Operation(summary = "分页查询", description = "分页查询")
    public PageResponse<Vo> page(@RequestBody @Validated PageRequest<Vo> pageRequest) {
        return service.page(pageRequest);
    }

    /**
     * 批量保存VO对象集合到数据库
     *
     * @param vos 要保存的VO对象集合，通过@RequestBody接收请求体中的JSON数据，
     *            使用@Validated注解进行参数校验，集合元素应符合VO类定义的校验规则
     * @return Boolean 类型操作结果，true表示全部保存成功，false表示保存过程中出现异常或部分失败
     * @apiNote 本方法通过HTTP请求体接收数据，适用于前端批量提交场景
     * 注意请求体应遵循List<Vo>的JSON格式规范
     * @see Service#saveBatch 实际执行保存操作的底层服务方法
     */
    @Override
    @Operation(summary = "批量保存", description = "批量保存")
    public Boolean saveBatch(@RequestBody @Validated List<Vo> vos) {
        // 直接委托给Service层执行批量保存操作
        return service.saveBatch(vos);
    }

    /**
     * 导入Excel数据
     * <p>
     * 通过HTTP POST请求接收multipart/form-data格式的Excel文件，解析为Vo对象列表进行批量存储
     *
     * @param vos 需要导入的Vo对象集合，通过@ExcelRequest注解实现Excel文件到Vo对象的转换
     * @return Boolean 批量保存操作结果，true表示全部保存成功，false表示存在保存失败
     */
    @Operation(summary = "导入", description = "导入")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Boolean importExcel(@ExcelRequest List<Vo> vos) {
        // 调用服务层批量保存方法处理导入数据
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
