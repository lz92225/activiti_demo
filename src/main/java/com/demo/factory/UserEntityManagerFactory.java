package com.demo.factory;

import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.springframework.stereotype.Component;

@Component
public class UserEntityManagerFactory implements SessionFactory {

    private UserEntityManager userEntityManager;
    @Override
    public Class<?> getSessionType() {
        return null;
    }

    @Override
    public Session openSession(CommandContext commandContext) {
        return null;
    }

    public void setUserEntityManager(UserEntityManager userEntityManager) {
        this.userEntityManager = userEntityManager;
    }
}
