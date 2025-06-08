package com.gls.athena.common.bean.base;

/**
 * 树节点通用接口
 * <p>
 * 定义了树形结构节点的基本属性和操作方法。实现该接口的类可以作为树形结构的节点使用，
 * 支持节点的ID、父节点ID、名称、编码、类型、描述和排序等基本属性的管理。
 * </p>
 *
 * @author george
 * @since 1.0.0
 */
public interface ITreeNode {
    /**
     * 获取节点唯一标识
     *
     * @return 节点ID，用于唯一标识一个节点
     */
    Long getId();

    /**
     * 设置节点唯一标识
     *
     * @param id 节点ID，必须唯一
     */
    void setId(Long id);

    /**
     * 获取父节点标识
     *
     * @return 父节点ID，如果是根节点则可能为null或特定值
     */
    Long getParentId();

    /**
     * 设置父节点标识
     *
     * @param parentId 父节点ID，用于构建节点间的父子关系
     */
    void setParentId(Long parentId);

    /**
     * 获取节点显示名称
     *
     * @return 节点名称，用于显示或标识节点
     */
    String getName();

    /**
     * 设置节点显示名称
     *
     * @param name 节点名称，不应为null或空字符串
     */
    void setName(String name);

    /**
     * 获取节点业务编码
     *
     * @return 节点编码，用于业务标识
     */
    String getCode();

    /**
     * 设置节点业务编码
     *
     * @param code 节点编码，建议使用统一的编码规则
     */
    void setCode(String code);

    /**
     * 获取节点排序值
     *
     * @return 排序值，用于定义同级节点间的显示顺序
     */
    Integer getSort();

    /**
     * 设置节点排序值
     *
     * @param sort 排序值，值越小优先级越高
     */
    void setSort(Integer sort);
}
