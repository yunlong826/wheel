package com.wheel.ctgu.spring.util;


import com.wheel.ctgu.ApplicationConfig;
import com.wheel.ctgu.ProtocolConfig;
import com.wheel.ctgu.RegistryConfig;
import com.wheel.ctgu.spring.ServiceBean;
import org.springframework.beans.factory.BeanFactory;

/**
 * @author jack_yun
 * @version 1.0
 * @description: TODO
 * @date 2022/5/30 17:01
 */
public class WheelBeanUtils {
    public static ServiceBean getServiceBean(BeanFactory beanFactory, Class<?> cls){
        String beanName = cls.getName();
        if(beanFactory.containsBean(beanName)){
            return (ServiceBean) beanFactory.getBean(beanName,cls);
        }
        return null;
    }
    public static Object getRef(BeanFactory beanFactory,String ref_name){
        if(beanFactory.containsBean(ref_name)){
            return beanFactory.getBean(ref_name);
        }
        return null;
    }

    public static ProtocolConfig getProtocolConfig(BeanFactory beanFactory, Class<?> cls){
        String beanName = cls.getName();
        if(beanFactory.containsBean(beanName)){
            return (ProtocolConfig) beanFactory.getBean(beanName,cls);
        }
        return null;
    }

    public static RegistryConfig getRegistryConfig(BeanFactory beanFactory, Class<?> cls){
        String beanName = cls.getName();
        if(beanFactory.containsBean(beanName)){
            return (RegistryConfig) beanFactory.getBean(beanName,cls);
        }
        return null;
    }
    public static ApplicationConfig getApplicationConfig(BeanFactory beanFactory, Class<?> cls){
        String beanName = cls.getName();
        if(beanFactory.containsBean(beanName)){
            return (ApplicationConfig) beanFactory.getBean(beanName,cls);
        }
        return null;
    }
}
