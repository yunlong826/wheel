package com.wheel.ctgu.api;

import java.io.IOException;

/*
 *
 *
 * @param null
 * @return null
 * @author long_yun
 * @date 2022/6/2 17:13
 * @describe
 */

public interface RpcSerialization {
    <T> byte[] serialize(T obj) throws IOException;

    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;
}