package com.gls.athena.common.bean.base;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.parser.NodeParser;

/**
 * 树节点解析器
 * 用于将实现了 ITreeNode 接口的实体类解析为树形结构节点
 *
 * @param <T> 树节点类型，必须实现 ITreeNode 接口
 * @author george
 */
public class ITreeNodeParser<T extends ITreeNode> implements NodeParser<T, Long> {
    /**
     * 将源数据实体解析为树节点
     *
     * @param object   源数据实体，实现了 ITreeNode 接口的对象
     * @param treeNode 目标树节点实体，用于存储解析后的树节点数据
     */
    @Override
    public void parse(T object, Tree<Long> treeNode) {
        // 设置节点基本属性
        // 节点唯一标识
        treeNode.setId(object.getId());
        // 父节点ID
        treeNode.setParentId(object.getParentId());
        // 节点名称
        treeNode.setName(object.getName());
        // 节点排序权重
        treeNode.setWeight(object.getSort());

        // 设置节点扩展属性
        // 节点编码
        treeNode.putExtra("code", object.getCode());
        // 节点描述
        treeNode.putExtra("description", object.getDescription());
        // 节点类型
        treeNode.putExtra("type", object.getType());

        // 将源对象的所有属性复制到树节点的扩展属性中
        treeNode.putAll(BeanUtil.beanToMap(object));
    }
}
