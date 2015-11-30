package com.dtdream.user.account;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;

/**
 * Created by lenovo on 2015/10/21.
 */
public class DDGroupEntityManagerFactory implements SessionFactory {
    private DDGroupEntityManager ddGroupEntityManager;

    @Override
    public Class<?> getSessionType() { // 返回引擎的实体管理器接口
        return UserIdentityManager.class;
    }

    @Override
    public Session openSession() {
        return ddGroupEntityManager;
    }

    public void setDdGroupEntityManager(DDGroupEntityManager ddGroupEntityManager) {
        this.ddGroupEntityManager = ddGroupEntityManager;
    }
}
