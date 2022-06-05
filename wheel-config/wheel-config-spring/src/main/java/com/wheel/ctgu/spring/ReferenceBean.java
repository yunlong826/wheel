package com.wheel.ctgu.spring;

import com.google.common.collect.Maps;
import com.wheel.ctgu.ReferenceConfig;
import com.wheel.ctgu.ReferenceProxy;
import com.wheel.ctgu.RegistryConfig;
import com.wheel.ctgu.common.config.InterfaceConfig;
import com.wheel.ctgu.spring.annonation.Reference;
import com.wheel.ctgu.spring.util.WheelBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author jack_yun
 * @version 1.0
 * @description: TODO
 * @date 2022/5/30 16:13
 */
@Slf4j
public class ReferenceBean<T> extends ReferenceConfig<T> implements BeanFactoryPostProcessor,ApplicationContextAware {

    private ApplicationContext applicationContext;
    private RegistryConfig registryConfig;
    private Map<String,ReferenceConfig> referenceConfigMap = Maps.newHashMap();
//    public ReferenceBean(Class<T> clazz, InterfaceConfig interfaceConfig, RegistryConfig registryConfig) {
//        super(clazz, interfaceConfig, registryConfig);
//    }
    public ReferenceBean(){
        super();
    }

//    @Override
//    public void destroy() {
//        referenceConfigMap.forEach((key, referenceConfig) -> {
//            referenceConfig.destroy();
//        });
//        referenceConfigMap.clear();
//    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.registryConfig = WheelBeanUtils.getRegistryConfig(beanFactory,RegistryConfig.class);
        for(String beanDefinitionName:beanFactory.getBeanDefinitionNames()){
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if(beanClassName != null){
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, this.getClass().getClassLoader());
                Object bean = applicationContext.getBean(clazz);
                Field[] declaredFields = clazz.getDeclaredFields();
                for(Field field:declaredFields){
                    boolean isExist = field.isAnnotationPresent(Reference.class);
                    if(isExist){
                        try{
                            if (!field.getType().isInterface()) {
                                throw new RuntimeException("wheel依赖不是接口：" + field.getType().getName());
                            }
                            Reference ref = field.getAnnotation(Reference.class);
                            log.info("尝试注入接口代理，bean:{} 属性为：{}", clazz.getName(), field.getName());
                            field.setAccessible(true);
                            InterfaceConfig interfaceConfig = transform(ref);
                            log.info("registryConfig的地址:{}",registryConfig.getHost());
                            ReferenceProxy referenceProxy = new ReferenceProxy(field.getType(),interfaceConfig,registryConfig);
                            log.info("代理对象：{}",referenceProxy.getProxy());
                            field.set(bean,referenceProxy.getProxy());
                        }catch (IllegalAccessException e){
                            log.error("设置jdk实例出错啦：{}", field);
                        }
                    }
                }
//                ReflectionUtils.doWithFields(clazz,field->{
//                    Reference reference = AnnotationUtils.getAnnotation(field, Reference.class);
//                    if(reference!=null){
//                        if(!field.getType().isInterface()){
//                            throw new RuntimeException("wheel依赖不是接口：" + field.getType().getName());
//                        }
//                        Object bean = applicationContext.getBean(clazz);
//                        InterfaceConfig interfaceConfig = transform(reference);
//                        String refKey =  field.getType().getName() + "_" + interfaceConfig.getGroup();
//                        field.setAccessible(true);
//                        ReferenceConfig referenceConfig = referenceConfigMap.computeIfAbsent(refKey,key->
//                            new ReferenceConfig<>(field.getType(),interfaceConfig,this.registryConfig));
//                        ReflectionUtils.setField(field,bean,referenceConfig.getProxy());
//                    }
//                });
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    private InterfaceConfig transform(Reference ref) {
        InterfaceConfig interfaceConfig = new InterfaceConfig();
        interfaceConfig.setGroup(ref.group());
        interfaceConfig.setVersion(ref.version());
        interfaceConfig.setTimeout(ref.timeout());
        interfaceConfig.setFailStrategy(ref.failStrategy());
        interfaceConfig.setRetryCount(ref.retryCount());
        return interfaceConfig;
    }
}
