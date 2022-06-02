package com.wheel.ctgu.cluster.balancer;

import com.wheel.ctgu.cluster.api.LoadBalance;
import com.wheel.ctgu.rpc.core.common.ServiceInfo;

import java.util.List;

/**
 * @author jack_yun
 * @version 1.0
 * @description: TODO
 * @date 2022/5/30 13:29
 */
public class FullRoundBalance implements LoadBalance {
    private int index;
    
    @Override
    public ServiceInfo chooseOne(List<ServiceInfo> services) {
        synchronized (this){
            if(index>=services.size()){
                index = 0;
            }
            return services.get(index++);
        }
    }
}
