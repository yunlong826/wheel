package com.wheel.ctgu.cluster.api;

import com.wheel.ctgu.rpc.core.common.ServiceInfo;

import java.util.List;

/**
 * @author jack_yun
 * @version 1.0
 * @description: TODO
 * @date 2022/5/30 13:26
 */
public interface LoadBalance {
    ServiceInfo chooseOne(List<ServiceInfo> services);
}
