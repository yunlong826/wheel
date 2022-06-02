package com.wheel.ctgu.cluster.balancer;

import com.wheel.ctgu.cluster.api.LoadBalance;
import com.wheel.ctgu.rpc.core.common.ServiceInfo;

import java.util.List;
import java.util.Random;

/**
 * @author jack_yun
 * @version 1.0
 * @description: TODO
 * @date 2022/5/30 13:30
 */
public class RandomBalance implements LoadBalance {
    private static Random random = new Random();
    @Override
    public ServiceInfo chooseOne(List<ServiceInfo> services) {
        return services.get(random.nextInt(services.size()));
    }
}
