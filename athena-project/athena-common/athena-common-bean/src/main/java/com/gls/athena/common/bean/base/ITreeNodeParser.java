package com.gls.athena.common.bean.base;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.parser.NodeParser;

/**
 * 树节点解析器
 * <p>
 * 实现Hutool树形结构的节点解析器接口，用于将实现了{@link ITreeNode}接口的实体对象
 * 转换为{@link Tree}树形结构节点。
 *
 * @param <T> 树节点类型，必须实现{@link ITreeNode}接口
 * @author george
 * @see NodeParser
 * @see ITreeNode
 */
public class ITreeNodeParser<T extends ITreeNode> implements NodeParser<T, Long> {

    /**
     * 解析源对象为树节点
     * <p>
     * 将实现了{@link ITreeNode}接口的源对象解析为Hutool的{@link Tree}节点，
     * 包括设置基本属性（ID、父节点ID、名称、权重）和扩展属性。
     *
     * @param object   源数据对象，实现{@link ITreeNode}接口
     * @param treeNode 目标树节点，用于存储解析后的数据
     */
    @Override
    public void parse(T object, Tree<Long> treeNode) {
        // 设置树节点基本属性
        treeNode.setId(object.getId());
        treeNode.setParentId(object.getParentId());
        treeNode.setName(object.getName());
        treeNode.setWeight(object.getSort());

        // 复制源对象所有属性到树节点扩展属性中
        treeNode.putAll(BeanUtil.beanToMap(object));
    }
}

