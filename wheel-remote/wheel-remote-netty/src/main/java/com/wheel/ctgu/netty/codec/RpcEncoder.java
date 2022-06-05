package com.wheel.ctgu.netty.codec;


import com.wheel.ctgu.SerializationFactory;
import com.wheel.ctgu.api.RpcSerialization;
import com.wheel.ctgu.api.SerializationTypeEnum;
import com.wheel.ctgu.netty.client.Constants;
import com.wheel.ctgu.rpc.core.common.RpcRequest;
import com.wheel.ctgu.rpc.core.common.RpcResponse;
import com.wheel.ctgu.rpc.core.protocol.MessageHeader;
import com.wheel.ctgu.rpc.core.protocol.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  编码器
 *
 * @Author: changjiu.wang
 * @Date: 2021/7/24 22:27
 */

public class RpcEncoder extends MessageToByteEncoder {




    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Encode a message into a {@link ByteBuf}. This method will be called for each written message that can be handled
     * by this encoder.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link MessageToByteEncoder} belongs to
     * @param msg the message to encode
     * @param out the {@link ByteBuf} into which the encoded message will be written
     * @throws Exception is thrown if an error occurs
     */


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        logger.info("对消息：{}进行编码", msg);
        byte flag;
        if (msg instanceof RpcRequest) {
            flag = Constants.REQUEST;
        } else if (msg instanceof RpcResponse) {
            flag = Constants.RESPONSE;
        } else {
            throw new UnsupportedOperationException("flag unknown:" + msg);
        }
        byte[] wordBytes = Constants.SERIALIZER.serialize(msg);
        out.writeInt(wordBytes.length + 1);
        out.writeByte(flag);
        out.writeBytes(wordBytes);
    }
}
