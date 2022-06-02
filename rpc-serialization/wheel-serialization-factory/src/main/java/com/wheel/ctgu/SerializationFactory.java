package com.wheel.ctgu;

import com.wheel.ctgu.api.RpcSerialization;
import com.wheel.ctgu.api.SerializationTypeEnum;
import com.wheel.ctgu.serialization.HessianSerialization;

/*
 *
 *
 * @param null
 * @return null
 * @author long_yun
 * @date 2022/6/2 17:21
 * @describe
 */

public class SerializationFactory {

    public static RpcSerialization getRpcSerialization(SerializationTypeEnum typeEnum) {
        switch (typeEnum) {
            case HESSIAN:
                return new HessianSerialization();
            case JSON:
                return new JsonSerialization();
            default:
                throw new IllegalArgumentException("serialization type is illegal");
        }
    }

}
