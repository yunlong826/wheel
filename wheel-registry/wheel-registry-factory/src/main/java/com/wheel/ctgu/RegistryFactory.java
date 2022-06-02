package com.wheel.ctgu;

import com.wheel.ctgu.api.RegistryService;
import com.wheel.ctgu.registry.ZookeeperRegistryService;

/**
 * @author jack_yun
 * @version 1.0
 * @description: TODO
 * @date 2022/6/2 18:42
 */
public class RegistryFactory {
    public static RegistryService getRpcSerialization(RegistryTypeEnum typeEnum,String address) {
        switch (typeEnum) {
            case ZOOKEEPER:
                return new ZookeeperRegistryService(address);
            case REDIS:
                return null;
            default:
                throw new IllegalArgumentException("serialization type is illegal");
        }
    }
}
