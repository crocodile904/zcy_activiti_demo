package me.kafeitu.activiti.chapter7.listener;


import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;

/**
 * Created by dawenxi on 2015/11/6.
 */
public class TaskEventLinster implements ActivitiEventListener {

    @Override
    public void onEvent(ActivitiEvent activitiEvent) {
        System.out.println("已经被监听了"+ activitiEvent.getProcessInstanceId()+"的" +activitiEvent.getType()+"事件");
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
