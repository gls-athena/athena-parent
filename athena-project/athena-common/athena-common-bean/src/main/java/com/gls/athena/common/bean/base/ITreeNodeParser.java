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
     * <p>
     * 该函数用于将一个实现了 ITreeNode 接口的源数据实体对象解析为树节点实体，并将解析后的数据存储在目标树节点实体中。
     * 解析过程包括设置节点的基本属性（如ID、父节点ID、名称、排序权重）以及扩展属性（如编码、描述、类型等）。
     * 最后，将源对象的所有属性复制到树节点的扩展属性中。
     *
     * @param object   源数据实体，实现了 ITreeNode 接口的对象，包含需要解析的树节点数据
     * @param treeNode 目标树节点实体，用于存储解析后的树节点数据
     */
    @Override
    public void parse(T object, Tree<Long> treeNode) {
        // 设置节点基本属性
        treeNode.setId(object.getId());
        treeNode.setParentId(object.getParentId());
        treeNode.setName(object.getName());
        treeNode.setWeight(object.getSort());

        // 设置节点扩展属性
        treeNode.putExtra("code", object.getCode());
        treeNode.putExtra("description", object.getDescription());
        treeNode.putExtra("type", object.getType());

        // 将源对象的所有属性复制到树节点的扩展属性中
        treeNode.putAll(BeanUtil.beanToMap(object));
    }

}
