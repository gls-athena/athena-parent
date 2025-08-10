package com.gls.athena.common.core.base;

import com.gls.athena.common.bean.base.BaseVo;
import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Feign接口，定义了通用的远程调用方法，用于对VO对象进行增删改查等操作。
 *
 * @param <Vo> VO对象类型，必须继承自BaseVo
 * @author george
 */
public interface IFeign<Vo extends BaseVo> {

    /**
     * 新增一个VO对象。
     *
     * @param vo 待新增的VO对象，必须经过验证
     * @return 新增后的VO对象
     */
    @PostMapping("/insert")
    Vo insert(@RequestBody @Validated Vo vo);

    /**
     * 更新一个VO对象。
     *
     * @param vo 待更新的VO对象，必须经过验证
     * @return 更新后的VO对象
     */
    @PostMapping("/update")
    Vo update(@RequestBody @Validated Vo vo);

    /**
     * 根据ID删除一个VO对象。
     *
     * @param id 待删除对象的ID
     * @return 删除是否成功
     */
    @PostMapping("/delete/{id}")
    Boolean delete(@PathVariable Long id);

    /**
     * 根据ID获取一个VO对象。
     *
     * @param id 待查询对象的ID
     * @return 查询到的VO对象
     */
    @PostMapping("/get/{id}")
    Vo get(@PathVariable Long id);

    /**
     * 查询符合条件的VO对象列表。
     *
     * @param vo 查询条件封装的VO对象
     * @return 符合条件的VO对象列表
     */
    @PostMapping("/list")
    List<Vo> list(@RequestBody @Validated Vo vo);

    /**
     * 分页查询VO对象。
     *
     * @param pageRequest 分页查询参数，包含分页信息和查询条件
     * @return 分页查询结果，包含数据列表和分页信息
     */
    @PostMapping("/page")
    PageResponse<Vo> page(@RequestBody @Validated PageRequest<Vo> pageRequest);

    /**
     * 批量保存VO对象列表。
     *
     * @param voList 待保存的VO对象列表
     * @return 保存是否成功
     */
    @PostMapping("/saveBatch")
    Boolean saveBatch(@RequestBody @Validated List<Vo> voList);
}
