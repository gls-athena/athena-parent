package com.gls.athena.common.bean.security;

import com.gls.athena.common.bean.base.IDomain;
import com.gls.athena.common.bean.base.ITreeNode;

/**
 * 组织机构接口
 * 定义组织机构的基本属性和行为，继承树节点和领域实体接口
 *
 * @author george
 */
public interface IOrganization extends ITreeNode, IDomain {
    /**
     * 获取是否为默认组织机构的标识
     *
     * @return Boolean 默认组织机构标识，true表示是默认组织，false表示不是默认组织
     */
    Boolean getDefaultOrganization();
}

