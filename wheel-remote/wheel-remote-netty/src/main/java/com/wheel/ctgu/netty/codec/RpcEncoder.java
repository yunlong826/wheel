package com.wheel.ctgu.netty.codec;


import com.wheel.ctgu.SerializationFactory;
import com.wheel.ctgu.api.RpcSerialization;
import com.wheel.ctgu.api.SerializationTypeEnum;
import com.wheel.ctgu.rpc.core.protocol.MessageHeader;
import com.wheel.ctgu.rpc.core.protocol.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 *  编码器
 *
 * @Author: changjiu.wang
 * @Date: 2021/7/24 22:27
 */

public class RpcEncoder<T> extends MessageToByteEncoder<MessageProtocol<T>> {

    /**
     *
     *  +---------------------------------------------------------------+
     *  | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte|
     *  +---------------------------------------------------------------+
     *  | 状态 1byte |        消息 ID 32byte     |      数据长度 4byte    |
     *  +---------------------------------------------------------------+
     *  |                   数据内容 （长度不定）                         |
     *  +---------------------------------------------------------------+
     *
     *
     * @param channelHandlerContext
     * @param messageProtocol
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageProtocol<T> messageProtocol, ByteBuf byteBuf) throws Exception {
        MessageHeader header = messageProtocol.getHeader();



        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(SerializationTypeEnum.parseByType(header.getSerialization()));
        byte[] data = rpcSerialization.serialize(messageProtocol.getBody());

        // 数据长度
        byteBuf.writeInt(data.length);

        // 数据内容
        byteBuf.writeBytes(data);
    }
}
