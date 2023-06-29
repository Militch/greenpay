package com.esiran.greenpay.actuator;


import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class PluginLoader {
    private final ApplicationContext applicationContext;

    public PluginLoader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    @SuppressWarnings("unchecked")
    public <T> Plugin<T> loadForClassPath(String classPath) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return (Plugin<T>) applicationContext.getBean(classPath,Plugin.class);
    }
}
