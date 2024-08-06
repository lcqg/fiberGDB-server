package com.xiongdwm.fiberGDB.support;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    //private static ConfigurableApplicationContext configurableApplicationContext;
    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        BeanContext.applicationContext=applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException{
        return (T) applicationContext.getBean(name);
    }
    public static <T> T getBean(Class<T> clazz) throws BeansException{
        return (T) applicationContext.getBean(clazz);
    }
}
