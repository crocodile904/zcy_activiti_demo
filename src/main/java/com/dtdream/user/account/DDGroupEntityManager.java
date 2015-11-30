package com.dtdream.user.account;

import org.activiti.engine.impl.persistence.entity.GroupEntityManager;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;

/**
 * Created by lenovo on 2015/10/21.
 */
public class DDGroupEntityManager extends UserEntityManager {
    // 这个Bean就是公司提供的统一身份访问接口，可以覆盖UserEntityManager的任何方法用公司内部的统一接口提供服务
    private com.dtdream.user.account.DDGroupManager ddGroupManager;

    public void setDdGroupManager(com.dtdream.user.account.DDGroupManager ddGroupManager) {
        this.ddGroupManager = ddGroupManager;
    }
}
