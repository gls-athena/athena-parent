package com.gls.athena.common.bean.base;

import cn.hutool.core.lang.tree.Tree;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ITreeNodeParser 接口的测试类，用于验证树节点解析器的功能。
 */
class ITreeNodeParserTest {

    private ITreeNodeParser<ITreeNode> parser;

    /**
     * 在每个测试方法执行前初始化解析器。
     */
    @BeforeEach
    public void setUp() {
        parser = new ITreeNodeParser<>();
    }

    /**
     * 测试解析方法，验证当所有属性都设置时，解析是否正确。
     */
    @Test
    public void parse_AllPropertiesSet_CorrectlyParsed() {
        ITreeNode source = new ITreeNodeImpl();

        Tree<Long> treeNode = new Tree<>();

        // 执行解析操作
        parser.parse(source, treeNode);

        // 验证解析后的树节点属性是否正确
        assertEquals(1L, treeNode.getId());
        assertEquals(0L, treeNode.getParentId());
        assertEquals("Node 1", treeNode.getName());
        assertEquals(1, treeNode.getWeight());

        assertEquals("CODE1", treeNode.get("code"));
        assertEquals("Description 1", treeNode.get("description"));
        assertEquals("TYPE1", treeNode.get("type"));

        assertEquals(1L, treeNode.get("id"));
        assertEquals(0L, treeNode.get("parentId"));
        assertEquals("Node 1", treeNode.get("name"));
        assertEquals(1, treeNode.get("sort"));
        assertEquals("CODE1", treeNode.get("code"));
        assertEquals("Description 1", treeNode.get("description"));
        assertEquals("TYPE1", treeNode.get("type"));
    }

    /**
     * ITreeNode 接口的实现类，用于测试。
     */
    @Data
    private static class ITreeNodeImpl implements ITreeNode {
        private Long id = 1L;

        private Long parentId = 0L;

        private String name = "Node 1";

        private Integer sort = 1;

        private String code = "CODE1";

        private String description = "Description 1";

        private String type = "TYPE1";
    }
}