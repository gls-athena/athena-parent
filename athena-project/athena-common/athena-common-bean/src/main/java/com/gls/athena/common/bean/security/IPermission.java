package com.gls.athena.common.bean.security;

import com.gls.athena.common.bean.base.IDomain;
import com.gls.athena.common.bean.base.ITreeNode;

/**
 * 权限接口
 * 定义了权限相关的基础属性和方法，继承了树节点和领域实体接口
 *
 * @author george
 */
public interface IPermission extends ITreeNode, IDomain {
    /**
     * 获取权限指令
     * 返回当前权限对象对应的指令字符串
     *
     * @return 权限指令字符串
     */
    String getCommand();

    /**
     * 设置权限指令
     * 为当前权限对象设置指令字符串
     *
     * @param command 权限指令字符串
     */
    void setCommand(String command);
}

