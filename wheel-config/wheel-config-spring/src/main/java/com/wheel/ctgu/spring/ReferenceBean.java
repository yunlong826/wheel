package com.wheel.ctgu.spring;

import com.wheel.ctgu.ReferenceConfig;
import com.wheel.ctgu.RegistryConfig;
import com.wheel.ctgu.common.config.InterfaceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author jack_yun
 * @version 1.0
 * @description: TODO
 * @date 2022/5/30 16:13
 */
public class ReferenceBean<T> extends ReferenceConfig<T> implements InitializingBean, DisposableBean,
        ApplicationContextAware, BeanNameAware {
    public ReferenceBean(Class<T> clazz, InterfaceConfig interfaceConfig, RegistryConfig registryConfig) {
        super(clazz, interfaceConfig, registryConfig);
    }

    @Override
    public void setBeanName(String s) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
