package com.wheel.ctgu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jack_yun
 * @version 1.0
 * @description: TODO
 * @date 2022/5/28 17:52
 */
public class ApplicationConfig {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private String name;
    public ApplicationConfig(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
