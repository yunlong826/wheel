package com.wheel.ctgu.spring;

import com.google.common.collect.Maps;
import com.wheel.ctgu.ReferenceConfig;
import com.wheel.ctgu.RegistryConfig;
import com.wheel.ctgu.common.config.InterfaceConfig;
import com.wheel.ctgu.spring.util.WheelBeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author jack_yun
 * @version 1.0
 * @description: TODO
 * @date 2022/5/30 16:13
 */
public class ReferenceBean<T> extends ReferenceConfig<T> implements InitializingBean,DisposableBean,
        ApplicationContextAware, BeanNameAware {

    private String beanName;
    private ApplicationContext applicationContext;

    private Map<String,ReferenceConfig> referenceConfigMap = Maps.newHashMap();
    public ReferenceBean(Class<T> clazz, InterfaceConfig interfaceConfig, RegistryConfig registryConfig) {
        super(clazz, interfaceConfig, registryConfig);
    }
    public ReferenceBean(){
        super();
    }

    @Override
    public void setBeanName(String s) {
        this.beanName = s;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
