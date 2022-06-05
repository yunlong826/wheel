package com.wheel.ctgu.spring;


import com.wheel.ctgu.*;
import com.wheel.ctgu.annotation.Wheel;
import com.wheel.ctgu.api.RegistryService;
import com.wheel.ctgu.common.config.*;

import com.wheel.ctgu.common.exporter.WheelExporter;

import com.wheel.ctgu.netty.NettyManager;
import com.wheel.ctgu.netty.server.NettyServer;
import com.wheel.ctgu.spring.util.NetUtils;
import com.wheel.ctgu.spring.util.WheelBeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author jack_yun
 * @version 1.0
 * @description: TODO
 * @date 2022/5/29 14:35
 */
public class ServiceBean<T> extends ServiceConfig<T> implements InitializingBean, DisposableBean,
        ApplicationContextAware, BeanNameAware {

//    private final transient Wheel wheel;

    private AtomicBoolean exported = new AtomicBoolean();

    private AtomicBoolean unexported = new AtomicBoolean();

    private transient ApplicationContext applicationContext;


    private transient String beanName;

    private NettyServer nettyServer;
    private String providerPath;

    private String ipAndPort;

    private InterfaceConfig interfaceConfig;

    private String clazzName;
    private Object ref_replace;

    /**
     * zk地址
     */
    private RegistryService registryService;


//    public ServiceBean(){
//        this.wheel = null;
//    }


    @Override
    public void setBeanName(String s) {
        this.beanName = s;
    }

    @Override
    public void destroy() throws Exception {
        unexport();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        export();

    }
    public void export(){
        if (!exported.compareAndSet(false, true)) {
            return;
        }
        ServiceBean serviceBean = WheelBeanUtils.getServiceBean(applicationContext, ServiceBean.class);
        this.clazzName = serviceBean.getInterface();
        InterfaceConfig interfaceConfig = transForm(serviceBean);
        this.interfaceConfig = interfaceConfig;
        ProtocolConfig protocolConfig = WheelBeanUtils.getProtocolConfig(applicationContext, ProtocolConfig.class);
        RegistryConfig registryConfig = WheelBeanUtils.getRegistryConfig(applicationContext, RegistryConfig.class);
        this.ipAndPort = registryConfig.getHost()+":"+registryConfig.getPort();
        ApplicationConfig applicationConfig = WheelBeanUtils.getApplicationConfig(applicationContext, ApplicationConfig.class);
        String ref = getRef();
        this.ref_replace = WheelBeanUtils.getRef(applicationContext,ref);
        WheelExporter.exportService(this.clazzName, interfaceConfig,ref_replace);
        if("dubbo".equals(protocolConfig.getProtocol())){
            nettyServer = NettyManager.getNettyServer(protocolConfig.getPort());
        }else{
            throw new RuntimeException("unknown communicate protocol:" + protocolConfig.getProtocol());
        }
        // 判断什么类型的注册中心
        registryService = RegistryManager.getRegistryService(this.ipAndPort);
        providerPath = "/wheel/"+interfaceConfig.getGroup()+clazzName+"/providers"+"/"+ NetUtils.getServerIp() + ":"
                +protocolConfig.getPort();
        try {
            registryService.register(providerPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InterfaceConfig transForm(ServiceBean serviceBean){
        InterfaceConfig interfaceConfig = new InterfaceConfig();
        interfaceConfig.setGroup(serviceBean.getGroup());
        interfaceConfig.setFailStrategy(serviceBean.getFailStrategy());
        interfaceConfig.setRetryCount(serviceBean.getRetryCount());
        interfaceConfig.setTimeout(serviceBean.getTimeout());
        interfaceConfig.setVersion(serviceBean.getVersion());
        return interfaceConfig;
    }


    /**
     * 逆向操作
     * 引用计数销毁 统统使用RegistryManager.getRegistryService管理
     * @throws Exception
     */
    public void unexport() throws Exception {
        if (!exported.get() || !unexported.compareAndSet(false, true)) {
            return;
        }

        // 需要unregister
        registryService.unregister(providerPath);
        // 减少引用，引用为0再关闭。这两个其实可以不移除，一般占用不会太多，client可能太多，需要移除
        RegistryManager.remove(this.ipAndPort);
        NettyManager.removeNettyServer(Integer.valueOf(this.ipAndPort.substring(this.ipAndPort.indexOf(":")+1)));
        WheelExporter.remove(clazzName, interfaceConfig);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }




}
