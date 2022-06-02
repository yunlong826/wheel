package com.wheel.ctgu.spring.schema;

import com.wheel.ctgu.ApplicationConfig;
import com.wheel.ctgu.ProtocolConfig;
import com.wheel.ctgu.RegistryConfig;
import com.wheel.ctgu.spring.ReferenceBean;
import com.wheel.ctgu.spring.ServiceBean;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author jack_yun
 * @version 1.0
 * @description: TODO
 * @date 2022/5/28 17:29
 */
public class WheelNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("application", new WheelBeanDefinitionParser(ApplicationConfig.class));
        registerBeanDefinitionParser("registry", new WheelBeanDefinitionParser(RegistryConfig.class));
        registerBeanDefinitionParser("protocol", new WheelBeanDefinitionParser(ProtocolConfig.class));
        registerBeanDefinitionParser("service", new WheelBeanDefinitionParser(ServiceBean.class));
        registerBeanDefinitionParser("reference", new WheelBeanDefinitionParser(ReferenceBean.class));
    }



}
