package com.demo.factory;

import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.springframework.stereotype.Component;

@Component
public class GroupEntityManagerFactory implements SessionFactory {

    private GroupEntityManager groupEntityManager;
    @Override
    public Class<?> getSessionType() {
        return null;
    }

    @Override
    public Session openSession(CommandContext commandContext) {
        return null;
    }

    public void setGroupEntityManager(GroupEntityManager groupEntityManager){
        this.groupEntityManager = groupEntityManager;
    }
}
