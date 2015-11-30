package com.dtdream.user.account;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.identity.*;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.db.PersistentObject;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2015/10/21.
 */
public class DDUserEntityManager extends UserEntityManager {
    // 这个Bean就是公司提供的统一身份访问接口，可以覆盖UserEntityManager的任何方法用公司内部的统一接口提供服务
    private DDUserManager ddUserManager;

    @Override
    public Boolean checkPassword(String userId, String password) {
        //CustomUser customUser = customUserManager.get(new Long(userId));
        return true;//CustomUser.getPassword().equals(password);
    }

    public void setDdUserManager(DDUserManager ddUserManager) {
        this.ddUserManager = ddUserManager;
    }

    public org.activiti.engine.identity.User createNewUser(String userId) {
        return new UserEntity(userId);
    }

    @Override
    public void insertUser(org.activiti.engine.identity.User user) {
        this.getDbSqlSession().insert((PersistentObject) user);
        if(this.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
            this.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(ActivitiEventBuilder.createEntityEvent(ActivitiEventType.ENTITY_CREATED, user));
            this.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(ActivitiEventBuilder.createEntityEvent(ActivitiEventType.ENTITY_INITIALIZED, user));
        }
    }

    @Override
    public void updateUser(org.activiti.engine.identity.User updatedUser) {
        CommandContext commandContext = Context.getCommandContext();
        DbSqlSession dbSqlSession = commandContext.getDbSqlSession();
        dbSqlSession.update((PersistentObject)updatedUser);
        if(this.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
            this.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(ActivitiEventBuilder.createEntityEvent(ActivitiEventType.ENTITY_UPDATED, updatedUser));
        }

    }

    @Override
    public org.activiti.engine.identity.User findUserById(String userId) {
        return (UserEntity)this.getDbSqlSession().selectOne("selectUserById", userId);
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity user = (UserEntity)this.findUserById(userId);
        if(user != null) {
            List identityInfos = this.getDbSqlSession().selectList("selectIdentityInfoByUserId", userId);
            Iterator i$ = identityInfos.iterator();

            while(i$.hasNext()) {
                IdentityInfoEntity identityInfo = (IdentityInfoEntity)i$.next();
                this.getIdentityInfoManager().deleteIdentityInfo(identityInfo);
            }

            this.getDbSqlSession().delete("deleteMembershipsByUserId", userId);
            user.delete();
            if(this.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
                this.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(ActivitiEventBuilder.createEntityEvent(ActivitiEventType.ENTITY_DELETED, user));
            }
        }

    }

    @Override
    public List<org.activiti.engine.identity.User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
        return this.getDbSqlSession().selectList("selectUserByQueryCriteria", query, page);
    }

    @Override
    public long findUserCountByQueryCriteria(UserQueryImpl query) {
        return ((Long)this.getDbSqlSession().selectOne("selectUserCountByQueryCriteria", query)).longValue();
    }

    @Override
    public List<org.activiti.engine.identity.Group> findGroupsByUser(String userId) {
        return this.getDbSqlSession().selectList("selectGroupsByUserId", userId);
    }

    @Override
    public UserQuery createNewUserQuery() {
        return new UserQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutor());
    }

    @Override
    public IdentityInfoEntity findUserInfoByUserIdAndKey(String userId, String key) {
        HashMap parameters = new HashMap();
        parameters.put("userId", userId);
        parameters.put("key", key);
        return (IdentityInfoEntity)this.getDbSqlSession().selectOne("selectIdentityInfoByUserIdAndKey", parameters);
    }

//    @Override
//    public List<String> findUserInfoKeysByUserIdAndType(String userId, String type) {
//        HashMap parameters = new HashMap();
//        parameters.put("userId", userId);
//        parameters.put("type", type);
//        return this.getDbSqlSession().getSqlSession().selectList("selectIdentityInfoKeysByUserIdAndType", parameters);
//    }

    public List<org.activiti.engine.identity.User> findPotentialStarterUsers(String proceDefId) {
        HashMap parameters = new HashMap();
        parameters.put("procDefId", proceDefId);
        return (List)this.getDbSqlSession().selectOne("selectUserByQueryCriteria", parameters);
    }

    @Override
    public List<org.activiti.engine.identity.User> findUsersByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
        return this.getDbSqlSession().selectListWithRawParameter("selectUserByNativeQuery", parameterMap, firstResult, maxResults);
    }

    @Override
    public long findUserCountByNativeQuery(Map<String, Object> parameterMap) {
        return ((Long)this.getDbSqlSession().selectOne("selectUserCountByNativeQuery", parameterMap)).longValue();
    }

    @Override
    public boolean isNewUser(org.activiti.engine.identity.User user) {
        return ((UserEntity)user).getRevision() == 0;
    }

    @Override
    public Picture getUserPicture(String userId) {
        UserEntity user = (UserEntity)this.findUserById(userId);
        return user.getPicture();
    }

    @Override
    public void setUserPicture(String userId, Picture picture) {
        UserEntity user = (UserEntity)this.findUserById(userId);
        if(user == null) {
            throw new ActivitiObjectNotFoundException("user " + userId + " doesn\'t exist", User.class);
        } else {
            user.setPicture(picture);
        }
    }
}
